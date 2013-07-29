package controllers;

import annotation.QicFunction;
import bussiness.StrategyContrastService;
import bussiness.StrategyFavoriteService;
import bussiness.UserStrategyCollectService;
import controllers.common.BasePlayControllerSupport;
import dto.StrategyContrastDto;
import dto.StrategySearchCnd;
import models.common.StrategyBaseDto;
import models.common.UserTemplate;
import play.libs.F;
import util.Page;

import java.util.List;
import java.util.Set;

/**
 * 我的收藏 策略展示
 * User: liangbing
 * Date: 12-11-12
 * Time: 下午1:24
 */
public class StrategyFavorite extends BasePlayControllerSupport {


    /**
     * 策略展示
     */
    @QicFunction(id=5)
    public static void strategyList(int myselect, String content,boolean isTableList,int pageNo, Long uid) {


        F.T2<List<StrategyBaseDto>, Page> t2 = StrategyFavoriteService.strategyList(myselect,content, pageNo,uid);
        List<StrategyBaseDto> sbdList = t2._1;
        Page page = t2._2;

        Set<Integer> collectSet = UserStrategyCollectService.queryUserCollectSet(sbdList, uid);

        //用户的自定义搜索条件
        List<UserTemplate> utList = UserTemplate.fetchUserSearchCond(uid, 1);
        if(isTableList){
            render("@strategyTableList",sbdList, myselect, content, collectSet, utList, page,isTableList);
        }else{
            render(sbdList, myselect, content, collectSet, utList, page,isTableList);
        }
    }

    /**
     *
     * 策略对比
     * @Auther liuhongjiang
     * @param idArray 策略ID
     */
    @QicFunction(id=3)
    public static  void  strategyContrast(String idArray[]){
        List<StrategyContrastDto> list = StrategyContrastService.strategyContrast(idArray);
        String[] dates = StrategyContrastService.strategyContrastForPictrue(idArray);
        render(list, dates);
    }

    /**
     * 高级搜索
     * @param pageNo 第几页. 从1开始
     */
    @QicFunction(id=5)
    public static void advanceSearch(StrategySearchCnd cnd,int myselect,boolean isTableList ,int pageNo, Long uid) {
        F.T2<List<StrategyBaseDto>, Page> t2 = StrategyFavoriteService.advanceSearch(cnd,myselect,pageNo,uid);
        List<StrategyBaseDto> sbdList = t2._1;
        Page page = t2._2;

        Set<Integer> collectSet = UserStrategyCollectService.queryUserCollectSet(sbdList, uid);

        //用户的自定义搜索条件
        List<UserTemplate> utList = UserTemplate.fetchUserSearchCond(uid, 1);

        boolean advanceSearch = true;

        if(isTableList){
            render("@strategyTableList" ,sbdList, pageNo, collectSet, utList, page, advanceSearch, cnd,myselect,isTableList);
        }else{
            render("@strategyList" ,sbdList, pageNo, collectSet, utList, page, advanceSearch, cnd,myselect,isTableList);

        }
    }
}
