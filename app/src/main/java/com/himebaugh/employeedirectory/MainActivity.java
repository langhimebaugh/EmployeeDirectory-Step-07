package com.himebaugh.employeedirectory;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

//	GOAL: Build a native android Mobile Employee Directory 

//	** The result is similar to the sample with Flex and Flash Builder 
//	see http://www.adobe.com/devnet/flex/articles/employee-directory-android-flex.html

//	PURPOSE: Learning how to build an Android App.

//	Step 7: Create a Widget with it's own ContentProvider & Service.
//			1) Create EmployeeWidgetProvider (Changes every 30 minutes, with a random selection. Click to view Employee in DetailActivity.)
//			2) Create EmployeeWidgetProvider2 & EmployeeWidgetService2 (Changes when clicked, with a random selection)
//			3) Create EmployeeStackWidgetProvider & EmployeeStackWidgetService (Changes when scrolled. Click to view Employee in DetailActivity.)
//			4) Create WidgetItem
//			5) Create appwidget_background.xml  (in res/drawable)
//			6) Create stack_widget_item_background.xml  (in res/drawable)
//			7) Create preview1.png  (in res/drawable)
//			8) Create preview2.png   (in res/drawable)
//			9) Create stack_widget_item.xml  (in res/layout)
//			10) Create stack_widget_layout.xml  (in res/layout)
//			11) Create widget_layout.xml  (in res/layout)
//			12) Create stack_widget.xml  (in res/xml)
//			13) Create widget.xml  (in res/xml)
//			14) Modify dimens.xml  (in res/values)
//			15) Modify strings.xml  (in res/values)
//			16) Modify AndroidManifest.xml

//	Step 6: Create a Search Interface using Android's search dialog.
//			1) Add Search logic within the ContentProvider (EmployeeProvider)
//			2) Create SearchableActivity
//			3) Create activity_searchable.xml  (in res/layout)
//			4) Create searchable.xml  (in res/xml)
//			5) Modify MainActivity (add SearchView to onCreateOptionsMenu, add onSearchRequested() to onOptionsItemSelected )
//			6) Modify main.xml  (in res/menu)
//			7) Modify strings.xml  (in res/values)
//			8) Modify AndroidManifest.xml
//	Step 6B: Pass data to DetailActivity using Intent.ACTION_VIEW in place of intent.putExtra
//			1) Modify MainActivity (add Intent.ACTION_VIEW in onListItemClick )
//			2) Modify DetailActivity
//			3) Modify AndroidManifest.xml (add android:mimeType="vnd.android.cursor.item...)

//	Step 5: Create a ContentProvider to access the database.
//			1) Create EmployeeProvider 
//			2) Modify LoadEmployeesTask To implement the new ContentProvider (MainActivity)
//			3) Remove getAllEmployeesCursor() method from EmployeeDatabase

//	Step 4: Pass data to DetailActivity to display more data and provide other functionality (w/ intent.putExtra)
//			1) Create DetailActivity 
//			2) Create activity_detail.xml  (in res/layout)
//			3) Add DetailActivity to AndroidManifest.xml
//			4) Add uses-permissions to AndroidManifest.xml
//			5) Modify strings.xml  (in res/values)
//			6) Create mail.png, phone.png, sms.png  (in res/drawable)
//			7) Create employee_photo.jpg  (in assets/pics)

//	Step 3: Save (Persist) the data into a SQLite Database & Load a ListView from a SQLite Database
//			1) Modify LoadEmployeesTask to load the database. 
//			2) The database is created when called for the first time. This will also call the EmployeeXmlParser from within.
//			3) A Cursor is returned that exposes results from a query on a SQLiteDatabase.
//			4) The SimpleCursorAdapter displays the data from the Cursor.

//	Step 2: Load data into the ListActivity from an XML file via XmlParser
//			1) employee_list.xml  (in res/xml)
//			2) Employee.java
//			3) EmployeeXmlParser.java


public class MainActivity extends ListActivity  {
	
	public List<Employee> employees = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
 
        // load data in a non-ui thread
        new LoadEmployeesTask().execute();
	}
	
	private class LoadEmployeesTask extends AsyncTask<String, Void, Cursor> {

		@Override
		protected Cursor doInBackground(String... args) {
			
			Uri uri = EmployeeProvider.CONTENT_URI;
			String[] projection = { 
					EmployeeDatabase.COLUMN_ID, 
					EmployeeDatabase.COLUMN_FIRSTNAME, 
					EmployeeDatabase.COLUMN_LASTNAME, 
					EmployeeDatabase.COLUMN_TITLE, 
					EmployeeDatabase.COLUMN_DEPARTMENT,
					EmployeeDatabase.COLUMN_CITY, 
					EmployeeDatabase.COLUMN_OFFICE_PHONE, 
					EmployeeDatabase.COLUMN_MOBILE_PHONE, 
					EmployeeDatabase.COLUMN_EMAIL, 
					EmployeeDatabase.COLUMN_PICTURE 
					};
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = EmployeeDatabase.COLUMN_LASTNAME + " COLLATE LOCALIZED ASC";

			Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);						
			
			return cursor;
		}
		
		@Override
		protected void onPostExecute(Cursor cursor) {

			String[] from = { 
					EmployeeDatabase.COLUMN_FIRSTNAME, 
					EmployeeDatabase.COLUMN_TITLE, 
					EmployeeDatabase.COLUMN_DEPARTMENT, 
					EmployeeDatabase.COLUMN_CITY, 
					EmployeeDatabase.COLUMN_OFFICE_PHONE, 
					EmployeeDatabase.COLUMN_MOBILE_PHONE, 
					EmployeeDatabase.COLUMN_EMAIL, 
					EmployeeDatabase.COLUMN_PICTURE
					};
			int[] to = { 
					R.id.list_item_name, 
					R.id.list_item_title, 
					R.id.list_item_department, 
					R.id.list_item_city, 
					R.id.list_item_office_phone, 
					R.id.list_item_mobile_phone, 
					R.id.list_item_email, 
					R.id.list_item_picture 
					};
					
			SimpleCursorAdapter records = new SimpleCursorAdapter(getBaseContext(), R.layout.list_item, cursor, from, to, 0);
			setListAdapter(records);
		}

	}
	
	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		
		Uri details = Uri.withAppendedPath(EmployeeProvider.CONTENT_URI, "" + id);
		Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
		startActivity(detailsIntent);
	}
	
	// FROM SIMPLE SEARCH INTEGRATION
	public void onForceSearch(View view) {
		onSearchRequested();
	}
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		
		// MINE
//		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//		searchView.setIconifiedByDefault(false);
		
		// FROM SIMPLE SEARCH INTEGRATION
		searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
		searchView.setIconifiedByDefault(true);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			onSearchRequested();
			return true;
		default:
			return false;
		}
	}

}
