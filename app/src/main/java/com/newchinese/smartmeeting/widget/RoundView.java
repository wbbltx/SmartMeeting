package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.widget.color.core.ColorBoard;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class RoundView extends android.support.v7.widget.AppCompatImageView {
    private final Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;// 图片的外边界
    private int mBorderInsideColor = 0;// 图片的内边界
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private float mBorderThickness = 0;

    public RoundView(Context context) {
        this(context, null);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RoundView);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.RoundView_border_thickness, 0);
        mBorderInsideColor = a.getColor(R.styleable.RoundView_border_inside_color, defaultColor);
        mBorderOutsideColor = a.getColor(R.styleable.RoundView_border_outside_color, defaultColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (defaultHeight == 0)
            defaultHeight = getHeight();

        if (defaultWidth == 0)
            defaultWidth = getWidth();

        float radius = 0;
        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {//两个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
//绘制内圆
            drawRoundBoard(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
//绘制外圆
            drawRoundBoard(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {//只有内框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            drawRoundBoard(canvas,radius + mBorderThickness / 2, mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {//只有外框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            drawRoundBoard(canvas,radius + mBorderThickness / 2, mBorderOutsideColor);
        } else {
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        Bitmap croppedBitmap = getCroppedBitmap(bitmap, (int) radius);
        canvas.drawBitmap(croppedBitmap,defaultWidth/2 - radius,defaultHeight/2 - radius,null);
        super.onDraw(canvas);
    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int radius){
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0,y = 0;

        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth){
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth)/2;
            squareBitmap = Bitmap.createBitmap(bmp,x,y,squareWidth,squareHeight);
        }else if (bmpHeight < bmpWidth){
            squareHeight = squareWidth = bmpHeight;
            y = 0;
            x = (bmpWidth - bmpHeight)/2;
            squareBitmap = Bitmap.createBitmap(bmp,x,y,squareWidth,squareHeight);
        }else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter){
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap,diameter,diameter,true);
        }else {
            scaledSrcBmp = squareBitmap;
        }

        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0,0,0,0);
        canvas.drawCircle(scaledSrcBmp.getWidth()/2,scaledSrcBmp.getHeight()/2,scaledSrcBmp.getWidth()/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp,rect,rect,paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    private void drawRoundBoard(Canvas canvas, float radius, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }
}
