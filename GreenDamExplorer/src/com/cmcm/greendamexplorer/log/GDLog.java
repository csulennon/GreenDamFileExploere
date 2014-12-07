package com.cmcm.greendamexplorer.log;

import android.util.Log;

public class GDLog {
    public static final boolean DEBUG = true;
    public static final String TAG_i = "--->info:";
    public static final String TAG_i1 = "---->info:";

    public static final String TAG_d = "--->debug:";
    public static final String TAG_d1 = "---->debug:";

    public static final String TAG_e = "--->err:";
    public static final String TAG_e1 = "---->err:";

    public static void i(String... args) {
        if (!DEBUG || args.length < 1) {
            return;
        }

        if (args.length == 1) {
            Log.i(TAG_i, args[0]);
            return;
        }

        for (int i = 1; i < args.length; i++) {
            Log.i(args[0], args[i]);
        }
    }

    public static void d(String... args) {
        if (!DEBUG || args.length < 1) {
            return;
        }

        if (args.length == 1) {
            Log.d(TAG_d, args[0]);
            return;
        }

        for (int i = 1; i < args.length; i++) {
            Log.d(args[0], args[i]);
        }
    }

    public static void e(String... args) {
        if (!DEBUG || args.length < 1) {
            return;
        }

        if (args.length == 1) {
            Log.e(TAG_e, args[0]);
            return;
        }

        for (int i = 1; i < args.length; i++) {
            Log.e(args[0], args[i]);
        }
    }
}
