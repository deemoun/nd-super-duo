package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class WidgetScoresFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext = null;
    List<String> mMatchTime = new ArrayList<>();
    List<String> mHomeGoals = new ArrayList<>();
    List<String> mAwayGoals = new ArrayList<>();
    List<String> mHomeName = new ArrayList<>();
    List<String> mAwayName = new ArrayList<>();

    public WidgetScoresFactory(Context context, Intent intent) {
        mContext = context;
    }


    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mHomeName.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item_widget);

        row.setTextViewText(R.id.data_textview, mMatchTime.get(position));
        row.setTextViewText(R.id.score_textview, mHomeGoals.get(position) + "  -  " + mAwayGoals.get(position));
        row.setTextViewText(R.id.home_name, mHomeName.get(position));
        row.setTextViewText(R.id.away_name,mAwayName.get(position));
        row.setImageViewResource(R.id.home_image, Utilies.getTeamCrestByTeamName(mHomeName.get(position)));
        row.setImageViewResource(R.id.away_image, Utilies.getTeamCrestByTeamName(mAwayName.get(position)));

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mMatchTime.clear();
        mHomeGoals.clear();
        mAwayGoals.clear();
        mHomeName.clear();
        mAwayName.clear();

        Cursor mCursor;

        ScoresDBHelper mOpenHelper = new ScoresDBHelper(mContext);

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseContract.SCORES_TABLE + " ORDER BY date DESC";
        mCursor = db.rawQuery(selectQuery, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
        } try {
            do {
                mMatchTime.add(mCursor.getString(mCursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));
                mHomeName.add(mCursor.getString(mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
                mAwayName.add(mCursor.getString(mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
                mHomeGoals.add(mCursor.getString(mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)));
                mAwayGoals.add(mCursor.getString(mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL)));
            } while (mCursor.moveToNext());
        } catch (CursorIndexOutOfBoundsException e){
            Log.v("Football Scores Widget", "Problem loading football scores (connection issues?): " + e.getLocalizedMessage());
        } finally {
            if(!mCursor.isClosed())
            mCursor.close();
        }
    }
}
