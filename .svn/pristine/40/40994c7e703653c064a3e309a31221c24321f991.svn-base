package business;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import dbutils.QicDbUtil;
import dbutils.QicoreDbUtil;
import dbutils.QsmDbUtil;
import dbutils.SqlLoader;
import models.common.*;
import play.Logger;
import play.db.DB;
import util.Constants;
import util.Page;
import util.QicFileUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 策略超市基本信息展示业务方法
 * User: liangbing
 * Date: 12-11-9
 * Time: 上午9:10
 * To change this template use File | Settings | File Templates.
 */
public class StrategyBaseService {


    /**
     * 上传策略
     */
    public static boolean insertStrategy(StrategyDto strategyDto, String[] files, long uid) throws SQLException {
        String dynamicDir = "";
        Connection conn = DB.getDBConfig().getConnection();
        String sql = SqlLoader.getSqlById("insertStrategy");
        //conn.setTransactionIsolation(TransactionIsolation.);设置事务的隔离级别
        try {

            PreparedStatement statement = conn.prepareStatement(sql);
            //uuid
            String strategyId = UUID.randomUUID().toString().replaceAll("-", "");
            statement.setString(1, strategyId);
            //策略名
            statement.setString(2, strategyDto.sname);
            //策略类型
            statement.setInt(3, strategyDto.stype);
            //交易品种
            statement.setInt(4, strategyDto.tradeVariety);
            //提供者
            statement.setString(5, strategyDto.provider);
            //提供者信息
            statement.setString(6, strategyDto.providerDesp);
            //提供简介
            statement.setString(7, strategyDto.desp);
            //策略状态
            statement.setInt(8, StrategyBaseDto.StrategyStatus.CHECKING.value);
            //上传用户的uid,暂时不填
            statement.setLong(9, uid);
            statement.setString(10, strategyDto.customerLookbackStartTime);
            statement.setString(11, strategyDto.customerLookbackEndTime);
            //引擎ID
            statement.setInt(12, strategyDto.enginetypeId);
            statement.setString(13, strategyDto.customerLookbackStartTime);
            statement.setString(14, strategyDto.customerLookbackEndTime);
            //
            //赋值
            statement.executeUpdate();
            for (String tmpFile : files) {
                dynamicDir = String.format("%1$ty" + File.separator + "%1$tm" + File.separator + "%1$td" + File.separator + "%1$tH" + File.separator + "%1$tM" + File.separator, System.currentTimeMillis());
                File file = new File(tmpFile);
                File newFile = null;
                String fileSuffix = file.getName().substring(file.getName().lastIndexOf("."));
                /*    if(file.getName().endsWith(".xml")){
                 newFile = new File(file.getParentFile().getAbsolutePath(),strategyId + ".xml");
                }else if(file.getName().endsWith(".dll")){
                  newFile = new File(file.getParentFile().getAbsolutePath(),strategyId + ".dll");
                }else {
                    continue;
                }*/
                if (strategyDto.enginetypeId == StrategyBaseDto.QICORE_ENGINEE_ID) {//QICORE
                    newFile = new File(file.getParentFile().getAbsolutePath(), strategyId + fileSuffix);
                    //如果回测时间有修改 则修改文件
                    if(file.getName().endsWith(".xml") && (!strategyDto.lookbackEtime.equals(strategyDto.customerLookbackEndTime)||!strategyDto.lookbackStime.equals(strategyDto.customerLookbackStartTime))){//修改回测时间
                        QicFileUtil.updateQicoreXml(file,strategyDto.customerLookbackStartTime.replace("-",""),strategyDto.customerLookbackEndTime.replace("-",""));
                    }
                } else {//QIA
                    newFile = new File(file.getParentFile().getAbsolutePath(), file.getName().substring(0, file.getName().lastIndexOf(".")) + fileSuffix);
                    if(file.getName().endsWith("BackTestCfg.xml")){
                        QicFileUtil.turnOffExportExcelAndSaveResult(file);
                    }
                }
                file.renameTo(newFile);
                QicFileUtil.saveStrategyToOfficai(newFile, strategyId, dynamicDir);//移动正式文件库中,用uuid作文件 防止文件名重复
                QicFileUtil.deleteFile(newFile);//将临时文件库中文件删除
            }
            String qsmSql = SqlLoader.getSqlById("syncStrategyToQsm");
            String loadBaseDir = SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR);//这里的baseDir一定要/结尾
            // QicDbUtil.updateQicDB(qsmSql, strategyId, strategyDto.sname, loadBaseDir + strategyId);
            QicDbUtil.updateQicDB(qsmSql, strategyId, strategyDto.sname, dynamicDir + strategyId, "127.0.0.1");//2012-12-24改的
            return true;
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            //出现异常,删除临时文件

            conn.rollback();//只要一方面出异常则删除文件 ，数据库回滚
            return false;
            //throw new DatabaseException(e.getMessage(),e);
        }

    }

    public static StrategyBaseDto findStrategyByName(String sname) {
        String sql = SqlLoader.getSqlById("findStrategyByName");
        return QicDbUtil.queryQicDBSingleBean(sql, StrategyBaseDto.class, sname);
    }


    public static StrategyBaseinfo findStrategyById(long id) {
        String sql = SqlLoader.getSqlById("findStrategyById");
        return QicDbUtil.queryQicDBSingleBean(sql, StrategyBaseinfo.class, id);
    }

    /**
     * 查询一批策略
     *
     * @param ids
     * @return
     */
    public static List<StrategyBaseinfo> findStrategysByIds(String ids[]) {
        if (ids == null || ids.length == 0) {
            return new ArrayList<StrategyBaseinfo>();
        }

        Long[] list = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {

            list[i] = Long.valueOf(ids[i]);
        }
        return findStrategysByIds(list);
    }

    public static List<StrategyBaseinfo> findStrategysByIds(Long ids[]) {
        StringBuffer sql = new StringBuffer(SqlLoader.getSqlById("findStrategysByIds"));
        sql.append(" and id IN (");
        for (int i = 0; i < ids.length; i++) {
            sql.append("?");
            if (i < ids.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return QicDbUtil.queryQicDBBeanList(sql.toString(), StrategyBaseinfo.class, ids);
    }

    public static StrategyDto findStrategyByUUID(String strUUID) {
        String sql = SqlLoader.getSqlById("findStrategyByUUID");
        return QicDbUtil.queryQicDBSingleBean(sql, StrategyDto.class, strUUID);
    }

    public static List<QiaStrategyDto> findQiaStrategyByStrategyIds(List<Long> stids) {
        if (stids == null || stids.size() == 0) {
            return Lists.newArrayList();
        }
        Joiner joiner = Joiner.on(",");
        String ids = joiner.join(stids);
        String sql = SqlLoader.getSqlById("findQiaStrategyByStrategyIds");
        return QicDbUtil.queryQicDBBeanList(sql.replace("#stids#", ids), QiaStrategyDto.class);
    }

    //策略持仓
    public static List<StrategyPositionDto> getStrategyPosition(long stid, int pageNo) {
        StrategyBaseinfo strategyBaseinfo = findStrategyById(stid);

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

    //绩效指标
    public static IndicatorDto getindicator(long stid, int type) {
        String sql = SqlLoader.getSqlById("Indicator");
        IndicatorDto indicator = QicDbUtil.queryQicDBSingleBean(sql, IndicatorDto.class, stid, type);
        /*if(indicator == null){
            IndicatorDto indicatorDto = new IndicatorDto();
            indicator = indicatorDto;
        }*/
        return indicator;
    }

    //QIA 绩效指标
    public static QiaIndicatorDto getQiaIndicatorDto(long stid, int type) {
        String sql = SqlLoader.getSqlById("qiaIndicatorSql");
        QiaIndicatorDto indicator = QicDbUtil.queryQicDBSingleBean(sql, QiaIndicatorDto.class, stid, type);
        /*if(indicator == null){
            indicator = new QiaIndicatorDto();
        }*/
        return indicator;
    }

    //成交记录
    public static List<ExecutionRecordDto> getExecutionRecord(Long stid, int pageNo) {
        StrategyBaseinfo strategyBaseinfo = findStrategyById(stid);

        String sql = SqlLoader.getSqlById("ExecutionRecord");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicoreDbUtil.queryQicDbCount(coutSql.toString(), strategyBaseinfo.stUuid, strategyBaseinfo.lookbackEtime);
        Page page = new Page(total.intValue(), pageNo);
        sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";
        if (pageNo * page.pageSize > total) {
            return null;
        }
        List<ExecutionRecordDto> executionRecordDtoList = QicoreDbUtil.queryQicoreDBBeanList(sql, ExecutionRecordDto.class, strategyBaseinfo.stUuid, strategyBaseinfo.lookbackEtime);
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

    //委托记录
    public static List<AuthorizeRecordDto> getAuthorizeRecord(long stid, int pageNo) {
        StrategyBaseinfo strategyBaseinfo = findStrategyById(stid);

        String sql = SqlLoader.getSqlById("AuthorizeRecord");
        StringBuilder coutSql = new StringBuilder("select count(*) from (\n" + sql + "\n) distTable  \n");
        Long total = QicoreDbUtil.queryQicDbCount(coutSql.toString(), strategyBaseinfo.stUuid, strategyBaseinfo.lookbackEtime);
        Page page = new Page(total.intValue(), pageNo);
        if (pageNo * page.pageSize > total) {
            return null;
        }
        sql += " limit " + page.beginIndex + "," + page.pageSize + "\n";
        List<AuthorizeRecordDto> authorizeRecordDtoList = QicoreDbUtil.queryQicoreDBBeanList(sql, AuthorizeRecordDto.class, strategyBaseinfo.stUuid, strategyBaseinfo.lookbackEtime);
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

    public static boolean synStrateToQsm(List<StrategyBaseinfo> list, int serverId) {
        //List<StrategyBaseinfo> list =findStrategysByIds(ids);
        String ip = BackTestBaseService.findServerIpById(serverId);
        if (list == null || list.size() == 0) {
            return false;
        } else {
            //String basePath = SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR);
            String qsmSql = SqlLoader.getSqlById("syncStrategyToQsm");
            String queryFilePathSql = SqlLoader.getSqlById("queryFilePath");
            Object[][] params = new Object[list.size()][4];
            String runBasePath = SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR);
            for (int row = 0; row < list.size(); row++) {
                params[row][0] = list.get(row).stUuid;
                params[row][1] = list.get(row).name;
                //params[row][2] = basePath + list.get(row).stUuid;//是否要拼要基地址? 改为查询数据库得到地址
                params[row][2] = runBasePath + QicDbUtil.queryQicDBSingleBean(queryFilePathSql, QsmStrategyDto.class, list.get(row).stUuid).filePath.replace("/", "\\");//是否要拼要基地址? 改为查询数据库得到地址
                params[row][3] = ip;//是否要拼要基地址?
            }
            QsmDbUtil.batchQsmDB(qsmSql, params);
            return true;
        }

    }

    public static boolean syncProductToQsm(long strategyVirtualProductRelatedId, long strategyId) {
        //List<StrategyBaseinfo> list =findStrategysByIds(ids);
        try {
            String qsmSql = SqlLoader.getSqlById("syncStrategyToQsm");
            String queryFilePathSql = SqlLoader.getSqlById("queryFilePath");
            StrategyBaseinfo strategyBaseinfo = findStrategyById(strategyId);
            String strategyUuid = strategyBaseinfo.stUuid;
            QsmStrategyDto qsmStrategyDto = QicDbUtil.queryQicDBSingleBean(queryFilePathSql, QsmStrategyDto.class, strategyUuid);

            String ip = BackTestBaseService.findServerIpById(Integer.valueOf(qsmStrategyDto.agentIp));//其实之前存的是服务器id
           // String runBasePath = SystemConfigService.get(Constants.OTHERS_LOAD_STRATEGY_BASE_DIR);
            String realFilePath =  qsmStrategyDto.filePath.replace("/", "\\");
            QsmDbUtil.updateQsmDB(qsmSql, String.valueOf(strategyVirtualProductRelatedId), qsmStrategyDto.strategyName, realFilePath, ip);
            Logger.info("添加运行策略到qsm库中 关联id=%d,策略id=%d,finalPath=%s",strategyVirtualProductRelatedId,strategyId,realFilePath);
            return true;
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            return false;
        }

    }
    public static boolean deleteProductFromQsm(long strategyVirtualProductRelatedId) {
        //List<StrategyBaseinfo> list =findStrategysByIds(ids);
        try {
            String qsmSql = SqlLoader.getSqlById("deleteProductFromQsm");
            QsmDbUtil.updateQsmDB(qsmSql, String.valueOf(strategyVirtualProductRelatedId));
            Logger.info("从qsm库中 删除id=%d",strategyVirtualProductRelatedId);
            return true;
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            return false;
        }

    }

}
