package com.svdroid.testtask.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract
{
	public static final String AUTHORITY = "com.svdroid.testtask";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Product implements BaseColumns
	{
		public static final String TABLE_NAME = "products";
		public static final String SKU = "sku";
		public static final String TRANSACTION_IDS = "transaction_ids";

		public static final String[] PROJECTION = {
			_ID,
			SKU,
			TRANSACTION_IDS
		};

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.testtask.products";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.testtask.products";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "products");

		public static Uri getContentUri(long productId)
		{
			return ContentUris.withAppendedId(CONTENT_URI, productId);
		}
	}

	public static final class Transaction implements BaseColumns
	{
		public static final String TABLE_NAME = "transactions";
		public static final String AMOUNT = "amount";
		public static final String CURRENCY = "currency";

		public static final String[] PROJECTION = {
			_ID,
			AMOUNT,
			CURRENCY
		};

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.testtask.transactions";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.testtask.transactions";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "transactions");

		public static Uri getContentUri(long transactionId)
		{
			return ContentUris.withAppendedId(CONTENT_URI, transactionId);
		}

		public static String buildWhereStatement(int[] transactionIds) {
			StringBuilder sb = new StringBuilder(transactionIds.length * 7);
			sb.append(_ID);
			sb.append(" IN(");
			sb.append(transactionIds[0]);
			for (int i = 1; i < transactionIds.length; i++) {
				sb.append(", ");
				sb.append(transactionIds[i]);
			}
			sb.append(')');
			return sb.toString();
		}
	}
}
