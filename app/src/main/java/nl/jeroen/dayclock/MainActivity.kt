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

class MainActivity : AppCompatActivity() {

    private var tvTime: TextView? = null
    private var tvPercent: TextView? = null
    private var clockView: ClockView? = null

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
        tvPercent = findViewById(R.id.tvPercent)
        clockView = findViewById(R.id.clockView)
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
        val firstJanCurrentYear = LocalDateTime.of(now.year, 1, 1, 0, 0)
        val firstJanNextYear = firstJanCurrentYear.minusYears(-1)

        val nsInYear = firstJanCurrentYear.until(firstJanNextYear, ChronoUnit.NANOS)
        val nsSinceYearStart = firstJanCurrentYear.until(now, ChronoUnit.NANOS).toDouble()

        val percentOfYear = nsSinceYearStart / nsInYear
        val msElapsed = (percentOfYear * MsInDay).toLong()


        val tdc = getDurationBreakdown(msElapsed)
        clockView!!.SetTime(tdc.hours.toInt(), tdc.minutes.toInt(), tdc.seconds.toInt())
        val timeString = String.format(Locale.ENGLISH, Format, tdc.hours, tdc.minutes, tdc.seconds, tdc.millis)
        tvTime!!.text = timeString

        tvPercent!!.text = String.format(Locale.ENGLISH, "%.3f %%", percentOfYear * 100f)
    }

    companion object {

        private const val MsInDay = (24 * 60 * 60 * 1000).toLong()
        private const val Delay = 0
        private const val Period = 1000
        private const val Format = "%02d:%02d:%02d:%03d"

        private fun getDurationBreakdown(millis: Long): TimeDataContainer {
            var millis = millis
            if (millis < 0) {
                throw IllegalArgumentException("Duration must be greater than zero!")
            }

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
