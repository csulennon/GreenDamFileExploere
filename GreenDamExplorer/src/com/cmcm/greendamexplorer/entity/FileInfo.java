package com.cmcm.greendamexplorer.entity;

import java.io.File;
import java.net.URI;

public class FileInfo extends File {

    private static final long serialVersionUID = -6869243927158081838L;

    public FileInfo(URI uri) {
        super(uri);
    }

    public FileInfo(File dir, String name) {
        super(dir, name);
    }

    public FileInfo(String dirPath, String name) {
        super(dirPath, name);
    }
    
    public FileInfo(String path) {
        super(path);
    }
    

}
