package com.shizhenbao.sharePreferencess;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dell on 2017/5/25.
 */

public class SharedPreferenceText {

    SharedPreferences sp;
    Context context;
    public SharedPreferenceText(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    //存储登陆信息
    public void setLoginInfo(String name,String pass){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loginName", name);
        editor.putString("loginPass", pass);
        editor.commit();

    }
}
