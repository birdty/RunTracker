package runtracker.android.bignerdranch.com.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import java.security.Security;

public class RunManager
{
    private static final String TAG = "RunManager";

    private static final String PREFS_FILE = "runs";

    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

    public static final String ACTION_LOCATION = "com.bignerdranch.android.runtracker.ACTION_LOCATION";

    private static RunManager runManager;
    private Context appContext;
    private LocationManager locationManager;

    private RunDatabaseHelper helper;
    private SharedPreferences prefs;
    private long currentRunId;

    private RunManager(Context newAppContext)
    {
        appContext = newAppContext;
        locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
        helper = new RunDatabaseHelper(appContext);
        prefs = appContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        currentRunId = prefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public Run startNewRun()
    {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run)
    {
        currentRunId = run.getId();
        prefs.edit().putLong(PREF_CURRENT_RUN_ID, currentRunId).commit();
        startLocationUpdates();
    }

    public void stopRun()
    {
        stopLocationUpdates();
        currentRunId = -1;
        prefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    private Run insertRun()
    {
        Run run = new Run();
        run.setId(helper.insertRun(run));
        return run;
    }

    public static RunManager get(Context c)
    {
        if ( runManager == null )
        {
            runManager = new RunManager(c.getApplicationContext());
        }

        return runManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate)
    {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(appContext, 0, broadcast, flags);
    }

    public void startLocationUpdates()
    {
        String provider = LocationManager.GPS_PROVIDER;

        try {
            Location lastKnown = locationManager.getLastKnownLocation(provider);

            if (lastKnown != null) {
                lastKnown.setTime(System.currentTimeMillis());
                broadcastLocation(lastKnown);
            }

        }
        catch (SecurityException se )
        {

        }

        PendingIntent pi = getLocationPendingIntent(true);

        try {
            locationManager.requestLocationUpdates(provider, 0, 0, pi);
        }
        catch (SecurityException e)
        {

        }
    }

    private void broadcastLocation(Location location)
    {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        appContext.sendBroadcast(broadcast);
    }

    public void stopLocationUpdates()
    {
        PendingIntent pi = getLocationPendingIntent(false);

        if ( pi != null )
        {
            locationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun()
    {
        return getLocationPendingIntent(false) != null;
    }

    public void insertLocation(Location loc )
    {
        if ( currentRunId != -1 )
        {
            helper.insertLocation(currentRunId, loc);
        }
    }

    public RunDatabaseHelper.RunCursor queryRuns()
    {
        return helper.queryRuns();
    }

    public Run getRun(long id)
    {
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = helper.queryRun(id);

        cursor.moveToFirst();

        if ( ! cursor.isAfterLast() )
        {
            run = cursor.getRun();
        }


        cursor.close();

        return run;
    }

    public boolean isTrackingRun(Run run)
    {
        return run != null && run.getId() == currentRunId;
    }

    public Location getLastLocationForRun(long runId)
    {
        Location location = null;

        RunDatabaseHelper.LocationCursor cursor = helper.queryLastLocationForRun(runId);

        if ( ! cursor.isAfterLast() )
            location = cursor.getLocation();

        cursor.close();

        return location;
    }
}
