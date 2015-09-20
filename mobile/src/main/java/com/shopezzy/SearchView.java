package com.shopezzy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

//import android.util.Log;

public class SearchView extends android.widget.SearchView {

  // static boolean allow = true;

  public SearchView(Context context) {
    super(context);
  }

  public SearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK
        && event.getAction() == KeyEvent.ACTION_UP) {
      // do your stuff

    }
    return super.onKeyPreIme(keyCode, event);

  }

}
