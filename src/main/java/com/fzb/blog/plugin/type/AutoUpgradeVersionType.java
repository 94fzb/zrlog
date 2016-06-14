package com.fzb.blog.plugin.type;

public enum AutoUpgradeVersionType {

    NEVER(-1), ONE_DAY(24 * 60 * 60), ONE_WEEK(24 * 60 * 60 * 7);

    private int cycle;

    AutoUpgradeVersionType(int cycle) {
        this.cycle = cycle;
    }

    public int getCycle() {
        return cycle;
    }
}
