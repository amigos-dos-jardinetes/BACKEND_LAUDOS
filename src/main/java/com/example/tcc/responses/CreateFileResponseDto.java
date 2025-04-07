package com.example.tcc.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFileResponseDto {
    private Long id;
    private String userEmail;

    public CreateFileResponseDto(Long id, String userEmail) {
        this.id = id;
        this.userEmail = userEmail;
    }
}
