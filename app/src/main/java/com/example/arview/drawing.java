package com.example.arview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class drawing extends View {

    int width;
    int height;
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint mPaint;
    Context context;
    private float mx,my;
    private static float TOLRANCE = 100;

    public drawing(Context context) {
        super(context);
        this.context = context;

        path = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15f);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public drawing(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;

        path = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15f);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public drawing(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        path = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15f);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,mPaint);
    }

    private void startTouch(float x, float y){
        path.moveTo(x,y);
        mx = x;
        my = y;

    }

    public void clearCanvas(){
        path.reset();
        invalidate();
    }

    public void upTouch(){
        path.lineTo(mx,my);
    }

    private void moveTouch(float x, float y){
        float fx = Math.abs(x-mx);
        float fy = Math.abs(y-my);
        if(fx >= TOLRANCE || fy >= TOLRANCE){
            path.quadTo(mx,my,(x+mx)/2,(y+my)/2);
            mx = x;
            my = y;

        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTouch(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x,y);
                invalidate();
                break;
        }
        return true;
    }

}
