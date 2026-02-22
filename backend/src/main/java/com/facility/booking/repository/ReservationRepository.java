package com.facility.booking.repository;

import com.facility.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByFacilityId(Long facilityId);

    List<Reservation> findByStatus(String status);
}
