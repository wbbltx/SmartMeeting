package com.newchinese.smartmeeting.ui.meeting.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.DrawingBoardActContract;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.event.AddDeviceEvent;
import com.newchinese.smartmeeting.entity.event.CheckBlueStateEvent;
import com.newchinese.smartmeeting.entity.event.ConnectEvent;
import com.newchinese.smartmeeting.entity.event.ElectricityReceivedEvent;
import com.newchinese.smartmeeting.entity.event.HisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OnHisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.entity.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.entity.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.entity.event.OpenBleEvent;
import com.newchinese.smartmeeting.entity.event.RequestPowerEvent;
import com.newchinese.smartmeeting.entity.event.ScanEvent;
import com.newchinese.smartmeeting.entity.event.ScanResultEvent;
import com.newchinese.smartmeeting.entity.listener.MulitPointTouchListener;
import com.newchinese.smartmeeting.entity.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.entity.listener.OnShareListener;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;
import com.newchinese.smartmeeting.entity.listener.ShareCallBackListener;
import com.newchinese.smartmeeting.presenter.meeting.DrawingBoardPresenter;
import com.newchinese.smartmeeting.ui.meeting.service.RecordService;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.widget.BluePopUpWindow;
import com.newchinese.smartmeeting.widget.CheckColorPopWin;
import com.newchinese.smartmeeting.widget.ScanResultDialog;
import com.newchinese.smartmeeting.widget.SharePopWindow;
import com.newchinese.smartmeeting.widget.TakePhotoPopWin;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Description:   画板Activity
 * author         xulei
 * Date           2017/8/20 21:12
 */
public class DrawingBoardActivity extends BaseActivity<DrawingBoardPresenter, BluetoothDevice> implements
        DrawingBoardActContract.View<BluetoothDevice>, View.OnTouchListener, PopWindowListener, RadioGroup.OnCheckedChangeListener, OnShareListener, PopupWindow.OnDismissListener, OnDeviceItemClickListener, DialogInterface.OnDismissListener, CheckColorPopWin.OnSelectListener {
    public final static String TAG_PAGE_INDEX = "selectPageIndex";
    private static final String TAG = "DrawingBoardActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.tv_power)
    TextView ivPower;
    @BindView(R.id.draw_view_meeting)
    DrawingBoardView drawViewMeeting;
    @BindView(R.id.iv_menu_btn)
    ImageView ivMenuBtn;
    @BindView(R.id.rl_menu_container)
    RelativeLayout rlMenuContainer;
    @BindView(R.id.rl_draw_view_container)
    RelativeLayout rlDrawViewContainer;
    @BindView(R.id.record_bar)
    RelativeLayout recordBar;
    @BindView(R.id.save_record)
    TextView saveRecord;
    @BindView(R.id.record_time)
    TextView recordTime;
    @BindView(R.id.record_count)
    TextView recordCount;
    @BindView(R.id.rl_record_count)
    RelativeLayout rlRecordCount;
    @BindView(R.id.record_cancel)
    TextView recordCancel;
    @BindView(R.id.iv_insert_image)
    ImageView ivInsertImage;
    @BindView(R.id.ll_insert_operate)
    LinearLayout llInsertOperate;
    @BindView(R.id.iv_right)
    ImageView ivShare;
    @BindView(R.id.gifImageView)
    GifImageView gifImageView;
    @BindView(R.id.dark_background)
    ImageView bar;
    private View strokeWidthView;
    private RadioGroup rgStrkoeWidth;
    private PopupWindow pwStrkoeWidth;
    private CheckColorPopWin checkColorPopWin;
    private TakePhotoPopWin takePhotoPopWin;

    private int pageIndex;
    private boolean isMenuBtnClicked = false;
    private boolean hasPic = false; //标记当页是否插入了图片
    private float mPosX, mPosY, mCurPosX, mCurPosY;
    private List<NotePage> activeNotePageList;
    private NoteRecord activeNoteRecord;
    private Bitmap insertBitmap;
    private DataCacheUtil dataCacheUtil;
    //    private ScanResultDialog scanResultDialog;
    private BluePopUpWindow bluePopUpWindow;
    private SharePopWindow sharePopWindow;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;

    private boolean startTimeDown;
    private UMImage shareImage;
    private Bitmap shareBitmap;
    private ScanResultDialog scanResultDialog;

    static final int NONE = 0;//无
    static final int DRAG = 1;//拖曳
    static final int ZOOM = 2;//缩放
    static final int FLING = 3;//翻页
    int mode = NONE;
    float oldDist = 1f;
    PointF start = new PointF();//触摸起始点

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drawing_board;
    }

    @Override
    protected DrawingBoardPresenter initPresenter() {
        return new DrawingBoardPresenter();
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        XLog.d(TAG,"onViewCreated");
        super.onViewCreated(savedInstanceState);
        scanResultDialog = new ScanResultDialog(this);
        dataCacheUtil = DataCacheUtil.getInstance();
        //初始化笔状态
        initPenState(true);
        initStrokeWidthWindow();
        ivInsertImage.setBackgroundColor(Color.TRANSPARENT);
        ivShare.setVisibility(View.VISIBLE);
        ivShare.setImageResource(R.mipmap.icon_share);
    }

    /**
     * 初始化笔粗窗口
     */
    private void initStrokeWidthWindow() {
        strokeWidthView = LayoutInflater.from(this).inflate(R.layout.layout_stroke_width, null);
        rgStrkoeWidth = (RadioGroup) strokeWidthView.findViewById(R.id.rg_pop_stroke_width);
        ((RadioButton) rgStrkoeWidth.getChildAt(0)).setChecked(true);
        pwStrkoeWidth = new PopupWindow(strokeWidthView, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pwStrkoeWidth.setAnimationStyle(R.style.popup_anim);// 淡入淡出动画
        pwStrkoeWidth.setBackgroundDrawable(new BitmapDrawable());
        pwStrkoeWidth.setOutsideTouchable(true);
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);
        //获取当前活动本
        activeNoteRecord = dataCacheUtil.getActiveNoteRecord();
        //获取当前活动本所有页
        activeNotePageList = dataCacheUtil.getActiveNotePageList();
        //加载第一笔缓存
        mPresenter.loadFirstStokeCache();
        //仅列表页点击进入逻辑
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_PAGE_INDEX)) {
            pageIndex = intent.getIntExtra("selectPageIndex", 0);
            setTitleText(pageIndex); //设置当前页数
            //延时一会儿再加载数据库，防止View还未初始化完毕
            Flowable.timer(300, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    mPresenter.readDataBasePoint(pageIndex);
                    mPresenter.readInsertImageFromData(pageIndex);
                    mPresenter.queryRecordCount(pageIndex);
                    mPresenter.updateTime(pageIndex);
                }
            });
        }
        //初始化调色板窗口
        checkColorPopWin = new CheckColorPopWin(this, this);
        //初始化图片窗口
        takePhotoPopWin = new TakePhotoPopWin(this, "DrawingBoardActivity");

