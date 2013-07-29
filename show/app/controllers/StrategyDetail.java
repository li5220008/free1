package controllers;

import annotation.QicFunction;
import business.DrawYieldChartService;
import bussiness.StrategyDetailService;
import bussiness.StrategyUserDiscussService;
import controllers.common.BasePlayControllerSupport;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.StrategyOrderDto;
import models.common.*;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 策略详情
 * User: liangbing
 * Date: 12-11-10
 * Time: 上午9:58
 */
public class StrategyDetail extends BasePlayControllerSupport {
    @QicFunction(id=2)
    public static void detail(long stid,String strategyId,int enginetypeId,long uid){
        long stime  = System.currentTimeMillis();
        //得到策略基本信息
        StrategyBaseDto strategyBaseDto = StrategyDetailService.getStrategyDetail(stid);
        //查询策略是否被收藏
        boolean b = StrategyDetailService.iscollect(stid, uid);
        //策略简单信息展示
        StrategyBaseDto strategyBaseinfo = StrategyDetailService.getstratebasicinfo(stid);
        //绩效指标
        IndicatorDto indicator = new IndicatorDto();
        IndicatorDto indicatorhis = new IndicatorDto();
        //ＱＩＡ的绩效指标
        QiaIndicatorDto qiaIndicatorDto = new QiaIndicatorDto();
        QiaIndicatorDto qiaIndicatorDtohis = new QiaIndicatorDto();
        switch (enginetypeId){
            case 1://QICore的绩效指标
                indicator= StrategyDetailService.getindicator(stid, 2);
                indicatorhis= StrategyDetailService.getindicator(stid, 1);
                break;
            case 2://QIA的绩效指标
                qiaIndicatorDto=  StrategyDetailService.getQiaIndicatorDto(stid, 2);
                qiaIndicatorDtohis=  StrategyDetailService.getQiaIndicatorDto(stid, 1);
                break;
            default:
        }

        //策略委托信号
        List<AuthorizeRecordDto> ordersignallist = new ArrayList<AuthorizeRecordDto>();
        if(enginetypeId==1){//只有QICore 才有委托信号
            ordersignallist = StrategyDetailService.getorder_signal(stid);
        }
        // 收益率走势图
        Date upTime = strategyBaseDto.upTime;//上架时间
        //周线
        String[] weekDataHTM = DrawYieldChartService.strategyDetailForWeekPictrue(strategyId, true, upTime);//所有的历史回测数据
        String[] weekDataRTM = DrawYieldChartService.strategyDetailForWeekPictrue(strategyId, false, upTime);//所有实时模拟数据
        //日线
        String[] dayDataHTM = DrawYieldChartService.strategyDetailForDayPictrue(strategyId, true, upTime);//所有的历史回测数据
        String[] dayDataRTM = DrawYieldChartService.strategyDetailForDayPictrue(strategyId, false, upTime);//所有实时模拟数据

        //判断句是否订阅
        boolean isorder = StrategyDetailService.isorder(uid, stid);
        //订阅到期时间
        String isorder_sql = SqlLoader.getSqlById("isorder_sql");
        StrategyOrderDto strategy = QicDbUtil.queryQicDBSingleBean(isorder_sql, StrategyOrderDto.class, uid, stid);
        Date strategy_etime = new Date();
        if(strategy != null){
           strategy_etime = strategy.order_etime;
        }
        int discussesBoolean = StrategyUserDiscussService.judge(stid, uid);
        System.out.println("detail=====================cost time :" + (System.currentTimeMillis()-stime));
        render(b,strategyBaseDto,uid,stid,indicator,indicatorhis,ordersignallist,isorder,
                strategy_etime,discussesBoolean,dayDataHTM,dayDataRTM,weekDataHTM,weekDataRTM,
                strategyId,qiaIndicatorDto,qiaIndicatorDtohis,enginetypeId);
    }

    public static void refreshSignal(long stid){
        List<AuthorizeRecordDto> ordersignallist = StrategyDetailService.getorder_signal(stid);
        render(ordersignallist);
    }

    //策略持仓
    @QicFunction(id=2)
    public static void holdPosition(long id,int enginetypeId,int pageNo){
        List<StrategyPositionDto> strategyPositionDtoList = new ArrayList<StrategyPositionDto>();
        if(enginetypeId==1){
            strategyPositionDtoList = StrategyDetailService.getStrategyPosition(id, pageNo);
        }else if(enginetypeId==2){
            strategyPositionDtoList = StrategyDetailService.getQiaPosition(id, pageNo);
        }
        render(strategyPositionDtoList,enginetypeId,id);
    }

    //持仓滚动分页
    @QicFunction(id=2)
    public static void moreHoldPosition(long id, int enginetypeId, int pageNo) {
        List<StrategyPositionDto> strategyPositionDtoList = new ArrayList<StrategyPositionDto>();
        if(enginetypeId==1){
            strategyPositionDtoList = StrategyDetailService.getStrategyPosition(id, pageNo);
        }else if(enginetypeId==2){
            strategyPositionDtoList = StrategyDetailService.getQiaPosition(id, pageNo);
        }
        render(strategyPositionDtoList,enginetypeId,id);
    }

    //委托记录
    @QicFunction(id=2)
    public static void entrustRecord(long id, int pageNo){
        List<AuthorizeRecordDto> authorizeRecordDtoList = StrategyDetailService.getAuthorizeRecord(id, pageNo);
        render(authorizeRecordDtoList, id);
    }

