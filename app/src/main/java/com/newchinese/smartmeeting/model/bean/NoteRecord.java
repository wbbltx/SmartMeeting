package com.newchinese.smartmeeting.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.newchinese.smartmeeting.database.DaoSession;
import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.database.NoteRecordDao;

/**
 * Description:   记录表
 * author         xulei
 * Date           2017/8/18
 */
@Entity
public class NoteRecord {
    @Id(autoincrement = true)
    private Long id;
    private String classifyName; //分类
    private String classifyCode; //分类code，用于读写缩略图

    @ToMany(referencedJoinProperty = "bookId") //记录与页一对多
    private List<NotePage> pageList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1863175196)
    private transient NoteRecordDao myDao;

    @Generated(hash = 415565195)
    public NoteRecord(Long id, String classifyName, String classifyCode) {
        this.id = id;
        this.classifyName = classifyName;
        this.classifyCode = classifyCode;
    }

    @Generated(hash = 38732380)
    public NoteRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassifyName() {
        return this.classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1065801672)
    public List<NotePage> getPageList() {
        if (pageList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NotePageDao targetDao = daoSession.getNotePageDao();
            List<NotePage> pageListNew = targetDao._queryNoteRecord_PageList(id);
            synchronized (this) {
                if (pageList == null) {
                    pageList = pageListNew;
                }
            }
        }
        return pageList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 852283546)
    public synchronized void resetPageList() {
        pageList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1219103832)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteRecordDao() : null;
    }

    @Override
    public String toString() {
        return "NoteRecord{" +
                "id=" + id +
                ", classifyName='" + classifyName + '\'' +
                '}';
    }

    public String getClassifyCode() {
        return this.classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }
}
