package com.shopezzy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

//import android.util.Log;

public class SearchView extends android.widget.SearchView {

	// static boolean allow = true;

	public SearchView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// public SearchView(Context context, AttributeSet attrs, int defStyleAttr)
	// {
	// super(context, attrs, defStyleAttr);
	// // TODO Auto-generated constructor stub
	// }

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			// do your stuff

		}
//		 Log.i(getClass().getSimpleName(), "Event PreIme" + event + "-"
//		 + keyCode);
		return super.onKeyPreIme(keyCode, event);

	}

}
