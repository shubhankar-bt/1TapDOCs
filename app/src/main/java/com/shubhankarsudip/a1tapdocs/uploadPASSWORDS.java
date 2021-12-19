package com.shubhankarsudip.a1tapdocs;

public class uploadPASSWORDS {
    String WebsiteName,WebsiteUser,WebsitePassword ;

    public uploadPASSWORDS() {
    }


    public uploadPASSWORDS(String websiteName, String websiteUser, String websitePassword, String uniqueKey) {
        WebsiteName = websiteName;
        WebsiteUser = websiteUser;
        WebsitePassword = websitePassword;
    }

    public String getWebsiteName() {
        return WebsiteName;
    }

    public void setWebsiteName(String websiteName) {
        WebsiteName = websiteName;
    }

    public String getWebsiteUser() {
        return WebsiteUser;
    }

    public void setWebsiteUser(String websiteUser) {
        WebsiteUser = websiteUser;
    }

    public String getWebsitePassword() {
        return WebsitePassword;
    }

    public void setWebsitePassword(String websitePassword) {
        WebsitePassword = websitePassword;
    }
}
