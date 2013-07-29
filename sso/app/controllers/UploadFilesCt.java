package controllers;

import annotation.QicFunction;
import business.LogInfosService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.common.BasePlayControllerSupport;
import dbutils.QicDbUtil;
import dbutils.SqlLoader;
import models.common.StrategyBaseDto;
import org.apache.commons.io.IOUtils;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Util;
import play.mvc.With;
import util.*;

import java.io.File;
import java.util.Map;

/**
 * 上传文件. 从mange工程的 controllers.UploadFiles 拷贝一份过来
 * User: wenzhihong
 * Date: 13-5-7
 * Time: 下午1:43
 */
@With(value = {LoginTokenCheckIntercept.class, GeneralIntercept.class})
public class UploadFilesCt extends BaseController {

    /**
     * 检查策略名称是否唯一. 唯一返回 true, 否则返回 false
     */
    public static void checkStategyOnlyName(String name) {
        boolean onlyName = checkStategyName(name);
        if (onlyName) {
            renderText("true");
        }else {
            renderText("false");
        }
    }

    @Util
    static boolean checkStategyName(String name){
        String sql = SqlLoader.getSqlById("checkStategyName");
        long count = QicDbUtil.queryCount(sql, name);
        return count == 0;
    }

    /**
     * 上传策略. 给iquant使用
     * @param file 策略附件
     * @param otherParam 策略的其它参数. json格式
     * json参数格式为:
     <pre>
     {
     	"sname" : "策略名称",
     	"tradeType: 1, // 策略类型
     	"tradeVariety" : 1, //交易品种
     	"provider" : "策略师姓名",
     	"providerDesp" : "策略师简介",
     	"desp" : "策略简介",
     	"lookBackStime" : '2012-03-12', //回测开始时间
     	"lookBackEtime" : '2013-03-13' //回测结束时间
     }
    </pre>
    交易类型: 1. 选股型 2. 择时型 3. 交易型 4. 其他
    交易品种: 1. 股票 2. 期货 3. 混合
     */
    public static void uploadStrage(File file, String otherParam) {
        if (file != null) {
            Logger.info("上传的文件" + file.getName());
        }else {
            Logger.info("没有收到上传的文件");
        }

        if (otherParam != null) {
            Logger.info("otherParam==" + otherParam);
        }else {
            Logger.info("没有收到 otherParam");
        }


        LoginTokenCompose compose = LoginTokenCompose.current();
        //提供判断名称是否唯一的接口
        try {

            //把内容存入数据库
            Gson gson = GsonUtil.createGson();
            Map<String, Object> paramMap = gson.fromJson(otherParam, new TypeToken<Map<String, Object>>() {
            }.getType());

            //检查重名
            if (paramMap.containsKey("sname")) {
                boolean onlyName = checkStategyName(paramMap.get("sname").toString());
                if (!onlyName) {
                    setSuccessFlag(false);
                    setMessage(String.format("策略名:%s已被占用", paramMap.get("sname")));
                    renderJSON(getSampleResponseMap());
                }
            }


            if (file == null) {
                setSuccessFlag(false);
                setMessage(SystemResponseMessage.UPLOAD_FILE_EMPTY_RSP);
                renderJSON(getSampleResponseMap());
                return;
            }
            if (file.length() > Constants.MAX_SIZE_OF_ZIP_STRATEGY_FILE) {
                setSuccessFlag(false);
                setMessage(String.format(SystemResponseMessage.UPLOAD_FILE_OUT_OF_SIZE_RSP, Constants.MAX_SIZE_OF_ZIP_STRATEGY_FILE >> 20));
                renderJSON(getSampleResponseMap());
            }
            if (!file.getName().endsWith(".zip")) {
                setSuccessFlag(false);
                setMessage("文件类型错误，只能上传zip压缩文件");
                renderJSON(getSampleResponseMap());
            }

            //解压保存文件
            Map<String, Object> map = QicFileUtil.saveStrategyForIquant(file);

            //上传用户id
            paramMap.put("stupUid", compose.uid);
            //状态
            paramMap.put("status", StrategyBaseDto.StrategyStatus.CHECKING.value); //刚上传

            paramMap.putAll(map);

            if (!paramMap.containsKey("stkcdContent")) { //标的内容
                Logger.warn("策略里没有标的内容,设置为空");
                paramMap.put("stkcdContent", "");
            }

            if (!paramMap.containsKey("fundsProportion")) {
                Logger.warn("策略里没有资金使用比例,设置为null");
                paramMap.put("fundsProportion", null);
            }

            paramMap.put("agentIp", "127.0.0.1"); //先写死??

            String nameSql = SqlLoader.getSqlById("strateSave4Iquant");
            QicDbUtil.insertWithNameParam(nameSql, paramMap);

            nameSql = SqlLoader.getSqlById("syncStrategyToQsm4Iquant");
            QicDbUtil.insertWithNameParam(nameSql, paramMap);


        } catch (Exception e) {
            e.printStackTrace();
            setSuccessFlag(false);
            setMessage(e.getMessage());
            renderJSON(getSampleResponseMap());
        }

        renderJSON(getSampleResponseMap());
    }

}
