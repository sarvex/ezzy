package com.shopezzy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shopezzy.FullItems.FullItemsData;
import com.shopezzy.FullItems.SubList;
import com.shopezzy.SearchItems.ItemsQuery;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class CategoriesFragment extends Fragment {

  private static final String ARG_TEXT = "com.shopezzy.CategoriesFragment.text";
  private boolean loaded = false;
  public String category, categoryId, allItems;
  private Gson gson;
  private ArrayList<SubList> fullList;
  private CategorySubAdapter categoryAdapter;
  static NotifyCallBack notify;
  private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
  FullItemsData fullData;
  int index = 0;
  int beforepos = -1;

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

  void addItemsSArray(int position, int id, int count) {

    if (count == 0) {
//			 Log.i(getClass().getSimpleName(), "Count is zero");
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

      // Log.i(getClass().getSimpleName(), "Here in for loop");
      if (Integer.valueOf(itemsSArray.get(i).itemId) == id) {
        itemsSArray.get(i).itemNumber = count + "";
        break;
      }

      if (i + 1 == itemsSArray.size()) {
        // Log.i(getClass().getSimpleName(), "i-1");
        ItemsQuery itemsQu = new SearchItems().new ItemsQuery();
        itemsQu.itemId = fullList.get(position).packings.get(0).packid + "";
        itemsQu.itemImage = fullList.get(position).image;
        itemsQu.itemMrp = fullList.get(position).packings.get(0).mrp + "";
        itemsQu.itemName = fullList.get(position).item;
        itemsQu.itemNumber = "1";
        itemsQu.itemSp = fullList.get(position).packings.get(0).sellPrice + "";
        itemsQu.itemWt = fullList.get(position).packings.get(0).pack;
        itemsQu.offer = fullList.get(position).packings.get(0).offer;
        itemsSArray.add(itemsQu);

      }
    }

    if (itemsSArray.size() == 0) {
      // Log.i(getClass().getSimpleName(), "i == 0");
      ItemsQuery itemsQu = new SearchItems().new ItemsQuery();
      itemsQu.itemId = fullList.get(position).packings.get(0).packid + "";
      itemsQu.itemImage = fullList.get(position).image;
      itemsQu.itemMrp = fullList.get(position).packings.get(0).mrp + "";
      itemsQu.itemName = fullList.get(position).item;
      itemsQu.itemNumber = "1";
      itemsQu.itemSp = fullList.get(position).packings.get(0).sellPrice + "";
      itemsQu.itemWt = fullList.get(position).packings.get(0).pack;
      itemsQu.offer = fullList.get(position).packings.get(0).offer;
      itemsSArray.add(itemsQu);
    }
  }

  private Animation moveCart, moveCart1, moveCart2;
  final int toLoc[] = new int[2];
  ImageView imageW = null;
  ImageView image = null;
  LinearLayout linearViewN = null;
  AnimationListener listener = new AnimationListener() {

    @Override
    public void onAnimationStart(Animation animation) {
      // TODO Auto-generated method stub
      if (imageW != null) {
        // Log.i(getClass().getSimpleName(), "ImageW is not null");
        // imageW.setVisibility(View.VISIBLE);
      } else {
        // Log.i(getClass().getSimpleName(), "ImageW is null");
      }

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
      if (imageW != null) {
        // Log.i(getClass().getSimpleName(), "ImageW is not null");
        // imageW.setVisibility(View.VISIBLE);
      } else {
        // Log.i(getClass().getSimpleName(), "ImageW is null");
      }

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

  public static CategoriesFragment newInstance(String tag) {

    System.out.println("New Instance Created Fragment");
    CategoriesFragment rechargeFragment = new CategoriesFragment();

    // category = tag;
    Bundle args = new Bundle();
    args.putString(ARG_TEXT, tag);
    rechargeFragment.setArguments(args);
    return rechargeFragment;
  }

  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    // Log.i(getClass().getSimpleName(), "Attached to Activity");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    // Log.i(getClass().getSimpleName(), "OnCreate");
    MoreItems.notify = notify;
    category = getArguments().getString("category").split(",")[0];
    categoryId = getArguments().getString("category").split(",")[1];
    allItems = getArguments().getString("allcat");
    gson = new Gson();

  }

  View view = null;
  private ListView listview;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    // Log.i(getClass().getSimpleName(), "Here...View");

    if (notify != null && notify.getUpdatedItems() != null) {
      // Log.i(getClass().getSimpleName(), "saveinstanceinstate");
      // allItems = notify.getUpdatedResponse();
      // Log.i(getClass().getSimpleName(), "Items in CreateView: " +
      // notify.getUpdatedItems());
      Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
      }.getType();
      itemsSArray = gson.fromJson(notify.getUpdatedItems(), collectionType);

      // Log.i(getClass().getSimpleName(), "Items Size: " +
      // itemsSArray.size());
    }
    if (view == null) {
      // Log.i(getClass().getSimpleName(), "If");
      view = inflater.inflate(R.layout.fullitems, container, false);
      view.setBackgroundColor(Color.WHITE);
      listview = (ListView) view.findViewById(R.id.catlist);
    } else {

      // Log.i(getClass().getSimpleName(), "Else");
      ((ViewGroup) view.getParent()).removeView(view);
    }

    return view;
  }

  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
