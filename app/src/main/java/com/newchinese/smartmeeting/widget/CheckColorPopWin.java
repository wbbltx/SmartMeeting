package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.entity.event.ColorEvent;
import com.newchinese.smartmeeting.ui.meeting.adapter.HorizontalListViewAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.color.ColorSelectDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;


/**
 * Description:   选笔色的弹出窗口
 * author         xulei
 * Date           2017/8/22 20:46
 */
public class CheckColorPopWin extends PopupWindow {
    private View view;
    private HorizontalListView hListView;
    private ColorSelectDialog colorSelectDialog;

    private int lastColor;
    private int[] colors = Constant.colors;
    private Context mContext;
    private ColorEvent event = null;
    private HorizontalListViewAdapter hListViewAdapter;
    private OnSelectListener onSelectListener;

    public CheckColorPopWin(final Context mContext, final OnSelectListener onSelectListener) {
        this.mContext = mContext;
        this.onSelectListener = onSelectListener;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.checkout_color, null);
        hListView = (HorizontalListView) view.findViewById(R.id.horizon_listview);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.popup_anim);
        hListViewAdapter = new HorizontalListViewAdapter(mContext, null, null, colors);
        hListView.setAdapter(hListViewAdapter);
        hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(mContext, "color", position + "");
                if (position <= 5) {
                    if (position == 5) {
                        Constant.colors[5] = colors[5];
                    }
                    event = new ColorEvent(position);
                    EventBus.getDefault().post(event);
                    DataCacheUtil.getInstance().setCurrentColorPosition(position);
                    onSelectListener.onSelect();
                    hListViewAdapter.notifyDataSetChanged();
                    if (event != null) {
                        EventBus.getDefault().post(event);
                    }
//                    dismiss();
                } else if (position == 6) {
                    if (colorSelectDialog == null) {
                        colorSelectDialog = new ColorSelectDialog(mContext);
                        colorSelectDialog.setOnColorSelectListener(new ColorSelectDialog.OnColorSelectListener() {
                            @Override
                            public void onSelectFinish(int color) {
                                lastColor = color;
                                colors[5] = color;
                                hListViewAdapter.notifyDataSetChanged();
                                SharedPreUtils.setInteger(mContext, "lastcolor", color);
                                DataCacheUtil.getInstance().setCurrentColorPosition(5);
                                onSelectListener.onSelect();
                                event = new ColorEvent(5);
                                EventBus.getDefault().post(event);
                                hListViewAdapter.notifyDataSetChanged();
                                if (event != null) {
                                    EventBus.getDefault().post(event);
                                }
                            }
                        });
                    }
                    colorSelectDialog.setLastColor(lastColor);
                    colorSelectDialog.show();
                }
            }
        });
    }

    public interface OnSelectListener {
        void onSelect();
    }
}
