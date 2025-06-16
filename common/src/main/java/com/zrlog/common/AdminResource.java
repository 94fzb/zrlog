package com.zrlog.common;

import java.io.InputStream;
import java.util.Set;

public interface AdminResource  {

    Set<String> getAdminResourceUris(boolean requiredStaticFiles);

    boolean isAdminMainJs(String uri);

    InputStream renderServiceWorker();
}
