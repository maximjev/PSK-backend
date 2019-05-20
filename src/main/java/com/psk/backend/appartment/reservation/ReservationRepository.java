package com.psk.backend.appartment.reservation;


import com.google.common.collect.ImmutableList;
import com.psk.backend.appartment.AppartmentRepository;
import com.psk.backend.appartment.reservation.aggregations.QueryResultCount;
import com.psk.backend.appartment.reservation.value.PlacementFilter;
import com.psk.backend.appartment.reservation.value.PlacementResult;
import com.psk.backend.appartment.reservation.value.ReservationListView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ReservationRepository {

    private final MongoOperations mongoOperations;
    private final ReservationMapper mapper;
    private final AppartmentRepository appartmentRepository;

    public ReservationRepository(MongoOperations mongoOperations,
                                 ReservationMapper mapper,
                                 AppartmentRepository appartmentRepository) {
        this.mongoOperations = mongoOperations;
        this.mapper = mapper;
        this.appartmentRepository = appartmentRepository;
    }

    public Page<ReservationListView> list(String appartmentId, Pageable page) {
        var conditions = new Criteria().and("appartmentId").is(appartmentId);

        var total = mongoOperations.count(query(conditions), Reservation.class);

        var entities = mongoOperations.find(
                query(conditions)
                        .skip(page.getOffset())
                        .limit(page.getPageSize()),
                Reservation.class)
                .stream()
                .map(mapper::listView)
                .collect(toList());

        return new PageImpl<>(entities, page, total);
    }

    public Try<PlacementResult> availablePlaces(String appartmentId, PlacementFilter filter) {
        var conditions = new Criteria()
                .and("appartmentId").is(appartmentId)
                .orOperator(
                        new Criteria().andOperator(
                                where("from").lt(filter.getFrom()),
                                where("till").lt(filter.getTill()),
                                where("till").gt(filter.getFrom())
                        ),
                        new Criteria().andOperator(
                                where("from").lt(filter.getFrom()),
                                where("till").gt(filter.getTill())
                        ),
                        new Criteria().andOperator(
                                where("from").lt(filter.getTill()),
                                where("from").gt(filter.getFrom()),
                                where("till").gt(filter.getTill())
                        ),
                        new Criteria().andOperator(
                                where("from").gt(filter.getFrom()),
                                where("till").lt(filter.getTill())
                        )
                );

        var countQueryOperations = ImmutableList.<AggregationOperation>builder()
                .add(match(conditions))
                .add(count().as("totalCount"))
                .build();

        var reservationCount = mongoOperations
                .aggregate(newAggregation(countQueryOperations), "reservation", QueryResultCount.class)
                .getUniqueMappedResult();

        var placesCountOperations = ImmutableList.<AggregationOperation>builder()
                .add(match(conditions))
                .add(group().sum("places").as("totalCount"))
                .build();

        var placesCount = mongoOperations
                .aggregate(newAggregation(placesCountOperations), "reservation", QueryResultCount.class)
                .getUniqueMappedResult();

        var availablePlaces = new PlacementResult(
                placesCount != null ? placesCount.getTotalCount() : 0,
                reservationCount != null ? reservationCount.getTotalCount() : 0);

        return appartmentRepository.findById(appartmentId).map(a -> {
            availablePlaces.calculateAvailablePlaces(a.getSize());
            return availablePlaces;
        });
    }

    public Try<EntityId> insert(Reservation entity) {
        mongoOperations.insert(entity);
        return successful(entityId(entity.getId()));
    }

    public Try<EntityId> updateByTripId(String tripId, Reservation newReservation) {
        return findByTripId(tripId).map(a -> {
            mongoOperations.save(mapper.update(newReservation, a));
            return entityId(a.getId());
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
}
