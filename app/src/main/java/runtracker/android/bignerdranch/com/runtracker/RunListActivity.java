package runtracker.android.bignerdranch.com.runtracker;

import android.support.v4.app.Fragment;

public class RunListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new RunListFragment();
    }
}
