package controllers;

import business.LogInfosService;
import bussiness.BackTestService;
import com.google.common.collect.Lists;
import controllers.common.AuthorBaseIntercept;
import controllers.common.BasePlayControllerSupport;
import dto.BackTestServerDto;
import dto.QiaBackTestResultDto;
import dto.StrageServerDto;
import play.data.binding.As;
import play.jobs.Job;
import play.mvc.With;
import util.SystemLoggerMessage;

import java.util.List;

/**
 * User: 刘建力(liujianli@gtadata.com))
 * Date: 12-12-20
 * Time: 下午4:34
 * 功能描述: 回测服务器管理
 */
@With({AuthorBaseIntercept.class})
public class BackTestServers extends BasePlayControllerSupport {
    /**
     * 添加回测服务器
     * @param backTestServerDto
     */
  public  static void addServer(StrageServerDto backTestServerDto,long uid){

      if(backTestServerDto != null){
          BackTestService.addServer(backTestServerDto);
          LogInfosService.writeSystemLog(uid, SystemLoggerMessage.DO_ADD_BACKTEST_SERVICE, SystemLoggerMessage.ADD_BACKTEST_SERVICE, SystemLoggerMessage.TYPE);//写入系统日志

      }

  }
    /**
     * 更改策略的回测服务器
     * @param
     */
    public  static void updateStratedyServerId(long uid){

        String strategyId = params.get("suuid");
        long serverId = params.get("sid",Long.class);
        BackTestService.updateStratedyServerId(serverId, strategyId);
        LogInfosService.writeSystemLog(uid, SystemLoggerMessage.DO_UPDATE_BACKTEST_SERVICEID, SystemLoggerMessage.UPDATE_BACKTEST_SERVICEID, SystemLoggerMessage.TYPE);//写入系统日志


    }

    public static void updateServerStatus(long uid){
        int status = params.get("status",Integer.class);
        long serverId = params.get("sid",Long.class);
        BackTestService.updateServerStatus(status, serverId);
        LogInfosService.writeSystemLog(uid, SystemLoggerMessage.DO_UPDATE_BACKTEST_SERVICESTATUS, SystemLoggerMessage.UPDATE_BACKTEST_SERVICESTATUS, SystemLoggerMessage.TYPE);//写入系统日志

    }

    public static void getServers(int type,int eId){
        BackTestServerDto.ServerTypeEnum  serverTypeEnum= null;
        for(BackTestServerDto.ServerTypeEnum typeEnums : BackTestServerDto.ServerTypeEnum.values()){
           if(typeEnums.getValue() == type){
               serverTypeEnum = typeEnums;
               break;
           }
        }
        List<StrageServerDto> list = BackTestService.findServerByTypeAndEngineeId(BackTestServerDto.ServerStatusEnum.VALID, serverTypeEnum, eId);
        renderJSON(list);
    }
    public static void startBackTest(int serverId){
       // F.Promise<QiaBackTestResultDto> result =   BackTestService.startBackTestQiaStrategy(serverId);
        final int sid = serverId;
        //QiaBackTestResultDto result = BackTestService.startBackTestQiaStrategy(serverId);
        QiaBackTestResultDto result = await(new Job<QiaBackTestResultDto>(){
            public QiaBackTestResultDto doJobWithResult(){
                return BackTestService.startBackTestQiaStrategy(sid);
            }
        }.now());
        renderJSON(result);
    }
    public static void getQiaBackTestStatus(final @As(",")Integer[] serverIds){
     //  final  List<QiaBackTestResultDto> list = Lists.newArrayList();
        List<QiaBackTestResultDto>  result = await(new Job<List<QiaBackTestResultDto>>(){
            public List<QiaBackTestResultDto> doJobWithResult(){
                List<QiaBackTestResultDto> list = Lists.newArrayList();
                for(int serverId : serverIds){
                    QiaBackTestResultDto resultDto = BackTestService.getQiaStrategyBackTestStatus(serverId);
                    list.add(resultDto);
                }
                return list;
            }
        }.now());
     /*   for(int serverId : serverIds){
        QiaBackTestResultDto resultDto = BackTestService.getQiaStrategyBackTestStatus(serverId);
        list.add(resultDto);
        }*/
        renderJSON(result);
    }
}
