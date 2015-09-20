package com.shopezzy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shopezzy.SearchItems.ItemsQuery;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetails extends SherlockActivity {

	private ActionBar mActionBar;
	private SharedPreferences pref;
	Typeface tff;
	private TextView pageTitle;
	private Button addmore;
	ProgressDialog proD;
	String orderId;
	private TextView shopname, date, items, price;
	private Button orderstatus;
	ArrayList<ItemsQuery> itemsQuery;
	String orderstatusString, totalItems, totalPrice, shopnameS;
	int orderSN;
	private ListView listView;


	// Handler invoked after fetching the order details from API.
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			try {
				proD.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(OrderDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub
			try {
				String responseString = new String(arg2, "UTF-8");
				itemsQuery = new ArrayList<SearchItems.ItemsQuery>();
				JSONObject jsonObject = new JSONObject(responseString);

				if (jsonObject.getString("status").equalsIgnoreCase("success")) {
					String orderId = jsonObject.getString("orderId");
					String date = jsonObject.getString("datetime");

					shopname.setText(shopnameS);
					orderstatus.setText(orderstatusString);
					try {
						GradientDrawable drawable = (GradientDrawable) orderstatus.getBackground();

						if (orderSN == 0) {

							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "PLACED");
						}

						if (orderSN == 1) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "Accepted");
						}

						if (orderSN == 2) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "DECLINED");
						}

						if (orderSN == 3) {
							drawable.setColor(getResources().getColor(R.color.cancelled));
							// viewHolder.orderstatus.setText("" + "CANCELLED");
						}

						if (orderSN == 4) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "Accepted
							// With Comment");
						}

						if (orderSN == 5) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" +
							// "IN-PROCESS");
						}

						if (orderSN == 6) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "IN
							// TRANSIT");
						}

						if (orderSN == 7) {
							drawable.setColor(getResources().getColor(R.color.delivered));
							// viewHolder.orderstatus.setText("" + "DELIVERED");
						}

						if (orderSN == 8) {
							drawable.setColor(getResources().getColor(R.color.inprocess));
							// viewHolder.orderstatus.setText("" + "COMPLETED");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					items.setText(totalItems + " items");
					price.setText(OrderDetails.this.getResources().getString(R.string.rs) + " " + totalPrice);
					OrderDetails.this.date.setText(date);

					if (jsonObject.getJSONArray("items").length() > 0) {

						itemsQuery = new ArrayList<SearchItems.ItemsQuery>();
						for (int i = 0; i < jsonObject.getJSONArray("items").length(); i++) {

							ItemsQuery itemsQ = new SearchItems().new ItemsQuery();
							JSONObject itemObject = jsonObject.getJSONArray("items").getJSONObject(i);
							itemsQ.itemId = itemObject.getString("inventoryId");
							itemsQ.itemNumber = itemObject.getString("itemQuantity");
							itemsQ.itemMrp = itemObject.getString("price");
							itemsQ.itemName = itemObject.getString("name");
							itemsQ.itemWt = itemObject.getString("size");
							itemsQuery.add(itemsQ);
						}

						orderstatus.setVisibility(View.VISIBLE);
//						Log.i(getClass().getSimpleName(), "Size is: " + itemsQuery.size());
						OrderAdapter orderAd = new OrderAdapter(OrderDetails.this, 0, itemsQuery);
						listView.setAdapter(orderAd);

					}

				}
//				Log.i(getClass().getSimpleName(), "Response String: " + responseString);

				try {
					proD.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	private class OrderAdapter extends ArrayAdapter<ItemsQuery> {
		private Context mContext;
		private ArrayList<ItemsQuery> list;
		private int i = 0;

		public OrderAdapter(Context context, int resource, ArrayList<ItemsQuery> objects) {
			super(context, resource, objects);
			this.mContext = context;
			this.list = (ArrayList<ItemsQuery>) objects;

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.orderdetailsview, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.itemName = (TextView) rowView.findViewById(R.id.itemname);
				viewHolder.weight = (TextView) rowView.findViewById(R.id.weight);
				viewHolder.price1 = (TextView) rowView.findViewById(R.id.cost);
				viewHolder.image = (ImageView) rowView.findViewById(R.id.image1);

				viewHolder.price2 = (TextView) rowView.findViewById(R.id.price);
				rowView.setTag(viewHolder);

			}

			final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			// Log.i(getClass().getSimpleName(), "Here..**" +
			// list.get(position));
			viewHolder.itemName.setText(list.get(position).itemName);
			viewHolder.weight.setText(list.get(position).itemWt);
			viewHolder.price1.setText(this.mContext.getResources().getString(R.string.rs) + " "
					+ list.get(position).itemMrp + " x " + list.get(position).itemNumber);
			viewHolder.price2.setText(this.mContext.getResources().getString(R.string.rs) + " "
					+ (Float.valueOf(list.get(position).itemMrp) * Float.valueOf(list.get(position).itemNumber)));

			try {

				Picasso.with(OrderDetails.this).load(list.get(position).itemImage).placeholder(R.drawable.default_image)
						.into(viewHolder.image);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
		private TextView price1;
		private TextView price2;
		private ImageView image;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdettails);
//		if (USHOP.launch) {
//
//			Intent intent = new Intent(this, SplashScreen.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//		}
		mActionBar = getSupportActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
		mActionBar.setBackgroundDrawable(colorDrawable);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbarfinal, null);

		pageTitle = (TextView) actionbarView.findViewById(R.id.txtsubtitle);

		addmore = (Button) actionbarView.findViewById(R.id.remainingItems);
		addmore.setVisibility(View.GONE);

		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setCustomView(actionbarView);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		try {
			orderId = getIntent().getStringExtra("orderid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			orderId = "";
		}

		pageTitle.setText("Order # " + orderId);
		proD = new ProgressDialog(OrderDetails.this);
		proD.setMessage("Please wait...");
		proD.setCancelable(false);

		shopname = (TextView) findViewById(R.id.shopname);
		date = (TextView) findViewById(R.id.date);
		orderstatus = (Button) findViewById(R.id.orderstatus);
		items = (TextView) findViewById(R.id.items);
		price = (TextView) findViewById(R.id.price);
		listView = (ListView) findViewById(R.id.listview);

		orderstatusString = getIntent().getStringExtra("orderstatus");
		totalItems = getIntent().getStringExtra("totalitems");
		totalPrice = getIntent().getStringExtra("totalprice");
		shopnameS = getIntent().getStringExtra("shopname");
		orderSN = getIntent().getIntExtra("ordersn", -1);

		// shopname.setText(orderstatusString);

		proD.show();
		UShopRestClient.getUAHOY(this,
				"app.orderIdItems?msisdn=" + pref.getString(Constants.mobile_num, "NA") + "&orderid=" + orderId, null,
				responseHandler);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
