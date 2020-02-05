package com.tmsoft.tm.elinamclient.Holders;

public class cartProducts {
    String postKey, quantity, category, cartPostKey, limitedQuantity;

    public cartProducts() {
    }

    public cartProducts(String postKey, String quantity, String category, String cartPostKey, String limitedQuantity) {
        this.postKey = postKey;
        this.quantity = quantity;
        this.category = category;
        this.cartPostKey = cartPostKey;
        this.limitedQuantity = limitedQuantity;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCartPostKey() {
        return cartPostKey;
    }

    public void setCartPostKey(String cartPostKey) {
        this.cartPostKey = cartPostKey;
    }

    public String getLimitedQuantity() {
        return limitedQuantity;
    }

    public void setLimitedQuantity(String limitedQuantity) {
        this.limitedQuantity = limitedQuantity;
    }
}
