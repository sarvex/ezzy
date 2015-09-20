package com.shopezzy;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class AddAddress extends AppCompatActivity {

  private Toolbar toolbar;
  private EditText hname, hnumber, streat, city, picode, locality, email;
  Typeface tff;
  private SharedPreferences pref;
  private RelativeLayout done;
  ProgressDialog proD;
  private TextView pageTitle;
  private Button addmore;
  private String updatedJson;
  private String storeName;
  String storeId;
  String storeAddress;
  String totalItems;
  String totalPrice;
  String viewUser;
  String clear;


  // For fetching the email.
  String getEmail(Context context) {
    AccountManager accountManager = AccountManager.get(context);
    Account account = getAccount(accountManager);

    if (account == null) {
      return null;
    } else {
      return account.name;
    }
  }

  private Account getAccount(AccountManager accountManager) {
    Account[] accounts = accountManager.getAccountsByType("com.google");
    Account account;
    if (accounts.length > 0) {
      account = accounts[0];
    } else {
      account = null;
    }
    return account;
  }

  // User handler invokes after fetching user info.
  AsyncHttpResponseHandler responseHandlerUser = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
        Toast.makeText(AddAddress.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      // TODO Auto-generated method stub
      try {
        String responseString = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
          pref.edit().putString(Constants.viewuser, responseString).commit();

          String fromI = "";
          if (getIntent().hasExtra("from")) {
            fromI = getIntent().getStringExtra("from");
          }
          if (!fromI.equalsIgnoreCase("checkout")) {
            Intent nextIntent = new Intent(AddAddress.this, Checkout.class);
            if (updatedJson != null && updatedJson.length() > 0)
              nextIntent.putExtra("updatedJson", updatedJson);

            nextIntent.putExtra("storename", storeName);
            nextIntent.putExtra("storeid", storeId);
            nextIntent.putExtra("storeaddress", storeAddress);
            nextIntent.putExtra("totalitems", totalItems);
            nextIntent.putExtra("totalprice", totalPrice);
            startActivity(nextIntent);

          }
          AddAddress.this.finish();

        } else {
          Toast.makeText(AddAddress.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

        proD.dismiss();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        proD.dismiss();
        e.printStackTrace();
        Toast.makeText(AddAddress.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
      }
    }

  };

  // Handler after adding user info will be called.
  AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
        Toast.makeText(AddAddress.this, "Something went wrong", Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      // TODO Auto-generated method stub
      try {
        String responseString = new String(arg2, "UTF-8");
        if (responseString.trim().equalsIgnoreCase("update")) {
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              // TODO Auto-generated method stub
              UShopRestClient.getUAHOY(AddAddress.this,
                  "app.viewUser?msisdn=" + pref.getString(Constants.mobile_num, "NA"), null,
                  responseHandlerUser);
            }
          }, 1000);

          Toast.makeText(AddAddress.this, "Success", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("User is not register")
            || responseString.trim().equalsIgnoreCase("Bad Request")
            || responseString.trim().equalsIgnoreCase("Internal Server Error")) {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("Invalid Name")) {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Invalid Name", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("Invalid Address1")
            || responseString.trim().equalsIgnoreCase("Invalid Address2")) {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Invalid Address", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("Invalid Locality")) {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Invalid Locality", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("Invalid city")) {
          Toast.makeText(AddAddress.this, "Invalid City", Toast.LENGTH_SHORT).show();
        } else if (responseString.trim().equalsIgnoreCase("Invalid pincode")) {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Invalid Pincode", Toast.LENGTH_SHORT).show();
        } else {
          proD.dismiss();
          Toast.makeText(AddAddress.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

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
  protected void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    try {
      outState.putString("hname", hname.getText().toString());
      outState.putString("hnumber", hnumber.getText().toString());
      outState.putString("streat", streat.getText().toString());
      outState.putString("email", email.getText().toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.shippingaddress);
    // if (USHOP.launch) {
    //
    // Intent intent = new Intent(this, SplashScreen.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    // startActivity(intent);
    // }
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    toolbar = getSupportActionBar();
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);

    toolbar.setBackgroundDrawable(colorDrawable);
    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);

    viewUser = pref.getString(Constants.viewuser, "NA");

    pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);
    pageTitle.setText("Delivery Address");
    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setVisibility(View.GONE);

    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);
    toolbar.setDisplayHomeAsUpEnabled(true);

    storeName = getIntent().getStringExtra("storename");
    storeId = getIntent().getStringExtra("storeid");
    storeAddress = getIntent().getStringExtra("storeaddress");
    totalItems = getIntent().getStringExtra("totalitems");
    totalPrice = getIntent().getStringExtra("totalprice");

    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    if (getIntent().hasExtra("updatedJson")) {
      updatedJson = getIntent().getStringExtra("updatedJson");
    }
    hname = (EditText) findViewById(R.id.hname);
    hname.setTypeface(tff);
    hnumber = (EditText) findViewById(R.id.hnumber);
    hnumber.setTypeface(tff);
    streat = (EditText) findViewById(R.id.streat);
    streat.setTypeface(tff);
    city = (EditText) findViewById(R.id.city);
    city.setTypeface(tff);
    picode = (EditText) findViewById(R.id.picode);
    picode.setTypeface(tff);
    locality = (EditText) findViewById(R.id.locality);
    locality.setTypeface(tff);
    email = (EditText) findViewById(R.id.email);
    email.setTypeface(tff);
    done = (RelativeLayout) findViewById(R.id.done);

    if (!viewUser.equalsIgnoreCase("NA"))

    {

      // Log.i(getClass().getSimpleName(), "viewuser: " + viewUser);

      if (savedInstanceState != null) {
        hname.setText(savedInstanceState.getString("hname"));
        hnumber.setText(savedInstanceState.getString("hname"));
        streat.setText(savedInstanceState.getString("streat"));
        email.setText(savedInstanceState.getString("email"));
      } else {
        try {
          hname.setText(new JSONObject(viewUser).getString("name").toString());
          email.setText(new JSONObject(viewUser).getString("email").toString());
          if (!getIntent().hasExtra("clear")) {
            hnumber.setText(new JSONObject(viewUser).getString("add1").toString());
            streat.setText(new JSONObject(viewUser).getString("add2").toString());
          }
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      // city.setText(new
      // JSONObject(viewUser).getString("city").toString());
      // locality.setText(new
      // JSONObject(viewUser).getString("locality").toString());
      // picode.setText(new
      // JSONObject(viewUser).getString("picode").toString());

    } else {
      if (savedInstanceState != null) {
        hname.setText(savedInstanceState.getString("hname"));
        hnumber.setText(savedInstanceState.getString("hname"));
        streat.setText(savedInstanceState.getString("streat"));
        email.setText(savedInstanceState.getString("email"));
      }
    }

    city.setText(pref.getString(Constants.selectedCity, "NA"));
    locality.setText(pref.getString(Constants.selectedLocality, "NA"));
    picode.setText(pref.getString(Constants.selectedPin, "NA"));

    proD = new ProgressDialog(AddAddress.this);
    proD.setMessage("please wait...");
    proD.setCancelable(false);

    try {
      if (email.getText().toString().length() == 0)
        email.setText(getEmail(this));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    done.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        if (hname.getText().toString().length() == 0) {
          hname.setError("Enter your name");
          return;
        }

        if (hnumber.getText().toString().length() == 0) {
          hnumber.requestFocus();
          hnumber.setFocusable(true);
          hnumber.setError("Enter your address");
          return;
        }

        if (streat.getText().toString().length() == 0) {
          streat.requestFocus();
          streat.setFocusable(true);
          streat.setError("Enter your address");
          return;
        }

        if (email.getText().toString().length() == 0) {
          email.requestFocus();
          email.setFocusable(true);
          email.setError("Enter your email");
          return;
        }

        try {
          proD.show();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        UShopRestClient.getUAHOY(AddAddress.this, "app.updateUser?msisdn="
            + Uri.encode(pref.getString(Constants.mobile_num, "NA")) + "&city="
            + Uri.encode(city.getText().toString()) + "&pin=" + Uri.encode(picode.getText().toString())
            + "&locality=" + Uri.encode(locality.getText().toString()) + "&add1="
            + Uri.encode(hnumber.getText().toString()) + "&add2=" + Uri.encode(streat.getText().toString())
            + "&name=" + Uri.encode(hname.getText().toString()) + "&email="
            + Uri.encode(email.getText().toString()), null, responseHandler);

      }
    });
  }
}
