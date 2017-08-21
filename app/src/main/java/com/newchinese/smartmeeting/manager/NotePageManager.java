package com.newchinese.smartmeeting.manager;


import com.newchinese.smartmeeting.database.NotePageDao;
import com.newchinese.smartmeeting.model.bean.NotePage;

import java.util.List;
import java.util.Objects;

/**
 * Description:   页表操作类
 * author         xulei
 * Date           2017/8/19
 */

public class NotePageManager {
    private static NotePageManager instance;

    public static NotePageManager getInstance() {
        if (instance == null) {
            synchronized (NotePageManager.class) {
                if (instance == null) {
                    instance = new NotePageManager();
                }
            }
        }
        return instance;
    }

    /**
     * 插入页
     *
     * @param bookId 关联本id
     */
    public NotePage insertNotePage(NotePageDao notePageDao, long bookId, int pageIndex, long date,
                                   String insertPicPath, List<String> screenPathList) {
        NotePage notePage = new NotePage();
        notePage.setId(null);
        notePage.setBookId(bookId);
        notePage.setPageIndex(pageIndex);
        notePage.setDate(date);
        notePage.setInsertPicPath(insertPicPath);
        notePage.setScreenPathList(screenPathList);
        notePageDao.insert(notePage);
        return notePage;
    }

    /**
     * 根据页码查询关联本中是否有此页的表
     *
     * @param bookId 关联本id
     */
    public NotePage getPageByIndex(NotePageDao notePageDao, long bookId, int pageIndex) {
        List<NotePage> notePageList = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(bookId)).list();
        if (notePageList != null && notePageList.size() > 0) { //当前本包含的页集合不为空
            for (NotePage notePage : notePageList) {
                if (Objects.equals(notePage.getPageIndex(), pageIndex)) {
                    return notePage;
                }
            }
        }
        return null;
    }
}
