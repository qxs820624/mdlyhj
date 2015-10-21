package mobi.domore.mcdonalds;

import android.app.Application;

public class DmApplication extends Application {
	public static String VERSION = "1.0";

	private static DmApplication instance;

	public static DmApplication getInstance() {
		return instance;
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
 
		try {
			System.loadLibrary("mgpbase");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onExit() {
		// ThemeManager.getInstance().release();
		// UserManager.getInstance().release();
	}
}
