package com.newchinese.smartmeeting.entity.bean;

import com.newchinese.smartmeeting.util.StringConverterUtil;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Description:   收藏的页表
 * author         xulei
 * Date           2017/8/18 17:22
 */
@Entity
public class CollectPage implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    @Id(autoincrement = true)
    private Long id;
    private int pageIndex; //页码
    private long bookId; //记录表Id
    private long date; //时间
    private String thumbnailPath; //缩略图文件路径
    @Convert(columnType = String.class, converter = StringConverterUtil.class)
    private List<String> screenPathList; //录屏文件路径，需将List转换为StringBuilder

    @Generated(hash = 1475832732)
    public CollectPage(Long id, int pageIndex, long bookId, long date,
                       String thumbnailPath, List<String> screenPathList) {
        this.id = id;
        this.pageIndex = pageIndex;
        this.bookId = bookId;
        this.date = date;
        this.thumbnailPath = thumbnailPath;
        this.screenPathList = screenPathList;
    }

    @Generated(hash = 781769683)
    public CollectPage() {
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

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public List<String> getScreenPathList() {
        return this.screenPathList;
    }

    public void setScreenPathList(List<String> screenPathList) {
        this.screenPathList = screenPathList;
    }

    @Override
    public String toString() {
        return "CollectPage{" +
                "id=" + id +
                ", pageIndex=" + pageIndex +
                ", bookId=" + bookId +
                ", date=" + date +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", screenPathList=" + screenPathList +
                '}';
    }
}
