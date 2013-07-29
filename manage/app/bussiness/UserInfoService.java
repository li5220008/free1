package bussiness;

import business.UserInfoBaseService;
import com.google.common.base.Joiner;
import dbutils.QicDbUtil;
import dbutils.QsmDbUtil;
import dbutils.SqlLoader;
import dto.FunctionInfoDto;
import dto.RoleInfoDto;
import dto.UserInfoDto;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import play.Logger;
import play.db.DB;
import play.libs.Crypto;
import util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * User: 刘建力(liujianli@gtadata.com))
 * Date: 12-12-4
 * Time: 下午1:46
 * 功能描述: 用户信息相关业务逻辑处理
 */
public class UserInfoService extends UserInfoBaseService {
    /**
     * 根据用户ID查询用户相关信息
     *
     * @param uid 用户ID
     * @return 用户相关信息
     */
    public static UserInfoDto findUserInfoById(long uid) {

        String sql = SqlLoader.getSqlById("findUserInfoById");
        //1. 查询用户基本信息
        UserInfoDto userBaseInfo = QicDbUtil.queryQicDBSingleBean(sql, UserInfoDto.class, uid);
        //  userBaseInfo.functionInfoDto = getFuntionInfoTreeByUid(uid);
        return userBaseInfo;
    }

    /**
     * 根据用户ID查询 用户的菜单
     *
     * @param uid
     * @return
     */
    public static List<FunctionInfoDto> findUserFunctionInfoById(long uid) {


        String sql = SqlLoader.getSqlById("findUserFunctionInfoById");
        return QicDbUtil.queryQicDBBeanList(sql, FunctionInfoDto.class, uid);
    }

    /**
     * 根据用户ID 查询用户的角色信息
     *
     * @param uid
     * @return
     */
    public static List<RoleInfoDto> findUserRoleInfoById(long uid) {
        String sql = SqlLoader.getSqlById("findUserRoleInfoById");
        return QicDbUtil.queryQicDBBeanList(sql, RoleInfoDto.class, uid);
    }


    /**
     * 修改用户基本信息
     *
     * @param userInfoDto
     * @return
     */
    public static boolean updateUserInfo(UserInfoDto userInfoDto) {
        //如果用户没有输入密码则查出原来的密码  后期再改成在页面保存原密码
        String sql;
        Object[] params = null;
        //“用户所属部门”在页面上是可选项，为了能让update SQL成功执行 这里做一些特殊处理
        if("".equals(userInfoDto.saleDept.trim())){
            userInfoDto.saleDept = null;
         }
        if (null == userInfoDto.password || "".equals(userInfoDto.password.trim())) {
            //没修改密码的情况
            sql = SqlLoader.getSqlById("updateUserInfoWithoutPwd");
            params = new Object[]{
                    userInfoDto.name,
                    userInfoDto.account,
                    userInfoDto.phone,
                    userInfoDto.email,
                    userInfoDto.idCard,
                    userInfoDto.saleDept,
                    userInfoDto.address,
                    userInfoDto.postCode,
                    userInfoDto.capitalAccount,
                    userInfoDto.id
            };
            return QicDbUtil.updateQicDB(sql, params) > 0;

        } else {//修改了密码的情况
            params = new Object[]{
                    userInfoDto.name,
                    userInfoDto.account,
                    Crypto.passwordHash(userInfoDto.password),
                    userInfoDto.phone,
                    userInfoDto.email,
                    userInfoDto.idCard,
                    userInfoDto.saleDept,
                    userInfoDto.address,
                    userInfoDto.postCode,
                    userInfoDto.capitalAccount,
                    userInfoDto.id
            };
            sql = SqlLoader.getSqlById("updateUserInfo");
            return QicDbUtil.updateQicDB(sql, params) > 0;

        }


    }

