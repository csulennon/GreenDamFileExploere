package com.cmcm.greendamexplorer.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.adapter.SimpleFileListAdapter;
import com.cmcm.greendamexplorer.core.engine.service.delete.IDeleteFiles;
import com.cmcm.greendamexplorer.core.engine.service.delete.IDeleteFilesCallback;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

@SuppressLint("HandlerLeak")
public class DeleteFileDialog extends Dialog implements OnClickListener {
    private static final int MSG_DEL_CANCEL = 0x3001;
    private static final int MSG_UPDATE = 0x3002;
    private static final int MSG_DEL_FINISH = 0x03003;
    private static final String TAG = "DeleteFileDialog";
    private final String BIND_ACTION = "com.cmcm.greendamexplorer.core.engine.service.delete.DeleteFileService";
    private View mView = null;
    private ListView mListView = null;
    private ProgressBar mProgressBar = null;
    private TextView mTvFileName = null;
    private TextView mTvFileSize = null;
    private Button mBtnOk = null;
    private Button mBtnCancel = null;

    private IOnDialogBtnClickListener mClickListener = null;

    private IDeleteFiles mService = null;
    private ServiceImplConnection mServiceConnection = new ServiceImplConnection();

    List<String> mFiles = null;

    private Activity mActivity = null;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DEL_CANCEL) {
                mProgressBar.setVisibility(View.GONE);
                if ((Long) msg.obj == 0) {
                    Toast.makeText(mView.getContext(), "已取消", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(mView.getContext(), "取消成功，已删除" + TextUtil.getSizeSting((Long) msg.obj) + "数据！", Toast.LENGTH_SHORT).show();

                }
            } else if (msg.what == MSG_UPDATE) {
                Holder holder = (Holder) msg.obj;
                mProgressBar.setProgress(holder.progress);
                mTvFileName.setText(FileUtils.getFileName(holder.fileName));
                mTvFileSize.setText(TextUtil.getSizeSting(holder.deleteSize) + "/" + TextUtil.getSizeSting(holder.allSize));

            } else if (msg.what == MSG_DEL_FINISH) {
                mProgressBar.setVisibility(View.GONE);
                if (mClickListener != null) {
                    mClickListener.onOkClick(mBtnOk, "");
                }
                dismiss();
                if ((Long) msg.obj != 0) {
                    Toast.makeText(mView.getContext(), "成功删除 " + TextUtil.getSizeSting((Long) msg.obj) + " 数据！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mView.getContext(), "成功删除数据！", Toast.LENGTH_SHORT).show();
                }
            }
        }

    };

    public void setOnDialogBtnClickListener(IOnDialogBtnClickListener listener) {
        mClickListener = listener;
    }

    public DeleteFileDialog(Context context, Activity mActivity, List<String> files) {
        super(context);
        this.mActivity = mActivity;
        this.mFiles = files;
        init();
    }

    public DeleteFileDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public DeleteFileDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public DeleteFileDialog(Context context) {
        super(context);
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_file, null);
        mView = view.findViewById(R.id.mDelFileDialogRoot);

        mListView = (ListView) view.findViewById(R.id.mDelDialogListView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.mDelDialogProgressBar);

        mTvFileName = (TextView) mView.findViewById(R.id.mTvDelDialogFileName);
        mTvFileName.setText("");

        mTvFileSize = (TextView) mView.findViewById(R.id.mTvDelDialogSize);
        mTvFileSize.setText("");

        mBtnOk = (Button) mView.findViewById(R.id.mBtnDelDialogOk);
        mBtnCancel = (Button) mView.findViewById(R.id.mBtnDelDialogCancel);

        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mListView.setAdapter(new SimpleFileListAdapter(getContext(), mFiles));

        this.setCanceledOnTouchOutside(false);
        this.setTitle("是否删除以下文件");
        this.setContentView(mView);
        Intent intent = new Intent();
        intent.setAction(BIND_ACTION);
        mActivity.bindService(intent, mServiceConnection, Activity.BIND_AUTO_CREATE);

    }

    @Override
    public void dismiss() {
        if (mServiceConnection != null) {
            mActivity.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        super.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.mBtnDelDialogOk:
            try {
                mService.registerCallback(mCallback);
                mService.start(mFiles);
            } catch (RemoteException e) {
                Log.e(TAG, "远程调用Start失败");
                e.printStackTrace();
            }
            break;

        case R.id.mBtnDelDialogCancel:
            // 取消删除
            try {
                mService.cancel();
                mService.unregisterCallback(mCallback);
                dismiss();
                if (mServiceConnection != null) {
                    mActivity.unbindService(mServiceConnection);
                }
                if (mClickListener != null) {
                    mClickListener.onCancelClick(mBtnOk);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "远程调用Start失败");
                e.printStackTrace();
            }
            break;
        default:
            break;
        }

    }

    class ServiceImplConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IDeleteFiles.Stub.asInterface(service);
            if (mService != null) {
                try {
                    mService.registerCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private IDeleteFilesCallback mCallback = new IDeleteFilesCallback.Stub() {

        @Override
        public void postUpdate(String fileName, long allSize, long hasDelete, int progress) throws RemoteException {
            Message msg = new Message();
            msg.what = MSG_UPDATE;
            msg.obj = new Holder(fileName, allSize, hasDelete, progress);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onCancel(long hasDeletedSize) throws RemoteException {
            Message msg = new Message();
            msg.what = MSG_DEL_CANCEL;
            msg.obj = hasDeletedSize;
            mHandler.sendMessage(msg);

        }

        @Override
        public void onStart() throws RemoteException {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setMax(100);
            mProgressBar.setProgress(0);
        }

        @Override
        public void onResume() throws RemoteException {

        }

        @Override
        public void onPause() throws RemoteException {

        }

        @Override
        public void onFinish(long hasDeletedSize) throws RemoteException {
            Message msg = new Message();
            msg.what = MSG_DEL_FINISH;
            msg.obj = hasDeletedSize;
            mHandler.sendMessage(msg);
        }
    };

    class Holder {
        public Holder(String fileName, long allSize, long deleteSize, int progress) {
            this.fileName = fileName;
            this.allSize = allSize;
            this.deleteSize = deleteSize;
            this.progress = progress;
        }

        String fileName = "";
        long allSize;
        long deleteSize;
        int progress;
    }

}
