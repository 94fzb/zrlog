package com.zrlog.business.rest.response;

public record PublicInfoVO(String currentVersion, String websiteTitle, String homeUrl, Boolean admin_darkMode,
                           String admin_color_primary,
                           String pwaThemeColor) {
}
