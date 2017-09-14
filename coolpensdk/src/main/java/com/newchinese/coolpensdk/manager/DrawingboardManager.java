package com.newchinese.coolpensdk.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;
import com.newchinese.coolpensdk.listener.OnPointListener;
import com.newchinese.coolpensdk.utils.GetAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:   画板工具类
 * author         xulei
 * Date           2017/4/25
 */

class DrawingboardManager {
    private Context context;
    private NotePoint previousPoint;
    private OnPointListener onPointListener;
    private FirstStrokeCache firstStrokeCache;
    private LogicController controller;
    private List<NotePoint> notePointList = new ArrayList<>();
    private boolean isStrokeFirstUpPoint = true;

    DrawingboardManager(Context context, String appKey) {
        this.context = context;
        firstStrokeCache = FirstStrokeCache.getInstance();
        controller = LogicController.getInstance();
        if (SharedPreUtils.getBoolean(context, "approved", true)) {
            controller.setApproved(true);//未验证前默认使用权限可用，请求验证后为不可用，默认则改为不可用
        } else {
            controller.setApproved(false);
        }
        if (!TextUtils.isEmpty(appKey)) {
            controller.setAppKey(appKey);
            confirmStatus();
        } else {
            Log.e("coolPenError", "11004:AppKey为空");
        }
    }

    /**
     * 设置点回传监听
     */
    void setPointListener(OnPointListener onPointListener) {
        this.onPointListener = onPointListener;
    }

    /**
     * 获取页数
     *
     * @param notePoint 当前传入的点对象
     */
    int getPageIndex(NotePoint notePoint) {
        return notePoint.getPageIndex();
    }

    /**
     * 判断当前传来的点与保存的前一点是否是同一页的点
     *
     * @param notePoint 在接收到down点时传入NotePoint
     */
    boolean isSameNotePage(NotePoint notePoint) {
        return LogicController.getInstance().isSameNotePage(notePoint);
    }

    /**
     * 设置写字本的大小
     *
     * @param width  宽
     * @param height 高
     */
    void setBookSize(float width, float height) {
        Constant.ACTIVE_PAGE_X = width;
        Constant.ACTIVE_PAGE_Y = height;
        Constant.AXIS_NUM_X = (int) Math.floor(Constant.POINT_MAX_X / Constant.ACTIVE_PAGE_X);
        Constant.AXIS_NUM_Y = (int) Math.floor(Constant.POINT_MAX_Y / Constant.ACTIVE_PAGE_Y);
    }

    /**
     * 设置原点偏移量
     *
     * @param baseXOffset
     * @param baseYOffset
     */
    void setBaseOffset(float baseXOffset, float baseYOffset) {
        LogicController.getInstance().setBaseXOffset(baseXOffset);
        LogicController.getInstance().setBaseYOffset(baseYOffset);
    }

    /**
     * 清除缓存点
     */
    void clearCache() {
        controller.setCachePoint(null);
        controller.setPreviousPoint(null);
    }

    /**
     * 解析原始数据为点对象,调用此方法得到的点对象没有PointType
     *
     * @param value 蓝牙原始数据
     */
    NotePoint getAnalysisPoint(String value) {
        return AnalysisPointUtil.getInstance().parseStringToBean(value);
    }

