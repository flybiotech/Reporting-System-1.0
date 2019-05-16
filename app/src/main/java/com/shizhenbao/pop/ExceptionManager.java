package com.shizhenbao.pop;

import org.litepal.crud.LitePalSupport;

/**
 * Created by zhangbin on 2017/9/29.
 */

public class ExceptionManager extends LitePalSupport {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
