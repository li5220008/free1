package controllers;

import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controllers.common.AuthorBaseIntercept;
import controllers.external.IpInterceptor;
import models.common.UserInfo;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Util;
import util.LoginTokenCompose;
import util.Tokens;

/**
 * 用户操作, 把用户登陆的放在这里进行操作
 * User: wenzhihong
 * Date: 12-11-11
 * Time: 下午2:04
 */
public class Users extends Controller {
    public static void gotoLogin() {
        render();
    }

    private static String createMacArr(){
        String remoteIp = IpInterceptor.getRemoteIp();
        if (remoteIp == null) {
            Logger.error("没有取到客户端ip, 给个默认值:256");
            remoteIp = "256";
        }
        String tmp = "00" + Integer.toHexString(remoteIp.hashCode() % 256);

        return "E06F05EA4E" + tmp.substring(tmp.length()-2);
    }

    public static void login(String name, String pwd, String macAddr) {
        checkAuthenticity();
        Long pid = 2L; //先用一个固定的
        String mac = null; //TODO 这里随机产生一个
        if (!StringUtils.isBlank(macAddr)) {
            mac = macAddr;
        }else{
            mac = createMacArr();
            Logger.info("没有mac地址, 根据ip[%s]地址,随机产生一个[%s]", IpInterceptor.getRemoteIp(), mac);
        }
        String url = Play.configuration.getProperty("sso.url") + "/api/loginWithoutCryp?u=" + name + "&p=" + pwd + "&mac=" + mac + "&pid=" + pid;
        System.out.println(url);
        JsonElement json = WS.url(url).get().getJson();
        Logger.info("登陆处理返回json对象:%s", json.toString());
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            boolean success = jsonObject.get("success").getAsBoolean();
            if (success) {
                String token = jsonObject.get("token").getAsString();
                LoginTokenCompose loginTokenCompose = Tokens.decryptLoginToken(token);
                session.put(AuthorBaseIntercept.USER_ID_SESSION_KEY, loginTokenCompose.uid); //写入session数据
                response.setCookie("ntToken", token);
                renderTemplate("@list");
            }else{
                String errorMsg = jsonObject.get("message").getAsString();
                params.flash("name");
                validation.addError("aa", errorMsg);
                renderTemplate("@gotoLogin");
            }
        }
    }

    public static void list() {
        render();
    }

    public static void logout() {
    }

}
