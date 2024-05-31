package com.example.effi.domain.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
    @Id
    private Long tokenId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    private String refreshToken;

    @Builder(toBuilder = true)
    public RefreshToken(Long tokenId, Employee employee, String refreshToken) {
        this.tokenId = tokenId;
        this.employee = employee;
        this.refreshToken = refreshToken;
    }


    public boolean validateRefreshToken(String refreshToken) {
        return this.refreshToken.equals(refreshToken);
    }
}
