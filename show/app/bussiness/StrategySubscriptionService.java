package bussiness;

import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.StrategySearchCnd;
import models.common.StrategyBaseDto;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.libs.F;
import util.CommonUtils;
import util.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * User: weiguili(li5220008@163.com)
 * Date: 12-12-18
 * Time: 上午11:21
 */
public class StrategySubscriptionService {
    /**
     * 订阅策略列表
     * @param myselect 分类
     * @param content 关键字
     * @param pageNo 页码
     * @param uid 用户ID
     * @return
     */
    public static F.T2<List<StrategyBaseDto>,Page> strategyList(int myselect, String content, int pageNo,Long uid){
        String sql = SqlLoader.getSqlById("strategyUserListSql");
        sql = sql.replaceAll("#user_type_table#", "user_strategy_order");
        sql = sql.replace("#fieldList#", "1");
        StringBuilder querySql = new StringBuilder(sql);
        List<Object> queryList = new ArrayList<Object>();
        queryList.add(uid);
        //关键字过滤
        if(StringUtils.isNotBlank(content)){
            querySql.append(" AND (sname like ? OR provider like ? )\n");
            queryList.add("%"+content+"%");
            queryList.add("%"+content+"%");
        }

        //排序
        if(myselect ==2){
            querySql.append(" ORDER BY collectCount DESC\n");
        }else if(myselect == 3){
            querySql.append(" ORDER BY upTime DESC\n");
        }else{
            querySql.append(" ORDER BY yield DESC\n");//默认排序
        }

        StringBuilder countSql = new StringBuilder("select count(*) from (\n" + querySql + "\n) distTable ");
        //获得总记录
        Long total = QicDbUtil.queryQicDbCount(countSql.toString(), queryList.toArray());
        //实例分页类
        Page page = new Page(total.intValue(),pageNo);
        querySql.append(" limit ?,?\n");
        queryList.add(page.beginIndex);
        queryList.add(page.pageSize);
        List<StrategyBaseDto> sbList = QicDbUtil.queryQicDBBeanList(querySql.toString(), StrategyBaseDto.class, queryList.toArray());

        return F.T2(sbList,page);
    }

    /**
     * 高级搜索
     *
     * @param cnd 搜索DTO
     * @param pageNo 当前页
     * @return _1 为结果集, _2为总条数, _3 总共页数
     */
    public static F.T2<List<StrategyBaseDto>, Page> advanceSearch(StrategySearchCnd cnd, int myselect,int pageNo,Long uid) {
        String sql = SqlLoader.getSqlById("strategyUserListSql");
        sql = sql.replaceAll("#user_type_table#", "user_strategy_order");
        sql = sql.replace("#fieldList#", "1");

        StringBuilder listSelectSql = new StringBuilder("select * from (\n" + sql + "\n) distTable where 1=1 ");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable where 1=1 ");
        StringBuilder where = new StringBuilder();
        if (cnd.tradeType != null && cnd.tradeType != CommonUtils.SELECT_ALL_OPTION_VALUE) {
            where.append(" and stype = " + cnd.tradeType.intValue());
        }

        if (cnd.tradeVariety != null && cnd.tradeVariety != CommonUtils.SELECT_ALL_OPTION_VALUE) {
            where.append(" and tradeVariety = " + cnd.tradeVariety.intValue()).append('\n');
        }

        if (cnd.yieldDown != null) {
            where.append(" and yield >= " + cnd.yieldDown / 100).append('\n');
        }

        if (cnd.yieldUp != null) {
            where.append(" and yield <= " + cnd.yieldUp / 100).append('\n');
        }

        if (cnd.profitRatioDown != null) {
            where.append(" and profitRatio >= " + cnd.profitRatioDown).append('\n');
        }

        if (cnd.profitRatioUp != null) {
            where.append(" and profitRatio <= " + cnd.profitRatioUp).append('\n');
        }

        if (cnd.starDown != null) {
            where.append(" and starLevel >= " + cnd.starDown).append('\n');
        }

        if (cnd.starUp != null) {
            where.append(" and starLevel <= " + cnd.starUp).append('\n');
        }

        if (myselect == 3) {
            where.append(" ORDER BY upTime DESC ");
        } else if (myselect == 2) {
            where.append(" ORDER BY collectCount DESC ");
        } else {
            where.append(" ORDER BY yield DESC ");
        }

        if (Logger.isDebugEnabled()) {
            Logger.debug("sql where ==" + where.toString());
        }

        listSelectSql.append(where);
        coutSql.append(where);

        Long total = QicDbUtil.queryQicDbWithHandler(coutSql.toString(),new ScalarHandler<Long>(),uid);

        Page page = new Page(total.intValue(), pageNo);
        listSelectSql.append("\n limit " + page.beginIndex + "," + page.pageSize + "\n");
        List<StrategyBaseDto> dtoList = QicDbUtil.queryQicDBBeanList(listSelectSql.toString(), StrategyBaseDto.class,uid);
        return F.T2(dtoList, page);
    }
}
