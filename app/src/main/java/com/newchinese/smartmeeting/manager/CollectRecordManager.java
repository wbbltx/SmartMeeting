package com.newchinese.smartmeeting.manager;

import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;

import java.util.List;

/**
 * Description:   收藏记录表操作类
 * author         xulei
 * Date           2017/8/24
 */

public class CollectRecordManager {
    private static CollectRecordManager instance;

    public static CollectRecordManager getInstance() {
        if (instance == null) {
            synchronized (CollectRecordManager.class) {
                if (instance == null) {
                    instance = new CollectRecordManager();
                }
            }
        }
        return instance;
    }

    /**
     * 插入本
     */
    public CollectRecord insertCollectRecord(CollectRecordDao collectRecordDao, String classifyName,
                                             String collectRecordName) {
        CollectRecord collectRecord = new CollectRecord();
        collectRecord.setId(null);
        collectRecord.setClassifyName(classifyName);
        collectRecord.setCollectRecordName(collectRecordName);
        collectRecord.setCollectDate(System.currentTimeMillis());
        collectRecordDao.insert(collectRecord);
        return collectRecord;
    }

    /**
     * 根据分类获取CollectRecord集合
     */
    public List<CollectRecord> getCollectRecords(CollectRecordDao collectRecordDao, String classifyName) {
        return collectRecordDao.queryBuilder().where(CollectRecordDao.Properties.ClassifyName.eq(classifyName)).list();
    }
}
