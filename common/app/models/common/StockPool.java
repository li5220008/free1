package models.common;

import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 股票池基本信息. (注:对我们来说只能是读.)
 * User: wenzhihong
 * Date: 12-11-7
 * Time: 上午11:46
 */
@Entity
@Table(name = "stp_stockpool")
public class StockPool extends GenericModel {

    @Id
    public Long stockpoolcode;

    public String stockpoolname;

    public Long getId() {
        return stockpoolcode;
    }

    @Override
    public Object _key() {
        return getId();
    }
}
