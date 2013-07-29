package bussiness;

import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.FunctionInfoDto;

import java.util.List;

/**
 * User: 刘建力(liujianli@gtadata.com))
 * Date: 12-12-5
 * Time: 上午9:39
 * 功能描述:  获取系统树
 */
public class FunctionService {
    private static List<FunctionInfoDto> list = null;
    public static int TREE_ROOT_ID = 1;

    static {
        reload();
    }


    public static List<FunctionInfoDto> getAllSystemFunctions() {
        reload();//暂时先每次都加载....
        return list;
    }

    private static void findAll() {
        String sql = SqlLoader.getSqlById("findAll");
        list = QicDbUtil.queryQicDBBeanList(sql, FunctionInfoDto.class);
    }

    public static List<FunctionInfoDto> reload() {
        findAll();
        return list;
    }
}
