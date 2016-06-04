package runtracker.android.bignerdranch.com.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import java.security.Security;

public class RunManager
{
    private static final String TAG = "RunManager";

    public static final String ACTION_LOCATION = "com.bignerdranch.android.runtracker.ACTION_LOCATION";

    private static RunManager runManager;
    private Context appContext;
    private LocationManager locationManager;

    private RunManager(Context newAppContext)
    {
        appContext = newAppContext;
        locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
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
}
