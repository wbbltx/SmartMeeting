package com.newchinese.smartmeeting.ui.record.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.ui.meeting.activity.RecordLibActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:   收藏页详情ViewPager适配器
 * author         xulei
 * Date           2017/9/1
 */

public class CollectPageAdapter extends PagerAdapter {
    private Context context;
    private List<CollectPage> collectPageList;
    private List<View> viewList = new ArrayList<>();

    public CollectPageAdapter(Context context, List<CollectPage> collectPageList) {
        this.context = context;
        this.collectPageList = collectPageList;
        for (int i = 0; i < 4; i++) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_collect_page_detail, null);
            viewList.add(itemView);
        }
    }

    @Override
    public int getCount() {
        return collectPageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position % 4;
        final View itemView = viewList.get(index);

        ImageView ivThumnbail = (ImageView) itemView.findViewById(R.id.iv_thumnbail);
        RelativeLayout rlRecordCount = (RelativeLayout) itemView.findViewById(R.id.rl_record_count);
        TextView recordCount = (TextView) itemView.findViewById(R.id.record_count);
        rlRecordCount.setVisibility(View.GONE);

        final CollectPage collectPage = collectPageList.get(position);
        if (collectPage != null) {
            List<String> screenPathList = collectPage.getScreenPathList();
            if (screenPathList != null && screenPathList.size() != 0 && screenPathList.get(0) != "") {
                rlRecordCount.setVisibility(View.VISIBLE);
                recordCount.setText(screenPathList.size() + "");

            }
            rlRecordCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecordLibActivity.class);
                    intent.putExtra("selectPageIndex", collectPage.getPageIndex());
                    intent.putExtra("currentPage",collectPage);
                    intent.putExtra("fromFlag","2");
                    context.startActivity(intent);
                }
            });
            Glide.with(context)
                    .load(collectPage.getThumbnailPath())
                    .transition(new DrawableTransitionOptions().crossFade(500)) //淡入淡出1s
                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(ivThumnbail);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int index = position % 4;
        View itemView = viewList.get(index);
        container.removeView(itemView);
    }
}
