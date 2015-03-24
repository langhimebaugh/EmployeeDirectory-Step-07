package com.himebaugh.employeedirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EmployeeStackWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackRemoteViewsFactory2(this.getApplicationContext(), intent);
	}
}

class StackRemoteViewsFactory2 implements RemoteViewsService.RemoteViewsFactory {
	// private static final int mCount = 10;
	private int mCount = 0;
	private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
	private Context mContext;
	private int mAppWidgetId;

	private int mID;
	private String mName;
	private String mTitle;
	private String mDepartment;
	private String mPicture;

	public StackRemoteViewsFactory2(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public void onCreate() {
		// LANG

		// Uri uri = ContentUris.withAppendedId(EmployeeProvider.CONTENT_URI, mID);
		Uri uri = EmployeeProvider.CONTENT_URI;
		String[] projection = { EmployeeDatabase.COLUMN_ID, EmployeeDatabase.COLUMN_FIRSTNAME, EmployeeDatabase.COLUMN_LASTNAME, EmployeeDatabase.COLUMN_TITLE, EmployeeDatabase.COLUMN_DEPARTMENT,
				EmployeeDatabase.COLUMN_CITY, EmployeeDatabase.COLUMN_OFFICE_PHONE, EmployeeDatabase.COLUMN_MOBILE_PHONE, EmployeeDatabase.COLUMN_EMAIL, EmployeeDatabase.COLUMN_PICTURE };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = EmployeeDatabase.COLUMN_LASTNAME + " COLLATE LOCALIZED ASC";

		Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			mID = cursor.getInt(cursor.getColumnIndex(EmployeeDatabase.COLUMN_ID));
			mName = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_LASTNAME)) + ", " + cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_FIRSTNAME));
			mTitle = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_TITLE));
			mDepartment = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_DEPARTMENT));
			mPicture = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_PICTURE));

			mWidgetItems.add(new WidgetItem(mID, mName, mTitle, mDepartment, mPicture));
			mCount = mCount + 1;

		}

		cursor.close();

		//

		// In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
		// for (int i = 0; i < mCount; i++) {
		// mWidgetItems.add(new WidgetItem(i + "!LALALA"));
		// }

		// We sleep for 3 seconds here to show how the empty view appears in the interim.
		// The empty view is set in the StackWidgetProvider and should be a sibling of the
		// collection view.
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		// In onDestroy() you should tear down anything that was setup for your data source,
		// eg. cursors, connections, etc.
		mWidgetItems.clear();
	}

	public int getCount() {
		return mCount;
	}

	public RemoteViews getViewAt(int position) {
		// position will always range from 0 to getCount() - 1.

		// We construct a remote views item based on our widget item xml file, and set the
		// text based on the position.
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.stack_widget_item);
		rv.setTextViewText(R.id.stack_widget_layout_name, mWidgetItems.get(position)._name);
		rv.setTextViewText(R.id.stack_widget_layout_title, mWidgetItems.get(position)._title);
		rv.setTextViewText(R.id.stack_widget_layout_department, mWidgetItems.get(position)._department);
		// rv.setTextViewText(R.id.stack_widget_layout_picture, mWidgetItems.get(position)._picture);

		// Next, we set a fill-intent which will be used to fill-in the pending intent template
		// which is set on the collection view in StackWidgetProvider.
		Bundle extras = new Bundle();
		//extras.putInt(EmployeeStackWidgetProvider.EXTRA_ITEM, position);
		extras.putInt(EmployeeStackWidgetProvider.EMPLOYEE_ID, mWidgetItems.get(position)._empID);
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.stack_widget_layout_text, fillInIntent);

		// You can do heaving lifting in here, synchronously. For example, if you need to
		// process an image, fetch something from the network, etc., it is ok to do it here,
		// synchronously. A loading view will show up in lieu of the actual contents in the
		// interim.
		
		InputStream is;
		try {
			System.out.println("Loading view " + position);
			
			is = mContext.getAssets().open("pics/" + mWidgetItems.get(position)._picture);
			Bitmap bit = BitmapFactory.decodeStream(is);

			rv.setImageViewBitmap(R.id.stack_widget_layout_picture, bit);
			
			// Thread.sleep(500);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the remote views object.
		return rv;
	}

	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt() is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do heaving lifting in
		// here, synchronously. For example, if you need to process an image, fetch something
		// from the network, etc., it is ok to do it here, synchronously. The widget will remain
		// in its current state while work is being done here, so you don't need to worry about
		// locking up the widget.
	}
}