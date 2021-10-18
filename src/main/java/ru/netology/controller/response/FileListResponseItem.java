package ru.netology.controller.response;

public class FileListResponseItem {

    public final String filename;
    public final long size;

    public FileListResponseItem(String filename, long size) {
        this.filename = filename;
        this.size = size;
    }

}
