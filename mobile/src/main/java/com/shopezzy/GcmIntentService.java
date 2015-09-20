package com.shopezzy;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class GcmIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	class DownloadBitmap extends AsyncTask<Void, Void, Bitmap> {

		Bundle extras;

		public DownloadBitmap(Bundle extras) {
			// TODO Auto-generated constructor stub
			this.extras = extras;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (result != null) {
					generateNotification1(result, extras.getString("message"));
				} else {
					generateNotification(extras.getString("message"), null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Bitmap bmp = null;
			try {

				InputStream in = new URL(extras.getString("url")).openStream();
				bmp = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				// log error
			}
			return bmp;
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		final Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				// Log.i(TAG, "Completed work @ " +
				// SystemClock.elapsedRealtime());

				if (extras.getString("url") != null && Build.VERSION.SDK_INT >= 16) {

					new DownloadBitmap(extras).execute();

				} else {
					try {
						generateNotification(extras.getString("message"), null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} // Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void generateNotification(String message, Bitmap bitmap) {
		// int i=0;
		try {
			int requestID = (int) System.currentTimeMillis();

			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			@SuppressWarnings("deprecation")
			Notification notification = new Notification(icon, message, when);
			String title = this.getString(R.string.app_name);
			Intent notificationIntent = new Intent(this, SplashScreen.class);
			notificationIntent.putExtra(Constants.KEY_PUSH_MESSAGE, message);

			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// PendingIntent intent =
			// PendingIntent.getActivity(context, 0, notificationIntent,
			// PendingIntent.FLAG_ONE_SHOT);

			PendingIntent intent = PendingIntent.getActivity(this, requestID, notificationIntent, 0);

			notification.setLatestEventInfo(this, title, message, intent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;
			notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// notificationManager.notify(0, notification);
		// notificationManager.notify(i, notification);
		// i++;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void generateNotification1(Bitmap bitmap, String mess) {

		try {
			android.app.Notification.Builder builder = new Notification.Builder(this);
			Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			builder.setLargeIcon(largeIcon);

			builder.setTicker("shopEZZY");
			builder.setContentTitle("shopEZZY");
			builder.setContentText("View in full screen mode");
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setAutoCancel(true);

			Notification.BigPictureStyle bigPicutureStyle = new Notification.BigPictureStyle(builder);
			bigPicutureStyle.bigLargeIcon(largeIcon);
			bigPicutureStyle.bigPicture(bitmap);
			bigPicutureStyle.setBigContentTitle("shopEZZY");
			bigPicutureStyle.setSummaryText(mess);
			// Intent resultIntent = new Intent(this, SlidingList.class);
			Intent notificationIntent = new Intent(this, SplashScreen.class);
			notificationIntent.putExtra(Constants.KEY_PUSH_MESSAGE, mess);
			// notificationIntent.putExtra(Consts.KEY_PUSH_MESSAGE, mess);
			// The stack builder object will contain an artificial back stack
			// for
			// the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out
			// of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

			// Adds the back stack for the Intent (but not the Intent itself)
			// stackBuilder.addParentStack(SlidingList.class);

			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(notificationIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);

			// NotificationManager mNotificationManager = (NotificationManager)
			// getSystemService(Context.NOTIFICATION_SERVICE);
			//
			// // mId allows you to update the notification later on.
			// mNotificationManager.notify(100, mBuilder.build());
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(01, bigPicutureStyle.build());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void generateNotificationNew(Context context, String message) {

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, SplashScreen.class);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// //notification.sound = Uri.parse(
		// "android.resource://"
		// + context.getPackageName()
		// + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}
