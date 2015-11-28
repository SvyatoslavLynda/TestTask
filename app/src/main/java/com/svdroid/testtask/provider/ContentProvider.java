package com.svdroid.testtask.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ContentProvider extends android.content.ContentProvider
{
	private static final int PRODUCTS = 100;
	private static final int PRODUCTS_ID = 102;
	private static final int TRANSACTIONS = 200;
	private static final int TRANSACTIONS_ID = 202;

	private static final UriMatcher URI_MATCHER = buildUriMatcher();

	private DBHelper mDBHelper;

	@Override
	public boolean onCreate()
	{
		mDBHelper = new DBHelper(getContext());
		return true;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor cursor;

		switch (URI_MATCHER.match(uri)) {
			case PRODUCTS:
				cursor = mDBHelper.getReadableDatabase().query(
					Contract.Product.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
				break;
			case PRODUCTS_ID:
				cursor = mDBHelper.getReadableDatabase().query(
					Contract.Product.TABLE_NAME,
					projection,
					Contract.Product._ID + "='" + ContentUris.parseId(uri) + "'",
					null,
					null,
					null,
					sortOrder
				);
				break;
			case TRANSACTIONS:
				cursor = mDBHelper.getReadableDatabase().query(
					Contract.Transaction.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
				break;
			case TRANSACTIONS_ID:
				cursor = mDBHelper.getReadableDatabase().query(
					Contract.Transaction.TABLE_NAME,
					projection,
					Contract.Transaction._ID + "='" + ContentUris.parseId(uri) + "'",
					null,
					null,
					null,
					sortOrder
				);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri)
	{
		switch (URI_MATCHER.match(uri)) {
			case PRODUCTS:
				return Contract.Product.CONTENT_TYPE;
			case PRODUCTS_ID:
				return Contract.Product.CONTENT_ITEM_TYPE;
			case TRANSACTIONS:
				return Contract.Transaction.CONTENT_TYPE;
			case TRANSACTIONS_ID:
				return Contract.Transaction.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri " + uri);
		}
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values)
	{
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		final Uri returnUri;
		final long id;

		switch (URI_MATCHER.match(uri)) {
			case PRODUCTS:
				id = db.insert(Contract.Product.TABLE_NAME, null, values);
				if (id > 0) {
					returnUri = Contract.Product.getContentUri(id);
				} else {
					throw new android.database.SQLException("Failed to insert row into " + uri);
				}
				break;
			case TRANSACTIONS:
				id = db.insert(Contract.Transaction.TABLE_NAME, null, values);
				if (id > 0) {
					returnUri = Contract.Transaction.getContentUri(id);
				} else {
					throw new android.database.SQLException("Failed to insert row into " + uri);
				}
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		return returnUri;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}

	private static UriMatcher buildUriMatcher()
	{
		final String authority = Contract.AUTHORITY;
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(authority, "products", PRODUCTS);
		matcher.addURI(authority, "products_id/#", PRODUCTS_ID);
		matcher.addURI(authority, "transactions", TRANSACTIONS);
		matcher.addURI(authority, "transactions_id/#", TRANSACTIONS_ID);

		return matcher;
	}
}
