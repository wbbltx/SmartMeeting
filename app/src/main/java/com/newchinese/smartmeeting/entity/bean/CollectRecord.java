package com.newchinese.smartmeeting.entity.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.newchinese.smartmeeting.database.DaoSession;
import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.database.CollectRecordDao;

/**
 * Description:   收藏的记录表
 * author         xulei
 * Date           2017/8/18
 */
@Entity
public class CollectRecord {
    @Id(autoincrement = true)
    private Long id;
    private String classifyName; //分类
    private String collectRecordName; //名字
    private long collectDate; //收藏时间

    @ToMany(referencedJoinProperty = "bookId") //记录与页一对多
    private List<CollectPage> pageList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1815077541)
    private transient CollectRecordDao myDao;

    @Generated(hash = 591851038)
    public CollectRecord(Long id, String classifyName, String collectRecordName,
            long collectDate) {
        this.id = id;
        this.classifyName = classifyName;
        this.collectRecordName = collectRecordName;
        this.collectDate = collectDate;
    }

    @Generated(hash = 2133593564)
    public CollectRecord() {
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
    @Generated(hash = 829177212)
    public List<CollectPage> getPageList() {
        if (pageList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CollectPageDao targetDao = daoSession.getCollectPageDao();
            List<CollectPage> pageListNew = targetDao
                    ._queryCollectRecord_PageList(id);
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
    @Generated(hash = 759048126)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCollectRecordDao() : null;
    }

    public String getCollectRecordName() {
        return this.collectRecordName;
    }

    public void setCollectRecordName(String collectRecordName) {
        this.collectRecordName = collectRecordName;
    }

    public long getCollectDate() {
        return this.collectDate;
    }

    public void setCollectDate(long collectDate) {
        this.collectDate = collectDate;
    }

    @Override
    public String toString() {
        return "CollectRecord{" +
                "id=" + id +
                ", classifyName='" + classifyName + '\'' +
                ", collectRecordName='" + collectRecordName + '\'' +
                ", collectDate=" + collectDate +
                '}';
    }
}
