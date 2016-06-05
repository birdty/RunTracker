package runtracker.android.bignerdranch.com.runtracker;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;

public abstract class DataLoader<D> extends AsyncTaskLoader<D>
{
    private D data;

    public DataLoader(Context context)
    {
        super(context);
    }

    @Override
    protected void onStartLoading()
    {
        if ( data != null )
        {
            deliverResult(data);
        }
        else
        {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D newData)
    {
        data = newData;

        if ( isStarted() )
        {
            super.deliverResult(data);
        }
    }
}
