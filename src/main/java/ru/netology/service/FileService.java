package ru.netology.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ru.netology.entity.File;
import ru.netology.entity.User;
import ru.netology.repository.FileRepository;
import ru.netology.service.exception.FileNotFoundException;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileStorageService fileStorage;

    public List<File> getUserFileList(User user, Integer limit) {
        PageRequest pageRequest = null;

        if (limit != null) {
            pageRequest = PageRequest.ofSize(limit);
        }

        return fileRepository.findByUserId(user.getId(), pageRequest);
    }

    @Transactional
    public void saveUserFile(User user, String filename, String hash, MultipartFile file) throws IOException {
        File fileEntity = fileRepository.findByUserIdAndName(user.getId(), filename);

        if (fileEntity == null) {
            fileEntity = new File();
            fileEntity.setUser(user);
            fileEntity.setName(filename);
        }

        fileEntity.setHash(hash);
        fileEntity.setSize(file.getSize());
        fileRepository.saveAndFlush(fileEntity);

        fileStorage.copy(file.getInputStream(), fileEntity);
    }

    @Transactional
    public void deleteUserFile(User user, String filename) throws IOException {
        File fileEntity = fileRepository.findByUserIdAndName(user.getId(), filename);

        if (fileEntity == null) {
            throw new FileNotFoundException();
        }

        fileStorage.delete(fileEntity);
        fileRepository.delete(fileEntity);
    }

    @Transactional
    public void renameUserFile(User user, String oldName, String newName) throws IOException {
        File srcFileEntity = fileRepository.findByUserIdAndName(user.getId(), oldName);

        if (srcFileEntity == null) {
            throw new FileNotFoundException();
        }

        if (oldName.equals(newName)) {
            return;
        }

        try {
            deleteUserFile(user, newName);
        } catch (FileNotFoundException e) {
        }

        srcFileEntity.setName(newName);
        fileRepository.saveAndFlush(srcFileEntity);
    }


    public InputStream getUserFileStream(User user, String filename) throws IOException {
        File fileEntity = fileRepository.findByUserIdAndName(user.getId(), filename);

        if (fileEntity == null) {
            throw new FileNotFoundException();
        }

        return fileStorage.stream(fileEntity);
    }

}
