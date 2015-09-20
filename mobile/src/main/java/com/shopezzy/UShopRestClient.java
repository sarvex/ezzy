package com.shopezzy;

//import android.util.Log;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;

public class UShopRestClient {

	private static final String BASE_URL = "http://shopezzy.com/ushop/";
	private static final String UAHOY_URL = "http://shopezzy.com/";
	private static final String SHOPEEZY_URL = "http://www.shopezzy.com/api/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		// Log.i("USHOPRESTCLIENT", "URL: " + getAbsoluteUrl(url));
		client.setTimeout(1 * 60 * 1000);

		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void getUAHOY(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		 Log.i("USHOPRESTCLIENT", "URL: " + getAbsoluteUrlUAHOY(url));
		client.get(getAbsoluteUrlUAHOY(url), params, responseHandler);
	}

	@SuppressWarnings("deprecation")
	public static void postUAHOYFull(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		Log.i("USHOPRESTCLIENT", url);
		client.post(context, url, params, responseHandler);
	}

	@SuppressWarnings("deprecation")
	public static void postUAHOY(Context context, String url, HttpEntity entity,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);

		client.post(context, getAbsoluteUrlUAHOY(url), entity, null, responseHandler);
	}

	public static void getUAHOYFull(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		Log.i("USHOPRESTCLIENT", "URL: " + url);
		client.get(url, params, responseHandler);
	}

	public static void getSHOPEZZY(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		 Log.i("USHOPRESTCLIENT", "URL: " + getAbsoluteUrlUAHOY(url));
		client.get(getAbsoluteUrlSHOPEZZY(url), params, responseHandler);
	}

	public static void getSHOPEZZYFULLURL(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		 Log.i("USHOPRESTCLIENT", url);
		 client.setTimeout(30000);
		client.get(url, params, responseHandler);

	}

	public static void cancelRequest(Context context) {
		client.cancelRequests(context, true);
	}

	public static void post(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(1 * 60 * 1000);
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		// Log.i("USHOPRESTCLIENT", "Base URL: " + relativeUrl);
		return BASE_URL + relativeUrl;
	}

	private static String getAbsoluteUrlUAHOY(String relativeUrl) {
		// Log.i("USHOPRESTCLIENT", "UAHOY_URL URL: " + relativeUrl);
		return UAHOY_URL + relativeUrl;
	}

	private static String getAbsoluteUrlSHOPEZZY(String relativeUrl) {
		// Log.i("USHOPRESTCLIENT", "UAHOY_URL URL: " + relativeUrl);
		return SHOPEEZY_URL + relativeUrl;
	}

}
