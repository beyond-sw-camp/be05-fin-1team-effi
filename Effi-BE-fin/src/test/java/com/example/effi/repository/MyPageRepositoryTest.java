package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.Rollback;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
public class MyPageRepositoryTest {

    @Autowired
    private MyPageRepository myPageRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Test setup: create and save entities
        Employee employee = Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(null) // Adjusted to match the entity definition
                .password("password")
                .build();
        entityManager.persist(employee);

        Timezone timezone = Timezone.builder()
                .timezoneName("UTC")
                .countryCode("US")
                .abbreviation("UTC")
                .timeStart(0L)
                .gmtOffset(0)
                .dst("N")
                .build();
        entityManager.persist(timezone);

        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        entityManager.persist(timezoneEmp);

        entityManager.flush();
    }

    @Test
    @Rollback(false)
    public void testFindDefaultTimezoneName() {
        // Given
        Long empNo = 1L;

        // When
        String timezoneName = myPageRepository.findDefaultTimezoneName(empNo);

        // Then
        assertThat(timezoneName).isEqualTo("UTC");
    }
}
