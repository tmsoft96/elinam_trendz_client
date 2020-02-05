package com.tmsoft.tm.elinamclient.Holders;

public class cartViewOldOrder {
    String userId, orderDate, orderTime, orderConfirm, paymentAmountPaid,paymentConfirm, deliverySuccess, productId;

    public cartViewOldOrder() {
    }

    public cartViewOldOrder(String userId, String orderDate, String orderTime, String orderConfirm, String paymentAmountPaid, String paymentConfirm, String deliverySuccess, String productId) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderConfirm = orderConfirm;
        this.paymentAmountPaid = paymentAmountPaid;
        this.paymentConfirm = paymentConfirm;
        this.deliverySuccess = deliverySuccess;
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getOrderConfirm() {
        return orderConfirm;
    }

    public void setOrderConfirm(String orderConfirm) {
        this.orderConfirm = orderConfirm;
    }

    public String getPaymentAmountPaid() {
        return paymentAmountPaid;
    }

    public void setPaymentAmountPaid(String paymentAmountPaid) {
        this.paymentAmountPaid = paymentAmountPaid;
    }

    public String getPaymentConfirm() {
        return paymentConfirm;
    }

    public void setPaymentConfirm(String paymentConfirm) {
        this.paymentConfirm = paymentConfirm;
    }

    public String getDeliverySuccess() {
        return deliverySuccess;
    }

    public void setDeliverySuccess(String deliverySuccess) {
        this.deliverySuccess = deliverySuccess;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
