package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DrawingBoardContract;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.model.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.model.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.presenter.meeting.DrawingBoardPresenter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.widget.CheckColorPopWin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
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
public class DrawingBoardActivity extends BaseActivity<DrawingBoardPresenter, String> implements
        DrawingBoardContract.View<String>, View.OnTouchListener {
    public final static String TAG_PAGE_INDEX = "selectPageIndex";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    TextView ivPen;
    @BindView(R.id.draw_view_meeting)
    DrawingBoardView drawViewMeeting;
    @BindView(R.id.iv_menu_btn)
    ImageView ivMenuBtn;
    @BindView(R.id.rl_menu_container)
    RelativeLayout rlMenuContainer;
    private CheckColorPopWin checkColorPopWin;

    private int pageIndex;
    private boolean isMenuBtnClicked = false;
    private float mPosX, mPosY, mCurPosX, mCurPosY;
    private List<NotePage> activeNotePageList;
    private DataCacheUtil dataCacheUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drawing_board;
    }

    @Override
    protected DrawingBoardPresenter initPresenter() {
        return new DrawingBoardPresenter();
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);
        //获取当前活动本所有页
        dataCacheUtil = DataCacheUtil.getInstance();
        activeNotePageList = dataCacheUtil.getActiveNotePageList();
        //加载第一笔缓存
        mPresenter.loadFirstStokeCache();
        //仅列表页点击进入逻辑
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_PAGE_INDEX)) {
            pageIndex = intent.getIntExtra("selectPageIndex", 0);
            setTitleText(pageIndex); //设置当前页数
            //延时一会儿再加载数据库，防止View还未初始化完毕
            Flowable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    mPresenter.readDataBasePoint(pageIndex);
                }
            });
        }
        //初始化调色板窗口
        checkColorPopWin = new CheckColorPopWin(this);
    }

    @Override
    protected void initListener() {
        //设置左右滑动作监听器
        drawViewMeeting.setOnTouchListener(this);
        checkColorPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dataCacheUtil.setCurrentColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
                drawViewMeeting.setPaintColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
            }
        });
    }

    /**
     * 收到返回的NotePoint点对象event
     */
    @Subscribe
    public void onEvent(OnPointCatchedEvent event) {
        NotePoint notePoint = event.getNotePoint();
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
        NoteStroke noteStroke = event.getNoteStroke();
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
        NotePoint notePoint = event.getNotePoint();
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
     * 获取到第一笔缓存的点
     */
    @Override
    public void getFirstStrokeCachePoint(final NotePoint notePoint) {
        drawViewMeeting.drawLine(notePoint);
    }

    /**
     * 获取到数据库的点
     */
    @Override
    public void getDataBasePoint(NotePoint notePoint, int strokeColor, float strokeWidth) {
        //转换点对象为SDK所需格式并绘制
        drawViewMeeting.drawDataBase(notePoint, strokeColor, strokeWidth);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosX = event.getX();
                mPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosX = event.getX();
                mCurPosY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (mCurPosX - mPosX > 0 && (Math.abs(mCurPosX - mPosX) > 200)) {
                    hideAll();
                    activeNotePageList = dataCacheUtil.getActiveNotePageList();
                    //读下一页的数据库数据
                    int position = mPresenter.getCurrentPosition(activeNotePageList, pageIndex);
                    if (position > 0 && position <= (activeNotePageList.size() - 1)) {
//                                mPresenter.shutDownExecutor(); //关闭上一页未读取玩的数据库线程
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        pageIndex = activeNotePageList.get(position - 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库
                    }
                } else if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 200)) {
                    activeNotePageList = dataCacheUtil.getActiveNotePageList();
                    hideAll();
                    //读上一页的数据库数据
                    int position = mPresenter.getCurrentPosition(activeNotePageList, pageIndex);
                    if (position >= 0 && position < (activeNotePageList.size() - 1)) {
//                                mPresenter.shutDownExecutor(); //关闭上一页未读取玩的数据库线程
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        pageIndex = activeNotePageList.get(position + 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库
                    }
                }
                break;
        }
        return true;
    }

    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.iv_menu_btn, R.id.iv_screen, R.id.iv_insert_pic,
            R.id.iv_stroke_color, R.id.iv_pen_stroke, R.id.iv_review})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回键
                finish();
                break;
            case R.id.iv_pen: //笔图标
                break;
            case R.id.iv_menu_btn: //菜单
                if (!isMenuBtnClicked) showMenu();
                else hideMenu();
                break;
            case R.id.iv_screen: //录视频
                hideMenu();
                break;
            case R.id.iv_insert_pic: //图片
                hideMenu();
                break;
            case R.id.iv_stroke_color: //调色
                hideMenu();
                showStrokeColor();
                break;
            case R.id.iv_pen_stroke: //笔迹粗细
                hideMenu();
                break;
            case R.id.iv_review: //笔记回放
                hideMenu();
                break;
        }
    }

    /**
     * 显示功能菜单
     */
    private void showMenu() {
        ivMenuBtn.setImageResource(R.mipmap.draw_menu_open);
        rlMenuContainer.setVisibility(View.VISIBLE);
        isMenuBtnClicked = !isMenuBtnClicked;
    }

    /**
     * 隐藏功能菜单
     */
    private void hideMenu() {
        ivMenuBtn.setImageResource(R.mipmap.draw_menu_close);
        rlMenuContainer.setVisibility(View.GONE);
        isMenuBtnClicked = !isMenuBtnClicked;
    }

    /**
     * 显示调色板
     */
    private void showStrokeColor() {
        checkColorPopWin.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 隐藏调色板
     */
    private void hideStrokeColor() {
        checkColorPopWin.dismiss();
    }

    /**
     * 隐藏所有菜单窗口
     */
    private void hideAll() {
        hideMenu();
        hideStrokeColor();
    }
}
