package com.example.android.mygarden;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANTS = "com.example.android.mygarden.action.water_plants";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PlantWateringService(String name) {
        super(name);
    }

    /**
     * STARTWATERACTIONPLANTS - allows explicitly triggering the Service to perform this action
     * @param context
     */
    public static void startWaterActionPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_WATER_PLANTS.equals(action)) {
                handleActionWaterPlants();
            }
        }
    }

    /**
     * HANDLEACTIONWATERPLANTS - To water all plants we just run an update
     * query setting the last watered time to now, but only for those
     * plants that are still alive.  To be used in onHandleIntent
     */
    private void handleActionWaterPlants() {
        Uri uri = PlantContract.PlantEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        long nowTime = System.currentTimeMillis();  // Current time
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, nowTime);

        // Now update all the plants who have been watered in time not to die
        getContentResolver().update(
                uri,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + "> ? ",
                new String[]{Long.toString(nowTime - PlantUtils.MAX_AGE_WITHOUT_WATER)}
                );
    }

}
