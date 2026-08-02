package com.masonsaver;
public class EbayBookInfo{

    private double lowestPrice; 
    private double averageUsedPrice; 
    private String condition; 
    private String listingUrl; 


    public double getLowestEbayPrice(){
        return lowestPrice; 
    }

    public double getAverageUsedPrice(){
        return averageUsedPrice;
    }

    public String getCondition(){
        return condition; 
    }

    public String getListingUrl(){
        return listingUrl; 
    }

    public void setlowestPrice(double lowestPrice){
        this.lowestPrice = lowestPrice; 
    }

    public void setAverageUsedPrice(double averageUsedPrice){
        this.averageUsedPrice = averageUsedPrice; 
    }

    public void setCondition(String condition){
        this.condition = condition; 
    }

    public void setListingUrl(String listingUrl){
        this.listingUrl = listingUrl;
    }











}
