package com.newchinese.smartmeeting.entity.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017-08-24.
 */
@Entity
public class LoginData {

    @Id(autoincrement = true)
    public Long id;

    public String code, token, nickname, im_token, tel, icon, comment, password;

    @Transient
    public String new_password, icon_format, openid, access_token, flag, sms;

    @Generated(hash = 315100689)
    public LoginData(Long id, String code, String token, String nickname,
                     String im_token, String tel, String icon, String comment,
                     String password) {
        this.id = id;
        this.code = code;
        this.token = token;
        this.nickname = nickname;
        this.im_token = im_token;
        this.tel = tel;
        this.icon = icon;
        this.comment = comment;
        this.password = password;
    }

    @Generated(hash = 1578814127)
    public LoginData() {
    }

    public Long getId() {
        return id;
    }

    public LoginData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public LoginData setCode(String code) {
        this.code = code;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginData setToken(String token) {
        this.token = token;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public LoginData setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getIm_token() {
        return im_token;
    }

    public LoginData setIm_token(String im_token) {
        this.im_token = im_token;
        return this;
    }

    public String getTel() {
        return tel;
    }

    public LoginData setTel(String tel) {
        this.tel = tel;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginData setPassword(String password) {
//        this.password = md5(password);
        this.password = password;
        return this;
    }

    public String getIcon_format() {
        return icon_format;
    }

    public LoginData setIcon_format(String icon_format) {
        this.icon_format = icon_format;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public LoginData setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getNew_password() {
        return new_password;
    }

    public LoginData setNew_password(String new_password) {
//        this.new_password = md5(new_password);
        this.new_password = new_password;
        return this;
    }

    public String getComment() {
        return this.comment;
    }

    public LoginData setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getOpenid() {
        return openid;
    }

    public LoginData setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public LoginData setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getSms() {
        return sms;
    }

    public LoginData setSms(String sms) {
        this.sms = sms;
        return this;
    }

    public String getAccess_token() {
        return access_token;
    }

    public LoginData setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
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

    @Override
    public String toString() {
        return "LoginData{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", token='" + token + '\'' +
                ", nickname='" + nickname + '\'' +
                ", im_token='" + im_token + '\'' +
                ", tel='" + tel + '\'' +
                ", icon='" + icon + '\'' +
                ", comment='" + comment + '\'' +
                ", password='" + password + '\'' +
                ", new_password='" + new_password + '\'' +
                ", icon_format='" + icon_format + '\'' +
                ", openid='" + openid + '\'' +
                ", flag='" + flag + '\'' +
                ", sms='" + sms + '\'' +
                '}';
    }
}
