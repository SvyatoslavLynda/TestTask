package com.svdroid.testtask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.svdroid.testtask.R;
import com.svdroid.testtask.service.DataLoaderService;
import com.svdroid.testtask.widget.ProductAdapter;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener
{
	private static final String TAG = "ProductsActivity";
	public static final String KEY_SKU = TAG + ".KEY_SKU";
	public static final String KEY_TRANSACTION_IDS = TAG + ".KEY_TRANSACTION_IDS";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		startServiceForLoadData();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products);

		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
			this,
			LinearLayoutManager.VERTICAL,
			false
		);
		final ProductAdapter adapter = new ProductAdapter(this, this);
		final RecyclerView products = (RecyclerView) findViewById(R.id.products);
		products.setLayoutManager(linearLayoutManager);
		products.setAdapter(adapter);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle(getResources().getString(R.string.title_products));
			actionBar.setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public void onItemClick(int position, String sku, int[] transactionIds)
	{
		Intent intent = new Intent(this, TransactionsActivity.class);
		intent.putExtra(KEY_SKU, sku);
		intent.putExtra(KEY_TRANSACTION_IDS, transactionIds);
		startActivity(intent);
	}

	private void startServiceForLoadData()
	{
		startService(new Intent(this, DataLoaderService.class));
	}
}
