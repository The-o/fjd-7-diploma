package ru.netology.controller.response;

public class ErrorResponse {
    
    public static final ErrorResponse ERROR_LOGIN = new ErrorResponse(1, "Login error");
    public static final ErrorResponse ERROR_ACCESS_DENIED = new ErrorResponse(2, "Access denied");
    public static final ErrorResponse ERROR_UNAUTHORIZED = new ErrorResponse(3, "Unauthorized");
    public static final ErrorResponse ERROR_INPUT_DATA = new ErrorResponse(4, "Bad input data");
    public static final ErrorResponse ERROR_INTERNAL = new ErrorResponse(5, "Internal server error");

    public final int id;
    public final String message;

    public ErrorResponse(int id, String message) {
        this.id = id;
        this.message = message;
    }

}
