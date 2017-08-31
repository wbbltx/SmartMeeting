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
import android.graphics.drawable.BitmapDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.newchinese.coolpensdk.constants.PointFromType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.Constant;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DrawingBoardActContract;
import com.newchinese.smartmeeting.listener.MulitPointTouchListener;
import com.newchinese.smartmeeting.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.listener.OnShareListener;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.listener.ShareCallBackListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.event.AddDeviceEvent;
import com.newchinese.smartmeeting.model.event.CheckBlueStateEvent;
import com.newchinese.smartmeeting.model.event.ConnectEvent;
import com.newchinese.smartmeeting.model.event.ElectricityReceivedEvent;
import com.newchinese.smartmeeting.model.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.model.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.model.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.model.event.OpenBleEvent;
import com.newchinese.smartmeeting.model.event.ScanEvent;
import com.newchinese.smartmeeting.model.event.ScanResultEvent;
import com.newchinese.smartmeeting.presenter.meeting.DrawingBoardPresenter;
import com.newchinese.smartmeeting.ui.meeting.service.RecordService;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.BluePopUpWindow;
import com.newchinese.smartmeeting.widget.CheckColorPopWin;
import com.newchinese.smartmeeting.widget.SharePopWindow;
import com.newchinese.smartmeeting.widget.TakePhotoPopWin;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

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
public class DrawingBoardActivity extends BaseActivity<DrawingBoardPresenter, BluetoothDevice> implements
        DrawingBoardActContract.View<BluetoothDevice>, View.OnTouchListener, PopWindowListener, RadioGroup.OnCheckedChangeListener, OnShareListener, PopupWindow.OnDismissListener, OnDeviceItemClickListener {
    public final static String TAG_PAGE_INDEX = "selectPageIndex";
    private static final String TAG = "DrawingBoardActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
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
    @BindView(R.id.iv_insert_image)
    ImageView ivInsertImage;
    @BindView(R.id.ll_insert_operate)
    LinearLayout llInsertOperate;
    @BindView(R.id.iv_right)
    ImageView ivShare;
    private View strokeWidthView;
    private RadioGroup rgStrkoeWidth;
    private PopupWindow pwStrkoeWidth;
    private CheckColorPopWin checkColorPopWin;
    private TakePhotoPopWin takePhotoPopWin;

    private int pageIndex;
    private boolean isMenuBtnClicked = false;
    private float mPosX, mPosY, mCurPosX, mCurPosY;
    private List<NotePage> activeNotePageList;
    private Bitmap insertBitmap;
    private DataCacheUtil dataCacheUtil;
    //    private ScanResultDialog scanResultDialog;
    private BluePopUpWindow bluePopUpWindow;
    private SharePopWindow sharePopWindow;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;

    private boolean startTimeDown;
    private Handler handler = new Handler();
    private UMImage shareImage;
    private Bitmap shareBitmap;

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
        super.onViewCreated(savedInstanceState);
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
                    mPresenter.readInsertImageFromData(pageIndex);
                    mPresenter.queryRecordCount(pageIndex);
                }
            });
        }
        //初始化调色板窗口
        checkColorPopWin = new CheckColorPopWin(this);
        //初始化图片窗口
        takePhotoPopWin = new TakePhotoPopWin(this,"DrawingBoardActivity");

        //初始化笔状态
        initPenState();

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

    private void initPenState() {
        int penState = dataCacheUtil.getPenState();
        if (penState == BluCommonUtils.PEN_CONNECTED) {
            ivPen.setImageResource(R.mipmap.pen_normal_power);
        } else if (penState == BluCommonUtils.PEN_DISCONNECTED) {
            ivPen.setImageResource(R.mipmap.pen_disconnect);
        } else if (penState == BluCommonUtils.PEN_CONNECTING) {
            ivPen.setImageResource(R.mipmap.weilianjie);
        }
    }

    @Override
    protected void initListener() {
        scanResultDialog.setOnDeviceItemClickListener(this);
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
        mPresenter.readInsertImageFromData(pageIndex);
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

    @Override
    public void setRecordTime(String time) {
        if (startTimeDown) {
            recordTime.setText(time);
        }
    }

    @Override
    public void setRecordCount(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    rlRecordCount.setVisibility(View.GONE);
                } else {
                    rlRecordCount.setVisibility(View.VISIBLE);
                    recordCount.setText(i + "");
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
    public void getDataBasePoint(NotePoint notePoint, int strokeColor, float strokeWidth) {
        //转换点对象为SDK所需格式并绘制
        drawViewMeeting.drawDataBase(notePoint, strokeColor, strokeWidth);
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
                        //mPresenter.shutDownExecutor(); //关闭上一页未读取玩的数据库线程
                        //截图
                        mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        pageIndex = activeNotePageList.get(position - 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库
                        //清空当页图片，置位编辑状态，置位所有window 
                        resetInsertImage();
                        closeEditInsertImage();
                        mPresenter.readInsertImageFromData(pageIndex);
                    }
                } else if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
                    activeNotePageList = dataCacheUtil.getActiveNotePageList();
                    hideAll();
                    //读上一页的数据库数据
                    int position = mPresenter.getCurrentPosition(activeNotePageList, pageIndex);
                    if (position >= 0 && position < (activeNotePageList.size() - 1)) {
                        //mPresenter.shutDownExecutor(); //关闭上一页未读取玩的数据库线程
                        //截图
                        mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
                        drawViewMeeting.clearCanvars(); //换页清空画布
                        pageIndex = activeNotePageList.get(position + 1).getPageIndex(); //更新页码
                        setTitleText(pageIndex); //更新标题
                        mPresenter.readDataBasePoint(pageIndex); //读数据库
                        //清空当页图片，置位编辑状态，置位所有window 
                        resetInsertImage();
                        closeEditInsertImage();
                        mPresenter.readInsertImageFromData(pageIndex);
                    }
                }
                break;
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.iv_menu_btn, R.id.iv_screen, R.id.iv_insert_pic,
            R.id.iv_stroke_color, R.id.iv_pen_stroke, R.id.iv_review, R.id.save_record, R.id.iv_right,
            R.id.rl_record_count, R.id.iv_image_delete, R.id.iv_image_cancle, R.id.iv_image_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回键
                finish();
                break;
            case R.id.iv_pen: //笔图标
                checkState();
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
                mPresenter.loadCacheMatrix();
                closeEditInsertImage();
                break;
            case R.id.iv_image_confirm: //确认插入图片
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
                Intent intent = new Intent(DrawingBoardActivity.this, PlayBackActivity.class);
                intent.putExtra(TAG_PAGE_INDEX, pageIndex);
                startActivity(intent);
                break;
            case R.id.save_record://保存结束录屏
                stopRecord();
                break;
            case R.id.rl_record_count://录屏图标
                Intent intent1 = new Intent(DrawingBoardActivity.this, RecordLibActivity.class);
                intent1.putExtra(TAG_PAGE_INDEX, pageIndex);
                startActivity(intent1);
                break;
            case R.id.iv_right://分享按钮
                sharePopWindow.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.CENTER, 0, 0);
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

    /**
     * 选择笔粗RadioGroup选择回调
     */
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
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
        hideStrokeWidth();
    }

    private void checkRecordState() {
        if (!recordService.isRunning()) {
            recordBar.setVisibility(View.VISIBLE);
            mPresenter.startRecordTimer();
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, 101);
            CustomizedToast.showShort(this, "正在录屏");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                recordService.setMediaProject(mediaProjection);
                recordService.startRecord();
                startTimeDown = true;
                dataCacheUtil.addPages(pageIndex);
            } else if (requestCode == Constant.SELECT_PIC_KITKAT || requestCode == Constant.TAKEPHOTO_SAVE_MYPATH) {
                mPresenter.operateInsertImag(this, requestCode, ivInsertImage.getImageMatrix(), data);
            }
        } else {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 设置插入图片View的Bitmap
     */
    @Override
    public void setInsertViewBitmap(Bitmap insertBitmap) {
        this.insertBitmap = insertBitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivInsertImage.setVisibility(View.VISIBLE);
                ivInsertImage.setImageBitmap(DrawingBoardActivity.this.insertBitmap);
            }
        });
    }

    /**
     * 设置插入图片View的Matrix
     */
    @Override
    public void setInsertViewMatrix(final Matrix matrix) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivInsertImage.setImageMatrix(matrix);
            }
        });
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

    private void checkState() {
        boolean bluetoothOpen = mPresenter.isBluetoothOpen();
        if (!bluetoothOpen) {
            //如果蓝牙没有打开，弹出是否打开蓝牙对话框
            bluePopUpWindow.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM, 0, 0);
        } else {
            //如果蓝牙已经打开，则去扫描
            EventBus.getDefault().post(new ScanEvent());
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
                ivMenuBtn.setVisibility(View.GONE);
                llInsertOperate.setVisibility(View.VISIBLE);
                ivInsertImage.bringToFront();
                llInsertOperate.bringToFront();
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
        takePhotoPopWin.showAtLocation(findViewById(R.id.rl_draw_base), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
        int flag = scanResultEvent.getFlag();
        if (flag == 0) {//如果是断开状态进行的搜索，应该判断
            onComplete();
        } else if (flag == 1) {//如果是连接状态进行的搜索，显示结果
            scanResultDialog.show();
        }
    }

    /**
     * 接收到添加设备事件
     *
     * @param addDeviceEvent
     */
    @Subscribe
    public void onEvent(AddDeviceEvent addDeviceEvent) {
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
        if (i <= 30) {
            ivPen.setImageResource(R.mipmap.pen_low_power);
        }
    }

    @Override
    protected void onDestroy() {
        if (pageIndex != 0) {
            mPresenter.loadCacheMatrix();
            closeEditInsertImage();
            mPresenter.savePageThumbnail(mPresenter.viewToBitmap(rlDrawViewContainer), pageIndex);
        }
        EventBus.getDefault().unregister(this);
        unbindService(connection);
        if (shareBitmap != null){
            shareBitmap.recycle();
            shareBitmap = null;
        }
        super.onDestroy();
    }

    /**
     * 接收到更新图标事件
     *
     * @param stateEvent
     */
    @Subscribe
    public void onEvent(CheckBlueStateEvent stateEvent) {
        int flag = stateEvent.getFlag();
        if (flag == 0) {
            ivPen.setImageResource(R.mipmap.weilianjie);
        } else if (flag == 1) {
            ivPen.setImageResource(R.mipmap.pen_normal_power);
        } else if (flag == -1) {
            ivPen.setImageResource(R.mipmap.pen_disconnect);
        }
    }

    @Override
    public void onBackPressed() {
        if (recordService.isRunning()) {
            showDialog1("离开当前界面将退出录制功能");
        } else {
            super.onBackPressed();
        }
    }

    private void showDialog1(final String address) {
        new AlertDialog.Builder(this)
                .setTitle(address)
                .setPositiveButton("离开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopRecord();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
                share(SHARE_MEDIA.WEIXIN);
                break;
            case "3"://微信
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
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
            EventBus.getDefault().post(new ConnectEvent(add , 0));
        }
    }
}
