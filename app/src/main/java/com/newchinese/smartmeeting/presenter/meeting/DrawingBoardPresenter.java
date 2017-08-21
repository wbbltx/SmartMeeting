package com.newchinese.smartmeeting.presenter.meeting;

import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DrawingBoardContract;
import com.newchinese.smartmeeting.util.PointCacheUtil;

/**
 * Description:   画板Presenter
 * author         xulei
 * Date           2017/8/18
 */

public class DrawingBoardPresenter extends BasePresenter<DrawingBoardContract.View> implements DrawingBoardContract.Presenter {

    @Override
    public void onPresenterCreated() {
        
    }

    @Override
    public void onPresenterDestroy() {

    }

    @Override
    public boolean isBluetoothOpen() {
        return false;
    }

    @Override
    public void openBle() {

    }

    @Override
    public void scanBlueDevice() {

    }
    
    
    
    /**
     * 根据当前活动页读数据库的点
     */
    @Override
    public void readDataBasePoint() {
        
    }

    /**
     * 加载第一笔缓存
     */
    @Override
    public void loadFirstStokeCache() {
        PointCacheUtil pointCacheUtil = PointCacheUtil.getInstance();
        com.newchinese.coolpensdk.entity.NotePoint[] all = pointCacheUtil.getArrayAll();
        if (all.length > 0) {
            for (com.newchinese.coolpensdk.entity.NotePoint notePoint : all) {
                mView.getFirstStrokeCachePoint(notePoint);
            }
            pointCacheUtil.clearQueue();
        }
        pointCacheUtil.setCanAddFlag(false);
    }
}
