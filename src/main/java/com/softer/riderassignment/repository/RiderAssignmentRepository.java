package com.softer.riderassignment.repository;

import com.softer.riderassignment.model.RiderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderAssignmentRepository extends JpaRepository<RiderAssignment, Long> {
    boolean existsByDeliveryId(Long deliveryId);
}
