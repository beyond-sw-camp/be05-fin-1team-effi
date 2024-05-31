package com.example.effi.domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "employee")
public class Employee {
    
    @Id
    @Column(name = "emp_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_no", nullable = false)
    private Long empNo;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_num", nullable = false)
    private String phoneNum;

    @Column(name = "extension_num", nullable = false)
    private String extensionNum;

    @Column(name = "rank", nullable = false)
    private String rank;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;

    @Builder(toBuilder = true)
    public Employee(Long id, Long empNo, String company, String name, String email, String phoneNum, String extensionNum, String rank, String password, Dept dept) {
        this.id = id;
        this.empNo = empNo;
        this.company = company;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.extensionNum = extensionNum;
        this.rank = rank;
        this.password = password;
        this.dept = dept;
    }
}
