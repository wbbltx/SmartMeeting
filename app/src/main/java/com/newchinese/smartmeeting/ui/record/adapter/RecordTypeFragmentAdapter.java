package com.newchinese.smartmeeting.ui.record.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.ui.record.fragment.RecordSubFragment;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public class RecordTypeFragmentAdapter extends FragmentPagerAdapter {

    private final Context context;
    private RecordSubFragment recordSubFragment;

    public RecordTypeFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        recordSubFragment = new RecordSubFragment();
        return recordSubFragment;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "全部";
            case 1:
                return Constant.CLASSIFY_NAME_WORK;
            case 2:
                return Constant.CLASSIFY_NAME_PROJECT;
            case 3:
                return Constant.CLASSIFY_NAME_STUDY;
            case 4:
                return Constant.CLASSIFY_NAME_EXPLORE;
            case 5:
                return Constant.CLASSIFY_NAME_REPORT;
            case 6:
                return Constant.CLASSIFY_NAME_REVIEW;
            default:
                return Constant.CLASSIFY_NAME_OTHER;
        }
    }
}
