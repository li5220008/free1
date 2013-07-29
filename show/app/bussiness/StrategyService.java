package bussiness;

import business.StrategyBaseService;
import com.google.common.collect.Lists;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.StrategyOrderDto;
import dto.StrategySearchCnd;
import models.common.StrategyBaseDto;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.libs.F;
import util.CommonUtils;
import util.Page;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 策略超市基本信息展示业务方法
 * User: liangbing
 * Date: 12-11-9
 * Time: 上午9:10
 * To change this template use File | Settings | File Templates.
 */
public class StrategyService extends StrategyBaseService{

    /**
     * @param myselect 分类搜索
     * @param content  关键字
     * @param pageNo   当前页数
     * @return  _1 为结果集, _2为 分页page信息,
     */
    public static F.T2<List<StrategyBaseDto>, Page> strategyList(int myselect, String content, int pageNo) {
        String sqlList = SqlLoader.getSqlById("strategyListSql");

        List<StrategyBaseDto> sbdList = null;
        String condition = "";

        //content 关键字查询内容
        if (StringUtils.isNotBlank(content)) {
            condition = " AND  (sb.`name` like ? OR sb.`provider` like ?) ";
        }
        if (myselect == 0) {//分类查询,默认按收益率排行 排序
            if (content != null && content != "") {
                sqlList += condition;
            }
            sqlList += " AND sb.up_time < DATE_ADD(NOW(), INTERVAL - 3 DAY)  ORDER BY yield DESC ";

        } else if (myselect == 1) {//myselect = 1按收益率排行 排序
            if (content != null && content != "") {
                sqlList += condition;
            }
            sqlList += " AND sb.up_time < DATE_ADD(NOW(), INTERVAL - 3 DAY)  ORDER BY yield DESC ";

        } else if (myselect == 2) {//myselect =2 按人气排行 排序
            if (content != null && content != "") {
                sqlList += condition;
            }
            sqlList += " ORDER BY collectCount DESC ";

        } else {//按最新 排序
            if (content != null && content != "") {
                sqlList += condition;
            }
            sqlList += " ORDER BY upTime DESC ";

        }
        Long total;
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sqlList + "\n) distTable where 1=1 \n");

