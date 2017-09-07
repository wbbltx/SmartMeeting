package com.newchinese.smartmeeting.manager;


import com.newchinese.smartmeeting.database.CollectPageDao;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.NotePage;

import java.util.List;
import java.util.Objects;

/**
 * Description:   收藏页表操作类
 * author         xulei
 * Date           2017/8/24
 */

public class CollectPageManager {
    private static CollectPageManager instance;

    public static CollectPageManager getInstance() {
        if (instance == null) {
            synchronized (CollectPageManager.class) {
                if (instance == null) {
                    instance = new CollectPageManager();
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
    public CollectPage insertCollectPage(CollectPageDao collectPageDao, long bookId, int pageIndex, long date,
                                         String thumbnailPath, List<String> screenPathList) {
        CollectPage collectPage = new CollectPage();
        collectPage.setId(null);
        collectPage.setBookId(bookId);
        collectPage.setPageIndex(pageIndex);
        collectPage.setDate(date);
        collectPage.setThumbnailPath(thumbnailPath);
        collectPage.setScreenPathList(screenPathList);
        collectPageDao.insert(collectPage);
        return collectPage;
    }

    /**
     * 根据页码查询关联本中是否有此页的表
     */
    public CollectPage getPageByIndex(CollectPageDao collectPageDao, long bookId, int pageIndex) {
        List<CollectPage> collectPageList = collectPageDao.queryBuilder().where(CollectPageDao.Properties.BookId.eq(bookId)).list();
        if (collectPageList != null && collectPageList.size() > 0) { //当前本包含的页集合不为空
            for (CollectPage collectPage : collectPageList) {
                if (Objects.equals(collectPage.getPageIndex(), pageIndex)) {
                    return collectPage;
                }
            }
        }
        return null;
    }
}
