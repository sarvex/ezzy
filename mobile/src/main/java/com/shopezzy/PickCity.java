package com.shopezzy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.shopezzy.CitySelection.AllCities;

import java.util.ArrayList;

public class PickCity extends AppCompatActivity {

  private ListView listView;
  AllCities allCities;
  private Gson gson;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.citypopulate);

    listView = (ListView) findViewById(R.id.listview);
    gson = new Gson();
    allCities = gson.fromJson(getIntent().getStringExtra("allcities"), AllCities.class);

    if (allCities != null && allCities.cities.size() > 0) {

      MySpinnerAdapter adapter = new MySpinnerAdapter(PickCity.this, 0, allCities.cities);
      listView.setAdapter(adapter);
      listView.setOnItemClickListener(new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
