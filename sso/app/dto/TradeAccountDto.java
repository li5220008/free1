package dto;

import models.common.BaseUserResourceDtoSupport;

/**
 * 资金帐号
 * User: wenzhihong
 * Date: 13-5-2
 * Time: 下午6:52
 */
public class TradeAccountDto extends BaseUserResourceDtoSupport {
    public String name;

    public String account;

    public String password;

    public Integer type;

    //交易柜台
    public String clientId;

    //帐号命令路由地址,由ORS配置并提供
    public String targetCompId;

    //投机/套保标识. 投机(Speculation)/套保(Hedge)
    // 0. 投机(Speculation)
    // 1. 套保(Hedge)
    public Integer hedgeType;

}
