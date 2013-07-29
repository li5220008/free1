package business;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import models.common.*;
import play.Logger;
import play.libs.WS;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: 刘建力(liujianli@gtadata.com))
 * Date: 12-12-20
 * Time: 下午1:49
 * 功能描述:  策略回测同外部api交互相关处理方法
 */
public class BackTestBaseService {
    private static String  START_BACKTEST_URL_TEMPLATE = "http://%s:9501/backtest/start";
    private static String  GET_BACKTEST_STATUS_URL_TEMPLATE = "http://%s:9501/backtest/status";
    private static List<StrageServerDto> serverList_cacehe = new CopyOnWriteArrayList<StrageServerDto>();
    /**
     * 检查此ip是否有权限进行回测，如果有则返回服务器id 其它则返回-1表示无权限
     * @param ip
     * @return
     */
   public static long findServerIdByAddr(String ip,int engineId,int serverType){
       String sql = SqlLoader.getSqlById("findServerIdByAddr");
       StrageServerDto backTestServerDto = QicDbUtil.queryQicDBSingleBean(sql,StrageServerDto.class,ip,engineId,serverType);
       if(backTestServerDto == null || backTestServerDto.status == BackTestServerDto.ServerStatusEnum.DISABLED.getValue()){
           return BackTestServerDto.ServerStatusEnum.DISABLED.getValue();
       }
       return backTestServerDto.id;
   }
    public static String findServerIpById(int serverId){
        List<StrageServerDto> list = findAllServer();
        for(BackTestServerDto backTestServerDto:list){
            if(backTestServerDto.id == serverId)
            return backTestServerDto.ip;
        }
        return null;
    }

    /**
     * 根据服务器id查询待回测qicore策略列表
     * @param serverId
     * @return
     */
   public static List<BackTestStrategyDto> findBackTestStrategyByServerId(long serverId){
        return findBackTestStrategyByServerId(serverId, StrategyDto.QICORE_ENGINEE_ID);
   }
    /**
     * 根据服务器id查询待回测列表
     * @param serverId
     * @return
     */
    public static List<BackTestStrategyDto> findBackTestStrategyByServerId(long serverId,int engineId){
        String sql = SqlLoader.getSqlById("findBackTestStrategyByServerId");
        List<BackTestStrategyDto> list = QicDbUtil.queryQicDBBeanList(sql,BackTestStrategyDto.class,serverId,engineId, StrategyDto.StrategyStatus.SANDBOXTESTING.value, StrategyDto.StrategyStatus.BACKTESTING.value);
        if(list!=null && list.size()>0){
            for(BackTestStrategyDto backTestStrategyDto: list){
                backTestStrategyDto.filePath = (SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR)+ backTestStrategyDto.filePath.replace("/","\\"));
            }
        }

