package com.cmcm.greendamexplorer.view;

import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.adapter.FileListAdapter;
import com.cmcm.greendamexplorer.core.common.FileComparator;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.utils.SharedPreferenceUtil;

public class SortDialog extends Dialog implements OnClickListener {

    private View mView = null;
    private Button mBtnSortNameUp = null;
    private Button mBtnSortNameDown = null;
    private Button mBtnSortSizeUp = null;
    private Button mBtnSortSizeDown = null;
    private Button mBtnSortDateUp = null;
    private Button mBtnSortDateDown = null;

    private FileListAdapter mAdapter = null;

    private List<SimpleFileInfo> mFileINfos = null;

    //
    public SortDialog(Context context, List<SimpleFileInfo> infos, FileListAdapter mAdapter) {
        super(context);
        this.mFileINfos = infos;
        this.mAdapter = mAdapter;
        init();
    }

    public SortDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public SortDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public SortDialog(Context context) {
        super(context);
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sort, null);

        mBtnSortNameUp = (Button) mView.findViewById(R.id.mBtnSortNameUp);
        mBtnSortNameDown = (Button) mView.findViewById(R.id.mBtnSortNameDown);
        mBtnSortSizeUp = (Button) mView.findViewById(R.id.mBtnSortSizeUp);
        mBtnSortSizeDown = (Button) mView.findViewById(R.id.mBtnSortSizeDown);
        mBtnSortDateUp = (Button) mView.findViewById(R.id.mBtnSortDateUp);
        mBtnSortDateDown = (Button) mView.findViewById(R.id.mBtnSortDateDown);

        mBtnSortNameUp.setOnClickListener(this);
        mBtnSortNameDown.setOnClickListener(this);
        mBtnSortSizeUp.setOnClickListener(this);
        mBtnSortSizeDown.setOnClickListener(this);
        mBtnSortDateUp.setOnClickListener(this);
        mBtnSortDateDown.setOnClickListener(this);

        this.setTitle("排序方式");
        this.setContentView(mView);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnSortNameUp:
            dismiss();
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_NAME_UP);
            break;

        case R.id.mBtnSortNameDown:
            dismiss();
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_NAME_DOWN);
            break;

        case R.id.mBtnSortSizeUp:
            dismiss();
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_SIZE_UP);
            break;
        case R.id.mBtnSortSizeDown:
            dismiss();
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_SIZE_DOWN);
            break;
        case R.id.mBtnSortDateUp:
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_TIME_UP);
            dismiss();
            break;
        case R.id.mBtnSortDateDown:
            SharedPreferenceUtil.setSortType(FileComparator.SORT_TYPE_BY_TIME_DOWN);
            dismiss();
            break;

        default:
            break;
        }

        Collections.sort(mFileINfos, new FileComparator());
        mAdapter.notifyDataSetChanged();

    }

}
