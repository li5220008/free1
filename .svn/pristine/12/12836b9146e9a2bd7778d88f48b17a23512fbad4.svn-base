#!/bin/sh
#chkconfig: 35 85 15
#author:liujianli@gtadata.com
#布署的时候改成对应的项目根目录就OK
DIRECTORY=/var/myapp/qic/web/manage
PID_FILE=$DIRECTORY/server.pid
LOG_FILE=$DIRECTORY/logs/system.out
#play脚本位置
DEAMON=/usr/local/play1.2.x_cust/play
#java安装目录
export JAVA_HOME=/usr/local/jdk1.6.0_33/
function start()
{
  if [ -f "$PID_FILE" ]
  then
         echo "服务运行中，请先关闭"
  else
        # echo "开始启动。。。"
         cd $DIRECTORY && $DEAMON start
         echo "服务启动成功"

  fi
}
function stop()
{
  if [ -f "$PID_FILE" ]
  then
        cd $DIRECTORY && $DEAMON stop;
        sleep 3
        if [ -f "$PID_FILE" ]
           then
           #play 意外中止不会删除pid文件
           rm -f $PID_FILE
        fi
        echo "服务停止成功"
  else
        echo "服务没启动，不需停止..."
  fi

}

function status()
{
  if [ -f "$PID_FILE" ]
  then
       echo "运行中. PID为:"
       cat $PID_FILE
       echo ""
  else
       echo "服务没有运行"
  fi

}
case $1 in
'start')
        #start的时候也调用restart 因为当play异常关闭后server.pid还在 此时若调用start命令并不能重启
        $0 restart
;;
'stop')
        stop
;;
'restart')
        stop
        sleep 4
        start
;;
'status')
        status
;;
*)
        echo "usage: service qic_manage [start|stop|status|restart]"
;;
esac
