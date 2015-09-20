package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shopezzy.SearchItems.ItemsQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Checkout extends AppCompatActivity {

  private Toolbar toolbar;
  private SharedPreferences pref;
  Typeface tff;
  private TextView pageTitle;
  private Button addmore;
  private String storeName;
  String storeId;
  String storeAddress;
  String updatedJson;
  private ImageView editimage;
  private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
  private TextView shopName, address, itemsNumber, itemsPrice, userName, userAddress1, userAddress2, userMobilenumber;
  private Gson gson;
  private String userInfo;
  String totalItems;
  String totalPrice;
  private LinearLayout done;
  ProgressDialog proD;

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();

    userInfo = pref.getString(Constants.viewuser, "NA");
    // Log.i(getClass().getSimpleName(), "user Address: " + userInfo);
    shopName.setText(storeName);
    address.setText(storeAddress);
    itemsNumber.setText(totalItems);
    itemsPrice.setText(totalPrice);
    String viewuser = pref.getString(Constants.viewuser, "NA");
    proD = new ProgressDialog(Checkout.this);
    proD.setMessage("Your order is being placed...");
    proD.setCancelable(false);
    try {

      String name = new JSONObject(viewuser).getString("name").toString();
      if (name.length() > 0)
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
      userName.setText(name);

      String add1 = new JSONObject(viewuser).getString("add1").toString();
      String add2 = new JSONObject(viewuser).getString("add2").toString();
      String add3 = new JSONObject(viewuser).getString("locality").toString();
      String city = new JSONObject(viewuser).getString("city").toString();
      String pincode = new JSONObject(viewuser).getString("pincode").toString();
      String msisdn = new JSONObject(viewuser).getString("msisdn").toString();
      userAddress1.setText(add1 + ", " + add2 + ", " + add3);
      userAddress2.setText(city + ", " + pincode);
      userMobilenumber.setText("(M) " + msisdn);

    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // Handler invoked after placing the order.
  AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Toast.makeText(Checkout.this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Toast.makeText(Checkout.this, "Success", Toast.LENGTH_SHORT).show();

      try {
        String responseString = new String(arg2, "UTF-8");
        // Log.i(getClass().getSimpleName(), "Response: " +
        // responseString);
        Intent nextIntent = new Intent(Checkout.this, ThankYou.class);
        nextIntent.putExtra("orderplace", responseString);
        // nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextIntent);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  };

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checkout);
    // if (USHOP.launch) {
    //
    // Intent intent = new Intent(this, SplashScreen.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    // startActivity(intent);
    // }
    toolbar = getSupportActionBar();
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);

    pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);
    pageTitle.setText("Checkout");
    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setVisibility(View.GONE);

    shopName = (TextView) findViewById(R.id.shopname);
    address = (TextView) findViewById(R.id.address);
    itemsNumber = (TextView) findViewById(R.id.itemss);
    itemsPrice = (TextView) findViewById(R.id.cost);
    userName = (TextView) findViewById(R.id.name);
    userAddress1 = (TextView) findViewById(R.id.address1);
    userAddress2 = (TextView) findViewById(R.id.address2);
    userMobilenumber = (TextView) findViewById(R.id.phone);
    editimage = (ImageView) findViewById(R.id.editimage);
    gson = new Gson();
    done = (LinearLayout) findViewById(R.id.done);

    done.setOnClickListener(new OnClickListener() {

      @SuppressWarnings("deprecation")
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        try {
          String url = "app.orderplace?msisdn=" + pref.getString(Constants.mobile_num, "NA") + "&sid="
              + storeId + "&totprice=" + totalPrice.split(" ")[1] + "&totitems="
              + totalItems.split(" ")[0];

          // Log.i(getClass().getSimpleName(), "URL is: " + url);
          if (updatedJson != null && updatedJson.length() > 0) {
            // Log.i(getClass().getSimpleName(), "Updated JSON: "
            // + updatedJson);
            JSONArray jsonArray = new JSONArray(updatedJson);
            JSONArray mainArray = new JSONArray();
            if (jsonArray.length() > 0) {

              for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subObject = new JSONObject();
                subObject.put("inventoryId",
                    Integer.valueOf(jsonArray.getJSONObject(i).getString("itemId")));
                subObject.put("itemQuantity",
                    Integer.valueOf(jsonArray.getJSONObject(i).getString("itemNumber")));
                subObject.put("price", Double.valueOf(jsonArray.getJSONObject(i).getString("itemSp")));
                subObject.put("name", jsonArray.getJSONObject(i).getString("itemName"));
                subObject.put("size", jsonArray.getJSONObject(i).getString("itemWt"));
                mainArray.put(subObject);
              }

              // Log.i(getClass().getSimpleName(),
              // "JSON Array is: "
              // + mainArray.toString());
              HttpPost httpPost = new HttpPost();
              httpPost.setEntity(new StringEntity(mainArray.toString()));
              try {
                proD.show();
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              UShopRestClient.postUAHOY(Checkout.this, url, httpPost.getEntity(), responseHandler);
            }

          }
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    });

    editimage.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent nextIntent = new Intent(Checkout.this, AddAddress.class);
        nextIntent.putExtra("from", "checkout");
        startActivity(nextIntent);
      }
    });

    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);
    toolbar.setDisplayHomeAsUpEnabled(true);

    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    storeName = getIntent().getStringExtra("storename");
    storeId = getIntent().getStringExtra("storeid");
    storeAddress = getIntent().getStringExtra("storeaddress");
    updatedJson = getIntent().getStringExtra("updatedJson");
    totalItems = getIntent().getStringExtra("totalitems");
    totalPrice = getIntent().getStringExtra("totalprice");

    Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
    }.getType();
    itemsSArray = gson.fromJson(updatedJson, collectionType);

  }
}
