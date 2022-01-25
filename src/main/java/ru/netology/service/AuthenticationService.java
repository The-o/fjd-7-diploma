package ru.netology.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.netology.entity.Session;
import ru.netology.entity.User;
import ru.netology.repository.SessionRepository;
import ru.netology.repository.UserRepository;
import ru.netology.service.exception.SessionNotFoundException;
import ru.netology.service.exception.UserNotFoundException;

@Service
public class AuthenticationService {

    private UserRepository userRepository;

    private SessionRepository sessionRepository;

    private PasswordEncoder passwordEncoder;

    public AuthenticationService(
        UserRepository userRepository,
        SessionRepository sessionRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Session createSession(String login, String password, String ip) {
        User user = userRepository.findByLogin(login);

        if (user == null || password == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException();
        }

        Session session = new Session();
        session.setUser(user);
        session.setIp(ip);

        sessionRepository.saveAndFlush(session);

        return session;
    }

    public void closeSession(Session session) {
        sessionRepository.delete(session);
    }

    public Session getSession(String uuid, String ip) {
        Session session = sessionRepository.findByUuidAndIp(uuid, ip);

        if (session == null) {
            throw new SessionNotFoundException();
        }

        return session;
    }
}
