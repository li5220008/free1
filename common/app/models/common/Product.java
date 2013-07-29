package models.common;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * 产品描述
 * User: wenzhihong
 * Date: 12-4-12
 * Time: 下午4:56
 */
@Entity
@Table(name = "sys_product_info")
public class Product extends Model {
    @Column(nullable = false, length = 100, unique = true)
    public String name;

    @Temporal(TemporalType.TIMESTAMP)
    public Date utime = new Date();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    public Set<FunctionInfo> functions;

    public Product(String name) {
        this.name = name;
        functions = new TreeSet<FunctionInfo>();
    }

}
