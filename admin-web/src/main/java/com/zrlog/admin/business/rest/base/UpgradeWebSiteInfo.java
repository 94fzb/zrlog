package com.zrlog.admin.business.rest.base;

import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;

import java.util.Objects;

public class UpgradeWebSiteInfo implements Validator {

    private Long autoUpgradeVersion;
    private Boolean upgradePreview;

    public Long getAutoUpgradeVersion() {
        return autoUpgradeVersion;
    }

    public void setAutoUpgradeVersion(Long autoUpgradeVersion) {
        this.autoUpgradeVersion = autoUpgradeVersion;
    }

    public Boolean getUpgradePreview() {
        return upgradePreview;
    }

    public void setUpgradePreview(Boolean upgradePreview) {
        this.upgradePreview = upgradePreview;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(autoUpgradeVersion)) {
            throw new ArgsException("autoUpgradeVersion");
        }
    }
}
