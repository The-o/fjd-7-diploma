package ru.netology.controller.request;

public class FileRenameRequest {

    private String filename;

    public FileRenameRequest() {
    }

    public FileRenameRequest(String filename) {
        this.setFilename(filename);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
