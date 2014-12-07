package com.cmcm.greendamexplorer.core.common;

import java.util.Comparator;
import java.util.Locale;

import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.utils.SharedPreferenceUtil;

public class FileComparator implements Comparator<SimpleFileInfo> {
    public static final int SORT_TYPE_BY_NAME_UP = 0;
    public static final int SORT_TYPE_BY_NAME_DOWN = 1;
    public static final int SORT_TYPE_BY_TIME_UP = 2;
    public static final int SORT_TYPE_BY_TIME_DOWN = 3;
    public static final int SORT_TYPE_BY_SIZE_UP = 4;
    public static final int SORT_TYPE_BY_SIZE_DOWN = 5;

    private int mSortType = SORT_TYPE_BY_NAME_UP;

    public FileComparator() {
        super();
        mSortType = SharedPreferenceUtil.getSortType();
    }

    @Override
    public int compare(SimpleFileInfo left, SimpleFileInfo right) {

        switch (mSortType) {
        case SORT_TYPE_BY_NAME_UP:// 名称
            return sortByNameUp(left, right);
        case SORT_TYPE_BY_NAME_DOWN:
            return sortByNameDown(left, right);
        case SORT_TYPE_BY_TIME_UP:
            return sortByTimeUp(left, right);
        case SORT_TYPE_BY_TIME_DOWN:
            return sortByTimeDown(left, right);
        case SORT_TYPE_BY_SIZE_UP:
            return sortBySizeUp(left, right);
        case SORT_TYPE_BY_SIZE_DOWN:
            return sortBySizeDown(left, right);
        default:
            break;
        }
        return 0;
    }

    private int sortByTimeDown(SimpleFileInfo left, SimpleFileInfo right) {
        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                long diff = left.getCreateTime() - right.getCreateTime();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                long diff = left.getCreateTime() - right.getCreateTime();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    private int sortBySizeDown(SimpleFileInfo left, SimpleFileInfo right) {
        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                long diff = left.getFileSize() - right.getFileSize();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                long diff = left.getFileSize() - right.getFileSize();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    private int sortByTimeUp(SimpleFileInfo left, SimpleFileInfo right) {
        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                long diff = left.getCreateTime() - right.getCreateTime();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                long diff = left.getCreateTime() - right.getCreateTime();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    private int sortBySizeUp(SimpleFileInfo left, SimpleFileInfo right) {
        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                long diff = left.getFileSize() - right.getFileSize();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                long diff = left.getFileSize() - right.getFileSize();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    private int sortByNameDown(SimpleFileInfo left, SimpleFileInfo right) {
        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return -1 * left.getName().toLowerCase(Locale.CHINA).compareTo(right.getName().toLowerCase(Locale.CHINA));
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                return -1 * left.getName().toLowerCase(Locale.CHINA).compareTo(right.getName().toLowerCase(Locale.CHINA));
            }
        }

    }

    private int sortByNameUp(SimpleFileInfo left, SimpleFileInfo right) {

        if (left.getFileType() == FileType.TYPE_FOLDER) {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return left.getName().toLowerCase(Locale.CHINA).compareTo(right.getName().toLowerCase(Locale.CHINA));
            } else {
                return -1;
            }
        } else {
            if (right.getFileType() == FileType.TYPE_FOLDER) {
                return 1;
            } else {
                return left.getName().toLowerCase(Locale.CHINA).compareTo(right.getName().toLowerCase(Locale.CHINA));
            }
        }

    }
}
