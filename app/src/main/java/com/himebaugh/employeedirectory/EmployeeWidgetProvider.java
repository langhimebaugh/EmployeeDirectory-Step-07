package com.himebaugh.employeedirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RemoteViews;

public class EmployeeWidgetProvider extends AppWidgetProvider{
	
	private int mID;
	private String mName;
	private String mTitle;
	private String mDepartment;
	private String mPicture;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, EmployeeWidgetProvider.class);
		
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		for (int widgetId : allWidgetIds) {
			
			// Select a random employee
			mID = (new Random().nextInt(11) + 1);

			Uri uri = ContentUris.withAppendedId(EmployeeProvider.CONTENT_URI, mID);
			String[] projection = { EmployeeDatabase.COLUMN_ID, EmployeeDatabase.COLUMN_FIRSTNAME, EmployeeDatabase.COLUMN_LASTNAME, EmployeeDatabase.COLUMN_TITLE, EmployeeDatabase.COLUMN_DEPARTMENT,
					EmployeeDatabase.COLUMN_CITY, EmployeeDatabase.COLUMN_OFFICE_PHONE, EmployeeDatabase.COLUMN_MOBILE_PHONE, EmployeeDatabase.COLUMN_EMAIL, EmployeeDatabase.COLUMN_PICTURE };
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = EmployeeDatabase.COLUMN_LASTNAME + " COLLATE LOCALIZED ASC";

			Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

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

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			// Set the text
			remoteViews.setTextViewText(R.id.appwidget_layout_name, mName);
			remoteViews.setTextViewText(R.id.appwidget_layout_title, mTitle);
			remoteViews.setTextViewText(R.id.appwidget_layout_department, mDepartment);

			InputStream is;
			try {
				is = context.getAssets().open("pics/" + mPicture);
				Bitmap bit = BitmapFactory.decodeStream(is);

				remoteViews.setImageViewBitmap(R.id.appwidget_layout_picture, bit);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Register an onClickListener
			// Intent intent = new Intent(context, EmployeeWidgetProvider.class);
//			Intent intent = new Intent(context, DetailActivity.class);
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//			intent.putExtra("empID", mID);
			
			Uri details = Uri.withAppendedPath(EmployeeProvider.CONTENT_URI, "" + mID);
			Intent intent = new Intent(Intent.ACTION_VIEW, details);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 				// ??
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); 	// ??	
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			// context.startActivity(intent);

			// Use PendingIntent.getActivity to open the App from the widget...
			// PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			remoteViews.setOnClickPendingIntent(R.id.appwidget_layout_text, pendingIntent);
			
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
}
