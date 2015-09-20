package com.shopezzy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopezzy.SearchItems.ItemsQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class SearchItemReview extends AppCompatActivity {

  Typeface tff;
  private Toolbar toolbar;
  private TextView pageTitle;
  private Button addmore;
  private ListView listView;
  private TextView cartAmount, itemsNumber;
  private ImageView forward;
  private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
  private Gson gson;
  float cartAmountS;
  int numberOfItems;
  private ListView listview;
  private SearchAdapter searchAdapter;
  private String storeName;
  String storeId;
  String storeAddress;
  String minorder;
  private SharedPreferences pref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reviewsearch);
    tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");
    toolbar = getSupportActionBar();
    gson = new Gson();
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
    toolbar.setBackgroundDrawable(colorDrawable);
    View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);
    pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);

    if (getIntent().hasExtra("storename")) {
      storeName = getIntent().getStringExtra("storename");
      storeId = getIntent().getStringExtra("storeid");
      storeAddress = getIntent().getStringExtra("storeaddress");
      minorder = getIntent().getStringExtra("minorder");
    }

    pageTitle.setText("My Cart");
    addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
    addmore.setTypeface(tff);

    toolbar.setDisplayShowCustomEnabled(true);
    toolbar.setDisplayShowTitleEnabled(false);
    toolbar.setCustomView(actionbarView);
    toolbar.setDisplayHomeAsUpEnabled(true);

    addmore.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent backIntent = getIntent();
        String updatedJson = gson.toJson(itemsSArray);
        backIntent.putExtra("updatedJson", updatedJson);
        setResult(1, backIntent);
        SearchItemReview.this.finish();

      }
    });
    listView = (ListView) findViewById(R.id.searchlist);
    cartAmount = (TextView) findViewById(R.id.cartamount);
    itemsNumber = (TextView) findViewById(R.id.itemsnumber);
    forward = (ImageView) findViewById(R.id.forward);
    forward.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        try {
          if (itemsSArray != null && itemsSArray.size() == 0) {
            Toast.makeText(SearchItemReview.this, "Sorry no items in your cart", Toast.LENGTH_SHORT).show();
            return;
          }

          if (Float.valueOf(cartAmount.getText().toString().split(" ")[1]) < Float.valueOf(minorder)) {

            Toast.makeText(SearchItemReview.this, "Minimum order is Rs " + minorder, Toast.LENGTH_SHORT)
                .show();
            return;
          }

          String jsonString = pref.getString(Constants.viewuser, "NA");

          if (jsonString.equalsIgnoreCase("NA")) {
            String updatedJson = gson.toJson(itemsSArray);
            Intent nextIntent = new Intent(SearchItemReview.this, AddAddress.class);
            nextIntent.putExtra("updatedJson", updatedJson);
            nextIntent.putExtra("storename", storeName);
            nextIntent.putExtra("storeid", storeId);
            nextIntent.putExtra("storeaddress", storeAddress);
            nextIntent.putExtra("totalitems", itemsNumber.getText().toString());
            nextIntent.putExtra("totalprice", cartAmount.getText().toString());
            startActivity(nextIntent);
          } else if (!new JSONObject(jsonString).getString("locality").toString()
              .equals(pref.getString(Constants.selectedLocality, "NA"))) {
            String updatedJson = gson.toJson(itemsSArray);
            Intent nextIntent = new Intent(SearchItemReview.this, AddAddress.class);
            nextIntent.putExtra("updatedJson", updatedJson);
            nextIntent.putExtra("storename", storeName);
            nextIntent.putExtra("storeid", storeId);
            nextIntent.putExtra("storeaddress", storeAddress);
            nextIntent.putExtra("totalitems", itemsNumber.getText().toString());
            nextIntent.putExtra("totalprice", cartAmount.getText().toString());
            nextIntent.putExtra("clear", "yes");
            startActivity(nextIntent);

          } else {
            Intent nextIntent = new Intent(SearchItemReview.this, Checkout.class);
            String updatedJson = gson.toJson(itemsSArray);
            nextIntent.putExtra("updatedJson", updatedJson);
            nextIntent.putExtra("storename", storeName);
            nextIntent.putExtra("storeid", storeId);
            nextIntent.putExtra("storeaddress", storeAddress);
            nextIntent.putExtra("totalitems", itemsNumber.getText().toString());
            nextIntent.putExtra("totalprice", cartAmount.getText().toString());
            startActivity(nextIntent);
          }
        } catch (NumberFormatException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });

    if (getIntent().hasExtra("items")) {
      String itemsS = getIntent().getStringExtra("items");
      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      itemsSArray = gson.fromJson(itemsS, collectionType);

    }

    if (savedInstanceState != null) {

      storeId = savedInstanceState.getString("storeid");
      storeName = savedInstanceState.getString("storename");
      storeAddress = savedInstanceState.getString("storeaddress");
      minorder = savedInstanceState.getString("minorder");
      String items = savedInstanceState.getString("items");

      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      if (itemsSArray != null && itemsSArray.size() > 0)
        itemsSArray.clear();
      itemsSArray = gson.fromJson(items, collectionType);

    }

    int itemNumber = 0;

    if (itemsSArray != null) {
      for (int i = 0; i < itemsSArray.size(); i++) {
        ItemsQuery itemsQ = itemsSArray.get(i);
        String itemsNumber = itemsQ.itemNumber;
        cartAmountS = cartAmountS + (Float.valueOf(itemsQ.itemSp) * Integer.valueOf(itemsNumber));
        itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);

      }

      cartAmount.setText(getResources().getString(R.string.rs) + " " + cartAmountS);

      itemsNumber.setText("" + itemNumber + " items");
      listview = (ListView) findViewById(R.id.listview);

      searchAdapter = new SearchAdapter(this, 0, itemsSArray);
      listView.setAdapter(searchAdapter);
    }

    // viewHolder.price.setText(this.mContext.getResources().getString(
    // R.string.rs)
    // + " " + list.get(position).itemSp);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        Intent backIntent = SearchItemReview.this.getIntent();
        String updatedJson = gson.toJson(itemsSArray);
        backIntent.putExtra("updatedJson", updatedJson);
        setResult(1, backIntent);
        SearchItemReview.this.finish();
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    Intent backIntent = getIntent();
    String updatedJson = gson.toJson(itemsSArray);
    backIntent.putExtra("updatedJson", updatedJson);
    setResult(1, backIntent);
    SearchItemReview.this.finish();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("storeid", storeId);
    outState.putString("storename", storeName);
    outState.putString("storeaddress", storeAddress);
    outState.putString("minorder", minorder);
    outState.putString("items", gson.toJson(itemsSArray));
  }

  boolean allow = true;

  // Adapter for displaying items.
  private class SearchAdapter extends ArrayAdapter<ItemsQuery> {
    private Context mContext;
    private ArrayList<ItemsQuery> list;
    private int i = 0;

    public SearchAdapter(Context context, int resource, ArrayList<ItemsQuery> objects) {
      super(context, resource, objects);
      this.mContext = context;
      this.list = (ArrayList<ItemsQuery>) objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      View rowView = convertView;
      if (rowView == null) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.reviewsearchitemview2, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.itemName = (TextView) rowView.findViewById(R.id.itemname);
        viewHolder.weight = (TextView) rowView.findViewById(R.id.weight);
        viewHolder.price = (TextView) rowView.findViewById(R.id.cost);
        viewHolder.price2 = (TextView) rowView.findViewById(R.id.cost1);
        viewHolder.offerpercent = (Button) rowView.findViewById(R.id.offerpecent);
        viewHolder.image1 = (ImageView) rowView.findViewById(R.id.image1);
        viewHolder.up = (Button) rowView.findViewById(R.id.add);
        viewHolder.down = (Button) rowView.findViewById(R.id.minus);
        viewHolder.count = (TextView) rowView.findViewById(R.id.number);
        viewHolder.v1 = rowView.findViewById(R.id.v1);
        rowView.setTag(viewHolder);

      }

      final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
      viewHolder.itemName.setText(list.get(position).itemName);
      viewHolder.weight.setText(list.get(position).itemWt);
      // viewHolder.price
      // .setText(this.mContext.getResources().getString(R.string.rs) + "
      // " + list.get(position).itemSp);
      if (list.get(position).itemMrp.equalsIgnoreCase(list.get(position).itemSp)) {
        viewHolder.price
            .setText(this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).itemSp);
        viewHolder.price2.setVisibility(View.GONE);
        viewHolder.v1.setVisibility(View.GONE);
      } else {
        viewHolder.v1.setVisibility(View.VISIBLE);
        viewHolder.price2.setVisibility(View.VISIBLE);
        viewHolder.price
            .setText(this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).itemSp);
        viewHolder.price2.setText(
            this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).itemMrp);
        viewHolder.price2.setPaintFlags(viewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }
      if (list.get(position).offer.length() > 0) {
        viewHolder.offerpercent.setVisibility(View.VISIBLE);

        viewHolder.offerpercent.setText(list.get(position).offer);
      } else {
        viewHolder.offerpercent.setVisibility(View.GONE);
      }
      viewHolder.count.setText(list.get(position).itemNumber);

      try {
        if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

          viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
        } else {
          viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
        }
      } catch (NumberFormatException e1) {
        e1.printStackTrace();
      } catch (NotFoundException e1) {
        e1.printStackTrace();
      }

      try {

        Picasso.with(SearchItemReview.this).load(list.get(position).itemImage)
            .placeholder(R.drawable.default_image).into(viewHolder.image1);
      } catch (Exception e) {
        e.printStackTrace();
      }
      viewHolder.up.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          int s = Integer.valueOf(viewHolder.count.getText().toString().trim());
          itemsNumber.setText(
              "" + (Integer.valueOf(itemsNumber.getText().toString().split(" ")[0]) + 1) + " items");
          int s1 = 1;
          float amount = Float.valueOf(cartAmount.getText().toString().split(" ")[1]);
          float newAmount = amount + Float.valueOf(itemsSArray.get(position).itemSp);
          cartAmount.setText(mContext.getResources().getString(R.string.rs) + " " + newAmount);
          int main = s + s1;
          viewHolder.count.setText("" + main);
          list.get(position).itemNumber = "" + main;

          if (main > 12) {
            if (allow) {
              Toast.makeText(SearchItemReview.this, "Bulk orders may be denied", Toast.LENGTH_SHORT)
                  .show();
              allow = false;
            }

          }
        }
      });

      viewHolder.down.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {

          int s = Integer.valueOf(viewHolder.count.getText().toString().trim());
          itemsNumber.setText(
              "" + (Integer.valueOf(itemsNumber.getText().toString().split(" ")[0]) - 1) + " items");
          int s1 = 1;

          int main = s - s1;

          viewHolder.count.setText("" + main);
          list.get(position).itemNumber = "" + main;

          float amount = Float.valueOf(cartAmount.getText().toString().split(" ")[1]);
          float newAmount = amount - Float.valueOf(itemsSArray.get(position).itemSp);
          cartAmount.setText(mContext.getResources().getString(R.string.rs) + " " + newAmount);

          if (main == 0) {
            Iterator<ItemsQuery> iterateItems = list.iterator();
            int k1 = 0;
            while (iterateItems.hasNext()) {
              ItemsQuery itemQuery = iterateItems.next();
              if (list.get(position).itemId.equalsIgnoreCase(itemQuery.itemId)) {
                iterateItems.remove();
                searchAdapter.notifyDataSetChanged();

                Toast.makeText(SearchItemReview.this, "Item Removed", Toast.LENGTH_SHORT).show();

                break;
              }
            }
          }

        }
      });
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

  static class ViewHolder {

    private TextView itemName;
    private TextView weight;
    private TextView price;
    private ImageView image1;
    private Button down;
    private Button up;
    private TextView count;
    private Button offerpercent;
    private TextView price2;
    private View v1;

  }
}
