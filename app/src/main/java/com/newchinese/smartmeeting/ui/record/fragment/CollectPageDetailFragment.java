package com.newchinese.smartmeeting.ui.record.fragment;


import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.model.bean.CollectPage;

import butterknife.BindView;

/**
 * Description:   收藏页详情ViewPager的Fragment
 * author         xulei
 * Date           2017/8/26 10:16
 */
public class CollectPageDetailFragment extends BaseSimpleFragment {
    private static final String COLLECT_PAGE = "collectPage";
    @BindView(R.id.iv_thumnbail)
    ImageView ivThumnbail;
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
                Glide.with(mContext)
                        .load(collectPage.getThumbnailPath())
                        .transition(new DrawableTransitionOptions().crossFade(500)) //淡入淡出1s
                        .into(ivThumnbail);
            }
        }
    }

    @Override
    protected void initListener() {

    }
}
