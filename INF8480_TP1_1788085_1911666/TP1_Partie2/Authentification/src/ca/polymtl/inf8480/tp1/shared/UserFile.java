package ca.polymtl.inf8480.tp1.shared;

import java.io.Serializable;

public class UserFile implements Serializable{
    private String fileName;
    private String lockUsername;
    private String content;

    public UserFile(String fileName, String lockUsername, String content){
        this.fileName = fileName;
        this.lockUsername = lockUsername;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLockUsername() {
        return lockUsername;
    }

    public void setLockUsername(String lockUsername) {
        this.lockUsername = lockUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
