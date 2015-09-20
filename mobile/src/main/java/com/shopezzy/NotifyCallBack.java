package com.shopezzy;

public interface NotifyCallBack {

	void passDataToIncrement(String value);

	void passDataToDecrement(String value);

	void finishActivity();

	void triggerAreas(String city);

	void callBackUpdatedResponse(String response);

	String getUpdatedResponse();

	String getUpdatedItems();
	
	void updateCart(String text);

}
