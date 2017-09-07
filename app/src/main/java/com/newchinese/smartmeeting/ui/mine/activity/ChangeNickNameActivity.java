package com.newchinese.smartmeeting.ui.mine.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.ApiService;
import com.newchinese.smartmeeting.entity.http.NetProviderImpl;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.entity.http.XApi;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:   修改昵称Activity
 * author         xulei
 * Date           2017/8/30 10:38
 */
public class ChangeNickNameActivity extends BaseSimpleActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.et_nick_name)
    EditText etNickName;

    private LoginDataDao loginDataDao;
    private LoginData loginData;
    private ApiService mServices;
    private Flowable<BaseResult<LoginData>> observable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_nick_name;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        ivPen.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
        tvTitle.setText("修改昵称");
        loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
        loginData = loginDataDao.queryBuilder().unique();
        if (loginData != null) {
            etNickName.setText(loginData.getNickname() + "");
            etNickName.setSelection(loginData.getNickname().length());
        }
        XApi.registerProvider(new NetProviderImpl());
        mServices = XApi.get(NetUrl.HOST, ApiService.class);
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                String nickName = etNickName.getText().toString();
                if (nickName.isEmpty()) {
                    Toast.makeText(mContext, "请输入昵称", Toast.LENGTH_SHORT).show();
                } else {
                    tvRight.setClickable(false); //防止连续点击
                    loginData.setNickname(nickName);
                    loginDataDao.update(loginData);
                    observable = mServices.updateNick(loginData);
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BaseResult<LoginData>>() {
                                @Override
                                public void accept(BaseResult<LoginData> loginDataBaseResult) throws Exception {
                                    Log.e("test_http", "" + loginDataBaseResult.toString());
                                    Toast.makeText(ChangeNickNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(ChangeNickNameActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
                                    tvRight.setClickable(true); //防止连续点击
                                }
                            });
                }
                break;
        }
    }
}
