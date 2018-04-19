package ua.stellar.seatingchart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class MapView extends View {

    private Bitmap background;

    private GestureDetector gestureDetector;

    //events
    private OnDoubleClickListener doubleClickListener;

    private OnClickListener clickListener;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            gestureDetector = new GestureDetector(getContext(), gestureListener);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(Color.WHITE);
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }
        canvas.restore();
    }

    public void setBackground(Bitmap background) {
        this.background = background;

        invalidate();
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.d("LOG_TAG","onSingleTapConfirmed");
            doClickListener();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Log.d("LOG_TAG","onDoubleTap");
            doDoubleClickListener();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            Log.d("LOG_TAG","onLongPress");
        }
    };

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    private void doClickListener() {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.doubleClickListener = listener;
    }

    private void doDoubleClickListener() {
        if (doubleClickListener != null) {
            doubleClickListener.onDoubleClick(this);
        }
    }

    public interface OnClickListener {
        void onClick(MapView map);
    }

    public interface OnDoubleClickListener {
        void onDoubleClick(MapView map);
    }
}
