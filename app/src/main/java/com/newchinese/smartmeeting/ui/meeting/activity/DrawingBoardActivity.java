package com.newchinese.smartmeeting.ui.meeting.activity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DrawingBoardContract;
import com.newchinese.smartmeeting.model.bean.NotePoint;
import com.newchinese.smartmeeting.model.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.model.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.model.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.presenter.meeting.DrawingBoardPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   画板Activity
 * author         xulei
 * Date           2017/8/20 21:12
 */
public class DrawingBoardActivity extends BaseActivity<DrawingBoardPresenter, String> implements DrawingBoardContract.View<String> {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.draw_board_view_meeting)
    DrawingBoardView drawBoardViewMeeting;
    private int pageIndex;
    private boolean isFirstLoad = true; //初次加载第一笔缓存标记
    private Handler delayHandler = new Handler(); //用于延时的Handler

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drawing_board;
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);
        mPresenter.loadFirstStokeCache(); //加载第一笔缓存
        tvTitle.setText("书写");
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void showResult(String s) {

    }

    @Override
    protected DrawingBoardPresenter initPresenter() {
        return new DrawingBoardPresenter();
    }

    @OnClick({R.id.iv_back, R.id.iv_pen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pen:

                break;
        }
    }

    /**
     * 收到返回的NotePoint点对象event
     */
    @Subscribe
    public void onEvent(OnPointCatchedEvent event) {
        com.newchinese.coolpensdk.entity.NotePoint notePoint = event.getNotePoint();
        int fromType = event.getFromType();
        switch (fromType) {
            case PointFromType.POINT_FROM_DRAW://实时绘制来的点
                break;
            case PointFromType.POINT_FROM_SAVE://存储来的点
                break;
        }
        drawBoardViewMeeting.drawLine(notePoint);
    }

    /**
     * 收到返回NoteStroke线对象event
     */
    @Subscribe
    public void onEvent(OnStrokeCatchedEvent event) {
        com.newchinese.coolpensdk.entity.NoteStroke noteStroke = event.getNoteStroke();
        int fromType = event.getFromType();
        switch (fromType) {
            case PointFromType.POINT_FROM_DRAW://实时绘制来的点
                break;
            case PointFromType.POINT_FROM_SAVE://存储来的点
                break;
        }
    }

    /**
     * 收到换页的event
     */
    @Subscribe
    public void onEvent(OnPageIndexChangedEvent event) {
        com.newchinese.coolpensdk.entity.NotePoint notePoint = event.getNotePoint();
        int fromType = event.getFromType();
        pageIndex = notePoint.getPageIndex();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText("书写，第" + pageIndex + "页"); //设置当前页数
                if (drawBoardViewMeeting != null) { //换页清空画布
                    drawBoardViewMeeting.clearCanvars();
                }
            }
        });
        //读当页的数据库数据
        mPresenter.readDataBasePoint();
    }

    /**
     * 获取到第一笔缓存的点
     */
    @Override
    public void getFirstStrokeCachePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        if (notePoint.getPointType() == PointType.TYPE_DOWN && isFirstLoad) {
            pageIndex = notePoint.getPageIndex();
            tvTitle.setText("书写，第" + pageIndex + "页"); //设置当前页数
            //延时加载数据库，否则view未初始化完毕会丢点
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPresenter.readDataBasePoint();
                }
            }, 500);
            isFirstLoad = false;
        }
        drawBoardViewMeeting.drawLine(notePoint);
    }

    /**
     * 获取到数据库的点
     */
    @Override
    public void getDataBasePoint(NotePoint notePoint, int strokeColor, float strokeWidth) {
        //转换点对象为SDK所需格式并绘制
        drawBoardViewMeeting.drawDataBase(new com.newchinese.coolpensdk.entity.NotePoint(notePoint.getPX(),
                notePoint.getPY(), notePoint.getTestTime(), notePoint.getFirstPress(), notePoint.getPress(),
                notePoint.getPageIndex(), notePoint.getPointType()), strokeColor, strokeWidth);
    }
}