//        scanResultDialog = new ScanResultDialog(this);
        bluePopUpWindow = new BluePopUpWindow(this, this);
        sharePopWindow = new SharePopWindow(this, this);
        sharePopWindow.setOnDismissListener(this);
        //请求权限 录屏初始化 绑定服务
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent recordIntent = new Intent(this, RecordService.class);
        bindService(recordIntent, connection, BIND_AUTO_CREATE);

        recordTime.setText("00:00");
    }

    private void initPenState(boolean isClicked) {
        int penState = dataCacheUtil.getPenState();
        if (penState == BluCommonUtils.PEN_CONNECTED) {
            EventBus.getDefault().post(new RequestPowerEvent());
            setConnState();
        } else if (penState == BluCommonUtils.PEN_DISCONNECTED) {
            ivPen.setImageResource(R.mipmap.pen_disconnect);
            checkState(isClicked);
        } else if (penState == BluCommonUtils.PEN_CONNECTING || mPresenter.isScanning()) {
            ivPen.setImageResource(R.mipmap.weilianjie);
        }
    }

    public void setConnState() {
        if (dataCacheUtil.isLowPower()) {
            ivPen.setImageResource(R.mipmap.pen_low_power);
        } else {
            ivPen.setImageResource(R.mipmap.pen_normal_power);
        }
    }

    @Override
    protected void initListener() {
        scanResultDialog.setOnDeviceItemClickListener(this);
        scanResultDialog.setOnDismissListener(this);
        //设置左右滑动作监听器
        drawViewMeeting.setOnTouchListener(this);
        checkColorPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dataCacheUtil.setCurrentColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
                drawViewMeeting.setPaintColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
            }
        });
        rgStrkoeWidth.setOnCheckedChangeListener(this);
        //设置插入图片的Touch事件
        ivInsertImage.setOnTouchListener(new MulitPointTouchListener());
        //编辑图片
        drawViewMeeting.setClickable(true);
        drawViewMeeting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.isCurrentPageHasInsertImage(pageIndex);
                return true;
            }
        });
        takePhotoPopWin.setOnDismissListener(this);
