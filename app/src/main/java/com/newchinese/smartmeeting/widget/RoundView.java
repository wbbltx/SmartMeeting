package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class RoundView extends android.support.v7.widget.AppCompatImageView {
    public RoundView(Context context) {
        this(context,null);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Paint paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
