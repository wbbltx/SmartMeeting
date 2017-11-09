package com.newchinese.smartmeeting.presenter.mine;

import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.model.AboutModelImp;
import com.newchinese.smartmeeting.util.log.XLog;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class AboutPresenterImpl implements AboutContract.AboutIPresenter<AboutContract.AboutIView> {


    private AboutContract.AboutIView mV;
    private AboutModelImp mModel;

    @Override
    public AboutContract.AboutIPresenter attach(AboutContract.AboutIView aboutIView) {
        mV = aboutIView;
        mModel = new AboutModelImp(this);
        return this;
    }

    @Override
    public AboutContract.AboutIPresenter detach() {
        mV = null;
        return this;
    }

    @Override
    public void checkVersion() {
        mModel.checkVersion();
    }

    @Override
    public void loading() {
        XLog.d("hahehe"," loading ");
    }

    @Override
    public void result() {


    }
}
