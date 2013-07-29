package bussiness;

import business.SystemConfigService;
import business.UserInfoBaseService;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.ActivateUserDto;
import dto.UserInfoDto;
import models.common.RoleInfo;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import util.MessageBuilder;

import java.util.Date;
import java.util.List;

/**
 * 用户授权
 * User: liuhongjiang
 * Date: 12-12-5
 * Time: 下午6:34
 */
public class UserAuthorizationService extends UserInfoBaseService {
    //找到所有权限名称
    public static List<RoleInfo> findAllRole(){
        List<RoleInfo> list = RoleInfoService.findAllRole();
        return list;
    }

    /**
     * 批量插入用户权限
     * @author 刘泓江
     * @param userId 用户id
     * @param roleId 角色id
     * @return  flag  -1  参数不全， -2 时间验证失败， 1 授权成功
     */

    public static int insertUserRole(int[] userId,int[] roleId,Date edate){
        int flag = -1;
        int len = 0;
        String insertUserRoleSql = SqlLoader.getSqlById("insertUserRole");
        String deleteUserRoleSql = SqlLoader.getSqlById("deleteUserRole");
        String updateRoleDateSql = SqlLoader.getSqlById("updateUserRole");
        String findUserByIdSql = SqlLoader.getSqlById("findUserById");
        if(edate==null){
            edate = new Date("2099/09/09");
        }
        if(edate.before(new Date())){
            return -2;
        }
        if(userId == null ||roleId==null){
            return -1;
        }
        if (userId.length > 0 && roleId.length > 0) {
            len = (userId.length * roleId.length);
        }
        Integer[][] arrayId = new Integer[len][2];

        int k = 0;
        for (int i = 0; i < userId.length; i++) {
            for (int j = 0; j < roleId.length; j++) {
                arrayId[k][0] = userId[i];
                arrayId[k][1] = roleId[j];
                k++;
            }
        }

        //更新时间
        for (int uid :userId){
            QicDbUtil.updateQicDB(updateRoleDateSql, edate, uid);
            //删除
            QicDbUtil.updateQicDB(deleteUserRoleSql, uid);
            //清空能缓存
            deleteUserFromCache(uid);
        }
        //新增
        int[] count = QicDbUtil.batchQicDB(insertUserRoleSql, arrayId);
        if (count.length > 0 && count.length == len) {
            flag = 1;
        }
        //发邮件给用户，告知账号已激活
        try {
            for(int uid:userId){
                UserInfoDto userInfo = QicDbUtil.queryQicDBSingleBean(findUserByIdSql, UserInfoDto.class, uid);
                if(userInfo!=null){
                    sendActivateMsg(userInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //从缓存中删除
        return flag;
    }


    /**
     * 用户授权 用户列表展示
     * @param userIdArray 用户ID数组
     * @return 用户名 账号 所属营业部
     */
    public static List<ActivateUserDto> getUserList(int[] userIdArray) {
        String selectedUserSql = SqlLoader.getSqlById("selectedUserSql");
        for(int item : userIdArray) {
            selectedUserSql += " a.id = " + item + " or";
        }
        if(selectedUserSql.endsWith("or")) {
            selectedUserSql = selectedUserSql.substring(0, selectedUserSql.length() - 3);
        }
        return QicDbUtil.queryQicDBBeanList(selectedUserSql, ActivateUserDto.class);
    }

    /**
     *
     * 用户授权 用户列表展示
     * @param startId 开始ID
     * @param endId  结束ID
     * @return 用户名 账号 所属营业部
     */
    public static List<ActivateUserDto> getUserList(int startId, int endId) {
        String selectMoreUsersql = SqlLoader.getSqlById("selectedMoreUsersql");
        return QicDbUtil.queryQicDBBeanList(selectMoreUsersql, ActivateUserDto.class, startId, endId);
    }

    //给已激活的账号的邮箱发送提示信息
    public static void sendActivateMsg(UserInfoDto userInfo) {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("UTF-8");// 编码格式
        email.setHostName("smtp.163.com");// smtp服务器
        try {
            email.addTo(userInfo.email);// 接收者
            email.setFrom("gta_qic@163.com", "超级管理员");// 发送者，姓名
            email.setSubject("账号激活通知");// 邮件标题
            email.setAuthentication("gta_qic@163.com", "gta123");// 用户名，密码
            String message= SystemConfigService.get("activateMsg");//得到模板类容
            MessageBuilder messageBuilder = new MessageBuilder(message);
            messageBuilder.addParameter("userInfo",userInfo);
            String inputVal = messageBuilder.execute();
            email.setMsg(inputVal);// 发送内容
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}