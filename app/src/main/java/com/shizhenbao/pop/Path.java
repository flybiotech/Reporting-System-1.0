package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangbin on 2017/8/9.
 */

public class Path extends DataSupport {
    private String PicPath;


    public String getPicPath() {
        return PicPath;
    }

    public void setPicPath(String picPath) {
        PicPath = picPath;
    }
}
