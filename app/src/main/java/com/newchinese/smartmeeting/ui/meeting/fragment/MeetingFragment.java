package com.newchinese.smartmeeting.ui.meeting.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.ui.meeting.adapter.MeetingClassifyRecyAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description:   会议页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MeetingFragment extends BaseSimpleFragment {
    @BindView(R.id.rv_meeting_classify)
    RecyclerView rvMeetingClassify;
    private List<String> classifyNameList;

    private MeetingClassifyRecyAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meeting;
    }

    @Override
    protected void onFragViewCreated() {
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        //设置RecyclerView保持固定的大小
        rvMeetingClassify.setHasFixedSize(true);
        //设置RecyclerView布局管理
        rvMeetingClassify.setLayoutManager(new GridLayoutManager(mActivity, 2));
        //设置RecyclerView的动画
        rvMeetingClassify.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initStateAndData() {
        initClassifyData();
        adapter = new MeetingClassifyRecyAdapter(mContext, classifyNameList);
        rvMeetingClassify.setAdapter(adapter);
    }

    private void initClassifyData() {
        classifyNameList = new ArrayList<>();
        classifyNameList.add("工作例会");
        classifyNameList.add("项目会议");
        classifyNameList.add("学习培训");
        classifyNameList.add("研讨会");
        classifyNameList.add("工作汇报");
        classifyNameList.add("评审会");
        classifyNameList.add("其他");
        classifyNameList.add("+");
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(new MeetingClassifyRecyAdapter.onItemClickedListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(mActivity, "点击了:" + classifyNameList.get(position), Toast.LENGTH_SHORT).show();
                if (position == (classifyNameList.size() - 1)) { //点的加号，添加Item
                    adapter.addItem(position, "学术报告");
                } else {
                    
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(mActivity, "长点击了:" + classifyNameList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
