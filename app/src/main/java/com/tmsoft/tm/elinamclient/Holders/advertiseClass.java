package com.tmsoft.tm.elinamclient.Holders;

public class advertiseClass {
    private String advertisementSlot, advertiseId, productDiscountPrice, productDiscountPercent,
            isProductDiscount, advertisementDateAndTime, advertisementEvent, productName, productPicture, productPrice;

    public advertiseClass() {
    }

    public advertiseClass(String advertisementSlot, String advertiseId, String productDiscountPrice, String productDiscountPercent, String isProductDiscount, String advertisementDateAndTime, String advertisementEvent, String productName, String productPicture, String productPrice) {
        this.advertisementSlot = advertisementSlot;
        this.advertiseId = advertiseId;
        this.productDiscountPrice = productDiscountPrice;
        this.productDiscountPercent = productDiscountPercent;
        this.isProductDiscount = isProductDiscount;
        this.advertisementDateAndTime = advertisementDateAndTime;
        this.advertisementEvent = advertisementEvent;
        this.productName = productName;
        this.productPicture = productPicture;
        this.productPrice = productPrice;
    }

    public String getAdvertisementSlot() {
        return advertisementSlot;
    }

    public void setAdvertisementSlot(String advertisementSlot) {
        this.advertisementSlot = advertisementSlot;
    }

    public String getAdvertiseId() {
        return advertiseId;
    }

    public void setAdvertiseId(String advertiseId) {
        this.advertiseId = advertiseId;
    }

    public String getProductDiscountPrice() {
        return productDiscountPrice;
    }

    public void setProductDiscountPrice(String productDiscountPrice) {
        this.productDiscountPrice = productDiscountPrice;
    }

    public String getProductDiscountPercent() {
        return productDiscountPercent;
    }

    public void setProductDiscountPercent(String productDiscountPercent) {
        this.productDiscountPercent = productDiscountPercent;
    }

    public String getIsProductDiscount() {
        return isProductDiscount;
    }

    public void setIsProductDiscount(String isProductDiscount) {
        this.isProductDiscount = isProductDiscount;
    }

    public String getAdvertisementDateAndTime() {
        return advertisementDateAndTime;
    }

    public void setAdvertisementDateAndTime(String advertisementDateAndTime) {
        this.advertisementDateAndTime = advertisementDateAndTime;
    }

    public String getAdvertisementEvent() {
        return advertisementEvent;
    }

    public void setAdvertisementEvent(String advertisementEvent) {
        this.advertisementEvent = advertisementEvent;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPicture() {
        return productPicture;
    }

    public void setProductPicture(String productPicture) {
        this.productPicture = productPicture;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
