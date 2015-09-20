package com.shopezzy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.shopezzy.CitySelection.AllCities;

import java.util.ArrayList;

public class PickCity extends SherlockActivity {

	private ListView listView;
	AllCities allCities;
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citypopulate);
		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }

		// if (Build.VERSION.SDK_INT >= 14) {
		//
		// this.setFinishOnTouchOutside(false);
		// // Log.i(getClass().getSimpleName(),
		// // "above 14"+Build.VERSION.SDK_INT);
		// } else {
		// // Log.i(getClass().getSimpleName(),
		// // "below 14"+Build.VERSION.SDK_INT);
		// }

		listView = (ListView) findViewById(R.id.listview);
		gson = new Gson();
		allCities = gson.fromJson(getIntent().getStringExtra("allcities"), AllCities.class);

		if (allCities != null && allCities.cities.size() > 0) {

			MySpinnerAdapter adapter = new MySpinnerAdapter(PickCity.this, 0, allCities.cities);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					// if (notify != null) {
					// notify.triggerAreas(allCities.cities.get(position).toString());
					Intent nextIntent = PickCity.this.getIntent();
					nextIntent.putExtra("city", allCities.cities.get(position).toString());
					setResult(1, nextIntent);
					PickCity.this.finish();
					// }

				}
			});

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	// Adapter for displaying Cities.
	private class MySpinnerAdapter extends ArrayAdapter<String> {
		private Context mContext;
		private ArrayList<String> list;
		private int i = 0;

		public MySpinnerAdapter(Context context, int resource, ArrayList<String> objects) {
			super(context, resource, objects);
			this.mContext = context;
			this.list = (ArrayList<String>) objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.pickcity, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.countrytxt = (TextView) rowView.findViewById(R.id.text);
				// viewHolder.layout = (FrameLayout) rowView
				// .findViewById(R.id.frame);
				rowView.setTag(viewHolder);

			}
			final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			viewHolder.countrytxt.setText(list.get(position));

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

		private TextView countrytxt;
		private FrameLayout layout;

	}

}
