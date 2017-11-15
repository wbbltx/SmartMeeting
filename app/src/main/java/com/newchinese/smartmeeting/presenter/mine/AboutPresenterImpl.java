package com.newchinese.smartmeeting.presenter.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.model.AboutModelImp;
import com.newchinese.smartmeeting.util.log.XLog;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class AboutPresenterImpl implements AboutContract.AboutIPresenter<AboutContract.AboutIView> {


    private static final java.lang.String TAG = "AboutPresenterImpl";
    private final Context context;
    private AboutContract.AboutIView mV;
    private AboutModelImp mModel;

    public AboutPresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public AboutContract.AboutIPresenter attach(AboutContract.AboutIView aboutIView) {
        mV = aboutIView;
        mModel = new AboutModelImp(this,context);
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
        XLog.d(TAG," loading ");
    }

    @Override
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.update_dialog))
                .setNegativeButton(context.getString(R.string.update_nexttime), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.update_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mModel.downLoad();
                    }
                })
                .create().show();
    }

    public void showProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressNumberFormat("%1d KB/%2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void result() {


    }
}
