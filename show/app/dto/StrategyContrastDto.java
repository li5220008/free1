package dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 策略超市 策略对比表格数据展示
 * User: liuhongjiang
 * Date: 12-11-8
 * Time: 上午11:26
 */
public class StrategyContrastDto {
    //机构名称
    @SerializedName("name")
    public String name;
    //收益率
    @SerializedName("Yield")
    public BigDecimal yield;
    //月化收益率
    @SerializedName("YieldOfMonth")
    public  BigDecimal yieldOfMonth;
    //年化收益率
    @SerializedName("YieldOfYear")
    public Double yieldOfYear;
    //盈利次数
    @SerializedName("ProfitCount")
    public Integer profitCount;
    //胜率
    @SerializedName("ProfitRatio")
    public Double profitRatio;
    //单次最大盈利
    @SerializedName("MAXSingleProfit")
    public BigDecimal maxSingleProfit;
    //单次最大亏损
    @SerializedName("MAXSingleDeficit")
    public BigDecimal maxSingleDeficit;
    //月度收益率标准差
    @SerializedName("YieldOfMonthStandardDeviation")
    public BigDecimal yieldOfMonthSD;
    //夏普比率 (夏普指数)
    @SerializedName("SharpeIndex")
    public Double sharpeIndex;
    //总交易天数
    @SerializedName("TradeDays")
    public Integer tradeDays;
    //控制页面策略名称颜色
    public String color;
    //交易类型
    public Integer tradeType;
    //交易品种
    public Integer tradeVariety;
    //上架时间
    public Date upTime;
    //策略引擎类型 1:qic,2:qia
    public int enginetypeId;
    //评论总人数
    public int discussCount;
    // 收藏总人数
    public int collectCount;
    //订阅总人数
    public int orderCount;
    //当前订阅人数
    public long currentOrder;
    //星级
    public float starLevel;

}
