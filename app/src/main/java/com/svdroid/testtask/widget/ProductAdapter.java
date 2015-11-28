package com.svdroid.testtask.widget;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svdroid.testtask.R;
import com.svdroid.testtask.activity.ProductsActivity;
import com.svdroid.testtask.provider.Contract;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.PViewHolder>
	implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final int PRODUCT_LOADER_ID = 100;

	private Context mContext;
	private ArrayList<Pair<String, String>> mProducts;
	private OnItemClickListener mListener;

	public ProductAdapter(Context context, OnItemClickListener listener)
	{
		mContext = context;
		mListener = listener;
		mProducts = new ArrayList<>();
		((ProductsActivity) mContext).getLoaderManager().restartLoader(
			PRODUCT_LOADER_ID,
			null,
			this
		);
	}

	@Override
	public PViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		final View view = LayoutInflater.from(mContext).inflate(R.layout.product_item_view, parent, false);
		return new PViewHolder(view);
	}

	@Override
	public void onBindViewHolder(PViewHolder holder, int position)
	{
		holder.set();
	}

	@Override
	public int getItemCount()
	{
		return mProducts.size();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		return new CursorLoader(
			mContext,
			Contract.Product.CONTENT_URI,
			Contract.Product.PROJECTION,
			null,
			null,
			null
		);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		if (data == null) {
			return;
		}

		for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
			Pair<String, String> element = new Pair<>(data.getString(1), data.getString(2));
			mProducts.add(element);
		}

		notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		mProducts.clear();
	}

	protected class PViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView mSKU;
		private final TextView mTransactionCount;

		public PViewHolder(View itemView)
		{
			super(itemView);
			mSKU = (TextView) itemView.findViewById(R.id.sku);
			mTransactionCount = (TextView) itemView.findViewById(R.id.transaction_count);
		}

		public void set()
		{
			mSKU.setText(mProducts.get(getLayoutPosition()).first);
			int transactionCaunt = mProducts.get(getLayoutPosition()).second.split(",").length;
			mTransactionCount.setText(String.format(mContext.getResources()
				.getString(R.string.hint_transactions_for_sku), transactionCaunt));

			if (mListener != null && !itemView.hasOnClickListeners()) {
				itemView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						final int position = getLayoutPosition();
						String sku = mProducts.get(position).first;
						String transactionIds = mProducts.get(position).second;
						mListener.onItemClick(position, sku, parseTransactionIds(transactionIds));
					}
				});
			}
		}

		private int[] parseTransactionIds(String transactionIds)
		{
			final String[] stringIds = transactionIds.split(",");
			final int[] intIds = new int[stringIds.length];

			for (int i = 0; i < stringIds.length; i++) {
				intIds[i] = Integer.parseInt(stringIds[i]);
			}

			return intIds;
		}
	}

	public interface OnItemClickListener
	{
		void onItemClick(int position, String sku, int[] transactionIds);
	}
}
