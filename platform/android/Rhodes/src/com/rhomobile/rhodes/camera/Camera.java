/*
 ============================================================================
 Author	    : Dmitry Moskalchuk
 Version	: 1.5
 Copyright  : Copyright (C) 2008 Rhomobile. All rights reserved.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ============================================================================
 */
package com.rhomobile.rhodes.camera;

import java.io.File;

import android.content.Intent;
import com.rhomobile.rhodes.Logger;
import com.rhomobile.rhodes.Rhodes;
import com.rhomobile.rhodes.RhodesInstance;

public class Camera {

	private static final String TAG = "Camera";
	
	public static final String INTENT_EXTRA_PREFIX = Rhodes.INTENT_EXTRA_PREFIX + "camera.";
	
	public static String BASE_CAMERA_DIR = RhodesInstance.getInstance().getRootPath() + "apps/public/db-files";
	
	private static void reportFail(String name, Exception e) {
		Logger.E(TAG, "Call of \"" + name + "\" failed: " + e.getMessage());
	}
	
	private static void init() {
		File f = new File(BASE_CAMERA_DIR);
		if (!f.exists())
			f.mkdirs();
	}
	
	private static class Runner implements Runnable {
		
		private String url;
		private Class<?> cls;
		
		public Runner(String u, Class<?> c) {
			url = u;
			cls = c;
		}
		
		public void run() {
			init();
			Rhodes r = RhodesInstance.getInstance();
			Intent intent = new Intent(r, cls);
			intent.putExtra(INTENT_EXTRA_PREFIX + "callback", url);
			r.startActivity(intent);
		}
	};

	public static void takePicture(String sourceUrl) {
		try {
			Rhodes.performOnUiThread(new Runner(sourceUrl, ImageCapture.class), false);
		}
		catch (Exception e) {
			reportFail("takePicture", e);
		}
	}

	public static void choosePicture(String sourceUrl) {
		try {
			Rhodes.performOnUiThread(new Runner(sourceUrl, FileList.class), false);
		}
		catch (Exception e) {
			reportFail("choosePicture", e);
		}
	}
	
	public static void doCallback(String callbackUrl, String filePath) {
		String fp = filePath == null ? "" : filePath;
		int idx = fp.lastIndexOf('/');
		if (idx != -1)
			fp = fp.substring(idx + 1);
		callback(callbackUrl, fp, "", fp.length() == 0);
	}

	public static native void callback(String callbackUrl, String filePath, String error, boolean cancelled);

}
