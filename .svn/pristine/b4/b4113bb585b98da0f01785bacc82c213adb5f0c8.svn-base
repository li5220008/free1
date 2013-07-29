package dbutils;

import com.google.common.collect.Lists;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import play.db.DB;
import play.db.DBConfig;
import play.exceptions.DatabaseException;
import play.libs.F;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * qic数据库操作
 * User: wenzhihong
 * Date: 12-11-10
 * Time: 上午9:38
 */
public abstract class QicDbUtil {
    public static NameParameterFacade nameParameterFacade = new NameParameterFacade("qicdb");

    public static QueryRunner qicQueryRunner = new QueryRunner();

    //默认数据库配制名
    public static final String DB_CONF_NAME = DBConfig.defaultDbConfigName;

    //把数据库查询的行处理成 Map
    public static final RowProcessor MAP_ROW_PROCESSOR = new MapRowProcessor();

    public static final BeanProcessorWithModelId PLAY_BEAN_PROCESSOR = new BeanProcessorWithModelId();

    //处理一行. 加入处理playframework的model
    public static final RowProcessor ROW_PROCESSOR = new BasicRowProcessor(PLAY_BEAN_PROCESSOR);

    /**
     * 返回提取数据的数据库连接
     */
    public static Connection getQicDBConnection() {
        return DB.getDBConfig(DB_CONF_NAME, false).getConnection();
    }

    public static Connection getConnection() {
        return DB.getDBConfig(DB_CONF_NAME, false).getConnection();
    }

    /**
     * 在提取数据的数据库上执行sql. (一般是执行对数据库有更新的那种)
     */
    public static boolean execute4QicDB(String SQL) {
        return DB.getDBConfig(DB_CONF_NAME, false).execute(SQL);
    }

    /**
     * 在提取数据的数据库上执行sql语句(查询类)
     */
    public static ResultSet executeQuery4QicDB(String SQL) {
        return DB.getDBConfig(DB_CONF_NAME, false).executeQuery(SQL);
    }

