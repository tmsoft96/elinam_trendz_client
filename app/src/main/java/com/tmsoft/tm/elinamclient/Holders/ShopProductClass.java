package com.tmsoft.tm.elinamclient.Holders;

public class ShopProductClass {
    String shopNumber, shopLogo, shopName, shopItemsMIG, shopItemsManu;

    public ShopProductClass() {
    }

    public ShopProductClass(String shopNumber, String shopLogo, String shopName, String shopItemsMIG, String shopItemsManu) {
        this.shopNumber = shopNumber;
        this.shopLogo = shopLogo;
        this.shopName = shopName;
        this.shopItemsMIG = shopItemsMIG;
        this.shopItemsManu = shopItemsManu;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopItemsMIG() {
        return shopItemsMIG;
    }

    public void setShopItemsMIG(String shopItemsMIG) {
        this.shopItemsMIG = shopItemsMIG;
    }

    public String getShopItemsManu() {
        return shopItemsManu;
    }

    public void setShopItemsManu(String shopItemsManu) {
        this.shopItemsManu = shopItemsManu;
    }
}
