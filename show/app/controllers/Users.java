package controllers;

import bussiness.UsersService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controllers.common.AuthorBaseIntercept;
import dto.UserRegisterDto;
import models.common.SaleDepartment;
import models.common.UserInfo;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Util;
import util.LoginTokenCompose;
import util.Tokens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户操作, 把用户登陆的放在这里进行操作
 * User: wenzhihong
 * Date: 12-11-11
 * Time: 下午2:04
 */
public class Users extends Controller {
    @Util
    public static String getRemoteIp(){
        String ip = null;
        Http.Header header = request.headers.get("x-forwarded-for");
        if(header != null){
            ip = header.value();
            Logger.info("client remote ip:%s",ip);
        }else{
            ip = request.remoteAddress;
            Logger.info("client local ip:%s",ip);
        }
        return ip;
    }

    private static String createMacArr(){
        String remoteIp = getRemoteIp();
        if (remoteIp == null) {
            Logger.error("没有取到客户端ip, 给个默认值:256");
            remoteIp = "256";
        }
        String tmp = "00" + Integer.toHexString(remoteIp.hashCode() % 256);

        return "E06F05EA4E" + tmp.substring(tmp.length()-2);
    }


    public static void gotoLogin() {
        render();
    }

    public static void login(String name, String pwd, String macAddr) {
        checkAuthenticity();
        Long pid = 2L; //先用一个固定的
        String mac = null; //TODO 这里随机产生一个
        if (!StringUtils.isBlank(macAddr)) {
            mac = macAddr;
        }else{
            mac = createMacArr();
            Logger.info("没有mac地址, 根据ip[%s]地址,随机产生一个[%s]", getRemoteIp(), mac);
        }
        String url = Play.configuration.getProperty("sso.url") + "/api/loginWithoutCryp?u=" + name + "&p=" + pwd + "&mac=" + mac + "&pid=" + pid;
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

    public static void register(@Valid UserRegisterDto userRegisterDto){
        List<SaleDepartment> saleDepartments = SaleDepartment.findAll();
        if(userRegisterDto == null){
            render(saleDepartments);
        }

        try {
            Map<String,Object> json = new HashMap();
            if(UsersService.addUser(userRegisterDto)){
                //String s = "";
                //renderTemplate("@gotoLogin"); //qicScriptContext.CloseWindow()

                json.put("message","注册成功");
                renderJSON(json);

            }else{
                json.put("message","注册失败");
                renderJSON(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证email
     * @param email
     */
    public static void findUserByEmail(String email){
        if(  UsersService.findUserByEmail(email)==null)    {
            renderText(true);
        }
        else
            renderText(false);
    }

    /**
     * 验证账户
     * @param account
     */
    public static void findUserByAccount(String account){
        if(  UsersService.findUserByAccount(account)==null)    {
            renderText(true);
        }
        else
            renderText(false);
    }

}
