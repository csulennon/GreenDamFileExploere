package com.cmcm.greendamexplorer.exception;

/**
 * 未初始化实例
 * 
 * @author Administrator
 * 
 */
public class NoSuchInstanceException extends Exception {
    
    private static final long serialVersionUID = -1844948615455296890L;
    
    public NoSuchInstanceException() {
        super();
    }

    public NoSuchInstanceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoSuchInstanceException(String detailMessage) {
        super(detailMessage);
    }

    public NoSuchInstanceException(Throwable throwable) {
        super(throwable);
    }

}
