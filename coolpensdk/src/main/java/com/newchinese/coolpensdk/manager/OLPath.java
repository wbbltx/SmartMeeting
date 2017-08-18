package com.newchinese.coolpensdk.manager;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.SurfaceHolder;

/**
 * Description:   用于记录一条线的轨迹，初始化Paint样式
 * author         xulei
 * Date           2017/4/25 10:52
 */
class OLPath {

    private static final float TOUCH_TOLERANCE = 2;
    private Path mPath;
    private int color;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mX = 0, mY = 0;
    private PaintFlagsDrawFilter pfdf;
    private float xx1 = -100;
    private float yy1 = -100;

    //提供绘制控件的轨迹
    public OLPath() {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        initPath();//初始化线路经
    }

    public OLPath(float width, int color) {
        super();
        initPaint(width, color);//真笔，第二次验证怀疑不是真笔
        initPath();//初始化线路经
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public Path getmPath() {
        return mPath;
    }

    public void setAntiAliasMeoth(boolean isAntiaalias) {
        if (mPaint != null) {
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    public void initPath() {
        mPath = new Path();//提供空路胫。
        pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);
    }

    public void setPaintStrokeWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    private void initPaint(float width, int color) {
        PathEffect pathEffect = new CornerPathEffect(100);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setColor(color);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setPathEffect(pathEffect);
    }

    public void setPath(Path path) {
        this.mPath = path;
    }

    public void addPath(Path path) {
        this.mPath.addPath(path);
    }

    /**
     * 将Path利用Paint绘制到指定Canvas上面
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        setAntiAliasMeoth(true);
        canvas.setDrawFilter(pfdf);
        canvas.drawPath(mPath, mPaint);
    }

    public void draw(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        setAntiAliasMeoth(true);
        canvas.setDrawFilter(pfdf);
        canvas.drawPath(mPath, mPaint);
        canvas.translate(0, 200);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * 将Path进行MOVETO到这个点上面，贝塞尔曲线起点
     *
     * @param x
     * @param y
     */
    public void touchDown(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * 将Path进行LineTo到这个点上面，
     *
     * @param x
     * @param y
     */
    public boolean touchMove(float x, float y) {
        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);//平滑
        mX = x;
        mY = y;
        return true;
    }

    public boolean lineTo(float x, float y) {
        mPath.lineTo(x, y);
        return true;
    }

    public void touchUp(float x, float y) {
        mX = x;
        mY = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
