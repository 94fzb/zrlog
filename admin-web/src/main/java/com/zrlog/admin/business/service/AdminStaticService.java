package com.zrlog.admin.business.service;

public class AdminStaticService {

    private static final AdminStaticService instance = new AdminStaticService();

    private final UpgradeService upgradeService;

    private AdminStaticService() {
        this.upgradeService = new UpgradeService();
    }

    public static AdminStaticService getInstance() {
        return instance;
    }

    public UpgradeService getUpgradeService() {
        return upgradeService;
    }
}
