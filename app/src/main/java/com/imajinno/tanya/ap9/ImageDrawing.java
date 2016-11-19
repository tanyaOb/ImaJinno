package com.imajinno.tanya.ap9;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.drawable.Drawable;



public class ImageDrawing extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    public int paintColor = 0xFF660000;
    public Canvas drawCanvas;
    public Bitmap canvasBitmap;
    public float paintBrushSize=20;

    public ImageDrawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public void setCurrentSize(float currentSize){
        this.paintBrushSize=currentSize;
    }
    public float getCurrentSize(){

        return this.paintBrushSize;
    }

    public void setCurrentColor(int currentColor) {this.paintColor=currentColor;}
    public int getCurrentColor()
    {
        return this.paintColor;
    }

    public void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(getCurrentColor());
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(getCurrentSize());
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public Bitmap SetConturs()
    {

        ImageDrawing y=(ImageDrawing)findViewById(R.id.drawing);
        Drawable b=y.getBackground();
        Bitmap canvasBitmap2;
        Bitmap bm=((BitmapDrawable)b).getBitmap();
        Bitmap bm2;
        int width= bm.getWidth();
        int height=bm.getHeight();
        int nw=canvasBitmap.getWidth();
        int nh=canvasBitmap.getHeight();
        float scaleWidth = ((float) nw) / width;
        float scaleHeight = ((float) nh) / height;
        int [] srcPixels;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
         bm2=Bitmap.createBitmap(bm, 0, 0, width, height,matrix,true);
        canvasBitmap2 = Bitmap.createBitmap(nw, nh, Bitmap.Config.ARGB_8888);
        srcPixels=new int[nw*nh];
        bm2.getPixels(srcPixels,0,nw,0,0,nw,nh);
        int[] srcPixels2=new int[nw * nh];
        for (int i=0; i <srcPixels.length; i++)
        {
            if(srcPixels[i]!= Color.WHITE){
                srcPixels2[i]=srcPixels[i];
            }

        }
        canvasBitmap2.setPixels(srcPixels2,0,nw,0,0,nw,nh);
        return canvasBitmap2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.DARKEN);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        canvas.drawBitmap(SetConturs(),0,0,canvasPaint);
    }

    public void Clean ()
    {
        drawCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void SetBrushSize(float p)
    {
        float k=p;
        drawPaint.setStrokeWidth(k);
    }
}



