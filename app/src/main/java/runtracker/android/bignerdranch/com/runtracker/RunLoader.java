package runtracker.android.bignerdranch.com.runtracker;

import android.content.Context;

public class RunLoader extends DataLoader<Run>
{
    private long runId;

    public RunLoader(Context context,long newRunId)
    {
        super(context);
        runId = newRunId;
    }

    @Override
    public Run loadInBackground()
    {
        return RunManager.get(getContext()).getRun(runId);
    }
}
