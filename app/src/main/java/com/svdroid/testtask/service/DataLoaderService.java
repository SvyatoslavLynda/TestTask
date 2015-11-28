package com.svdroid.testtask.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import com.google.gson.Gson;
import com.svdroid.testtask.Application;
import com.svdroid.testtask.data.Currency;
import com.svdroid.testtask.data.Transaction;
import com.svdroid.testtask.provider.Contract;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class DataLoaderService extends IntentService
{
	private static final String LOG_TAG = "DataLoaderService";
	private static final float CAPACITY_FACTOR = 0.75f;

	public DataLoaderService()
	{
		super("DataLoaderService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		getCurrency();
		insertTransactionToDB();
	}

	private String readFile(String fileName)
	{
		final StringBuilder buf = new StringBuilder();
		try {
			final InputStream io = getAssets().open(fileName);
			final BufferedReader in = new BufferedReader(new InputStreamReader(io, "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				buf.append(str);
			}

			in.close();
			return new JSONArray(buf.toString()).toString();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void getCurrency()
	{
		final String json = readFile("rates.json");

		if (json != null) {
			final Gson gson = new Gson();
			final Currency[] currencies = gson.fromJson(json, Currency[].class);

			for (Currency currency : currencies) {
				String to = currency.to;
				if (to.equals("GBP")) {
					Application.CURRENCY_MUP.put(currency.from, currency.rate);
				} else {
					if (!Application.CURRENCY_MUP.containsKey(to)) {
						Application.CURRENCY_MUP.put(to, searchByTo(currencies, currency.from, currency.to));
					}
				}
			}

			Application.CURRENCY_MUP.remove("GBP");
			Log.d(LOG_TAG, Application.CURRENCY_MUP.toString());
		}
	}

	private double searchByTo(Currency[] currencies, String from, String to)
	{
		for (Currency currency : currencies) {
			if (currency.from.equals(to) && currency.to.equals(from)) {
				Log.d(LOG_TAG, String.valueOf(currency.rate));

				for (Currency c : currencies) {
					if (c.from.equals(from) && c.to.equals("GBP")) {
						Log.d(LOG_TAG, String.valueOf(c.rate));
						return BigDecimal.valueOf(currency.rate).multiply(BigDecimal.valueOf(c.rate)).doubleValue();
					}
				}

			}
		}

		return 0;
	}

	private void insertTransactionToDB()
	{
		final String json = readFile("transactions.json");

		if (json != null) {
			final Gson gson = new Gson();
			final Transaction[] transactions = gson.fromJson(json, Transaction[].class);
			final int capacityFactor = (int) (transactions.length * CAPACITY_FACTOR);
			final HashMap<String, ArrayList<Transaction>> map = new HashMap<>(capacityFactor);

			for (int i = 0; i < transactions.length; i++) {
				final Transaction transaction = transactions[i];
				transaction.id = i + 1;

				if (map.containsKey(transaction.sku)) {
					map.get(transaction.sku).add(transaction);
				} else {
					final ArrayList<Transaction> values = new ArrayList<>();
					values.add(transaction);
					map.put(transaction.sku, values);
				}
			}

			ContentValues[] contentValues = new ContentValues[map.keySet().size()];

			int i = 0;
			for (String sku : map.keySet()) {
				contentValues[i] = new ContentValues();
				contentValues[i].put(Contract.Product._ID, i + 1);
				contentValues[i].put(Contract.Product.SKU, sku);
				contentValues[i].put(Contract.Product.TRANSACTION_IDS, transactionIds(map.get(sku)));
				insertToTransactionTable(map.get(sku));
				i++;
			}

			try {
				getContentResolver().bulkInsert(Contract.Product.CONTENT_URI, contentValues);
				getContentResolver().notifyChange(Contract.Product.CONTENT_URI, null);
			} catch (SQLException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
	}

	private void insertToTransactionTable(ArrayList<Transaction> transactions)
	{
		ContentValues[] contentValues = new ContentValues[transactions.size()];
		for (int i = 0; i < transactions.size(); i++) {
			final Transaction transaction = transactions.get(i);
			contentValues[i] = new ContentValues();
			contentValues[i].put(Contract.Transaction._ID, transaction.id);
			contentValues[i].put(Contract.Transaction.CURRENCY, transaction.currency);
			contentValues[i].put(Contract.Transaction.AMOUNT, transaction.amount);
		}

		try {
			getContentResolver().bulkInsert(Contract.Transaction.CONTENT_URI, contentValues);
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	private String transactionIds(ArrayList<Transaction> transactions)
	{
		StringBuilder sb = new StringBuilder(transactions.size() * 7);
		sb.append(transactions.get(0).id);
		for (int i = 1; i < transactions.size(); i++) {
			sb.append(",");
			sb.append(transactions.get(i).id);
		}

		return sb.toString();
	}
}
