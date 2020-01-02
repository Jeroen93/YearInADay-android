package nl.jeroen.dayclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// https://www.youtube.com/watch?v=ybKgq6qqTeA
class ClockView : View {

    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0

    private var clockHeight: Int = 0
    private var clockWidth = 0
    private var padding = 0
    private var fontSize = 0
    private var handTruncation: Int = 0
    private var hourHandTruncation = 0
    private var radius = 0
    private var paint: Paint? = null
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setTime(hour: Int, minute: Int, second: Int) {
        this.hour = if (hour > 12) hour - 12 else hour
        this.minute = minute
        this.second = second
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        clockHeight = h
        clockWidth = w
        padding = 50
        fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f,
                resources.displayMetrics).toInt()
        val min = min(clockHeight, clockWidth)
        radius = min / 2 - padding
        handTruncation = min / 20
        hourHandTruncation = min / 7
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        drawCircle(canvas)
        drawNumeral(canvas)
        drawHands(canvas)
        drawCenter(canvas)

        postInvalidateDelayed(500)
    }

    private fun drawHands(canvas: Canvas) {
        drawHourHand(canvas)
        drawMinuteHand(canvas)
        drawSecondHand(canvas)
    }

    private fun drawHourHand(canvas: Canvas) {
        val handRadius = radius - handTruncation - hourHandTruncation
        val loc = (hour + minute / 60f) * 5f
        val minutePaint = Paint(paint)
        minutePaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        minutePaint.strokeWidth = 15f

        drawHand(canvas, loc.toDouble(), handRadius, minutePaint)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val handRadius = radius - handTruncation
        val secondPaint = Paint(paint)
        secondPaint.strokeWidth = 12f

        drawHand(canvas, minute.toDouble(), handRadius, secondPaint)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val handRadius = radius - handTruncation
        val secondPaint = Paint(paint)
        secondPaint.color = ContextCompat.getColor(context, R.color.colorAccent)

        drawHand(canvas, second.toDouble(), handRadius, secondPaint)
    }

    private fun drawHand(canvas: Canvas, loc: Double, handRadius: Int, paint: Paint) {
        val angle = Math.PI * loc / 30 - Math.PI / 2

        val stopX = (clockWidth / 2 + cos(angle) * handRadius).toFloat()
        val stopY = (clockHeight / 2 + sin(angle) * handRadius).toFloat()
        canvas.drawLine(clockWidth / 2f, clockHeight / 2f, stopX, stopY, paint)
    }

    private fun drawNumeral(canvas: Canvas) {
        paint!!.textSize = fontSize.toFloat()
        paint!!.style = Paint.Style.FILL

        for (number in numbers) {
            val tmp = number.toString()
            paint!!.getTextBounds(tmp, 0, tmp.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (clockWidth / 2f + cos(angle) * radius - rect.width() / 2f).toInt()
            val y = ((clockHeight / 2f).toDouble() + sin(angle) * radius + (rect.height() / 2f).toDouble()).toInt()
            canvas.drawText(tmp, x.toFloat(), y.toFloat(), paint!!)
        }
    }

    private fun drawCenter(canvas: Canvas) {
        canvas.drawCircle(clockWidth / 2f, clockHeight / 2f, 12f, paint!!)
    }

    private fun drawCircle(canvas: Canvas) {
        paint!!.reset()
        paint!!.color = ContextCompat.getColor(context, android.R.color.white)
        paint!!.strokeWidth = 5f
        paint!!.style = Paint.Style.STROKE
        paint!!.isAntiAlias = true
        canvas.drawCircle(clockWidth / 2f, clockHeight / 2f, (radius + padding - 10).toFloat(), paint!!)
    }
}
