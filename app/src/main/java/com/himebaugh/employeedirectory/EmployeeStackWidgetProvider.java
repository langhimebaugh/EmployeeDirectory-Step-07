package com.himebaugh.employeedirectory;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EmployeeStackWidgetProvider extends AppWidgetProvider {

	public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
	public static final String CLICK_ACTION = "com.himebaugh.employeedirectory.stackwidget.CLICK_ACTION";
	public static final String EXTRA_ITEM = "com.himebaugh.employeedirectory.stackwidget.EXTRA_ITEM";
	public static final String EMPLOYEE_ID = "com.himebaugh.employeedirectory.stackwidget.EMPLOYEE_ID";

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);

		if (intent.getAction().equals(TOAST_ACTION)) {
			int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
		}

		if (intent.getAction().equals(CLICK_ACTION)) {
			int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			int empID = intent.getIntExtra(EMPLOYEE_ID, 0);

			// Starting new intent
//			Intent intent2 = new Intent(context, DetailActivity.class);
//			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent2.putExtra("empID", empID);
//			context.startActivity(intent2);
			
			Uri details = Uri.withAppendedPath(EmployeeProvider.CONTENT_URI, "" + empID);
			Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
			detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(detailsIntent);

		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// update each of the widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {

			// Here we setup the intent which points to the StackViewService which will
			// provide the views for this collection.
			Intent intent = new Intent(context, EmployeeStackWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

			// When intents are compared, the extras are ignored, so we need to embed the extras
			// into the data so that the extras will not be ignored.
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stack_widget_layout);
			rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

			// The empty view is displayed when the collection has no items. It should be a sibling
			// of the collection view.
			rv.setEmptyView(R.id.stack_view, R.id.empty_view);

			// ********************************************************************************
			// ********************************************************************************
			// This section makes it possible for items to have individualized behavior.
			// It does this by setting up a pending intent template. Individuals items of a collection
			// cannot set up their own pending intents. Instead, the collection as a whole sets
			// up a pending intent template, and the individual items set a fillInIntent
			// to create unique behavior on an item-by-item basis.
			// Intent toastIntent = new Intent(context, EmployeeStackWidgetProvider.class);
			Intent clickIntent = new Intent(context, EmployeeStackWidgetProvider.class);
			// Set the action for the intent.
			// When the user touches a particular view, it will have the effect of
			// broadcasting TOAST_ACTION.
			// toastIntent.setAction(EmployeeStackWidgetProvider.TOAST_ACTION);
			clickIntent.setAction(EmployeeStackWidgetProvider.CLICK_ACTION);
			// toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			// PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			// rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
			rv.setPendingIntentTemplate(R.id.stack_view, clickPendingIntent);
			// ********************************************************************************
			// ********************************************************************************

			// ********************************************************************************
			// ********************************************************************************
			// // Register an onClickListener
			// Intent clickIntent = new Intent(context, DetailActivity.class);
			// clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			// clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			// // ?????????????????????????????????????????
			// clickIntent.putExtra("empID", 3);
			// intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// rv.setPendingIntentTemplate(R.id.stack_view, pendingIntent);
			// ********************************************************************************
			// ********************************************************************************

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}