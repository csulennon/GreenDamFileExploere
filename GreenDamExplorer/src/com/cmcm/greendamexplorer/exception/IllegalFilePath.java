package com.cmcm.greendamexplorer.exception;

public class IllegalFilePath extends Exception {
    private static final long serialVersionUID = 7693529901733733265L;

    public IllegalFilePath() {
        super();
    }

    public IllegalFilePath(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IllegalFilePath(String detailMessage) {
        super(detailMessage);
    }

    public IllegalFilePath(Throwable throwable) {
        super(throwable);
    }
    
    

}
