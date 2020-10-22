package com.ezymd.restaurantapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class SuspendKeyPad {
	public static void suspendKeyPad(Context context)
	{
		try
		{
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
		}
		catch(Exception e)
		{

		}
	}
}
