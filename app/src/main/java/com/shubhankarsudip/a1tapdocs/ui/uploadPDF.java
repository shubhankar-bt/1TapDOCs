package com.shubhankarsudip.a1tapdocs.ui;

public class uploadPDF {
    public String name;
    public String url;
    public String userId;

    public uploadPDF() {
    }

    public uploadPDF (String name, String url , String userId ){
        this.name = name;
        this.url = url;
        this.userId = userId;

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }






    public void setName(String name) { this.name = name;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }
}
