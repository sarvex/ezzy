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
  }

  class DownloadBitmap extends AsyncTask<Void, Void, Bitmap> {

    Bundle extras;

    public DownloadBitmap(Bundle extras) {
      this.extras = extras;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      super.onPostExecute(result);
      try {
        if (result != null) {
          generateNotification1(result, extras.getString("message"));
        } else {
          generateNotification(extras.getString("message"), null);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
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
    final Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    String messageType = gcm.getMessageType(intent);
    if (!extras.isEmpty()) {

      if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

        if (extras.getString("url") != null && Build.VERSION.SDK_INT >= 16) {

          new DownloadBitmap(extras).execute();

        } else {
          try {
            generateNotification(extras.getString("message"), null);
          } catch (Exception e) {
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

      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      PendingIntent intent = PendingIntent.getActivity(this, requestID, notificationIntent, 0);

      notification.setLatestEventInfo(this, title, message, intent);
      notification.flags |= Notification.FLAG_AUTO_CANCEL;

      // Play default notification sound
      notification.defaults |= Notification.DEFAULT_SOUND;
      notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification);
    } catch (Exception e) {
      e.printStackTrace();
    }
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
      Intent notificationIntent = new Intent(this, SplashScreen.class);
      notificationIntent.putExtra(Constants.KEY_PUSH_MESSAGE, mess);
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

      stackBuilder.addNextIntent(notificationIntent);
      PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      builder.setContentIntent(resultPendingIntent);

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

    // Vibrate if vibrate is enabled
    notification.defaults |= Notification.DEFAULT_VIBRATE;
    notificationManager.notify(0, notification);

  }

}
