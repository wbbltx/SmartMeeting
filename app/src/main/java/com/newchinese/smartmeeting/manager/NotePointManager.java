package com.newchinese.smartmeeting.manager;

import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.entity.bean.NotePoint;

/**
 * Description:   点表操作类
 * author         xulei
 * Date           2017/8/19
 */

public class NotePointManager {
    private static NotePointManager instance;

    public static NotePointManager getInstance() {
        if (instance == null) {
            synchronized (NotePointManager.class) {
                if (instance == null) {
                    instance = new NotePointManager();
                }
            }
        }
        return instance;
    }

    /**
     * 插入点
     *
     * @param strokeId 关联先Id
     */
    public NotePoint insertPoint(NotePointDao notePointDao, long strokeId, float pX, float pY, float testTime,
                                 float firstPress, float press, int pageIndex, int pointType) {
        NotePoint notePoint = new NotePoint();
        notePoint.setId(null);
        notePoint.setStrokeId(strokeId);
        notePoint.setPX(pX);
        notePoint.setPY(pY);
        notePoint.setTestTime(testTime);
        notePoint.setFirstPress(firstPress);
        notePoint.setPress(press);
        notePoint.setPageIndex(pageIndex);
        notePoint.setPointType(pointType);
        notePointDao.insert(notePoint);
        return notePoint;
    }
}
