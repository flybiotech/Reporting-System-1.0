package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangbin on 2017/9/29.
 */

public class ExceptionManager extends DataSupport {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
