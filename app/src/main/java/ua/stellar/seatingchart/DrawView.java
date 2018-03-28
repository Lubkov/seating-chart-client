package ua.stellar.seatingchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class DrawView extends View {

    private final String LOG_TAG = "RESERVE";

    private Paint paint;
    private Paint strokePaint;
    private Paint textPaint;
    private Rect rect;
    private int fontSize = 32;
    private final Rect textBounds;

    private String caption = "Caption";
    private int color = Color.WHITE;
    private float borderWidth = 3;
    private int borderColor = Color.BLACK;

    public DrawView(Context context) {
        super(context);

        textBounds = new Rect();

        //
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        paint.setStyle(Paint.Style.STROKE);

        // border
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1);

        //font
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(color); //Color.TRANSPARENT

        if (rect == null) {
            rect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        // сетка
        drawGrid(canvas);

        // нарисовать контур ресурса
        drawBorder(canvas);

        // текст
        drawCaption(canvas);
    }

    private void drawBorder(Canvas canvas) {
        strokePaint.setColor(borderColor);

        // контур ресурса
        for (int i = 0; i < borderWidth; i++) {
            drawBorderRect(canvas, i);
        }
    }

    private void drawBorderRect(Canvas canvas, int index) {
        Rect r = new Rect(rect.left + index,
                          rect.top + index,
                          rect.right - index,
                          rect.bottom - index);

        canvas.drawRect(r, strokePaint);
    }

    private void drawCaption(Canvas canvas) {
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(fontSize);
        textPaint.getTextBounds(caption, 0, caption.length(), textBounds);

        canvas.drawText(caption,
                        rect.width() / 2 - textBounds.exactCenterX(),
                        rect.height() / 2 - textBounds.exactCenterY(),
                        textPaint);
    }

    private void drawGrid(Canvas canvas) {
        paint.setStrokeWidth(0.2f);
        paint.setColor(Color.GRAY);

        int x = 0;
        int y = 0;
        int step = 10;

        while ((x < 2 * rect.width()) || (y < 2 * rect.height())) {
            x+= step;
            y+= step;

            canvas.drawLine(x, 0, 0, y, paint);
        }
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;

        invalidate();
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;

        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;

        invalidate();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;

        invalidate();
    }
}
