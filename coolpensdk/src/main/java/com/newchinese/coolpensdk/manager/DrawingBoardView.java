package com.newchinese.coolpensdk.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:   实时绘制画板
 * author         xulei
 * Date           2017/4/18
 */

public class DrawingBoardView extends View {
    private int currentColor = Color.BLACK;//默认颜色
    private int height;
    private int width;
    private int resourceCache = 0;
    private float transX;
    private float transY;
    private float canvasScale;
    private float lastX, dataLastX;
    private float lastY, dataLastY;
    private float[] targetSize = new float[2];
    private float offsetX = 0.0f, offsetY = 0.0f;
    private boolean isDown, isDownData; //是否是down点：实时绘制，数据库逻辑
    private boolean isDrawBack = false; //是否在初始化完毕监听内绘制背景的标记
    private boolean isFirstDraw = true; //初始化完毕监听会多次回调，此标记用来只画一次背景图
    private boolean isDrawViewInited = false; //是否初始化完毕的标记
    private boolean isFirstInitBitmap = true; //是否第一次初始化画板Bitmap
    private Matrix sMatrix;
    private Matrix mMatrix;//记录当前页面的位移，缩放[Matrix是矩阵对象]
    private OLPath mOLPath;//记录当前线段的路径
    private OLPath mOLPath0;//记录同一1逻辑里的第二条线段
    private OLPath tempOlPath;
    private Bitmap backBitmap; //背景图片Bitmap
    private Bitmap bgBitmap; //数据库画板Bitmap
    private Bitmap foreGroundBitmap; //实时绘制画板Bitmap
    private Canvas bgCanvas; //数据库画板
    private Canvas foreGroundCanvas; //实时绘制画板
    private PaintFlagsDrawFilter pfdf;
    private Rect ensureRect = new Rect();//坐标对象【x，y】
    private FirstStrokeCache firstStrokeCache;
    private HandlerThread dataBaseThread;
    private Handler dataBaseHandler;

    private boolean isDown() {
        return isDown;
    }

    /**
     * 记录是否是一条线的第一个点
     */
    private void setIsDown(boolean isDown) {
        this.isDown = isDown;
        if (isDown) {
            ensureRect.setEmpty();//系统方法，用于初始化点坐标为【0，0】
        }
    }

    private boolean isDownData() {
        return isDownData;
    }

    /**
     * 记录是否是数据库一条线的第一个点
     */
    private void setIsDownData(boolean isDownData) {
        this.isDownData = isDownData;
        if (isDownData) {
            ensureRect.setEmpty();//系统方法，用于初始化点坐标为【0，0】
        }
    }

    /**
     * 设置实时绘制画笔颜色
     */
    public void setPaintColor(int color) {//设置笔的颜色和粗细
        this.currentColor = color;
        if (mOLPath0 != null) {
            mOLPath0.getmPaint().setColor(currentColor);
        }
        if (isDown) {
            ensureRect.setEmpty();
        }
    }

    /**
     * 设置数据库绘制画笔颜色
     */
    public void setDataPaintColor(int color) {
        if (tempOlPath != null) {
            tempOlPath.getmPaint().setColor(color);
        }
        if (isDownData) {
            ensureRect.setEmpty();
        }
    }

