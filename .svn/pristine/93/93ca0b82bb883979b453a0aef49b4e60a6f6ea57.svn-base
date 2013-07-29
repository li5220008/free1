package controllers;


import bussiness.BackTestService;
import dto.StrageServerDto;
import play.mvc.Controller;

import java.util.List;

/**
 * 加入回测服务器
 * User: panzhiwei
 * Date: 13-5-28
 * Time: 上午10:18
 * To change this template use File | Settings | File Templates.
 */

public class Servers extends Controller {

    public static void serverInfo() {
        render();
    }

    public static void addServer(StrageServerDto backTestServerDto) {
        if (backTestServerDto != null) {
            backTestServerDto.status = 0;
            BackTestService.addServer(backTestServerDto);
        }
        serverList();
    }

    public static void serverList() {
        List<StrageServerDto> list = BackTestService.findServerList();
        render(list);
    }

    public static void delBackTestServer(int id) {
        BackTestService.delBackTestServer(id);
        renderText("删除成功!");
    }

    public static void changeStatus(int id, int status) {
        BackTestService.updateServerStatus(status, id);
    }
}
