package com.cmcm.greendamexplorer.utils;

import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    public static int[] getWindowSize() {
        WindowManager windowManager = (WindowManager) DeploymentOperation.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        int windowWidth = outMetrics.widthPixels;
        int windowHeight = outMetrics.heightPixels;
        
        int[] point = new int[] {windowWidth, windowHeight};
        
        return point;
    }

}
