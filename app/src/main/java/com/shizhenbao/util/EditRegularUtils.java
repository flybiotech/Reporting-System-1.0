package com.shizhenbao.util;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DialerKeyListener;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditRegularUtils {

    /**
     * 过滤特殊字符和空格,限制输入框长度
     */
    public static void speChat(EditText editText,int lenth){
        InputFilter filter_space = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" "))
                    return "";
                else
                    return null;
            }
        };
        InputFilter filter_speChat = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                String speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter_space, filter_speChat,new InputFilter.LengthFilter(lenth)});
//        editText.setFilters(new InputFilter[]{filter_speChat,new InputFilter.LengthFilter(lenth)});
    }

    /**
     * 只能输入字母和数字
     */
    public static void alphanumeric(EditText editText){
        String regular = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        editText.setKeyListener(new DialerKeyListener(){
            @Override
            public int getInputType() {
                return InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[]ac = regular.toCharArray();
                return ac;
            }
        });
    }

    /**
     * 单独限制输入的长度
     */
    public static void editmaxLenth(EditText editText,int lenth){
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenth)});
    }

    /**
     * 限制行数
     */
    public static void editLenth(EditText editText){
        editText.setMaxLines(1);
    }
}
