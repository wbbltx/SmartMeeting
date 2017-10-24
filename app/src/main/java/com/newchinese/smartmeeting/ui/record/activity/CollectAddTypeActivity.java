package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.util.BluCommonUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 点击补充按钮后跳转到该页面
 */
public class CollectAddTypeActivity extends BaseSimpleActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_add_type;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        tvTitle.setVisibility(View.VISIBLE);
        ivPen.setVisibility(View.GONE);
    }

    @Override
    protected void initStateAndData() {
        tvTitle.setText(R.string.style_meeting);
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.tv_type_one, R.id.tv_type_two, R.id.tv_type_three, R.id.tv_type_four, R.id.tv_type_five, R.id.tv_type_six, R.id.tv_type_seven, R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type_one://学习培训
                jump(Constant.CLASSIFY_NAME_STUDY);
                break;

            case R.id.tv_type_two://工作例会
                jump(Constant.CLASSIFY_NAME_WORK);
                break;

            case R.id.tv_type_three://项目会议
                jump(Constant.CLASSIFY_NAME_PROJECT);
                break;

            case R.id.tv_type_four://研讨会
                jump(Constant.CLASSIFY_NAME_EXPLORE);
                break;

            case R.id.tv_type_five://工作汇报
                jump(Constant.CLASSIFY_NAME_REPORT);
                break;

            case R.id.tv_type_six://评审会
                jump(Constant.CLASSIFY_NAME_REVIEW);
                break;

            case R.id.tv_type_seven://其他
                jump(Constant.CLASSIFY_NAME_OTHER);
                break;

            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void jump(String classifyName) {
        Intent intent = new Intent(this, CollectToAddListActivity.class);
        intent.putExtra(BluCommonUtils.CLASSIFY_NAME, classifyName);
        startActivity(intent);
    }
}
