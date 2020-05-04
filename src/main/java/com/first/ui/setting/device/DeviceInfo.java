package com.first.ui.setting.device;

import java.util.Map;

public interface DeviceInfo {
	public void loadSettings(Map<String, String> params);
	public void saveSettings(Map<String, String> params);
}
