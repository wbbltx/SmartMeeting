package com.newchinese.smartmeeting.contract;

import com.newchinese.smartmeeting.base.BaseSimplePresenter;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.model.bean.CollectRecord;

import java.util.List;

/**
 * Description:   收藏记录分类筛选列表Activity的Contract
 * author         xulei
 * Date           2017/8/25
 */

public interface CollectRecordFilterActContract {
    interface View<E> extends BaseView<E> {
        void getFilterCollectRecordData(List<CollectRecord> collectRecords);
    }

    interface Presenter extends BaseSimplePresenter<View> {
        void loadCollectPageDataByClassify(String classifyName);
    }
}
