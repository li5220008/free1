#!/usr/bin/python
#author:liujianli@gtadata.com
import MySQLdb  as mysql
import time,datetime
import random
#define a function
def _connectqicoredb():
	qicore_conn=mysql.connect(host='192.168.100.199', user='root',passwd='123456', db="qicore",charset='utf8')
	qicore_conn.ping(True)
	qicore_conn.autocommit(True)
	qicore_cursor=qicore_conn.cursor()
	qicore_cursor.execute('SET foreign_key_checks = 0')
	return qicore_cursor
def _connectqicdb():
	qicore_conn=mysql.connect(host='192.168.101.108', user='qic',passwd='qic', db="qic_db",charset='utf8')
	qicore_conn.ping(True)
	qicore_conn.autocommit(True)
	qicore_cursor=qicore_conn.cursor()
	qicore_cursor.execute('SET foreign_key_checks = 0')
	return qicore_cursor
# insert one data to table capital_list for per strategy
def _inserttocapital(cursor,suuid):
	capital_value=[suuid,'K0000001','0',880259.2900,1000000.0000,973960.0900,1000000.0000,'2012-12-25 16:47:37',0.0000]
	capital_sql="insert into capital_list(StrategyID,Account,AccountType,AvailableCapital,WithdrawableCapital,TotalCapital,IniCapital,UpdateTime,TotalSlipPrice) values(%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	cursor.execute(capital_sql,capital_value)
#90 record per strategy 
def _inserttostrategydailyyield(cursor,suuid):
	strategy_daily_yield_values=[]
	strategy_daily_yield_sql="insert into strategy_daily_yield(strategyID,yield,updateDate) values(%s,%s,%s)"
	start_day=datetime.datetime.now()-datetime.timedelta(days=80)
	list = [-0.9001,-0.7001,0.4001,0.2001]   
	for i in range(80):
		yields = random.sample(list, 1) 
		date = datetime.datetime.strftime(start_day+datetime.timedelta(days=i) ,'%Y-%m-%d' ) 
		strategy_daily_yield_values.append((suuid,yields[0],date))
	cursor.executemany(strategy_daily_yield_sql,strategy_daily_yield_values)
#1
def _inserttohighlowstrategy(cursor,suuid):
	high_low_values=[suuid, -1086085.9000, -688551.9000, 279605.3400, 1365691.2000, 0.2047, 557, 279, 278, 0.1490, 83, 474, 0, 16993.9500, 22609.4960, 0.0608, 0.0166, -48.0367, 395674.6600, -1.0861, -407282.2000, -247418.3400, 0, -0.4073, -4.7189, 135865.5500, 0, 4, 0, 40, 0, 58, 0, 0.2651, -5.3243, 0.0000]
	high_low_strategy_sql="insert into strategy_high_low (StrategyID, RetainedProfits, GrossProfit, OverallProfitability, OverallDeficit, CanhsiedRatio, TradeCount, LongPositionTradeCount, ShortPositionTradeCount, ProfitRatio, ProfitCount, DeficitCount, PositionCloseCount, MAXSingleProfit, MAXSingleDeficit, MAXSingleProfitRatio, MAXSingleDeficitRatio, ProfitLossRatio, SumOfCommission, Yield, AvgProfitOfMonth, FloatingProfitAndLoss, TotalAsset, YieldOfMonth, YieldOfYear, MAXSequentialDeficitCapital, LastSequentialDeficitCapital, MAXSequentialProfitCount, LastSequentialProfitCount, MAXSequentialDeficitCount, LastSequentialDeficitCount, TradeDays, MAXShortPositionTime, YieldOfMonthStandardDeviation, SharpeIndex, MovingCost) values (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	cursor.execute(high_low_strategy_sql,high_low_values)	
def _inserttopositionlist(cursor,suuid):
	positions_values=['CFFEX','IF1205','00001',0,0,0,2,0.0000,0.0000,'2012-12-06 19:50:53',0.0000,'002813C1DC8249FC98302C3B3A56A57D','K0000001']
	position_list_sql="insert into position_list(SecurityExchange,SecurityID,Symbol,PositionQty,TodayPosition,PrePosition,Side,PositionCost,PositionCommission,UpdateTime,PerformanceCost,StrategyID,Account) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	positions_values[11]=suuid
	cursor.execute(position_list_sql,positions_values)
def _inserttoorderlist(cursor,values):
	order_list_sql="insert into order_list(ClordID,ClOrdLinkID,SecurityID,Side,Price,OrdType,SendingTime,OrderQty,OrigClOrdID,Account,PositionEffect,MsgType,UpdateTime) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	cursor.executemany(order_list_sql,values)
def _inserttoexecutionlist(cursor,values):
	execution_list_sql="insert into execution_report_list(ReportID,ClordID,OrderID,ClOrdLinkID,SecurityID,OrdStatus,Side,LastQty,LastPx,CumQty,AvgPx,LeavesQty,TransactTime,OrderQty,Account,OrigClOrdID,MsgType,UpdateTime) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	cursor.executemany(execution_list_sql,values)
def _getallstrategyinfo(cursor):
	qic_strategys=qic_cursor.execute("select * from strategy_baseinfo")
	return qic_cursor.fetchall()
	

#define each database operation cursor
qic_cursor = _connectqicdb()
qicore_cursor =_connectqicoredb()
execution_values=[]
order_values=[]
strategys = _getallstrategyinfo(qic_cursor)
#create about 3,000,000 record for execution_report_list and order_list table
print "start generate simulate data"
start_time=time.time()
index=0
for row in strategys:
	strategy_id=row[1]
	index+=1
	print "strategy id:",strategy_id,"current index is:",index
	for i in range(300):
		#create 300 order record  and  300 execution record for per strategy
		execution_values.append((1,1682,27845,strategy_id,601766,1,1,250.0000,0.0000,250.0000,4.6200,250.0000,'2012-11-05 09:30:29',500.0000,290100021893,1,8,'2012-11-05 17:31:06'))
		order_values.append(('2312275632012-11-2 14:39:14',strategy_id,'IF1210',1,1200.0000,0,'2012-11-02 14:39:14',1.0000,1,2220001013,1,'D','2012-12-06 19:50:39'))
	_inserttopositionlist(qicore_cursor,strategy_id)
	_inserttoorderlist(qicore_cursor,order_values)
	_inserttoexecutionlist(qicore_cursor,execution_values)
	_inserttocapital(qicore_cursor,strategy_id)
	_inserttostrategydailyyield(qic_cursor,strategy_id)
	_inserttohighlowstrategy(qic_cursor,strategy_id)
	#clean Array
	execution_values=[]
	order_values=[]
#close qicore cursor
if(qicore_cursor):
	qicore_cursor.close()
#close qic cursor
if(qic_cursor):
	qic_cursor.close()
cost_time=time.time()-start_time
print "end generate simulate data,cost time:",cost_time




	

		
		
		
		


