package ru.netology.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ru.netology.controller.request.FileRenameRequest;
import ru.netology.controller.response.ErrorResponse;
import ru.netology.entity.Session;
import ru.netology.security.AppAuthenticationProvider;
import ru.netology.service.FileService;
import ru.netology.service.exception.FileNotFoundException;

@RestController
public class FileController {

    private static int FILE_OUTPUT_BUFFER_SIZE = 1024;

    @Autowired
    private FileService fileService;

    @PostMapping(path = "file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Secured(AppAuthenticationProvider.ROLE_AUTHORIZED)
    @ResponseStatus(code = HttpStatus.OK)
    public void fileUpload(
        @RequestParam(required = false) String filename,
        @RequestPart(required = false) String hash,
        @RequestPart(required = true) MultipartFile file,
        @AuthenticationPrincipal Session session
    ) throws IOException {
        fileService.saveUserFile(session.getUser(), filename, hash, file);
    }

    @DeleteMapping(path = "file")
    @Secured(AppAuthenticationProvider.ROLE_AUTHORIZED)
    @ResponseStatus(code = HttpStatus.OK)
    public void fileDelete(
        @RequestParam(required = true) String filename,
        @AuthenticationPrincipal Session session
    ) throws IOException {
        fileService.deleteUserFile(session.getUser(), filename);
    }

    @PutMapping(path = "file", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Secured(AppAuthenticationProvider.ROLE_AUTHORIZED)
    @ResponseStatus(code = HttpStatus.OK)
    public void fileRename(
        @RequestParam(required = true) String filename,
        @RequestBody(required = true) FileRenameRequest renameRequest,
        @AuthenticationPrincipal Session session
    ) throws IOException {
        fileService.renameUserFile(session.getUser(), filename, renameRequest.getFilename());
    }

    @GetMapping(path = "file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    @Secured(AppAuthenticationProvider.ROLE_AUTHORIZED)
    public StreamingResponseBody fileGet(
        @RequestParam(required = true) String filename,
        @AuthenticationPrincipal Session session
    ) throws IOException {
        InputStream fileStream = fileService.getUserFileStream(session.getUser(), filename);

        return out -> {
            byte[] bytes = new byte[FILE_OUTPUT_BUFFER_SIZE];
            int length;
            while (0 <= (length = fileStream.read(bytes))) {
                out.write(bytes, 0, length);
            }
            fileStream.close();
        };
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccessDeniedException() {
        return ErrorResponse.ERROR_UNAUTHORIZED;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException() {
        return ErrorResponse.ERROR_INPUT_DATA;
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIOException() {
        return ErrorResponse.ERROR_INTERNAL;
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileNotFoundException() {
        return ErrorResponse.ERROR_INPUT_DATA;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException() {
        return ErrorResponse.ERROR_INPUT_DATA;
    }

}
