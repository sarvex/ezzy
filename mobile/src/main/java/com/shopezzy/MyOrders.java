package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOrders extends AppCompatActivity {

  private Toolbar toolbar;
  private SharedPreferences pref;
  Typeface tff;
  private TextView pageTitle;
  private Button addmore;
  ProgressDialog proD;
  private ArrayList<MyOrdersSub> myorders;
  private ListView listView;


  // Adapter to display all the orders.
  public class MySpinnerAdapter extends ArrayAdapter<MyOrdersSub> {
    private Context mContext;

    ArrayList<MyOrdersSub> store;

    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return store.size();
    }

    public MySpinnerAdapter(Context context, int resource, ArrayList<MyOrdersSub> objects) {
      super(context, resource, objects);
      this.mContext = context;

      this.store = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // TODO Auto-generated method stub
      View rowView = convertView;
      if (rowView == null) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.myorderview, null);
        ViewHolderStore viewHolder = new ViewHolderStore();
        viewHolder.shopname = (TextView) rowView.findViewById(R.id.shopname);
        viewHolder.orderid = (TextView) rowView.findViewById(R.id.orderid);
        viewHolder.date = (TextView) rowView.findViewById(R.id.date);
        viewHolder.items = (TextView) rowView.findViewById(R.id.items);
        viewHolder.price = (TextView) rowView.findViewById(R.id.price);
        viewHolder.orderstatus = (Button) rowView.findViewById(R.id.orderstatus);
        viewHolder.orderstatus.setTypeface(tff);
        rowView.setTag(viewHolder);

      }
      final ViewHolderStore viewHolder = (ViewHolderStore) rowView.getTag();
      viewHolder.shopname.setText(store.get(position).sname);
      viewHolder.orderid.setText(store.get(position).orderId);
      viewHolder.price
          .setText(this.mContext.getResources().getString(R.string.rs) + " " + store.get(position).prices);
      viewHolder.date.setText(store.get(position).datetime);

      GradientDrawable drawable = (GradientDrawable) viewHolder.orderstatus.getBackground();

      if (store.get(position).status == 0) {

        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "PLACED");
      }

      if (store.get(position).status == 1) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "Accepted");
      }

      if (store.get(position).status == 2) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "DECLINED");
      }

      if (store.get(position).status == 3) {
        drawable.setColor(getResources().getColor(R.color.cancelled));
        viewHolder.orderstatus.setText("" + "CANCELLED");
      }

      if (store.get(position).status == 4) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "Accepted With Comment");
      }

      if (store.get(position).status == 5) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "IN-PROCESS");
      }

      if (store.get(position).status == 6) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "IN TRANSIT");
      }

      if (store.get(position).status == 7) {
        drawable.setColor(getResources().getColor(R.color.delivered));
        viewHolder.orderstatus.setText("" + "DELIVERED");
      }

      if (store.get(position).status == 8) {
        drawable.setColor(getResources().getColor(R.color.inprocess));
        viewHolder.orderstatus.setText("" + "COMPLETED");
      }

      viewHolder.items.setText(store.get(position).items + " items");

      // i++;
      // if (i % 2 == 0) {
      // if (i == 0)
      // return rowView;
      //
      // viewHolder.layout.setBackgroundColor(getResources().getColor(
      // R.color.black90T));
      // }

      return rowView;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  static class ViewHolderStore {

    private TextView shopname;
    private TextView orderid;
    private TextView date;
    private TextView items;
    private Button orderstatus;
    private TextView price;

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.myorders);
    toolbar = getSupportActionBar();
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);

    pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);
    pageTitle.setText("My Orders");
    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setVisibility(View.GONE);

    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);
    toolbar.setDisplayHomeAsUpEnabled(true);

    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

    proD = new ProgressDialog(MyOrders.this);
    proD.setMessage("Please wait...");
    proD.setCancelable(false);

    listView = (ListView) findViewById(R.id.listview);
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        // Log.i(getClass().getSimpleName(), "In the onItemclick");
        Button orderPla = (Button) view.findViewById(R.id.orderstatus);
        Intent nextIntent = new Intent(MyOrders.this, OrderDetails.class);
        nextIntent.putExtra("orderid", myorders.get(position).orderId);
        nextIntent.putExtra("orderstatus", orderPla.getText().toString());
        nextIntent.putExtra("ordersn", Integer.valueOf(myorders.get(position).status));
        nextIntent.putExtra("shopname", myorders.get(position).sname);
        nextIntent.putExtra("totalprice", myorders.get(position).prices + "");
        nextIntent.putExtra("totalitems", myorders.get(position).items + "");
        startActivity(nextIntent);

      }
    });

    proD.show();
    String url = "app.vieworders?msisdn=" + pref.getString(Constants.mobile_num, "NA") + "&sidx=0&rows=10";
    UShopRestClient.getUAHOY(this, url, null, responseHandler);

  }

  class MyOrdersSub {

    String orderId;
    String sname;
    String sadd;
    String stype;
    String cty;
    String pin;
    int items;
    double prices;
    int dchrg;
    int status;
    String datetime;
  }

  // Handler invoked after getting order items from the api
  AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      // TODO Auto-generated method stub
      try {
        proD.dismiss();
        Toast.makeText(MyOrders.this, "Something went wrong", Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      // TODO Auto-generated method stub
      try {
        try {
          proD.dismiss();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        String responseString = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.getString("status").equalsIgnoreCase("Success")) {
          if (jsonObject.getJSONArray("orders").length() > 0) {
            myorders = new ArrayList<MyOrders.MyOrdersSub>();
            JSONArray myordersJ = jsonObject.getJSONArray("orders");
            for (int i = 0; i < myordersJ.length(); i++) {

              JSONObject myorderO = myordersJ.getJSONObject(i);
              MyOrdersSub myordersObj = new MyOrdersSub();
              myordersObj.orderId = myorderO.getString("orderId");
              myordersObj.sname = myorderO.getString("sname");
              myordersObj.sadd = myorderO.getString("sadd");
              myordersObj.stype = myorderO.getString("stype");
              myordersObj.cty = myorderO.getString("cty");
              myordersObj.pin = myorderO.getString("pin");
              myordersObj.items = myorderO.getInt("items");
              myordersObj.prices = myorderO.getDouble("prices");
              myordersObj.dchrg = myorderO.getInt("dchrg");
              myordersObj.status = myorderO.getInt("status");
              myordersObj.datetime = myorderO.getString("datetime");
              myorders.add(myordersObj);
            }

            if (myorders.size() > 0) {
              // Log.i(getClass().getSimpleName(), "Size is: " +
              // myorders.size());
              MySpinnerAdapter myorderAdapter = new MySpinnerAdapter(MyOrders.this, 0, myorders);
              listView.setAdapter(myorderAdapter);
            }

          } else {
            Toast.makeText(MyOrders.this, "No Orders Available", Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(MyOrders.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
}
