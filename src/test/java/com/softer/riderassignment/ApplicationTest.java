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
}
