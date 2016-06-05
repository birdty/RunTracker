package runtracker.android.bignerdranch.com.runtracker;

import android.content.Context;
import android.location.Location;

public class LastLocationLoader extends DataLoader<Location>
{

    private long runId;

    public LastLocationLoader(Context context, long newRunId)
    {
        super(context);
        runId = newRunId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(runId);
    }
}
