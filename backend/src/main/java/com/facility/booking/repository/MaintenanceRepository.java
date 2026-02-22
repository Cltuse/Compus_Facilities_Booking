package com.facility.booking.repository;

import com.facility.booking.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByFacilityId(Long facilityId);

    List<Maintenance> findByStatus(String status);
}