    /**
     * 过滤解析完的点
     *
     * @param values    蓝牙原始数据
     * @param notePoint 解析过的点
     */
    void startFilterPoint(String values, NotePoint notePoint, int fromType) {
        if (!controller.isApproved()) {//没有使用权限
            Log.e("coolPenError", "11007:没有使用权限，请续费");
            return;
        }
        if (notePoint == null) {//防止传入点为空
            Log.e("coolPenError", "11002:蓝牙传入的点为空");
            return;
        }
        if (!((values.length() == 24 && values.startsWith("6")) ||
                notePoint.getFirstPress() == notePoint.getPress())) {//首次过滤
            return;
        }
        previousPoint = LogicController.getInstance().getPreviousPoint();//缓存的前一个点
        float first_press = notePoint.getFirstPress();
        float press = notePoint.getPress();
        float x = notePoint.getPX();
        float y = notePoint.getPY();
        float testTime = notePoint.getTestTime();
        int pageDesIndex = notePoint.getPageIndex();

        if (previousPoint != null && pageDesIndex != previousPoint.getPageIndex()) { //跳页点设置为新页一笔的第一个点
            LogicController.getInstance().setCachePoint(previousPoint);
            LogicController.getInstance().setIsFirstpoint(true);
            FirstStrokeCache.getInstance().setCanAddFlag(true);
        }

        if (pageDesIndex > 0 && pageDesIndex < 100) { //第二次过滤：过滤页数大于100的点
            if (LogicController.getInstance().getIsFirstpoint() && press > first_press) { //一笔的第一个点且压力值不为0
                if (previousPoint != null && previousPoint.getPageIndex() == pageDesIndex) { //过滤乱页点，只可过滤一个乱页点,若出现连续多个乱页点则无法过滤
                    //给down点event
                    setDownPoint(fromType, notePoint);
                    LogicController.getInstance().setPreviousPoint(notePoint);
                    LogicController.getInstance().setCachePoint(notePoint);
                    LogicController.getInstance().setIsFirstpoint(false);
                    isStrokeFirstUpPoint = true;
                } else {
                    LogicController.getInstance().setPreviousPoint(notePoint);
                }
            } else { //非第一个点
                LogicController.getInstance().setPreviousPoint(notePoint);
                if (previousPoint != null && ((Math.abs(previousPoint.getPX() - x) > 0.5 ||
                        Math.abs(previousPoint.getPY() - y) > 0.5) || (press >= first_press)) && //第三次过滤，滤小点
                        previousPoint.getPageIndex() == pageDesIndex) {
                    if (Math.abs(x - previousPoint.getPX()) > 15 ||
                            Math.abs(y - previousPoint.getPY()) > 15) { //第四次过滤,过滤破点
                        //手动给抬起点event
                        setUpPoint(fromType, previousPoint);
                        //设置下一个点为下一条线的第一个点
                        LogicController.getInstance().setIsFirstpoint(true);
                        return;
                    } else if (testTime - previousPoint.getTestTime() > 20) { //第五次过滤，根据时间差解决连笔
                        setUpPoint(fromType, previousPoint);
                        LogicController.getInstance().setIsFirstpoint(true);
                        return;
                    } else { //正常Move点
                        if (press > first_press) { //压力值大于0
                            //移动中的给Move点event
                            setMovePoint(fromType, notePoint);
                        } else { //压力值等于0
                            //一笔完事儿给抬起点event
                            if (isStrokeFirstUpPoint) {
                                setUpPoint(fromType, notePoint);
                                LogicController.getInstance().setIsFirstpoint(true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 回传down点并缓存第一笔
     */
    private void setDownPoint(int fromType, NotePoint notePoint) {
        notePoint.setPointType(PointType.TYPE_DOWN);
        if (!controller.isSameNotePage(notePoint)) {//down点页数不同说明是新页第一笔则开启第一笔缓存
            firstStrokeCache.setCanAddFlag(true);
        }
        if (firstStrokeCache.isCanAddFlag()) { //第一笔缓存
            firstStrokeCache.putInQueue(notePoint); //保存第一笔down点
        }
        if (onPointListener != null) {
            if (!isSameNotePage(notePoint)) { //换页回调
                onPointListener.onPageIndexChanged(fromType, notePoint);
            }
            onPointListener.onPointCatched(fromType, notePoint);
        } else {
            Log.e("coolPenError", "11003:未设置点监听");
        }
        notePointList = new ArrayList<>();
        notePointList.add(notePoint);
    }

    /**
     * 回传move点并缓存第一笔
     */
    private void setMovePoint(int fromType, NotePoint notePoint) {
        notePoint.setPointType(PointType.TYPE_MOVE);
        if (firstStrokeCache.isCanAddFlag()) { //第一笔缓存
            firstStrokeCache.putInQueue(notePoint); //保存第一笔move点
        }
        if (onPointListener != null) {
            onPointListener.onPointCatched(fromType, notePoint);
        } else {
            Log.e("coolPenError", "11003:未设置点监听");
        }
        notePointList.add(notePoint);
    }

    /**
     * 回传up点并缓存第一笔
     */
    private void setUpPoint(int fromType, NotePoint notePoint) {
        notePoint.setPointType(PointType.TYPE_UP);
        if (firstStrokeCache.isCanAddFlag()) { //第一笔缓存
            firstStrokeCache.putInQueue(notePoint); //保存第一笔up点
        }
        if (onPointListener != null) {
            onPointListener.onPointCatched(fromType, notePoint);
            notePointList.add(notePoint);
            NoteStroke noteStroke = new NoteStroke(notePointList);
            onPointListener.onStrokeCached(fromType, noteStroke);
        } else
            Log.e("coolPenError", "11003:未设置点监听");
    }

    /**
     * 验证当前用户使用权限
     */
    private void confirmStatus() {
        if (isNetworkAvailable(context)) {
            reQuestPermission();
        } else {
            Log.e("coolPenError", "11005:当前网络不可用");
        }
    }

    private void reQuestPermission() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String appKey = controller.getAppKey();
                String address = GetAddressUtil.getIMEIAddress(context);
                Log.e("test_address", "IMEIAddress:" + address);
                //appKey,手机唯一识别码，android传1
                String[] parameter = {"userToken", "phoneId", "phonetype"};
//                String[] parameterValue = {"1308e911d0841bf20922d075dfaab229", "1502342411324566026", "0"};
                String[] parameterValue = {appKey, address, "1"};
                try {
                    //通过openConnection 连接  
                    URL url = new URL(Constant.CONFIRM_URL + "/user/testToken");
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                    //设置输入和输出流   
                    urlConn.setRequestMethod("POST");
                    urlConn.setDoOutput(true);
                    urlConn.setDoInput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setInstanceFollowRedirects(false);
                    urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConn.connect();

                    //设置请求参数
                    DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                    JSONObject o = new JSONObject();
                    for (int i = 0; i < parameter.length; i++) {
                        o.put(parameter[i], parameterValue[i]);
                    }
                    Log.i("requestData", "请求参数" + o.toString());
                    out.writeBytes(o.toString());
                    out.flush();
                    out.close();

                    //接收返回结果
                    if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                        BufferedReader buffer = new BufferedReader(in);
                        String inputLine = null;
                        String resultData = "";
                        while (((inputLine = buffer.readLine()) != null)) {
                            resultData += inputLine + "\n";
                        }
                        Log.e("resultData", "返回结果:" + resultData);
                        analysisResult(resultData);
                        in.close();
                    } else {
                        Log.e("coolPenError", "11006:网络请求失败");
                    }
                    urlConn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("coolPenError", "11006:网络请求失败");
                }
            }
        }).start();
    }

    /**
     * 检测网络状态
     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的json结果
     */
    private void analysisResult(String resultData) {
        try {
            JSONObject jsonObject = new JSONObject(resultData);
            String status = jsonObject.getString("status");
            int base = jsonObject.getInt("base");
            if (!TextUtils.isEmpty(status) && status.equals("100000") && base == 1) {
                controller.setApproved(true);
                SharedPreUtils.setBoolean(context, "approved", true);
            } else {
                controller.setApproved(false);
                SharedPreUtils.setBoolean(context, "approved", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
