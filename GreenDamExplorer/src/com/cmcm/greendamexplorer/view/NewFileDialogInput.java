package com.cmcm.greendamexplorer.view;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.FileComparator;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.fragment.FileListPageFragment;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.TextUtil;

@SuppressLint("HandlerLeak")
public class NewFileDialogInput extends Dialog implements TextWatcher, OnClickListener {
    private static final String TAG = "DeleteFileDialog";

    public static final int CREATE_TYPE_FILE = 0;
    public static final int CREATE_TYPE_FOLDER = 1;

    private String mCurrentFolder = ResourceManager.mExternalStoragePath;
    private String mNewName = "noname";
    private int mCreateType = CREATE_TYPE_FILE;

    private View mView = null;
    private EditText mEtNewFileDialogName = null;
    private Button mBtnNewFileOk = null;
    private Button mBtnNewFileCancel = null;

    private IOnDialogBtnClickListener mClickListener = null;

    private List<SimpleFileInfo> mInfos = null;

    private FileListPageFragment mFragment = null;

    public void setOnDialogBtnClickListener(IOnDialogBtnClickListener listener) {
        mClickListener = listener;
    }

    public NewFileDialogInput(Context context, FileListPageFragment fragment, List<SimpleFileInfo> infos, String currentFolder, int createType) {
        super(context);
        this.mCurrentFolder = currentFolder;
        this.mCreateType = createType;
        this.mInfos = infos;
        this.mFragment = fragment;
        init();
    }

    public NewFileDialogInput(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public NewFileDialogInput(Context context, int theme) {
        super(context, theme);
        init();
    }

    public NewFileDialogInput(Context context) {
        super(context);
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_file_input, null);

        mEtNewFileDialogName = (EditText) mView.findViewById(R.id.mEtNewFileName);
        mBtnNewFileOk = (Button) mView.findViewById(R.id.mBtnNewFileInputDialogOk);
        mBtnNewFileCancel = (Button) mView.findViewById(R.id.mBtnNewFileInputDialogCancel);

        mBtnNewFileOk.setOnClickListener(this);
        mBtnNewFileCancel.setOnClickListener(this);
        mEtNewFileDialogName.addTextChangedListener(this);

        mBtnNewFileOk.setEnabled(false);

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

        switch (v.getId()) {
        case R.id.mBtnNewFileInputDialogOk:
            dismiss();
            File file = new File(mNewName);
            switch (mCreateType) {
            case CREATE_TYPE_FILE:
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                        mInfos.add(new SimpleFileInfo(mNewName,System.currentTimeMillis(), 0));
                        Collections.sort(mInfos, new FileComparator());
                        mFragment.gotoSelecttion(mNewName);
                        
                        Toast.makeText(getContext(), "创建文件" + FileUtils.getFileName(mNewName) + "成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = OpenFileUtil.openFile(mNewName);
                        getContext().startActivity(intent);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "创建文件" + FileUtils.getFileName(mNewName) + "失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;

            case CREATE_TYPE_FOLDER:
                if (!file.exists()) {
                    SimpleFileInfo info = new SimpleFileInfo(mNewName,FileType.TYPE_FOLDER);
                    info.setCreateTime(System.currentTimeMillis());
                    mInfos.add(info);
                    Collections.sort(mInfos, new FileComparator());
                    mFragment.gotoSelecttion(mNewName);
                    boolean flag = file.mkdirs();
                    if (flag) {
                        Toast.makeText(getContext(), "创建文件夹" + FileUtils.getFileName(mNewName) + "成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "创建文件夹" + FileUtils.getFileName(mNewName) + "失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
            }

            break;

        case R.id.mBtnNewFileInputDialogCancel:
            dismiss();
            break;

        default:
            break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        String name = s.toString().trim();
        if (TextUtil.isEmpty(name)) {
            mBtnNewFileOk.setEnabled(false);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String name = s.toString().trim();
        if (TextUtil.isEmpty(name)) {
            mBtnNewFileOk.setEnabled(false);
            return;
        }
        String path = mCurrentFolder + "/" + name;

        if (!FileUtils.isLegalPath(path) || FileUtils.contansPath(mInfos, path)) {
            mBtnNewFileOk.setEnabled(false);
        } else {
            mNewName = path;
            mBtnNewFileOk.setEnabled(true);
        }
    }

}
