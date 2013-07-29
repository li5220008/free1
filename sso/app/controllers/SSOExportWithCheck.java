package controllers;

import models.common.UserInfo;
import play.data.validation.Required;
import play.modules.redis.Redis;
import play.mvc.With;
import redis.clients.jedis.Pipeline;
import util.LoginTokenCompose;
import util.RedisKeys;

import java.util.List;
import java.util.Map;

import static util.ConstantInterface.PID_REQUIRED;

/**
 * sso暴露出来的接口,需要loginToken校验
 * User: wenzhihong
 * Date: 12-4-18
 * Time: 上午11:53
 */
@With(value = {LoginTokenCheckIntercept.class, GeneralIntercept.class})
public class SSOExportWithCheck extends BaseController {

    /**
     * 取本产品(登陆产品)功能列表
     */
    public static void fetchFuction() {
        LoginTokenCompose compose = LoginTokenCompose.current();
        List<Map<String, Object>> funList = UserInfo.fetchFunctionByUserAndProduct(compose.uid, compose.pid);
        renderJSON(funList);
    }

    /**
     * 获取其它产品功能列表
     *
     * @param pid 产品id
     */
    public static void fetchOtherProductFunction(@Required(message = PID_REQUIRED) Long pid) {
        LoginTokenCompose compose = LoginTokenCompose.current();
        List<Map<String, Object>> funList = UserInfo.fetchFunctionByUserAndProduct(compose.uid, pid);
        renderJSON(funList);
    }

    /**
     * 退出操作
     * 成功退出返回 ok
     */
    public static void loginOut() {
        LoginTokenCompose compose = LoginTokenCompose.current();

        //把mac地址和token都从redis去掉
        Pipeline p = Redis.pipelined();
        String userTokenKey = RedisKeys.userMacTokenKey(compose.mac);
        p.del(new String[]{userTokenKey});

        String userMacSetKey = RedisKeys.userMacSetKey(compose.userName);
        p.srem(userMacSetKey, compose.mac);
        p.sync();

        //这里认为总是成功
        renderText("ok");
    }
}
