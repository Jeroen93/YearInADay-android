package nl.jeroen.dayclock

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class MainActivity : AppCompatActivity() {

    private var tvTime: TextView? = null
    private var tvTimeOfYear: TextView? = null
    private var tvPercent: TextView? = null
    private var clockView: ClockView? = null
    private var displayedYearSecond = -1
    private var timeFormatter: DateTimeFormatter? = null

    private val timerHandler = Handler()
    private val timerRunnable = object : Runnable {
        override fun run() {
            calculateYearTime()

            timerHandler.postDelayed(this, Period)
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

        timerHandler.postDelayed(timerRunnable, Delay)
    }

    override fun onPause() {
        super.onPause()

        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun calculateYearTime() {
        val now = LocalDateTime.now()

        tvTime!!.text = now.format(timeFormatter)

        val firstJanCurrentYear = LocalDateTime.of(now.year, 1, 1, 0, 0)
        val firstJanNextYear = firstJanCurrentYear.plusYears(1)

        val nsInYear = firstJanCurrentYear.until(firstJanNextYear, ChronoUnit.NANOS)
        val nsSinceYearStart = firstJanCurrentYear.until(now, ChronoUnit.NANOS).toDouble()

        val percentOfYear = nsSinceYearStart / nsInYear
        val nsElapsed = (percentOfYear * NsInDay).toLong()

        val elapsedTime = LocalTime.ofNanoOfDay(nsElapsed)
        val currentSecond = elapsedTime.second

        if (displayedYearSecond != currentSecond) {
            displayedYearSecond = currentSecond

            clockView!!.setTime(elapsedTime.hour, elapsedTime.minute, currentSecond)
        }

        tvTimeOfYear!!.text = elapsedTime.format(timeFormatter)

        tvPercent!!.text = String.format(Locale.ENGLISH, "%.5f %%", percentOfYear * 100f)
    }

    companion object {

        private const val NsInDay = (24 * 60 * 60 * 1e9).toLong()
        private const val Delay = 0L
        private const val Period = 50L
    }
}
