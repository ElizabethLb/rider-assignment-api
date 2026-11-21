package com.softer.riderassignment.service;

import com.softer.riderassignment.exception.BusinessRuleException;
import com.softer.riderassignment.exception.ResourceNotFoundException;
import com.softer.riderassignment.model.AssignmentStatus;
import com.softer.riderassignment.model.RiderAssignment;
import com.softer.riderassignment.repository.RiderAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RiderAssignmentService {

    private final RiderAssignmentRepository repository;

    public RiderAssignmentService(RiderAssignmentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RiderAssignment> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public RiderAssignment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una asignación con identificador " + id));
    }

    public RiderAssignment create(RiderAssignment assignment) {
        if (repository.existsByDeliveryId(assignment.getDeliveryId())) {
            throw new BusinessRuleException(
                    "El delivery " + assignment.getDeliveryId() + " ya tiene una asignación registrada");
        }

        assignment.setId(null);
        assignment.setStatus(assignment.getStatus() == null
                ? AssignmentStatus.ASSIGNED : assignment.getStatus());
        assignment.setAssignedAt(assignment.getAssignedAt() == null
                ? LocalDateTime.now() : assignment.getAssignedAt());
        return repository.save(assignment);
    }

    public RiderAssignment update(Long id, RiderAssignment request) {
        RiderAssignment existing = findById(id);

        if (!existing.getDeliveryId().equals(request.getDeliveryId())
                && repository.existsByDeliveryId(request.getDeliveryId())) {
            throw new BusinessRuleException(
                    "El delivery " + request.getDeliveryId() + " ya tiene una asignación registrada");
        }

        existing.setDeliveryId(request.getDeliveryId());
        existing.setRiderId(request.getRiderId());
        existing.setStatus(request.getStatus() == null
                ? existing.getStatus() : request.getStatus());
        return repository.save(existing);
    }

    public void delete(Long id) {
        RiderAssignment existing = findById(id);
        repository.delete(existing);
    }
}
