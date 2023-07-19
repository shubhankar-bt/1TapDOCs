package com.shubhankarsudip.a1tapdocs.ui;

public class uploadPDF {
    public String key;
    public String name;
    public String url;
    public String userId;
    public String uniqeKey;

    public uploadPDF() {
    }


    public uploadPDF (String name, String url , String userId, String uniqeKey ){
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.uniqeKey = uniqeKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUniqeKey() {
        return uniqeKey;
    }

    public void setUniqeKey(String uniqeKey) {
        this.uniqeKey = uniqeKey;
    }
}
