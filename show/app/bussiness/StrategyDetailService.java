package bussiness;

import dbutils.QicDbUtil;
import dbutils.QicoreDbUtil;
import dbutils.SqlLoader;
import dto.*;
import models.common.QiaIndicatorDto;
import models.common.IndicatorDto;
import models.common.StrategyPositionDto;
import models.common.ExecutionRecordDto;
import models.common.AuthorizeRecordDto;
import models.common.StrategyBaseDto;
import models.common.StrategyBaseinfo;
import models.common.StrategyDailyYieldDto;
import models.common.UserStrategyCollect;
import org.apache.commons.lang.time.DateUtils;
import play.Logger;
import play.libs.F;
import util.CommonUtils;
import util.Page;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 策略详情
 * User: panzhiwei
 * Date: 12-11-10
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class StrategyDetailService {
    //策略基本信息
    public static StrategyBaseDto getStrategyDetail(long stid) {
        String sql = SqlLoader.getSqlById("getStrategyBaseInfo");
        StrategyBaseDto strategyBaseDto = QicDbUtil.queryQicDBSingleBean(sql,StrategyBaseDto.class,stid);
        if(strategyBaseDto.discussCount == 0)
            strategyBaseDto.starLevel = 0;
        else
            strategyBaseDto.starLevel = (float)strategyBaseDto.discussTotal / strategyBaseDto.discussCount;
        return strategyBaseDto;
    }

    //策略持仓
    public static List<StrategyPositionDto> getStrategyPosition(long stid, int pageNo) {
        String strategy_baseinfo_sql = SqlLoader.getSqlById("strategy_baseinfo");
        StrategyBaseinfo strategyBaseinfo = QicDbUtil.queryQicDBSingleBean(strategy_baseinfo_sql, StrategyBaseinfo.class, stid);

        String sql = SqlLoader.getSqlById("StrategyPosition");
        /*StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicDbUtil.queryQicDbCount(coutSql.toString(),strategyBaseinfo.stUuid,strategyBaseinfo.lookbackEtime);
        Page page = new Page(total.intValue(), pageNo);
        if (pageNo * page.pageSize > total) {
            return null;
        }
        sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";*/
        List<StrategyPositionDto> strategyPositionDtoList = QicoreDbUtil.queryQicoreDBBeanList(sql, StrategyPositionDto.class, strategyBaseinfo.stUuid,strategyBaseinfo.stUuid);
        if (strategyPositionDtoList != null) {
            for (StrategyPositionDto sp : strategyPositionDtoList) {
                sp.name = strategyBaseinfo.name;
                sp.trade_variety = strategyBaseinfo.tradeVariety;
            }
        } else {
            List<StrategyPositionDto> strategyPositionDtoList1 = new ArrayList<StrategyPositionDto>();
            strategyPositionDtoList = strategyPositionDtoList1;
        }
        return strategyPositionDtoList;
    }

    //委托记录
    public static List<AuthorizeRecordDto> getAuthorizeRecord(long stid, int pageNo) {
        String strategy_baseinfo_sql = SqlLoader.getSqlById("strategy_baseinfo");
        StrategyBaseinfo strategyBaseinfo = QicDbUtil.queryQicDBSingleBean(strategy_baseinfo_sql, StrategyBaseinfo.class, stid);

        String sql = SqlLoader.getSqlById("AuthorizeRecord");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicoreDbUtil.queryQicDbCount(coutSql.toString(),strategyBaseinfo.stUuid,strategyBaseinfo.lookbackEtime);
        Page page = new Page(total.intValue(), pageNo);

        sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";
        List<AuthorizeRecordDto> authorizeRecordDtoList = QicoreDbUtil.queryQicoreDBBeanList(sql, AuthorizeRecordDto.class, strategyBaseinfo.stUuid,strategyBaseinfo.lookbackEtime);
        if (authorizeRecordDtoList != null) {
            for (AuthorizeRecordDto ar : authorizeRecordDtoList) {
                ar.name = strategyBaseinfo.name;
                ar.trade_variety = strategyBaseinfo.tradeVariety;
            }
        } else {
            List<AuthorizeRecordDto> authorizeRecordDtoList1 = new ArrayList<AuthorizeRecordDto>();
            authorizeRecordDtoList = authorizeRecordDtoList1;
        }
        return authorizeRecordDtoList;
    }

    //成交记录
    public static List<ExecutionRecordDto> getExecutionRecord(Long stid, int pageNo) {
        String strategy_baseinfo_sql = SqlLoader.getSqlById("strategy_baseinfo");
        StrategyBaseinfo strategyBaseinfo = QicDbUtil.queryQicDBSingleBean(strategy_baseinfo_sql, StrategyBaseinfo.class, stid);

        String sql = SqlLoader.getSqlById("ExecutionRecord");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicoreDbUtil.queryQicDbCount(coutSql.toString(),strategyBaseinfo.stUuid,strategyBaseinfo.lookbackEtime);
        Page page = new Page(total.intValue(), pageNo);

        sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";

        List<ExecutionRecordDto> executionRecordDtoList = QicoreDbUtil.queryQicoreDBBeanList(sql, ExecutionRecordDto.class, strategyBaseinfo.stUuid,strategyBaseinfo.lookbackEtime);
        if (executionRecordDtoList != null) {
            for (ExecutionRecordDto er : executionRecordDtoList) {
                er.name = strategyBaseinfo.name;
                er.trade_variety = strategyBaseinfo.tradeVariety;
            }
        } else {
            List<ExecutionRecordDto> executionRecordDtoList1 = new ArrayList<ExecutionRecordDto>();
            executionRecordDtoList = executionRecordDtoList1;
        }
        return executionRecordDtoList;
    }

    //根据策略id查询是否被收藏
    public static boolean iscollect(long stid, long uid) {
        UserStrategyCollect userStrategyCollect = UserStrategyCollect.find("byStidAndUid", stid, uid).first();
        if (userStrategyCollect == null)
            return false;
        else
            return true;

    }

    //策略交易简单信息展示
    public static StrategyBaseDto getstratebasicinfo(long stid) {
        StrategyBaseinfo strategy = StrategyBaseinfo.findById(stid);
        if(strategy == null){
            StrategyBaseinfo strategy1 = new StrategyBaseinfo();
            strategy = strategy1;
        }
        StrategyBaseDto strategyBaseDto = new StrategyBaseDto();
        strategyBaseDto.sname = strategy.name;
        strategyBaseDto.stype = strategy.tradeType;
        strategyBaseDto.upTime = strategy.upTime;
        strategyBaseDto.collectCount = strategy.collectCount;
        if(strategy.discussCount == 0)
            strategyBaseDto.starLevel = 0;
        else
            strategyBaseDto.starLevel = (float)strategy.discussTotal / strategy.discussCount;
        return strategyBaseDto;
    }

    //绩效指标
    public static IndicatorDto getindicator(long stid,int type) {
        String sql = SqlLoader.getSqlById("Indicator");
        IndicatorDto indicator = QicDbUtil.queryQicDBSingleBean(sql, IndicatorDto.class, stid,type);
      /*  if(indicator == null){
            IndicatorDto indicatorDto = new IndicatorDto();
            indicator = indicatorDto;
        }*/
        return indicator;
    }

    //策略委托信号
    public static List<AuthorizeRecordDto> getorder_signal(long stid) {
        String strategy_baseinfo_sql = SqlLoader.getSqlById("strategy_baseinfo");
        StrategyBaseinfo strategyBaseinfo = QicDbUtil.queryQicDBSingleBean(strategy_baseinfo_sql,StrategyBaseinfo.class,stid);
        String sql = SqlLoader.getSqlById("order_signal");
        List<AuthorizeRecordDto> ordersignallist = QicoreDbUtil.queryQicoreDBBeanList(sql, AuthorizeRecordDto.class, strategyBaseinfo.stUuid,strategyBaseinfo.upTime, CommonUtils.getFormatDate("yyyy-MM-dd", new Date()));
        if (ordersignallist != null) {
            for (AuthorizeRecordDto ar : ordersignallist) {
                ar.name = strategyBaseinfo.name;
                ar.trade_variety = strategyBaseinfo.tradeVariety;
            }
        } else {
            List<AuthorizeRecordDto> authorizeRecordDtoList1 = new ArrayList<AuthorizeRecordDto>();
            ordersignallist = authorizeRecordDtoList1;
        }
        return ordersignallist;
    }

    /**
     * 加入订阅
     * @param month
     * @param uid
     * @param stid
     */
    public static Date addstrategyorder(int month, Long uid, Long stid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getdowntime_sql = SqlLoader.getSqlById("getdowntime_sql");//得到下架时间
        Map<String,Object> map = QicDbUtil.queryQicDBSingleMap(getdowntime_sql,stid);
        String downtime = "";
        if(map.get("down_time") == null){
            downtime = "2099-12-12";
        }
        else {
            downtime = map.get("down_time").toString();
        }

        Date downtime2 = new Date();
        try {
            downtime2  = sdf.parse(downtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //订阅结束时间
        Date date = new Date();
        Date newDate = DateUtils.addMonths(date, month);

        long l = newDate.getTime() - downtime2.getTime();
        long day = l/(24*60*60*1000);
        long hour = (l/(60*60*1000)-24*day);
        long min = (l/(60*1000)-day*24*60-hour*60);

        //插入订阅时间
        if (min <=0 ){
            String insert_orderetime_sql = SqlLoader.getSqlById("insert_downtime");
            QicDbUtil.updateQicDB(insert_orderetime_sql, uid, stid,newDate);
            String add_sumOrderCount_sql = SqlLoader.getSqlById("add_sumOrderCount_sql");
            QicDbUtil.updateQicDB(add_sumOrderCount_sql,stid);
            return newDate;
        }
        else {
            return null;
        }
    }

    /**
     * 续订
     * @param month
     * @param uid
     * @param stid
     * @return
     */
    public static Date delaystrategyorder(int month, Long uid, Long stid){
        //得到下架时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getdowntime_sql = SqlLoader.getSqlById("getdowntime_sql");
        Map<String,Object> map = QicDbUtil.queryQicDBSingleMap(getdowntime_sql,stid);
        String downtime = "";
        if(map.get("down_time") == null){
            downtime = "2099-12-12";
        }
        else {
            downtime = map.get("down_time").toString();
        }

        Date downtime2 = new Date();
        try {
            downtime2  = sdf.parse(downtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String get_orderetime_sql = SqlLoader.getSqlById("get_orderetime_sql");
        StrategyOrderDto strategyorder = QicDbUtil.queryQicDBSingleBean(get_orderetime_sql, StrategyOrderDto.class, uid, stid);
        //订阅结束时间
        Date newDate = DateUtils.addMonths(strategyorder.order_etime, month);

        long l =  newDate.getTime() - downtime2.getTime();
        long day = l/(24*60*60*1000);
        long hour = (l/(60*60*1000)-24*day);
        long min = (l/(60*1000)-day*24*60-hour*60);
        //更新订阅时间
        if(min <=0 ){
            String update_orderetime_sql = SqlLoader.getSqlById("update_downtime");
            QicDbUtil.updateQicDB(update_orderetime_sql, newDate, uid, stid);
            return newDate;
        }
        else {
            return null;
        }

    }

    /**
     * 判断是否订阅
     * @param uid
     * @param stid
     * @return
     */
    public static boolean isorder(Long uid, Long stid){
        String isorder_sql = SqlLoader.getSqlById("isorder_sql");
        StrategyOrderDto strategy = QicDbUtil.queryQicDBSingleBean(isorder_sql,StrategyOrderDto.class,uid,stid);
        if(strategy != null){
           return true;
        }
        else {
            return false;
        }

    }


    //QIA 绩效指标
    public static QiaIndicatorDto getQiaIndicatorDto(long stid,int type) {
        String sql = SqlLoader.getSqlById("qiaIndicatorSql");
        QiaIndicatorDto indicator = QicDbUtil.queryQicDBSingleBean(sql, QiaIndicatorDto.class, stid,type);
       /* if(indicator == null){
            indicator = new QiaIndicatorDto();
        }*/
        return indicator;
    }


    //QIA 策略持仓
    public static List<StrategyPositionDto> getQiaPosition(long stid, int pageNo) {
        String sql = SqlLoader.getSqlById("qiaPosition");
        /*StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicDbUtil.queryQicDbCount(coutSql.toString(),stid);
        Page page = new Page(total.intValue(), pageNo);
        if (pageNo * page.pageSize > total) {
            return null;
        }*/
        //sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";
        List<StrategyPositionDto> strategyPositionDtoList = QicDbUtil.queryQicDBBeanList(sql, StrategyPositionDto.class,stid,stid);
        if (strategyPositionDtoList == null) {
            strategyPositionDtoList = new ArrayList<StrategyPositionDto>();
        }

        return strategyPositionDtoList;
    }

}
