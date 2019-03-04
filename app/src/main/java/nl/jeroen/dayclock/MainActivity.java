package nl.jeroen.dayclock;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.jeroen.dayclock.model.TimeDataContainer;

public class MainActivity extends AppCompatActivity {

    private static final long MsInDay = 24 * 60 * 60 * 1000;
    private static final int Delay = 0;
    private static final int Period = 1000;
    private static final String Format = "%02d:%02d:%02d:%03d";

    private TextView tvTime;
    private TextView tvPercent;
    private ClockView clockView;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            CalculateYearTime();

            timerHandler.postDelayed(this, Period);
        }
    };

    private static TimeDataContainer getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        return new TimeDataContainer(hours, minutes, seconds, millis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        tvPercent = findViewById(R.id.tvPercent);
        clockView = findViewById(R.id.clockView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        timerHandler.postDelayed(timerRunnable, Delay);
    }

    @Override
    protected void onPause() {
        super.onPause();

        timerHandler.removeCallbacks(timerRunnable);
    }

    private void CalculateYearTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstJanCurrentYear = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);
        LocalDateTime firstJanNextYear = firstJanCurrentYear.minusYears(-1);

        long nsInYear = firstJanCurrentYear.until(firstJanNextYear, ChronoUnit.NANOS);
        double nsSinceYearStart = firstJanCurrentYear.until(now, ChronoUnit.NANOS);

        double percentOfYear = nsSinceYearStart / nsInYear;
        long msElapsed = (long) (percentOfYear * MsInDay);


        TimeDataContainer tdc = getDurationBreakdown(msElapsed);
        clockView.SetTime((int) tdc.hours, (int) tdc.minutes, (int) tdc.seconds);
        String timeString = String.format(Locale.ENGLISH, Format, tdc.hours, tdc.minutes, tdc.seconds, tdc.millis);
        tvTime.setText(timeString);

        tvPercent.setText(String.format(Locale.ENGLISH, "%.3f %%", percentOfYear * 100f));
    }
}
