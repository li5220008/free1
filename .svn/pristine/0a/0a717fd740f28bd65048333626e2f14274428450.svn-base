package controllers;

import job.StrategyYieldSmallPicJob;
import job.common.TestDataJob;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        //new StrategyYieldSmallPicJob().now();
        Users.gotoLogin();
        render();
    }
    public static void bb() {
        new TestDataJob().now();
        renderText("生成初始化数据");
    }

}