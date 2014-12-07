package com.cmcm.greendamexplorer.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.AboutMeActivity;
import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.activity.WelcomActivity;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.utils.SharedPreferenceUtil;
import com.cmcm.greendamexplorer.view.flatui.views.FlatToggleButton;

public class LeftMenuFragment extends Fragment implements OnClickListener{
    
    private View mView = null;
    private View mLeftMenuGoExternalStorage = null;
    private View mLeftMenuGoRootDir = null;
    private View mLeftMenuGoCategoryPage = null;
    private View mLeftMenuGoFavoritePage = null;
    private FlatToggleButton mToggleBtnShowHideFile = null;
    private ImageView mLeftMenuExit = null;
    private ImageView mLeftMenuAboutme = null;
    private ImageView mLeftMenuRefresh = null;
    private ImageView mImageHead = null;
    private TextView mTvLeftMenuUserName = null;
    private ImageButton mImgBtnHideLeftMenu = null;
    
    private MainActivity mActivity = null;
    private FileListPageFragment mFileListPageFragment = null;
    

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.left_menu_item, null);
        mActivity = (MainActivity) getActivity();
        mFileListPageFragment = mActivity.getViewPageFragment().getFileListPageFragment();
        findViews();
        setViews();
        
        return mView;
    }

    private void setViews() {
        mTvLeftMenuUserName.setText(android.os.Build.BRAND);
        
        mLeftMenuGoExternalStorage.setOnClickListener(this);
        mLeftMenuGoRootDir.setOnClickListener(this);
        mLeftMenuGoCategoryPage.setOnClickListener(this);
        mLeftMenuGoFavoritePage.setOnClickListener(this);
        
        mLeftMenuExit.setOnClickListener(this);
        mLeftMenuAboutme.setOnClickListener(this);
        mLeftMenuRefresh.setOnClickListener(this);
        mImageHead.setOnClickListener(this);
        
        mImgBtnHideLeftMenu.setOnClickListener(this);
        
        
        boolean isShowHideFiles = SharedPreferenceUtil.getShowHideFiles();
        mToggleBtnShowHideFile.setChecked(isShowHideFiles);
        
        mToggleBtnShowHideFile.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferenceUtil.setShowHideFiles(isChecked);
                if(mFileListPageFragment == null) {
                    mFileListPageFragment = mActivity.getViewPageFragment().getFileListPageFragment();
                }
                mActivity.goToPage(0);
                mActivity.hideLeft();
                mFileListPageFragment.refresh();
            }
        });
    }

    private void findViews() {
        
        mLeftMenuGoExternalStorage = mView.findViewById(R.id.mLeftMenuGoExternalStorage);
        mLeftMenuGoRootDir = mView.findViewById(R.id.mLeftMenuGoRootDir);
        mLeftMenuGoCategoryPage = mView.findViewById(R.id.mLeftMenuGoCategoryPage);
        mLeftMenuGoFavoritePage = mView.findViewById(R.id.mLeftMenuGoFavoritePage);
        mToggleBtnShowHideFile = (FlatToggleButton) mView.findViewById(R.id.mToggleBtnShowHideFile);
        
        mLeftMenuExit = (ImageView) mView.findViewById(R.id.mLeftMenuExit);
        mLeftMenuAboutme = (ImageView) mView.findViewById(R.id.mLeftMenuAboutme);
        mLeftMenuRefresh = (ImageView) mView.findViewById(R.id.mLeftMenuRefresh);
        mImageHead = (ImageView) mView.findViewById(R.id.head_view);
        
        mTvLeftMenuUserName = (TextView) mView.findViewById(R.id.mTvLeftMenuUserName);
        mImgBtnHideLeftMenu = (ImageButton) mView.findViewById(R.id.mImgBtnHideLeftMenu);
        
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        mFileListPageFragment = mActivity.getViewPageFragment().getFileListPageFragment();
        switch (v.getId()) {
        case R.id.mLeftMenuGoExternalStorage:
            mActivity.hideLeft();
            mActivity.goToPage(0);
            mFileListPageFragment.autoDeploymentTopNavisStack(ResourceManager.mExternalStoragePath);
            break;
        case R.id.mLeftMenuGoRootDir:
            mActivity.hideLeft();
            mActivity.goToPage(0);
            mFileListPageFragment.autoDeploymentTopNavisStack("/");
            break;
        case R.id.mLeftMenuGoCategoryPage:
            mActivity.hideLeft();
            mActivity.goToPage(1);
            break;
        case R.id.mLeftMenuGoFavoritePage:
            mActivity.hideLeft();
            mActivity.goToPage(2);
            break;
        case R.id.mLeftMenuExit:
            Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setTitle("是否退出？");
            dialog.setNegativeButton("不退出", null);
            dialog.setPositiveButton("退出", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.finish();
                }
            });
            dialog.show();
            break;
        case R.id.mLeftMenuAboutme:
             intent = new Intent(getActivity(), AboutMeActivity.class);
            startActivity(intent);
            break;
        case R.id.mLeftMenuRefresh:
            mActivity.hideLeft();
            mActivity.goToPage(1);
            mActivity.getViewPageFragment().getFileCategoryPageFragment().reScan();
            break;
            
        case R.id.mImgBtnHideLeftMenu:
            mActivity.hideLeft();
            break;
            
        case R.id.head_view:
            intent = new Intent(mActivity, WelcomActivity.class);
            startActivity(intent);
            mActivity.hideLeft();
            break;

        default:
            break;
        }
    }
    
}
