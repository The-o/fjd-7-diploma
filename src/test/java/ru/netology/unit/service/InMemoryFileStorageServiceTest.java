package ru.netology.unit.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ru.netology.entity.File;
import ru.netology.service.FileStorageService;
import ru.netology.service.InMemoryFileStorageService;

public class InMemoryFileStorageServiceTest {

    @Test
    void testStreamNotFound() throws IOException {
        FileStorageService service = new InMemoryFileStorageService();
        File fileEntity = new File();
        fileEntity.setId(123);

        assertThrows(FileNotFoundException.class, () -> service.stream(fileEntity));
    }

    @Test
    void testStreamOK() throws IOException {
        FileStorageService service = new InMemoryFileStorageService();
        File fileEntity = new File();
        fileEntity.setId(123);

        service.copy(new ByteArrayInputStream("TEST".getBytes()), fileEntity);
        assertArrayEquals(service.stream(fileEntity).readAllBytes(), "TEST".getBytes());
    }

    @Test
    void testDeleteNotFound() throws IOException {
        FileStorageService service = new InMemoryFileStorageService();
        File fileEntity = new File();
        fileEntity.setId(123);

        assertThrows(FileNotFoundException.class, () -> service.delete(fileEntity));
    }

    @Test
    void testDeleteOK() throws IOException {
        FileStorageService service = new InMemoryFileStorageService();
        File fileEntity = new File();
        fileEntity.setId(123);

        service.copy(new ByteArrayInputStream("TEST".getBytes()), fileEntity);
        assertAll(
            () -> assertDoesNotThrow(() -> service.delete(fileEntity)),
            () -> assertThrows(FileNotFoundException.class, () -> service.delete(fileEntity))
        );
    }
}
