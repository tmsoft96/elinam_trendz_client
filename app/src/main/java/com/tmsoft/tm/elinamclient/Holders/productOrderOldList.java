package com.tmsoft.tm.elinamclient.Holders;

public class productOrderOldList {
    private String productId, productPicture1, productName, productPrice, orderConfirm, orderTime, orderDate;

    public productOrderOldList() {
    }

    public productOrderOldList(String productId, String productPicture1, String productName, String productPrice, String orderConfirm, String orderTime, String orderDate) {
        this.productId = productId;
        this.productPicture1 = productPicture1;
        this.productName = productName;
        this.productPrice = productPrice;
        this.orderConfirm = orderConfirm;
        this.orderTime = orderTime;
        this.orderDate = orderDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductPicture1() {
        return productPicture1;
    }

    public void setProductPicture1(String productPicture1) {
        this.productPicture1 = productPicture1;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getOrderConfirm() {
        return orderConfirm;
    }

    public void setOrderConfirm(String orderConfirm) {
        this.orderConfirm = orderConfirm;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
