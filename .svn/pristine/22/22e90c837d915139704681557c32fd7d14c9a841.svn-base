package models.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;

/**
 * 功能点
 * User: wenzhihong
 * Date: 12-11-7
 * Time: 上午9:59
 */
@Entity
@Table(name = "function_info")
public class FunctionInfo  {
    @Id
    @GeneratedValue
    @SerializedName("a")
    @Expose
    public Long id;

    public String name;

    public String action;

    public String code;

    @ManyToOne
    @JoinColumn(name = "pid")
    //用不上
    public FunctionInfo parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    public Product product;

    @Transient
    @SerializedName("b")
    @Expose
    public long fpid;
}
