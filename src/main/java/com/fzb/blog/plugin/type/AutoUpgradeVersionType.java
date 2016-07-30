package com.fzb.blog.plugin.type;

public enum AutoUpgradeVersionType {

    NEVER(-1), ONE_DAY(86400), ONE_WEEK(604800), HALF_MONTH(1296000);

    private int cycle;

    AutoUpgradeVersionType(int cycle) {
        this.cycle = cycle;
    }

    public int getCycle() {
        return cycle;
    }

    public static AutoUpgradeVersionType cycle(int cycle) {
        if (cycle == -1) {
            return AutoUpgradeVersionType.NEVER;
        } else if (cycle == 86400) {
            return AutoUpgradeVersionType.ONE_DAY;
        } else if (cycle == 604800) {
            return AutoUpgradeVersionType.ONE_WEEK;
        } else {
            return AutoUpgradeVersionType.HALF_MONTH;
        }
    }
}
