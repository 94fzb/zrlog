package com.zrlog.data.dto;

public class FaviconBase64DTO {

    private String generator_html_status;
    private String favicon_ico_base64;
    private String favicon_png_pwa_192_base64;
    private String favicon_png_pwa_512_base64;

    public String getFavicon_ico_base64() {
        return favicon_ico_base64;
    }

    public void setFavicon_ico_base64(String favicon_ico_base64) {
        this.favicon_ico_base64 = favicon_ico_base64;
    }

    public String getFavicon_png_pwa_192_base64() {
        return favicon_png_pwa_192_base64;
    }

    public void setFavicon_png_pwa_192_base64(String favicon_png_pwa_192_base64) {
        this.favicon_png_pwa_192_base64 = favicon_png_pwa_192_base64;
    }

    public String getFavicon_png_pwa_512_base64() {
        return favicon_png_pwa_512_base64;
    }

    public void setFavicon_png_pwa_512_base64(String favicon_png_pwa_512_base64) {
        this.favicon_png_pwa_512_base64 = favicon_png_pwa_512_base64;
    }

    public String getGenerator_html_status() {
        return generator_html_status;
    }

    public void setGenerator_html_status(String generator_html_status) {
        this.generator_html_status = generator_html_status;
    }
}
