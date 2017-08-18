
package com.newchinese.coolpensdk.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Description:
 * author         xulei
 * Date           2017/5/17 17:03
 */
class SharedPreUtils {

    /**
     * 全局shared preference的名称
     */
    private static final String SHARED_PREFERANCE_NAME = "coolpensdk";

    static void setInteger(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    static int getInteger(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static String getString(Context context, String key) {

        //
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    static void setLong(Context context, String key, Long value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    static Long getLong(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, -1);
    }

    static Long getLong(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }


    static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    static boolean getBoolean(Context context, String key,
                              boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
}
