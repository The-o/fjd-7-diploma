package ru.netology.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.netology.controller.request.LoginRequest;
import ru.netology.controller.response.ErrorResponse;
import ru.netology.controller.response.LoginResponse;
import ru.netology.entity.Session;
import ru.netology.service.AuthenticationService;
import ru.netology.service.exception.UserNotFoundException;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path = "login", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public LoginResponse login(@RequestBody(required = true) LoginRequest loginRequest, HttpServletRequest request) {
        Session session = authenticationService.createSession(loginRequest.getLogin(), loginRequest.getPassword(), request.getRemoteAddr());

        return new LoginResponse(session.getUuid());
    }


    @PostMapping("logout")
    public void logout(@AuthenticationPrincipal(errorOnInvalidType = false) Session session) {
        if (session == null) {
            return;
        }

        authenticationService.closeSession(session);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException() {
        return ErrorResponse.ERROR_LOGIN;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException() {
        return ErrorResponse.ERROR_LOGIN;
    }

}
