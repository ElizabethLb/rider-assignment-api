package com.softer.riderassignment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "rider_assignments")
public class RiderAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El identificador del delivery es obligatorio")
    @Positive(message = "El identificador del delivery debe ser positivo")
    @Column(name = "delivery_id", nullable = false)
    private Long deliveryId;

    @NotNull(message = "El identificador del rider es obligatorio")
    @Positive(message = "El identificador del rider debe ser positivo")
    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @NotNull(message = "El estado de la asignación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = AssignmentStatus.ASSIGNED;
        }
    }

    public RiderAssignment() {
    }

    public RiderAssignment(Long id, Long deliveryId, Long riderId,
                           AssignmentStatus status, LocalDateTime assignedAt) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.riderId = riderId;
        this.status = status;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
