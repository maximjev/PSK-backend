package com.psk.backend.scheduled;


import com.psk.backend.trip.Trip;
import com.psk.backend.trip.TripRepository;
import com.psk.backend.trip.TripStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {
    private TripRepository tripRepository;

    public ScheduledTaskService(TripRepository tripRepository){
        this.tripRepository=tripRepository;
    }

    @Scheduled(fixedRate=5000)
    public void updateTripStatus(){

       List <Trip>  trips = tripRepository.getAllTrips();
       for (Trip trip :trips){
           if (trip.getDeparture().isAfter(LocalDateTime.now())){
               trip.setStatus(TripStatus.STARTED);
               tripRepository.save(trip);
           }
           else if (trip.getReservationEnd().isAfter(LocalDateTime.now())){
               trip.setStatus(TripStatus.FINISHED);
               tripRepository.save(trip);
           }
       }
    }
}
