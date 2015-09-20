package com.shopezzy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {

  public TextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.MyComponent, 0, 0);
    String fontType = a.getString(R.styleable.MyComponent_font);
    init(fontType);
  }

  public TextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.MyComponent, 0, 0);
    String fontType = a.getString(R.styleable.MyComponent_font);
    init(fontType);
  }

  public TextView(Context context) {
    super(context);

  }

  private void init(String fontTpe) {
    if (!isInEditMode()) {

      Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
          fontTpe);
      setTypeface(tf);
    }
  }

}
