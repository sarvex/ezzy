package com.shopezzy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ahoy.analytics.AHTracker;

public class PendingItems extends AppCompatActivity {

  private Button yes, no;
  private String count;
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
    setContentView(R.layout.noitems);

    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    if (Build.VERSION.SDK_INT >= 14) {

      this.setFinishOnTouchOutside(false);
    } else {
    }

    yes = (Button) findViewById(R.id.yes);

    no = (Button) findViewById(R.id.no);

    yes.setTypeface(tff);
    no.setTypeface(tff);
    nameList = (TextView) findViewById(R.id.namelist);

    yes.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent nextIntent = PendingItems.this.getIntent();
        nextIntent.putExtra("result", "yes");
        setResult(3, nextIntent);
        finish();
      }
    });

    no.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent nextIntent = PendingItems.this.getIntent();
        nextIntent.putExtra("result", "no");
        setResult(2, nextIntent);
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
