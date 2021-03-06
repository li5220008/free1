package controllers;

import annotation.QicFunction;
import bussiness.StockPoolCollectService;
import bussiness.StockPoolDiscussService;
import bussiness.StockPoolsService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.common.BasePlayControllerSupport;
import dto.StockPoolSearchCnd;
import dto.StockPoolsPar;
import dto.StockpoolDto;
import models.common.StockPoolClassify;
import models.common.UserTemplate;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.data.binding.As;
import play.libs.F;
import play.vfs.VirtualFile;
import util.CommonUtils;
import util.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 股票池
 * User: wenzhihong
 * Date: 12-11-12
 * Time: 上午8:49
 */
public class StockPools extends BasePlayControllerSupport {

    //股票池分类列表
    static List<StockPoolClassify> stockPoolClassifies;

    static {
        VirtualFile vf = Play.getVirtualFile("conf/stockPoolClassify.js");
        String json = CommonUtils.readJsonConfigFile2String(vf.inputstream());
        stockPoolClassifies = new Gson().fromJson(json, new TypeToken<List<StockPoolClassify>>() {
        }.getType());
    }


    /**
     * 股票池列表
     */
    @QicFunction(id=9)
    public static void list(StockPoolsPar spp, int pageNo, Long uid) {
        if(spp==null){
            spp= new StockPoolsPar();
        }
        F.T2<List<StockpoolDto>, Page> t2=null;
        if(StringUtils.isNotBlank(spp.strategyName)&&spp.strategyName.equals("hot")){//热门搜索
            t2 = StockPoolsService.hotList(spp,pageNo);
        }else{
            t2 = StockPoolsService.stockPoolyList(spp,pageNo);
        }
        List<StockpoolDto> StockpoolList = t2._1;
        Page page = t2._2;
        //用户的自定义搜索条件
        List<UserTemplate> utList = UserTemplate.fetchUserSearchCond(uid, 2);

        List<StockPoolClassify> spclist = stockPoolClassifies; //这里要加上这个局部变量, 不然在写html的时候, ide不会自动提示

        //得到已收藏的股票池
        Set<Integer> collectSet = StockPoolCollectService.queryStockPoolCollectMap(StockpoolList, uid);
        Set<Integer> discussSet = StockPoolDiscussService.queryStockPoolDiscussMap(uid);

        render(utList, spclist, StockpoolList,spp, collectSet, discussSet,page);
    }
    public static void getStarLevelAndHot(@As(",")String[] stpIds){
        List<StockpoolDto> list = StockPoolsService.getStpStarLevelAndHot(stpIds);
        renderJSON(list);
    }

    /**
     * 高级搜索
     * @param pageNo 第几页. 从1开始
     */
    @QicFunction(id=9)
    public static void advanceSearch(StockPoolSearchCnd cnd, int pageNo, Long uid) {
        StockPoolsPar spp = new StockPoolsPar();//初始化股票列表的参数
        F.T2<List<StockpoolDto>, Page> t2 = StockPoolsService.advanceSearch(cnd, pageNo);
        List<StockpoolDto> StockpoolList = t2._1;
        Page page = t2._2;

        //用户的自定义搜索条件
        List<UserTemplate> utList = UserTemplate.fetchUserSearchCond(uid, 2);

        List<StockPoolClassify> spclist = stockPoolClassifies;

        //得到已收藏的股票池
        Set<Integer> collectSet = StockPoolCollectService.queryStockPoolCollectMap(StockpoolList, uid);
        Set<Integer> discussSet = StockPoolDiscussService.queryStockPoolDiscussMap(uid);

        boolean advanceSearch = true;

        render("@list",spp, utList, spclist, StockpoolList, collectSet, discussSet, page, cnd, advanceSearch);
    }


    /**
     * 保存评论
     * @param uid
     * @param spid
     * @param star
     */
    @QicFunction(id=10)
    public static void comment(Long uid,Long spid,int star){
        boolean flag = false;

        if (Logger.isDebugEnabled()) {
            Logger.debug("enter");
            Logger.debug("spid=" + spid);
            Logger.debug("star=" + star);
        }
        if (uid != null && spid != null) {
            //usd.disTime=new Date();
            flag = StockPoolDiscussService.saveDiscuss(star, uid, spid);
        }
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("commented", flag);
        Logger.debug("flag_-------------------------------------------" + flag);
        renderJSON(json);
    }

    public static void websocket(){
        System.out.println("==========websocket");
         render();
    }
}
