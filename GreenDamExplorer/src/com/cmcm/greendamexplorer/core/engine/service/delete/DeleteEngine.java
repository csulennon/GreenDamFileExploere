package com.cmcm.greendamexplorer.core.engine.service.delete;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.cmcm.greendamexplorer.utils.FileUtils;

import android.os.RemoteException;

public class DeleteEngine extends IDeleteFiles.Stub {

    private List<String> mDeleteFiles = null;
    private IDeleteFilesCallback mCallback = null;
    private DeleteThread mThread = null;
    private long mAllDeleteFileSize = 0;
    private long mHasDeleteSize = 0;
    private int mProgress = 0;// 进度条
    private int mLastProgress = mProgress;

    private Object mLockObj = new Object();
    private boolean mIsRunning = true;
    private boolean mIsWait = false;

    @Override
    public void start(List<String> files) throws RemoteException {
        mDeleteFiles = files;
        mThread = new DeleteThread();
        if (mCallback != null) {
            init();
            mCallback.onStart();
        }
        if (!mThread.isAlive()) {
            mThread.start();
        }
    }

    private void init() {
        mIsRunning = true;
        mIsWait = false;
        mProgress = 0;
        mHasDeleteSize = 0;
        mAllDeleteFileSize = 0;
        mLastProgress = 0;
    }

    @Override
    public void cancel() throws RemoteException {
        mIsRunning = false;
        synchronized (mLockObj) {
            mLockObj.notify();
        }

        if (mCallback != null) {
            mCallback.onCancel(mHasDeleteSize);
        }
    }

    // 继续
    @Override
    public void resume() throws RemoteException {
        if (mIsRunning == true) {
            mIsWait = false;
            synchronized (mLockObj) {
                mLockObj.notify();
            }
            if (mCallback != null) {
                mCallback.onResume();
            }
        }
    }

    @Override
    public void pause() throws RemoteException {
        mIsWait = true;
    }

    @Override
    public void registerCallback(IDeleteFilesCallback callback) throws RemoteException {
        mCallback = callback;
    }

    @Override
    public void unregisterCallback(IDeleteFilesCallback callback) throws RemoteException {
        mCallback = null;
    }

    class DeleteThread extends Thread {
        @Override
        public void run() {

            if (mCallback != null) {
                try {
                    mCallback.postUpdate("正在统计中....", mAllDeleteFileSize, mHasDeleteSize, 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            mAllDeleteFileSize = FileUtils.getFileSize(mDeleteFiles);// 获取文件的总大小
            deleteFiles();

            if (mCallback != null) {
                try {
                    mCallback.onFinish(mHasDeleteSize);// 删除完成通知
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        private void deleteFiles() {
            for (int i = 0; i < mDeleteFiles.size(); i++) {

                if (!mIsRunning) {
                    break;
                }

                while (mIsWait) {
                    System.out.println("--->等待中");
                    synchronized (mLockObj) {
                        try {
                            mLockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                File file = new File(mDeleteFiles.get(i));
                if (file.exists() && file.canWrite()) {
                    if (file.isFile()) {// 是文件直接删除
                        updateToProgress(file);
                        file.delete();
                    } else if (file.isDirectory()) {
                        // 清空文件夹下的文件
                        clearFolder(file);
                        // 删除文件夹
                        file.delete();
                    }
                }
            }
        }

        // 更新到进度
        private void updateToProgress(File file) {
            mHasDeleteSize += file.length();
            mLastProgress = mProgress;
            if (mAllDeleteFileSize != 0) {
                mProgress = (int) (mHasDeleteSize * 100 / mAllDeleteFileSize);
            } else {
                mProgress = 100;
            }

            if (mProgress > mLastProgress) {
                if (mCallback != null) {
                    try {
                        mCallback.postUpdate(file.getCanonicalPath(), mAllDeleteFileSize, mHasDeleteSize, mProgress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void clearFolder(File folder) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {

                    if (!mIsRunning) {
                        break;
                    }

                    while (mIsWait) {
                        System.out.println("--->等待中");
                        synchronized (mLockObj) {
                            try {
                                mLockObj.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (file.isFile()) {
                        updateToProgress(file);
                        file.delete();
                    } else if (file.isDirectory()) {

                        clearFolder(file);
                        file.delete();
                    }
                }
            }
        }

    }

}
