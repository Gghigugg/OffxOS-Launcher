package com.offxos.launcher;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/** Handles the Android HOME role request without forcing the user. */
public final class DefaultLauncherHelper {
    public static final int REQUEST_HOME_ROLE = 902;

    private DefaultLauncherHelper() {}

    public static boolean isSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean isDefault(Context context) {
        if (!isSupported()) return false;
        RoleManager rm = (RoleManager) context.getSystemService(Context.ROLE_SERVICE);
        return rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)
                && rm.isRoleHeld(RoleManager.ROLE_HOME);
    }

    public static boolean request(Activity activity) {
        if (!isSupported()) return false;
        RoleManager rm = (RoleManager) activity.getSystemService(Context.ROLE_SERVICE);
        if (rm == null || !rm.isRoleAvailable(RoleManager.ROLE_HOME)
                || rm.isRoleHeld(RoleManager.ROLE_HOME)) return false;
        Intent intent = rm.createRequestRoleIntent(RoleManager.ROLE_HOME);
        activity.startActivityForResult(intent, REQUEST_HOME_ROLE);
        return true;
    }
}
