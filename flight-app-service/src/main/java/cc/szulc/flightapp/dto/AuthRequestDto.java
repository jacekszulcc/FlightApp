package cc.szulc.flightapp.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String username;
    private String password;
}