package com.example.a15927.bottombardemo.functiontools;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/28.
 */

public class ItemGoods implements Serializable {
    private int opType;     //操作类型（发布，维护等）
    private String goodsID;  //ID
    private String goodsType; //商品所属类
    private String goodsName; //商品名
    private float price;       // 价格
    private String unit;      //单位
    private int quality;   //数量
    private String userid;   //发布人ID
    private String goodsImg; //商品图片
    private String uname;
    private String uphone;
    private String qq;
    private String weixin;
    private String goodsTypeName; //所属类名（可选字段）
    private int sex;
    private String description;
    private String Token;


    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }

    @Override
    public String toString() {
        return "ItemGoods{" +
                "opType=" + opType +
                ", goodsID='" + goodsID + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", quality=" + quality +
                ", userid='" + userid + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", uname='" + uname + '\'' +
                ", uphone='" + uphone + '\'' +
                ", qq='" + qq + '\'' +
                ", weixin='" + weixin + '\'' +
                ", goodsTypeName='" + goodsTypeName + '\'' +
                ", sex=" + sex +
                ", description='" + description + '\'' +
                ", Token='" + Token + '\'' +
                '}';
    }
}
