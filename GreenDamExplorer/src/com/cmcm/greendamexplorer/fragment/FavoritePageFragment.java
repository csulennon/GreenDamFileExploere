package com.cmcm.greendamexplorer.fragment;

import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.adapter.FavoriteListViewAdapter;
import com.cmcm.greendamexplorer.adapter.FileListAdapter.OnCheckBoxChangedListener;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.Favorite;

public class FavoritePageFragment extends Fragment implements OnCheckBoxChangedListener, OnClickListener {
    private View mView = null;
    private View mFavoriteBottomDelete = null;
    private View mLayoutSelectAll = null;
    private ImageView mImageFavoriteSelectAll = null;
    private TextView mTvFavoriteSelectAll = null;
    private ImageButton mImgBtnBack = null;
    private View mNothingView = null;

    private ListView mListView = null;
    private FavoriteListViewAdapter mAdapter = null;
    private List<Favorite> mFavorites = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.page_favorite, null);

        mImageFavoriteSelectAll = (ImageView) mView.findViewById(R.id.mImageFavoriteSelectAll);
        mImageFavoriteSelectAll.setTag(false);
        mTvFavoriteSelectAll = (TextView) mView.findViewById(R.id.mTvFavoriteSelectAll);
        mListView = (ListView) mView.findViewById(R.id.mListViewFavorit);
        mNothingView = mView.findViewById(R.id.nothingFavorite);
        
        mFavoriteBottomDelete = mView.findViewById(R.id.mFavoriteBottomDelete);
        mFavoriteBottomDelete.setOnClickListener(this);
        mImgBtnBack =  (ImageButton) mView.findViewById(R.id.mImgBtnBack);
        mImgBtnBack.setOnClickListener(this);
        
        mLayoutSelectAll = mView.findViewById(R.id.mLayoutSelectAll);
        mLayoutSelectAll.setOnClickListener(this);

        mFavorites = ResourceManager.getAllFavorites();
        if(mFavorites.size() == 0) {
            mNothingView.setVisibility(View.VISIBLE);
        } else {
            mNothingView.setVisibility(View.GONE);
        }
        mAdapter = new FavoriteListViewAdapter(mView.getContext(), mFavorites, mListView, (MainActivity) getActivity());
        mAdapter.setOnCheckBoxChangedListener(this);
        mListView.setAdapter(mAdapter);

        return mView;
    }

    public void startListAnim() {
        mListView.startLayoutAnimation();
    }

    public void reLoadFavoriteList() {
        mFavorites = ResourceManager.getAllFavorites();
        if(mFavorites.size() == 0) {
            mNothingView.setVisibility(View.VISIBLE);
        } else {
            mNothingView.setVisibility(View.GONE);
        }
        Collections.reverse(mFavorites);
        
        mImageFavoriteSelectAll.setTag(false);
        mAdapter = new FavoriteListViewAdapter(mView.getContext(), mFavorites, mListView, (MainActivity) getActivity());
        mAdapter.setOnCheckBoxChangedListener(this);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setSelectImage();
    }

    @Override
    public void onCheckChanged(int position, boolean isChecked) {
        setSelectImage();
    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId()) {
        case R.id.mLayoutSelectAll:
            boolean isSelectAll = (Boolean) mImageFavoriteSelectAll.getTag();
            if(isSelectAll) {
                setSelectItems(false);
            }else {
                setSelectItems(true);
            }
            setSelectImage();
            mAdapter.notifyDataSetChanged();
            break;
        case R.id.mFavoriteBottomDelete:
            deletSelectedItem();
            break;
            
        case R.id.mImgBtnBack:
            ((MainActivity)getActivity()).goToPage(0);
            break;

        default:
            break;
        }
    }
    
    private void deletSelectedItem() {
        for (int i = 0; i < mFavorites.size(); i++) {
            if(mFavorites.get(i).isChecked()) {
                ResourceManager.removeItem(mFavorites.get(i).getCanonicalPath());
                mFavorites.remove(i);
                i--;
            }
        }
        mAdapter.notifyDataSetChanged();
        setSelectImage();
    }

    private void setSelectItems(boolean selecte) {
        for (Favorite favorite : mFavorites) {
            favorite.setChecked(selecte);
        }
    }

    public void setSelectImage() {
        if(isSelectAll()) {
            
            mImageFavoriteSelectAll.setBackgroundResource(R.drawable.op_select_nothing); 
            mTvFavoriteSelectAll.setText("取消");
            mImageFavoriteSelectAll.setTag(true);
        } else {
            mImageFavoriteSelectAll.setBackgroundResource(R.drawable.op_select_all); 
            mTvFavoriteSelectAll.setText("全选");
            mImageFavoriteSelectAll.setTag(false);
        }
        
        if(isSelectOne()) {
            mFavoriteBottomDelete.setVisibility(View.VISIBLE);
        } else {
            mFavoriteBottomDelete.setVisibility(View.GONE);
        }
    }

    private boolean isSelectOne() {
        for (Favorite favorite : mFavorites) {
            if (favorite.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelectAll() {
        for (Favorite favorite : mFavorites) {
            if (!favorite.isChecked()) {
                return false;
            }
        }
        return true;
    }

}
