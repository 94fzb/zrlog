package com.fzb.blog.web.plugin.type;

import com.fzb.blog.common.Constants;

/**
 * 定义常见的自动检查更新检查周期。
 * 使用秒为单位，而不是毫秒
 */
public enum AutoUpgradeVersionType {

    NEVER(-1), ONE_MINUTE(60), ONE_HOUR(3600), ONE_DAY(86400), ONE_WEEK(604800), HALF_MONTH(1296000);

    private int cycle;

    AutoUpgradeVersionType(int cycle) {
        this.cycle = cycle;
    }

    public static AutoUpgradeVersionType cycle(int cycle) {
        for (AutoUpgradeVersionType autoUpgradeVersionType : AutoUpgradeVersionType.values()) {
            if (autoUpgradeVersionType.getCycle() == cycle) {
                return autoUpgradeVersionType;
            }
        }
        return Constants.DEFAULT_AUTO_UPGRADE_VERSION_TYPE;
    }

    public int getCycle() {
        return cycle;
    }
}
