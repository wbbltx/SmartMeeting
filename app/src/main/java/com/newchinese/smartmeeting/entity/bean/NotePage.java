package com.newchinese.smartmeeting.entity.bean;

import com.newchinese.smartmeeting.util.StringConverterUtil;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.newchinese.smartmeeting.database.DaoSession;
import com.newchinese.smartmeeting.database.NoteStrokeDao;
import com.newchinese.smartmeeting.database.NotePageDao;

/**
 * Description:   页表
 * author         xulei
 * Date           2017/8/18 17:22
 */
@Entity
public class NotePage {
    @Id(autoincrement = true)
    private Long id;
    private int pageIndex; //页码
    private long bookId; //记录表Id
    private long date; //时间
    private String insertPicPath; //插入图片路径
    private float X;
    private float Y;
    private  float height;
    private  float width;
    private String thumbnailPath; //缩略图路径
    @Convert(columnType = String.class, converter = StringConverterUtil.class)
    private List<String> screenPathList; //录屏文件路径，需将List转换为StringBuilder
    @ToMany(referencedJoinProperty = "pageId") //页线一对多
    private List<NoteStroke> strokeList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1639320209)
    private transient NotePageDao myDao;

    @Generated(hash = 711196225)
    public NotePage(Long id, int pageIndex, long bookId, long date, String insertPicPath,
            float X, float Y, float height, float width, String thumbnailPath,
            List<String> screenPathList) {
        this.id = id;
        this.pageIndex = pageIndex;
        this.bookId = bookId;
        this.date = date;
        this.insertPicPath = insertPicPath;
        this.X = X;
        this.Y = Y;
        this.height = height;
        this.width = width;
        this.thumbnailPath = thumbnailPath;
        this.screenPathList = screenPathList;
    }

    @Generated(hash = 1457843902)
    public NotePage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getScreenPathList() {
        return this.screenPathList;
    }

    public void setScreenPathList(List<String> screenPathList) {
        this.screenPathList = screenPathList;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1497590420)
    public List<NoteStroke> getStrokeList() {
        if (strokeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteStrokeDao targetDao = daoSession.getNoteStrokeDao();
            List<NoteStroke> strokeListNew = targetDao
                    ._queryNotePage_StrokeList(id);
            synchronized (this) {
                if (strokeList == null) {
                    strokeList = strokeListNew;
                }
            }
        }
        return strokeList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 531561772)
    public synchronized void resetStrokeList() {
        strokeList = null;
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
    @Generated(hash = 165825040)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNotePageDao() : null;
    }

    public String getInsertPicPath() {
        return this.insertPicPath;
    }

    public void setInsertPicPath(String insertPicPath) {
        this.insertPicPath = insertPicPath;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    public String toString() {
        return "NotePage{" +
                "id=" + id +
                ", pageIndex=" + pageIndex +
                ", bookId=" + bookId +
                ", date=" + date +
                ", insertPicPath='" + insertPicPath + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", screenPathList=" + screenPathList +
                '}';
    }

    public float getX() {
        return this.X;
    }

    public void setX(float X) {
        this.X = X;
    }

    public float getY() {
        return this.Y;
    }

    public void setY(float Y) {
        this.Y = Y;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
