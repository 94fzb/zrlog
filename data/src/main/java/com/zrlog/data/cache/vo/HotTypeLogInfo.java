package com.zrlog.data.cache.vo;

import java.util.List;

public class HotTypeLogInfo {

    private String typeAlias;
    private String typeName;
    private Long typeId;
    private List<HotLogBasicInfoEntry> logs;

    public String getTypeAlias() {
        return typeAlias;
    }

    public void setTypeAlias(String typeAlias) {
        this.typeAlias = typeAlias;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<HotLogBasicInfoEntry> getLogs() {
        return logs;
    }

    public void setLogs(List<HotLogBasicInfoEntry> logs) {
        this.logs = logs;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}
