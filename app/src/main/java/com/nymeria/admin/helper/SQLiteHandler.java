/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.nymeria.admin.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "farhatty-admin";

	// Login table name
	private static final String TABLE_USER = "user";

	// Login Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
    private static final String KEY_CID = "cid";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PHONE1 = "phone1";
	private static final String KEY_PHONE2 = "phone2";
	private static final String KEY_SUPERNAME= "supername";
	private static final String KEY_STATUS= "stats";
	private static final String KEY_LOC= "adress";
    private static final String KEY_CAT = "CatID";
	private static final String KEY_COMP = "comp";
	private static final String KEY_UID = "uid";
	private static final String KEY_CREATED_AT = "created_at";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_CID + " TEXT,"
				+ KEY_EMAIL + " TEXT," + KEY_PHONE1 + " TEXT," + KEY_PHONE2 + " TEXT," + KEY_SUPERNAME + " TEXT," + KEY_STATUS+ " TEXT,"  + KEY_LOC + " TEXT,"  + KEY_CAT + " TEXT," + KEY_COMP + " TEXT," + KEY_UID + " TEXT,"
				+ KEY_CREATED_AT + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String cid, String email , String phone1 , String phone2,  String supername ,  String stats ,  String adress , String CatID, String comp, String uid, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name); // Name
        values.put(KEY_CID, cid); // Name
        values.put(KEY_EMAIL, email); // Email
		values.put(KEY_PHONE1, phone1); // Email
		values.put(KEY_PHONE2, phone2); // Email
		values.put(KEY_SUPERNAME, supername); // Email
		values.put(KEY_STATUS, stats); // Email
		values.put(KEY_LOC, adress); // Email
		values.put(KEY_CAT, CatID); // Email
		values.put(KEY_COMP, comp); // Email
		values.put(KEY_UID, uid); // Email
		values.put(KEY_CREATED_AT, created_at); // Created At

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
            user.put("cid", cursor.getString(2));
			user.put("email", cursor.getString(3));
			user.put("phone1", cursor.getString(4));
			user.put("phone2", cursor.getString(5));
			user.put("supername", cursor.getString(6));
			user.put("stats", cursor.getString(7));
			user.put("adress", cursor.getString(8));
            user.put("CatID", cursor.getString(9));
			user.put("comp", cursor.getString(10));
			user.put("uid", cursor.getString(11));
			user.put("created_at", cursor.getString(12));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}
