package com.himebaugh.employeedirectory;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class EmployeeProvider extends ContentProvider {

	private EmployeeDatabase mEmployeeDatabase;

	// Uri matcher to decode incoming URIs.
	private final UriMatcher mUriMatcher;

	private static final String AUTHORITY = "com.himebaugh.employeedirectory.EmployeeProvider";
	
	// content mime types
	public static final String BASE_DATA_NAME = "employees";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.himebaugh.search." + BASE_DATA_NAME;
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.himebaugh.search." + BASE_DATA_NAME;
	
	// common URIs
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_DATA_NAME);
	public static final Uri SEARCH_SUGGEST_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_DATA_NAME + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);
	
	// matcher
	private static final int EMPLOYEES = 1; // The incoming URI matches the main table URI pattern
	private static final int EMPLOYEE_ID = 2; // The incoming URI matches the main table row ID URI pattern
	private static final int SEARCH_SUGGEST = 3;
	
	
	private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
	static { 
		SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
		SEARCH_SUGGEST_PROJECTION_MAP.put(EmployeeDatabase.COLUMN_ID, EmployeeDatabase.COLUMN_ID); 
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, EmployeeDatabase.COLUMN_FIRSTNAME + " AS "	+ SearchManager.SUGGEST_COLUMN_TEXT_1); 
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, EmployeeDatabase.COLUMN_LASTNAME + " AS "	+ SearchManager.SUGGEST_COLUMN_TEXT_2); 
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, EmployeeDatabase.COLUMN_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}
	
	public EmployeeProvider() {
		// Create and initialize URI matcher.
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, EmployeeDatabase.TABLE_EMPLOYEES, EMPLOYEES);
		mUriMatcher.addURI(AUTHORITY, EmployeeDatabase.TABLE_EMPLOYEES + "/#", EMPLOYEE_ID);
		
        // to get suggestions...
		mUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		mUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
	}

	@Override
	public boolean onCreate() {
		mEmployeeDatabase = new EmployeeDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(EmployeeDatabase.TABLE_EMPLOYEES);

		switch (mUriMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			selectionArgs = new String[] { "%" + selectionArgs[0] + "%", "%" + selectionArgs[0] + "%" };
			queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
			break;
		case EMPLOYEES:
			// no filter
			break;
		case EMPLOYEE_ID:
			queryBuilder.appendWhere(EmployeeDatabase.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}

		Cursor cursor = queryBuilder.query(mEmployeeDatabase.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case EMPLOYEES:
			return CONTENT_TYPE;
		case EMPLOYEE_ID:
			return CONTENT_ITEM_TYPE;
		case SEARCH_SUGGEST:
			return null;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
