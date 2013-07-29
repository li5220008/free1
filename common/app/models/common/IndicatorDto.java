package models.common;

import com.google.gson.annotations.SerializedName;

/**
 * 绩效指标
 * User: panzhiwei
 * Date: 12-11-13
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class IndicatorDto {
    public String id;
    public String uuid;
    //净利润
    @SerializedName("retainedProfits")
    public Float retainedProfits;
    //收益率
    @SerializedName("yield")
    public Float yield;
    //月化收益率
    @SerializedName("yieldOfMonth")
    public Float yieldOfMonth;
    //月度收益标准差
    @SerializedName("yieldOfMonthStandardDeviation")
    public Float yieldOfMonthStandardDeviation;
    //总盈利
    @SerializedName("overallProfitability")
    public Float overallProfitability;
    //总亏损
    @SerializedName("overallDeficit")
    public Float overallDeficit;
    //总交易天数
    @SerializedName("tradeDays")
    public int tradeDays;
    //最大空仓时间
    @SerializedName("mAXShortPositionTime")
    public int mAXShortPositionTime;
    //交易次数
    @SerializedName("tradeCount")
    public int tradeCount;
    //多头交易次数
    @SerializedName("longPositionTradeCount")
    public int longPositionTradeCount;
    //空头交易次数
    @SerializedName("shortPositionTradeCount")
    public int shortPositionTradeCount;
    //盈利次数
    @SerializedName("profitCount")
    public int profitCount;
    //亏损次数
    @SerializedName("deficitCount")
    public int deficitCount;
    //持平次数
    @SerializedName("positionCloseCount")
    public int positionCloseCount;
    //最大连续盈利次数
    @SerializedName("mAXSequentialProfitCount")
    public int mAXSequentialProfitCount;
    //最大连续亏损次数
    @SerializedName("mAXSequentialDeficitCount")
    public int mAXSequentialDeficitCount;
    //盈利比率
    @SerializedName("profitRatio")
    public Float profitRatio;
    //总盈利/总亏损
    @SerializedName("canhsiedRatio")
    public Float canhsiedRatio;
    //单词最大盈利
    @SerializedName("mAXSingleProfit")
    public Float mAXSingleProfit;
    //单词最大亏损
    @SerializedName("mAXSingleDeficit")
    public Float mAXSingleDeficit;
    //最大盈利/总盈利
    @SerializedName("mAXSingleProfitRatio")
    public Float mAXSingleProfitRatio;
    //最大亏损/总亏损
    @SerializedName("mAXSingleDeficitRatio")
    public Float mAXSingleDeficitRatio;
    //最大连续亏损额
    @SerializedName("mAXSequentialDeficitCapital")
    public Float mAXSequentialDeficitCapital;
    //手续费合计
    @SerializedName("sumOfCommission")
    public Float sumOfCommission;
    //毛利润
    @SerializedName("grossProfit")
    public Float grossProfit;
    //净利润/单次最大亏损
    @SerializedName("profitLossRatio")
    public Float profitLossRatio;
    //月度平均盈利
    @SerializedName("avgProfitOfMonth")
    public Float avgProfitOfMonth;
    //年化收益率
    @SerializedName("yieldOfYear")
    public Float yieldOfYear;
    //夏普指数
    @SerializedName("sharpeIndex")
    public Float sharpeIndex;
    //浮动盈亏
    @SerializedName("FloatingProfitAndLoss")
    public Float FloatingProfitAndLoss;
    //滑价成本
    @SerializedName("movingCost")
    public Float movingCost;

}

