package com.shopezzy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class ThankYou extends AppCompatActivity {

  private Toolbar toolbar;
  private SharedPreferences pref;
  Typeface tff;
  private TextView pageTitle;
  private Button addmore;
  private TextView ordernumnext, orderdate, shopname, address, delivery, myorders, home, itemsNum, itemsprice;
  String orderplace;

  @Override
  public void onBackPressed() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.orderplaced);
    toolbar = getSupportActionBar();
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);

    pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);
    pageTitle.setText("Order Placed");
    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setVisibility(View.GONE);

    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);

    ordernumnext = (TextView) findViewById(R.id.ordernumnext);
    orderdate = (TextView) findViewById(R.id.orderdate);
    shopname = (TextView) findViewById(R.id.shopname);
    address = (TextView) findViewById(R.id.address);
    delivery = (TextView) findViewById(R.id.delivery);
    itemsNum = (TextView) findViewById(R.id.itemsNum);
    itemsprice = (TextView) findViewById(R.id.itemsprice);
    myorders = (TextView) findViewById(R.id.myorders);
    myorders.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ThankYou.this, MyOrders.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    });
    home = (TextView) findViewById(R.id.home);
    home.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ThankYou.this, PickStore.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    });

    if (getIntent().hasExtra("orderplace")) {
      orderplace = getIntent().getStringExtra("orderplace");
      try {
        ordernumnext.setText(new JSONObject(orderplace).getString("orderid"));
        orderdate.setText(new JSONObject(orderplace).getString("ordertime"));
        shopname.setText(new JSONObject(orderplace).getString("sname"));
        address.setText(new JSONObject(orderplace).getString("sadd"));
        delivery.setText("Delivery " + new JSONObject(orderplace).getString("dtime"));
        itemsNum.setText(new JSONObject(orderplace).getString("items") + " items - ");
        itemsprice.setText(
            getResources().getString(R.string.rs) + " " + new JSONObject(orderplace).getString("prices"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

  }

}