    //委托滚动分页
    @QicFunction(id=2)
    public static void moreEntrustRecord(long id, int pageNo) {
        List<AuthorizeRecordDto> authorizeRecordDtoList = StrategyDetailService.getAuthorizeRecord(id, pageNo);
        render(authorizeRecordDtoList,id);
    }

    //成交记录
    @QicFunction(id=2)
    public static void bargainRecord(long id, int pageNo){
        List<ExecutionRecordDto> executionRecordDtoList = StrategyDetailService.getExecutionRecord(id, pageNo);
        render(executionRecordDtoList, id);
    }

    //成交滚动分页
    @QicFunction(id=2)
    public static void moreBargainRecord(long id, int pageNo) {
        List<ExecutionRecordDto> executionRecordDtoList = StrategyDetailService.getExecutionRecord(id, pageNo);
        render(executionRecordDtoList, id);
    }

    //我要评论历史数据
    @QicFunction(id=4)
    public static void userComment(long id,long uid){
         List<UserStrategyDiscuss> discussesList = StrategyUserDiscussService.userDiscussList(id);
        int discussesBoolean = StrategyUserDiscussService.judge(id, uid);
        render(discussesBoolean,discussesList);
    }

    /**
     * 保存评论
     * @param uid
     * @param stid
     * @param usd
     */
    @QicFunction(id=4)
    public static void userDiscuss(Long uid,Long stid,UserStrategyDiscuss usd){
        if(uid!=null&&stid!=null){
            usd.disTime=new Date();
            StrategyUserDiscussService.saveDiscuss(usd, uid, stid);
        }
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("op", true);
        json.put("msg", "提交成功");
        json.put("dname",usd.user.name);
        json.put("star",usd.star);
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String time = df.format(usd.disTime);
        // String time = DateUtil.formatDate(usd.disTime);
        json.put("time",time);
        json.put("content",usd.content);
        renderJSON(json);
    }

    /**
     * 加入订阅
     * @param month
     * @param uid
     * @param stid
     */
    @QicFunction(id=4)
    public static void addStrategyOrder(int month,Long uid,Long stid){
        Map<String, Object> json = new HashMap<String, Object>();
        Date edate = DateUtils.addMonths(new Date(), month);
        if(edate.getYear() + 1900 > 2030){
            json.put("message","订阅到期日期不能超过2030年");
            json.put("success",false);
        }
        else{
            Date newDate = StrategyDetailService.addstrategyorder(month, uid, stid);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(newDate != null){
                json.put("message","订阅成功");
                json.put("success",true);
                json.put("date",sdf.format(newDate));
            }
            else {
                String getdowntime_sql = SqlLoader.getSqlById("getdowntime_sql");
                Map<String,Object> map = QicDbUtil.queryQicDBSingleMap(getdowntime_sql, stid);
                String downtime = "";
                if(map.get("down_time") == null){
                    downtime = "2099-12-12";
                }
                else {
                    downtime = map.get("down_time").toString();
                }
                downtime = downtime.substring(0,downtime.length()-2);
                StringBuffer sb = new StringBuffer("该策略将于");
                sb.append(downtime);
                sb.append("下架,请重新选择订阅时间");

                json.put("message",sb);
                json.put("success",false);

            }
        }

        renderJSON(json);
    }

    /**
     * 续订
     * @param month
     * @param uid
     * @param stid
     */
    @QicFunction(id=4)
    public static void delayStrategyOrder(int month,Long uid,Long stid){
        Map<String, Object> json = new HashMap<String, Object>();
        String get_orderetime_sql = SqlLoader.getSqlById("get_orderetime_sql");
        StrategyOrderDto strategyorder = QicDbUtil.queryQicDBSingleBean(get_orderetime_sql, StrategyOrderDto.class, uid, stid);
        Date edate = DateUtils.addMonths(strategyorder.order_etime, month);
        if(edate.getYear() + 1900 > 2030){
            json.put("message","订阅到期日期不能超过2030年");
            json.put("success",false);
        }
        else{
            Date newDate = StrategyDetailService.delaystrategyorder(month, uid, stid);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(newDate != null){
                json.put("message","续订成功");
                json.put("success",true);
                json.put("date",sdf.format(newDate));
            }
            else {
                String getdowntime_sql = SqlLoader.getSqlById("getdowntime_sql");
                Map<String,Object> map = QicDbUtil.queryQicDBSingleMap(getdowntime_sql, stid);
                String downtime = "";
                if(map.get("down_time") == null){
                    downtime = "2099-12-12";
                }
                else {
                    downtime = map.get("down_time").toString();
                }
                downtime = downtime.substring(0,downtime.length()-2);
                StringBuffer sb = new StringBuffer("该策略将于");
                sb.append(downtime);
                sb.append("下架,请重新选择订阅时间");

                json.put("message",sb);
                json.put("success",false);
            }
        }

        renderJSON(json);
    }

    //绩效数据 历史回测和实时数据切换
    @QicFunction(id=4)
    public static void indicator(long id,int type){
        QiaIndicatorDto qiaIndicatorDto=  StrategyDetailService.getQiaIndicatorDto(id, type);
        render(qiaIndicatorDto,type);
    }

   /*//this is a test
    public static void highStockDome1(){
        render();
    }
    //this is a test
    public static void highStockDome2(){
        render();
    }*/
}