    /**
     * 判断当前节点是否在用户的权限列表中
     *
     * @param list
     * @param id
     * @return
     */
    private static boolean isExist(List<FunctionInfoDto> list, long id) {
        if (id == FunctionService.TREE_ROOT_ID) {
            return true;
        } else {
            for (FunctionInfoDto tmp : list) {
                if (tmp.id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除不在用户权限列表中的菜单节点
     *
     * @param subList
     * @param functionInfoDtoList
     * @deprecated 算法有问题 不用了 以后有时间再解决
     */
    private static void filterUserTreeFromSystemTree(List<FunctionInfoDto> subList, List<FunctionInfoDto> functionInfoDtoList) {

        for (int i = 0; subList != null && i < subList.size(); ) {
            boolean isExist = isExist(functionInfoDtoList, subList.get(i).id);
            if (!isExist) {
                //找到没有权限的节点从树中移掉
                i = 0;//之前算法有问题 ，去掉list中的一个元素之后要重新归零 2012-12-10
                subList.remove(i);
            } else {
                //递归判断子节点是否有权限
                filterUserTreeFromSystemTree(subList.get(i).subs, functionInfoDtoList);
                ++i;
            }
        }

    }

    /**
     * 根据账号查找用户(账号在系统中是唯一的)
     *
     * @param account
     * @return
     */
    public static UserInfoDto findUserByAccount(String account) {

        String sql = SqlLoader.getSqlById("findUserByAccount");
        UserInfoDto userInfoDto = QicDbUtil.queryQicDBSingleBean(sql, UserInfoDto.class, account);
        return userInfoDto;
    }

    /**
     * 根据email查找用户(email在系统中是唯一的)
     *
     * @param email
     * @return
     */
    public static UserInfoDto findUserByEmail(String email) {

        String sql = SqlLoader.getSqlById("findUserByEmail");
        UserInfoDto userInfoDto = QicDbUtil.queryQicDBSingleBean(sql, UserInfoDto.class, email);
        return userInfoDto;
    }


    /**
     * 新建用户
     *
     * @param userInfo
     * @return
     */
    public static List<Long> addUser(UserInfoDto userInfo) {

        List<UserInfoDto> userInfoDtos = new ArrayList<UserInfoDto>();
        userInfoDtos.add(userInfo);
        List<Long> idlist = null;
        try {
            idlist = addUserBatch(userInfoDtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idlist;
        //QicDbUtil.updateQicDB(addusersql, userInfo.name, userInfo.account, userInfo.password, userInfo.phone, userInfo.email, userInfo.idcard, userInfo.saleDep.id, userInfo.capitalAccount, userInfo.address, userInfo.post, sdate, edate,UserInfoDto.UserStatus.WITHOUTACTIVITY.value);
    }

    /**
     * 删除用户
     *
     * @param ids 删除用户id数组s
     */
    public static void delUser(String[] ids) {
        if(ids==null){
            return;
        }
        long systemTime = System.currentTimeMillis();
        String delPre = "del";
        String deleteProductSql = SqlLoader.getSqlById("deleteUserProduct");
        String deleteTradeAccountSql = SqlLoader.getSqlById("deleteUserTradeAccount");
        String findRuntimeStrategySql = SqlLoader.getSqlById("findUserRuntimeStrategyByIds");
        String deleteRuntimeStrategySql = SqlLoader.getSqlById("deleteRuntimeStrategyByIds");
        String deleteStrategyProcductSql = SqlLoader.getSqlById("deleteUserStrategyProduct");
        String deleteUserStrategySql = SqlLoader.getSqlById("deleteUserStrategy");
        for (String uid : ids) {
            UserInfoDto userInfoDto = findUserInfoById(Long.parseLong(uid));
            String newAccount = delPre + userInfoDto.account + "_" + systemTime;
            String newEmail = delPre + userInfoDto.email + "_" + systemTime;
            String delusersql = "UPDATE qic_db.`user_info` a SET a.`status` = -100 , a.account = '" + newAccount + "' , a.email = '" + newEmail + "'  WHERE a.id = " + uid;
            QicDbUtil.updateQicDB(delusersql);
            //删除产品
            QicDbUtil.updateQicDB(deleteProductSql, uid);//删除产品
            QicDbUtil.updateQicDB(deleteUserStrategySql, uid);//删除产品
            QicDbUtil.updateQicDB(deleteTradeAccountSql, uid);//删除用户交易账号
            List<Long> relationIds = QicDbUtil.queryWithHandler(findRuntimeStrategySql, new ColumnListHandler<Long>(), uid);
            if(relationIds != null && relationIds.size()>0){
                Joiner joiner = Joiner.on(",");
                String idsString = joiner.join(relationIds);
                deleteRuntimeStrategySql = deleteRuntimeStrategySql.replace("#ids#", idsString);
                QsmDbUtil.updateQsmDB(deleteRuntimeStrategySql);
            }
            QicDbUtil.updateQicDB(deleteStrategyProcductSql, uid);

        }
    }


    /**
     * 用户状态修改
     *
     * @param ids    用户id数组
     * @param status 修改状态
     */
    public static void userStateModify(String[] ids, int status) {
        String sql = "UPDATE qic_db.`user_info` a SET a.`status` = ? WHERE a.`id` IN (";
        for (int i = 0; i < ids.length; i++) {
            if (i == ids.length - 1) {
                sql += ids[i];
            } else {
                sql += ids[i] + ",";
            }
        }
        sql += ")";
        QicDbUtil.updateQicDB(sql, status);
    }


    /**
     * 批量添加用户 insert ignore into tb......
     *
     * @param userInfos
     * @return 新增的用户数量
     */
    public static List<Long> addUserBatch(List<UserInfoDto> userInfos) throws Exception {
        if (null == userInfos || userInfos.size() == 0) {
            return new ArrayList<Long>(0);
        }
        String sql = SqlLoader.getSqlById("addUserBatch");
        Connection conn = DB.getDBConfig().getConnection();
        String[] columnNames = {"id"};
        PreparedStatement pstmt = conn.prepareStatement(sql, columnNames);
        for (UserInfoDto tmp : userInfos) {
            pstmt.setString(1, tmp.name);
            pstmt.setString(2, tmp.account);
            pstmt.setString(3, Crypto.passwordHash(tmp.password));
            pstmt.setString(4, tmp.phone);
            pstmt.setString(5, tmp.email);
            pstmt.setString(6, tmp.idCard);
            pstmt.setString(7, tmp.saleDept);
            pstmt.setString(8, tmp.address);
            pstmt.setString(9, tmp.postCode);
            pstmt.setString(10, tmp.capitalAccount);
            pstmt.setString(11, Constants.USER_SDATE);
            pstmt.setString(12, Constants.USER_EDATE);
            pstmt.setInt(13, UserInfoDto.UserStatus.WITHOUTACTIVITY.value);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        // conn.commit();
        List<Long> keys = new ArrayList<Long>(userInfos.size());
        //取得自动生成的主键值的结果集
        ResultSet rs = pstmt.getGeneratedKeys();
        while (rs.next()) {
            keys.add(rs.getLong(1));
            Logger.info(rs.getLong(1) + "");
        }
        return keys;
    }


    /**
     * 到期用户延期
     *
     * @param ids
     * @param delaydate
     */
    public static void userdelay(String[] ids, String delaydate) {

        /* SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format.parse(delaydate);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        String sql = "UPDATE qic_db.`user_info` a SET a.`edate`= ?  WHERE a.`id` IN (";
        for (int i = 0; i < ids.length; i++) {
            if (i == ids.length - 1) {
                sql += ids[i];
            } else {
                sql += ids[i] + ",";
            }

        }
        sql += ")";
        QicDbUtil.updateQicDB(sql, delaydate);
        System.out.println(sql);


    }

    public static UserInfoDto findUserByEmailExcludeSelf(String newEmail, String selfEmail) {
        String sql = SqlLoader.getSqlById("findUserByEmailExcludeSelf");
        UserInfoDto userInfoDto = QicDbUtil.queryQicDBSingleBean(sql, UserInfoDto.class, newEmail, selfEmail);
        return userInfoDto;
    }

    public static UserInfoDto findUserByAccountExcludeSelf(String newAccount, String selfAccount) {
        String sql = SqlLoader.getSqlById("findUserByAccountExcludeSelf");
        UserInfoDto userInfoDto = QicDbUtil.queryQicDBSingleBean(sql, UserInfoDto.class, newAccount, selfAccount);
        return userInfoDto;
    }


}
