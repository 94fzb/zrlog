package com.zrlog.admin.business.rest.response;


public class ArticleTypeResponseEntry {

    private String alias;
    private String typeName;
    private String remark;
    private Long id;
    private Long amount;
    private Long typeamount;
    private String url;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTypeamount() {
        return typeamount;
    }

    public void setTypeamount(Long typeamount) {
        this.typeamount = typeamount;
    }
}
