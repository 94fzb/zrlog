package com.zrlog.admin.web.type;

/**
 * 定义常见的自动检查更新检查周期。
 * 使用秒为单位，而不是毫秒
 */
public enum AutoUpgradeVersionType {

    NEVER(-1), ONE_MINUTE(60), ONE_HOUR(3600), ONE_DAY(86400), ONE_WEEK(604800), HALF_MONTH(1296000);

    private final int cycle;

    AutoUpgradeVersionType(int cycle) {
        this.cycle = cycle;
    }

    public static AutoUpgradeVersionType cycle(int cycle) {
        for (AutoUpgradeVersionType autoUpgradeVersionType : AutoUpgradeVersionType.values()) {
            if (autoUpgradeVersionType.getCycle() == cycle) {
                return autoUpgradeVersionType;
            }
        }
        return ONE_DAY;
    }

    public int getCycle() {
        return cycle;
    }
}
