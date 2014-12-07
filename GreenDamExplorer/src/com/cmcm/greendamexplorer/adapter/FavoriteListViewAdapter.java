package com.cmcm.greendamexplorer.adapter;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.adapter.FileListAdapter.OnCheckBoxChangedListener;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.fragment.FileListPageFragment;
import com.cmcm.greendamexplorer.fragment.ViewPageFragment;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class FavoriteListViewAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<Favorite> mFavorites = null;
    private ListView mListView = null;
    private OnCheckBoxChangedListener mOnCheckBoxChangedListener = null;
    private FileListPageFragment mFileListPageFragment = null;
    private ViewPageFragment mViewPageFragment = null;

    public FavoriteListViewAdapter(Context mContext, List<Favorite> mFavorites, ListView listView, MainActivity activity) {
        super();
        this.mContext = mContext;
        this.mFavorites = mFavorites;
        mListView = listView;
        mViewPageFragment = activity.getViewPageFragment();
        mFileListPageFragment = mViewPageFragment.getFileListPageFragment();
    }

    public void setOnCheckBoxChangedListener(OnCheckBoxChangedListener listener) {
        this.mOnCheckBoxChangedListener = listener;
    }

    @Override
    public int getCount() {
        return mFavorites.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.favorite_list_item, null);
            holder = new ViewHolder();
            findViews(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        setHolder(holder, position);
        final int p = position;
        holder.mCbFavorite.setOnClickListener(new SimpleClickListener(position));
        holder.mImageFavoriteBorder.setOnClickListener(new OnClickListener() {
            Favorite favorite = mFavorites.get(p);

            @Override
            public void onClick(View v) {

                File file = new File(favorite.getCanonicalPath());

                if (hasCheckedItem(mFavorites)) {

                    Favorite favorite = mFavorites.get(p);
                    boolean isChecked = false;
                    if (favorite.isChecked()) {
                        isChecked = false;
                    } else {
                        isChecked = true;
                    }
                    favorite.setChecked(isChecked);
                    notifyDataSetChanged();

                    if (mListView != null) {
                        int firstPosition = mListView.getFirstVisiblePosition();
                        int lastPosition = mListView.getLastVisiblePosition();
                        int moveTo = firstPosition;
                        if (firstPosition == p) {
                            moveTo = moveTo - 1 ;
                        } else if (lastPosition == p) {
                            moveTo = moveTo + 1 ;
                        }
                        mListView.setSelection(moveTo);
                    }

                    if (mOnCheckBoxChangedListener != null) {
                        mOnCheckBoxChangedListener.onCheckChanged(p, isChecked);
                    }
                    return;

                } else {
                    if (file.exists()) {
                        mViewPageFragment.setPage(0);
                        mFileListPageFragment.autoDeploymentTopNavisStack(favorite.getCanonicalPath());
                    } else {
                        Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("提醒").setMessage("很遗憾，您收藏文件从宇宙消失了，你可以删除这条记录！");
                        dialog.setNegativeButton("暂不删除", null);
                        dialog.setPositiveButton("直接删除", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FavoriteDao dao = DaoFactory.getFavoriteDao(mContext);
                                dao.deleteFavorite(favorite.getCanonicalPath());
                                mFavorites.remove(p);
                                notifyDataSetChanged();
                            }
                        });
                        dialog.show();
                    }
                }
            }

        });

        return view;
    }

    private void setHolder(ViewHolder holder, int position) {
        Favorite favorite = mFavorites.get(position);

        holder.mTvFavoriteName.setText(favorite.getName());
        holder.mTvFavoritePath.setText(favorite.getCanonicalPath());
        holder.mTvFavoriteTime.setText("收藏于:" + TextUtil.getDateStringString(favorite.getFavoriteTime()));
        if (favorite.getFileType() == FileType.TYPE_FOLDER) {
            holder.mImageFavorite.setBackgroundResource(R.drawable.favorite_icon_red);
            holder.mTvFavoriteSize.setText("（" + favorite.getSize() + "个）");
        } else {
            holder.mImageFavorite.setBackgroundResource(R.drawable.favorite_icon_green);
            holder.mTvFavoriteSize.setText("（" + TextUtil.getSizeSting(favorite.getSize()) + "）");
        }

        holder.mCbFavorite.setChecked(favorite.isChecked());

    }

    /**
     * 判断是否有选中项
     * 
     * @param favorites
     * @return
     */
    private static boolean hasCheckedItem(List<Favorite> favorites) {
        for (Favorite favorite : favorites) {
            if (favorite.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void findViews(ViewHolder holder, View view) {
        holder.mImageFavorite = (ImageView) view.findViewById(R.id.mImageFavorite);
        holder.mImageFavoriteBorder = (ImageView) view.findViewById(R.id.mImageFaviriteBorder);
        holder.mTvFavoriteName = (TextView) view.findViewById(R.id.mTvFavoriteName);
        holder.mTvFavoritePath = (TextView) view.findViewById(R.id.mTvFavoritePath);
        holder.mTvFavoriteTime = (TextView) view.findViewById(R.id.mTvFavoriteTime);
        holder.mTvFavoriteSize = (TextView) view.findViewById(R.id.mTvFavoriteSize);
        holder.mCbFavorite = (CheckBox) view.findViewById(R.id.mCbFavorite);
    }

    class ViewHolder {
        private ImageView mImageFavorite;
        private ImageView mImageFavoriteBorder;
        private TextView mTvFavoriteName;
        private TextView mTvFavoritePath;
        private TextView mTvFavoriteTime;
        private TextView mTvFavoriteSize;
        private CheckBox mCbFavorite;
    }

    class SimpleClickListener implements OnClickListener {

        private int position = 0;

        public SimpleClickListener(int position) {
            super();
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            Favorite favorite = mFavorites.get(position);
            boolean isChecked = false;
            if (favorite.isChecked()) {
                isChecked = false;
            } else {
                isChecked = true;
            }
            favorite.setChecked(isChecked);

            if (mListView != null) {
                int firstPosition = mListView.getFirstVisiblePosition();
                int lastPosition = mListView.getLastVisiblePosition();
                int moveTo = firstPosition;
                if (firstPosition == position) {
                    moveTo = moveTo - 1;
                } else if (lastPosition == position) {
                    moveTo = moveTo + 1;
                }
                mListView.setSelection(moveTo);
            }

            if (mOnCheckBoxChangedListener != null) {
                mOnCheckBoxChangedListener.onCheckChanged(position, isChecked);
            }
        }

    }

}
