#!/usr/bin/python
#author:liujianli@gtadata.com
#desc:must install MysqlDb module
# -*- coding: utf-8 -*-
#1. init role_info
#2. init function_info
#3. init role_function
#4.	init sale_department
#5. init user_info
#6.	init user_role
#7.	init config_manage
#8. init server_list
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
default_role_id=1
default_user_id=1
default_user_name="管理员"
default_user_account="admin"
default_department_id=1
default_department_name="营业部"
#123
default_user_pwd="ICy5YqxZB1uWSwcVLSNLcA=="
default_admin_email="admin@admin.com"
db_host="192.168.103.108"
db_name="qic_db"
db_user="qic"
db_passwd="qic"
cursor=False
def get_connect_db():
	global cursor
	if(cursor):
		return cursor
	else:
		import MySQLdb  as mysql
		qic_db_conn = mysql.connect(host=db_host, user=db_user,passwd=db_passwd, db=db_name,charset='utf8')
		qic_db_conn.autocommit(True)
		cursor=qic_db_conn.cursor()
		cursor.execute('SET foreign_key_checks = 0')
	return cursor
def init_role_info():
	role_value=[default_role_id,"系统管理员","超级管理员拥有所有权限"]
	role_sql = "insert into role_info(id,name,desp) values(%s,%s,%s)"
	truncate_sql="truncate table role_info"
	get_connect_db().execute(truncate_sql)
	get_connect_db().execute(role_sql,role_value)
def init_sys_product_info():
    product_info=[]
    product_info.append((1, "qic"))
    product_info.append((2, "iquant"))
    product_info_sql = "INSERT INTO sys_product_info(id, NAME) VALUES(%s, %s)"
    truncate_sql = "truncate table sys_product_info"
    get_connect_db().execute(truncate_sql)
    get_connect_db().execute(product_info_sql, product_info)
def init_function_info():
	function_values=[]
	function_values.append((1,"菜单根节点", "001", 0, 0, 1))
	function_values.append((2,"策略超市", "001", 0, 1, 1))
	function_values.append((3,"策略浏览", "001", 0, 2, 1))
	function_values.append((4,"策略订阅及评价", "001", 0, 2, 1))
	function_values.append((5,"我的收藏，(取消)", "001", 0, 2, 1))
	function_values.append((6,"策略编写", "001", 0, 2, 1))
	function_values.append((7,"策略上传", "001", 0, 2, 1))
	function_values.append((8,"信号查看", "001", 0, 2, 1))
	function_values.append((9,"股票池", "001", 0, 1, 1))
	function_values.append((10,"我的收藏和评价", "001", 0, 9, 1))
	function_values.append((11,"组合浏览", "001", 0, 9, 1))
	function_values.append((12,"行情报价", "001", 0, 1, 1))
	function_values.append((13,"指数", "001", 0, 12, 1))
	function_values.append((14,"沪深股票", "001", 0, 12, 1))
	function_values.append((15,"债券", "001",0, 12, 1))
	function_values.append((16,"基金", "001",0, 12, 1))
	function_values.append((17,"期货", "001",0, 12, 1))
	function_values.append((18,"外汇", "001",0, 12, 1))
	function_values.append((19,"f10", "001", 0, 12, 1))
	function_values.append((20,"用户管理", "001", 0, 1, 1))
	function_values.append((21,"用户权限管理", "001", 0, 20, 1))
	function_values.append((22,"角色管理", "001", 0, 20, 1))
	function_values.append((23,"策略管理", "001", 0, 1, 1))
	function_values.append((24,"配置管理", "001", 0, 1, 1))
	function_values.append((25,"操作日志", "001", 0, 1, 1))
	insert_sql = "insert into function_info(id, name, action, code, pid, product_id) values(%s, %s, %s, %s, %s, %s)"
	truncate_sql="truncate table function_info"
	get_connect_db().execute(truncate_sql)
	get_connect_db().executemany(insert_sql,function_values)
def init_role_function():
	role_function_values=[]
	for fid in range(1,26):
		role_function_values.append((default_role_id,fid))
	insert_sql = "insert into role_function(rid,fid) values(%s,%s)"
	truncate_sql="truncate table role_function"
	get_connect_db().execute(truncate_sql)
	get_connect_db().executemany(insert_sql,role_function_values)
