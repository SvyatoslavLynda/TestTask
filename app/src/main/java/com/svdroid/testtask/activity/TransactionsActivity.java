package com.svdroid.testtask.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.svdroid.testtask.R;
import com.svdroid.testtask.widget.TransactionAdapter;

public class TransactionsActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
			this,
			LinearLayoutManager.VERTICAL,
			false
		);
		final TextView total = (TextView) findViewById(R.id.total_count);
		final TransactionAdapter adapter = new TransactionAdapter(this, total, getTransactionsIds());
		final RecyclerView products = (RecyclerView) findViewById(R.id.product_transactions);
		products.setLayoutManager(linearLayoutManager);
		products.setAdapter(adapter);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(String.format(getResources().getString(R.string.title_transactions_for_sku), getSKU()));
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private String getSKU()
	{
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			return extras.getString(ProductsActivity.KEY_SKU, "");
		}

		return null;
	}

	private int[] getTransactionsIds()
	{
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			return extras.getIntArray(ProductsActivity.KEY_TRANSACTION_IDS);
		}

		return null;
	}
}
