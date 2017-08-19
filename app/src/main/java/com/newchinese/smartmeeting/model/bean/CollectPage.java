package com.newchinese.smartmeeting.model.bean;

import com.newchinese.smartmeeting.util.StringConverterUtil;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Description:   收藏的页表
 * author         xulei
 * Date           2017/8/18 17:22
 */
@Entity
public class CollectPage {
    @Id(autoincrement = true)
    private Long id;
    private int pageIndex; //页码
    private long bookId; //记录表Id
    private long date; //时间
    private String picPath; //缩略图文件路径
    @Convert(columnType = String.class, converter = StringConverterUtil.class)
    private List<String> screenPathList; //录屏文件路径，需将List转换为StringBuilder
    @Generated(hash = 1131121656)
    public CollectPage(Long id, int pageIndex, long bookId, long date,
            String picPath, List<String> screenPathList) {
        this.id = id;
        this.pageIndex = pageIndex;
        this.bookId = bookId;
        this.date = date;
        this.picPath = picPath;
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
    public String getPicPath() {
        return this.picPath;
    }
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
    public List<String> getScreenPathList() {
        return this.screenPathList;
    }
    public void setScreenPathList(List<String> screenPathList) {
        this.screenPathList = screenPathList;
    }
}