def init_sale_deparment():
	deparment_values=[default_department_id,default_department_name]
	insert_sql = "insert into sale_department(id,name) values(%s,%s)"
	truncate_sql="truncate table sale_department"
	get_connect_db().execute(truncate_sql)
	get_connect_db().execute(insert_sql,deparment_values)

def init_user_info():
	user_info_value=[default_user_id,default_user_name,default_user_account,default_user_pwd,"",default_admin_email,"",default_department_id,"","","","2013-03-01","2099-01-01","2013-03-01",10,1,"2013-03-01 00:00:00"]
	insert_sql = "insert into user_info(id,name,account,pwd,phone,email,idcard,sale_dep,\
	capital_account,address,post,sdate,edate,apply_date,status,utype,utime) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
	truncate_sql="truncate table user_info"
	get_connect_db().execute(truncate_sql)
	get_connect_db().execute(insert_sql,user_info_value)
def	init_user_role():
	user_role_value=[default_user_id,default_role_id]
	insert_sql = "insert into user_role(uid,rid) values(%s,%s)"
	truncate_sql="truncate table user_role"
	get_connect_db().execute(truncate_sql)
	get_connect_db().execute(insert_sql,user_role_value)
def init_config_manage():
	config_manage_values=[]
	config_manage_values.append((1,"strategyUpMsg","尊敬的用户您好！ [$strategy.Name] 策略将于[$strategy.DownTime]上架，请知悉。","策略上架通知"))
	config_manage_values.append((2,"strategyDownMsg","尊敬的用户您好！ [$strategy.Name] 策略将于[$strategy.DownTime]下架，请知悉。","策略下架通知"))
	config_manage_values.append((3,"otherMsg","尊敬的用户您好！\
	您的新密码是：[$userInfo.Pwd]\
	为了您的账号安全，请登入后进入系统配置中进行密码修改。","策略下架通知"))
	config_manage_values.append((4,"user.excel.template.dir","http://192.168.100.195/template.xls","用户上传模板文件路径"))
	config_manage_values.append((5,"user.excel.upload.tmp.dir","/data/excel/tmp/","excel保存的临时目录"))
	config_manage_values.append((6,"user.excel.upload.official.dir","/data/excel/official/","excel保存的正式目录"))
	config_manage_values.append((7,"strategy.upload.temp.dir","/data/strategy/tmp/","策略文件保存手临时目录"))
	config_manage_values.append((8,"strategy.upload.official.dir","/home/gta/strategies/","策略文件保存的正式目录"))
	config_manage_values.append((9,"others.load.strategy.base.dir","\\\\192.168.103.104\\strategies\\","第三方加载策略文件的基路径"))
	config_manage_values.append((10,"show.createOneStrategyPic.path","http://localhost:9000/createOneStrategyPic/doRun?strategyId=%s","策略上架通知"))
	config_manage_values.append((11,"activateMsg","尊敬的[$userInfo.Name] 您好！您的账号：[$userInfo.Account]已经成功激活,有效日期截止到：[$userInfo.EDate],谢谢使用！","账号激活邮件通知"))
	insert_sql = "insert into config_manage(id,key_name,key_value,remark) values(%s,%s,%s,%s)"
	truncate_sql="truncate table config_manage"
	get_connect_db().execute(truncate_sql)
	get_connect_db().executemany(insert_sql,config_manage_values)
def init_server_list():
	server_list_values=[]
	server_list_values.append(())
	server_list_values.append(())
	insert_sql = "insert into server_list(ip,status,stype,memo) values(%s,%s,%s,%s)"
	truncate_sql="truncate table server_list"
	get_connect_db().execute(truncate_sql)
	get_connect_db().executemany(insert_sql,server_list_values)
def init_db():
    init_role_info()
    init_function_info()
    init_sys_product_info()
    init_role_function()
    init_sale_deparment()
    init_user_info()
    init_user_role()
    #init_config_manage()
if __name__=="__main__":
  
	print "start initlize qic_db"
	init_db()
	print "success"
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	