package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SimpleFileListView extends ListView{

    public SimpleFileListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    

    public SimpleFileListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleFileListView(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        
    }

}