    /**
     * 查询sql返回单个bean. 如果没有的话, 返回null
     */
    public static <T> T queryQicDBSingleBean(String sql, Class<T> cl, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<T> h = new BeanHandler<T>(cl, ROW_PROCESSOR);
        T t = null;
        try {
            t = qicQueryRunner.query(conn, sql, h, params);
            return t;
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询sql返回Bean list. 如果没有的话, 返回的list长度为0
     */
    public static <T> List<T> queryQicDBBeanList(String sql, Class<T> cl, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<List<T>> h = new BeanListHandler<T>(cl, ROW_PROCESSOR);
        try {
            return qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询单条记录, 转成一个map. 注意, 这里的map的key值为小写的
     */
    public static Map<String, Object> queryQicDBSingleMap(String sql, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<Map<String, Object>> h = new MapHandler(MAP_ROW_PROCESSOR);
        Map t = null;
        try {
            t = qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
        return t;
    }

    /**
     * 用handler处理查询记录
     */
    public static <T> T queryQicDbWithHandler(String sql, ResultSetHandler<T> rsh, Object... params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.query(conn, sql, rsh, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 用于count语句的.只查总数
     */
    public static Long queryQicDbCount(String sql, Object... params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.query(conn, sql, new ScalarHandler<Long>(), params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询多条记录, 转成list<map>. 注意, 这里的map的key值为小写的
     * 如果没有,则返回的list长度为0
     */
    public static List<Map<String, Object>> queryQicDBMapList(String sql, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler(MAP_ROW_PROCESSOR);
        try {
            return qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 批量执行sql语句
     *
     * @param sql
     * @param params 这里是一个二维数组, 第二维记录的是要操作的数据
     * @return 每次受影响的记录条数
     */
    public static int[] batchQicDB(String sql, Object[][] params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.batch(conn, sql, params);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    /**
     * 可以执行sql语句. insert, update, delete
     *
     * @return 受sql语句影响的记录条数
     */
    public static int updateQicDB(String sql, Object... param) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.update(conn, sql, param);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }


    //-----------------以下为上面方面的签名变化

    /**
     * 在提取数据的数据库上执行sql. (一般是执行对数据库有更新的那种)
     */
    public static boolean execute(String SQL) {
        return DB.getDBConfig(DB_CONF_NAME, false).execute(SQL);
    }

    /**
     * 在提取数据的数据库上执行sql语句(查询类)
     */
    public static ResultSet executeQuery(String SQL) {
        return DB.getDBConfig(DB_CONF_NAME, false).executeQuery(SQL);
    }

    /**
     * 查询sql返回单个bean. 如果没有的话, 返回null
     */
    public static <T> T querySingleBean(String sql, Class<T> cl, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<T> h = new BeanHandler<T>(cl, ROW_PROCESSOR);
        T t = null;
        try {
            t = qicQueryRunner.query(conn, sql, h, params);
            return t;
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询sql返回Bean list. 如果没有的话, 返回的list长度为0
     */
    public static <T> List<T> queryBeanList(String sql, Class<T> cl, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<List<T>> h = new BeanListHandler<T>(cl, ROW_PROCESSOR);
        try {
            return qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询单条记录, 转成一个map. 注意, 这里的map的key值为小写的
     */
    public static Map<String, Object> querySingleMap(String sql, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<Map<String, Object>> h = new MapHandler(MAP_ROW_PROCESSOR);
        Map t = null;
        try {
            t = qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
        return t;
    }

    /**
     * 用handler处理查询记录
     */
    public static <T> T queryWithHandler(String sql, ResultSetHandler<T> rsh, Object... params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.query(conn, sql, rsh, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 用于count语句的.只查总数
     */
    public static Long queryCount(String sql, Object... params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.query(conn, sql, new ScalarHandler<Long>(), params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 查询多条记录, 转成list<map>. 注意, 这里的map的key值为小写的
     * 如果没有,则返回的list长度为0
     */
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        Connection conn = getConnection();
        ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler(MAP_ROW_PROCESSOR);
        try {
            return qicQueryRunner.query(conn, sql, h, params);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        }
    }

    /**
     * 批量执行sql语句
     *
     * @param sql
     * @param params 这里是一个二维数组, 第二维记录的是要操作的数据
     * @return 每次受影响的记录条数
     */
    public static int[] batch(String sql, Object[][] params) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.batch(conn, sql, params);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    /**
     * 可以执行sql语句. insert, update, delete
     *
     * @param sql   sql语句
     * @param param sql语句里的参数
     * @return 受sql语句影响的记录条数
     */
    public static int update(String sql, Object... param) {
        Connection conn = getConnection();
        try {
            return qicQueryRunner.update(conn, sql, param);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    /**
     * 用于insert 语句. 返回自动增长的id值. 失败则返回 Long.MIN_VALUE
     *
     * @param sql
     * @param param
     * @return
     */
    public static long insert(String sql, Object... param) {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            qicQueryRunner.fillStatement(pstmt, param);
            pstmt.executeUpdate();
            //返回自增加id
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return Long.MIN_VALUE;
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }

    /**
     * 批量插入. 返回每条语句的的自动增长id
     *
     * @param sql
     * @param params
     * @return
     */
    public static List<Long> batchInsert(String sql, Object[][] params) {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                qicQueryRunner.fillStatement(pstmt, params[i]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            rs = pstmt.getGeneratedKeys();
            List<Long> idLists = Lists.newLinkedList();
            while (rs.next()) {
                idLists.add(rs.getLong(1));
            }
            return idLists;
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage(), ex);
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pstmt);
        }
    }

    //-----------------以下处理含sql占位符的sql

    /**
     * 查询sql返回单个bean. 如果没有的话, 返回null
     */
    public static <T> T querySingleBeanWithNameParam(String nameParaSql, Class<T> cl, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return querySingleBean(t2._1, cl, t2._2);
    }

    public static <T> T querySingleBeanWithNameParam(String nameParaSql, Class<T> cl, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return querySingleBean(t2._1, cl, t2._2);
    }

    /**
     * 查询sql返回Bean list. 如果没有的话, 返回的list长度为0
     */
    public static <T> List<T> queryBeanListWithNameParam(String nameParaSql, Class<T> cl, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return queryBeanList(t2._1, cl, t2._2);
    }

    /**
     * 查询sql返回Bean list. 如果没有的话, 返回的list长度为0
     */
    public static <T> List<T> queryBeanListWithNameParam(String nameParaSql, Class<T> cl, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return queryBeanList(t2._1, cl, t2._2);
    }

    /**
     * 查询单条记录, 转成一个map. 注意, 这里的map的key值为小写的
     */
    public static Map<String, Object> querySingleMapWithNameParam(String nameParaSql, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return querySingleMap(t2._1, t2._2);
    }

    /**
     * 查询单条记录, 转成一个map. 注意, 这里的map的key值为小写的
     */
    public static Map<String, Object> querySingleMapWithNameParam(String nameParaSql, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return querySingleMap(t2._1, t2._2);
    }

    /**
     * 用handler处理查询记录
     */
    public static <T> T queryWithHandlerWithNameParam(String nameParaSql, ResultSetHandler<T> rsh, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return queryWithHandler(t2._1, rsh, t2._2);
    }

    /**
     * 用handler处理查询记录
     */
    public static <T> T queryWithHandlerWithNameParam(String nameParaSql, ResultSetHandler<T> rsh, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return queryWithHandler(t2._1, rsh, t2._2);
    }

    /**
     * 用于count语句的.只查总数
     */
    public static Long queryCountWithNameParam(String nameParaSql, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return queryCount(t2._1, t2._2);
    }

    /**
     * 用于count语句的.只查总数
     */
    public static Long queryCountWithNameParam(String nameParaSql, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return queryCount(t2._1, t2._2);
    }


    /**
     * 查询多条记录, 转成list<map>. 注意, 这里的map的key值为小写的
     * 如果没有,则返回的list长度为0
     */
    public static List<Map<String, Object>> queryMapListWithNameParam(String nameParaSql, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return queryMapList(t2._1, t2._2);
    }

    /**
     * 查询多条记录, 转成list<map>. 注意, 这里的map的key值为小写的
     * 如果没有,则返回的list长度为0
     */
    public static List<Map<String, Object>> queryMapListWithNameParam(String nameParaSql, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return queryMapList(t2._1, t2._2);
    }


    /**
     * 可以执行sql语句. insert, update, delete
     *
     * @return 受sql语句影响的记录条数
     */
    public static int updateWithNameParam(String nameParaSql, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return update(t2._1, t2._2);
    }

    /**
     * 可以执行sql语句. insert, update, delete
     *
     * @return 受sql语句影响的记录条数
     */
    public static int updateWithNameParam(String nameParaSql, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return update(t2._1, t2._2);
    }

    /**
     * 用于insert 语句. 返回自动增长的id值. 失败则返回 Long.MIN_VALUE
     */
    public static long insertWithNameParam(String nameParaSql, Object paramObj) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramObj);
        return insert(t2._1, t2._2);
    }

    /**
     * 用于insert 语句. 返回自动增长的id值. 失败则返回 Long.MIN_VALUE
     */
    public static long insertWithNameParam(String nameParaSql, Map<String, ?> paramMap) {
        F.T2<String, Object[]> t2 = nameParameterFacade.getSqlAndParameters(nameParaSql, paramMap);
        return insert(t2._1, t2._2);
    }

}
