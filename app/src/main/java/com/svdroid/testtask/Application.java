package com.svdroid.testtask;

import java.util.HashMap;
import java.util.Map;

public class Application extends android.app.Application
{
	public static final Map<String, Double> CURRENCY_MUP = new HashMap<>();

	@Override
	public void onCreate()
	{
		super.onCreate();
	}
}
