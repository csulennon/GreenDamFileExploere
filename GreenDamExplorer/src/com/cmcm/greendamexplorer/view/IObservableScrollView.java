package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class IObservableScrollView extends ScrollView {
	private IOnScrollChangedListener onScrollChangedListener;

	public IObservableScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public IObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IObservableScrollView(Context context) {
		super(context);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(this.onScrollChangedListener != null) {
			onScrollChangedListener.onScrollChanged(t, oldt);
		}
	}

	public void setOnScrollChangedListener(IOnScrollChangedListener onScrollChangedListener) {
		this.onScrollChangedListener = onScrollChangedListener;
	}

}
