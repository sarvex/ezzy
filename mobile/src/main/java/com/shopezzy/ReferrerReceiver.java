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
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    try {

      if ((null != intent)
          && (intent.getAction()
          .equals("com.android.vending.INSTALL_REFERRER"))) {
        String rawReferrer = intent.getStringExtra("referrer");
        if (null != rawReferrer) {
          String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
          prefRecv = PreferenceManager.getDefaultSharedPreferences(context);
          prefRecv.edit().putString("referrer", referrer).commit();
        }
      } else {
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
