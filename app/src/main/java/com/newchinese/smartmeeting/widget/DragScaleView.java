package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 */
public class DragScaleView extends View implements View.OnTouchListener {
    private static final String TAG = "DragScaleView";
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;   //21
    private static final int LEFT = 0x16;  //22
    private static final int BOTTOM = 0x17;  //23
    private static final int RIGHT = 0x18;   //24
    private static final int LEFT_TOP = 0x11;  //17
    private static final int RIGHT_TOP = 0x12;//18
    private static final int LEFT_BOTTOM = 0x13;//19
    private static final int RIGHT_BOTTOM = 0x14;//20
    private static final int CENTER = 0x19;//25
    private int offset = 20;
    private int lim = 25;
    protected Paint paint = new Paint();
    private OnPositionListener onPositionListener;
    private int heightOffset;

    /**
     * 初始化获取屏幕宽高 为什么要减去40
     */
    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - 50;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
        Log.i(TAG, "初始化 " + screenHeight + "---" + screenWidth);
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(offset, offset, getWidth() - offset, getHeight()
                - offset, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.i(TAG, "onTouch");
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(v, (int) event.getX(),//为了获得点击的是哪个位置
                    (int) event.getY());
//            Log.i(TAG, "MotionEvent.ACTION_DOWN " + oriLeft + "---" + oriRight + "---" + oriTop + "---" + oriBottom + "---" + lastY + "---" + lastX + "---" + dragDirection);
        }
        // 处理拖动事件
        delDrag(v, event, action);
        invalidate();
        return false;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     * @param action
     */
    protected void delDrag(View v, MotionEvent event, int action) {
//        Log.i(TAG, "处理拖动事件");
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
//                Log.i(TAG, "处理拖动事件 ACTION_MOVE " + dx + "---" + dy + "---");
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                    if (onPositionListener != null){
//                        Log.i(TAG, "触摸点为不为中心 监听到边界改变 "+ oriLeft + "---" + oriTop + "---" + oriRight + "---" + oriBottom);
                        onPositionListener.getboundary(oriLeft, oriTop, oriRight, oriBottom);
                    }
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
//        Log.i(TAG, "触摸点为中心 " + left + "---" + top + "---" + right + "---" + bottom);
        if (left < -offset) {
//            Log.i(TAG, "触摸点为中心1 " + left);
            left = -offset;
            right = left + v.getWidth();
//            Log.i(TAG, "触摸点为中心2 " + left + "+++" + right);
        }
        if (right > screenWidth + offset) {
//            Log.i(TAG, "触摸点为中心3 " + right);
            right = screenWidth + offset;
            left = right - v.getWidth();
//            Log.i(TAG, "触摸点为中心4 " + left + "+++" + right);
        }
        if (top < -offset) {
//            Log.i(TAG, "触摸点为中心5 " + top);
            top = -offset;
            bottom = top + v.getHeight();
//            Log.i(TAG, "触摸点为中心6 " + top + "+++" + bottom);
        }
//        if (bottom > screenHeight + offset) {
        if (bottom > screenHeight + offset) {
//            Log.i(TAG, "触摸点为中心 bottom " + bottom + "+++" + screenHeight + "+++" + offset);
//            bottom = screenHeight + offset;
            bottom = screenHeight + offset;
            top = bottom - v.getHeight();
//            Log.i(TAG, "触摸点为中心 bottom " + top + "+++" + bottom);
        }
//        Log.i(TAG, "触摸点为中心9 " + left + "---" + top + "---" + right + "---" + bottom);
        if (onPositionListener != null){
//            Log.i(TAG, "触摸点为中心 监听到边界改变"+ left + "---" + top + "---" + right + "---" + bottom);
            onPositionListener.getboundary(left, top, right, bottom);
        }
        v.layout(left, top, right, bottom);
    }

    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
//        Log.i(TAG, "触摸点为上边缘");
        oriTop += dy;
        if (oriTop < -offset) {
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < 50) {
            oriTop = oriBottom - 2 * offset - 50;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        Log.i(TAG, "触摸点为下边缘");
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 50) {
            oriBottom = 50 + oriTop + 2 * offset;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
//        Log.i(TAG, "触摸点为右边缘");
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 50) {
            oriRight = oriLeft + 2 * offset + 50;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
//        Log.i(TAG, "触摸点为左边缘");
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
//            Log.i(TAG, "触摸点为左边缘 " + "---" + oriLeft + "---" + offset);
        }
        if (oriRight - oriLeft - 2 * offset < 50) {
            oriLeft = oriRight - 2 * offset - 50;
//            Log.i(TAG, "触摸点为左边缘 " + "---" + oriLeft);
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x 触电距离view左边的距离
     * @param y 触电距离view右边的距离
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();  //控件距离父控件的距离 在这里就是该view距离屏幕左边的距离
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
//        Log.i(TAG, "获取触摸点flag " + left + "---" + right + "---" + bottom + "---" + top + "---" + x + "---" + y);
        if (x < lim && y < lim) {
//            Log.i(TAG, "获取触摸点左上 " + LEFT_TOP);
            return LEFT_TOP;
        }
        if (y < lim && right - left - x < lim) {
//            Log.i(TAG, "获取触摸点右上 " + RIGHT_TOP);
            return RIGHT_TOP;
        }
        if (x < lim && bottom - top - y < lim) {
//            Log.i(TAG, "获取触摸点坐左下" + LEFT_BOTTOM);
            return LEFT_BOTTOM;
        }
        if (right - left - x < lim && bottom - top - y < lim) {
//            Log.i(TAG, "获取触摸点右下 " + RIGHT_BOTTOM);
            return RIGHT_BOTTOM;
        }
        if (x < lim) {
//            Log.i(TAG, "获取触摸点左 " + LEFT);
            return LEFT;
        }
        if (y < lim) {
//            Log.i(TAG, "获取触摸点上 " + TOP);
            return TOP;
        }
        if (right - left - x < lim) {
//            Log.i(TAG, "获取触摸点右 " + RIGHT);
            return RIGHT;
        }
        if (bottom - top - y < lim) {
//            Log.i(TAG, "获取触摸点底部 " + BOTTOM);
            return BOTTOM;
        }
//        Log.i(TAG, "获取触摸点中心 " + CENTER);
        return CENTER;
    }

    /**
     * 获取截取宽度
     *
     * @return
     */
    public int getCutWidth() {
        return getWidth() - 2 * offset;
    }

    /**
     * 获取截取高度
     *
     * @return
     */
    public int getCutHeight() {
        return getHeight() - 2 * offset;
    }

    public int px2dp(float px) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    public interface OnPositionListener {
        void getboundary(int left, int top, int right, int bottom);
    }

    public void setOnPositionListener(OnPositionListener onPositionListener) {
        this.onPositionListener = onPositionListener;
    }
}