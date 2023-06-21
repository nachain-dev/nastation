package org.nastation.common.service;

import org.nastation.common.model.AppNewVersion;

/**
 * @author John | NaChain
 * @since 12/24/2021 17:12
 */
public class SystemService {

    private boolean stopRun = false;

    private boolean requestBlockDataOver = false;

    public boolean dev = false;

    public AppNewVersion appNewVersion;

    private static SystemService instance = new SystemService();

    private SystemService() {
    }

    public static SystemService me() {
        return instance;
    }

    public boolean isStopRun() {
        return stopRun;
    }

    public void setStopRun(boolean stopRun) {
        this.stopRun = stopRun;
    }

    public boolean isRequestBlockDataOver() {
        return requestBlockDataOver;
    }

    public void setRequestBlockDataOver(boolean requestBlockDataOver) {
        this.requestBlockDataOver = requestBlockDataOver;
    }

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    public AppNewVersion getAppNewVersion() {
        return appNewVersion;
    }

    public void setAppNewVersion(AppNewVersion appNewVersion) {
        this.appNewVersion = appNewVersion;
    }

    public static SystemService getInstance() {
        return instance;
    }

    public static void setInstance(SystemService instance) {
        SystemService.instance = instance;
    }
}
