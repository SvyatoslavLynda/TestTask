package com.svdroid.testtask.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "test_task.db";

	public DBHelper(Context context)
	{
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + Contract.Product.TABLE_NAME + " (" +
			Contract.Product._ID + " INTEGER PRIMARY KEY, " +
			Contract.Product.SKU + " TEXT NOT NULL, " +
			Contract.Product.TRANSACTION_IDS + " TEXT NOT NULL);";

		final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + Contract.Transaction.TABLE_NAME + " (" +
			Contract.Transaction._ID + " INTEGER PRIMARY KEY, " +
			Contract.Transaction.AMOUNT + " REAL NOT NULL, " +
			Contract.Transaction.CURRENCY + " TEXT NOT NULL);";

		db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
		db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + Contract.Product.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Contract.Transaction.TABLE_NAME);
		onCreate(db);
	}
}
