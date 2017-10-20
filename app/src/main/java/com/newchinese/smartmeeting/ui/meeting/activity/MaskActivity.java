package com.newchinese.smartmeeting.ui.meeting.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;

import butterknife.BindView;

public class MaskActivity extends Activity implements View.OnClickListener {

    ImageView ivMaskTwo;
    ImageView ivMaskThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);
        SharedPreUtils.setBoolean(BluCommonUtils.IS_FIRST_INSTALL, false); //首次安装标记置否
        ivMaskTwo = (ImageView)findViewById(R.id.iv_mask_two);
        ivMaskThree = (ImageView)findViewById(R.id.iv_mask_three);
        initListener();
    }

    private void initListener() {
        ivMaskTwo.setOnClickListener(this);
        ivMaskThree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_mask_two:
                ivMaskTwo.setVisibility(View.GONE);
                ivMaskThree.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_mask_three:
                ivMaskTwo.setVisibility(View.GONE);
                ivMaskThree.setVisibility(View.GONE);
                Intent intent = new Intent(MaskActivity.this, DraftBoxActivity.class);
                intent.putExtra("classify_name", Constant.CLASSIFY_NAME_STUDY);
                startActivity(intent);
                finish();
                break;

        }
    }
}
