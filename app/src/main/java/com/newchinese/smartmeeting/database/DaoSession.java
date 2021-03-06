package com.newchinese.smartmeeting.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NotePoint;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.bean.NoteStroke;

import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteRecordDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig collectPageDaoConfig;
    private final DaoConfig collectRecordDaoConfig;
    private final DaoConfig loginDataDaoConfig;
    private final DaoConfig notePageDaoConfig;
    private final DaoConfig notePointDaoConfig;
    private final DaoConfig noteRecordDaoConfig;
    private final DaoConfig noteStrokeDaoConfig;

    private final CollectPageDao collectPageDao;
    private final CollectRecordDao collectRecordDao;
    private final LoginDataDao loginDataDao;
    private final NotePageDao notePageDao;
    private final NotePointDao notePointDao;
    private final NoteRecordDao noteRecordDao;
    private final NoteStrokeDao noteStrokeDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        collectPageDaoConfig = daoConfigMap.get(CollectPageDao.class).clone();
        collectPageDaoConfig.initIdentityScope(type);

        collectRecordDaoConfig = daoConfigMap.get(CollectRecordDao.class).clone();
        collectRecordDaoConfig.initIdentityScope(type);

        loginDataDaoConfig = daoConfigMap.get(LoginDataDao.class).clone();
        loginDataDaoConfig.initIdentityScope(type);

        notePageDaoConfig = daoConfigMap.get(NotePageDao.class).clone();
        notePageDaoConfig.initIdentityScope(type);

        notePointDaoConfig = daoConfigMap.get(NotePointDao.class).clone();
        notePointDaoConfig.initIdentityScope(type);

        noteRecordDaoConfig = daoConfigMap.get(NoteRecordDao.class).clone();
        noteRecordDaoConfig.initIdentityScope(type);

        noteStrokeDaoConfig = daoConfigMap.get(NoteStrokeDao.class).clone();
        noteStrokeDaoConfig.initIdentityScope(type);

        collectPageDao = new CollectPageDao(collectPageDaoConfig, this);
        collectRecordDao = new CollectRecordDao(collectRecordDaoConfig, this);
        loginDataDao = new LoginDataDao(loginDataDaoConfig, this);
        notePageDao = new NotePageDao(notePageDaoConfig, this);
        notePointDao = new NotePointDao(notePointDaoConfig, this);
        noteRecordDao = new NoteRecordDao(noteRecordDaoConfig, this);
        noteStrokeDao = new NoteStrokeDao(noteStrokeDaoConfig, this);

        registerDao(CollectPage.class, collectPageDao);
        registerDao(CollectRecord.class, collectRecordDao);
        registerDao(LoginData.class, loginDataDao);
        registerDao(NotePage.class, notePageDao);
        registerDao(NotePoint.class, notePointDao);
        registerDao(NoteRecord.class, noteRecordDao);
        registerDao(NoteStroke.class, noteStrokeDao);
    }
    
    public void clear() {
        collectPageDaoConfig.clearIdentityScope();
        collectRecordDaoConfig.clearIdentityScope();
        loginDataDaoConfig.clearIdentityScope();
        notePageDaoConfig.clearIdentityScope();
        notePointDaoConfig.clearIdentityScope();
        noteRecordDaoConfig.clearIdentityScope();
        noteStrokeDaoConfig.clearIdentityScope();
    }

    public CollectPageDao getCollectPageDao() {
        return collectPageDao;
    }

    public CollectRecordDao getCollectRecordDao() {
        return collectRecordDao;
    }

    public LoginDataDao getLoginDataDao() {
        return loginDataDao;
    }

    public NotePageDao getNotePageDao() {
        return notePageDao;
    }

    public NotePointDao getNotePointDao() {
        return notePointDao;
    }

    public NoteRecordDao getNoteRecordDao() {
        return noteRecordDao;
    }

    public NoteStrokeDao getNoteStrokeDao() {
        return noteStrokeDao;
    }

}
