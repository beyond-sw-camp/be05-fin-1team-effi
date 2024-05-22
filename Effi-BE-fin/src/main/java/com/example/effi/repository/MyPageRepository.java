package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyPageRepository extends JpaRepository<Employee, Long> {

    @Query("select t.timezoneName " +
            "from Timezone t " +
            "left join TimezoneEmp te on t.timezoneId = te.timezone.timezoneId " +
            "left join Employee e on e.id = te.employee.id " +
            "where e.empNo = :empNo " +
            "and te.defaultTimezone = true")
    String findDefaultTimezoneName(@Param("empNo") Long empNo);

}
