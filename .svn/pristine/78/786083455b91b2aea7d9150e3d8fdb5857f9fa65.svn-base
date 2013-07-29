package controllers;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import dbutils.QicDbUtil;
import models.common.UserInfo;
import play.Logger;
import play.Play;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.libs.Codec;
import play.libs.IO;
import play.modules.redis.Redis;
import play.mvc.Controller;
import util.RedisKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Application extends Controller {

    public static void index() {
        String str = "{\n" +
                "     \"name\":\"产品1\",\n" +
                "     \"strategys\":[1, 2, 3],\n" +
                "     \"tradeAccounts\":[4, 5, 6]\n" +
                "}";
        render();
    }

    //把记录用户登陆的mac地址清除掉
    public static void gotoClearUserMacSet(){
        render();
    }

    public static void clearUserMacSet(String account) {
        Logger.info("消除帐号[%s]的macset", account);
        UserInfo userInfo = UserInfo.findByAccount(account);
        if (userInfo != null) {
            String userMacSetKey = RedisKeys.userMacSetKey(userInfo.account);
            Redis.del(new String[]{userMacSetKey});
            renderText("清除了%s的macSet", account);
        }else{
            renderText("没有找到用户");
        }
    }

    static Joiner joiner = Joiner.on("\r\n").skipNulls();
    //列出account的mac地址集信息
    public static void listUserMacSet(String account){
        UserInfo userInfo = UserInfo.findByAccount(account);
        if (userInfo != null) {
            String userMacSetKey = RedisKeys.userMacSetKey(userInfo.account);
            Set<String> macSets = Redis.smembers(userMacSetKey);
            if (macSets == null) {
                macSets = Sets.newHashSet();
            }
            renderText(joiner.join(macSets));
        } else {
            renderText("没有找到用户");
        }
    }

    public static void ad() {
        render();
    }

    @NoTransaction
    public static void customerInfo() {
        render();
    }

    @NoTransaction
    public static void vpTest(){
        render();
    }

    public static void gotoIquantUpload() {
        render();
    }

    /**
     * 获取产品json信息
     * 这个只是为了演示而临时存在的
     */
    @Transactional(readOnly = true)
    public static void fetchProducts() {
        String sql = "SELECT id, `name` FROM sys_product_info";
        List<Map<String, Object>> productList = QicDbUtil.queryMapList(sql);
        renderJSON(productList);
    }

    /**
     * 这个只是先提供给文件上传接口的测试例子
     */
    public static void fileUploadTest(File file, String field1, String field2, String field3) {
        if (file != null) {
            FileInputStream inputStream = null;
            FileOutputStream os = null;
            try {
                inputStream = new FileInputStream(file);
                String name = file.getName() + Codec.UUID().replace('-', '_');
                if (Play.mode == Play.Mode.DEV) {
                    os = new FileOutputStream("e:/testdata/" + name);
                }else{
                    os = new FileOutputStream("/var/testdata/" + name);
                }
                IO.copy(inputStream, os);
                os.close();
            } catch (Exception e) {
                Logger.error("文件上传错误");
                e.printStackTrace();
                renderText("上传出现错误%s", e.getMessage());
            }

            renderText("上传成功, 文件名:%s, 大小:%d, 其它值: field1=%s, field2=%s, field3=%s", file.getName(), file.length(), field1, field2, field3);
        }else {
            renderText("没有上传文件, 其它值: field1=%s, field2=%s, field3=%s", field1, field2, field3);
        }
    }

}