    /**
     * 设置横纵坐标偏移量
     *
     * @param offsetX 横坐标
     * @param offsetY 纵坐标
     */
    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        LogicController.getInstance().setOffsetX(offsetX);
        LogicController.getInstance().setOffsetY(offsetY);
    }

    /**
     * 清理画布，也可以说是初始化画板
     */
    public void clearCanvars() {
        if (bgCanvas != null) {
            bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//用透明色填充整个画布Canvas，bg表示背后
        }
        if (foreGroundCanvas != null) {
            foreGroundCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//用透明色填充前方画布，fore代表前
        }
        postInvalidate();//从子线程发送更新ui的请求，默认调用者是本类对象。也就是CanverView执行此方法，要求主线程过来更新CanvarView自己.写在ondraw里会导致死循环
    }

    /**
     * 设置初始化完毕标志
     */
    public void setIsDrawViewInited(boolean isDrawViewInited) {
        this.isDrawViewInited = isDrawViewInited;
        firstStrokeCache.setCanAddFlag(false);
    }

    public DrawingBoardView(Context context) {
        this(context, null);
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化
    private void init(Context context) {
        pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mMatrix = new Matrix();//实例化一个空的矩阵对象2
        sMatrix = new Matrix();//实例化一个空的矩阵对象2
        mOLPath = new OLPath();////画自定义控件本身所使用的轨迹。所以这只笔不用色值，不用宽度
        mOLPath0 = new OLPath(0, currentColor);//mOLPath前面被用过了，所以换一个对象以区分，暴露的颜色方法改变的是这个值，从蓝牙获得第一个点以该压力的转换值为笔粗
        tempOlPath = new OLPath(0, currentColor);

        firstStrokeCache = FirstStrokeCache.getInstance();
        initListener();

        dataBaseThread = new HandlerThread("dataBaseThread");
        dataBaseThread.start();
        dataBaseHandler = new Handler(dataBaseThread.getLooper());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mOLPath0.setAntiAliasMeoth(true);
        tempOlPath.setAntiAliasMeoth(true);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (bgCanvas != null) {
            return;
        }
        //测量调用者的宽高width、height
        if (LogicController.getInstance().getWidth() == -1 || LogicController.getInstance().getHeight() == -1) {
            height = getMeasuredHeight();//测量xml布局设定的最大高度
            width = getMeasuredWidth();
            LogicController.getInstance().setWidth(width);
            LogicController.getInstance().setHeight(height);
        } else {
            height = LogicController.getInstance().getHeight();
            width = LogicController.getInstance().getWidth();
        }

        //targetSize是个2位浮点数组，调用了自定义适配器管理器，传入父控件的宽高和每页纸的宽高。
        //targetSize[0]和[1]是分别是canvarView的宽度与高度。
        targetSize = AdaptManager.getTargetSize((float) width, (float) height, Constant.ACTIVE_PAGE_X, Constant.ACTIVE_PAGE_Y);
        canvasScale = targetSize[0] / Constant.ACTIVE_PAGE_X;//canvarView控件最大时是纸张的百分之几 控件与纸张的匹配比例

        transX = (width - targetSize[0]) / 2;   //x方向上需要在父控件居中时，canverview的margin值【A4纸下，width 和targetSize[0]是一样大的】
        transY = (height - targetSize[1]) / 2;  //y方向上需要在父控件居中时，canverview的margin值

        mMatrix.setScale(canvasScale / getScale(mMatrix), canvasScale / getScale(mMatrix));//设置当前的缩放比例？？但canvasScale / getScale()==1，mMatrix[000]=9.8732
        sMatrix.setScale(canvasScale / getScale(mMatrix), canvasScale / getScale(mMatrix));//设置当前的缩放比例？？但canvasScale / getScale()==1，mMatrix[000]=9.8732

        mOLPath.getmPath().transform(mMatrix);//这个mOLPath是成员变量，在init里被初始化。路径mOpath得到空path。path根据矩阵规则跑画点。重点。这应该是描边。

        if (isFirstInitBitmap) {
            initBitmap();
            isFirstInitBitmap = false;
        }

        //数据库画板
        if (bgCanvas == null) {
            bgCanvas = new Canvas(bgBitmap);//新建在前台绘制从数据库中加载的Canvas
        }
        bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//0重绘过程中不允许滑动

        //实时绘制画板
        if (foreGroundCanvas == null) {
            foreGroundCanvas = new Canvas(foreGroundBitmap);//新建在前台绘制从数据库中加载的Canvas
        }
        foreGroundCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//0重绘过程中不允许滑动

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins((int) transX, (int) transY, (int) transX, (int) transY);//4个参数按顺序分别是左上右下
        setLayoutParams(layoutParams);//此法只适合canvar外层就是最大父控件的时候
    }

    private void initBitmap() {
        //提供一个空的bitmap准备填充新加入数据库的数据。
        //数据库画板
        if (LogicController.getInstance().getBgBitmap() == null) {
            bgBitmap = Bitmap.createBitmap((int) targetSize[0], (int) targetSize[1], Bitmap.Config.ARGB_8888);
            LogicController.getInstance().setBgBitmap(bgBitmap);
        } else {
            bgBitmap = LogicController.getInstance().getBgBitmap();
        }
        //实时绘制画板
        if (LogicController.getInstance().getForeGroundBitmap() == null) {
            foreGroundBitmap = Bitmap.createBitmap((int) targetSize[0], (int) targetSize[1], Bitmap.Config.ARGB_8888);
            LogicController.getInstance().setForeGroundBitmap(foreGroundBitmap);
        } else {
            foreGroundBitmap = LogicController.getInstance().getForeGroundBitmap();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mOLPath0.setAntiAliasMeoth(true);
        tempOlPath.setAntiAliasMeoth(true);
        canvas.setDrawFilter(pfdf);
        super.onDraw(canvas);
        if (backBitmap != null) //背景图片
            canvas.drawBitmap(backBitmap, sMatrix, null);
        if (getScale(sMatrix) > 1) {
            canvas.drawBitmap(bgBitmap, sMatrix, null);
            canvas.drawBitmap(foreGroundBitmap, sMatrix, null);
            return;
        } else {
            sMatrix.setScale(1, 1);
            canvas.drawBitmap(bgBitmap, 0, 0, null);
            canvas.drawBitmap(foreGroundBitmap, 0, 0, null);
        }
    }


    /**
     * 实时绘制逻辑
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void drawLine(NotePoint point) {
        switch (point.getPointType()) {//down点与up点设置点初始化，move点去绘制
            case 1:
            case 3:
                setIsDown(true);//初始化起点00坐标
                return;
            case 2:
                if (!isDrawViewInited) {
                    return;
                }
                float x = point.getPX();
                float y = point.getPY();
                float press = point.getPress();//下面的逻辑很重要问题,我在使用笔时会画板丢页，就是因为Constant.AXIS_NUM_X值不对
                int notePageIndex = point.getPageIndex();
                float x1 = x - ((notePageIndex - 1) * Constant.ACTIVE_PAGE_X);
                x = x - ((notePageIndex - 1) % Constant.AXIS_NUM_X) * Constant.ACTIVE_PAGE_X;//以每页纸的左上角当做坐标系原点时，该点在每页纸的坐标系上的横坐标
                y = y - ((notePageIndex - 1) / Constant.AXIS_NUM_X) * Constant.ACTIVE_PAGE_Y;  //以每页纸的左上角当做坐标系原点时，该点在每页纸的坐标系上的纵坐标
                //获取偏移量缓存
                offsetX = LogicController.getInstance().getOffsetX();
                offsetY = LogicController.getInstance().getOffsetY();
                //该页的相对坐标，需要进行比例缩放，得到屏幕上对应的点
                float realX = (x * canvasScale) + offsetX;
                float realY = (y * canvasScale) + offsetY;
                if (isDown()) {
                    mOLPath0.setPaintStrokeWidth(penWidth(press));
                    mOLPath0.touchDown(realX, realY);//mOLPath的初始点设为realx，realy  moveTo
                    drawPoint(foreGroundCanvas, mOLPath0.getmPaint(), realX, realY);
                    lastX = realX;
                    lastY = realY;
                    setIsDown(false);
                } else {
                    mOLPath0.setPaintStrokeWidth(penWidth(press));//仅仅只加了这一句就基本完成可笔锋的效果，但是还有很大的瑕疵
                    if (Objects.equals(point.getPress(), point.getFirstPress())) {
                        //当来的点是00点时，不再从上一个中点连到00点和上一点的中点，而是直接练到00点
                        mOLPath0.touchMove(realX, realY);
                        mOLPath0.lineTo(realX, realY);
                        drawPath(foreGroundCanvas, mOLPath0.getmPath(), mOLPath0.getmPaint(), realX, realY);
                        mOLPath0.getmPath().reset();
                        mOLPath0.getmPath().moveTo(realX, realY);
                        mOLPath0.getmPath().lineTo(realX, realY);
                    } else {
                        mOLPath0.touchMove(realX, realY);
                        drawPath(foreGroundCanvas, mOLPath0.getmPath(), mOLPath0.getmPaint(), realX, realY);
                        mOLPath0.getmPath().reset();
                        mOLPath0.getmPath().moveTo((lastX + realX) / 2, (lastY + realY) / 2);
                        mOLPath0.getmPath().lineTo((lastX + realX) / 2, (lastY + realY) / 2);
                    }
                }
                ensureDirty(mOLPath0, realX, realY);
                postInvalidate(ensureRect.left, ensureRect.top, ensureRect.right, ensureRect.bottom);//通知ui线程更新左.上.右.下:ensureRect是成员变量，由ensureDirty()实例化
                lastX = realX;
                lastY = realY;
                break;
        }
    }

    /**
     * 计算笔宽度
     */
    private float penWidth(float press) {
        float penWidth = 0;
        float maxWidth0 = 2;//5;//我设定笔粗最粗为
        float minWidth0 = 1;
        float penWidth0 = minWidth0 + (maxWidth0 - minWidth0) * press / 255.0f;//最大do值*压力比例---得到对应dp值
        penWidth = penWidth0 > minWidth0 ? penWidth0 : minWidth0;
        return penWidth + 1;
    }

    /**
     * 获取Matrix的缩放值
     *
     * @return
     */
    private float getScale(Matrix matrix) {
        float[] floats = new float[9];//一个空的9位浮点型数组
        matrix.getValues(floats);//将矩阵放入这个数组
        return floats[Matrix.MSCALE_X];//返回矩阵0号值
    }

    /**
     * 更新的区域
     */
    private Rect ensureDirty(OLPath olPath, float x, float y) {
        float s = olPath.getmPaint().getStrokeWidth() / 2;//s是 当前轨迹点的线宽的一半保证更新区域在线内
        Rect tempRect = new Rect((int) Math.floor(x - s),//+transX
                (int) Math.floor(y - s),//+transY
                (int) Math.ceil(x + s),//+transX
                (int) Math.ceil(y + s));//+transY
        if (ensureRect == null) {
            ensureRect = new Rect();
        }
        ensureRect.union(tempRect);
        return tempRect;
    }

    /**
     * 绘制路径
     */
    private void drawPath(Canvas myCanvas, Path path, Paint paint, float tempX, float tempY) {
        try {
            myCanvas.drawPath(path, paint);
        } catch (Exception e) {
        }
    }

    /**
     * 绘制点
     */
    private void drawPoint(Canvas myCanvas, Paint paint, float tempX, float tempY) {
        try {
            myCanvas.drawPoint(tempX, tempY, paint);
        } catch (Exception e) {
        }
    }

    /**
     * 绘制背景图片
     *
     * @param imageResource 图片资源
     */
    public void drawBackground(int imageResource) {
        if (isDrawViewInited) { //初始化完毕才能绘制背景，否则先缓存资源，初始化完毕监听回调内再绘制
            isDrawBack = false;
            backBitmap = getbackBitmap(imageResource);
            postInvalidate();
        } else {
            isDrawBack = true;
        }
        resourceCache = imageResource;
    }

    private Bitmap getbackBitmap(int imageResource) {
        InputStream is = getResources().openRawResource(imageResource);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float sx = targetSize[0] / w;
        float sy = targetSize[1] / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap bmp0 = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);//oom通过及时的recycle初步解决 ，以后待查
        bmp.recycle();
        System.gc();
        return bmp0;
    }

    public void initListener() {
        //监听view初始化完毕
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化完成读第一笔缓存,设置初始化完成状态isDrawViewInited为true
                isDrawViewInited = true;
                final NotePoint[] all = firstStrokeCache.getArrayAll();
                if (all.length > 0) {
                    readPointByFirstStroke(all);
                    firstStrokeCache.clearQueue();
                }
                firstStrokeCache.setCanAddFlag(false);
                if (isDrawBack && isFirstDraw) { //绘制背景图,只画一次
                    backBitmap = getbackBitmap(resourceCache);
                    postInvalidate();
                    isFirstDraw = false;
                }
            }
        });
    }

    /**
     * 加载第一笔缓存
     */
    private void readPointByFirstStroke(NotePoint[] firstStroke) {
        for (int i = 0; i < firstStroke.length; i++) {
            NotePoint point = firstStroke[i];
            if (point.getPress() <= point.getFirstPress()) {
                setIsDown(true);//初始化起点00坐标
            }
            point.setPointType(point.getPointType());
            drawLine(point);
        }
        firstStrokeCache.clearQueue();
    }

    /**
     * 加载数据库传来的点
     */
    public void readPointByDataBase(final List<NoteStroke> noteStrokeList) {
        dataBaseHandler.post(new Runnable() {

            private LinkedBlockingQueue<NoteStroke> queue = new LinkedBlockingQueue<>();//一个队列【存线对象的】

            @Override
            public void run() {
                queue.addAll(noteStrokeList);
                for (NoteStroke noteStroke : queue) {
                    if (tempOlPath != null)
                        tempOlPath.getmPaint().setColor(noteStroke.getStrokeColor());
                    List<NotePoint> notePointList = noteStroke.getNotePointList();
                    for (int j = 0; j < notePointList.size(); j++) {
                        NotePoint point = notePointList.get(j);
                        if (point.getPress() <= point.getFirstPress()) {
                            setIsDownData(true);//初始化起点00坐标
                        }
                        drawDataBase(point, noteStroke.getStrokeColor());
                    }
                    queue.remove(noteStroke);
                }
            }
        });
    }

    /**
     * 数据库绘制逻辑
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void drawDataBase(NotePoint point, int color) {
        if (tempOlPath != null)
            tempOlPath.getmPaint().setColor(color);
        switch (point.getPointType()) {//down点与up点设置点初始化，move点去绘制
            case 1:
            case 3:
                setIsDownData(true);//初始化起点00坐标
                return;
            case 2:
                if (!isDrawViewInited) {
                    return;
                }
                float x = point.getPX();
                float y = point.getPY();
                float press = point.getPress();//下面的逻辑很重要问题,我在使用笔时会画板丢页，就是因为Constant.AXIS_NUM_X值不对
                int notePageIndex = point.getPageIndex();
                x = x - ((notePageIndex - 1) % Constant.AXIS_NUM_X) * Constant.ACTIVE_PAGE_X;//以每页纸的左上角当做坐标系原点时，该点在每页纸的坐标系上的横坐标
                y = y - ((notePageIndex - 1) / Constant.AXIS_NUM_X) * Constant.ACTIVE_PAGE_Y;  //以每页纸的左上角当做坐标系原点时，该点在每页纸的坐标系上的纵坐标
                //获取偏移量缓存
                offsetX = LogicController.getInstance().getOffsetX();
                offsetY = LogicController.getInstance().getOffsetY();
                //该页的相对坐标，需要进行比例缩放，得到屏幕上对应的点
                float realX = (x * canvasScale) + offsetX;
                float realY = (y * canvasScale) + offsetY;

                if (isDownData()) {
                    tempOlPath.setPaintStrokeWidth(penWidth(press));
                    tempOlPath.touchDown(realX, realY);//mOLPath的初始点设为realx，realy  moveTo
                    drawPoint(bgCanvas, tempOlPath.getmPaint(), realX, realY);
                    dataLastX = realX;
                    dataLastY = realY;
                    setIsDownData(false);
                } else {
                    tempOlPath.setPaintStrokeWidth(penWidth(press));//仅仅只加了这一句就基本完成可笔锋的效果，但是还有很大的瑕疵
                    if (Objects.equals(point.getPress(), point.getFirstPress())) {
                        //当来的点是00点时，不再从上一个中点连到00点和上一点的中点，而是直接练到00点
                        tempOlPath.touchMove(realX, realY);
                        tempOlPath.lineTo(realX, realY);
                        drawPath(bgCanvas, tempOlPath.getmPath(), tempOlPath.getmPaint(), realX, realY);
                        tempOlPath.getmPath().reset();
                        tempOlPath.getmPath().moveTo(realX, realY);
                        tempOlPath.getmPath().lineTo(realX, realY);
                    } else {
                        tempOlPath.touchMove(realX, realY);
                        drawPath(bgCanvas, tempOlPath.getmPath(), tempOlPath.getmPaint(), realX, realY);
                        tempOlPath.getmPath().reset();
                        tempOlPath.getmPath().moveTo((dataLastX + realX) / 2, (dataLastY + realY) / 2);
                        tempOlPath.getmPath().lineTo((dataLastX + realX) / 2, (dataLastY + realY) / 2);
                    }
                }
                ensureDirty(tempOlPath, realX, realY);
                //通知ui线程更新左.上.右.下:ensureRect是成员变量，由ensureDirty()实例化
                postInvalidate(ensureRect.left, ensureRect.top, ensureRect.right, ensureRect.bottom);
                dataLastX = realX;
                dataLastY = realY;
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //销毁设置可缓存第一笔
        firstStrokeCache.setCanAddFlag(true);
        dataBaseThread.quit();//数据库线程关闭
//        if (bgBitmap != null)
//            bgBitmap.recycle();
//        bgBitmap = null;
//        if (foreGroundBitmap != null)
//            foreGroundBitmap.recycle();
//        foreGroundBitmap = null;
//        System.gc();
    }

    public float getScale() {
        return canvasScale;
    }
}