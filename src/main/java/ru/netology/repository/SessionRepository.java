package ru.netology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.netology.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    public Session findByUuidAndIp(String uuid, String ip);

}
