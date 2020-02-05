package com.tmsoft.tm.elinamclient.Holders;

public class favoriteProducts {
    private String UserId, productKey;

    public favoriteProducts() {
    }

    public favoriteProducts(String userId, String productKey) {
        UserId = userId;
        this.productKey = productKey;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }
}
