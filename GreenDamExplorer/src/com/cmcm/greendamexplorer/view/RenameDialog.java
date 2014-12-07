package com.cmcm.greendamexplorer.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class RenameDialog extends Dialog implements TextWatcher, OnClickListener{
    private View mView = null;
    private EditText mEtOrignalName = null;
    private EditText mEtNewName = null;
    private Button mBtnOk = null;
    private Button mBtnCancel = null;

    private String mOrignalPath = "";
    private String mNewName = "";
    private String mParentPath = "/";
    private List<SimpleFileInfo> mInfos = null;
    private IOnDialogBtnClickListener mOnDialogBtnClickListener = null;

    public RenameDialog(Context context, List<SimpleFileInfo> infos, String orignalPath) {
        super(context);
        this.mInfos = infos;
        mOrignalPath = orignalPath;
        init();
    }
    
    public void setOnDialogBtnClickListener(IOnDialogBtnClickListener listener) {
        this.mOnDialogBtnClickListener = listener;
    }

    public RenameDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public RenameDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_name_input, null);
        mView = view.findViewById(R.id.rename_dialog_root);
        this.setContentView(mView);
        mEtNewName = (EditText) mView.findViewById(R.id.mEtNewName);
        mEtOrignalName = (EditText) mView.findViewById(R.id.mEtOriginalName);
        mBtnCancel = (Button) mView.findViewById(R.id.mBtnDelDialogCancel);
        mBtnOk = (Button) mView.findViewById(R.id.mBtnDelDialogOk);
        
        mEtOrignalName.setEnabled(false);
        mEtOrignalName.setText(FileUtils.getFileName(mOrignalPath));
        mEtNewName.setText(FileUtils.getFileName(mOrignalPath));
        mEtNewName.setSelectAllOnFocus(true);
        mParentPath = FileUtils.getParent(mOrignalPath);
        
        mEtNewName.addTextChangedListener(this);
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        this.setTitle("重命名文件");
        this.setCanceledOnTouchOutside(false);
        
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        String name = s.toString().trim();
        if (TextUtil.isEmpty(name)) {
            mBtnOk.setEnabled(false);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        
        String name = s.toString().trim();
        if (TextUtil.isEmpty(name)) {
            mBtnOk.setEnabled(false);
            return ;
        }
         String path = mParentPath + "/" + name;

        if (!FileUtils.isLegalPath(path) || FileUtils.contansPath(mInfos, path)) {
            mBtnOk.setEnabled(false);
        } else {
            mNewName = path;
            mBtnOk.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnDelDialogCancel:
            if(mOnDialogBtnClickListener != null) {
                mOnDialogBtnClickListener.onCancelClick(mBtnCancel);
            }
//            Toast.makeText(getContext(), "取消", 0).show();
            this.dismiss();
            break;
            
        case R.id.mBtnDelDialogOk:
            if(mOnDialogBtnClickListener != null) {
                mOnDialogBtnClickListener.onOkClick(mBtnOk, mNewName);
            }
//            Toast.makeText(getContext(), "确定", 0).show();
            this.dismiss();
        default:
            break;
        }
    }

}
