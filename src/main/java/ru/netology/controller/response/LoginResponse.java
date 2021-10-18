package ru.netology.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    
    @JsonProperty("auth-token")
    public final String authToken;

    public LoginResponse(String authToken) {
        this.authToken = authToken;
    }

}
