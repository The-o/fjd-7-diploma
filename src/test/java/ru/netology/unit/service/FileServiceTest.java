package ru.netology.unit.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import ru.netology.entity.File;
import ru.netology.entity.User;
import ru.netology.repository.FileRepository;
import ru.netology.service.FileService;
import ru.netology.service.FileStorageService;
import ru.netology.service.exception.FileNotFoundException;

public class FileServiceTest {

    private FileRepository fileRepositoryMock;
    private FileStorageService fileStorageMock;
    private FileService service;

    @BeforeEach
    public void initTests() {
        fileRepositoryMock = mock(FileRepository.class);
        fileStorageMock = mock(FileStorageService.class);

        service = new FileService(fileRepositoryMock, fileStorageMock);
    }

    @Test
    public void testGetUserFileListLimited() {
        User userEntity = new User();
        userEntity.setId(12);

        List<File> list = new ArrayList<>() {{
            add(new File());
            add(new File());
        }};

        PageRequest pageRequest = PageRequest.ofSize(10);

        when(fileRepositoryMock.findByUserId(12, pageRequest))
            .thenReturn(list);
        List<File> returnedList = service.getUserFileList(userEntity, 10);

        assertEquals(list, returnedList);
    }

    @Test
    public void testGetUserFileListWhole() {
        User userEntity = new User();
        userEntity.setId(12);

        List<File> list = new ArrayList<>() {{
            add(new File());
            add(new File());
        }};

        when(fileRepositoryMock.findByUserId(12, null))
            .thenReturn(list);
        List<File> returnedList = service.getUserFileList(userEntity, null);

        assertEquals(list, returnedList);
    }

    @Test
    public void testSaveUserFileNew() throws IOException{
        User userEntity = new User();
        userEntity.setId(12);

        String fileContents = "Some file data";
        File fileEntity = new File();
        fileEntity.setName("test.pdf");
        fileEntity.setHash("hash");
        fileEntity.setSize(fileContents.length());
        fileEntity.setUser(userEntity);

        MockMultipartFile fileMock = new MockMultipartFile("test.pdf", new ByteArrayInputStream(fileContents.getBytes()));

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(null);

        service.saveUserFile(userEntity, "test.pdf", "hash", fileMock);
        
        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        verify(fileRepositoryMock).saveAndFlush(fileEntity);
        verify(fileStorageMock).copy(streamCaptor.capture(), fileCaptor.capture());

        assertAll(
            () -> assertEquals(fileEntity, fileCaptor.getValue()),
            () -> assertArrayEquals(fileMock.getInputStream().readAllBytes(), streamCaptor.getValue().readAllBytes())
        );
    }

    @Test
    public void testSaveUserFileRewrite() throws IOException{
        User userEntity = new User();
        userEntity.setId(12);

        String fileContents = "Some file data";

        File fileEntity = new File();
        fileEntity.setId(13);
        fileEntity.setName("test.pdf");
        fileEntity.setHash("hash");
        fileEntity.setSize(fileContents.length());
        fileEntity.setUser(userEntity);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(fileEntity);

        MockMultipartFile fileMock = new MockMultipartFile("test.pdf", new ByteArrayInputStream(fileContents.getBytes()));

        service.saveUserFile(userEntity, "test.pdf", "hash", fileMock);
        
        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        verify(fileRepositoryMock).saveAndFlush(fileEntity);
        verify(fileStorageMock).copy(streamCaptor.capture(), fileCaptor.capture());

        assertAll(
            () -> assertEquals(fileEntity, fileCaptor.getValue()),
            () -> assertArrayEquals(fileMock.getInputStream().readAllBytes(), streamCaptor.getValue().readAllBytes())
        );
    }

    @Test
    public void testDeleteUserFileNotFound() {
        User userEntity = new User();
        userEntity.setId(12);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(null);
        
        assertThrows(FileNotFoundException.class, () -> service.deleteUserFile(userEntity, "test.pdf"));
    }

    @Test
    public void testDeleteUserFileOK() throws IOException {
        User userEntity = new User();
        userEntity.setId(12);

        File fileEntity = new File();
        fileEntity.setId(13);
        fileEntity.setName("test.pdf");
        fileEntity.setHash("hash");
        fileEntity.setSize(11);
        fileEntity.setUser(userEntity);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(fileEntity);

        service.deleteUserFile(userEntity, "test.pdf");

        verify(fileRepositoryMock).delete(fileEntity);
        verify(fileStorageMock).delete(fileEntity);
    }

    @Test
    public void testRenameUserFileNotExists() {
        User userEntity = new User();
        userEntity.setId(12);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(null);
        
        assertThrows(FileNotFoundException.class, () -> service.renameUserFile(userEntity, "test.pdf", "test.json"));
    }

    @Test
    public void testRenameUserFileOK() throws IOException {
        User userEntity = new User();
        userEntity.setId(12);

        File fileEntity = new File();
        fileEntity.setId(13);
        fileEntity.setName("test.pdf");
        fileEntity.setHash("hash");
        fileEntity.setSize(11);
        fileEntity.setUser(userEntity);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(fileEntity);
        when(fileRepositoryMock.findByUserIdAndName(12, "test.json"))
            .thenReturn(null);

        service.renameUserFile(userEntity, "test.pdf", "test.json");
        
        verify(fileRepositoryMock).saveAndFlush(fileEntity);
        assertEquals(fileEntity.getName(), "test.json");
    }

    @Test
    public void testRenameUserFileRewrite() throws IOException {
        User userEntity = new User();
        userEntity.setId(12);

        File srcFileEntity = new File();
        srcFileEntity.setId(13);
        srcFileEntity.setName("test.pdf");
        srcFileEntity.setHash("hash");
        srcFileEntity.setSize(11);
        srcFileEntity.setUser(userEntity);

        File dstFileEntity = new File();
        srcFileEntity.setId(14);
        srcFileEntity.setName("test.json");
        srcFileEntity.setHash("hash");
        srcFileEntity.setSize(2);
        srcFileEntity.setUser(userEntity);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(srcFileEntity);
        when(fileRepositoryMock.findByUserIdAndName(12, "test.json"))
            .thenReturn(dstFileEntity);

        service.renameUserFile(userEntity, "test.pdf", "test.json");
        verify(fileRepositoryMock).delete(dstFileEntity);
        verify(fileStorageMock).delete(dstFileEntity);
        verify(fileRepositoryMock).saveAndFlush(srcFileEntity);

        assertEquals(srcFileEntity.getName(), "test.json");
    }

    @Test
    public void testGetUserFileStreamNotFound() {
        User userEntity = new User();
        userEntity.setId(12);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(null);

        assertThrows(FileNotFoundException.class, () -> service.getUserFileStream(userEntity, "test.pdf"));
    }

    @Test
    public void testGetUserFileStreamOK() throws IOException {
        User userEntity = new User();
        userEntity.setId(12);

        File fileEntity = new File();
        fileEntity.setId(13);
        fileEntity.setName("test.pdf");
        fileEntity.setHash("hash");
        fileEntity.setSize(11);
        fileEntity.setUser(userEntity);

        when(fileRepositoryMock.findByUserIdAndName(12, "test.pdf"))
            .thenReturn(fileEntity);
        when(fileStorageMock.stream(fileEntity))
            .thenReturn(new ByteArrayInputStream("TEST".getBytes()));

        InputStream stream = service.getUserFileStream(userEntity, "test.pdf");

        assertArrayEquals("TEST".getBytes(), stream.readAllBytes());
    }
}
