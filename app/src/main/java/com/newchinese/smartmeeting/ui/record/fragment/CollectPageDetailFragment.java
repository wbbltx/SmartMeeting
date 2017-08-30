package com.newchinese.smartmeeting.ui.record.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.ui.meeting.activity.RecordLibActivity;

import java.util.List;

import butterknife.BindView;

/**
 * Description:   收藏页详情ViewPager的Fragment
 * author         xulei
 * Date           2017/8/26 10:16
 */
public class CollectPageDetailFragment extends BaseSimpleFragment implements View.OnClickListener {
    private static final String COLLECT_PAGE = "collectPage";
    @BindView(R.id.iv_thumnbail)
    ImageView ivThumnbail;
    @BindView(R.id.rl_record_count)
    RelativeLayout rlRecordCount;
    @BindView(R.id.record_count)
    TextView recordCount;
    private CollectPage collectPage;

    public CollectPageDetailFragment() {
    }

    public static CollectPageDetailFragment newInstance(CollectPage collectPage) {
        CollectPageDetailFragment fragment = new CollectPageDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(COLLECT_PAGE, collectPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collect_page_detail;
    }

    @Override
    protected void onFragViewCreated() {

    }

    @Override
    protected void initStateAndData() {
        if (getArguments() != null) {
            collectPage = (CollectPage) getArguments().getSerializable(COLLECT_PAGE);
            Log.i("test_page", "" + collectPage.toString());
            if (collectPage != null) {
                List<String> screenPathList = collectPage.getScreenPathList();
                if (screenPathList != null && screenPathList.size()!=0 && screenPathList.get(0)!=""){
                    setCount(screenPathList.size());
                    Log.i("test_page", "screenPathList的长度是：" + screenPathList.size());
                }

                Glide.with(mContext)
                        .load(collectPage.getThumbnailPath())
                        .transition(new DrawableTransitionOptions().crossFade(500)) //淡入淡出1s
                        .into(ivThumnbail);
            }
        }
    }

    @Override
    protected void initListener() {
        rlRecordCount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_record_count:
                Intent intent = new Intent(getActivity(), RecordLibActivity.class);
                intent.putExtra("selectPageIndex",collectPage.getPageIndex());
                startActivity(intent);
                break;
        }
    }

    private void setCount(int count){
        rlRecordCount.setVisibility(View.VISIBLE);
        recordCount.setText(count+"");
    }
}
