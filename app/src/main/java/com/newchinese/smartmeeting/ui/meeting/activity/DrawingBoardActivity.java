package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DrawingBoardContract;
import com.newchinese.smartmeeting.model.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.model.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.model.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.presenter.meeting.DrawingBoardPresenter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.PointCacheUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Description:   画板Activity
 * author         xulei
 * Date           2017/8/20 21:12
 */
public class DrawingBoardActivity extends BaseActivity<DrawingBoardPresenter, String> implements DrawingBoardContract.View<String> {
    public final static String TAG_PAGE_INDEX = "selectPageIndex";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.draw_view_meeting)
    DrawingBoardView drawViewMeeting;
    private int pageIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drawing_board;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);
        mPresenter.loadFirstStokeCache(); //加载第一笔缓存
        //仅列表点击进入逻辑
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_PAGE_INDEX)) {
            pageIndex = intent.getIntExtra("selectPageIndex", 0);
            setTitleText(pageIndex); //设置当前页数
            Flowable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    mPresenter.readDataBasePoint(pageIndex);
                }
            });
        }
    }

    @Override
    protected void initListener() {

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
        if (drawViewMeeting != null)
            drawViewMeeting.drawLine(notePoint);
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
                setTitleText(pageIndex); //设置当前页数
                if (drawViewMeeting != null) { //换页清空画布
                    drawViewMeeting.clearCanvars();
                }
            }
        });
        //读当页的数据库数据
        mPresenter.readDataBasePoint(pageIndex);
    }

    /**
     * 获取到第一笔缓存的点
     */
    @Override
    public void getFirstStrokeCachePoint(final com.newchinese.coolpensdk.entity.NotePoint notePoint) {
        drawViewMeeting.drawLine(notePoint);
    }

    /**
     * 清屏
     */
    @Override
    public void clearCanvars() {
        drawViewMeeting.clearCanvars();
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitleText(int pageIndex) {
        tvTitle.setText("书写，第" + pageIndex + "页"); //设置当前页数
    }

    /**
     * 获取到数据库的点
     */
    @Override
    public void getDataBasePoint(com.newchinese.coolpensdk.entity.NotePoint notePoint, int strokeColor, float strokeWidth) {
        //转换点对象为SDK所需格式并绘制
        drawViewMeeting.drawDataBase(notePoint, strokeColor, strokeWidth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置第一笔缓存标志，初始笔色，初始线宽
        PointCacheUtil.getInstance().setCanAddFlag(true);
        DataCacheUtil.getInstance().setCurrentColor(Constant.colors[0]);
        DataCacheUtil.getInstance().setStrokeWidth(0);
        //关闭线程池
        mPresenter.shutDownExecutor();
        //清除SDK数据缓存
        DrawingboardAPI.getInstance().clearCache();
    }
}
