package com.newchinese.smartmeeting.ui.meeting.fragment;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.manager.NoteRecordManager;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.ui.meeting.activity.DraftBoxActivity;
import com.newchinese.smartmeeting.ui.meeting.adapter.MeetingClassifyRecyAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

/**
 * Description:   会议页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MeetingFragment extends BaseSimpleFragment {
    @BindView(R.id.rv_meeting_classify)
    RecyclerView rvMeetingClassify;
    @BindView(R.id.iv_study)
    ImageView ivStudy;
    private List<String> classifyNameList;
    private MeetingClassifyRecyAdapter adapter;
    private ExecutorService singleThreadExecutor; //单核心线程线程池
    private static final String TAG = "MeetingFragment";

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
        rvMeetingClassify.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //设置RecyclerView的动画
        rvMeetingClassify.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initStateAndData() {
        initClassifyData();
        adapter = new MeetingClassifyRecyAdapter(mContext, classifyNameList);
        rvMeetingClassify.setAdapter(adapter);
        //初始化线程池
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private void initClassifyData() {
        classifyNameList = new ArrayList<>();
        classifyNameList.add(Constant.CLASSIFY_NAME_WORK);
        classifyNameList.add(Constant.CLASSIFY_NAME_PROJECT);
        classifyNameList.add(Constant.CLASSIFY_NAME_EXPLORE);
        classifyNameList.add(Constant.CLASSIFY_NAME_REPORT);
        classifyNameList.add(Constant.CLASSIFY_NAME_OTHER);
        classifyNameList.add(Constant.CLASSIFY_NAME_REVIEW);
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(new OnItemClickedListener() {
            @Override
            public void onClick(View view, int position) {
                MobclickAgent.onEvent(getActivity(), "classify_name", classifyNameList.get(position));
                Intent intent = new Intent(mActivity, DraftBoxActivity.class);
                intent.putExtra("classify_name", classifyNameList.get(position));
                startActivity(intent);
                setActiveNoteRecord(classifyNameList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        });
        ivStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "classify_name", Constant.CLASSIFY_NAME_STUDY);
                Intent intent = new Intent(mActivity, DraftBoxActivity.class);
                intent.putExtra("classify_name", Constant.CLASSIFY_NAME_STUDY);
                startActivity(intent);
                setActiveNoteRecord(Constant.CLASSIFY_NAME_STUDY);
            }
        });
    }

    /**
     * 存储选择的分类名称，并设置当前活动记录表
     */
    public void setActiveNoteRecord(final String classifyName) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DataCacheUtil dataCacheUtil = DataCacheUtil.getInstance();
                dataCacheUtil.setChosenClassifyName(classifyName); //缓存选择的分类名称
                NoteRecord activeNoteRecord = NoteRecordManager.getInstance().getNoteRecord(
                        GreenDaoUtil.getInstance().getNoteRecordDao(), classifyName);
                dataCacheUtil.setActiveNoteRecord(activeNoteRecord); //缓存当前活动记录表
            }
        };
        singleThreadExecutor.execute(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        singleThreadExecutor.shutdownNow();
    }
}
