package com.udacity.ak.localroots.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.Html;
import android.widget.RemoteViews;

import com.udacity.ak.localroots.BuildConfig;
import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.ui.activity.MainActivity;
import com.udacity.ak.localroots.utilities.AppConstants;

public class LocalRootsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_local_roots);
        Resources res = context.getResources();
        String strNoRecentItems = res.getString(R.string.no_recent_items);
        SharedPreferences sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        views.setTextViewText(R.id.tv_widget_market, sharedPreferences.getString(AppConstants.SP_KEY_MARKET_NAME, strNoRecentItems));
        views.setTextViewText(R.id.tv_widget_schedule, Html.fromHtml(sharedPreferences.getString(AppConstants.SP_KEY_MARKET_SCHEDULE, "")));

        //PendingIntent to launch the app when any of teh TextViews are clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.tv_widget_market, pendingIntent);
        views.setOnClickPendingIntent(R.id.tv_widget_schedule, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
