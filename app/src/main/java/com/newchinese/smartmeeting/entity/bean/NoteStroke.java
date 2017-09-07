package com.newchinese.smartmeeting.entity.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.newchinese.smartmeeting.database.DaoSession;
import com.newchinese.smartmeeting.database.NotePointDao;
import com.newchinese.smartmeeting.database.NoteStrokeDao;

/**
 * Description:   线表
 * author         xulei
 * Date           2017/8/18 17:23
 */
@Entity
public class NoteStroke {
    @Id(autoincrement = true)
    private Long id;
    private long pageId; //页表关联Id
    private int strokeColor; //线色
    private float strokeWidth; //线宽
    @ToMany(referencedJoinProperty = "strokeId") 
    private List<NotePoint> pointList; //线点一对多关联
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1790906883)
    private transient NoteStrokeDao myDao;
    @Generated(hash = 1818537999)
    public NoteStroke(Long id, long pageId, int strokeColor, float strokeWidth) {
        this.id = id;
        this.pageId = pageId;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }
    @Generated(hash = 566528330)
    public NoteStroke() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getPageId() {
        return this.pageId;
    }
    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
    public int getStrokeColor() {
        return this.strokeColor;
    }
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }
    public float getStrokeWidth() {
        return this.strokeWidth;
    }
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1384534157)
    public List<NotePoint> getPointList() {
        if (pointList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NotePointDao targetDao = daoSession.getNotePointDao();
            List<NotePoint> pointListNew = targetDao._queryNoteStroke_PointList(id);
            synchronized (this) {
                if (pointList == null) {
                    pointList = pointListNew;
                }
            }
        }
        return pointList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 18518400)
    public synchronized void resetPointList() {
        pointList = null;
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1078723076)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteStrokeDao() : null;
    }

    @Override
    public String toString() {
        return "NoteStroke{" +
                "id=" + id +
                ", pageId=" + pageId +
                ", strokeColor=" + strokeColor +
                ", strokeWidth=" + strokeWidth +
                '}';
    }
}
