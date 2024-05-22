package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UpdateGroupNameRequest {
    private String newGroupName;

    @Builder
    public UpdateGroupNameRequest(String newGroupName) {
        this.newGroupName = newGroupName;
    }
}
