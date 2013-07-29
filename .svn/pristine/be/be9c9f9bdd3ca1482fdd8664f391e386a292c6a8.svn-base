package models.common;


import dto.BaseDtoSupport;
import util.LoginTokenCompose;

import java.util.Date;

/**
 * 用户资源相关的dto
 * User: wenzhihong
 * Date: 13-4-28
 * Time: 下午2:33
 */
public class BaseUserResourceDtoSupport  extends BaseDtoSupport {

    /**
     * 创建时间
     */
    public Date ctime = new Date();

    /**
     * 用户id
     */
    public Long userId;

    /**
     * 产品id
     */
    public Long productId;

    /**
     * 从loginTokenCompose里取值,填充userId跟productId两个属性. 方便操作
     */
    public void fillUserAndProductProperty() {
        LoginTokenCompose compose = LoginTokenCompose.current();
        this.userId = compose.uid;
        this.productId = compose.pid;
    }

    /**
     * 是否属于自身资源
     */
    public boolean isMyselfResource(LoginTokenCompose compose) {
        return userId == compose.uid;
    }

    /**
     * 是否属于自身资源
     */
    public boolean isMyselfResource() {
        LoginTokenCompose compose = LoginTokenCompose.current();
        return compose != null && userId == compose.uid;
    }

    /**
     * 是否允许操作
     */
    public static boolean permit(BaseUserResourceDtoSupport r, LoginTokenCompose compose) {
        return r != null && r.userId == compose.uid;
    }
}
