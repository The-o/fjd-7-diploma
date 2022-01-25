package ru.netology.unit.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.netology.entity.Session;
import ru.netology.entity.User;
import ru.netology.repository.SessionRepository;
import ru.netology.repository.UserRepository;
import ru.netology.service.AuthenticationService;
import ru.netology.service.exception.SessionNotFoundException;
import ru.netology.service.exception.UserNotFoundException;

public class AuthenticationServiceTest {

    private UserRepository userRepositoryMock;
    private SessionRepository sessionRepositoryMock;
    private PasswordEncoder passwordEncoderMock;
    private AuthenticationService service;

    @BeforeEach
    public void initTests() {
        userRepositoryMock = mock(UserRepository.class);
        sessionRepositoryMock = mock(SessionRepository.class);
        passwordEncoderMock = mock(PasswordEncoder.class);

        service = new AuthenticationService(userRepositoryMock, sessionRepositoryMock, passwordEncoderMock);
    }

    @Test
    public void testCreateSessionNoUser() {
        when(userRepositoryMock.findByLogin("login"))
            .thenReturn(null);
        
        assertThrows(UserNotFoundException.class, () -> service.createSession("login", "password", "127.0.0.1"));
    }

    @Test
    public void testCreateSessionWrongPassword() {
        User userEntity = new User();
        userEntity.setId(1);
        userEntity.setLogin("login");
        userEntity.setPassword("userPassword");

        when(userRepositoryMock.findByLogin("login"))
            .thenReturn(userEntity);
        
        when(passwordEncoderMock.matches("password", "userPassword"))
            .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.createSession("login", "password", "127.0.0.1"));
    }

    @Test
    public void testCreateSessionOK() {
        User userEntity = new User();
        userEntity.setId(1);
        userEntity.setLogin("login");
        userEntity.setPassword("userPassword");

        when(userRepositoryMock.findByLogin("login"))
            .thenReturn(userEntity);
        
        when(passwordEncoderMock.matches("password", "userPassword"))
            .thenReturn(true);

        Session sessionEntity = service.createSession("login", "password", "127.0.0.1");

        verify(sessionRepositoryMock).saveAndFlush(sessionEntity);

        assertAll(
            () -> assertEquals(sessionEntity.getIp(), "127.0.0.1"),
            () -> assertEquals(sessionEntity.getUser(), userEntity),
            () -> assertDoesNotThrow(() -> UUID.fromString(sessionEntity.getUuid()))
        );
    }

    @Test
    public void testGetSessionNotFound() {
        when(sessionRepositoryMock.findByUuidAndIp("7171df8c-eb99-4c12-9e48-28b196c96600", "127.0.0.1"))
            .thenReturn(null);
        
        assertThrows(SessionNotFoundException.class, () -> service.getSession("7171df8c-eb99-4c12-9e48-28b196c96600", "127.0.0.1"));
    }

    @Test
    public void testGetSessionOK() {
        Session sessionEntity = new Session();

        when(sessionRepositoryMock.findByUuidAndIp("7171df8c-eb99-4c12-9e48-28b196c96600", "127.0.0.1"))
            .thenReturn(sessionEntity);
        
        Session returnedSession = service.getSession("7171df8c-eb99-4c12-9e48-28b196c96600", "127.0.0.1");
        
        assertEquals(sessionEntity, returnedSession);
    }

    @Test
    public void testCloseSession() {
        Session sessionEntity = new Session();

        service.closeSession(sessionEntity);

        verify(sessionRepositoryMock).delete(sessionEntity);
    }
}


