package com.newchinese.smartmeeting.manager;

import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;

/**
 * Description:   记录表操作类
 * author         xulei
 * Date           2017/8/19
 */

public class NoteRecordManager {
    private static NoteRecordManager instance;

    public static NoteRecordManager getInstance() {
        if (instance == null) {
            synchronized (NoteRecordManager.class) {
                if (instance == null) {
                    instance = new NoteRecordManager();
                }
            }
        }
        return instance;
    }

    /**
     * 插入本
     */
    public NoteRecord insertNoteRecord(NoteRecordDao noteRecordDao, String classifyName,
                                       String classifyCode) {
        NoteRecord noteRecord = new NoteRecord();
        noteRecord.setId(null);
        noteRecord.setClassifyName(classifyName);
        noteRecord.setClassifyCode(classifyCode);
        noteRecordDao.insert(noteRecord);
        return noteRecord;
    }

    /**
     * 根据分类获取NoteRecord对象
     */
    public NoteRecord getNoteRecord(NoteRecordDao noteRecordDao, String classifyName) {
        return noteRecordDao.queryBuilder().where(NoteRecordDao.Properties.ClassifyName.eq(classifyName)).unique();
    }
}
