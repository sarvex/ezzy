package com.shopezzy;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ahoy.analytics.AHTracker;

public class PushMessage extends AppCompatActivity {

  private Button ok;
  private TextView nameList;
  Typeface tff;

  @Override
  public void onBackPressed() {
  }

  @Override
  protected void onStart() {
    super.onStart();
    AHTracker.getInstance().setModule("shopezzy").setApiKey(Constants.analytics_key)
        .setPageName(getClass().getSimpleName()).startSession(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    AHTracker.getInstance().stopSession(this);
  }

  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pushmessage);
    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    if (Build.VERSION.SDK_INT >= 14) {

      this.setFinishOnTouchOutside(false);
    } else {
    }

    ok = (Button) findViewById(R.id.no);
    ok.setTypeface(tff);
    nameList = (TextView) findViewById(R.id.namelist);

    if (getIntent().hasExtra("message")) {
      String message = getIntent().getStringExtra("message");
      if (message.length() > 0) {
        nameList.setText(message);
      }
    }

    ok.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });

  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

}
