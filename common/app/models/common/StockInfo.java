package models.common;

import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * User: wenzhihong
 * Date: 13-2-21
 * Time: 上午9:37
 */
@Entity
@Table(name = "stk_stockinfo")
public class StockInfo extends GenericModel {
    @Id
    public Long securityId;

    public String symbol;

    public Date listeddate;

    public String exchangecode;

    public Long getId() {
        return securityId;
    }

    @Override
    public Object _key() {
        return getId();
    }
}
