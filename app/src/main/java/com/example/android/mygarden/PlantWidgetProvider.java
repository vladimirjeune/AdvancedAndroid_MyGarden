package com.example.android.mygarden;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;

public class PlantWidgetProvider extends AppWidgetProvider {

    private static final int SINGLE_PLANT_WIDGET_SIZE = 300;

    // setImageViewResource to update the widget’s image
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int imgRes, long plantId, boolean showWater, int appWidgetId) {

        // TODO (4): separate the updateAppWidget logic into getGardenGridRemoteView and getSinglePlantRemoteView
        // TODO (5): Use getAppWidgetOptions to get widget width and use the appropriate RemoteView method
        // TODO (6): Set the PendingIntent template in getGardenGridRemoteView to launch PlantDetailActivity

        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);  // Need to get the width of the Widget.  Options holds a lot of
        int appWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        RemoteViews remoteView ;
        if (appWidth < SINGLE_PLANT_WIDGET_SIZE) {
            remoteView = getSinglePlantRemoteView(context, imgRes, plantId, showWater);
        } else {
            remoteView = getGardenGridRemoteView(context);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteView);

/*        Intent intent;
        if (plantId == PlantContract.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        } else { // Set on click to open the corresponding detail activity
            Log.d(PlantWidgetProvider.class.getSimpleName(), "plantId=" + plantId);
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        // Update image
        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        // Update plant ID text
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        // Show/Hide the water drop button
        if (showWater) views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);
        else views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);
        // Add the wateringservice click handler
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);*/
    }


//    /**
//     * Create and return RemoteViews for the GridView mode
//     * @param context
//     * @return RemoteViews for GridView Mode widget
//     */
//    static RemoteViews getGardenGridRemoteView(Context context) {
//        return null;  // Null defaults to grass view
//    }


    /**
     * GETSINGLEPLANTREMOTEVIEW -
     * @param context
     * @param imgRes
     * @param plantId
     * @param showWater
     * @return RemoteViews for the Single Plant mode
     */
    static RemoteViews getSinglePlantRemoteView(Context context,
                                                int imgRes, long plantId, boolean showWater) {
        Intent intent;
        if (plantId == PlantContract.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        } else { // Set on click to open the corresponding detail activity
            Log.d(PlantWidgetProvider.class.getSimpleName(), "plantId=" + plantId);
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        // Update image
        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        // Update plant ID text
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        // Show/Hide the water drop button
        if (showWater) views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);
        else views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);
        // Add the wateringservice click handler
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);
        // Instruct the widget manager to update the widget

        return views;

    }

    /**
     * GETGARDENGRIDREMOTEVIEW - Creates and returns RemoteViews that will be displayed
     * in the GridView mode widget
     * 1. ) Create a RV object with a GridView layout
     * 2. ) Connect it to the RemoteViewService that will bind the RVs to the Adapter's cursor data
     *   To do that will create an Intent pointing to the GridViewService
     *   Then call setRemoteAdapter, linking the gridview with the Intent
     *   Then call setEmptyView and connect it to the RelativeLayout that is in the same thing as gridView
     *   So this will be displayed instead of GridView when the gridView is empty
     *   Lastly, return RV object
     * @param context
     * @return The RemoteViews for the GridView mode widget
     */
    public static RemoteViews getGardenGridRemoteView(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        // Set the GridWidgetService Intent as the Adapter for the GridView
        Intent intent = new Intent(context, GridWidgetService.class );
        rv.setRemoteAdapter(R.id.gv_widget, intent);  // So the Service is the Adapter

        // Set the PlantDetailActivity Intent to launch when clicked
        Intent appIntent = new Intent(context, PlantDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent
                .getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.gv_widget, appPendingIntent);

        rv.setEmptyView(R.id.gv_widget, R.id.rv_empty_view);

        return rv;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        PlantWateringService.startActionUpdatePlantWidgets(context);
    }


    /**
     * UPDATEPLANTWIDGET -
     * @param context - Needed to access some functionality
     * @param appWidgetManager - Needed to access some functionality
     * @param imgRes - Image to use
     * @param plantId - Id of the current plant
     * @param showWater - Whether the water droplet should be shown for this plant
     * @param appWidgetIds  - Ids of the appWidgets
     */
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, long plantId, boolean showWater, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, plantId, showWater, appWidgetId);
        }
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        PlantWateringService.startActionUpdatePlantWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
