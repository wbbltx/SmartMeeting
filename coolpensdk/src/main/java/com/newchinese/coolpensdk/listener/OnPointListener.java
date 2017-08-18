package com.newchinese.coolpensdk.listener;


import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;

/**
 * Description:   各种类型点监听器 pointType：down，move，up
 * author         xulei
 * Date           2017/4/11
 */

public interface OnPointListener {
    void onStrokeCached(int fromType, NoteStroke noteStroke);

    void onPointCatched(int fromType, NotePoint point);

    void onPageIndexChanged(int fromType, NotePoint point);
}
