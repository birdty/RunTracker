package runtracker.android.bignerdranch.com.runtracker;

import java.util.Date;

public class Run
{
    private Date startDate;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Run()
    {
        id = -1;
        startDate = new Date();
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date newStartDate)
    {
        startDate = newStartDate;
    }

    public int getDurationSeconds(long endMillis)
    {
        return (int)((endMillis - startDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds)
    {
        int seconds = durationSeconds % 60;
        int minutes = ((durationSeconds - seconds) / 60 ) % 60;
        int hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
