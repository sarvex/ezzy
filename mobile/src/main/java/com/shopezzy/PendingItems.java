package com.shopezzy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ahoy.analytics.AHTracker;

public class PendingItems extends AppCompatActivity {

	private Button yes, no;
	private String count;
	private TextView nameList;
	Typeface tff;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		AHTracker.getInstance().setModule("shopezzy").setApiKey(Constants.analytics_key)
				.setPageName(getClass().getSimpleName()).startSession(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AHTracker.getInstance().stopSession(this);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noitems);

		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }
		tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");

		if (Build.VERSION.SDK_INT >= 14) {

			this.setFinishOnTouchOutside(false);
			// Log.i(getClass().getSimpleName(),
			// "above 14"+Build.VERSION.SDK_INT);
		} else {
			// Log.i(getClass().getSimpleName(),
			// "below 14"+Build.VERSION.SDK_INT);
		}

		yes = (Button) findViewById(R.id.yes);

		no = (Button) findViewById(R.id.no);

		yes.setTypeface(tff);
		no.setTypeface(tff);
		nameList = (TextView) findViewById(R.id.namelist);

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent nextIntent = PendingItems.this.getIntent();
				nextIntent.putExtra("result", "yes");
				setResult(3, nextIntent);
				finish();
				// if (SearchItems.searchItems != null)
				// SearchItems.searchItems.finish();
			}
		});

		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent nextIntent = PendingItems.this.getIntent();
				nextIntent.putExtra("result", "no");
				setResult(2, nextIntent);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
