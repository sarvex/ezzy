package com.shopezzy;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shopezzy.CitySelection.Store;
import com.shopezzy.CitySelection.TotalStores;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PickStore extends AppCompatActivity {

  private Toolbar toolbar;
  private Gson gson;
  private TextView select3, near;
  private Button changeLocation;
  private ListView listView;
  Typeface tf;
  SharedPreferences pref;
  String selectedArea, selectedCity;
  private ProgressDialog proD;
  private String getStoresbyPin = "getstoresbypin?pin=";
  TotalStores totalStores;
  private GoogleCloudMessaging gcm = null;
  private String regid;
  String email = "";
  String message = "";

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.main, menu);
    Menu m = menu;
    MenuItem item = menu.findItem(R.id.myorder_action);
    item.getActionView().setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent nextIntent = new Intent(PickStore.this, MyOrders.class);
        startActivity(nextIntent);
      }
    });
    return true;
  }

  // Function for checking play services.
  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        finish();
      }
      return false;
    }
    return true;
  }

  //Handler invoked after fetching stores by Pin.
  AsyncHttpResponseHandler responseHandlerPin = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      try {
        proD.dismiss();
        if (message.length() > 0) {
          Intent nextIntent = new Intent(PickStore.this, PushMessage.class);
          nextIntent.putExtra("message", message);
          startActivity(nextIntent);
          message = "";
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      try {
        String response = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(response);
        totalStores = new CitySelection().new TotalStores();
        totalStores.pin = jsonObject.getString("pin");
        totalStores.area = jsonObject.getString("area");
        totalStores.city = jsonObject.getString("city");
        ArrayList<Store> stores = new ArrayList<CitySelection.Store>();
        if (jsonObject.getJSONArray("stores").length() > 0) {
          for (int i = 0; i < jsonObject.getJSONArray("stores").length(); i++) {
            JSONObject storeObject = jsonObject.getJSONArray("stores").getJSONObject(i);
            Store store = new CitySelection().new Store();
            store.id = storeObject.getString("id");
            store.name = storeObject.getString("name");
            store.type = storeObject.getString("type");
            store.add = storeObject.getString("add");
            store.city = storeObject.getString("city");
            store.pin = storeObject.getString("pin");
            store.min = storeObject.getString("min");
            store.dchrg = storeObject.getString("dchrg");
            store.pmode = storeObject.getString("pmode");
            store.dtime = storeObject.getString("dtime");
            store.imgurl = storeObject.getString("imgurl");
            store.rtng = storeObject.getString("rtng");
            stores.add(store);

          }
          totalStores.stores = stores;

          try {
            proD.dismiss();
          } catch (Exception e) {
            e.printStackTrace();
          }

          if (totalStores.stores.size() > 1)
            select3.setText("Select from " + totalStores.stores.size() + " Stores");
          else
            select3.setText("Select from " + totalStores.stores.size() + " Store");

          near.setText(pref.getString(Constants.selectedCity, "NA") + ", "
              + pref.getString(Constants.selectedLocality, "NA"));
          listView = (ListView) findViewById(R.id.listview);
          MySpinnerAdapter storeAdapter = new MySpinnerAdapter(PickStore.this, 0, totalStores.stores);
          listView.setAdapter(storeAdapter);

          if (message.length() > 0) {
            Intent nextIntent = new Intent(PickStore.this, PushMessage.class);
            nextIntent.putExtra("message", message);
            startActivity(nextIntent);
            message = "";
          }

        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  };

  // Function for getting device email.
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

  // Handler invoked after sending push info.
  AsyncHttpResponseHandler responseHandlerPush = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      try {
        String responseString = new String(arg2, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      try {
        String responseString = new String(arg2, "UTF-8");
        if (responseString.trim().equalsIgnoreCase("200 : Ok")) {
          pref.edit().putString(Constants.push_id, "yes").commit();
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

    }

  };

  // Function for fetching Android Id.
  String getAndroidID() {
    return android.provider.Settings.Secure.getString(this.getContentResolver(), "android_id");
  }

  // Function for fetching app version code.
  public static int getApplicationVersionCode(Context context) {
    PackageManager packageManager = context.getPackageManager();
    try {
      PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (NameNotFoundException ex) {
    } catch (Exception e) {
    }
    return 0;
  }


  // Function for sending device push id.
  private void sendDevicePushId() {

    if (!pref.getString(Constants.GCMID, "NA").equals("NA")) {
      String url = "http://shopezzy.com/ce/push/msisdn/" + pref.getString(Constants.mobile_num, "NA") + "/pushid/"
          + pref.getString(Constants.GCMID, "NA") + "/src/com.shopezzy/versioncode/"
          + getApplicationVersionCode(this);
      // + "?apv=1.0&apikey=564a7881902be3d6c85cbd5074dadacc&act=0&did="
      // + getAndroidID() + "&demail=" + email;
      RequestParams params = new RequestParams();
      params.put("apv", "1.0");
      params.put("apikey", "564a7881902be3d6c85cbd5074dadacc");
      params.put("act", "0");
      params.put("demail", email);
      UShopRestClient.postUAHOYFull(this, url, params, responseHandlerPush);

    }

  }

  // Handler will be invokes after sending device info.
  AsyncHttpResponseHandler responseHandlerDevice = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      pref.edit().putString(Constants.device_registered, "yes").commit();

    }

  };

  // Function for device registration.
  private void sendDeviceRegistration() {

    String url = "http://uahoy.com/dr/register?did=" + getAndroidID()
        + "&platform=android&src=com.shopezzy&testmode=false&apikey=483471a226ea6b56684b2bad7986c6a1&referrer="
        + pref.getString(Constants.pref_referrer, "NA");
    UShopRestClient.getUAHOYFull(this, url, null, responseHandlerDevice);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    setContentView(R.layout.pickstore);
    toolbar = getSupportActionBar();
    email = getEmail(this);
    pref = PreferenceManager.getDefaultSharedPreferences(this);

    if (checkPlayServices()) {

      gcm = GoogleCloudMessaging.getInstance(this);
      Thread th = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            if (pref.getString(Constants.GCMID, "").length() == 0) {
              regid = gcm.register(Constants.SENDERID);
              if (regid.length() > 0) {

                pref.edit().putString(Constants.GCMID, regid).commit();
                sendDevicePushId();

              }
            }

          } catch (IOException e) {
            e.printStackTrace();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      // sendDevicePushId();
      if (pref.getString(Constants.GCMID, "").length() == 0)
        th.start();

      if (pref.getString(Constants.GCMID, "").length() > 0) {

        if (pref.getString(Constants.push_id, "NA").equals("NA")) {
          sendDevicePushId();
        }

      }

      if (pref.getString(Constants.device_registered, "NA").equals("NA"))
        sendDeviceRegistration();
    }

    selectedArea = pref.getString(Constants.selectedLocality, "NA");
    selectedCity = pref.getString(Constants.selectedCity, "NA");
    tf = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");
    SpannableString s = new SpannableString("Pick your Store");
    s.setSpan(new ActionbarCus("", tf), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    toolbar.setTitle(s);

    toolbar.show();

    gson = new Gson();

    proD = new ProgressDialog(PickStore.this);
    proD.setMessage("Fetching Stores...");
    proD.setCancelable(false);

    listView = (ListView) findViewById(R.id.listview);
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent nextIntent = new Intent(PickStore.this, Categories.class);
        nextIntent.putExtra("storeid", totalStores.stores.get(position).id);
        nextIntent.putExtra("storename", totalStores.stores.get(position).name);
        nextIntent.putExtra("storeaddress", totalStores.stores.get(position).add);
        nextIntent.putExtra("minorder", totalStores.stores.get(position).min);

        startActivity(nextIntent);

      }
    });

    select3 = (TextView) findViewById(R.id.select3);
    near = (TextView) findViewById(R.id.near);
    changeLocation = (Button) findViewById(R.id.changeLocation);
    changeLocation.setTypeface(tf);
    changeLocation.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (!getIntent().hasExtra("stores")) {
          Intent nextIntent = new Intent(PickStore.this, CitySelection.class);
          startActivity(nextIntent);
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              PickStore.this.finish();
            }
          }, 100);

        } else {
          PickStore.this.finish();
        }

      }
    });

    if (getIntent().hasExtra("message")) {
      message = getIntent().getStringExtra("message");
    }

    if (!getIntent().hasExtra("stores")) {

      try {
        proD.show();
        UShopRestClient.getUAHOY(PickStore.this, getStoresbyPin + pref.getString(Constants.selectedPin, "NA"),
            null, responseHandlerPin);
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      try {
        totalStores = gson.fromJson(getIntent().getStringExtra("stores"), TotalStores.class);

        if (totalStores.stores.size() > 1)
          select3.setText("Select from " + totalStores.stores.size() + " Stores");
        else
          select3.setText("Select from " + totalStores.stores.size() + " Store");

        near.setText(pref.getString(Constants.selectedCity, "NA") + ", " + getIntent().getStringExtra("area"));
        listView = (ListView) findViewById(R.id.listview);
        MySpinnerAdapter storeAdapter = new MySpinnerAdapter(this, 0, totalStores.stores);
        listView.setAdapter(storeAdapter);
        if (message.length() > 0) {
          Intent nextIntent = new Intent(PickStore.this, PushMessage.class);
          nextIntent.putExtra("message", message);
          startActivity(nextIntent);
          message = "";
        }
      } catch (JsonSyntaxException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  protected void onRestart() {
    super.onRestart();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  // Adapter for displaying stores.
  public class MySpinnerAdapter extends ArrayAdapter<Store> {
    private Context mContext;

    ArrayList<Store> store;

    @Override
    public int getCount() {
      return store.size();
    }

    public MySpinnerAdapter(Context context, int resource, ArrayList<Store> objects) {
      super(context, resource, objects);
      this.mContext = context;

      this.store = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View rowView = convertView;
      if (rowView == null) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.pickstoreview, null);
        ViewHolderStore viewHolder = new ViewHolderStore();
        viewHolder.name = (TextView) rowView.findViewById(R.id.name);
        viewHolder.minorder = (TextView) rowView.findViewById(R.id.minorder);
        viewHolder.minrs = (TextView) rowView.findViewById(R.id.minrs);
        viewHolder.delivey = (TextView) rowView.findViewById(R.id.delivey);
        viewHolder.address = (TextView) rowView.findViewById(R.id.address);
        viewHolder.ratingBar2 = (RatingBar) rowView.findViewById(R.id.ratingBar2);
        rowView.setTag(viewHolder);

      }
      final ViewHolderStore viewHolder = (ViewHolderStore) rowView.getTag();
      viewHolder.name.setText(store.get(position).name);
      viewHolder.address.setText(store.get(position).add);
      viewHolder.minorder.setText("Min. Order");
      viewHolder.minrs.setText(this.mContext.getResources().getString(R.string.rs) + " " + store.get(position).min);
      viewHolder.delivey.setText("Delivery: " + store.get(position).dtime);
      viewHolder.ratingBar2.setRating(Float.valueOf(store.get(position).rtng));

      return rowView;
    }
  }

  static class ViewHolderStore {

    private TextView name;
    private TextView minorder;
    private TextView minrs;
    private TextView delivey;
    private TextView address;
    private RatingBar ratingBar2;

  }

}
