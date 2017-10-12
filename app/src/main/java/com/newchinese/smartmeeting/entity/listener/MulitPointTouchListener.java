package com.newchinese.smartmeeting.entity.listener;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/28 15:55
 */
public class MulitPointTouchListener implements View.OnTouchListener {

    // We can be in one of these 3 states
    static final int NONE = 0;//无
    static final int DRAG = 1;//拖曳
    static final int ZOOM = 2;//缩放
    private static final String TAG = "Touch";
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();//旧矩阵
    Matrix savedMatrix = new Matrix();//新矩阵
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();//触摸起始点
    PointF mid = new PointF();//？？？？
    float oldDist = 1f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;


        // Dump touch event to log
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://一点触摸down【也是多点触摸第一点down】
//down触发时，给matrix赋初值为当前图片的矩阵值
                matrix.set(view.getImageMatrix());
//down触发时，给savedMatrix赋初值为当前图片的矩阵值
                savedMatrix.set(matrix);
//down触发时，给start赋初值为当前触摸事件的手指点值
                start.set(event.getX(), event.getY());
                mode = DRAG;

                //Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN://多点触摸第二点down
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    //Log.d(TAG, "mode=ZOOM");
                }
                break;
//            最后一次抬起点时的先调用ACTION_UP、再调用ACTION_POINTER_UP
//            倒数第二次抬点只调用ACTION_POINTER_UP
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE://挪动
                if (mode == DRAG) {//一点就是拖曳
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);

                } else if (mode == ZOOM) {//多点就是缩放
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }

                break;
        }

        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        //Log.d(TAG, sb.toString());
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
