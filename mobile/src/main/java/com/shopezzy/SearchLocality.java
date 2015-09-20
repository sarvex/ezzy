package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shopezzy.CitySelection.CityLocality;
import com.shopezzy.CitySelection.Locality;
import com.shopezzy.CitySelection.MySpinnerAdapter;
import com.shopezzy.CitySelection.Store;
import com.shopezzy.CitySelection.TotalStores;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchLocality extends AppCompatActivity implements SearchView.OnQueryTextListener {

  private SearchView mSearchView;
  private ListView mListView;
  private ArrayAdapter<String> mAdapter;
  private ActionBar mActionBar;
  private android.widget.TextView text, title;
  private ImageView searchView;
  private ImageView searchCloseIcon, searchHintIcon;
  private MySpinnerAdapter searchAdapter;
  private ArrayList<Locality> localityLocal;
  private ProgressDialog proD;
  private String getStoresbyPin = "getstoresbypin?pin=";
  private String selectedArea;
  private String selectedCity;
  private String selectedPin;
  private SharedPreferences pref;
  private Gson gson;
  CityLocality localities;

  // Function for setting up the Search View
  private void setupSearchView() {
    mSearchView.requestFocus();
    mSearchView.setIconifiedByDefault(false);
    mSearchView.setOnQueryTextListener(this);
    mSearchView.setSubmitButtonEnabled(false);
    mSearchView.setQueryHint("Type Your Locality");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.searchlocality);

    // if (USHOP.launch) {
    //
    // Intent intent = new Intent(this, SplashScreen.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    // startActivity(intent);
    // }

    pref = PreferenceManager.getDefaultSharedPreferences(this);
    mActionBar = getSupportActionBar();
    mActionBar.hide();
    proD = new ProgressDialog(SearchLocality.this);
    proD.setMessage("Fetching available stores...");
    proD.setCancelable(false);

    gson = new Gson();
    mSearchView = (SearchView) findViewById(R.id.search_view);
    int searchcloseicon = getResources().getIdentifier("android:id/search_close_btn", null, null);
    searchCloseIcon = (ImageView) mSearchView.findViewById(searchcloseicon);
    searchCloseIcon.setImageResource(R.drawable.ic_clear_normal);

    int searchhinticon = getResources().getIdentifier("android:id/search_mag_icon", null, null);
    searchHintIcon = (ImageView) mSearchView.findViewById(searchhinticon);
    searchHintIcon.setImageResource(R.drawable.ic_search);
    int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
    android.widget.EditText searchEditText = (android.widget.EditText) mSearchView.findViewById(searchSrcTextId);
    searchEditText.setTextColor(Color.WHITE);
    searchEditText.setHintTextColor(getResources().getColor(R.color.hintcolor));

    int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null,
        null);
    View searchPlateView = mSearchView.findViewById(searchPlateId);
    if (searchPlateView != null) {
      searchPlateView.setBackgroundResource(R.drawable.searchfield);
    }

    mListView = (ListView) findViewById(R.id.listviewSearch);
    mListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Locality locality = SearchLocality.this.searchAdapter.locality.get(position);
        selectedArea = locality.name;
        selectedPin = locality.pin;
        proD.setMessage("Fetching available stores...");
        proD.show();
        UShopRestClient.getUAHOY(SearchLocality.this, getStoresbyPin + locality.pin, null, responseHandlerPin);
      }
    });
    setupSearchView();
    selectedCity = getIntent().getStringExtra("selectedcity");
    selectedPin = getIntent().getStringExtra("pin");
    // Log.i(getClass().getSimpleName(), "PIN: " + selectedPin);
    localities = gson.fromJson(getIntent().getStringExtra("localities"), CityLocality.class);
    ArrayList<Locality> allLoca = localities.localities;
    searchAdapter = new CitySelection().new MySpinnerAdapter(this, 0, allLoca);
    mListView.setAdapter(searchAdapter);
    localityLocal = new ArrayList<CitySelection.Locality>();
    localityLocal.addAll(localities.localities);

  }


  // Handler invoked after fecting the stores by PIN.
  AsyncHttpResponseHandler responseHandlerPin = new AsyncHttpResponseHandler() {

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
        String response = new String(arg2, "UTF-8");
        JSONObject jsonObject = new JSONObject(response);
        TotalStores totalStores = new CitySelection().new TotalStores();
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
          Gson gson = new Gson();
          final String json = gson.toJson(totalStores);

          // Log.i(getClass().getSimpleName(), "J-**" + json + "--"
          // + stores.size());
          try {
            proD.dismiss();
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              // TODO Auto-generated method stub
              Intent nextIntent = new Intent(SearchLocality.this, PickStore.class);
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  };

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    this.searchAdapter.locality.clear();
    this.searchAdapter.locality.addAll(localityLocal);

  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    // TODO Auto-generated method stub

    if (localities != null && localities.localities != null && localities.localities.size() > 0) {
      localities.localities.clear();
      localities.localities.addAll(localityLocal);
      // Log.i(getClass().getSimpleName(),
      // "Size: " + localities.localities.size() + "-"
      // + localityLocal.size() + "-"
      // + searchAdapter.locality.size());
      // SearchLocality.this.searchAdapter.notifyDataSetChanged();
      if (newText.length() == 0) {
        SearchLocality.this.searchAdapter.notifyDataSetChanged();
        return true;
      }
    }

    SearchLocality.this.searchAdapter.locality.clear();
    for (int i = 0; i < localityLocal.size(); i++) {
      Locality dataNames = localityLocal.get(i);
      if (dataNames.name.toLowerCase().contains(newText.toString())) {
        // SearchLocality.this.searchAdapter.locality.clear();
        SearchLocality.this.searchAdapter.locality.add(dataNames);
        SearchLocality.this.searchAdapter.notifyDataSetChanged();
      } else {

        SearchLocality.this.searchAdapter.notifyDataSetChanged();
      }
    }
    // SearchLocality.this.searchAdapter.locality.clear();
    // SearchLocality.this.searchAdapter.locality = ((USHOP)
    // getApplicationContext()).locality;

    // SearchLocality.this.searchAdapter.getFilter().filter(newText);

    return true;
  }

}
