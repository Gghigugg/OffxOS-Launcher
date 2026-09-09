package com.offxos.launcher;

import android.app.Activity;
import android.os.Bundle;

/** Small entry helper used by the launcher to keep HOME setup explicit. */
public final class HomeRoleBootstrap {
    private HomeRoleBootstrap() {}

    public static void requestIfNeeded(Activity activity) {
        if (!DefaultLauncherHelper.isDefault(activity)) {
            DefaultLauncherHelper.request(activity);
        }
    }
}
