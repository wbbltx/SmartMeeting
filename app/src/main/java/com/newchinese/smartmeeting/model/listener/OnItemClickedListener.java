package com.newchinese.smartmeeting.model.listener;

import android.view.View;

/**
 * Description:   用于RecyclerView添加点击效果
 * author         xulei
 * Date           2017/8/21
 */

public interface OnItemClickedListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
