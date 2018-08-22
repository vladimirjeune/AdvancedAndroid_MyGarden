package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.utils.PlantUtils;

public class GridWidgetFactory implements RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridWidgetFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }


    /**
     * ONDATASETCHANGED - Called when the RemoteViewsFactory is created and when it is notified to
     * update its data.  We will use here to obtain all plants in our garden from the DB.
     * Will use notifyAppWidgetViewDataChanged in other appropriate functions just like you would
     * use notifyDataSetChanged.
     */
    @Override
    public void onDataSetChanged() {
        Uri PLANT_URI = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();

        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * GETVIEWAT - Acts like onBindViewHolder in an Adapter
     * @param position - Current position of item in GridView to be displayed
     * @return - The RemoteViews object to display for the given position
     */
    @Override
    public RemoteViews getViewAt(int position) {

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToPosition(position);

            int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int typeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

            long plantId = mCursor.getLong(idIndex);
            long cretated = mCursor.getLong(createIndex);
            long watered = mCursor.getLong(waterIndex);
            int plantType = mCursor.getInt(typeIndex);

            long timeNow = System.currentTimeMillis();
            long plantAge = timeNow - cretated;
            long waterAge = timeNow - watered;
            int plantImage = PlantUtils.getPlantImageRes(mContext, plantAge, waterAge, plantType);

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);
            rv.setImageViewResource(R.id.iv_widget, plantImage);
            rv.setTextViewText(R.id.tv_widget, String.valueOf(plantType));
            rv.setViewVisibility(R.id.widget_water_button, View.GONE);  // The Droplet would clutter up the interface of the grid

            // Fill in the OnClickPendingIntent Template using the specific plant Id for each plant individually
            Bundle bundle = new Bundle();
            bundle.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);  // Loading payload for this ID
            Intent fillIntent = new Intent();
            fillIntent.putExtras(bundle);  // Put payload in Intent
            rv.setOnClickFillInIntent(R.id.iv_widget, fillIntent);  // Set the fillIntent

            return rv;
        }

        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;  // Treat all elements the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
