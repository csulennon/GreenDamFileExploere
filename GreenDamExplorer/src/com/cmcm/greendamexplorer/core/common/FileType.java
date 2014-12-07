package com.cmcm.greendamexplorer.core.common;

import java.util.Locale;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;
import com.cmcm.greendamexplorer.utils.TextUtil;

public abstract class FileType {
    public static final int TYPE_FOLDER = -1;
    public static final int TYPE_UNKNOWN = 100;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_TXT = 2;
    public static final int TYPE_DOC = 3;
    public static final int TYPE_XLS = 4;
    public static final int TYPE_PPT = 5;
    public static final int TYPE_PDF = 6;
    public static final int TYPE_XML = 7;
    public static final int TYPE_CAJ = 8;
    public static final int TYPE_HTML = 9;
    public static final int TYPE_MP3 = 10;
    public static final int TYPE_RAR = 11;
    public static final int TYPE_ZIP = 12;
    public static final int TYPE_TAR = 13;
    public static final int TYPE_MP4 = 14;
    public static final int TYPE_3GP = 15;
    public static final int TYPE_AVI = 16;
    public static final int TYPE_VEDIO = 19;
    public static final int TYPE_RMVB = 20;
    public static final int TYPE_APK = 21;

    public static final int CATEGORY_UNKNOWN = 0; // 图片
    public static final int CATEGORY_PICTURE = 1; // 图片
    public static final int CATEGORY_DOCUMENT = 2; // 文档

    private static final int[] mTypeCategoryInfo = new int[] { 0, 0 };

    public static int getFileType(String canonicalPath) {

        if (TextUtil.isEmpty(canonicalPath)) {
            return TYPE_UNKNOWN;
        }

        int index = canonicalPath.lastIndexOf(".");
        if (index == -1) {
            return TYPE_UNKNOWN;
        }

        String extension = canonicalPath.substring(index).trim();

        int[] value = DeploymentOperation.mExtensionTypeMap.get(extension);
        if (value == null) {
            return TYPE_UNKNOWN;
        }

        return value[0];
    }

    /**
     * 根据扩展名获取类别
     * 
     * @param canonicalPath
     * @return
     */
    public static int getFileCategory(String canonicalPath) {

        if (TextUtil.isEmpty(canonicalPath)) {
            return CATEGORY_UNKNOWN;
        }

        int index = canonicalPath.lastIndexOf(".");
        if (index == -1) {
            return CATEGORY_UNKNOWN;
        }

        String extension = canonicalPath.substring(index).trim();
        int[] value = DeploymentOperation.mExtensionTypeMap.get(extension);
        if (value == null) {
            return CATEGORY_UNKNOWN;
        }

        return value[1];
    }

    /**
     * 返回扩展名所能包含的全部信息
     * 
     * @param canonicalPath
     * @return
     */
    public static int[] getFileTypeAndCategory(String canonicalPath) {

        if (TextUtil.isEmpty(canonicalPath)) {
            return mTypeCategoryInfo;
        }

        int index = canonicalPath.lastIndexOf(".");
        if (index == -1) {
            return mTypeCategoryInfo;
        }

        String extension = canonicalPath.substring(index).trim().toLowerCase(Locale.CHINA);
        int[] value = DeploymentOperation.mExtensionTypeMap.get(extension);
        if (value == null) {
            return mTypeCategoryInfo;
        }
        return value;
    }

    public static int getResourceIdByType(int type) {
        int resourceId = 0;

        // public static final int TYPE_CAJ = 8;
        // public static final int TYPE_VEDIO = 19;
        // public static final int TYPE_RMVB = 20;
        // public static final int TYPE_CAJ = 8;
        // public static final int TYPE_3GP = 15;

        switch (type) {
        case FileType.TYPE_UNKNOWN:
            resourceId = R.drawable.type_unknow;
            break;
        case FileType.TYPE_PICTURE:
            resourceId = R.drawable.type_picture;
            break;
        case FileType.TYPE_FOLDER:
            resourceId = R.drawable.type_folder;
            break;
        case FileType.TYPE_DOC:
            resourceId = R.drawable.type_word;
            break;
        case FileType.TYPE_XLS:
            resourceId = R.drawable.type_excel;
            break;
        case FileType.TYPE_PPT:
            resourceId = R.drawable.type_ppt;
            break;
        case FileType.TYPE_PDF:
            resourceId = R.drawable.type_pdf;
            break;
        case FileType.TYPE_XML:
            resourceId = R.drawable.type_xml;
            break;
        case FileType.TYPE_HTML:
            resourceId = R.drawable.type_html;
            break;
        case FileType.TYPE_ZIP:
            resourceId = R.drawable.type_zip;
            break;
        case FileType.TYPE_MP3:
            resourceId = R.drawable.type_mp3;
            break;
        case FileType.TYPE_MP4:
            resourceId = R.drawable.type_mp4;
            break;
        case FileType.TYPE_AVI:
            resourceId = R.drawable.type_mp4;
            break;
        case FileType.TYPE_RAR:
            resourceId = R.drawable.type_rar;
            break;
        case FileType.TYPE_TXT:
            resourceId = R.drawable.type_txt;
            break;
        case FileType.TYPE_TAR:
            resourceId = R.drawable.type_archive;
            break;
        case FileType.TYPE_APK:
            resourceId = R.drawable.type_apk;
            break;
        default:
            resourceId = R.drawable.type_unknow;
            break;
        }
        return resourceId;
    }

    public static boolean isZip(String path) {
        path = path.toLowerCase();
        boolean isZip = false;
        if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return true;
        }

        return isZip;
    }

    public static boolean isDocument(String path) {
        path = path.toLowerCase();
        boolean flag = false;
        if (path.endsWith(".txt") || path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx") || path.endsWith(".xml") || path.endsWith(".html") || path.endsWith(".htm")) {
            return true;
        }

        return flag;
    }

    public static boolean isApk(String path) {
        path = path.toLowerCase();
        boolean flag = false;
        if (path.endsWith(".apk")) {
            return true;
        }
        return flag;
    }

    public static int getResuorceIdByPath(String path) {

        int type = getFileType(path.toLowerCase());
        return getResourceIdByType(type);

    }

}
