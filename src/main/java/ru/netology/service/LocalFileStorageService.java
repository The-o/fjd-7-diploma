package ru.netology.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.netology.entity.File;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${application.fileStorage}")
    private String fileStorage;

    public void copy(InputStream inputStream, File fileEntity) throws IOException {
        java.nio.file.Files.copy(inputStream, getFilePath(fileEntity), StandardCopyOption.REPLACE_EXISTING);
    }

    public void delete(File fileEntity) throws IOException {
        java.nio.file.Files.deleteIfExists(getFilePath(fileEntity));
    }

    public InputStream stream(File fileEntity) throws IOException {
        return new FileInputStream(getFilePath(fileEntity).toFile());
    }

    private Path getFilePath(File fileEntity) {
        return Path.of(fileStorage, String.valueOf(fileEntity.getId()));
    }

}
