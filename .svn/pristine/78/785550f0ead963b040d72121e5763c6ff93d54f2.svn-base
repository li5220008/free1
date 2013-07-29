package controllers;

import bussiness.PersonalModifyService;
import controllers.common.BasePlayControllerSupport;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.UserInfoDto;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息修改
 * User: panzhiwei
 * Date: 12-11-21
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
public class PersonalModify extends BasePlayControllerSupport{
    public static void  personalmodify(Long uid){
        String sql = SqlLoader.getSqlById("findUserInfoDtoById");
        UserInfoDto userInfo = QicDbUtil.queryQicDBSingleBean(sql,UserInfoDto.class,uid);
        render(userInfo);
    }
    public static void modifySuccess(UserInfoDto userInfoDto,Long uid){
        PersonalModifyService.updateUserInfo(userInfoDto,uid);
        Map<String,Object> json = new HashMap<String, Object>();
        json.put("success",true);
        json.put("message","个人信息修改成功");
        renderJSON(json);
    }

    public static void checkPassword(String password,Long uid){
        boolean  flag = PersonalModifyService.findPwdById(password,uid);
        if(flag){
           renderText(true);
        }
        else {
            renderText(false);
        }

    }

    public static void checkEmail (String email) {
        String[] emails = email.split(",");
        if(PersonalModifyService.checkEmail(emails[0],emails[1]) == null){
            renderText(true);
        } else {
            renderText(false);
        }

    }
}
