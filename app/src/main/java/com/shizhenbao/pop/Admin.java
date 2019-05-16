package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangbin on 2017/9/12.
 */

public class Admin extends DataSupport {
    private String controllerName;
    private String controllerPassword;

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getControllerPassword() {
        return controllerPassword;
    }

    public void setControllerPassword(String controllerPassword) {
        this.controllerPassword = controllerPassword;
    }
}
