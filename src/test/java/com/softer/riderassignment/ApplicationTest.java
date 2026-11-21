package com.softer.riderassignment;

import com.softer.riderassignment.model.AssignmentStatus;
import com.softer.riderassignment.model.RiderAssignment;
import com.softer.riderassignment.repository.TestH2Repository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ApplicationTest {
    @LocalServerPort
    private int port;

    private String baseUrl;
    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository testH2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/assignments";
    }

    @Test
    @DisplayName("should get no assignments when table is empty")
    public void test1_testGetAssignmentsWhenTableIsEmpty() {
        RiderAssignment[] response = restTemplate.getForObject(baseUrl, RiderAssignment[].class);

        assertNotNull(response);
        assertEquals(0, response.length);
        assertEquals(0, testH2Repository.count());
    }

    @Test
    @DisplayName("should get all assignments when table is not empty")
    @Sql(statements = "INSERT INTO rider_assignments(id, delivery_id, rider_id, status, assigned_at) "
            + "VALUES (1, 7001, 301, 'ASSIGNED', '2026-11-21 10:00:00')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM rider_assignments", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test2_testGetAssignmentsWhenTableIsNotEmpty() {
        RiderAssignment[] response = restTemplate.getForObject(baseUrl, RiderAssignment[].class);

        assertNotNull(response);
        assertEquals(1, response.length);
        assertAll(
                () -> assertEquals(1L, response[0].getId()),
                () -> assertEquals(7001L, response[0].getDeliveryId()),
                () -> assertEquals(301L, response[0].getRiderId()),
                () -> assertEquals(AssignmentStatus.ASSIGNED, response[0].getStatus()),
                () -> assertEquals(1, testH2Repository.count())
        );
    }

    @Test
    @DisplayName("should register a new assignment")
    @Sql(statements = "DELETE FROM rider_assignments", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test3_testAddAssignment() {
        assertEquals(0, testH2Repository.count());
        RiderAssignment assignment = new RiderAssignment(
                null, 7001L, 301L, AssignmentStatus.ASSIGNED, null);

        RiderAssignment response = restTemplate.postForObject(baseUrl, assignment, RiderAssignment.class);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertAll(
                () -> assertEquals(7001L, response.getDeliveryId()),
                () -> assertEquals(301L, response.getRiderId()),
                () -> assertEquals(AssignmentStatus.ASSIGNED, response.getStatus()),
                () -> assertNotNull(response.getAssignedAt()),
                () -> assertEquals(1, testH2Repository.count())
        );
        RiderAssignment saved = testH2Repository.findById(response.getId()).orElseThrow();
        assertEquals(7001L, saved.getDeliveryId());
        assertEquals(301L, saved.getRiderId());
    }

    @Test
    @DisplayName("should get assignment with id=1")
    @Sql(statements = "INSERT INTO rider_assignments(id, delivery_id, rider_id, status, assigned_at) "
            + "VALUES (1, 7001, 301, 'ASSIGNED', '2026-11-21 10:00:00')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM rider_assignments", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test4_testFindAssignmentById() {
        RiderAssignment response = restTemplate.getForObject(baseUrl + "/{id}", RiderAssignment.class, 1L);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals(7001L, response.getDeliveryId()),
                () -> assertEquals(301L, response.getRiderId()),
                () -> assertEquals(AssignmentStatus.ASSIGNED, response.getStatus()),
                () -> assertEquals(LocalDateTime.of(2026, 11, 21, 10, 0), response.getAssignedAt()),
                () -> assertEquals(1, testH2Repository.count())
        );
    }
}
