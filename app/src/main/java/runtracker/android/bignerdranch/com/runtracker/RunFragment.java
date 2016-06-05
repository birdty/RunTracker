package runtracker.android.bignerdranch.com.runtracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RunFragment extends Fragment
{

    private Button startButton, stopButton;

    private TextView startedTextView, latitudeTextView, longitudeTextView, altitudeTextView, durationTextView;

    private RunManager runManager;

    private Location lastLocation;

    private Run run;

    private static final String ARG_RUN_ID = "RUN_ID";

    private static final int LOAD_RUN = 0;
    private static final int LOAD_LOCATION = 1;

    public static RunFragment newInstance(long runId)
    {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf = new RunFragment();
        rf.setArguments(args);
        return rf;
    }

    private BroadcastReceiver locationReceiver = new LocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location loc)
        {
            if ( ! runManager.isTrackingRun(run ) )
            {
                return;
            }

            lastLocation = loc;

            if ( isVisible() )
            {
                updateUI();;
            }
        }


        @Override
        protected void onProviderEnabled(boolean enabled)
        {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;

            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();

        if ( args != null )
        {
            long runId = args.getLong(ARG_RUN_ID, -1);

            if ( runId != -1 )
            {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }

        runManager = RunManager.get(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        startedTextView = (TextView)view.findViewById(R.id.run_startedTextView);
        latitudeTextView = (TextView)view.findViewById(R.id.run_latitudeTextView);
        longitudeTextView = (TextView)view.findViewById(R.id.run_longitudeTextView);
        durationTextView = (TextView)view.findViewById(R.id.run_durationTextView);

        startButton = (Button)view.findViewById(R.id.run_startButton);

        startButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if ( run == null ) {
                    run = runManager.startNewRun();
                }
                else
                {
                    runManager.startTrackingRun(run);
                }

                updateUI();
            }
        });

        stopButton = (Button)view.findViewById(R.id.run_stopButton);

        startButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                runManager.stopRun();
                updateUI();
            }
        });

        updateUI();

        return view;

    }

    private void updateUI()
    {
        boolean started = runManager.isTrackingRun();
        boolean trackingThisRun = runManager.isTrackingRun(run);

        if( run != null )
        {
            startedTextView.setText(run.getStartDate().toString());
        }

        int durationSeconds = 0;

        if ( run != null && lastLocation != null )
        {
            durationSeconds = run.getDurationSeconds(lastLocation.getTime());
            latitudeTextView.setText(Double.toString(lastLocation.getLatitude()));
            longitudeTextView.setText(Double.toString(lastLocation.getLongitude()));
            altitudeTextView.setText(Double.toString(lastLocation.getAltitude()));


        }

        durationTextView.setText(Run.formatDuration(durationSeconds));


        startButton.setEnabled(!started);
        stopButton.setEnabled(started && trackingThisRun);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        getActivity().registerReceiver(locationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));


    }

    @Override
    public void onStop()
    {
        getActivity().unregisterReceiver(locationReceiver);
        super.onStop();
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run> {

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args)
        {
            return new android.support.v4.content.RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        public void onLoadFinished(Loader<Run> loader, Run newRun)
        {
            run = newRun;
            updateUI();
        }

        public void onLoaderReset(Loader<Run> loader)
        {

        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location> {

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args)
        {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location)
        {
            lastLocation = location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader)
        {

        }
    }
}
