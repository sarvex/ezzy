package com.shopezzy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

  private SharedPreferences pref;

  @Override
  public void onReceive(Context context, Intent intent) {

    if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

      Bundle localBundle = intent.getExtras();
      pref = PreferenceManager.getDefaultSharedPreferences(context);
      if (localBundle != null) {
        Object[] arrayOfObject = (Object[]) localBundle.get("pdus");

        for (int i = 0; i < arrayOfObject.length; i++) {
          SmsMessage localSmsMessage = SmsMessage.createFromPdu((byte[]) arrayOfObject[i]);
          String incomingNumber = localSmsMessage.getDisplayOriginatingAddress();
          String incomingSMS = localSmsMessage.getDisplayMessageBody();

          if (incomingNumber.trim().equals("DZ-SHOPEZ") && incomingSMS != null) {
            try {

              String confirmCode = incomingSMS.split(" ")[0].trim();
              if (pref != null
                  && pref.getString(Constants.otp_status, "NA").equalsIgnoreCase("verified")) {
                return;
              }
              Intent nextIntent = new Intent(context, ConfirmNumber.class);
              nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              nextIntent.putExtra("confirmationCode", confirmCode);
              context.startActivity(nextIntent);
            } catch (NumberFormatException e) {
              e.printStackTrace();
            } catch (Exception e) {
            }

          } else {
            return;
          }
        }
      }
    }

  }

}
