package nl.jeroen.dayclock

import android.os.Bundle
import android.os.Handler
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.TimeUnit

import nl.jeroen.dayclock.model.TimeDataContainer
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private var tvTime : TextView? = null
    private var tvTimeOfYear: TextView? = null
    private var tvPercent: TextView? = null
    private var clockView: ClockView? = null
    private var displayedYearSecond = (-1).toLong()
    private var timeFormatter : DateTimeFormatter? = null

    private val timerHandler = Handler()
    private val timerRunnable = object : Runnable {
        override fun run() {
            calculateYearTime()

            timerHandler.postDelayed(this, Period.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTime = findViewById(R.id.tvTime)
        tvTimeOfYear = findViewById(R.id.tvTimeOfYear)
        tvPercent = findViewById(R.id.tvPercent)
        clockView = findViewById(R.id.clockView)

        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    }

    override fun onResume() {
        super.onResume()

        timerHandler.postDelayed(timerRunnable, Delay.toLong())
    }

    override fun onPause() {
        super.onPause()

        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun calculateYearTime() {
        val now = LocalDateTime.now()

        tvTime!!.text = now.format(timeFormatter)

        val firstJanCurrentYear = LocalDateTime.of(now.year, 1, 1, 0, 0)
        val firstJanNextYear = firstJanCurrentYear.minusYears(-1)

        val nsInYear = firstJanCurrentYear.until(firstJanNextYear, ChronoUnit.NANOS)
        val nsSinceYearStart = firstJanCurrentYear.until(now, ChronoUnit.NANOS).toDouble()

        val percentOfYear = nsSinceYearStart / nsInYear
        val msElapsed = (percentOfYear * MsInDay).toLong()


        val tdc = getDurationBreakdown(msElapsed)
        val currentSecond = tdc.seconds

        if (displayedYearSecond != currentSecond){
            displayedYearSecond = currentSecond

            clockView!!.setTime(tdc.hours.toInt(), tdc.minutes.toInt(), currentSecond.toInt())
        }

        val timeOfYearString = String.format(Locale.ENGLISH, TOYFormat, tdc.hours, tdc.minutes, currentSecond, tdc.millis)
        tvTimeOfYear!!.text = timeOfYearString

        tvPercent!!.text = String.format(Locale.ENGLISH, "%.5f %%", percentOfYear * 100f)
    }

    companion object {

        private const val MsInDay = (24 * 60 * 60 * 1000).toLong()
        private const val Delay = 0
        private const val Period = 50
        private const val TOYFormat = "%02d:%02d:%02d:%03d"

        private fun getDurationBreakdown(ms: Long): TimeDataContainer {
            if (ms < 0) {
                throw IllegalArgumentException("Duration must be greater than zero!")
            }

            var millis = ms

            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            millis -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
            millis -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
            millis -= TimeUnit.SECONDS.toMillis(seconds)

            return TimeDataContainer(hours, minutes, seconds, millis)
        }
    }
}
