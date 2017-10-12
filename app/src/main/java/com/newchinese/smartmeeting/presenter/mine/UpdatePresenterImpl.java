package com.newchinese.smartmeeting.presenter.mine;

import android.text.TextUtils;
import android.util.Log;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.entity.bean.BaseResult;
import com.newchinese.smartmeeting.entity.bean.FeedBack;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.entity.http.NetUrl;
import com.newchinese.smartmeeting.model.UpdateModelImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017-08-30.
 */

public class UpdatePresenterImpl implements MineContract.UpdateIPresenter<MineContract.UpdateIVIew> {
    private MineContract.UpdateIVIew mV;
    private UpdateModelImpl mModel;

    @Override
    public MineContract.UpdateIPresenter attach(MineContract.UpdateIVIew v) {

        mV = v;
        mModel = new UpdateModelImpl(this);
        return this;
    }

    @Override
    public void detach() {
        mV = null;
    }

    @Override
    public void updatePass(String oldPass, String newPass) {
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
        mModel.updatePass(data.setPassword(oldPass).setNew_password(newPass));
    }

    @Override
    public void feedBack(String content, String contact) {
        mModel.feedBack(new FeedBack().setConnect(content).setFeed_back(contact));
    }

    @Override
    public void loading() {
        if (mV != null) {
            mV.showLoading(App.getContext().getString(R.string.loading_request_m));
        }
    }

    @Override
    public void onResult(boolean succ, String type, BaseResult data) {
        CustomizedToast.showLong(App.getAppliction(), data.msg);
        if (mV != null) {
            mV.showLoading(null);
        }
        if (succ) {
            if ("updatePass".equals(type)) {
                SharedPreUtils.setString(Constant.PASSWORD_FLAG, "1");
            }
            mV.jumpLogin("change");
        }
    }

    @Override
    public void setPass(final String tel, final String password) {
        loading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] parameter = {"tel", "password"};
                String[] parameterValue = {tel, md5(password)};
                try {
                    //通过openConnection 连接  
                    URL url = new URL(NetUrl.FORGET_PASS);
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
                        Log.i("resultData", "返回结果:" + resultData);
                        analysisResult(resultData);
                        in.close();
                    } else {
                    }
                    urlConn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 解析服务器返回的json结果
     */
    private void analysisResult(String resultData) {
        if (mV != null) {
            mV.showLoading(null);
        }
        try {
            JSONObject jsonObject = new JSONObject(resultData);
            String no = jsonObject.getString("no");
            if (!TextUtils.isEmpty(no) && no.equals(NetUrl.NO_SUCC)) {
                SharedPreUtils.setString(Constant.PASSWORD_FLAG, "1");
                mV.jumpLogin("set");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String md5(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(src.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String s = Integer.toHexString(b & 0xff);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
