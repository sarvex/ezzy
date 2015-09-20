package com.shopezzy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahoy.analytics.AHTracker;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;

//import android.util.Log;

public class IntroScreen extends AppCompatActivity {

  private ViewPager pager;
  private CirclePageIndicator mIndicator;
  private TextView textIntro1, textIntro2, textskip;
  private Toolbar toolbar;

  private String[] text1A = {"Pick Nearby ", "Search & Add to ", "Place ", "Enjoy Delivery in 45 "};
  private String[] text2A = {"Store", "Cart", "Order", "min"};

  // private String fromWhere;

  @Override
  protected void onStart() {
    super.onStart();
    AHTracker.getInstance().setModule("shopezzy").setApiKey(Constants.analytics_key)
        .setPageName(getClass().getSimpleName()).startSession(IntroScreen.this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    AHTracker.getInstance().stopSession(this);
  }

  @Override
  protected void onCreate(Bundle arg0) {
    super.onCreate(arg0);

    toolbar = getSupportActionBar();
    toolbar.hide();

    setContentView(R.layout.ushop_introscreen);

    pager = (ViewPager) findViewById(R.id.pager);
    mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
    textIntro1 = (TextView) findViewById(R.id.text1);
    textIntro2 = (TextView) findViewById(R.id.text2);
    textskip = (TextView) findViewById(R.id.text3);
    textskip.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(IntroScreen.this);
        pref.edit().putBoolean(Constants.pref_gotit, true).commit();

        Intent localIntent = new Intent(IntroScreen.this, RegisterYourSelf.class);
        IntroScreen.this.startActivity(localIntent);
        IntroScreen.this.finish();
      }
    });

    ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(
        new ArrayList<String>(Arrays.asList(new String[]{"intro_1", "intro_2", "intro_3", "intro_4"})));
    this.pager.setAdapter(pagerAdapter);
    this.mIndicator.setViewPager(pager);
    this.initTextAtPosition(0);
    this.mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int arg0) {
        initTextAtPosition(arg0);
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
      }

      @Override
      public void onPageScrollStateChanged(int arg0) {

      }
    });
  }

  private void initTextAtPosition(int var1) {
    String text1 = text1A[var1];
    String text2 = text2A[var1];

    if (text1 == null) {
    }

    if (this.textIntro1 == null) {
    }
    this.textIntro1.setText(text1);

    this.textIntro2.setText(text2);
    if (var1 == 3) {
      this.textskip.setVisibility(View.VISIBLE);
      this.textskip.setText("Start Shopping");
      this.textskip.setTextSize(19);
    } else {
      this.textskip.setVisibility(View.INVISIBLE);
      this.textskip.setText("Got it, Let's go");
      this.textskip.setTextSize(15);
    }

  }

  // Pager Adapter for displaying into pages.
  class ViewPagerAdapter extends PagerAdapter {

    ArrayList<String> mImagesArray;

    public ViewPagerAdapter(ArrayList<String> mImagesArray) {

      this.mImagesArray = mImagesArray;
    }

    @Override
    public Object instantiateItem(View container, int position) {
      if (position >= this.mImagesArray.size()) {
        return null;
      } else {

        LayoutInflater inflater = (LayoutInflater) container.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View intro = inflater.inflate(R.layout.pagerimage, null);
        ImageView intoImage = (ImageView) intro.findViewById(R.id.intro);
        Resources introRes = IntroScreen.this.getResources();
        intoImage.setImageDrawable(introRes.getDrawable(introRes.getIdentifier(
            (String) this.mImagesArray.get(position), "drawable", IntroScreen.this.getPackageName())));
        ((ViewPager) container).addView(intro, 0);
        return intro;
      }
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
      ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount() {
      return this.mImagesArray.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
      return arg0.equals(arg1);
    }

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
