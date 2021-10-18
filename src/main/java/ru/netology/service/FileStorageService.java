package ru.netology.service;

import java.io.IOException;
import java.io.InputStream;

import ru.netology.entity.File;

public interface FileStorageService {

    public void copy(InputStream inputStream, File fileEntity) throws IOException;

    public void delete(File fileEntity) throws IOException;

    public InputStream stream(File fileEntity) throws IOException;

}
