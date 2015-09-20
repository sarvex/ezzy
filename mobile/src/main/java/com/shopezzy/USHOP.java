package com.shopezzy;

import android.app.Application;

import com.splunk.mint.Mint;

public class USHOP extends Application {

	public static boolean launch = true;

	// public ArrayList<Locality> locality;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Mint.initAndStartSession(this, "3d1b9390");
	}
}
