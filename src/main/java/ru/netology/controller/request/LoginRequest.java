package ru.netology.controller.request;

public class LoginRequest {

    private String login;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String login, String password) {
        this.setLogin(login);
        this.setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
