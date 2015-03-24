package com.himebaugh.employeedirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

public class EmployeeWidgetService2 extends Service {
	
	private int mID;
	private String mName;
	private String mTitle;
	private String mDepartment;
	private String mPicture;

	@Override
	public void onStart(Intent intent, int startId) {

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(), EmployeeWidgetProvider2.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

		for (int widgetId : allWidgetIds) {
			
			// Select a random employee
			mID = (new Random().nextInt(11) + 1);

			Uri uri = ContentUris.withAppendedId(EmployeeProvider.CONTENT_URI, mID);
			String[] projection = { EmployeeDatabase.COLUMN_ID, EmployeeDatabase.COLUMN_FIRSTNAME, EmployeeDatabase.COLUMN_LASTNAME, EmployeeDatabase.COLUMN_TITLE, EmployeeDatabase.COLUMN_DEPARTMENT,
					EmployeeDatabase.COLUMN_CITY, EmployeeDatabase.COLUMN_OFFICE_PHONE, EmployeeDatabase.COLUMN_MOBILE_PHONE, EmployeeDatabase.COLUMN_EMAIL, EmployeeDatabase.COLUMN_PICTURE };
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = EmployeeDatabase.COLUMN_LASTNAME + " COLLATE LOCALIZED ASC";

			Cursor cursor = this.getApplicationContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				mName = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_LASTNAME)) + ", " + cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_FIRSTNAME));
				mTitle = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_TITLE));
				mDepartment = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_DEPARTMENT));
				mPicture = cursor.getString(cursor.getColumnIndex(EmployeeDatabase.COLUMN_PICTURE));
			} else {
				mName = "empty cursor";
			}

			cursor.close();

			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

			// Set the text
			remoteViews.setTextViewText(R.id.appwidget_layout_name, mName);
			remoteViews.setTextViewText(R.id.appwidget_layout_title, mTitle);
			remoteViews.setTextViewText(R.id.appwidget_layout_department, mDepartment);
			
			InputStream is;
			try {
				is = getAssets().open("pics/" + mPicture );
				Bitmap bit = BitmapFactory.decodeStream(is);
				
				remoteViews.setImageViewBitmap(R.id.appwidget_layout_picture, bit);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), EmployeeWidgetProvider2.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.appwidget_layout_text, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			//

		}
		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}