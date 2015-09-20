package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

public class CitySelection extends AppCompatActivity {

  private String url = "getCountryDetail?country=india";
  private String getLocality = "getlocality?country=india&city=";
  private String getStoresbyPin = "getstoresbypin?pin=";
  private ProgressDialog proD, proDi;
  private TextView pickCity, searchHere, or, pickFromBelow;
  private View view1, view2;
  private ListView listView;
  MySpinnerAdapter mySpinnerAdapter;
  String selectedArea, selectedCity, selectedPin;
  private Gson gson;
  AllCities allCities = null;
  SharedPreferences pref;
  CityLocality cityLocality;
  int i = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.cityselectionnew);
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    ActionBar actionBar = getSupportActionBar();
    actionBar.hide();
    gson = new Gson();
    pickCity = (TextView) findViewById(R.id.txtView1);
    searchHere = (TextView) findViewById(R.id.txtView2);
    pickFromBelow = (TextView) findViewById(R.id.txtView4);
    or = (TextView) findViewById(R.id.txtView3);
    view1 = findViewById(R.id.view1);
    view2 = findViewById(R.id.view2);
    listView = (ListView) findViewById(R.id.listview);
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Locality locality = CitySelection.this.mySpinnerAdapter.locality.get(position);
        selectedArea = locality.name;
        proD.setMessage("Fetching available stores...");
        proD.show();
        selectedPin = locality.pin;
        UShopRestClient.getUAHOY(CitySelection.this, getStoresbyPin + locality.pin, null, responseHandlerPin);
      }
    });

    searchHere.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (cityLocality != null && cityLocality.localities != null && cityLocality.localities.size() > 0) {
          Intent nextIntent = new Intent(CitySelection.this, SearchLocality.class);
          nextIntent.putExtra("selectedcity", selectedCity);
          nextIntent.putExtra("pin", selectedPin);
          nextIntent.putExtra("localities", gson.toJson(cityLocality));
          startActivity(nextIntent);
        }

      }
    });

    pickCity.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        if (allCities != null && allCities.cities.size() > 0) {
          Intent nextIntent = new Intent(CitySelection.this, PickCity.class);
          nextIntent.putExtra("allcities", gson.toJson(allCities));
          // PickCity.notify = CitySelection.this;
          startActivityForResult(nextIntent, 1);
        }

      }
    });

    proD = new ProgressDialog(CitySelection.this);
    proD.setMessage("please wait...");
    proD.setCancelable(false);

    if (!pref.getString(Constants.pref_locality, "NA").equalsIgnoreCase("NA")) {

      allCities = gson.fromJson(pref.getString(Constants.pref_cities, "NA"), AllCities.class);
      cityLocality = gson.fromJson(pref.getString(Constants.pref_locality, "NA"), CityLocality.class);
      selectedCity = pref.getString(Constants.pref_selectedCity, "NA");
      pickCity.setText(selectedCity);

      mySpinnerAdapter = new MySpinnerAdapter(CitySelection.this, 0, cityLocality.localities);
      view1.setVisibility(View.VISIBLE);
      searchHere.setVisibility(View.VISIBLE);
      view2.setVisibility(View.VISIBLE);
      or.setVisibility(View.VISIBLE);
      pickFromBelow.setVisibility(View.VISIBLE);
      listView.setAdapter(mySpinnerAdapter);

      proDi = new ProgressDialog(CitySelection.this);
      proDi.setMessage("please wait...");
      proDi.setCancelable(false);
      proDi.show();
      UShopRestClient.getUAHOY(CitySelection.this, url, null, responseHandlerMain);
      UShopRestClient.getUAHOY(CitySelection.this, getLocality + selectedCity + "&status=0", null,
          responseHandlerLocality);

      new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {
          try {
            proDi.dismiss();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, 50000);

    } else {
      proD.show();
      UShopRestClient.getUAHOY(CitySelection.this, url, null, responseHandlerMain);
    }

  }

  // Handler invokes after fetching stores using pin.
  AsyncHttpResponseHandler responseHandlerPin = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      try {
        proD.dismiss();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      try {
        String response = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(response);
        TotalStores totalStores = new TotalStores();
        totalStores.pin = jsonObject.getString("pin");
        totalStores.area = jsonObject.getString("area");
        totalStores.city = jsonObject.getString("city");
        ArrayList<Store> stores = new ArrayList<CitySelection.Store>();
        if (jsonObject.getJSONArray("stores").length() > 0) {
          for (int i = 0; i < jsonObject.getJSONArray("stores").length(); i++) {
            JSONObject storeObject = jsonObject.getJSONArray("stores").getJSONObject(i);
            Store store = new Store();
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
          Gson gson = new Gson();
          final String json = gson.toJson(totalStores);

          try {
            proD.dismiss();
          } catch (Exception e) {
            e.printStackTrace();
          }

          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              Intent nextIntent = new Intent(CitySelection.this, PickStore.class);
              nextIntent.putExtra("stores", json);
              pref.edit().putString(Constants.selectedCity, selectedCity).commit();
              pref.edit().putString(Constants.selectedLocality, selectedArea).commit();
              pref.edit().putString(Constants.selectedPin, selectedPin).commit();
              nextIntent.putExtra("area", selectedArea);
              startActivity(nextIntent);
            }
          }, 200);

        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  };

  //Handler invoked after fetching the localities.
  AsyncHttpResponseHandler responseHandlerLocality = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      try {
        proD.cancel();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        proDi.dismiss();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
      try {
        i++;
        String response = new String(arg2, "UTF-8");
        ArrayList<CityLocality> cityLocalityA = new ArrayList<CitySelection.CityLocality>();
        JSONObject jsonObject = new JSONObject(response);
        cityLocality = new CityLocality();
        cityLocality.id = jsonObject.getString("id");
        cityLocality.city = jsonObject.getString("city");
        cityLocality.country = jsonObject.getString("country");
        ArrayList<Locality> newLocalities = new ArrayList<CitySelection.Locality>();
        if (jsonObject.getJSONArray("locality").length() > 0) {
          for (int i = 0; i < jsonObject.getJSONArray("locality").length(); i++) {
            Locality locality = new Locality();
            locality.id = jsonObject.getJSONArray("locality").getJSONObject(i).getString("id");
            locality.name = jsonObject.getJSONArray("locality").getJSONObject(i).getString("name");
            locality.pin = jsonObject.getJSONArray("locality").getJSONObject(i).getString("pin");
            locality.svc = jsonObject.getJSONArray("locality").getJSONObject(i).getString("svc");
            newLocalities.add(locality);
          }
          // city
          cityLocality.localities = newLocalities;
          cityLocalityA.add(cityLocality);
          pref.edit().putString(Constants.pref_selectedCity, selectedCity).commit();
          pref.edit().putString(Constants.pref_locality, gson.toJson(cityLocality)).commit();

          view1.setVisibility(View.VISIBLE);
          searchHere.setVisibility(View.VISIBLE);
          view2.setVisibility(View.VISIBLE);
          or.setVisibility(View.VISIBLE);
          pickFromBelow.setVisibility(View.VISIBLE);

          mySpinnerAdapter = new MySpinnerAdapter(CitySelection.this, 0, cityLocality.localities);
          listView.setAdapter(mySpinnerAdapter);

        } else {
          if (mySpinnerAdapter != null) {
            mySpinnerAdapter.locality.clear();

            mySpinnerAdapter.notifyDataSetChanged();

          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      if (i == 2) {
        try {
          proDi.dismiss();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      try {
        proD.dismiss();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  };

  // Handler invoked after invoking getcountry detail api.
  AsyncHttpResponseHandler responseHandlerMain = new AsyncHttpResponseHandler() {

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
      try {
        proD.cancel();

      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        proDi.cancel();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

      i++;
      try {
        ArrayList<String> allcities = new ArrayList<String>();
        String response = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(response);
        allCities = new AllCities();
        allCities.country = jsonObject.getString("country");
        if (jsonObject.getJSONArray("cityList").length() != 0) {
          for (int i = 0; i < jsonObject.getJSONArray("cityList").length(); i++) {
            allcities.add(jsonObject.getJSONArray("cityList").getString(i));

          }
          allCities.cities = allcities;
          pref.edit().putString(Constants.pref_cities, gson.toJson(allCities)).commit();
        }
        try {

          proD.dismiss();
        } catch (Exception e) {
          e.printStackTrace();
        }

        try {
          if (i == 2)
            proDi.dismiss();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (Exception e) {
        try {

          proD.dismiss();
        } catch (Exception e1) {
          e1.printStackTrace();
        }

        try {
          if (i == 2)
            proDi.dismiss();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        e.printStackTrace();
      }

      try {

        if (proDi != null) {
          return;
        }
        new Handler().postDelayed(new Runnable() {

          @Override
          public void run() {
            if (allCities != null && allCities.cities.size() > 0) {
              if (selectedCity == null) {
                Intent nextIntent = new Intent(CitySelection.this, PickCity.class);
                nextIntent.putExtra("allcities", gson.toJson(allCities));
                startActivityForResult(nextIntent, 1);
                return;
              } else if (selectedCity.length() == 0) {
                Intent nextIntent = new Intent(CitySelection.this, PickCity.class);
                nextIntent.putExtra("allcities", gson.toJson(allCities));
                startActivityForResult(nextIntent, 1);
                return;
              }

            }
          }
        }, 200);

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  };

  @Override
  protected void onResume() {
    super.onResume();
    if (this.mySpinnerAdapter != null) {
      this.mySpinnerAdapter.notifyDataSetChanged();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  static class ViewHolder {

    private TextView countrytxt;
    private FrameLayout layout;

  }

  public class AllCities {
    public String country;
    public ArrayList<String> cities;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1) {

      if (data != null && data.hasExtra("city")) {
        final String city = data.getStringExtra("city");
        selectedCity = city;
        pickCity.setText(city);

        new Handler().postDelayed(new Runnable() {

          @Override
          public void run() {
            proD.setMessage("Fetching Localities...");
            proD.show();
            UShopRestClient.getUAHOY(CitySelection.this, getLocality + city + "&status=0", null,
                responseHandlerLocality);
          }
        }, 600);

      }
    }

  }

  public class MySpinnerAdapter extends ArrayAdapter<Locality> {
    private Context mContext;
    ArrayList<Locality> locality;
    private int i = 0;

    @Override
    public int getCount() {
      return locality.size();
    }

    public MySpinnerAdapter(Context context, int resource, ArrayList<Locality> objects) {
      super(context, resource, objects);
      this.mContext = context;
      this.locality = objects;

    }

    public void setLocality(ArrayList<Locality> obJects) {
      this.locality = obJects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View rowView = convertView;
      if (rowView == null) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.locationdisplay, null);
        ViewHolderLocality viewHolder = new ViewHolderLocality();
        viewHolder.countrytxt = (TextView) rowView.findViewById(R.id.locationitem);
        viewHolder.layout = (FrameLayout) rowView.findViewById(R.id.frame);
        rowView.setTag(viewHolder);

      }
      final ViewHolderLocality viewHolder = (ViewHolderLocality) rowView.getTag();
      viewHolder.countrytxt.setText(locality.get(position).name);
      return rowView;
    }
  }

  static class ViewHolderLocality {

    private TextView countrytxt;
    private FrameLayout layout;

  }

  public class Locality {
    String id;
    String name;
    String pin;
    String svc;
  }

  public class CityLocality {
    String id;
    String city;
    String country;
    ArrayList<Locality> localities;

  }

  public class Store {

    String id;
    String name;
    String type;
    String add;
    String city;
    String pin;
    String min;
    String dchrg;
    String pmode;
    String dtime;
    String imgurl;
    String rtng;
  }

  public class TotalStores {
    String pin;
    String city;
    String area;
    ArrayList<Store> stores;

  }

}
