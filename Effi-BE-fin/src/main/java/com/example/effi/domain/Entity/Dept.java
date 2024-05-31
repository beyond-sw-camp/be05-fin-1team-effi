package com.example.effi.domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "dept")
public class Dept {
    
    @Id
    @Column(name = "dept_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    @Builder
    public Dept(String deptName) {
        this.deptName = deptName;
    }
}
