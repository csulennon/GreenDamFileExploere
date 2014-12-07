package com.cmcm.greendamexplorer.core.engine.service.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.os.RemoteException;

import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class CopyFileEngine extends ICopyFiles.Stub {

    private ICopyFilesCallback mCallback = null;
    private CopyThread mThread = null;
    private List<String> mSourceFiles = null;
    private String mDestinationPath = null;

    private long mAllFileSize = 0;
    private long mCopyedSize = 0;
    private int mProgress = 0;
    private int mLastProgress = 0;
    private long mLastCopySize = 0;

    private boolean mIsRunning = true;
    private boolean mIsWait = false;

    private Object mLockObj = new Object();

    private Context mContext = null;

    private CopyNotyfication mNotyfication = null;
    private static int mNotificationId = 0;

    public CopyFileEngine(Context context) {
        mContext = context;
        mNotyfication = new CopyNotyfication(mContext, this);
    }

    @Override
    public void start(List<String> files, String des) throws RemoteException {
        mSourceFiles = files;
        mDestinationPath = des;
        if (mThread == null || !mThread.isAlive()) {

            mThread = new CopyThread();
            init();
            mThread.start();
            mNotyfication.startNotyfy(mNotificationId++);
            if (mCallback != null) {
                mCallback.onStart();
            }
        }
    }

    private void init() {
        mAllFileSize = 0;
        mCopyedSize = 0;
        mIsRunning = true;
        mIsWait = false;
        mProgress = 0;
        mLastProgress = 0;
        mLastCopySize = 0;
    }

    @Override
    public void cancel() throws RemoteException {
        mIsRunning = false;

        synchronized (mLockObj) {
            mLockObj.notify();
        }

        if (mCallback != null) {
            mCallback.onCancel(mCopyedSize);
        }
    }

    @Override
    public void pause() throws RemoteException {
        mIsWait = true;
        synchronized (mLockObj) {
            mLockObj.notify();
        }
        if (mCallback != null) {
            mCallback.onPause();
        }
    }

    @Override
    public void resume() throws RemoteException {
        mIsWait = false;
        synchronized (mLockObj) {
            mLockObj.notify();
        }
        if (mCallback != null) {
            mCallback.onResume();
        }
    }

    @Override
    public void registerCallback(ICopyFilesCallback callback) throws RemoteException {
        mCallback = callback;
    }

    @Override
    public void unregisterCallback(ICopyFilesCallback callback) throws RemoteException {
        mCallback = null;
    }

    class CopyThread extends Thread {

        @Override
        public void run() {

            if (mCallback != null) {
                try {
                    mCallback.postUpdate("正在统计中....", mAllFileSize, mCopyedSize, 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            mAllFileSize = FileUtils.getFileSize(mSourceFiles);

            // 复制文件
            for (int i = 0; i < mSourceFiles.size(); i++) {
                if (!mIsRunning) {
                    break;
                }

                while (mIsWait) {
                    System.out.println("--->等待复制中");
                    synchronized (mLockObj) {
                        try {
                            mLockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                File file = new File(mSourceFiles.get(i));
                if (file.exists() && file.isFile()) {
                    copyFile(mSourceFiles.get(i), mDestinationPath);
                } else if (file.exists() && file.isDirectory()) {
                    copyFolder(mSourceFiles.get(i), mDestinationPath);
                }

            }
            mNotyfication.setNotyfiy(mNotificationId - 1, "复制完成", mAllFileSize, mAllFileSize, 100);
            // mNotyfication.clear(mNotificationId - 1);

            // 回调通知界面
            try {
                if (mCallback != null) {
                    if (mIsRunning == false) {
                        mCallback.onCancel(mCopyedSize);
                    } else {
                        mCallback.onFinish(mCopyedSize);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 复制单个文件
     * 
     * @param oldPath
     *            String 原文件路径 如：c:/fqf.txt
     * @param newPath
     *            String 复制父路径
     * @return boolean
     */
    public void copyFile(String oldPath, String destDirPath) {
        File oldFile = new File(oldPath);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            String newPath = null;
            if (destDirPath.endsWith(File.separator)) {
                newPath = destDirPath + oldFile.getName();
            } else {
                newPath = destDirPath + File.separator + oldFile.getName();
            }

            File newFile = new File(newPath);

            if (newFile.exists()) {
                newFile = new File(FileUtils.getNewFileName(newPath));
            }

            in = new FileInputStream(oldFile); // 读入原文件
            out = new FileOutputStream(newFile);
            long len = oldFile.length();
            if (len > 4 * TextUtil.SIZE_MB) {
                len = 4 * TextUtil.SIZE_MB;
            }
            byte[] buffer = new byte[(int) len];

            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                // System.out.println("--->" + mCopyedSize);
                out.write(buffer, 0, byteread);
                if (byteread == 0) {
                    break;
                }
                try {
                    updateToProgress(oldFile, byteread);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }// 更新到进度条
            }
        } catch (IOException e) {
            System.out.println("出错啦~~~" + e);
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新到进度
     * 
     * @param oldFile
     * @param byteread
     * @throws RemoteException
     */
    private void updateToProgress(File oldFile, int byteread) throws RemoteException {
        mCopyedSize += byteread;
        mLastProgress = mProgress;
        if (mAllFileSize != 0) {
            mProgress = (int) (mCopyedSize * 100 / mAllFileSize);
        } else {
            mProgress = 100;
        }
        if (mLastProgress < mProgress || mCopyedSize - mLastCopySize > 8 * TextUtil.SIZE_MB) {
            mLastCopySize = mCopyedSize;
            if (mCallback != null) {
                mCallback.postUpdate(oldFile.getName(), mAllFileSize, mCopyedSize, mProgress);
            }
            mNotyfication.setNotyfiy(mNotificationId - 1, oldFile.getName(), mAllFileSize, mCopyedSize, mProgress);
        }
    }

    /**
     * 复制整个文件夹内容
     * 
     * @param oldPath
     *            String 原文件路径 如：c:/fqf
     * @param newPath
     *            String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String sourceDirPath, String targetDirPath) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            File oldDir = new File(sourceDirPath);
            File newDir = new File(targetDirPath + File.separator + oldDir.getName());
            if (newDir.exists()) {
                newDir = new File(FileUtils.getNewFolderPath(sourceDirPath));// 添加副本
            }
            newDir.mkdirs(); // 如果文件夹不存在 则建立新文件夹

            targetDirPath = newDir.getCanonicalPath();// /

            File[] files = oldDir.listFiles();

            if (files == null) {
                return;
            }

            for (int i = 0; i < files.length; i++) {

                if (!mIsRunning) {
                    break;
                }

                while (mIsWait) {
                    synchronized (mLockObj) {
                        try {
                            mLockObj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (files[i].isFile()) {
                    String sourceFile = sourceDirPath + File.separator + files[i].getName();
                    copyFile(sourceFile, targetDirPath);
                } else if (files[i].isDirectory()) {// 如果是子文件夹
                    String sourceDir = sourceDirPath + File.separator + files[i].getName();
                    String targetDir = targetDirPath + File.separator + files[i].getName();
                    copyFolder(sourceDir, targetDir);
                }
            }
        } catch (Exception e) {
            mNotyfication.setNotyfiy(mNotificationId - 1, "复制完成", mAllFileSize, mAllFileSize, 100);
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        } finally {

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
