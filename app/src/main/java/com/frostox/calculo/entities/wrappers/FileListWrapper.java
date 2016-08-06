package com.frostox.calculo.entities.wrappers;

import com.frostox.calculo.entities.File;

import java.util.List;

/**
 * Created by roger on 26/4/16.
 */
public class FileListWrapper {

    Long totalSize;

    List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
}
