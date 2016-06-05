package runtracker.android.bignerdranch.com.runtracker;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ParallelExecutorCompat;

public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor>
{
    private Cursor cursor;

    public SQLiteCursorLoader(Context context)
    {
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground()
    {
        Cursor cursor = loadCursor();

        if ( cursor != null )
        {
            cursor.getCount();
        }

        return cursor;
    }

    public void delieverResult(Cursor data)
    {
        Cursor oldCursor = cursor;
        cursor = data;

        if ( isStarted() )
        {
            super.deliverResult(data);
        }

        if ( oldCursor != null && oldCursor != data && ! oldCursor.isClosed() )
        {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading()
    {
        if ( cursor != null )
        {
            delieverResult(cursor);
        }

        if ( takeContentChanged() || cursor == null )
        {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading()
    {
        cancelLoad();
    }

    protected void onCanceled()
    {
        if ( cursor != null && ! cursor.isClosed() )
        {
            cursor.close();
        }
    }

    @Override
    protected void onReset()
    {
        super.onReset();

        onStopLoading();

        if ( cursor != null && ! cursor.isClosed() )
        {
            cursor.close();
        }

        cursor = null;
    }
}
