package bussiness;

import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import dto.StrategyContrastDto;
import models.common.StrategyBaseinfo;
import models.common.StrategyDailyYieldDto;
import play.Logger;
import util.DrawPictrueUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 处理 策略对比表格展示
 * User: liuhongjiang
 * Date: 12-11-9
 * Time: 下午4:54
 */
public class StrategyContrastService {
        public static List<StrategyContrastDto> strategyContrast(String idArray[]){
            if(idArray==null||idArray.length==0){
                return null;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            //策略ID
            List<StrategyContrastDto> strategyContrastlist = new ArrayList<StrategyContrastDto>();
            //根据策略ID查 qic策略的相关信息
            String StrategyContrastForQICSql = SqlLoader.getSqlById("StrategyContrastForQIC");
            //根据策略ID查 qia策略的相关信息
            String StrategyContrastForQIASql = SqlLoader.getSqlById("StrategyContrastForQIA");
            //查询策略(qic_db.strategy_baseinfo)
            String StrategyContrastInfoSql = SqlLoader.getSqlById("StrategyContrastInfo");
            //查询该策略当前的订阅人数
            String getCurrentOrderNumSql = SqlLoader.getSqlById("getCurrentOrderNum");
             //为页面策略名称添加颜色样式
            String[] colors = new String[]{"#059af8","#a67ec7","#2bb431","#fb3118","#ef9c00"};

            for (int i=0;i<idArray.length;i++){
                StrategyContrastDto sci = QicDbUtil.queryQicDBSingleBean(StrategyContrastInfoSql, StrategyContrastDto.class,idArray[i]);
                if(sci ==null){
                    sci = new StrategyContrastDto();
                }
                sci.currentOrder = QicDbUtil.queryQicDbCount(getCurrentOrderNumSql,idArray[i]);
                if(sci.enginetypeId== StrategyBaseinfo.QICORE_ENGINEE_ID){
                    map = QicDbUtil.queryQicDBSingleMap(StrategyContrastForQICSql, idArray[i]);
                }else if(sci.enginetypeId== StrategyBaseinfo.QIA_ENGINEE_ID){
                    map = QicDbUtil.queryQicDBSingleMap(StrategyContrastForQIASql, idArray[i]);
                }
                sci.color = colors[i];
                if(map!=null && map.size()!=0){
                    sci.profitRatio = Double.parseDouble( map.get("profitratio").toString());
                    sci.yieldOfYear = Double.parseDouble(map.get("yieldofyear").toString());
                    sci.sharpeIndex = Double.parseDouble(map.get("sharpeindex").toString());
                }
                //将组合的数据放入list
                strategyContrastlist.add(sci) ;
            }
                 return strategyContrastlist;
        }


    /**
     *     策略对比 图形展示
     *     这个方法的主要作用是组装数据
     *     需要的数据：
     *     1.格式化近三个月的起始时间 maxDate minDate
     *     2.列出最近三个月收益率的最大值 最小值  取绝对值最大的modYield
     *     3.把数据组装成[{name=“策略名1” date:[ Date.UTC(2010, 0, 1), 9.05],...},
     *                  {name=“策略名2” date:[ Date.UTC(2010, 1, 1), 9.05],...}]类型
     *     @param  idArray 策略ID数组
     *     @retrun  arr_strategys[]数组  放入的数据依次为： 最大日期（对应X轴最大值），
     *                                                   最小日期（对应X轴最小值），
     *                                                   最大收益率（对应Y轴最大值），
     *                                                   按highcharts格式组装好的一个或多个策略的单天收益率和时间，
     *                                                   Y轴最小间距，X轴最小间距
     *
     */

    public static  String[] strategyContrastForPictrue(String idArray[]){
        String[] arr_strategys = new String[5];
        String sname="--";//默认策略名
        String strategys="";//组装多个策略数据
        Date minDate=null ;
        Date maxDate=new Date() ;//取当前日期作为最大时间
        float maxYield = 0f;
        String pictrue_sql = SqlLoader.getSqlById("strategy_contrast_for_pictrue_sql");
        String getName_sql = SqlLoader.getSqlById("StrategyContrastInfo");

        if(idArray==null){
            return null;
        }

        for (int i=0;i<idArray.length;i++){  //根据策略ID查询 单位时间内的收益率
            String strategy = "";
            //取当前策略数据库最大（最近）时间
            //maxDate  = getlatelyDate(idArray[i]);
            minDate = DrawPictrueUtil.getTime(maxDate,5,-89); //取89天以前的时间
            //取最近三个月的收益率
            List<StrategyDailyYieldDto> yield_list = QicDbUtil.queryQicDBBeanList(pictrue_sql, StrategyDailyYieldDto.class, idArray[i],minDate);
            StrategyContrastDto strategyContrast = QicDbUtil.queryQicDBSingleBean(getName_sql,StrategyContrastDto.class,idArray[i]);
            if(strategyContrast!=null){
                sname = strategyContrast.name;
            }
            Iterator<StrategyDailyYieldDto> it =  yield_list.iterator();
             while (it.hasNext()){
                 StrategyDailyYieldDto sdy = it.next();
                 String s =  DrawPictrueUtil.combinationData(sdy) ;
                 strategy +=(s+"\n");
             }
            int flag = strategy.lastIndexOf(","); //剔除字符串中最后一个“,”
            if(flag == -1){//数据库没有数据 给一个初始化值 页面图形就能正确展示
                strategy = "{name:'"+sname+"', data:[[Date.UTC(2012,01,01),1.00]]}";
            }else{
                strategy = "{name:'"+sname+"', data:["+strategy.substring(0,flag)+"]}";//一个策略数据拼接完成
            }
            strategys += (strategy+",\n");
            float Yield =  DrawPictrueUtil.getmaxModYield(idArray[i],minDate);//取当前策略绝对值最大的yield
            //取所有对比的策略中最大的  yield
            if (maxYield<Yield){
                maxYield = Yield  ;
            }

        }
        int flag1 = strategys.lastIndexOf(",");//剔除字符串中最后一个“,”
        if(flag1==-1){
            return arr_strategys;
        }
        strategys = "["+strategys.substring(0,flag1)+"]";//参与策略对比的所有策略数据拼接完成
        //取最大时间
        String str_maxDate ="";
        if(maxDate!=null){
            str_maxDate = DrawPictrueUtil.getFormatMaxorMinDate(maxDate);
        }
        //取最小时间
        String str_minDate ="";
        if(minDate!=null){
            str_minDate = DrawPictrueUtil.getFormatMaxorMinDate(minDate);

        }
        String str_maxYield = String.format("%.2f",maxYield*100);
        //（最大收益率/3）来表示图中Y轴坐标间隔 保留小数点后两位 无论小数点后第三位是否大于5都入位
        String str_averYield = new BigDecimal((maxYield/3)*100).setScale(2, BigDecimal.ROUND_CEILING).toString();
        //最大时间，最小时间，最大收益率，组装的series，收益率均值
        arr_strategys = new String[]{str_maxDate,str_minDate,str_maxYield,strategys,str_averYield};
        if (Logger.isDebugEnabled()) {
            Logger.debug("strategys============"+strategys);
            Logger.debug("maxYield============"+maxYield);
            Logger.debug("str_maxDate============"+str_maxDate);
            Logger.debug("str_minDate============"+str_minDate);
            Logger.debug("str_averYield====================================="+str_averYield);
        }
        return  arr_strategys;
     }
}
