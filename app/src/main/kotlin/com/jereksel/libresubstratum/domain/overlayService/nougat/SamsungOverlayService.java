package com.jereksel.libresubstratum.domain.overlayService.nougat;

import android.content.Context;

import com.jereksel.libresubstratum.domain.OverlayInfo;
import com.jereksel.libresubstratum.domain.OverlayService;
import com.jereksel.libresubstratum.utils.Root;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SamsungOverlayService implements OverlayService {
    private Context context;

    public SamsungOverlayService(Context context) {
        this.context = context;
    }

    public void enableOverlay(String id) {}
    public void disableOverlay(String id) {}

    public OverlayInfo getOverlayInfo(String id) {
        return new OverlayInfo("test", true);
    }

    public List<OverlayInfo> getAllOverlaysForApk(String appId) {
        return new ArrayList<OverlayInfo>();
    }

    public void restartSystemUI() {

    }

    public void installApk(File apk) {
        Root.runCommand("pm install " + apk.getAbsolutePath());
        //Root.runCommand("kill -9 `pidof " + pkg + "`");
    }

    public void uninstallApk(String appId) {
        Root.runCommand("pm uninstall " + appId);
        //Root.runCommand("kill -9 `pidof " + pkg + "`");
    }

    public List<String> requiredPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        //permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
        return permissions;
    }

    public String additionalSteps() {
        return null;
    }

    public List<OverlayInfo> getOverlaysPrioritiesForTarget(String targetAppId) {
        return new ArrayList<>();
    }

    public void updatePriorities(List<String> overlayIds) {

    }
}
