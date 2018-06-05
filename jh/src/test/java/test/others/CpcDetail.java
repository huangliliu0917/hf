package test.others;

import java.util.List;

public class CpcDetail {
    private int shopid;
    private String shopname;
    private String pictureurl;
    private String recommendlang;
    private double power;
    private String region;
    private String district;
    private String categoryName;
    private String huidesc;
    private String rankingtags;
    private List<DealDetail> deals;

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getRecommendlang() {
        return recommendlang;
    }

    public void setRecommendlang(String recommendlang) {
        this.recommendlang = recommendlang;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getHuidesc() {
        return huidesc;
    }

    public void setHuidesc(String huidesc) {
        this.huidesc = huidesc;
    }

    public String getRankingtags() {
        return rankingtags;
    }

    public void setRankingtags(String rankingtags) {
        this.rankingtags = rankingtags;
    }

    public List<DealDetail> getDeals() {
        return deals;
    }

    public void setDeals(List<DealDetail> deals) {
        this.deals = deals;
    }
}
