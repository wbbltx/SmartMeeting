package com.newchinese.smartmeeting.manager;

import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.model.bean.NoteStroke;

/**
 * Description:   线表操作类
 * author         xulei
 * Date           2017/8/19
 */

public class NoteStrokeManager {
    private static NoteStrokeManager instance;

    public static NoteStrokeManager getInstance() {
        if (instance == null) {
            synchronized (NoteStrokeManager.class) {
                if (instance == null) {
                    instance = new NoteStrokeManager();
                }
            }
        }
        return instance;
    }

    /**
     * 插入线
     *
     * @param pageId 关联页Id
     */
    public NoteStroke insertNoteStroke(NoteStrokeDao noteStrokeDao, long pageId, int strokeColor,
                                       float strokeWidth) {
        NoteStroke noteStroke = new NoteStroke();
        noteStroke.setId(null);
        noteStroke.setPageId(pageId);
        noteStroke.setStrokeColor(strokeColor);
        noteStroke.setStrokeWidth(strokeWidth);
        noteStrokeDao.insert(noteStroke);
        return noteStroke;
    }
}
