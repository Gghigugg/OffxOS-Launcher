package com.offxos.launcher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

/** Small, defensive torch controller for the OffxOS Control Center. */
public class FlashController {
    private final Context context;
    private boolean enabled;

    public FlashController(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isEnabled() { return enabled; }
    public boolean toggle() { return setEnabled(!enabled); }

    public boolean setEnabled(boolean value) {
        if (android.os.Build.VERSION.SDK_INT < 23) return false;
        if (context.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return false;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) return false;
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (manager == null) return false;
            String cameraId = findFlashCamera(manager);
            if (cameraId == null) return false;
            manager.setTorchMode(cameraId, value);
            enabled = value;
            return true;
        } catch (CameraAccessException | SecurityException | IllegalArgumentException e) {
            return false;
        }
    }

    private String findFlashCamera(CameraManager manager) throws CameraAccessException {
        for (String id : manager.getCameraIdList()) {
            CameraCharacteristics c = manager.getCameraCharacteristics(id);
            Boolean flash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer facing = c.get(CameraCharacteristics.LENS_FACING);
            if (Boolean.TRUE.equals(flash) && (facing == null || facing == CameraCharacteristics.LENS_FACING_BACK)) return id;
        }
        return null;
    }
}