        if (StringUtils.isNotBlank(content)) {
              total = QicDbUtil.queryQicDbCount(coutSql.toString(),"%" + content + "%", "%" + content + "%");
        } else {
            total =QicDbUtil.queryQicDbCount(coutSql.toString());
        }
        Page page = new Page(total.intValue(), pageNo);
        sqlList += " limit " + page.beginIndex + "," + page.pageSize + "\n";
        if (StringUtils.isNotBlank(content)) {
            sbdList = QicDbUtil.queryQicDBBeanList(sqlList, StrategyBaseDto.class, "%" + content + "%", "%" + content + "%");
        } else {
            sbdList = QicDbUtil.queryQicDBBeanList(sqlList, StrategyBaseDto.class);
        }
        return F.T2(sbdList, page);

    }


    /**
     * 高级搜索
     *
     * @param cnd
     * @param pageNo 当前页
     * @return _1 为结果集, _2为总条数, _3 总共页数
     */
    public static F.T2<List<StrategyBaseDto>, Page> advanceSearch(StrategySearchCnd cnd,int myselect, int pageNo) {
        String sql = SqlLoader.getSqlById("strategyListSql");
        StringBuilder listSelectSql = new StringBuilder("select * from (\n" + sql + "\n) distTable where 1=1 \n");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable where 1=1 \n");
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
            where.append(" and profitRatio >= " + cnd.profitRatioDown / 100).append('\n');
        }

        if (cnd.profitRatioUp != null) {
            where.append(" and profitRatio <= " + cnd.profitRatioUp / 100).append('\n');
        }

        if (cnd.starDown != null) {
            where.append(" and starLevel >= " + cnd.starDown).append('\n');
        }

        if (cnd.starUp != null) {
            where.append(" and starLevel <= " + cnd.starUp).append('\n');
        }

        if(myselect==3){
           where.append(" ORDER BY upTime DESC ");
        }else if(myselect==2){
            where.append(" ORDER BY collectCount DESC ");
        }else{
            where.append(" AND upTime < DATE_ADD(NOW(), INTERVAL - 3 DAY)  ORDER BY yield DESC ");
        }

        if (Logger.isDebugEnabled()) {
            Logger.debug("sql where ==" + where.toString());
        }

        listSelectSql.append(where);
        coutSql.append(where);

        Long total=QicDbUtil.queryQicDbWithHandler(coutSql.toString(), new ScalarHandler<Long>());
        Page page = new Page(total.intValue(), pageNo);
        listSelectSql.append("\n limit " + page.beginIndex + "," + page.pageSize + "\n");
        List<StrategyBaseDto> dtoList = QicDbUtil.queryQicDBBeanList(listSelectSql.toString(), StrategyBaseDto.class);

        return F.T2(dtoList, page);
    }
    public  static  F.T2<List<StrategyBaseDto>, Page> findStrategysByUser(Map<String,String> queryParams){
            //查询sql
            StringBuffer querySql = new StringBuffer(SqlLoader.getSqlById("findStrategysByUser"));
            //计算总数sql
            StringBuffer countSql = new StringBuffer();
            countSql.append("SELECT \n").append("COUNT(1)\n").append("FROM (\n");
             countSql.append(SqlLoader.getSqlById("countOfStrategysByUser"));
            String keyWord = queryParams.get("keyword");
            String orderCol = queryParams.get("orderCol") == null?"1":queryParams.get("orderCol");
            int pageNo = Integer.valueOf(queryParams.get("pageNo"));
            int status = Integer.valueOf(queryParams.get("status"));
            int uid = Integer.valueOf(queryParams.get("uid"));
            int orderByType = queryParams.get("orderType")==null?0:Integer.valueOf(queryParams.get("orderType"));
            List<Object> queryList = new ArrayList<Object>(4);
            List<Object> countList = new ArrayList<Object>(2);
            queryList.add(uid);
            countList.add(uid);
            Long totalSize = 0L;

           if(status==StrategyBaseDto.StrategyStatus.DOWNSHELF.value || status==StrategyBaseDto.StrategyStatus.DELETED.value){//状态查询
                querySql.append(" and status=?\n");
                countSql.append(" and status=?\n");
                queryList.add(status);
                countList.add(status);
            }else if(status == StrategyBaseDto.StrategyStatus.UPSHELF.value){
               querySql.append(" and status=?  or status=?\n");
               countSql.append(" and status=?  or status=?\n");
               queryList.add(StrategyBaseDto.StrategyStatus.UPSHELF.value);
               countList.add(StrategyBaseDto.StrategyStatus.UPSHELF.value);
               queryList.add(StrategyBaseDto.StrategyStatus.WAITINGUPSHELF.value);
               countList.add(StrategyBaseDto.StrategyStatus.WAITINGUPSHELF.value);
           }
           else if(status==-2){//查询审核中的 此处的审核中包含 沙箱测试(2) 回测中(3) 回测试失败(8) 审核中(1) 四个状态
                querySql.append(" and (status >0 and status<? or status=? or status=?)\n");
                countSql.append(" and (status >0 and status<? or status=? or status=?)\n");
                queryList.add(StrategyBaseDto.StrategyStatus.UPSHELF.value);
                countList.add(StrategyBaseDto.StrategyStatus.UPSHELF.value);
                queryList.add(StrategyBaseDto.StrategyStatus.FINISHTEST.value);
                countList.add(StrategyBaseDto.StrategyStatus.FINISHTEST.value);
                queryList.add(StrategyBaseDto.StrategyStatus.BACKTESTINGFAILER.value);
                countList.add(StrategyBaseDto.StrategyStatus.BACKTESTINGFAILER.value);
            }
            if(keyWord !=null &&!"".equals(keyWord)){//关键查询
                //这里注意 like需要使用预编译的时候 不用写成:querySql.append(" and (provider like ? or name like '%?%' )\n");
                //%号必需在set的时候进行拼接
                querySql.append(" and (provider like ? or name like ? )\n");
                countSql.append(" and (provider like ?  or name like  ? )\n");
                queryList.add("%"+keyWord+"%");
                queryList.add("%"+keyWord+"%");
                countList.add("%"+keyWord+"%");
                countList.add("%"+keyWord+"%");
            }

        /**
         * 这里加入三个查询条件
         */

        int tradeType = Integer.valueOf(queryParams.get("tradeType"));//交易类型
        if(tradeType>0){
            querySql.append(" AND sb.trade_type=?\n");
            countSql.append(" AND sb.trade_type=?\n");
            queryList.add(tradeType);
            countList.add(tradeType);
        }
        int tradeVariety = Integer.valueOf(queryParams.get("tradeVariety"));//交易品种
        if(tradeVariety>0){
            querySql.append(" AND sb.trade_variety=?\n");
            countSql.append(" AND sb.trade_variety=?\n");
            queryList.add(tradeVariety);
            countList.add(tradeVariety);
        }
        int strategyLanguage = Integer.valueOf(queryParams.get("strategyLanguage"));//策略语言
        if(strategyLanguage>0){
            querySql.append(" AND sb.enginetype_id=?\n");
            countSql.append(" AND sb.enginetype_id=?\n");
            queryList.add(strategyLanguage);
            countList.add(strategyLanguage);
        }




            //排序,把order by 的字段出来
            querySql.append(" order by "+getColNameByIndex(orderCol)+ (orderByType == 0?" ASC":" DESC")+" \n");
            countSql.append("\n) tmp");
            totalSize = QicDbUtil.queryQicDbCount(countSql.toString(),countList.toArray());
            Page page = new Page(totalSize.intValue(), pageNo);
            querySql.append(" limit ?,? ");
            queryList.add(page.beginIndex);
            queryList.add(page.pageSize);

            List<StrategyBaseDto> strategyBaseDtoList = QicDbUtil.queryQicDBBeanList(querySql.toString(),StrategyBaseDto.class,queryList.toArray());
            return F.T2(strategyBaseDtoList,page);
    }
    private static String getColNameByIndex(String index){
        switch (Integer.valueOf(index)){
            case 1:
                return "sb.name";
            case 2:
                return "sb.provider";
            case 3:
                return "sb.status";
            case 4:
                return "sb.upload_time";
            case 5:
                return "sb.pass_time";
            case 6:
                return "sb.up_time";
            case 7:
                return "sb.trade_type";
            case 8:
                return "sb.order_count";
            case 9:
                return "uso.validCount";
            case 10:
                return "sb.collect_count";
            case 11:
                return "sb.trade_variety";
            case 12:
                return "sb.enginetype_id";
            default:
                return "sb.status";




        }

    }


    public static List<F.T2<StrategyBaseDto, StrategyOrderDto>> userOrderStrateList(long uid) {
        String sql = SqlLoader.getSqlById("strategyUserListSql");
        sql = sql.replaceAll("#user_type_table#", "user_strategy_order");
        sql = sql.replace("#fieldList#", "ust.id AS orderId, ust.id AS orderId, ust.order_etime AS order_etime, ust.order_stime AS order_stime, ust.stid AS stid");

        return QicDbUtil.queryQicDbWithHandler(sql, new ResultSetHandler<List<F.T2<StrategyBaseDto, StrategyOrderDto>>>() {
            @Override
            public List<F.T2<StrategyBaseDto, StrategyOrderDto>> handle(ResultSet rs) throws SQLException {
                RowProcessor convert = QicDbUtil.ROW_PROCESSOR;
                List<F.T2<StrategyBaseDto, StrategyOrderDto>> list = Lists.newLinkedList();
                while (rs.next()){
                    StrategyBaseDto strategyBaseDto = convert.toBean(rs, StrategyBaseDto.class);
                    StrategyOrderDto strategyOrderDto = QicDbUtil.PLAY_BEAN_PROCESSOR.toBeanWithField(rs, StrategyOrderDto.class, "order_etime", "order_stime", "stid");
                    list.add(F.T2(strategyBaseDto, strategyOrderDto));
                }
                return list;
            }
        }, uid);
    }


}