//		Log.i(getClass().getSimpleName(), "in OnResume");
    if (categoryAdapter != null) {

      if (notify != null && notify.getUpdatedItems() != null) {

        notify.updateCart("");
        Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
        }.getType();
        itemsSArray = gson.fromJson(notify.getUpdatedItems(), collectionType);

        // Log.i(getClass().getSimpleName(), "in onResume the size is: "
        // + itemsSArray.size());

      }
      categoryAdapter.notifyDataSetChanged();
    }

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1) {
//			Log.i(getClass().getSimpleName(), "In The Result");
      if (data != null && data.hasExtra("position")) {
        index = data.getIntExtra("position", 0);
        beforepos = data.getIntExtra("beforepos", 0);
      }

      notify.callBackUpdatedResponse(gson.toJson(fullData));
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onActivityCreated(savedInstanceState);
    if (!loaded) {
      loaded = true;

      // Log.i(getClass().getSimpleName(), "Response: ****");

      moveCart = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.zoomin);
      moveCart.setAnimationListener(listener);
      moveCart1 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.zoomout);
      moveCart1.setAnimationListener(listener1);
      View viewCustomA = getActivity().getSupportActionBar().getCustomView();
      linearViewN = (LinearLayout) viewCustomA.findViewById(R.id.circle1);

      fullData = gson.fromJson(allItems, FullItemsData.class);

      if (fullData != null)
        for (int i = 0; fullData.list.size() > 0; i++) {
          if (fullData.list.get(i).subcatid == Integer.valueOf(categoryId)) {

            fullList = new ArrayList<FullItems.SubList>();
            fullList.addAll(fullData.list.get(i).items);
            break;
          }
        }

      if (fullList != null) {

//				Log.i(getClass().getSimpleName(), "Size is: " + fullList.size());
        categoryAdapter = new CategorySubAdapter(getActivity(), 0, fullList);
        listview.setAdapter(categoryAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            Log.i(getClass().getSimpleName(), "Here....");

            Intent nextIntent = new Intent(getActivity(), MoreItems.class);
            nextIntent.putExtra("fullitems", gson.toJson(fullList).toString());
            nextIntent.putExtra("position", position);
            nextIntent.putExtra("index", index);
            startActivityForResult(nextIntent, 1);

          }
        });
      }
    }
  }

  boolean allow = true;

  private class CategorySubAdapter extends ArrayAdapter<SubList> {
    private Context mContext;
    private ArrayList<SubList> list;
    private int i = 0;

    public CategorySubAdapter(Context context, int resource, ArrayList<SubList> objects) {
      super(context, resource, objects);
      this.mContext = context;
      this.list = (ArrayList<SubList>) objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      // TODO Auto-generated method stub
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
        viewHolder.more = (TextView) rowView.findViewById(R.id.more);
        viewHolder.v1 = rowView.findViewById(R.id.v1);
        rowView.setTag(viewHolder);

      }

      final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
      ;
      // Log.i(getClass().getSimpleName(), "Here..**" +
      // list.get(position));

      if (beforepos != -1 && beforepos == position) {
        viewHolder.itemName.setText(list.get(position).item);
        viewHolder.weight.setText(list.get(position).packings.get(index).pack);

        if (list.get(position).packings.size() > 1) {
          viewHolder.more.setVisibility(View.VISIBLE);

        } else {
          viewHolder.more.setVisibility(View.INVISIBLE);
        }
        if (list.get(position).packings.get(index).mrp == list.get(position).packings.get(index).sellPrice) {
          viewHolder.price.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(index).sellPrice);
          viewHolder.price2.setVisibility(View.GONE);
          viewHolder.v1.setVisibility(View.GONE);

        } else {
          viewHolder.v1.setVisibility(View.VISIBLE);
          viewHolder.price2.setVisibility(View.VISIBLE);
          viewHolder.price.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(index).sellPrice);
          viewHolder.price2.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(index).mrp);
          viewHolder.price2.setPaintFlags(viewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (list.get(position).packings.get(index).offer.length() > 0) {
          viewHolder.offerpercent.setVisibility(View.VISIBLE);

          viewHolder.offerpercent.setText(list.get(position).packings.get(index).offer);
        } else {
          viewHolder.offerpercent.setVisibility(View.GONE);
        }

        viewHolder.count.setText("" + getItemIdCount(list.get(position).packings.get(index).packid));

        try {

          Picasso.with(getActivity()).load(list.get(position).image).placeholder(R.drawable.default_image)
              .into(viewHolder.image1);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        viewHolder.up.setOnClickListener(new OnClickListener() {

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
            int main = s + s1;
            viewHolder.count.setText("" + main);
            list.get(position).packings.get(index).itemnumber = main;
            if (notify != null)
              notify.passDataToIncrement("1");

            // Log.i(getClass().getSimpleName(), "Size in viewholder
            // up:
            // " + itemsSArray.size());
            linearViewN.startAnimation(moveCart);
            addItemsSArray(position, list.get(position).packings.get(index).packid, main);

            if (notify != null) {
              notify.triggerAreas(gson.toJson(itemsSArray));
              notify.callBackUpdatedResponse(gson.toJson(fullData));
            }

            if (main > 12) {
              if (allow) {
                Toast.makeText(getActivity(), "Bulk orders may be denied", Toast.LENGTH_SHORT).show();
                allow = false;
              }

            }

            try {
              if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

//								viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
              } else {
//								viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
              }
            } catch (NumberFormatException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (NotFoundException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (Exception e) {
              // TODO: handle exception
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
              linearViewN.startAnimation(moveCart);
              viewHolder.count.setText("" + main);
//							Log.i(getClass().getSimpleName(),
//									"In the If before additems: " + list.get(position).packings.get(index).packid+"--"+main);
              addItemsSArray(position, list.get(position).packings.get(index).packid, main);
              if (notify != null) {
                notify.triggerAreas(gson.toJson(itemsSArray));
                notify.callBackUpdatedResponse(gson.toJson(fullData));
              }

              list.get(position).packings.get(index).itemnumber = main;
            }

            try {
              if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

//								viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
              } else {
//								viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
              }
            } catch (NumberFormatException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (NotFoundException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (Exception e) {
              // TODO: handle exception
            }

          }
        });
      } else {
        viewHolder.itemName.setText(list.get(position).item);
        viewHolder.weight.setText(list.get(position).packings.get(0).pack);

        if (list.get(position).packings.size() > 1) {
          viewHolder.more.setVisibility(View.VISIBLE);

        } else {
          viewHolder.more.setVisibility(View.INVISIBLE);
        }
        if (list.get(position).packings.get(0).mrp == list.get(position).packings.get(0).sellPrice) {
          viewHolder.price.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(0).sellPrice);
          viewHolder.price2.setVisibility(View.GONE);
          viewHolder.v1.setVisibility(View.GONE);

        } else {
          viewHolder.v1.setVisibility(View.VISIBLE);
          viewHolder.price2.setVisibility(View.VISIBLE);
          viewHolder.price.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(0).sellPrice);
          viewHolder.price2.setText(this.mContext.getResources().getString(R.string.rs) + " "
              + list.get(position).packings.get(0).mrp);
          viewHolder.price2.setPaintFlags(viewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (list.get(position).packings.get(0).offer.length() > 0) {
          viewHolder.offerpercent.setVisibility(View.VISIBLE);

          viewHolder.offerpercent.setText(list.get(position).packings.get(0).offer);
        } else {
          viewHolder.offerpercent.setVisibility(View.GONE);
        }

        viewHolder.count.setText("" + getItemIdCount(list.get(position).packings.get(0).packid));

        try {

          Picasso.with(getActivity()).load(list.get(position).image).placeholder(R.drawable.default_image)
              .into(viewHolder.image1);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        viewHolder.up.setOnClickListener(new OnClickListener() {

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
            int main = s + s1;
            viewHolder.count.setText("" + main);
            list.get(position).packings.get(0).itemnumber = main;
            if (notify != null)
              notify.passDataToIncrement("1");

            // Log.i(getClass().getSimpleName(), "Size in viewholder
            // up:
            // " + itemsSArray.size());
            linearViewN.startAnimation(moveCart);
            addItemsSArray(position, list.get(position).packings.get(0).packid, main);

            if (notify != null) {
              notify.triggerAreas(gson.toJson(itemsSArray));
              notify.callBackUpdatedResponse(gson.toJson(fullData));
            }

            if (main > 12) {
              if (allow) {
                Toast.makeText(getActivity(), "Bulk orders may be denied", Toast.LENGTH_SHORT).show();
                allow = false;
              }

            }

            try {
              if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

//								viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
              } else {
//								viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
              }
            } catch (NumberFormatException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (NotFoundException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (Exception e) {
              // TODO: handle exception
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
              linearViewN.startAnimation(moveCart);
              viewHolder.count.setText("" + main);
              addItemsSArray(position, list.get(position).packings.get(0).packid, main);
              if (notify != null) {
                notify.triggerAreas(gson.toJson(itemsSArray));
                notify.callBackUpdatedResponse(gson.toJson(fullData));
              }

              list.get(position).packings.get(0).itemnumber = main;
            }

            try {
              if (Integer.parseInt(viewHolder.count.getText().toString()) > 0) {

//								viewHolder.count.setTextColor(getResources().getColor(R.color.orange));
              } else {
//								viewHolder.count.setTextColor(getResources().getColor(R.color.grey));
              }
            } catch (NumberFormatException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (NotFoundException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (Exception e) {
              // TODO: handle exception
            }

          }
        });
      }

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
    private TextView more;

  }

}
