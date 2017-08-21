package com.newchinese.smartmeeting.manager;

import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.model.bean.NoteRecord;

import java.util.List;

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
    public NoteRecord insertNoteRecord(NoteRecordDao noteRecordDao, String title, String date, String location,
                                       String member, String manager, String classifyName) {
        NoteRecord noteRecord = new NoteRecord();
        noteRecord.setId(null);
        noteRecord.setTitle(title);
        noteRecord.setDate(date);
        noteRecord.setLocation(location);
        noteRecord.setMember(member);
        noteRecord.setManager(manager);
        noteRecord.setClassifyName(classifyName);
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
