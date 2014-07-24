/**
 * Copyright 2014 Abel Lujan
 */

package com.abel.slicedtoast;

import android.os.Handler;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Main implements IXposedHookZygoteInit {
	public static long userToastLength = 0;
	public static boolean disabled, custom;
	public static Toast currentToast;
	public static Handler handle;
	public static final String PACKAGE_NAME = Main.class.getPackage().getName();
	public static XSharedPreferences preferences;
	
	private static void log(String log) {
		XposedBridge.log("SlicedT Log: " + log);
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		log("Initialized.");
		
		preferences = new XSharedPreferences(PACKAGE_NAME);
		
		disabled = preferences.getBoolean("checkbox_disable", false);
		custom = preferences.getBoolean("checkbox_custom", false);
		
		if (custom){
			userToastLength = Long.valueOf(preferences.getString("custom_preference", "850"));
			if (userToastLength < 0){
				userToastLength = 0;
			} else if (userToastLength > 3000){
				userToastLength = 3000;
			}
		} else {
			userToastLength = Long.valueOf(preferences.getString("list_preference", "1000"));
		}
		
		XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				currentToast = (Toast) param.thisObject;
				if (disabled){
					currentToast.cancel();
					log("toasts disabled");
				}
			}
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				handle = new Handler();
				handle.postDelayed(new Runnable(){
					@Override
					public void run() {
						currentToast.cancel();
						log("toast killed");
					}
				}, userToastLength);
			}
		});
	}
}
