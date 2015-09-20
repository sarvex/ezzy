package com.shopezzy;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ahoy.analytics.AHTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

public class RegisterYourSelf extends SherlockActivity {

	private ActionBar actionbar;
	private TextView txtView;
	private TextView txtViewNext;
	private android.widget.EditText editText;
	private Button confirm;
	private SharedPreferences pref;
	private ProgressDialog proD;
	Typeface tff;

	//For Fetching Email.
	String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

	// For Fetching AndroidId
	String getAndroidID() {
		return android.provider.Settings.Secure.getString(this.getContentResolver(), "android_id");
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.registerself);

		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }

		actionbar = getSupportActionBar();
		tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");
		SpannableString s = new SpannableString("Register Yourself");
		s.setSpan(new ActionbarCus("", tff), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		actionbar.setTitle(s);

		// actionbar.setTitle("Register Yourself");
		actionbar.show();

		proD = new ProgressDialog(RegisterYourSelf.this);
		proD.setMessage("please wait...");
		proD.setCancelable(false);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		txtView = (TextView) findViewById(R.id.txtView);
		txtViewNext = (TextView) findViewById(R.id.txtViewNext);

		txtView.setText(Html.fromHtml("<font color=#e58911>shop</font>"));
		txtViewNext.setText(Html.fromHtml("<b><font color=#25ad53>EZZY</font></b>"));

		editText = (android.widget.EditText) findViewById(R.id.edittext);
		editText.setTypeface(tff);

		confirm = (Button) findViewById(R.id.confirm);
		confirm.setTypeface(tff);

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editText.post(new Runnable() {
					@Override
					public void run() {
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				});
			}
		});

		if (savedInstanceState != null) {
			if (savedInstanceState.getString("text").length() > 0)
				editText.setText(savedInstanceState.getString("text"));
			else
				editText.requestFocus();
		} else
			editText.requestFocus();
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (editText.getText().toString() != null && editText.getText().toString().length() == 10) {

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (getCurrentFocus() != null) {
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
						getCurrentFocus().clearFocus();
					}
					// Log.i(getClass().getSimpleName(), "URL");
					// String url =
					// "oauth/generate?apikey=05e4baa076ff08c3639ccb0a62c0c150&src="
					// + getApplication().getPackageName()
					// + "&msisdn="
					// + editText.getText().toString() + "&mode=0&apv=1.0";

					String url = "register?msisdn=" + editText.getText().toString() + "&deviceid=" + getAndroidID()
							+ "&src=" + getApplication().getPackageName() + "&email=" + getEmail(RegisterYourSelf.this);

					try {
						proD.show();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					confirm.setEnabled(false);
					UShopRestClient.getUAHOY(RegisterYourSelf.this, url, null, responseHandler);
					pref.edit().putString(Constants.mobile_num, editText.getText().toString()).commit();

				} else {
					editText.setError("Enter Your Mobile Number to continue");
				}
			}
		});

	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			outState.putString("text", editText.getText().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	//Handler Invoked after the Registration happened.
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub

			try {
				String response = new String(arg2, "UTF-8");
				// Log.i(getClass().getSimpleName(), "response: "+response);

				try {
					confirm.setEnabled(true);
					proD.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Log.i(getClass().getSimpleName(), "Response: " + response);
				if (response.trim().equalsIgnoreCase("success")) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							Intent nextIntent = new Intent(RegisterYourSelf.this, ConfirmNumber.class);
							startActivity(nextIntent);
							RegisterYourSelf.this.finish();

						}
					}, 500);

				} else {
					try {
						Toast.makeText(RegisterYourSelf.this, "Something went wrong. Please try again",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				confirm.setEnabled(true);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			arg3.printStackTrace();

			try {
				confirm.setEnabled(true);
				proD.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Toast.makeText(RegisterYourSelf.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

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
