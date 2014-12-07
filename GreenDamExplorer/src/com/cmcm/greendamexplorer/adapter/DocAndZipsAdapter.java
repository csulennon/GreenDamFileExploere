package com.cmcm.greendamexplorer.adapter;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.utils.TextUtil;

@SuppressLint("DefaultLocale")
public class DocAndZipsAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<String> mFiles = null;
    private PackageManager mPackageManager = null;

    public DocAndZipsAdapter(Context mContext, List<String> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
        mPackageManager = mContext.getPackageManager();
    }

    @Override
    public int getCount() {
        return mFiles.size();
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

        ViewHolder holder = null;
        View view = convertView;
        if (view == null) {

            view = LayoutInflater.from(mContext).inflate(R.layout.document_and_zips_list_item, null);

            holder = new ViewHolder();
            findHolderViews(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setHolder(holder, position);

        return view;
    }

    private void setHolder(ViewHolder holder, int position) {

        String path = mFiles.get(position);
        File file = new File(path);
        if (!file.exists()) {
            mFiles.remove(position);
            this.notifyDataSetChanged();
        }

        if (file.isDirectory()) {
            holder.mImageDoc.setBackgroundResource(R.drawable.type_folder);
        } else {
            int type = FileType.getFileType(path);
            if (type == FileType.TYPE_APK) {
                PackageInfo pkgInfo = mPackageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                Drawable drawable = null;
                try {
                    if (pkgInfo != null) {
                        drawable = mPackageManager.getApplicationIcon(pkgInfo.packageName);
                    }

                    if (drawable != null) {
                        holder.mImageDoc.setImageDrawable(drawable);
                    } else {
                        holder.mImageDoc.setImageResource(R.drawable.type_apk);
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {

                int resid = FileType.getResourceIdByType(type);
                holder.mImageDoc.setBackgroundResource(resid);
            }

        }

        holder.mTvDocName.setText(file.getName());
        holder.mTvDocPath.setText(file.getAbsolutePath());
        holder.mTvDocModifyTime.setText(TextUtil.getDateStringString(file.lastModified()));
        holder.mTvDocSize.setText(TextUtil.getSizeSting(file.length()));

    }

    private void findHolderViews(ViewHolder holder, View view) {
        holder.mImageDoc = (ImageView) view.findViewById(R.id.mImageDoc);
        holder.mTvDocName = (TextView) view.findViewById(R.id.mTvDocName);
        holder.mTvDocPath = (TextView) view.findViewById(R.id.mTvDocPath);
        holder.mTvDocModifyTime = (TextView) view.findViewById(R.id.mTvDocModifyTime);
        holder.mTvDocSize = (TextView) view.findViewById(R.id.mTvDocSize);
    }

    class ViewHolder {

        private ImageView mImageDoc;
        private TextView mTvDocName;
        private TextView mTvDocPath;
        private TextView mTvDocModifyTime;
        private TextView mTvDocSize;

    }

}
