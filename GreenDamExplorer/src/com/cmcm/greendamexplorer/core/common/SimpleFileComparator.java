package com.cmcm.greendamexplorer.core.common;

import android.annotation.SuppressLint;
import java.util.Comparator;

public class SimpleFileComparator implements Comparator<String>{

    @SuppressLint("DefaultLocale")
    @Override
    public int compare(String left, String right) {
        
//        int indexL = left.lastIndexOf(".");
//        int indexR = left.lastIndexOf(".");
//
//        if(indexL == -1 && indexR != -1) {
//            return -1;
//        }
        return left.toLowerCase().compareTo(right.toLowerCase());
    }

}
