package com.android.intentfuzzer.db;

import com.android.intentfuzzer.auto.ExceptionInfo;
import com.android.intentfuzzer.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String COLUMN_PACKAGE_NAME = "package_name";
	
	private static final String COLUMN_EXCEPTION_TYPE = "exception_type";
	
	private static final String COLUMN_EXCEPTION_DETAIL = "exception_deatil";
	
	private static final String COLUMN_EXCEPTION_TIMESTAMP = "timestamp";
	
	private static final String DB_DATABASE = "result.db";
	
	private static final String DB_TEST_TABLE = "test_result";
	
	public DBHelper(Context context) {
		this(context, DB_DATABASE, null, 1);
	}

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStmt = "CREATE TABLE IF NOT EXISTS " + DB_TEST_TABLE + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ COLUMN_PACKAGE_NAME + " TEXT, "
				+ COLUMN_EXCEPTION_TYPE + " TEXT," 
				+ COLUMN_EXCEPTION_DETAIL + " TEXT,"
				+ COLUMN_EXCEPTION_TIMESTAMP + " BIGINT)";
		db.execSQL(sqlStmt);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(" DROP TABLE IF EXISTS " + DB_TEST_TABLE);
	    onCreate(db);
	}
	
	public void insertExceptionInfo(ExceptionInfo info) {
		Utils.d(DBHelper.class, "insert into DB:" + info.toString());
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_PACKAGE_NAME, info.getmCrashPackageName());
		values.put(COLUMN_EXCEPTION_TYPE, info.getmExceptionName());
		values.put(COLUMN_EXCEPTION_DETAIL, info.getmExceptionMessage());
		values.put(COLUMN_EXCEPTION_TIMESTAMP, info.getmCrashTime());
		db.insert(DB_TEST_TABLE, null, values);
		db.close();
	}

}
