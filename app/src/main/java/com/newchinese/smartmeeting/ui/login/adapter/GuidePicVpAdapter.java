package com.newchinese.smartmeeting.ui.login.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;

import java.util.List;

/**
 * Description:   引导页ViewPage适配器
 * author         xulei
 * Date           2017/9/14
 */

public class GuidePicVpAdapter extends PagerAdapter {
    private List<View> viewList;

    public GuidePicVpAdapter(List<View> viewList) {
        super();
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        ImageView ivGuide = (ImageView) view.findViewById(R.id.iv_guide);
        ivGuide.setImageResource(Constant.pics[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
}
