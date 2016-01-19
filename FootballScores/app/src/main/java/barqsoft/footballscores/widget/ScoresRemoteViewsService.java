package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresAdapter;
import barqsoft.footballscores.Utilies;

/**
 * Created by fcasado on 14/01/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Uri scoresUri = DatabaseContract.scores_table.buildScoreWithDate();
                long currentDate = Utilies.getWidgetDate(getApplicationContext());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String[] selectionArgs = {String.valueOf(format.format(currentDate))};

                data = getContentResolver().query(scoresUri,
                        null,
                        null,
                        selectionArgs,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.scores_list_item);

                views.setTextViewText(R.id.home_name, data.getString(ScoresAdapter.COL_HOME));
                views.setTextViewText(R.id.away_name, data.getString(ScoresAdapter.COL_AWAY));
                views.setTextViewText(R.id.date_textview, data.getString(ScoresAdapter.COL_MATCHTIME));
                views.setTextViewText(R.id.score_textview, Utilies.getScores(getBaseContext(), data.getInt(ScoresAdapter.COL_HOME_GOALS), data.getInt(ScoresAdapter.COL_AWAY_GOALS)));
                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(getBaseContext(),
                        data.getString(ScoresAdapter.COL_HOME)));
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(getBaseContext(),
                        data.getString(ScoresAdapter.COL_AWAY)));

                Intent fillInIntent = new Intent(Intent.ACTION_MAIN);
                fillInIntent.putExtra("position", position);

                views.setOnClickFillInIntent(R.id.score_item, fillInIntent);

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
//                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(ScoresAdapter.COL_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
