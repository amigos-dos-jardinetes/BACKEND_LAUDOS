package com.example.tcc.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponseDto {
    private String token;
    private Long expiresIn;
}
