package com.softer.riderassignment.controller;

import com.softer.riderassignment.model.RiderAssignment;
import com.softer.riderassignment.service.RiderAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class RiderAssignmentController {

    private final RiderAssignmentService service;

    public RiderAssignmentController(RiderAssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RiderAssignment>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderAssignment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RiderAssignment> create(@Valid @RequestBody RiderAssignment assignment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(assignment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiderAssignment> update(
            @PathVariable Long id,
            @Valid @RequestBody RiderAssignment assignment) {
        return ResponseEntity.ok(service.update(id, assignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
