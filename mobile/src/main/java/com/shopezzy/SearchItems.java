package com.shopezzy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class SearchItems extends SherlockActivity implements SearchView.OnQueryTextListener {

	private SearchView mSearchView;
	private ActionBar mActionBar;
	private ImageView cart;
	private ImageView searchView;
	static NotifyCallBack notify;
	private Animation moveCart, moveCart1, moveCart2;
	private android.widget.TextView text, title;
	private ImageView searchCloseIcon, searchHintIcon;
	String storeId;
	String storeName;
	String storeAddress;
	String minOrder;
	private ArrayList<ItemsQuery> itemsQArray = new ArrayList<SearchItems.ItemsQuery>();
	private SearchAdapter searchAdapter;

	LinearLayout linearViewN = null;
	private ListView mListView;
	private ImageView cartView;
	private ProgressBar progressBar;
	Typeface tff;
	private ArrayList<ItemsQuery> itemsSArray = new ArrayList<SearchItems.ItemsQuery>();
	private Gson gson;
	private String searchText;
	static Activity searchItems;

	AnimationListener listener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			// Log.i(getClass().getSimpleName(), "In Activity Result");
			if (data != null && data.hasExtra("updatedJson")) {

				// Log.i(getClass().getSimpleName(), "In Activity Result1");
				try {
					String itemsS = data.getStringExtra("updatedJson");
					Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
					}.getType();
					if (itemsSArray != null && itemsSArray.size() > 0)
						itemsSArray.clear();
					itemsSArray = gson.fromJson(itemsS, collectionType);

					int itemNumber = 0;

					if (itemsSArray != null) {
						for (int i = 0; i < itemsSArray.size(); i++) {
							ItemsQuery itemsQ = itemsSArray.get(i);
							String itemsNumber = itemsQ.itemNumber;
							itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
						}
					}

					searchAdapter.notifyDataSetChanged();
					this.text.setText("" + itemNumber);
					// Log.i(getClass().getSimpleName(), "ItemNumber: " +
					// itemNumber);
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		else if (requestCode == 2) {
			if (data != null && data.hasExtra("result")) {
				if (data.getStringExtra("result").equalsIgnoreCase("yes"))
					finish();
			}

		}

	};

	// For settingup the search view.
	private void setupSearchView() {
		// mSearchView.requestFocus();
		mSearchView.setFocusable(false);
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(false);
		mSearchView.setQueryHint("Type brand/item name");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchitems);
		tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }
		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }

		searchItems = this;
		gson = new Gson();
		mActionBar = getSupportActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#25ac52"));
		mActionBar.setBackgroundDrawable(colorDrawable);

		mSearchView = (SearchView) findViewById(R.id.search_view);

		cartView = (ImageView) findViewById(R.id.centerbackground);
		progressBar = (ProgressBar) findViewById(R.id.searchprogress);

		View actionbarView = LayoutInflater.from(this).inflate(R.layout.customactionbar, null);
		text = (TextView) actionbarView.findViewById(R.id.cartText);
		title = (TextView) actionbarView.findViewById(R.id.title);
		title.setText("Search Items");
		cart = (ImageView) actionbarView.findViewById(R.id.cart);
		cart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (itemsSArray != null && itemsSArray.size() > 0) {
					Intent nextIntent = new Intent(SearchItems.this, SearchItemReview.class);
					nextIntent.putExtra("items", gson.toJson(itemsSArray));
					nextIntent.putExtra("storename", storeName);
					nextIntent.putExtra("storeid", storeId);
					nextIntent.putExtra("storeaddress", storeAddress);
					nextIntent.putExtra("minorder", minOrder);
					startActivityForResult(nextIntent, 1);
				} else {
					Toast.makeText(SearchItems.this, "Please add atleast one item to proceed", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		linearViewN = (LinearLayout) actionbarView.findViewById(R.id.circle1);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setCustomView(actionbarView);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		int searchcloseicon = getResources().getIdentifier("android:id/search_close_btn", null, null);
		searchCloseIcon = (ImageView) mSearchView.findViewById(searchcloseicon);
		searchCloseIcon.setImageResource(R.drawable.close1);

		int searchhinticon = getResources().getIdentifier("android:id/search_mag_icon", null, null);
		searchHintIcon = (ImageView) mSearchView.findViewById(searchhinticon);
		searchHintIcon.setImageResource(R.drawable.search);

		moveCart = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.zoomin);
		moveCart.setAnimationListener(listener);
		moveCart1 = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.zoomout);
		moveCart1.setAnimationListener(listener1);

		int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
		android.widget.EditText searchEditText = (android.widget.EditText) mSearchView.findViewById(searchSrcTextId);
		searchEditText.setTextColor(getResources().getColor(R.color.searchtextcolor));

		searchEditText.setHintTextColor(getResources().getColor(R.color.hintcolor));

		int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null,
				null);
		View searchPlateView = mSearchView.findViewById(searchPlateId);
		if (searchPlateView != null) {
			searchPlateView.setBackgroundResource(R.drawable.searchfield1);
		}

		mListView = (ListView) findViewById(R.id.searchlist);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
					getCurrentFocus().clearFocus();
				}
				return false;
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
					getCurrentFocus().clearFocus();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		setupSearchView();
		searchAdapter = new SearchAdapter(this, 0, itemsQArray);
		mListView.setAdapter(searchAdapter);

		if (getIntent().hasExtra("storeid")) {
			storeId = getIntent().getStringExtra("storeid");
			storeName = getIntent().getStringExtra("storename");
			storeAddress = getIntent().getStringExtra("storeaddress");
			minOrder = getIntent().getStringExtra("minorder");

		}

		if (savedInstanceState != null) {

			storeId = savedInstanceState.getString("storeid");
			storeName = savedInstanceState.getString("storename");
			storeAddress = savedInstanceState.getString("storeaddress");
			minOrder = savedInstanceState.getString("minorder");
			String items = savedInstanceState.getString("items");

			Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
			}.getType();
			if (itemsSArray.size() > 0)
				itemsSArray.clear();
			itemsSArray = gson.fromJson(items, collectionType);

			if (itemsSArray.size() > 0) {
				int itemNumber = 0;

				for (int i = 0; i < itemsSArray.size(); i++) {
					ItemsQuery itemsQ = itemsSArray.get(i);
					String itemsNumber = itemsQ.itemNumber;
					itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
				}

				this.text.setText("" + itemNumber);
			}

			return;
		}

		if (getIntent().hasExtra("items")) {

//			Log.i(getClass().getSimpleName(), "Search Items");
			String items = getIntent().getStringExtra("items");

			Type collectionType = new TypeToken<ArrayList<ItemsQuery>>() {
			}.getType();
			if (itemsSArray.size() > 0)
				itemsSArray.clear();
			itemsSArray = gson.fromJson(items, collectionType);

			if (itemsSArray.size() > 0) {
				int itemNumber = 0;

				for (int i = 0; i < itemsSArray.size(); i++) {
					ItemsQuery itemsQ = itemsSArray.get(i);
					String itemsNumber = itemsQ.itemNumber;
					itemNumber = itemNumber + Integer.valueOf(itemsQ.itemNumber);
				}

				this.text.setText("" + itemNumber);
			}

			return;
		}

		// Log.i(getClass().getSimpleName(), "Minorder: " + minOrder);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		outState.putString("storeid", storeId);
		outState.putString("storename", storeName);
		outState.putString("storeaddress", storeAddress);
		outState.putString("minorder", minOrder);
		outState.putString("items", gson.toJson(itemsSArray));

	}

	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onRestoreInstanceState(savedInstanceState);
	// if (savedInstanceState != null) {
	// storeId = savedInstanceState.getString("storeid");
	// storeName = savedInstanceState.getString("storename");
	// storeAddress = savedInstanceState.getString("storeAddress");
	// minOrder = savedInstanceState.getString("minOrder");
	// }
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SearchItems.searchItems = this;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	// Adapter for displaying search items.
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
			// TODO Auto-generated method stub
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.searchitemview2, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.itemName = (TextView) rowView.findViewById(R.id.itemname);
				viewHolder.weight = (TextView) rowView.findViewById(R.id.weight);
				viewHolder.price = (TextView) rowView.findViewById(R.id.cost);
				viewHolder.price2 = (TextView) rowView.findViewById(R.id.cost1);
				viewHolder.offerpercent = (Button) rowView.findViewById(R.id.offerpecent);
				viewHolder.image1 = (ImageView) rowView.findViewById(R.id.image1);
				viewHolder.button = (Button) rowView.findViewById(R.id.add);
				viewHolder.button.setTypeface(tff);
				viewHolder.v1 = rowView.findViewById(R.id.v1);
				rowView.setTag(viewHolder);

			}

			final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			// Log.i(getClass().getSimpleName(), "Here..**" +
			// list.get(position));
			viewHolder.itemName.setText(list.get(position).itemName);
			viewHolder.weight.setText(list.get(position).itemWt);
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
			}
			else
			{
				viewHolder.offerpercent.setVisibility(View.GONE);
			}
			try {

				Picasso.with(SearchItems.this).load(list.get(position).itemImage).placeholder(R.drawable.default_image)
						.into(viewHolder.image1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewHolder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					try {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (getCurrentFocus() != null) {
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
							getCurrentFocus().clearFocus();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					itemsSArray.add(list.get(position));
					Iterator<ItemsQuery> iterateItems = list.iterator();

					while (iterateItems.hasNext()) {
						ItemsQuery itemQuery = iterateItems.next();
						if (list.get(position).itemId.equalsIgnoreCase(itemQuery.itemId)) {
							iterateItems.remove();
							searchAdapter.notifyDataSetChanged();
							String text = SearchItems.this.text.getText().toString();
							// Log.i(getClass().getSimpleName(), "Text: " +
							// text);
							int s = Integer.valueOf(text);
							int s1 = 1;

							int main = s + s1;
							SearchItems.this.text.setText(Integer.toString(main));
							linearViewN.startAnimation(moveCart);
							break;
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
		private Button button;
		private Button offerpercent;
		private TextView price2;
		private View v1;


	}

	//Handler invokes after searching items.
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			try {
				if (mListView.getVisibility() == View.INVISIBLE) {
					cartView.setVisibility(View.INVISIBLE);
					mListView.setVisibility(View.VISIBLE);

				}
				progressBar.setVisibility(View.INVISIBLE);
				SearchItems.this.itemsQArray.clear();
				searchAdapter.notifyDataSetChanged();
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
					String textJson = jsonObject.getString("searchtext");
					JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("items").toString());
					ArrayList<ItemsQuery> itemsQArray = new ArrayList<SearchItems.ItemsQuery>();
					if (SearchItems.this.itemsQArray.size() == 0) {
						SearchItems.this.itemsQArray.clear();
						searchAdapter.notifyDataSetChanged();
					}
					if (itemsQArray.size() > 0)
						SearchItems.this.itemsQArray.clear();
					if (jsonArray != null && jsonArray.length() > 0) {
						outer: for (int i = 0; i < jsonArray.length(); i++) {
							ItemsQuery itemsQue = new ItemsQuery();
							JSONObject itemObject = jsonArray.getJSONObject(i);
							itemsQue.itemId = itemObject.getString("id");
							itemsQue.itemName = itemObject.getString("name");
							itemsQue.itemMrp = itemObject.getString("mrp");
							itemsQue.itemImage = itemObject.getString("img");
							itemsQue.itemSp = itemObject.getString("sp");
							itemsQue.itemWt = itemObject.getString("wt");
							itemsQue.offer = itemObject.getString("ofr");
							itemsQue.itemNumber = "1";
							boolean isAdd = true;
							for (int j = 0; j < itemsSArray.size(); j++) {
								if (itemsQue.itemId.equalsIgnoreCase(itemsSArray.get(j).itemId)) {
									// continue outer;
									isAdd = false;
									break;
								}
							}

							if (isAdd) {
								itemsQArray.add(itemsQue);
								isAdd = true;
							}

						}

						if (searchText.equals(textJson)) {
							SearchItems.this.itemsQArray.clear();
							SearchItems.this.itemsQArray.addAll(itemsQArray);
							searchAdapter.notifyDataSetChanged();
						}

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (mListView.getVisibility() == View.INVISIBLE) {
									cartView.setVisibility(View.INVISIBLE);
									mListView.setVisibility(View.VISIBLE);

								}

								progressBar.setVisibility(View.INVISIBLE);

							}
						}, 1000);

					} else {

						SearchItems.this.itemsQArray.clear();
						SearchItems.this.itemsQArray.addAll(itemsQArray);
						searchAdapter.notifyDataSetChanged();

						// TODO Auto-generated method stub
						if (mListView.getVisibility() == View.INVISIBLE) {
							cartView.setVisibility(View.INVISIBLE);
							mListView.setVisibility(View.VISIBLE);

						}
						progressBar.setVisibility(View.INVISIBLE);

					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				try {
					itemsQArray.clear();
					searchAdapter.notifyDataSetChanged();

					if (mListView.getVisibility() == View.INVISIBLE) {
						cartView.setVisibility(View.INVISIBLE);
						mListView.setVisibility(View.VISIBLE);

					}
					progressBar.setVisibility(View.INVISIBLE);
					searchAdapter.notifyDataSetChanged();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	};

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		try {
			searchText = newText;
			UShopRestClient.cancelRequest(SearchItems.this);
			String url = "http://shopezzy.com/app.search?store=" + storeId + "&searchtext=" + newText.toLowerCase()
					+ "&sidx=0&rows=15";
			// String url = "http://shopezzy.com/ushoptest/searchItem?keyword="
			// + newText + "&sidx=0&eidx=5";
			if (newText.length() >= 3) {
				progressBar.setVisibility(View.VISIBLE);
				cartView.setVisibility(View.INVISIBLE);
				mListView.setVisibility(View.INVISIBLE);

				UShopRestClient.getSHOPEZZYFULLURL(SearchItems.this, url, null, responseHandler);
			} else {
				try {
					progressBar.setVisibility(View.INVISIBLE);

					mListView.setVisibility(View.INVISIBLE);
					itemsQArray.clear();
					searchAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// loadData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			// if (itemsSArray != null) {
			// if (itemsSArray.size() > 0) {
			//
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// if (getCurrentFocus() != null) {
			// imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
			// 0);
			// getCurrentFocus().clearFocus();
			// }
			//
			// new Handler().postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// Intent nextIntent = new Intent(SearchItems.this,
			// PendingItems.class);
			// startActivity(nextIntent);
			// }
			// }, 200);
			//
			// } else {
			// SearchItems.this.finish();
			// }
			// } else {
			// SearchItems.this.finish();
			// }

			if (itemsSArray != null && itemsSArray.size() > 0) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
					getCurrentFocus().clearFocus();
				}

				Intent nextIntent = getIntent();
				nextIntent.putExtra("carttext", this.text.getText().toString());
				nextIntent.putExtra("items", gson.toJson(itemsSArray));
				setResult(2, nextIntent);
				finish();

			} else {
				finish();
			}
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();

		// getCurrentFocus().clearFocus();
		// if (itemsSArray != null) {
		// if (itemsSArray.size() > 0) {
		//
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// if (getCurrentFocus() != null) {
		// imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		// getCurrentFocus().clearFocus();
		// }
		//
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Intent nextIntent = new Intent(SearchItems.this, PendingItems.class);
		// startActivityForResult(nextIntent, 2);
		// }
		// }, 200);
		// } else {
		// SearchItems.this.finish();
		// }
		// } else {
		// SearchItems.this.finish();
		// }

		if (itemsSArray != null && itemsSArray.size() > 0) {

			Intent nextIntent = getIntent();
			nextIntent.putExtra("carttext", this.text.getText().toString());
			nextIntent.putExtra("items", gson.toJson(itemsSArray));
			setResult(2, nextIntent);
			finish();

		} else {
			finish();
		}

	}

	class ItemsQuery {
		String itemId;
		String itemName;
		String itemImage;
		String itemMrp;
		String itemWt;
		String itemSp;
		String itemNumber;
		String offer;
	}
}
