package com.tmsoft.tm.elinamclient.Holders;

public class allProductsClass {
    private String UserID, ProductKey, ProductName, ProductPrice, ProductCategory,
            ProductPicture1, ProductDiscount, ProductDiscountPercentage, ProductDiscountPrice;

    public allProductsClass() {
    }

    public allProductsClass(String userID, String productKey, String productName, String productPrice, String productCategory, String productPicture1, String productDiscount, String productDiscountPercentage, String productDiscountPrice) {
        UserID = userID;
        ProductKey = productKey;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductCategory = productCategory;
        ProductPicture1 = productPicture1;
        ProductDiscount = productDiscount;
        ProductDiscountPercentage = productDiscountPercentage;
        ProductDiscountPrice = productDiscountPrice;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductPicture1() {
        return ProductPicture1;
    }

    public void setProductPicture1(String productPicture1) {
        ProductPicture1 = productPicture1;
    }

    public String getProductDiscount() {
        return ProductDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        ProductDiscount = productDiscount;
    }

    public String getProductDiscountPercentage() {
        return ProductDiscountPercentage;
    }

    public void setProductDiscountPercentage(String productDiscountPercentage) {
        ProductDiscountPercentage = productDiscountPercentage;
    }

    public String getProductDiscountPrice() {
        return ProductDiscountPrice;
    }

    public void setProductDiscountPrice(String productDiscountPrice) {
        ProductDiscountPrice = productDiscountPrice;
    }
}
