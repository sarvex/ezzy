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
		// TODO Auto-generated method stub

		if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

			// Log.i(getClass().getSimpleName(), "in receive");
			Bundle localBundle = intent.getExtras();
			pref = PreferenceManager.getDefaultSharedPreferences(context);
			if (localBundle != null) {
				Object[] arrayOfObject = (Object[]) localBundle.get("pdus");

				for (int i = 0; i < arrayOfObject.length; i++) {
					SmsMessage localSmsMessage = SmsMessage.createFromPdu((byte[]) arrayOfObject[i]);
					String incomingNumber = localSmsMessage.getDisplayOriginatingAddress();
					String incomingSMS = localSmsMessage.getDisplayMessageBody();

//					Log.i(getClass().getSimpleName(), "Incoming Message is: " + incomingNumber);
					if (incomingNumber.trim().equals("DZ-SHOPEZ") && incomingSMS != null) {
						// String s = "Thanks for registering with Shopezzy.
						// Your One Time Password is ";

						// Log.i(getClass().getSimpleName(), "Code-"
						// + confirmationCode);
						// if (confirmationCode.length() > 4)
						// return;
						try {

							String confirmCode = incomingSMS.split(" ")[0].trim();
							// Log.i(getClass().getSimpleName(),
							// "Here is the code"+confirmCode);
							if (pref != null
									&& pref.getString(Constants.otp_status, "NA").equalsIgnoreCase("verified")) {
								return;
							}
							Intent nextIntent = new Intent(context, ConfirmNumber.class);
							nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							nextIntent.putExtra("confirmationCode", confirmCode);
							context.startActivity(nextIntent);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO: handle exception
						}

					} else {
						return;
					}
				}
			}
		}

	}

}
