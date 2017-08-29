package com.newchinese.smartmeeting.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.DaoMaster;
import com.newchinese.smartmeeting.database.DaoSession;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;


/**
 * Description:   GreenDao数据库工具类
 * author         xulei
 * Date           2017/7/5
 */

public class GreenDaoUtil {
    private DaoSession daoSession;
    private NoteRecordDao noteRecordDao;
    private NotePageDao notePageDao;
    private NoteStrokeDao noteStrokeDao;
    private NotePointDao notePointDao;
    private CollectRecordDao collectRecordDao;
    private CollectPageDao collectPageDao;

    private static class SingleHolder {
        private static final GreenDaoUtil INSTANCE = new GreenDaoUtil();
    }

    public static GreenDaoUtil getInstance() {
        return SingleHolder.INSTANCE;
    }

    private GreenDaoUtil() {
    }

    /**
     * 初始化数据库
     * 建议放在Application中执行
     */
    public void initDataBase(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "smartmeeting.db", null);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        noteRecordDao = daoSession.getNoteRecordDao();
        notePageDao = daoSession.getNotePageDao();
        noteStrokeDao = daoSession.getNoteStrokeDao();
        notePointDao = daoSession.getNotePointDao();
        collectRecordDao = daoSession.getCollectRecordDao();
        collectPageDao = daoSession.getCollectPageDao();
    }

    /**
     * 获取DaoSession对象
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 获取NoteRecordDao对象
     */
    public NoteRecordDao getNoteRecordDao() {
        return noteRecordDao;
    }

    /**
     * 获取NotePageDao对象
     */
    public NotePageDao getNotePageDao() {
        return notePageDao;
    }

    /**
     * 获取NoteStrokeDao对象
     */
    public NoteStrokeDao getNoteStrokeDao() {
        return noteStrokeDao;
    }

    /**
     * 获取NotePointDao对象
     */
    public NotePointDao getNotePointDao() {
        return notePointDao;
    }

    public CollectRecordDao getCollectRecordDao() {
        return collectRecordDao;
    }

    public CollectPageDao getCollectPageDao() {
        return collectPageDao;
    }
}
