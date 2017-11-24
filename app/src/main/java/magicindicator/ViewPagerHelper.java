package magicindicator;

import android.support.v4.view.ViewPager;

import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.widget.NoPreloadViewPager;

/**
 * 简化和ViewPager绑定
 * Created by hackware on 2016/8/17.
 */

public class ViewPagerHelper {
    public static void bind(final MagicIndicator magicIndicator, NoPreloadViewPager viewPager) {
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                DataCacheUtil.getInstance().setName(Constant.titleList[position]);
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }
}
