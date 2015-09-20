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
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ahoy.analytics.AHTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ConfirmNumber extends SherlockActivity {

	private ActionBar actionbar;
	private android.widget.EditText editText;
	private Button confirm, resend;
	private TextView txtView, textview1;
	private TextView txtViewNext;
	private SharedPreferences pref;
	private ProgressDialog proD;
	Typeface tff;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.confirmyournumber);

		// if (USHOP.launch) {
		//
		// Intent intent = new Intent(this, SplashScreen.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }

		actionbar = getSupportActionBar();

		tff = Typeface.createFromAsset(this.getAssets(), "Sansation_Regular.ttf");
		SpannableString s = new SpannableString("Confirm Your Number");
		s.setSpan(new ActionbarCus("", tff), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionbar.setTitle(s);
		actionbar.show();

		proD = new ProgressDialog(ConfirmNumber.this);
		proD.setMessage("please wait...");
		proD.setCancelable(false);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		txtView = (TextView) findViewById(R.id.txtView);
		txtViewNext = (TextView) findViewById(R.id.txtViewNext);
		textview1 = (TextView) findViewById(R.id.txtView1);

		try {
			textview1.setText("We have sent you a 4 digit code via sms to \n"
					+ pref.getString(Constants.mobile_num, "NA").substring(0, 3) + "*****"
					+ pref.getString(Constants.mobile_num, "NA").substring(8,
							pref.getString(Constants.mobile_num, "NA").length())
					+ "\nTo confirm your code, enter the code below");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		txtView.setText(Html.fromHtml("<font color=#e58911>shop</font>"));
		txtViewNext.setText(Html.fromHtml("<b><font color=#25ad53>EZZY</font></b>"));

		editText = (android.widget.EditText) findViewById(R.id.edittext);
		editText.setTypeface(tff);
		confirm = (Button) findViewById(R.id.confirmB);
		confirm.setTypeface(tff);

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (editText.getText() != null && editText.getText().length() > 0) {
					try {
						confirmMobileNumber(editText.getText().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Toast.makeText(ConfirmNumber.this, "Kindly Enter Your Confirmation Code", Toast.LENGTH_SHORT)
								.show();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		resend = (Button) findViewById(R.id.resend);
		resend.setTypeface(tff);

		resend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String url = "register?msisdn=" + pref.getString(Constants.mobile_num, "NA") + "&deviceid="
						+ getAndroidID() + "&src=" + getApplication().getPackageName() + "&email="
						+ getEmail(ConfirmNumber.this);

				// String url =
				// "oauth/generate?apikey=05e4baa076ff08c3639ccb0a62c0c150&src="
				// + getApplication().getPackageName()
				// + "&msisdn="
				// + pref.getString(Constants.mobile_num, "NA")
				// + "&mode=0&apv=1.0";

				try {
					proD.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UShopRestClient.getUAHOY(ConfirmNumber.this, url, null, responseHandlerResend);
			}
		});
	}

	AsyncHttpResponseHandler responseHandlerResend = new AsyncHttpResponseHandler() {

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
				// Log.i(getClass().getSimpleName(), "Response: " + response);
				if (response.trim().equalsIgnoreCase("success")) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

						}
					}, 500);

				} else {
					try {
						Toast.makeText(ConfirmNumber.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT)
								.show();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					proD.dismiss();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			arg3.printStackTrace();

			try {
				proD.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Toast.makeText(ConfirmNumber.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	// Fetching Device Email
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

	// Fetching AndroidId
	String getAndroidID() {
		return android.provider.Settings.Secure.getString(this.getContentResolver(), "android_id");
	}



	// For Mobile Number Confirmation
	public void confirmMobileNumber(String otpCode) {

		String url = "verifyotp?msisdn=" + pref.getString(Constants.mobile_num, "NA") + "&otp=" + otpCode;
		//
		// String url =
		// "oauth/verify?apikey=05e4baa076ff08c3639ccb0a62c0c150&msisdn="
		// + pref.getString(Constants.mobile_num, "NA")
		// + "&auth="
		// + otpCode + "&apv=1.0";
		try {
			proD.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UShopRestClient.getUAHOY(ConfirmNumber.this, url, null, responseHandler);
	}


	// Handler invoked after fetching user info
	AsyncHttpResponseHandler responseHandlerUser = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub
			// Toast.makeText(ConfirmNumber.this, "Something went wrong.",
			// Toast.LENGTH_SHORT).show();

			try {
				proD.dismiss();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ConfirmNumber.this.finish();
				}
			}, 200);

			Intent nextIntent = new Intent(ConfirmNumber.this, CitySelection.class);
			startActivity(nextIntent);
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
					try {
						proD.dismiss();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ConfirmNumber.this.finish();
						}
					}, 200);

					Intent nextIntent = new Intent(ConfirmNumber.this, CitySelection.class);
					startActivity(nextIntent);
				} else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ConfirmNumber.this.finish();
						}
					}, 200);

					Intent nextIntent = new Intent(ConfirmNumber.this, CitySelection.class);
					startActivity(nextIntent);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block

				try {
					proD.dismiss();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ConfirmNumber.this.finish();
					}
				}, 200);

				Intent nextIntent = new Intent(ConfirmNumber.this, CitySelection.class);
				startActivity(nextIntent);

				e.printStackTrace();
			}
		}

	};

	// Handler invoked after verifying OTP Status
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub

			try {
				String response = new String(arg2, "UTF-8");
				// Log.i(getClass().getSimpleName(), "---" + response);
				if (response.trim().equalsIgnoreCase("Verified")) {
					pref.edit().putString(Constants.otp_status, "verified").commit();

					try {
						Toast.makeText(ConfirmNumber.this, response, Toast.LENGTH_SHORT).show();
						// proD.dismiss();
						proD.setMessage("Loading...");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					UShopRestClient.getUAHOY(ConfirmNumber.this,
							"app.viewUser?msisdn=" + pref.getString(Constants.mobile_num, "NA"), null,
							responseHandlerUser);

					// new Handler().postDelayed(new Runnable() {
					//
					// @Override
					// public void run() {
					// // TODO Auto-generated method stub
					// ConfirmNumber.this.finish();
					// }
					// }, 200);
					//
					// Intent nextIntent = new Intent(ConfirmNumber.this,
					// CitySelection.class);
					// startActivity(nextIntent);
				} else {
					Toast.makeText(ConfirmNumber.this, response, Toast.LENGTH_SHORT).show();
					try {
						proD.dismiss();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					proD.dismiss();
					Toast.makeText(ConfirmNumber.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT)
							.show();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			// TODO Auto-generated method stub

			arg3.printStackTrace();
			try {
				proD.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see com.actionbarsherlock.app.SherlockActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			// Log.i(getClass().getSimpleName(), "In the resume page");
			if (getIntent().hasExtra("confirmationCode")) {
				String code = getIntent().getExtras().getString("confirmationCode");
				// Log.i(getClass().getSimpleName(), "Code is: "+code);
				String codeF = String.valueOf(code);
				if (codeF.length() == 4) {
					editText.setText(codeF);
					confirmMobileNumber(codeF);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
