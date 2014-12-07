package com.cmcm.greendamexplorer.entity;

import android.view.View;

public class NaviInfo {
    private View view;
    private int position;

    public NaviInfo() {
    }

    public NaviInfo(View view, int position) {
        this.view = view;
        this.position = position;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}