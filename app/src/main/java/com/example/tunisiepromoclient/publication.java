package com.example.tunisiepromoclient;

import com.google.type.Date;

public class publication {
    private String pubId;
    private String cName;
    private String dateDebut;
    private String dateFin;
    private String imageUrl;
    private String promoName;
    private String userId;

    public publication(String pubId, String userId,String cName,  String promoName,String dateDebut, String dateFin, String imageUrl) {
        this.pubId = pubId;
        this.userId=userId;
        this.cName = cName;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.imageUrl = imageUrl;
        this.promoName = promoName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public publication() {
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
