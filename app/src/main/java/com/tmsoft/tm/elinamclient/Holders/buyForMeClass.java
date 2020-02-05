package com.tmsoft.tm.elinamclient.Holders;

public class buyForMeClass {
    private String productName, productUrgent, productQuantity, message, productImage, orderDate, orderTime;

    public buyForMeClass() {
    }

    public buyForMeClass(String productName, String productUrgent, String productQuantity, String message, String productImage, String orderDate, String orderTime) {
        this.productName = productName;
        this.productUrgent = productUrgent;
        this.productQuantity = productQuantity;
        this.message = message;
        this.productImage = productImage;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrgent() {
        return productUrgent;
    }

    public void setProductUrgent(String productUrgent) {
        this.productUrgent = productUrgent;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
