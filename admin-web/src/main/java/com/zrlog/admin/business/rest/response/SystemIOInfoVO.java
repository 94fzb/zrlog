package com.zrlog.admin.business.rest.response;

public class SystemIOInfoVO {

    private Long usedDiskSpace;
    private Long usedCacheSpace;
    private Long usedMemorySpace;
    private Long totalMemorySpace;

    public Long getUsedDiskSpace() {
        return usedDiskSpace;
    }

    public void setUsedDiskSpace(Long usedDiskSpace) {
        this.usedDiskSpace = usedDiskSpace;
    }

    public Long getUsedCacheSpace() {
        return usedCacheSpace;
    }

    public void setUsedCacheSpace(Long usedCacheSpace) {
        this.usedCacheSpace = usedCacheSpace;
    }

    public Long getUsedMemorySpace() {
        return usedMemorySpace;
    }

    public void setUsedMemorySpace(Long usedMemorySpace) {
        this.usedMemorySpace = usedMemorySpace;
    }

    public Long getTotalMemorySpace() {
        return totalMemorySpace;
    }

    public void setTotalMemorySpace(Long totalMemorySpace) {
        this.totalMemorySpace = totalMemorySpace;
    }
}
