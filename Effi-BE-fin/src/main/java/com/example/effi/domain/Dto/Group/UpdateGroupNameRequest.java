package com.example.effi.domain.Dto.Group;

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