//        initGestureListener();
    }

    private void initGestureListener() {
        drawViewMeeting.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (DataCacheUtil.getInstance().getActiveNotePage() != null) {

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                        case MotionEvent.ACTION_DOWN://一点触摸down【也是多点触摸第一点down】
                            start.set(event.getX(), event.getY());
                            Log.d(TAG, "ACTION_DOWN " + drawViewMeeting.getScaleX() + " ++ " + drawViewMeeting.getScaleY() + " ++ " + ivInsertImage.getScaleX() + " ++ " + ivInsertImage.getScaleY());
                            if (drawViewMeeting.getScaleX() > 1 && drawViewMeeting.getScaleY() > 1 && ivInsertImage.getScaleX() > 1 && ivInsertImage.getScaleY() > 1) {
                                Log.e(TAG, "设置为拖拽模式：");
                                mode = DRAG;
                            } else {
                                Log.e(TAG, "设置为翻页模式：");
                                mode = FLING;
                            }
                            mPosX = event.getX();
                            mPosY = event.getY();
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN://多点触摸第二点down
                            oldDist = spacing(event);
                            Log.e(TAG, "olddist初始两点的距离：" + oldDist);
                            if (oldDist > 10f) {
                                mode = ZOOM;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE://挪动
                            mCurPosX = event.getX();

                            mCurPosY = event.getY();
                            if (mode == DRAG) {//一点就是拖曳
                                ivInsertImage.setClickable(false);
                                if (drawViewMeeting.getScaleX() > 1 && drawViewMeeting.getScaleY() > 1 && ivInsertImage.getScaleX() > 1 && ivInsertImage.getScaleY() > 1) {
                                    drawViewMeeting.scrollTo((int) ((mPosX - event.getX()) / 2), (int) ((mPosY - event.getY()) / 2));
                                    ivInsertImage.scrollTo((int) ((mPosX - event.getX()) / 2), (int) ((mPosY - event.getY()) / 2));
                                    Log.d(TAG, "ACTION_MOVE_mode == DRAG：" + (mPosX - event.getX()) / 2);
                                }
                            } else if (mode == ZOOM) {//多点就是缩放
                                if (event.getPointerCount() >= 2) {
                                    float newDist = spacing(event);
                                    Log.d(TAG, "ACTION_MOVE_mode == ZOOM：" + newDist);
                                    if (newDist > 10f) {
                                        float scale = newDist / oldDist;
                                        if (drawViewMeeting.getScaleX() * scale > 2) {
                                            drawViewMeeting.setScaleY(2);
                                            drawViewMeeting.setScaleX(2);
                                            ivInsertImage.setScaleX(2);
                                            ivInsertImage.setScaleY(2);
                                        } else if (drawViewMeeting.getScaleX() * scale < 1) {
                                            drawViewMeeting.setScaleY(1);
                                            drawViewMeeting.setScaleX(1);
                                            ivInsertImage.setScaleX(1);
                                            ivInsertImage.setScaleY(1);
                                            drawViewMeeting.scrollTo(0, 0);
                                            ivInsertImage.scrollTo(0, 0);
                                        } else {
                                            drawViewMeeting.setScaleY(drawViewMeeting.getScaleY() * scale);
                                            drawViewMeeting.setScaleX(drawViewMeeting.getScaleX() * scale);
                                            ivInsertImage.setScaleX(ivInsertImage.getScaleX() * scale);
                                            ivInsertImage.setScaleY(ivInsertImage.getScaleY() * scale);
                                        }
//                                        Log.e("aaaa", "newDist" + newDist);
                                    }
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getPointerCount() == 1 && mode == FLING && drawViewMeeting.getScaleX() == 1 && drawViewMeeting.getScaleY() == 1 && ivInsertImage.getScaleX() == 1 && ivInsertImage.getScaleY() == 1) {
//                                GreenDaoUtil.getInstance().getDaoSession().clear();
//                                notePageList.clear();
//                                notePageList = notePageDao.queryBuilder().where(NotePageDao.Properties.BookId.eq(dataCacheUtil.getActiveNoteBook().getId())).orderAsc(NotePageDao.Properties.PageIndex).list();
                                //左滑
                                if (mCurPosX - mPosX > 0
                                        && (Math.abs(mCurPosX - mPosX) > 25)) {
//                                    Log.e("kkk", "initGestureListener--左滑");
                                    int tempPosition = -1;
                                    drawViewMeeting.scrollTo(0, 0);
                                    ivInsertImage.scrollTo(0, 0);
                                    drawViewMeeting.setScaleY(1);
                                    drawViewMeeting.setScaleX(1);
                                    ivInsertImage.setScaleX(1);
                                    ivInsertImage.setScaleY(1);
//                                    for (int i = 0; i < notePageList.size(); i++) {
//                                        if (currentNotePage != null && currentNotePage.getPageIndex() == notePageList.get(i).getPageIndex()) {
//                                            tempPosition = i;
//                                        }
//                                    }

//                                    for (int i = 0; i < notePageList.size(); i++) {
//                                        Log.e("==========", "all" + tempPosition + "=============" + notePageList.get(i).getPageIndex() + "======" + notePageList.get(i).getId());
//
//                                    }
//                                    Log.e("kkk", "tempposition" + tempPosition);
//                                    if ((tempPosition != -1) && (tempPosition - 1 >= 0)) {
////                                        Log.e("kkk", "tempposition1=" + tempPosition);
//                                        saveBitmapFile(getcanvarBit(main_view));
//                                        currentNotePage = notePageList.get(tempPosition - 1);
//                                        if (currentNotePage != null) {
//                                            if (drawViewMeeting != null) {
//                                                drawViewMeeting.clearCanvars();
//                                                ivInsertImage.setImageDrawable(null);
//                                                ivInsertImage.refreshDrawableState();
//                                                Log.e("kkk", "tempposition2=" + tempPosition);
//                                            }
//                                            DataCacheUtil.getInstance().setActiveNotePage(currentNotePage);
//                                            DrawingboardAPI.getInstance().clearCache();
//                                            if (GreenDaoUtil.getInstance().getDaoSession().getInsertImageDao().queryBuilder().where(InsertImageDao.Properties.PageId.eq(currentNotePage.getId())).list().size() > 0) {
//                                                InsertImage insertImage = GreenDaoUtil.getInstance().getDaoSession().getInsertImageDao().queryBuilder().where(InsertImageDao.Properties.PageId.eq(currentNotePage.getId())).list().get(0);
//                                                showImage(insertImage);
//                                            } else {
//                                                ivInsertImage.setImageDrawable(null);
//                                                ivInsertImage.refreshDrawableState();
//                                            }
//                                            Log.e("kkk", "tempposition3=" + tempPosition);
//                                            readDataBaseData();
//
//                                        }
//                                    }
                                } else if (mCurPosX - mPosX < 0
                                        && (Math.abs(mCurPosX - mPosX) > 25)) {

                                    //右滑
                                    Log.e("kkk", "initGestureListener--右滑");
                                    int tempPosition = -1;
                                    drawViewMeeting.scrollTo(0, 0);
                                    ivInsertImage.scrollTo(0, 0);
                                    drawViewMeeting.setScaleY(1);
                                    drawViewMeeting.setScaleX(1);
                                    ivInsertImage.setScaleX(1);
                                    ivInsertImage.setScaleY(1);
//                                    for (int i = 0; i < notePageList.size(); i++) {
//                                        if (currentNotePage != null && currentNotePage.getPageIndex() == notePageList.get(i).getPageIndex()) {
//                                            tempPosition = i;
//                                        }
//                                    }
//                                    if ((tempPosition != -1) && (tempPosition + 1 < notePageList.size())) {
//                                        saveBitmapFile(getcanvarBit(main_view));
//                                        currentNotePage = notePageList.get(tempPosition + 1);
//                                        if (currentNotePage != null) {
//                                            if (drawViewMeeting != null) {
//                                                drawViewMeeting.clearCanvars();
//                                                ivInsertImage.setImageDrawable(null);
//                                                ivInsertImage.refreshDrawableState();
//                                                page_text.setText("第" + currentNotePage.getPageIndex() + "页");
//                                                time_text.setText(DateUtils.timestampToDate3(currentNotePage.getCreateTime()));
//                                            }
//                                            DataCacheUtil.getInstance().setActiveNotePage(currentNotePage);
//                                            DrawingboardAPI.getInstance().clearCache();
//                                            if (GreenDaoUtil.getInstance().getDaoSession().getInsertImageDao().queryBuilder().where(InsertImageDao.Properties.PageId.eq(currentNotePage.getId())).list().size() > 0) {
//                                                InsertImage insertImage = GreenDaoUtil.getInstance().getDaoSession().getInsertImageDao().queryBuilder().where(InsertImageDao.Properties.PageId.eq(currentNotePage.getId())).list().get(0);
//                                                showImage(insertImage);
//                                            } else {
//                                                ivInsertImage.setImageDrawable(null);
//                                                ivInsertImage.refreshDrawableState();
//                                            }
//                                            readDataBaseData();
//                                        }
//                                    }
                                }
                            } else {

                            }
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;

                    }
                }
//                if (ll_menu.getVisibility() == View.VISIBLE) {
//                    return false;
//                } else {
//                }
                return false;
            }

        });

    }

    private float spacing(MotionEvent event) {
        Log.d(TAG, "spacing：event.getX(0)" + "=" + event.getX(0) + ";event.getX(1)" + "=" + event.getX(1));
        Log.d(TAG, "spacing：event.getY(0)" + "=" + event.getY(0) + ";event.getY(1)" + "=" + event.getY(1));
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 选择笔色回调
     */
    @Override
    public void onSelect() {
        dataCacheUtil.setCurrentColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
        drawViewMeeting.setPaintColor(Constant.colors[dataCacheUtil.getCurrentColorPosition()]);
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
        final NotePoint notePoint = event.getNotePoint();
        int fromType = event.getFromType();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //跳页时存上一页的缩略图
                if (pageIndex != 0) {
                    mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
                }
                pageIndex = notePoint.getPageIndex();
                setTitleText(pageIndex); //设置当前页数
                if (drawViewMeeting != null) { //换页清空画布
                    drawViewMeeting.clearCanvars();
                }
            }
        });
        //读当页的数据库数据
        mPresenter.readDataBasePoint(pageIndex);
        //清空当页图片，置位编辑状态，置位所有window 
        resetInsertImage();
        closeEditInsertImage();
        hasPic = false;
        mPresenter.readInsertImageFromData(pageIndex);
        mPresenter.queryRecordCount(pageIndex);
    }

    @Subscribe
    public void onEvent(HisInfoEvent infoEvent) {
        showDialog(infoEvent.getListener(), findViewById(R.id.rl_draw_base));
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
        tvTitle.setText(getString(R.string.write_page_index, pageIndex)); //设置当前页数
//        tvTitle.setText(activeNoteRecord.getClassifyName() + " " + getString(R.string.write_page_index, pageIndex)); //设置当前页数
    }

    @Override
    public void setRecordTime(String time) {
        if (startTimeDown && recordTime != null) {
            recordTime.setText(time);
        }
    }

    @Override
    public void setRecordCount(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                closeEditInsertImage(); //关闭图片编辑模式容错
                if (rlRecordCount != null && recordCount != null) {
                    if (i == 0) {
                        rlRecordCount.setVisibility(View.GONE);
                    } else {
                        rlRecordCount.setVisibility(View.VISIBLE);
                        recordCount.setText(i + "");
                    }
                }
            }
        });
    }

    /**
     * 获取到第一笔缓存的点
     */
    @Override
    public void getFirstStrokeCachePoint(final NotePoint notePoint) {
        pageIndex = notePoint.getPageIndex();
        drawViewMeeting.drawLine(notePoint);
    }

    /**
     * 获取到数据库的点
     */
    @Override
    public void getDataBasePoint(NotePoint notePoint, int strokeColor, float strokeWidth, int cachePageIndex) {
        if (pageIndex == cachePageIndex && drawViewMeeting != null) { //当前页的点才绘制，防止当前页还未加载完点时翻到下一页，造成点数据绘制错乱
            //转换点对象为SDK所需格式并绘制
            drawViewMeeting.drawDataBase(notePoint, strokeColor, strokeWidth);
        }
    }

    /**
     * 翻页手势监听
     */
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
                if (mCurPosX - mPosX > 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
                    hideAll();
                    activeNotePageList = dataCacheUtil.getActiveNotePageList();
                    //读下一页的数据库数据
                    int position = mPresenter.getCurrentPosition(activeNotePageList, pageIndex);
                    if (position > 0 && position <= (activeNotePageList.size() - 1)) {
                        //截图
                        mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        //清空当页图片，置位编辑状态，置位所有window
                        resetInsertImage();
                        closeEditInsertImage();
                        //清空点缓存
                        DrawingboardAPI.getInstance().clearCache();
                        pageIndex = activeNotePageList.get(position - 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库点
                        mPresenter.queryRecordCount(pageIndex); //查询数据库录屏
                        hasPic = false;
                        mPresenter.readInsertImageFromData(pageIndex);
                    }
                } else if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
                    activeNotePageList = dataCacheUtil.getActiveNotePageList();
                    hideAll();
                    //读上一页的数据库数据
                    int position = mPresenter.getCurrentPosition(activeNotePageList, pageIndex);
                    if (position >= 0 && position < (activeNotePageList.size() - 1)) {
                        //截图
                        mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        //清空当页图片，置位编辑状态，置位所有window
                        resetInsertImage();
                        closeEditInsertImage();
                        //清空点缓存
                        DrawingboardAPI.getInstance().clearCache();
                        pageIndex = activeNotePageList.get(position + 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库
                        mPresenter.queryRecordCount(pageIndex); //查询数据库录屏
                        hasPic = false;
                        mPresenter.readInsertImageFromData(pageIndex);
                    }
                }
                break;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.iv_menu_btn, R.id.iv_screen, R.id.iv_insert_pic, R.id.record_cancel,
            R.id.iv_stroke_color, R.id.iv_pen_stroke, R.id.iv_review, R.id.save_record, R.id.iv_right,
            R.id.rl_record_count, R.id.iv_image_delete, R.id.iv_image_cancle, R.id.iv_image_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回键
                if (mPresenter.isScanning()) {
                    hideGif();
                    mPresenter.stopScan();
                }
                if (recordService.isRunning()) {
                    showDialog1(getString(R.string.leave_warning));
                } else {
                    finish();
                }
                mPresenter.openWrite();

//                if (mPresenter.isScanning()) {
//                    hideGif();
//                    mPresenter.stopScan();
//                } else if (recordService != null && recordService.isRunning()) {
//                    showDialog1(getString(R.string.leave_warning));
//                } else {
//                    finish();
//                }
                break;
            case R.id.iv_pen: //笔图标
                checkState(true);
                break;
            case R.id.iv_menu_btn: //菜单
                if (!isMenuBtnClicked) showMenu();
                else hideMenu();
                break;
            case R.id.iv_screen: //录视频
                hideMenu();
                checkRecordState();
                break;
            case R.id.iv_insert_pic: //图片
                hideMenu();
                showTakePhotoWindow();
                break;
            case R.id.iv_image_delete: //删除插入图片
                resetInsertImage();
                mPresenter.deleteInsertImageToData(pageIndex);
                closeEditInsertImage();
                break;
            case R.id.iv_image_cancle: //取消插入图片
                mPresenter.loadCacheMatrix(pageIndex);
                if (!hasPic) {
                    ivInsertImage.setImageBitmap(null);
                }
                closeEditInsertImage();
                break;
            case R.id.iv_image_confirm: //确认插入图片
                hasPic = true;
                MobclickAgent.onEvent(this, "insert_image");
                mPresenter.saveInsertImageToData(pageIndex, ivInsertImage.getImageMatrix());
                closeEditInsertImage();
                break;
            case R.id.iv_stroke_color: //调色
                hideMenu();
                showStrokeColor();
                break;
            case R.id.iv_pen_stroke: //笔迹粗细
                hideMenu();
                showStrokeWidth();
                break;
            case R.id.iv_review: //笔记回放
                hideMenu();
                MobclickAgent.onEvent(this, "review");
                Intent intent = new Intent(DrawingBoardActivity.this, PlayBackActivity.class);
                intent.putExtra(TAG_PAGE_INDEX, pageIndex);
                startActivityForResult(intent, 102);
                break;
            case R.id.save_record://保存结束录屏
                MobclickAgent.onEvent(this, "record");
                stopRecord();
                break;
            case R.id.record_cancel:
                cancelRecord();
                break;
            case R.id.rl_record_count://录屏图标
                MobclickAgent.onEvent(this, "play_record");
                Intent intent1 = new Intent(DrawingBoardActivity.this, RecordLibActivity.class);
                intent1.putExtra(TAG_PAGE_INDEX, pageIndex);
                intent1.putExtra("fromFlag", "1");
                startActivity(intent1);
                break;
            case R.id.iv_right://分享按钮
                sharePopWindow.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.7f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
                shareBitmap = mPresenter.viewToBitmap(rlDrawViewContainer);
                shareImage = new UMImage(this, shareBitmap);
                UMImage thumb = new UMImage(this, shareBitmap);
                shareImage.setThumb(thumb);
                break;
        }
    }

    private void stopRecord() {
        recordBar.setVisibility(View.GONE);
        startTimeDown = false;
        if (recordService.isRunning()) {
            recordService.stopRecord();
            String recordPath = recordService.getRecordPath();
            mPresenter.stopRecordTimer();
            mPresenter.saveRecord(recordPath);
            recordTime.setText("00:00");
            mPresenter.queryRecordCount(pageIndex);
        }
    }

    private void cancelRecord() {
        recordBar.setVisibility(View.GONE);
        startTimeDown = false;
        if (recordService.isRunning()) {
            recordService.stopRecord();
            mPresenter.stopRecordTimer();
            recordTime.setText("00:00");
        }
    }

    /**
     * 选择笔粗RadioGroup选择回调
     */
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        MobclickAgent.onEvent(this, "pen_width", checkedId + "");
        switch (checkedId) {
            case R.id.rb_pop_stroke_width1:
                drawViewMeeting.setStrokeWidth(0);
                dataCacheUtil.setStrokeWidth(0);
                break;
            case R.id.rb_pop_stroke_width2:
                drawViewMeeting.setStrokeWidth(1);
                dataCacheUtil.setStrokeWidth(1);
                break;
            case R.id.rb_pop_stroke_width3:
                drawViewMeeting.setStrokeWidth(2);
                dataCacheUtil.setStrokeWidth(2);
                break;
            case R.id.rb_pop_stroke_width4:
                drawViewMeeting.setStrokeWidth(3);
                dataCacheUtil.setStrokeWidth(3);
                break;
        }
