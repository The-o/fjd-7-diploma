package ru.netology.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.netology.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

    public List<File> findByUserId(int userId, Pageable pageable);
    public File findByUserIdAndName(int userId, String name);

}
