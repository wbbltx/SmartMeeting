package com.newchinese.smartmeeting.presenter.mine;

import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.FeedBack;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.model.UpdateModelImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

/**
 * Created by Administrator on 2017-08-30.
 */

public class UpdatePresenterImpl implements MineContract.UpdateIPresenter<MineContract.UpdateIVIew> {
    private MineContract.UpdateIVIew mV;
    private UpdateModelImpl mModel;

    @Override
    public MineContract.UpdateIPresenter attach(MineContract.UpdateIVIew v) {

        mV = v;
        mModel = new UpdateModelImpl(this);
        return this;
    }

    @Override
    public void detach() {
        mV = null;
    }

    @Override
    public void updateNick(String nick) {
        GreenDaoUtil.getInstance().getDaoSession().clear();
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
        data.setId(null).setTel(null).setNickname(null);
        mModel.updateNick(data.setNickname(nick));
    }

    @Override
    public void updatePass(String oldPass, String newPass) {
//        GreenDaoUtil.getInstance().getDaoSession().clear();
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
//        data.setId(null).setTel(null).setNickname(null);
        mModel.updatePass(data.setPassword(oldPass).setNew_password(newPass));
    }

    @Override
    public void feedBack(String content, String contact) {
        mModel.feedBack(new FeedBack().setConnect(content).setFeed_back(contact));
    }

    @Override
    public void loading() {
        if (mV != null) {
            mV.showLoading("正在请求...");
        }
    }

    @Override
    public void onResult(boolean succ, BaseResult data) {
        CustomizedToast.showLong(App.getAppliction(), data.msg);
        if (mV != null) {
            mV.showLoading(null);
        }
        if ("修改成功".equals(data.msg))
            mV.jumpLogin();
    }
}
