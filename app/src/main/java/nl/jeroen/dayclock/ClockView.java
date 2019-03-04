package nl.jeroen.dayclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

// https://www.youtube.com/watch?v=ybKgq6qqTeA
public class ClockView extends View {

    private int height, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12};
    private Rect rect = new Rect();

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        width = w;
        padding = 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13,
                getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        drawCircle(canvas);
        drawCenter(canvas);
        drawNumeral(canvas);
        drawHands(canvas);

        postInvalidateDelayed(500);
    }

    private void drawHand(Canvas canvas, double loc, boolean isHour){
        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour
                ? radius - handTruncation - hourHandTruncation
                : radius - handTruncation;

        float stopX = (float) (width / 2 + Math.cos(angle) * handRadius);
        float stopY = (float) (height / 2 + Math.sin(angle) * handRadius);
        canvas.drawLine(width / 2f, height / 2f, stopX, stopY, paint);
    }

    private void drawHands(Canvas canvas) {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;
        float loc = (hour + c.get(Calendar.MINUTE) / 60f) * 5f;

        drawHand(canvas, loc, true);
        drawHand(canvas, c.get(Calendar.MINUTE), false);
        drawHand(canvas, c.get(Calendar.SECOND), false);
    }

    private void drawNumeral(Canvas canvas) {
        paint.setTextSize(fontSize);

        for (int number : numbers) {
            String tmp = String.valueOf(number);
            paint.getTextBounds(tmp, 0, tmp.length(), rect);
            double angle = Math.PI / 6 * (number - 3);
            int x = (int) (width / 2f + Math.cos(angle) * radius - rect.width() / 2f);
            int y = (int) (height / 2f + Math.sin(angle) * radius + rect.height() / 2f);
            canvas.drawText(tmp, x, y, paint);
        }
    }

    private void drawCenter(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2f, height / 2f, 12, paint);
    }

    private void drawCircle(Canvas canvas){
        paint.reset();
        paint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2f, height / 2f, radius + padding - 10, paint);
    }
}
