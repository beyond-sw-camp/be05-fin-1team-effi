package com.example.effi.domain.DTO;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ScheduleTags {
    
    private String message;
    private List<String> tags;
}