package ru.netology.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ru.netology.controller.response.ErrorResponse;
import ru.netology.controller.response.FileListResponseItem;
import ru.netology.entity.Session;
import ru.netology.security.AppAuthenticationProvider;
import ru.netology.service.FileService;

@RestController
public class FileListController {


    @Autowired
    private FileService fileService;

    @GetMapping("list")
    @Secured(AppAuthenticationProvider.ROLE_AUTHORIZED)
    public List<FileListResponseItem> list(@RequestParam(required = false) Integer limit, @AuthenticationPrincipal Session session) {
        return fileService.getUserFileList(session.getUser(), limit).stream()
            .map(file -> new FileListResponseItem(file.getName(), file.getSize()))
            .collect(Collectors.toList());
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

}
