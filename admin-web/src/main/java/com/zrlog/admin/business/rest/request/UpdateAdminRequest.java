package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class UpdateAdminRequest implements Validator {

    private String userName;
    private String email;
    private String header;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(userName) || userName.trim().isEmpty()) {
            throw new ArgsException("userName");
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(this.getEmail())) {
            this.setEmail(Jsoup.clean(this.getEmail(), Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.getHeader())) {
            this.setHeader(Jsoup.clean(this.getHeader(), Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.getUserName())) {
            this.setUserName(Jsoup.clean(this.getUserName(), Safelist.none()));
        }
    }
}
