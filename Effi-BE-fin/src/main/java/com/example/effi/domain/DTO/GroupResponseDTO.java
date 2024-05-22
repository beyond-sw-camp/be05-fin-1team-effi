package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupResponseDTO {
    private String code;
    private String message;
    private Data data;

    @Builder
    public GroupResponseDTO(String code, String message, String groupName) {
        this.code = code;
        this.message = message;
        this.data = new Data(groupName);
    }

    @Getter
    @NoArgsConstructor
    public static class Data {
        private String groupName;

        public Data(String groupName) {
            this.groupName = groupName;
        }
    }
}
