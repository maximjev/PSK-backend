package com.psk.backend.apartment.reservation;


import com.google.common.collect.ImmutableList;
import com.mongodb.client.result.DeleteResult;
import com.psk.backend.apartment.ApartmentRepository;
import com.psk.backend.apartment.reservation.aggregations.QueryResultCount;
import com.psk.backend.apartment.reservation.value.PlacementFilter;
import com.psk.backend.apartment.reservation.value.PlacementResult;
import com.psk.backend.apartment.reservation.value.ReservationForm;
import com.psk.backend.apartment.reservation.value.ReservationListView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.Collections.max;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ReservationRepository {

    private final MongoOperations mongoOperations;
    private final ReservationMapper mapper;
    private final ApartmentRepository apartmentRepository;

    public ReservationRepository(MongoOperations mongoOperations,
                                 ReservationMapper mapper,
                                 ApartmentRepository apartmentRepository) {
        this.mongoOperations = mongoOperations;
        this.mapper = mapper;
        this.apartmentRepository = apartmentRepository;
    }

    public Page<ReservationListView> list(String apartmentId, Pageable page) {
        var conditions = new Criteria().and("apartmentId").is(apartmentId);

        var total = mongoOperations.count(query(conditions), Reservation.class);

        var entities = mongoOperations.find(
                query(conditions)
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                Reservation.class)
                .stream()
                .map(mapper::listView)
                .collect(toList());

        return new PageImpl<>(entities, page, total);
    }

    public Try<PlacementResult> availablePlaces(String apartmentId, PlacementFilter filter) {
        var conditions = new Criteria()
                .and("apartmentId").is(apartmentId);

        var fullSetConditions = new Criteria().andOperator(
                where("from").lt(filter.getFrom()),
                where("till").gt(filter.getTill())
        );

        var intersectionConditions = new Criteria().orOperator(
                new Criteria().andOperator(
                        where("from").lt(filter.getFrom()),
                        where("till").lt(filter.getTill()),
                        where("till").gt(filter.getFrom())),
                new Criteria().andOperator(
                        where("from").lt(filter.getTill()),
                        where("from").gt(filter.getFrom()),
                        where("till").gt(filter.getTill())),
                new Criteria().andOperator(
                        where("from").gt(filter.getFrom()),
                        where("till").lt(filter.getTill()))
        );

        var fullConditions = new Criteria().orOperator(
                fullSetConditions,
                intersectionConditions
        );

        Long totalPlaces = calculateTotalPlaces(new Criteria().andOperator(conditions, intersectionConditions));

        var countQueryOperations = ImmutableList.<AggregationOperation>builder()
                .add(match(conditions))
                .add(match(fullConditions))
                .add(count().as("totalCount"))
                .build();

        var reservationCount = mongoOperations
                .aggregate(newAggregation(countQueryOperations), "reservation", QueryResultCount.class)
                .getUniqueMappedResult();

        var placesCountOperations = ImmutableList.<AggregationOperation>builder()
                .add(match(conditions))
                .add(match(fullSetConditions))
                .add(group().sum("places").as("totalCount"))
                .build();

        var placesCount = mongoOperations
                .aggregate(newAggregation(placesCountOperations), "reservation", QueryResultCount.class)
                .getUniqueMappedResult();

        var availablePlaces = new PlacementResult(
                placesCount == null ? totalPlaces : placesCount.getTotalCount() + totalPlaces,
                reservationCount != null ? reservationCount.getTotalCount() : 0);

        return apartmentRepository.findById(apartmentId).map(a -> {
            availablePlaces.calculateAvailablePlaces(a.getSize());
            return availablePlaces;
        });
    }

    private Long calculateTotalPlaces(Criteria conditions) {
        var intersectedReservations = mongoOperations.find(query(conditions), Reservation.class);
        Long totalPlaces = calculateUniqueNotIntersectingPlaces(intersectedReservations);

        totalPlaces += intersectedReservations.stream()
                .map(Reservation::getPlaces)
                .reduce((p, sum) -> sum += p)
                .orElse(0L);

        return totalPlaces;
    }

    private Long calculateUniqueNotIntersectingPlaces(List<Reservation> reservations) {
        Set<Reservation> reservationSet = new HashSet<>();

        reservations.forEach(lr -> {
            if (reservations.stream().noneMatch(rr -> rr.intersects(lr))) {
                reservationSet.add(lr);
            }
        });
        reservations.removeAll(reservationSet);

        if (reservationSet.isEmpty()) {
            return 0L;
        } else {
            long max = max(reservationSet, comparing(Reservation::getPlaces)).getPlaces();
            return reservations.isEmpty() ? max : max(reservations, comparing(Reservation::getPlaces)).getPlaces() - max;
        }
    }

    public Try<EntityId> insert(ReservationForm form) {
        var entity = mongoOperations.insert(mapper.fromForm(form));
        return successful(entityId(entity.getId()));
    }

    public Try<EntityId> updateByTripId(String tripId, ReservationForm newReservation) {
        return findByTripId(tripId).map(a -> {
            mongoOperations.save(mapper.update(newReservation, a));
            return entityId(a.getId());
        });
    }

    public Try<EntityId> updatePlacesByTripId(String id, Long places) {
        return findByTripId(id).map(r -> {
            r.setPlaces(places);
            mongoOperations.save(r);
            return entityId(r.getId());
        });
    }

    public Try<EntityId> reassignTripByTripId(String id, String newId) {
        return findByTripId(id).map(r ->{
            r.setTripId(newId);
            mongoOperations.save(r);
            return entityId(r.getId());
        });
    }

    public Try<Reservation> findByTripId(String id) {
        return mongoOperations
                .query(Reservation.class)
                .matching(query(where("tripId").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Reservation.class.getName(), id)));
    }

    public Try<EntityId> update(String id, ReservationForm reservation) {
        return findById(id).map(a -> {
            mongoOperations.save(mapper.update(reservation, a));
            return entityId(a.getId());
        });
    }

    public Try<EntityId> deleteByTripId(String tripId) {
        DeleteResult result = mongoOperations.remove(
                query(where("tripId").is(tripId)),
                Reservation.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(tripId))
                : failure(OBJECT_NOT_FOUND.entity(tripId));
    }

    public Try<Reservation> findById(String id) {
        return mongoOperations
                .query(Reservation.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Reservation.class.getName(), id)));
    }
}
