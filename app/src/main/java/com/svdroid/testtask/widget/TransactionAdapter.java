package com.svdroid.testtask.widget;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svdroid.testtask.Application;
import com.svdroid.testtask.R;
import com.svdroid.testtask.activity.ProductsActivity;
import com.svdroid.testtask.activity.TransactionsActivity;
import com.svdroid.testtask.data.Transaction;
import com.svdroid.testtask.provider.Contract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TViewHolder>
	implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final int TRANSACTION_LOADER_ID = 200;

	private Context mContext;
	private TextView mTotoal;
	private ArrayList<Transaction> mTransactions;

	public TransactionAdapter(Context context, TextView total, int[] transactionIds)
	{
		mContext = context;
		mTotoal = total;
		mTransactions = new ArrayList<>();

		final Bundle bundle = new Bundle(1);
		bundle.putIntArray(ProductsActivity.KEY_TRANSACTION_IDS, transactionIds);

		((TransactionsActivity) mContext).getLoaderManager().restartLoader(
			TRANSACTION_LOADER_ID,
			bundle,
			this
		);
	}

	@Override
	public TViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		final View view = LayoutInflater.from(mContext).inflate(R.layout.transaction_item_view, parent, false);
		return new TViewHolder(view);
	}

	@Override
	public void onBindViewHolder(TViewHolder holder, int position)
	{
		holder.set();
	}

	@Override
	public int getItemCount()
	{
		return mTransactions.size();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		int[] transactionIds = args.getIntArray(ProductsActivity.KEY_TRANSACTION_IDS);

		return new CursorLoader(
			mContext,
			Contract.Transaction.CONTENT_URI,
			Contract.Transaction.PROJECTION,
			Contract.Transaction.buildWhereStatement(transactionIds),
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

		Double total = 0.0;

		for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
			final Transaction transaction = new Transaction();

			transaction.id = data.getInt(0);
			transaction.amount = data.getDouble(1);
			transaction.currency = data.getString(2);

			mTransactions.add(transaction);

			final double rate = Application.CURRENCY_MUP.containsKey(transaction.currency)
				? Application.CURRENCY_MUP.get(transaction.currency)
				: 1.0;
			total += BigDecimal.valueOf(transaction.amount).multiply(BigDecimal.valueOf(rate)).doubleValue();
		}

		mTotoal.setText(
			String.format(
				mContext.getString(R.string.title_total_count),
				Currency.getInstance("GBP").getSymbol(),
				total
			)
		);
		notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{

	}

	protected class TViewHolder extends RecyclerView.ViewHolder
	{

		private final TextView mCurrency;
		private final TextView mGBP;

		public TViewHolder(View itemView)
		{
			super(itemView);
			mCurrency = (TextView) itemView.findViewById(R.id.currency);
			mGBP = (TextView) itemView.findViewById(R.id.gbp);
		}

		public void set()
		{
			final Transaction transaction = mTransactions.get(getLayoutPosition());
			mCurrency.setText(String.format(
				"%s %4.3f",
				Currency.getInstance(transaction.currency).getSymbol(),
				transaction.amount
			));
			final double rate = Application.CURRENCY_MUP.containsKey(transaction.currency)
				? Application.CURRENCY_MUP.get(transaction.currency)
				: 1.0;
			mGBP.setText(String.format(
				"%s %4.3f",
				Currency.getInstance("GBP").getSymbol(),
				BigDecimal.valueOf(transaction.amount).multiply(BigDecimal.valueOf(rate))
			));
		}
	}
}
