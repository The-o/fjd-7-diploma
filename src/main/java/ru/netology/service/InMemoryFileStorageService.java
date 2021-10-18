package ru.netology.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.netology.entity.File;

public class InMemoryFileStorageService implements FileStorageService {

    Map<Integer, byte[]> storage = new ConcurrentHashMap<>();

    public void copy(InputStream inputStream, File fileEntity) throws IOException {
        storage.put(fileEntity.getId(), inputStream.readAllBytes());
    }

    public void delete(File fileEntity) throws IOException {
        Integer id = fileEntity.getId();
        if (!storage.containsKey(id)) {
            throw new FileNotFoundException();
        }
        storage.remove(id);

    }

    public InputStream stream(File fileEntity) throws IOException {
        Integer id = fileEntity.getId();
        if (!storage.containsKey(id)) {
            throw new FileNotFoundException();
        }

        return new ByteArrayInputStream(storage.get(id));
    }

}
