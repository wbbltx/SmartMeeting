package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.presenter.meeting.DraftBoxPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18 16:34
 */
public class DraftBoxActivity extends BaseActivity<DraftBoxPresenter,String> implements DraftBoxContract.View<String>{
    @BindView(R.id.iv_back)
    ImageView ivBack; //返回
    @BindView(R.id.tv_title)
    TextView tvTitle; //标题
    @BindView(R.id.iv_pen)
    ImageView ivPen; //笔图标
    private String classifyName; //分类名

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft_box;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

    }

    @Override
    protected DraftBoxPresenter initPresenter() {
        return new DraftBoxPresenter();
    }

    @Override
    protected void initStateAndData() {
        Intent intent = getIntent();
        classifyName = intent.getStringExtra("classify_name");

        tvTitle.setText(classifyName);
//        ivBack.setImageResource(0);
        ivPen.setBackgroundColor(Color.parseColor("#a6a6a6"));
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.iv_back, R.id.iv_pen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.iv_pen:

                break;
        }
    }

    @Override
    public void showResult(String o) {

    }
}
