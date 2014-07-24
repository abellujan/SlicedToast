/**
 * Copyright 2014 Abel Lujan
 */

package com.abel.slicedtoast;


import java.io.DataOutputStream;
import java.io.IOException;

import com.abel.slicedtoast.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class Settings extends Activity {
	static String[] args = {"setprop ctl.restart surfaceflinger","setprop ctl.restart zygote"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
			getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
	
	

	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
			addPreferencesFromResource(R.xml.preferences);
			
			//preference soft reset listener
	        Preference reset = (Preference) findPreference("reset");
			reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference p) {
					try {
						RunAsRoot(args);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			});
		}
	}
	
	public static void RunAsRoot(String[] cmds) throws IOException{
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream dos = new DataOutputStream(p.getOutputStream());            
		for (String tmp : cmds) {
			dos.writeBytes(tmp+"\n");
		}           
		dos.writeBytes("exit\n");  
		dos.flush();
	}
}
