package com.gsbina.deviceownermanager;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;

public class AdminReceiver extends DeviceAdminReceiver {

	public static ComponentName getComponentName(Context context) {
		return new ComponentName(context.getApplicationContext(), AdminReceiver.class);
	}
}
