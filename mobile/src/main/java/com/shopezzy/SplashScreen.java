package com.shopezzy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

//import android.util.Log;

public class SplashScreen extends SherlockActivity {

	private Animation moveAhoy, movePro;
	private LinearLayout linear;
	private ProgressBar progress;
	private TextView txtView;
	private TextView txtViewNext;
	SharedPreferences pref;
	String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		USHOP.launch = false;
		setContentView(R.layout.splash);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		pref = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);

		if (getIntent().hasExtra(Constants.KEY_PUSH_MESSAGE)) {
			message = getIntent().getStringExtra(Constants.KEY_PUSH_MESSAGE);
//			Log.i(getClass().getSimpleName(), "Message Is: " + message);
		}
		// Log.i(getClass().getSimpleName(), getClass().getSimpleName());
		moveAhoy = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
		movePro = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveprogress);
		linear = (LinearLayout) findViewById(R.id.appname);

		txtView = (TextView) findViewById(R.id.txtView);
		txtViewNext = (TextView) findViewById(R.id.txtViewNext);

		txtView.setText(Html.fromHtml("<font color=#e58911>shop</font>"));
		txtViewNext.setText(Html.fromHtml("<b><font color=#25ad53>EZZY</font></b>"));

		linear.setAnimation(moveAhoy);

		progress = (ProgressBar) findViewById(R.id.progressbar);

		moveAhoy.setAnimationListener(txtViewList);
		movePro.setAnimationListener(progressBarList);

	}

	// Validations checking for OTP Status and PIN
	void validAndMove() {
		if (pref.getBoolean(Constants.pref_gotit, false)) {

			if (pref.getString(Constants.otp_status, "NA").equals("NA")) {
				Intent localIntent = new Intent(SplashScreen.this, RegisterYourSelf.class);
				if (message != null && message.length() > 0)
					localIntent.putExtra("message", message);
				SplashScreen.this.startActivity(localIntent);
				SplashScreen.this.finish();
			} else {

				if (!pref.getString(Constants.selectedPin, "NA").equalsIgnoreCase("NA")) {

					Intent nextIntent = new Intent(SplashScreen.this, PickStore.class);
					if (message != null && message.length() > 0)
						nextIntent.putExtra("message", message);
					startActivity(nextIntent);
					SplashScreen.this.finish();
				} else {
					Intent nextIntent = new Intent(SplashScreen.this, CitySelection.class);
					if (message != null && message.length() > 0)
						nextIntent.putExtra("message", message);
					startActivity(nextIntent);
					SplashScreen.this.finish();
				}

			}

		} else {
			Intent localIntent = new Intent(SplashScreen.this, IntroScreen.class);
			if (message != null && message.length() > 0)
				localIntent.putExtra("message", message);
			SplashScreen.this.startActivity(localIntent);
			SplashScreen.this.finish();
		}
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			Toast.makeText(SplashScreen.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub
			try {
				String responseString = new String(arg2, "UTF-8");

				JSONObject jsonObject = new JSONObject(responseString);
				if (jsonObject.getString("status").equalsIgnoreCase("success")
						&& jsonObject.getString("name").length() > 0) {
					// Log.i(getClass().getSimpleName(), "Commited");
					pref.edit().putString(Constants.viewuser, responseString).commit();
					validAndMove();
				} else {
					validAndMove();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	// Asynctask for fetching user info.
	private class AsynTaskSplash extends AsyncTask<Void, Void, Void> {

		JSONObject userLocObject = null;

		protected Void doInBackground(Void... paramArrayOfString) {

			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			super.onPostExecute(paramVoid);

			// Intent localIntent = new Intent(SplashScreen.this,
			// MainActivity.class);
			// SplashScreen.this.startActivity(localIntent);
			// SplashScreen.this.finish();

			if (!pref.getString(Constants.mobile_num, "NA").equalsIgnoreCase("NA")
					&& !pref.getString(Constants.otp_status, "NA").equals("NA")) {

				UShopRestClient.getUAHOY(SplashScreen.this,
						"app.viewUser?msisdn=" + pref.getString(Constants.mobile_num, "NA"), null, responseHandler);
			} else {
				if (pref.getBoolean(Constants.pref_gotit, false)) {

					if (pref.getString(Constants.otp_status, "NA").equals("NA")) {
						Intent localIntent = new Intent(SplashScreen.this, RegisterYourSelf.class);
						SplashScreen.this.startActivity(localIntent);
						SplashScreen.this.finish();
					} else {

						if (!pref.getString(Constants.selectedPin, "NA").equalsIgnoreCase("NA")) {

							Intent nextIntent = new Intent(SplashScreen.this, PickStore.class);

							startActivity(nextIntent);
							SplashScreen.this.finish();
						} else {
							Intent nextIntent = new Intent(SplashScreen.this, CitySelection.class);

							startActivity(nextIntent);
							SplashScreen.this.finish();
						}

					}

				} else {
					Intent localIntent = new Intent(SplashScreen.this, IntroScreen.class);

					SplashScreen.this.startActivity(localIntent);
					SplashScreen.this.finish();
				}
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	AnimationListener txtViewList = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			txtView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			progress.setAnimation(movePro);
		}
	};

	AnimationListener progressBarList = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			new AsynTaskSplash().execute();
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, "HKV9QWR8PPYYRMW4QV33");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
