package com.zrlog.business.rest.response;

public class PublicInfoVO {

    private final String currentVersion;
    private final String websiteTitle;
    private final String homeUrl;
    private final Boolean admin_darkMode;
    private final String admin_color_primary;
    private final String pwaThemeColor;

    public PublicInfoVO(String currentVersion, String websiteTitle, String homeUrl, Boolean adminDarkMode, String adminColorPrimary, String pwaThemeColor) {
        this.currentVersion = currentVersion;
        this.websiteTitle = websiteTitle;
        this.homeUrl = homeUrl;
        admin_darkMode = adminDarkMode;
        admin_color_primary = adminColorPrimary;
        this.pwaThemeColor = pwaThemeColor;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getWebsiteTitle() {
        return websiteTitle;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public Boolean getAdmin_darkMode() {
        return admin_darkMode;
    }

    public String getAdmin_color_primary() {
        return admin_color_primary;
    }

    public String getPwaThemeColor() {
        return pwaThemeColor;
    }
}
