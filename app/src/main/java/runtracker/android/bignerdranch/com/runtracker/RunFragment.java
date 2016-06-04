package runtracker.android.bignerdranch.com.runtracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RunFragment extends Fragment {

    private Button startButton, stopButton;

    private TextView startedTextView, latitudeTextView, longitudeTextView, altitudeTextView, durationTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        stopButton = (Button)view.findViewById(R.id.run_stopButton);



        return view;

    }

}
