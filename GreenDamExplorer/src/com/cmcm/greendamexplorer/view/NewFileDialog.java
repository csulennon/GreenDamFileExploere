package com.cmcm.greendamexplorer.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.fragment.FileListPageFragment;

public class NewFileDialog extends Dialog implements OnClickListener {
    // private static final String TAG = "NewFileDialog";

    private String mCurrentFolder = ResourceManager.mExternalStoragePath;

    private View mView = null;
    private Button mBtnNewFolder = null;
    private Button mBtnNewFile = null;
    private Button mBtnCancel = null;

    private List<SimpleFileInfo> mFileINfos = null;

    private FileListPageFragment mFragment = null;

    //
    public NewFileDialog(Context context, List<SimpleFileInfo> infos, String currentFolder, FileListPageFragment fragment) {
        super(context);
        this.mFileINfos = infos;
        this.mCurrentFolder = currentFolder;
        this.mFragment = fragment;
        init();
    }

    public NewFileDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public NewFileDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public NewFileDialog(Context context) {
        super(context);
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_file, null);
        // mView = view.findViewById(R.id.mNewFileDialogRoot);

        mBtnNewFolder = (Button) mView.findViewById(R.id.mBtnNewDialogNewFolder);
        mBtnNewFile = (Button) mView.findViewById(R.id.mBtnNewDialogNewFile);
        mBtnCancel = (Button) mView.findViewById(R.id.mBtnNewDialogCancel);

        mBtnNewFolder.setOnClickListener(this);
        mBtnNewFile.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        this.setTitle("请选择创建的文件类型");
        this.setContentView(mView);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        NewFileDialogInput inputDialog = null;
        switch (v.getId()) {
        case R.id.mBtnNewDialogNewFile:
            inputDialog = new NewFileDialogInput(getContext(), mFragment, mFileINfos, mCurrentFolder, NewFileDialogInput.CREATE_TYPE_FILE);
            inputDialog.show();
            dismiss();
            break;

        case R.id.mBtnNewDialogNewFolder:
            inputDialog = new NewFileDialogInput(getContext(), mFragment, mFileINfos, mCurrentFolder, NewFileDialogInput.CREATE_TYPE_FOLDER);
            inputDialog.show();
            dismiss();
            break;

        case R.id.mBtnNewDialogCancel:
            dismiss();
            break;

        default:
            break;
        }

    }

}
