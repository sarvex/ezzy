package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shopezzy.SearchItems.ItemsQuery;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Categories extends SherlockActivity {

	private GridView gridView;
	private ProgressDialog proD;
	private Gson gson;
	private ActionBar mActionBar;
	private TextView pageTitle;
	private Button addmore;
	private ImageView search;
	ArrayList<AllCat> allcat;

	String storeId;
	String storeName;
	String storeAddress;
	String minOrder;

	String text;
	String items;
	String updatedResponse;

	private ImageView cart;
	private android.widget.TextView text1, title;
	LinearLayout linearViewN = null;
	private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
		}.getType();
		itemsSArray = gson.fromJson(items, collectionType);

		if (itemsSArray != null && itemsSArray.size() > 0) {
			int itemNumber = 0;

			for (int i = 0; i < itemsSArray.size(); i++) {
				ItemsQuery itemsQ = itemsSArray.get(i);
				String itemsNumber = itemsQ.itemNumber;
				itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
			}

			this.text1.setText("" + itemNumber);
		} else {
			this.text1.setText("0");
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview);
		gridView = (GridView) findViewById(R.id.gridview);

		mActionBar = getSupportActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
		mActionBar.setBackgroundDrawable(colorDrawable);

		if (getIntent().hasExtra("storeid")) {
			storeId = getIntent().getStringExtra("storeid");
			storeName = getIntent().getStringExtra("storename");
			storeAddress = getIntent().getStringExtra("storeaddress");
			minOrder = getIntent().getStringExtra("minorder");

		}

		if (savedInstanceState != null) {

			// this.text.setText(savedInstanceState.getString("carttext"));

			items = savedInstanceState.getString("items");
			storeId = savedInstanceState.getString("storeid");
			storeName = savedInstanceState.getString("storename");
			storeAddress = savedInstanceState.getString("storeaddress");
			minOrder = savedInstanceState.getString("minorder");
			updatedResponse = savedInstanceState.getString("updatedresponse");
		}

		View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarsearch, null);

		text1 = (TextView) actionbarView.findViewById(R.id.cartText);
		cart = (ImageView) actionbarView.findViewById(R.id.cart);
		pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);
		if (storeName != null)
			pageTitle.setText(storeName);
		else
			pageTitle.setText("");
		addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
		addmore.setVisibility(View.GONE);
		search = (ImageView) actionbarView.findViewById(R.id.search);

		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setCustomView(actionbarView);
		// mActionBar.setDisplayHomeAsUpEnabled(true);

		cart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					if (items != null && items.length() > 0 && new JSONArray(items).length() > 0) {

						Intent nextIntent = new Intent(Categories.this, SearchItemReview.class);
						// Log.i(getClass().getSimpleName(), "Items: " + items);
						nextIntent.putExtra("items", items);
						nextIntent.putExtra("storename", storeName);
						nextIntent.putExtra("storeid", storeId);
						nextIntent.putExtra("storeaddress", storeAddress);
						nextIntent.putExtra("minorder", minOrder);
						startActivityForResult(nextIntent, 1);
					} else {
						Toast.makeText(Categories.this, "Please add atleast one item to proceed", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		gson = new Gson();
		proD = new ProgressDialog(this);
		proD.setMessage("Please wait...");
		proD.setCancelable(false);
		proD.show();
		UShopRestClient.getUAHOY(this, "app.category", null, responseHandlerAllCat);

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent nextIntent = new Intent(Categories.this, SearchItems.class);
				nextIntent.putExtra("storeid", storeId);
				nextIntent.putExtra("storename", storeName);
				nextIntent.putExtra("storeaddress", storeAddress);
				nextIntent.putExtra("minorder", minOrder);
				if (items != null) {
					nextIntent.putExtra("carttext", text);
					nextIntent.putExtra("items", items);
				}

				startActivityForResult(nextIntent, 2);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				if (allcat != null) {
					Intent nextIntent = new Intent(Categories.this, FullItems.class);
					nextIntent.putExtra("storeid", storeId);
					nextIntent.putExtra("storename", storeName);
					nextIntent.putExtra("storeaddress", storeAddress);
					nextIntent.putExtra("minorder", minOrder);
					nextIntent.putExtra("catid", allcat.get(position).cid + "");
					nextIntent.putExtra("catname", allcat.get(position).cname);
					if (items != null) {
						nextIntent.putExtra("carttext", text);
						nextIntent.putExtra("items", items);
						if (updatedResponse != null && updatedResponse.length() > 0)
							nextIntent.putExtra("updatedresponse", updatedResponse);
					}
					startActivityForResult(nextIntent, 4);
				}

			}
		});

	}

	// Handler called after fetching all the categories.
	AsyncHttpResponseHandler responseHandlerAllCat = new AsyncHttpResponseHandler() {

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
				try {
					proD.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (new JSONObject(response).getString("status").equalsIgnoreCase("Success")) {

					Type collectionType = new TypeToken<ArrayList<AllCat>>() {
					}.getType();

					if (new JSONObject(response).getJSONArray("list").length() > 0) {
						allcat = gson.fromJson(new JSONObject(response).getJSONArray("list").toString(),
								collectionType);
						// Log.i(getClass().getSimpleName(), "Size is: " +
						// allcat.size());
						MyCategoryAdapter catAdapter = new MyCategoryAdapter(Categories.this, 0, allcat);
						gridView.setAdapter(catAdapter);
					}

				} else {
					Toast.makeText(Categories.this, "Something went wrong", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};

	public class AllCat {

		public int cid;
		public String cname;
		public String desc;
		public String image;

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (items != null) {
			try {
				if (items.length() > 0 && new JSONArray(items).length() > 0) {

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (getCurrentFocus() != null) {
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
						getCurrentFocus().clearFocus();
					}

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Intent nextIntent = new Intent(Categories.this, PendingItems.class);
							startActivityForResult(nextIntent, 3);
						}
					}, 200);
				} else {
					Categories.this.finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Categories.this.finish();
		}
	}

	// Adapter for displaying all the categories.
	private class MyCategoryAdapter extends ArrayAdapter<AllCat> {
		private Context mContext;
		private ArrayList<AllCat> list;
		private int i = 0;

		public MyCategoryAdapter(Context context, int resource, ArrayList<AllCat> objects) {
			super(context, resource, objects);
			this.mContext = context;
			this.list = (ArrayList<AllCat>) objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.category, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) rowView.findViewById(R.id.productimage);
				viewHolder.textup = (TextView) rowView.findViewById(R.id.nameup);
				viewHolder.textdown = (TextView) rowView.findViewById(R.id.namedown);
				rowView.setTag(viewHolder);

			}
			final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			// viewHolder.countrytxt.setText(list.get(position));

			// i++;
			// if (i % 2 == 0) {
			// if (i == 0)
			// return rowView;
			//
			// viewHolder.layout.setBackgroundColor(getResources().getColor(
			// R.color.black90T));
			// }

			viewHolder.textup.setText(list.get(position).cname);
			viewHolder.textdown.setText(list.get(position).desc);
			try {
				viewHolder.image.setImageDrawable(getDrawableWithName(list.get(position).cid + ""));

//				Picasso.with(Categories.this).load(list.get(position).image).placeholder(R.drawable.offerszone)
//						.into(viewHolder.image);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return rowView;
		}
	}

	// For fetching drawable with the name.
	Drawable getDrawableWithName(String name) {
		Resources resources = this.getResources();
		final int resourceId = resources.getIdentifier("_"+name, "drawable", this.getPackageName());
		return resources.getDrawable(resourceId);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("items", items);
		outState.putString("carttext", this.text1.getText().toString());
		outState.putString("updatedresponse", updatedResponse);
		outState.putString("storeid", storeId);
		outState.putString("storename", storeName);
		outState.putString("storeaddress", storeAddress);
		outState.putString("minorder", minOrder);

	}

	static class ViewHolder {

		private ImageView image;
		private TextView textup;
		private TextView textdown;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 1) {
			if (data != null && data.hasExtra("updatedJson")) {
				items = data.getStringExtra("updatedJson");

				Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
				}.getType();
				if (itemsSArray != null && itemsSArray.size() > 0)
					itemsSArray.clear();
				itemsSArray = gson.fromJson(items, collectionType);

				int itemNumber = 0;

				if (itemsSArray != null) {
					for (int i = 0; i < itemsSArray.size(); i++) {
						ItemsQuery itemsQ = itemsSArray.get(i);
						String itemsNumber = itemsQ.itemNumber;
						itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
					}
				}

				this.text1.setText("" + itemNumber);

			}
		}
		if (resultCode == 4) {
			if (data != null && data.hasExtra("items")) {
				// Log.i(getClass().getSimpleName(), "In 1");
				this.text = data.getStringExtra("carttext");
				items = data.getStringExtra("items");
				updatedResponse = data.getStringExtra("updatedresponse");
			}

		}

		if (resultCode == 2) {
			if (data != null && data.hasExtra("items")) {
				this.text = data.getStringExtra("carttext");
				items = data.getStringExtra("items");
			}
		}

		if (requestCode == 3) {
			if (data != null && data.hasExtra("result")) {
				if (data.getStringExtra("result").equalsIgnoreCase("yes"))
					finish();
			}

		}
	}
}
