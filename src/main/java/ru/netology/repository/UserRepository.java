package ru.netology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.netology.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    public User findByLogin(String login);

}
