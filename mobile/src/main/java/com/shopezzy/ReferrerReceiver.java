package com.shopezzy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.URLDecoder;

public class ReferrerReceiver extends BroadcastReceiver {

	SharedPreferences prefRecv;

	public ReferrerReceiver() {
		// TODO Auto-generated constructor stub
		// Log.i(getClass().getSimpleName(), "In the ReferrerReceiver");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {

			if ((null != intent)
					&& (intent.getAction()
							.equals("com.android.vending.INSTALL_REFERRER"))) {
				String rawReferrer = intent.getStringExtra("referrer");
				if (null != rawReferrer) {
					// Log.i(getClass().getSimpleName(),
					// "Before URLDecoder"+rawReferrer);
					String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
					// Log.i(getClass().getSimpleName(),
					// "After URLDecoder"+referrer);
					// Toast.makeText(context, referrer, Toast.LENGTH_SHORT);
					prefRecv = PreferenceManager
							.getDefaultSharedPreferences(context);
					prefRecv.edit().putString("referrer", referrer).commit();

					// Toast.makeText(context, "Intent is Null",
					// Toast.LENGTH_SHORT);
					// Log.i(getClass().getSimpleName(),
					// "After Commit Preferrences");
				}
			} else {
				// Toast.makeText(context, "Intent is Null",
				// Toast.LENGTH_SHORT);
				// Log.i(getClass().getSimpleName(), "Intent is Null");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
