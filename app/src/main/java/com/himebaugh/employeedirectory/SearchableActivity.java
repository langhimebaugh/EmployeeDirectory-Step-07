package com.himebaugh.employeedirectory;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//@TargetApi(11)
public class SearchableActivity extends ListActivity {

	// SEE THIS
	// http://code.google.com/p/libs-for-android/source/browse/demos/jamendo/src/com/google/android/demos/jamendo/app/SearchActivity.java?r=6b555f06dd141fd1951c17c701c7555a9052c5d0

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// implement Up Navigation with caret in front of App icon in the Action Bar
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		checkIntent(intent);
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		// update the activity launch intent
		setIntent(newIntent);
		// handle it
		checkIntent(newIntent);
	}

	private void checkIntent(Intent intent) {
		String query = "";
		String intentAction = intent.getAction();
		if (Intent.ACTION_SEARCH.equals(intentAction)) {
			query = intent.getStringExtra(SearchManager.QUERY);
		} else if (Intent.ACTION_VIEW.equals(intentAction)) {

			Uri details = intent.getData();
			Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
			startActivity(detailsIntent);

		}
		fillList(query);
	}

	private void fillList(String query) {

		String wildcardQuery = "%" + query + "%";

		Cursor cursor = getContentResolver().query(
				EmployeeProvider.CONTENT_URI, 
				null, 
				EmployeeDatabase.COLUMN_FIRSTNAME + " LIKE ? OR " + EmployeeDatabase.COLUMN_LASTNAME + " LIKE ?", 
				new String[] { wildcardQuery, wildcardQuery }, 
				null);

		ListAdapter adapter = new SimpleCursorAdapter(
				this, 
				android.R.layout.simple_list_item_2, 
				cursor, 
				new String[] { EmployeeDatabase.COLUMN_FIRSTNAME, EmployeeDatabase.COLUMN_LASTNAME },
				new int[] { android.R.id.text1, android.R.id.text2 },
				0);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {

		Uri details = Uri.withAppendedPath(EmployeeProvider.CONTENT_URI, "" + id);
		Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
		startActivity(detailsIntent);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

}
