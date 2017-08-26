package com.newchinese.smartmeeting.ui.record.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.ui.record.fragment.CollectPageDetailFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:   收藏页详情ViewPager的Adapter
 * author         xulei
 * Date           2017/8/26
 */

public class CollectPageDetailVpAdapter extends FragmentPagerAdapter {
    private List<CollectPage> collectPageList = new ArrayList<>(); //防空

    public CollectPageDetailVpAdapter(FragmentManager fm, List<CollectPage> collectPageList) {
        super(fm);
        this.collectPageList = collectPageList;
    }

    @Override
    public Fragment getItem(int position) {
        return CollectPageDetailFragment.newInstance(collectPageList.get(position));
    }

    @Override
    public int getCount() {
        return collectPageList.size();
    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
//        ViewPager vpContainer = (ViewPager) container;
//        View view = vpContainer.getChildAt(position);
//        if (view != null) {
//            ImageView imageView = (ImageView) view.findViewById(R.id.iv_thumnbail);
//            releaseImageViewResouce(imageView);
//        }
//        Log.e("test_page", "destroyItem" + position);
//    }
//
//    /**
//     * 释放图片资源的方法
//     */
//    private void releaseImageViewResouce(ImageView imageView) {
//        if (imageView == null) return;
//        Drawable drawable = imageView.getDrawable();
//        if (drawable != null && drawable instanceof BitmapDrawable) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//                bitmap = null;
//            }
//        }
//        System.gc();
//    }
}
