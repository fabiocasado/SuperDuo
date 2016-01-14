package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Implementation of App Widget functionality.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_PREVIOUS = "ActionPrevious";
    private static final String ACTION_NEXT = "ActionNext";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        boolean shouldUpdate = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (intent.getAction() == ACTION_PREVIOUS) {
                long currentWidgetDate = Utilies.getWidgetDate(context);
                Utilies.setWidgetDate(context, currentWidgetDate - AlarmManager.INTERVAL_DAY);
                shouldUpdate = true;
            } else if (intent.getAction() == ACTION_NEXT) {

                long currentWidgetDate = Utilies.getWidgetDate(context);
                Utilies.setWidgetDate(context, currentWidgetDate + AlarmManager.INTERVAL_DAY);
                shouldUpdate = true;
            }

            if (shouldUpdate) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ScoresWidgetProvider.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.scores_widget);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            long date = Utilies.getWidgetDate(context);
            views.setTextViewText(R.id.widget_date, Utilies.getTodayOrDateTimeFormat(context, date));

            Intent mainIntent = new Intent(context, MainActivity.class);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_empty, mainPendingIntent);

            Intent previousIntent = new Intent(context, ScoresWidgetProvider.class);
            previousIntent.setAction(ACTION_PREVIOUS);
            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 0, previousIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_previous_day, previousPendingIntent);

            Intent nextIntent = new Intent(context, ScoresWidgetProvider.class);
            nextIntent.setAction(ACTION_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_next_day, nextPendingIntent);

            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent itemPendingIntent = PendingIntent.getActivity(context, 0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, itemPendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, ScoresRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, ScoresRemoteViewsService.class));
    }
}

