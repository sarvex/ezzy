package com.shopezzy;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopezzy.FullItems.Packing;
import com.shopezzy.FullItems.SubList;
import com.shopezzy.SearchItems.ItemsQuery;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class MoreItems extends AppCompatActivity {

  private ArrayList<SubList> fullList;
  private ArrayList<Packing> packing;
  String fullListS;
  int position;
  private Gson gson;
  private TextView itemName;
  private ImageView itemImage;
  private ListView listViewMore;
  static NotifyCallBack notify;
  private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
  int i = 0;

  // Function for getting Item Id Count.
  int getItemIdCount(int id) {
    if (itemsSArray != null && itemsSArray.size() > 0) {

      for (int i = 0; i < itemsSArray.size(); i++) {
        if (Integer.valueOf(itemsSArray.get(i).itemId) == id) {

          return Integer.valueOf(itemsSArray.get(i).itemNumber);
        }
      }
    }
    return 0;
  }


  // Function for adding items to the Array
  void addItemsSArray(int position, int id, int count) {

    if (count == 0) {
      Iterator<ItemsQuery> iterateItems = itemsSArray.iterator();
      int k1 = 0;
      outer:
      while (iterateItems.hasNext()) {
        ItemsQuery itemQuery = iterateItems.next();

        for (int j = 0; j < itemsSArray.size(); j++) {
          if (String.valueOf(id).equalsIgnoreCase(itemQuery.itemId)) {
            if (itemQuery != null)
              iterateItems.remove();
            break outer;
          }
        }

      }

      return;
    }

    for (int i = 0; i < itemsSArray.size(); i++) {

      if (Integer.valueOf(itemsSArray.get(i).itemId) == id) {
        itemsSArray.get(i).itemNumber = count + "";
        break;
      }

      if (i + 1 == itemsSArray.size()) {
        ItemsQuery itemsQu = new SearchItems().new ItemsQuery();
        itemsQu.itemId = fullList.get(this.position).packings.get(position).packid + "";
        itemsQu.itemImage = fullList.get(this.position).image;
        itemsQu.itemMrp = fullList.get(this.position).packings.get(position).mrp + "";
        itemsQu.itemName = fullList.get(this.position).item;
        itemsQu.itemNumber = "1";
        itemsQu.itemSp = fullList.get(this.position).packings.get(position).sellPrice + "";
        itemsQu.itemWt = fullList.get(this.position).packings.get(position).pack;
        itemsQu.offer = fullList.get(this.position).packings.get(position).offer;
        itemsSArray.add(itemsQu);

      }
    }

    if (itemsSArray.size() == 0) {
      ItemsQuery itemsQu = new SearchItems().new ItemsQuery();
      itemsQu.itemId = fullList.get(this.position).packings.get(position).packid + "";
      itemsQu.itemImage = fullList.get(this.position).image;
      itemsQu.itemMrp = fullList.get(this.position).packings.get(position).mrp + "";
      itemsQu.itemName = fullList.get(this.position).item;
      itemsQu.itemNumber = "1";
      itemsQu.itemSp = fullList.get(this.position).packings.get(position).sellPrice + "";
      itemsQu.itemWt = fullList.get(this.position).packings.get(position).pack;
      itemsQu.offer = fullList.get(this.position).packings.get(0).offer;
      itemsSArray.add(itemsQu);
    }
  }

  @Override
  public void onBackPressed() {

    if (this.i == 0) {
      Intent intent = new Intent();
      intent.putExtra("position", this.i);
      intent.putExtra("beforepos", position);
      setResult(1, intent);
      finish();
    } else {
      // this.i = this.i - 1;
      Intent intent = new Intent();
      intent.putExtra("position", this.i);
      intent.putExtra("beforepos", position);
      setResult(1, intent);
      finish();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.moreitems);

    if (Build.VERSION.SDK_INT >= 14) {

      this.setFinishOnTouchOutside(false);
    } else {
    }

    gson = new Gson();
    itemName = (TextView) findViewById(R.id.itemname);
    itemImage = (ImageView) findViewById(R.id.itemImage);
    listViewMore = (ListView) findViewById(R.id.moreitems);

    if (notify != null && notify.getUpdatedItems() != null) {
      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      itemsSArray = gson.fromJson(notify.getUpdatedItems(), collectionType);
    }

    if (getIntent().hasExtra("fullitems")) {
      fullListS = getIntent().getStringExtra("fullitems");
      position = getIntent().getIntExtra("position", -1);
      this.i = getIntent().getIntExtra("index", 0);

      Type collectionType = new TypeToken<ArrayList<SubList>>() {
      }.getType();
      fullList = gson.fromJson(fullListS, collectionType);
      try {

        Picasso.with(this).load(fullList.get(position).image).placeholder(R.drawable.default_image)
            .into(itemImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
      itemName.setText(fullList.get(position).item);
      packing = fullList.get(position).packings;
      CategorySubAdapter adapter = new CategorySubAdapter(this, 0, packing);
      listViewMore.setAdapter(adapter);

    }

  }

  boolean allow = true;

  // Adapter for displaying packings.
  private class CategorySubAdapter extends ArrayAdapter<Packing> {
    private Context mContext;
    private ArrayList<Packing> list;
    private int i = 0;

    public CategorySubAdapter(Context context, int resource, ArrayList<Packing> objects) {
      super(context, resource, objects);
      this.mContext = context;
      this.list = (ArrayList<Packing>) objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      View rowView = convertView;
      if (rowView == null) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.reviewsearchitemview3, null);
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.weight = (TextView) rowView.findViewById(R.id.weight);
        viewHolder.price = (TextView) rowView.findViewById(R.id.cost);
        viewHolder.price2 = (TextView) rowView.findViewById(R.id.cost1);
        viewHolder.offerpercent = (Button) rowView.findViewById(R.id.offerpecent);

        viewHolder.up = (Button) rowView.findViewById(R.id.add);
        viewHolder.down = (Button) rowView.findViewById(R.id.minus);
        viewHolder.count = (TextView) rowView.findViewById(R.id.number);
        viewHolder.more = (Button) rowView.findViewById(R.id.more);
        viewHolder.v1 = rowView.findViewById(R.id.v1);
        rowView.setTag(viewHolder);

      }

      final ViewHolder viewHolder = (ViewHolder) rowView.getTag();

      viewHolder.weight.setText(list.get(position).pack);


      if (list.get(position).mrp == list.get(position).sellPrice) {
        viewHolder.price.setText(
            this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).sellPrice);
        viewHolder.price2.setVisibility(View.GONE);
        viewHolder.v1.setVisibility(View.GONE);

      } else {
        viewHolder.v1.setVisibility(View.VISIBLE);
        viewHolder.price2.setVisibility(View.VISIBLE);
        viewHolder.price.setText(
            this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).sellPrice);
        viewHolder.price2
            .setText(this.mContext.getResources().getString(R.string.rs) + " " + list.get(position).mrp);
        viewHolder.price2.setPaintFlags(viewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }
      if (list.get(position).offer.length() > 0) {
        viewHolder.offerpercent.setVisibility(View.VISIBLE);

        viewHolder.offerpercent.setText(list.get(position).offer);
      } else {
        viewHolder.offerpercent.setVisibility(View.GONE);
      }

      viewHolder.count.setText("" + getItemIdCount(list.get(position).packid));

      viewHolder.up.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {

          if (notify != null && notify.getUpdatedItems() != null) {

            Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
            }.getType();
            itemsSArray = gson.fromJson(notify.getUpdatedItems(), collectionType);
          }

          int s = Integer.valueOf(viewHolder.count.getText().toString().trim());

          int s1 = 1;
          int main = s + s1;
          viewHolder.count.setText("" + main);
          MoreItems.this.i = position;
          list.get(position).itemnumber = main;
          if (notify != null)
            notify.passDataToIncrement("1");

          addItemsSArray(position, list.get(position).packid, main);

          if (notify != null) {
            notify.triggerAreas(gson.toJson(itemsSArray));
            // notify.callBackUpdatedResponse(gson.toJson(fullData));
          }

          if (main > 12) {
            if (allow) {
              // Toast.makeText(this, "Bulk orders may be denied",
              // Toast.LENGTH_SHORT).show();
              allow = false;
            }

          }

          try {
            if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

              // viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
            } else {
              // viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
            }
          } catch (NumberFormatException e1) {
            e1.printStackTrace();
          } catch (NotFoundException e1) {
            e1.printStackTrace();
          } catch (Exception e) {
          }

        }
      });

      viewHolder.down.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {

          if (notify != null && notify.getUpdatedItems() != null) {

            // notify.updateCart("");
            Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
            }.getType();
            itemsSArray = gson.fromJson(notify.getUpdatedItems(), collectionType);
          }

          int s = Integer.valueOf(viewHolder.count.getText().toString().trim());

          int s1 = 1;

          int main = s - s1;

          if (main >= 0) {
            if (notify != null)
              notify.passDataToDecrement("1");
            // linearViewN.startAnimation(moveCart);
            MoreItems.this.i = position;
            viewHolder.count.setText("" + main);
            addItemsSArray(position, list.get(position).packid, main);
            if (notify != null) {
              notify.triggerAreas(gson.toJson(itemsSArray));
              // notify.callBackUpdatedResponse(gson.toJson(fullData));
            }

            list.get(position).itemnumber = main;
          }

          try {
            if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

              // viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
            } else {
              // viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
            }
          } catch (NumberFormatException e1) {
            e1.printStackTrace();
          } catch (NotFoundException e1) {
            e1.printStackTrace();
          } catch (Exception e) {
          }

        }
      });

      return rowView;
    }
  }

  // View Holder for Holding items.
  static class ViewHolder {

    private TextView weight;
    private TextView price;
    private Button down;
    private Button up;
    private TextView count;
    private Button offerpercent;
    private TextView price2;
    private View v1;
    private Button more;

  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  protected void onResume() {
    super.onResume();
  }

  ;

}
