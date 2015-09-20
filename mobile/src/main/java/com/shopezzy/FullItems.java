package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shopezzy.SearchItems.ItemsQuery;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FullItems extends AppCompatActivity implements NotifyCallBack {

  private Toolbar toolbar;
  private ImageView cart;
  private Animation moveCart, moveCart1, moveCart2;
  private android.widget.TextView text, title;
  LinearLayout linearViewN = null;
  private ProgressDialog proD;
  Typeface tff;
  private Gson gson;
  private ListView mListView;
  private TabPageIndicator mIndicator;
  private TabPagerAdapter tabPagerAdapter;
  ViewPager mViewPager;
  private FragmentManager mFragmentManager;
  String storeId;
  String storeName;
  String storeAddress;
  String minOrder;
  String catId;
  String catDesc;
  String response;
  private String[] catArray, catArrayId;
  String items;
  String updatedResponse;
  private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
  private Button addmore;
  private ImageView search;

  AnimationListener listener = new AnimationListener() {

    @Override
    public void onAnimationStart(Animation animation) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
      // TODO Auto-generated method stub
      linearViewN.startAnimation(moveCart1);

    }
  };

  AnimationListener listener1 = new AnimationListener() {

    @Override
    public void onAnimationStart(Animation animation) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
      // TODO Auto-generated method stub

    }
  };

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:

        if (items != null) {

          // Log.i(getClass().getSimpleName(), "OnBackPressed");
          Intent nextIntent = getIntent();
          nextIntent.putExtra("carttext", this.text.getText().toString());
          nextIntent.putExtra("items", items);
          nextIntent.putExtra("updatedresponse", updatedResponse);
          setResult(4, nextIntent);
          finish();

        } else {
          finish();
        }

        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.displayitems);
    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    CategoriesFragment.notify = this;
    gson = new Gson();
    toolbar = getSupportActionBar();
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);
    proD = new ProgressDialog(this);
    proD.setMessage("Please wait...");
    proD.setCancelable(false);

    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarsearch, null);
    text = (TextView) actionbarView.findViewById(R.id.cartText);
    title = (TextView) actionbarView.findViewById(R.id.txtsubtitle);

    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setVisibility(View.GONE);
    search = (ImageView) actionbarView.findViewById(R.id.search);

    search.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent nextIntent = new Intent(FullItems.this, SearchItems.class);
        nextIntent.putExtra("storeid", storeId);
        nextIntent.putExtra("storename", storeName);
        nextIntent.putExtra("storeaddress", storeAddress);
        nextIntent.putExtra("minorder", minOrder);
        if (items != null) {
          // nextIntent.putExtra("carttext", text);
          nextIntent.putExtra("items", items);
        }

        startActivityForResult(nextIntent, 2);
      }
    });

    // title.setText("Food Mart");
    cart = (ImageView) actionbarView.findViewById(R.id.cart);
    cart.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        if (items != null && items.length() > 0) {
          Intent nextIntent = new Intent(FullItems.this, SearchItemReview.class);
          // Log.i(getClass().getSimpleName(), "Items: " + items);
          nextIntent.putExtra("items", items);
          nextIntent.putExtra("storename", storeName);
          nextIntent.putExtra("storeid", storeId);
          nextIntent.putExtra("storeaddress", storeAddress);
          nextIntent.putExtra("minorder", minOrder);
          startActivityForResult(nextIntent, 1);
        } else {
          Toast.makeText(FullItems.this, "Please add atleast one item to proceed", Toast.LENGTH_SHORT).show();
        }

      }
    });
    linearViewN = (LinearLayout) actionbarView.findViewById(R.id.circle1);
    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);
    toolbar.setDisplayHomeAsUpEnabled(true);

    if (getIntent().hasExtra("storeid")) {
      storeId = getIntent().getStringExtra("storeid");
      storeName = getIntent().getStringExtra("storename");
      storeAddress = getIntent().getStringExtra("storeaddress");
      minOrder = getIntent().getStringExtra("minorder");
      catId = getIntent().getStringExtra("catid");
      catDesc = getIntent().getStringExtra("catname");

    }

    mFragmentManager = getSupportFragmentManager();
    toolbar.show();
    if (savedInstanceState != null) {

      // this.text.setText(savedInstanceState.getString("carttext"));

      items = savedInstanceState.getString("items");
      storeId = savedInstanceState.getString("storeid");
      storeName = savedInstanceState.getString("storename");
      storeAddress = savedInstanceState.getString("storeaddress");
      minOrder = savedInstanceState.getString("minorder");
      catId = savedInstanceState.getString("catid");
      catDesc = savedInstanceState.getString("catdesc");

      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      if (itemsSArray.size() > 0)
        itemsSArray.clear();
      itemsSArray = gson.fromJson(items, collectionType);

      if (itemsSArray != null && itemsSArray.size() > 0) {
        int itemNumber = 0;

        for (int i = 0; i < itemsSArray.size(); i++) {
          ItemsQuery itemsQ = itemsSArray.get(i);
          String itemsNumber = itemsQ.itemNumber;
          itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
        }

        this.text.setText("" + itemNumber);
      } else {
        this.text.setText("0");
      }

      updatedResponse = savedInstanceState.getString("updatedresponse");
      if (updatedResponse != null) {
        response = updatedResponse;
        onSuccessR();
        return;
      }

    }

    if (catDesc != null)
      title.setText(catDesc);

    if (getIntent().hasExtra("items") && getIntent().hasExtra("updatedresponse")) {
      // this.text.setText(getIntent().getStringExtra("carttext"));
      items = getIntent().getStringExtra("items");

      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      if (itemsSArray != null && itemsSArray.size() > 0)
        itemsSArray.clear();
      itemsSArray = gson.fromJson(items, collectionType);

      if (itemsSArray != null && itemsSArray.size() > 0) {
        int itemNumber = 0;

        for (int i = 0; i < itemsSArray.size(); i++) {
          ItemsQuery itemsQ = itemsSArray.get(i);
          String itemsNumber = itemsQ.itemNumber;
          itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
        }

        this.text.setText("" + itemNumber);
      }

      try {
        if (new JSONObject(getIntent().getStringExtra("updatedresponse")).getString("catid")
            .equalsIgnoreCase(catId)) {
          updatedResponse = getIntent().getStringExtra("updatedresponse");

          if (updatedResponse != null) {
            response = updatedResponse;
            onSuccessR();
            return;
          }
        } else {
          if (storeId != null) {
            proD.show();
            UShopRestClient.getUAHOY(this, "app.itemsByCat?catid=" + catId + "&sid=" + storeId, null,
                responseHandlerAllItems);
          }

          return;
        }
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    if (getIntent().hasExtra("items")) {
      // this.text.setText(getIntent().getStringExtra("carttext"));
      items = getIntent().getStringExtra("items");
      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      if (itemsSArray != null && itemsSArray.size() > 0)
        itemsSArray.clear();
      itemsSArray = gson.fromJson(items, collectionType);

      if (itemsSArray != null && itemsSArray.size() > 0) {
        int itemNumber = 0;

        for (int i = 0; i < itemsSArray.size(); i++) {
          ItemsQuery itemsQ = itemsSArray.get(i);
          String itemsNumber = itemsQ.itemNumber;
          itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
        }

        this.text.setText("" + itemNumber);
      }
    }

    if (storeId != null) {
      proD.show();
      UShopRestClient.getUAHOY(this, "app.itemsByCat?catid=" + catId + "&sid=" + storeId, null,
          responseHandlerAllItems);
    }

  }

  private class TabListner implements ActionBar.TabListener {

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub

      mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub

      tab.getText();
      // Log.i(getClass().getSimpleName(), "Tab Text: " + tab.getText());
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub

    }

  }

  // FOr getting the fragment.
  private Fragment getFragment(String tag) {

    CategoriesFragment f = (CategoriesFragment) mFragmentManager.findFragmentByTag(tag);
    if (f != null) {
      // Log.i(getClass().getSimpleName(),
      // "In the If block of Not null getFragment");

    }

    if (f == null) {
      // Log.i(getClass().getSimpleName(),
      // "In the If block of getFragment");
      f = (CategoriesFragment) CategoriesFragment.newInstance(tag);

      Bundle args = new Bundle();
      args.putString("category", tag);
      args.putString("allcat", response);
      f.setArguments(args);

    }

    return f;
  }

  // Fragment Adater for getting the fragments.
  private class TabPagerAdapter extends FragmentPagerAdapter implements TabListener {

    private FragmentManager fm;

    private int mCount = catArray.length;

    public TabPagerAdapter(FragmentManager fm) {
      super(fm);
      this.fm = fm;

      // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
      // TODO Auto-generated method stub

      // Log.i(getClass().getSimpleName(), response);
      Fragment fragment = getFragment(catArray[arg0] + "," + catArrayId[arg0]);
      return fragment;

    }

    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      // Log.i(getClass().getSimpleName(), "In the PageTitle: " +
      // position);
      SpannableString s = new SpannableString(catArray[position].toUpperCase());
      s.setSpan(new ActionbarCus("", tff), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return s;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub
      // Log.i(getClass().getSimpleName(), "In onTabSelected");
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub
      // Log.i(getClass().getSimpleName(), "In onTabUnSelected");
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
      // TODO Auto-generated method stub

    }

  }

  public void onSuccessR() {
    try {

      // Log.i(getClass().getSimpleName(), "Response: " + response);

      if (new JSONObject(response).getString("status").equalsIgnoreCase("success")
          && new JSONObject(response).getInt("catid") == Integer.valueOf(catId)
          && new JSONObject(response).getInt("sid") == Integer.valueOf(storeId)) {

        FullItemsData fullData = gson.fromJson(response, FullItemsData.class);

        catArray = new String[fullData.list.size()];
        catArrayId = new String[fullData.list.size()];

        for (int i = 0; i < fullData.list.size(); i++) {
          catArray[i] = fullData.list.get(i).subcat;
          catArrayId[i] = String.valueOf(fullData.list.get(i).subcatid);
        }

        TabListner tabListner = new TabListner();
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(tabPagerAdapter);

        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

          @Override
          public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub

            // Log.i(getClass().getSimpleName(), "Title: " +
            // getTitle());

          }

          @Override
          public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

          }
        });
        mIndicator.setViewPager(mViewPager);

      }
      // FullItemsData fullData = gson.fromJson(response,
      // FullItemsData.class);
      // if(fullData != null)
      // {
      // Log.i(getClass().getSimpleName(), fullData.list.size()+"
      // items");
      // }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

    }
  }


  // Handler invoked after fetching all the items from the API.
  AsyncHttpResponseHandler responseHandlerAllItems = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      // TODO Auto-generated method stub
      try {
        response = new String(arg2, "UTF-8");
        // Log.i(getClass().getSimpleName(), "Response: " + response);
        try {
          proD.dismiss();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        if (new JSONObject(response).getString("status").equalsIgnoreCase("success")
            && new JSONObject(response).getInt("catid") == Integer.valueOf(catId)
            && new JSONObject(response).getInt("sid") == Integer.valueOf(storeId)) {

          FullItemsData fullData = gson.fromJson(response, FullItemsData.class);

          catArray = new String[fullData.list.size()];
          catArrayId = new String[fullData.list.size()];

          for (int i = 0; i < fullData.list.size(); i++) {
            catArray[i] = fullData.list.get(i).subcat;
            catArrayId[i] = String.valueOf(fullData.list.get(i).subcatid);
          }

          TabListner tabListner = new TabListner();
          tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

          mViewPager = (ViewPager) findViewById(R.id.pager);
          mViewPager.setAdapter(tabPagerAdapter);

          mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
          mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
              // TODO Auto-generated method stub

              // Log.i(getClass().getSimpleName(), "Title: " +
              // getTitle());

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
              // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
              // TODO Auto-generated method stub

            }
          });
          mIndicator.setViewPager(mViewPager);

        }
        // FullItemsData fullData = gson.fromJson(response,
        // FullItemsData.class);
        // if(fullData != null)
        // {
        // Log.i(getClass().getSimpleName(), fullData.list.size()+"
        // items");
        // }
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        try {
          proD.dismiss();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }

  };

  public class Packing {

    int packid;
    String pack;
    int mrp;
    String offer;
    int sellPrice;
    int itemnumber;

  }

  public class SubList {
    int itemid;
    String item;
    String image;
    ArrayList<Packing> packings;

  }

  public class FullList {
    int subcatid;
    String subcat;
    int itemcount;
    ArrayList<SubList> items;

  }

  public class FullItemsData {

    int catid;
    int sid;
    String status;
    ArrayList<FullList> list;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);

    outState.putString("items", items);
    outState.putString("carttext", this.text.getText().toString());
    outState.putString("updatedresponse", updatedResponse);
    outState.putString("storeid", storeId);
    outState.putString("storename", storeName);
    outState.putString("storeaddress", storeAddress);
    outState.putString("minorder", minOrder);
    outState.putString("catid", catId);
    outState.putString("catdesc", catDesc);

  }

  @Override
  public void passDataToIncrement(String value) {
    // TODO Auto-generated method stub
    String text = this.text.getText().toString();
    // Log.i(getClass().getSimpleName(), "Text: " + text);
    int s = Integer.valueOf(text);
    int s1 = Integer.valueOf(value);

    int main = s + s1;
    this.text.setText(Integer.toString(main));
  }

  @Override
  public void passDataToDecrement(String value) {
    // TODO Auto-generated method stub
    String text = this.text.getText().toString();
    int s = Integer.valueOf(text);
    int s1 = Integer.valueOf(value);

    int main = s - s1;
    this.text.setText(Integer.toString(main));
  }

  @Override
  public void finishActivity() {
    // TODO Auto-generated method stub

  }

  @Override
  public void triggerAreas(String city) {
    // TODO Auto-generated method stub
    items = city;
    Log.i(getClass().getSimpleName(), "Items are: " + items);
  }

  @Override
  public void callBackUpdatedResponse(String response) {
    // TODO Auto-generated method stub
    updatedResponse = response;
    // Log.i(getClass().getSimpleName(), "response is: " + response);
  }

  @Override
  public String getUpdatedResponse() {
    return updatedResponse;
  }

  @Override
  public String getUpdatedItems() {
    // TODO Auto-generated method stub
    return items;
  }

  @Override
  public void updateCart(String text) {
    // TODO Auto-generated method stub
    Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
    }.getType();
    if (itemsSArray != null && itemsSArray.size() > 0)
      itemsSArray.clear();
    itemsSArray = gson.fromJson(items, collectionType);

    if (itemsSArray.size() > 0) {
      int itemNumber = 0;

      for (int i = 0; i < itemsSArray.size(); i++) {
        ItemsQuery itemsQ = itemsSArray.get(i);
        String itemsNumber = itemsQ.itemNumber;
        itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
      }

      this.text.setText("" + itemNumber);

      Log.i(getClass().getSimpleName(), "Item Number is: " + itemNumber);
    } else {
      this.text.setText("0");
    }
  }

  @Override
  protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    // TODO Auto-generated method stub
    super.onActivityResult(arg0, arg1, arg2);
    if (arg0 == 1) {
      if (arg2 != null && arg2.hasExtra("updatedJson")) {

        items = arg2.getStringExtra("updatedJson");
      }
    }

    if (arg0 == 2) {
      if (arg2 != null && arg2.hasExtra("items")) {

        items = arg2.getStringExtra("items");
      }
    }
  }

  @Override
  public void onBackPressed() {

    if (items != null) {

      Intent nextIntent = getIntent();
      nextIntent.putExtra("carttext", this.text.getText().toString());
      nextIntent.putExtra("items", items);
      nextIntent.putExtra("updatedresponse", updatedResponse);
      setResult(4, nextIntent);
      finish();

    } else {
      finish();
    }
  }
}