        return list;
    }

    /**
     *供回测服务器开始回测的时候调用
     * @param serverId
     * @param strategyId
     * @return
     */
   public static boolean  checkBackTestStatus(long serverId,String strategyId){
       String sql = SqlLoader.getSqlById("checkBackTestStatus");
       BackTestStrategyDto backTestStrategyDto = QicDbUtil.queryQicDBSingleBean(sql,BackTestStrategyDto.class,serverId,strategyId);
       System.out.println("backTestStrategyDto====:" + backTestStrategyDto);
       return backTestStrategyDto != null;
   }

    /**
     * 更新策略状态   取走策略后要标识回测中
     * @param strategyId
     * @param status
     * @return
     */
   public  static int[] updateStrategyStatus(String strategyId,StrategyDto.StrategyStatus status){
       BackTestStrategyDto bsd = new BackTestStrategyDto();
       bsd.strategyUUid = strategyId;
       List<BackTestStrategyDto> list = new ArrayList<BackTestStrategyDto>(1);
       list.add(bsd);
       return updateStrategyStatus(list,status);
   }
    /**
     * 更新策略状态   取走策略后要标识回测中
     * @param strategyId
     * @param status
     * @return
     */
    public  static int updateStrategyStatus(String[] strategyId,StrategyDto.StrategyStatus status){
        return 0;
    }
    /**
     * 更新策略状态   取走策略后要标识回测中
     * @param list
     * @param status
     * @return
     */
    public  static int[] updateStrategyStatus(List<BackTestStrategyDto> list,StrategyDto.StrategyStatus status){
        if(list == null ||list.size()==0 ){
            return new int[]{0};
        }
        String sql = SqlLoader.getSqlById("updateStrategyStatus");
        Object[][] params = new Object[list.size()][2];
        for(int i = 0;i<list.size();i++){
            BackTestStrategyDto obj = list.get(i);
            params[i][0] = status.value;
            params[i][1] = obj.strategyUUid;

        }
        return QicDbUtil.batchQicDB(sql,params);

    }

    /**
     * 添加回测服务器
     * @param backTestServerDto
     * @return
     */
    public static int addServer(StrageServerDto backTestServerDto){
        if(backTestServerDto == null){
            return 0;
        }
        String sql = SqlLoader.getSqlById("addServer");
        clearCache();
        return QicDbUtil.updateQicDB(sql,backTestServerDto.enginetypeId,backTestServerDto.type,backTestServerDto.serverName,backTestServerDto.ip,backTestServerDto.status);
    }

    /**
     * 修改策略所对应的回测服务器ID
     * @param serverId
     * @param suuid  策略人uuid
     * @return
     */
    public static int updateStratedyServerId(long serverId,String suuid){
        String sql = SqlLoader.getSqlById("updateStratedyServerId");
        return QicDbUtil.updateQicDB(sql,serverId,suuid);
    }
    /**
     * 批量修改策略所对应的回测服务器ID
     * @param serverId
     * @param sid 自增长主键id
     * @return
     */
    public static int[] updateStratedyServerIdByIntId(Map<String,Integer> serverId,String[] sid){
        if(sid == null || sid.length == 0){
            return new int[]{};
        }
        List<StrategyBaseinfo> list =StrategyBaseService.findStrategysByIds(sid);
        for(StrategyBaseinfo tmp : list){
            if(tmp==null||(tmp.status != StrategyDto.StrategyStatus.CHECKING.value && tmp.status != StrategyDto.StrategyStatus.BACKTESTINGFAILER.value ) ){
                return new int[]{};
            }
        }
        Object[][] params = new Object[sid.length][3];
        int index = 0;
        for(StrategyBaseinfo tmp : list){
            params[index][0] = tmp.enginetypeId ==  StrategyBaseinfo.QICORE_ENGINEE_ID ?serverId.get("qicore"):serverId.get("qia");;
            params[index][1] = StrategyDto.StrategyStatus.SANDBOXTESTING.value;
            params[index][2] = tmp.id;
            ++index;

        }
        String sql = SqlLoader.getSqlById("updateStratedyServerIdByIntId");
      //  updateStrategyPassTime(sid);
        return QicDbUtil.batchQicDB(sql,params);
    }

    /**
     * 更改回测服务器状态
     * @param status
     * @param serverId
     * @return
     */
    public static int updateServerStatus(int status,long serverId){
        String sql = SqlLoader.getSqlById("updateBackTestServerStatus");
        clearCache();
        return QicDbUtil.updateQicDB(sql,status,serverId);
    }

    /**
     * 查询所有的待回测服务器列表
     * @return
     */
    public synchronized static List<StrageServerDto> findAllServer(){
        if(serverList_cacehe == null || serverList_cacehe.size()==0){
            String sql = SqlLoader.getSqlById("findAllServer");
             serverList_cacehe.addAll(QicDbUtil.queryQicDBBeanList(sql,StrageServerDto.class));
            return serverList_cacehe;
        }
        return serverList_cacehe;
    }

    /**
     * 查询回测服务器列表
     * @param status
     * @return
     */
    public static List<StrageServerDto> findBackTestServerByStatus(BackTestServerDto.ServerStatusEnum status){
        return findServerByStatusAndType(status,BackTestServerDto.ServerTypeEnum.BACKTEST);
    }
    /**
     * 查询代理服务器列表
     * @param status
     * @return
     */
    public static List<StrageServerDto> findAgentServerByStatus(BackTestServerDto.ServerStatusEnum status){
        return findServerByStatusAndType(status,BackTestServerDto.ServerTypeEnum.AGENT);
    }

    /**
     * 按服务器状态和类型查找服务器
     * @param status
     * @return
     */
    public static List<StrageServerDto> findServerByStatusAndType(BackTestServerDto.ServerStatusEnum status,BackTestServerDto.ServerTypeEnum type){
        //默认查qicore的
        return  findServerByTypeAndEngineeId(status,type,StrategyDto.QICORE_ENGINEE_ID);
    }
    public static List<StrageServerDto> findServerByTypeAndEngineeId(BackTestServerDto.ServerStatusEnum status,BackTestServerDto.ServerTypeEnum type,int enginetypeId){
        int value = status.getValue();
        int serverType = type.getValue();
        List<StrageServerDto> returnList = new ArrayList<StrageServerDto>();
        List<StrageServerDto> list = findAllServer();
        for(StrageServerDto dto :  list){
            if(dto.status==value && dto.type == serverType && dto.enginetypeId == enginetypeId){
                returnList.add(dto);
            }
        }
        return returnList;
    }
    public  static int updateStrategyStatusByServerId(long serverId,StrategyDto.StrategyStatus oriStatus,StrategyDto.StrategyStatus newStatus){
        String sql = SqlLoader.getSqlById("updateStrategyStatusByServerId");
      return QicDbUtil.updateQicDB(sql,newStatus.value,serverId,oriStatus.value);
    }
    //由于一些不可抗因素导致回测失败需将策略状态进行回滚，以便再次回入回测
    public static int rollBackAfterTestFailure(long serverId){
        return updateStrategyStatusByServerId(serverId,StrategyDto.StrategyStatus.BACKTESTING,StrategyDto.StrategyStatus.SANDBOXTESTING);
    }

    /**
     *更新策略的通过时间
     * @param sid
     */
    public static void updateStrategyPassTime(long[] sid){
        if(sid == null || sid.length == 0){
            return ;
        }
        Object[][] params = new Object[sid.length][1];
        for(int i =0 ;i<sid.length;i++){
            params[i][0] = sid[i];
        }
        String sql = SqlLoader.getSqlById("updateStratedyPassTime");
        QicDbUtil.batchQicDB(sql,params);
    }
   //清空缓存列表
    public static void clearCache(){

        if( serverList_cacehe.size()>0){
            serverList_cacehe.clear();
        }
    }

    public static List<BackTestStrategyDto> findInRuningStrategy(int engineId,long serverId){
        String sql = SqlLoader.getSqlById("findInRuningStrategy");
        List<BackTestStrategyDto> list = QicDbUtil.queryQicDBBeanList(sql,BackTestStrategyDto.class,serverId,engineId, StrategyDto.StrategyStatus.UPSHELF.value);
        if(list!=null && list.size()>0){
            for(BackTestStrategyDto backTestStrategyDto: list){
                backTestStrategyDto.filePath = (SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR)+ backTestStrategyDto.filePath.replace("/","\\"));
            }
        }

        return list;
    }

    /**
     * 获取qia服务器回测结果
     * @param serverId
     * @return
     */
    public  static QiaBackTestResultDto getQiaStrategyBackTestStatus(int serverId){
          String ip = findServerIpById(serverId);
          QiaBackTestResultDto resultDto = new QiaBackTestResultDto();
          resultDto.result = false;
          resultDto.serverId = serverId;
          try{
          WS.HttpResponse response = WS.url(GET_BACKTEST_STATUS_URL_TEMPLATE,ip).timeout("5s").get();

          if(response.success()){
              JsonElement je= response.getJson();
              JsonObject jo = je.getAsJsonObject();
              resultDto.result = jo.get("Result").getAsBoolean();
              Gson deSerializer = new Gson();
              QiaBackTestResultDto.ResultData  data = deSerializer.fromJson( jo.get("Data")
                      , QiaBackTestResultDto.ResultData.class);
              resultDto.data = data;
          }
          }catch (Exception ex){
              Logger.debug("服务器:id=[%s],ip=[%s]:状态：离线中",serverId,ip);
          }
        return resultDto;
    }

    /**
     * 启动qia回测
     * @param serverId
     * @return
     */
    public static QiaBackTestResultDto startBackTestQiaStrategy(int serverId){
        String ip = findServerIpById(serverId);

        QiaBackTestResultDto resultDto = new QiaBackTestResultDto();
        resultDto.result = false;
        resultDto.serverId = serverId;
        try{
        WS.HttpResponse response = WS.url(START_BACKTEST_URL_TEMPLATE,ip).timeout("5s").get();
        if(response.success()){
            JsonElement je= response.getJson();
            JsonObject jo = je.getAsJsonObject();
            resultDto.result = jo.get("Result").getAsBoolean();
        }
        }catch (Exception ex){
            Logger.debug("服务器:id=[%s],ip=[%s]:状态：离线中",serverId,ip);
        }
        return resultDto;
    }

    /**
     * 删除服务器
     * @param id
     */
    public static void delBackTestServer(int id) {
        String sql =SqlLoader.getSqlById("delBackTestServerById");
        QicDbUtil.update(sql,id);
        clearCache();
    }


}