//        hideStrokeWidth();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkRecordState() {
        if (!recordService.isRunning()) {
            recordBar.setVisibility(View.VISIBLE);
            mPresenter.startRecordTimer();
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, 101);
            CustomizedToast.showShort(this, getString(R.string.loading_media));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                recordService.setMediaProject(mediaProjection);
                recordService.startRecord();
                startTimeDown = true;
                dataCacheUtil.addPages(pageIndex);
            } else if (requestCode == 102) {
                //解决笔记回放页返回空白问题
                mPresenter.readDataBasePoint(pageIndex); //读数据库
            } else if (requestCode == Constant.SELECT_PIC_KITKAT || requestCode == Constant.TAKEPHOTO_SAVE_MYPATH) {
                mPresenter.operateInsertImag(this, requestCode, ivInsertImage.getImageMatrix(), data, pageIndex);
            }
        } else {
            if (requestCode == 101) {
                recordBar.setVisibility(View.GONE);
            } else if (requestCode == 102) {
                //解决笔记回放页返回空白问题
                mPresenter.readDataBasePoint(pageIndex); //读数据库
            }
        }
    }

    /**
     * 设置插入图片View的Bitmap
     */
    @Override
    public void setInsertViewBitmap(Bitmap insertBitmap, int cachePageIndex) {
        if (pageIndex == cachePageIndex) {
            this.insertBitmap = insertBitmap;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ivInsertImage != null) {
                        ivInsertImage.setVisibility(View.VISIBLE);
                        ivInsertImage.setImageBitmap(DrawingBoardActivity.this.insertBitmap);
                    }
                }
            });
        }
    }

    /**
     * 设置插入图片View的Matrix
     */
    @Override
    public void setInsertViewMatrix(final Matrix matrix, int cachePageIndex) {
        if (pageIndex == cachePageIndex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ivInsertImage != null) {
                        Log.e("test_pic", "setInsertViewMatrix:" + matrix.toString());
                        ivInsertImage.setImageMatrix(matrix);
                    }
                }
            });
        }
    }

    @Override
    public void hasPic(boolean isHasPic) {
        hasPic = isHasPic;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private void checkState(boolean isClicked) {
        if (isClicked) {
            boolean bluetoothOpen = mPresenter.isBluetoothOpen();
            if (!bluetoothOpen) {
                //如果蓝牙没有打开，弹出是否打开蓝牙对话框
                CustomizedToast.showShort(this, "请开启蓝牙！");
            } else {
                //如果蓝牙已经打开，则去扫描
                EventBus.getDefault().post(new ScanEvent());
                showGif();
                ivPen.setImageResource(R.mipmap.weilianjie);
            }
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
     * 打开插入图片编辑模式
     */
    @Override
    public void openEditInsertImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ivMenuBtn != null && llInsertOperate != null && ivInsertImage != null) {
                    ivMenuBtn.setVisibility(View.GONE);
                    llInsertOperate.setVisibility(View.VISIBLE);
                    ivInsertImage.bringToFront();
                    llInsertOperate.bringToFront();
                }
            }
        });
    }

    /**
     * 关闭插入图片编辑模式
     */
    @Override
    public void closeEditInsertImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivMenuBtn.setVisibility(View.VISIBLE);
                llInsertOperate.setVisibility(View.GONE);
                drawViewMeeting.bringToFront();
            }
        });
    }

    /**
     * 显示插入普片PopupWindow
     */

    private void showTakePhotoWindow() {
        turnDark();
        takePhotoPopWin.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void turnDark() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    /**
     * 隐藏插入图片PopupWindow
     */
    @Override
    public void hideTakePhotoWindow() {
        takePhotoPopWin.dismiss();
    }

    /**
     * 置位插入图片
     */
    private void resetInsertImage() {
        ivInsertImage.setImageDrawable(null);
        ivInsertImage.refreshDrawableState();
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
     * 显示笔粗
     */
    private void showStrokeWidth() {
        pwStrkoeWidth.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 隐藏笔粗
     */
    private void hideStrokeWidth() {
        pwStrkoeWidth.dismiss();
    }

    /**
     * 隐藏所有菜单窗口
     */
    private void hideAll() {
        hideMenu();
        hideTakePhotoWindow();
        hideStrokeColor();
        hideStrokeWidth();
    }

    /**
     * 是否打开蓝牙
     */
    @Override
    public void onConfirm(int i) {
        //该类不操作蓝牙，发送消息到第二个activity 使其打开蓝牙
        XLog.d(TAG, TAG + " onConfirm");
        EventBus.getDefault().post(new OpenBleEvent());
    }

    @Override
    public void onCancel(int i) {
        XLog.d(TAG, TAG + " onCancel");
    }

    /**
     * 接收到扫描结束时间
     *
     * @param scanResultEvent
     */
    @Subscribe
    public void onEvent(ScanResultEvent scanResultEvent) {
        XLog.d(TAG, TAG + " onScanComplete ");
        int flag = scanResultEvent.getFlag();
        if (flag == 0) {
            hideGif();
        } else if (flag == 1) {//如果是连接状态进行的搜索，显示结果
            scanResultDialog
                    .setContent(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_NAME), "1")
                    .show();
        }
    }

    /**
     * 接收到添加设备事件
     *
     * @param addDeviceEvent
     */
    @Subscribe
    public void onEvent(AddDeviceEvent addDeviceEvent) {
        XLog.d(TAG, TAG + " showResult ");
        scanResultDialog.addDevice(addDeviceEvent.getBluetoothDevice());
    }

    /**
     * 接收到电量事件
     *
     * @param receivedEvent
     */
    @Subscribe
    public void onEvent(ElectricityReceivedEvent receivedEvent) {
        String value = receivedEvent.getValue();
        int i = Integer.parseInt(value);
        ivPower.setText(i + "");
        XLog.d(TAG, TAG + " onElecReceived " + i);
        boolean lowPower = receivedEvent.isLowPower();
        if (lowPower) {
            ivPen.setImageResource(R.mipmap.pen_low_power);
        } else {
            ivPen.setImageResource(R.mipmap.pen_normal_power);
        }
    }

    @Override
    protected void onDestroy() {
        if (pageIndex != 0) {
            mPresenter.loadCacheMatrix(pageIndex);
            closeEditInsertImage();
            mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
        }
        EventBus.getDefault().unregister(this);
        unbindService(connection);
        if (shareBitmap != null) {
            shareBitmap.recycle();
            shareBitmap = null;
        }
        mPresenter.shutDownExecutor();
        super.onDestroy();
    }

    /**
     * 接收到更新图标事件
     *
     * @param stateEvent
     */
    @Subscribe
    public void onEvent(CheckBlueStateEvent stateEvent) {
        XLog.d(TAG, TAG + " onEvent CheckBlueStateEvent");
        int flag = stateEvent.getFlag();
        if (flag == 0) {
            showGif();
            ivPen.setImageResource(R.mipmap.weilianjie);
        } else if (flag == 1) {
            hideGif();
            ivPower.setText("");
            setConnState();
        } else if (flag == -1) {
            hideGif();
            ivPen.setImageResource(R.mipmap.pen_disconnect);
        }
    }

    @Subscribe
    public void onEvent(OnHisInfoEvent infoEvent) {
        if (infoEvent.getFlag().equals("deletingOrreading")) {
            showGif();
        } else {
            hideGif();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.isScanning()) {
            mPresenter.stopScan();
        }
        mPresenter.openWrite();
        if (recordService.isRunning()) {
            showDialog1(getString(R.string.leave_warning));
        } else {
            super.onBackPressed();
        }
    }

    private void showDialog1(final String address) {
        new AlertDialog.Builder(this)
                .setTitle(address)
                .setPositiveButton(getString(R.string.leave), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecord();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    @Override
    public void onShare(String i) {
        switch (i) {
            case "0"://qq空间分享
                share(SHARE_MEDIA.QZONE);
                break;
            case "1"://qq分享
                share(SHARE_MEDIA.QQ);
                break;
            case "2"://朋友圈
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case "3"://微信
                share(SHARE_MEDIA.WEIXIN);
                break;
            case "4"://微博
                share(SHARE_MEDIA.SINA);
                break;
        }
    }

    private void share(SHARE_MEDIA shareMedia) {
        new ShareAction(DrawingBoardActivity.this)
                .setPlatform(shareMedia)
//                .withText("content")
// .withTargetUrl(linkHref)
                .withMedia(shareImage)
                .setCallback(new ShareCallBackListener(this))
                .share();
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDeviceClick(BluetoothDevice add) {
        if (!add.getAddress().equals(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS)) || !mPresenter.isConnected()) {
            showGif();
            EventBus.getDefault().post(new ConnectEvent(add, 0));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.queryRecordCount(pageIndex);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
//        progressBar.setVisibility(View.GONE);
        hideGif();
        initPenState(false);
    }

    private void showGif() {
        bar.setVisibility(View.VISIBLE);
        gifImageView.setVisibility(View.VISIBLE);
    }

    private void hideGif() {
        bar.setVisibility(View.GONE);
        gifImageView.setVisibility(View.GONE);
    }
}
