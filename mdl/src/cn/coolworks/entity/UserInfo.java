package cn.coolworks.entity;

import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {
	/**
	 * 用户ID
	 */
	private String uid;
	//
	private String verify;
	/**
	 * 用户昵称
	 */
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	// 用户头像
	private String userHead;

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	//
	private String userDesc;

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	//
	private String startUrl;

	private int defaultSaveDays = 7;
	private int saveDays;

	public int getSaveDays() {
		return saveDays;
	}

	public void setSaveDays(int saveDays) {
		this.saveDays = saveDays;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	private int useCount = 0;

	private String defaultFolder;

	public String getDefaultFolder() {
		return defaultFolder;
	}

	public void setDefaultFolder(String defaultFolder) {
		this.defaultFolder = defaultFolder;
	}

	/**
	 * 登陆email
	 */
	private String email;
	/**
	 * 登陆密码
	 */
	private String password;
	/**
	 * 是否cmwap代理
	 */
	private boolean proxy = false;
	/**
	 * 是否自动隐藏
	 */
	private boolean autoHide = false;

	public boolean isAutoHide() {
		return autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	/**
	 * 广告条目
	 */
	public RSSItem adItem = new RSSItem();

	/**
	 * 是否只使用wifi
	 */
	private boolean wifi = false;
	/**
	 * 正文是否不获取新图片
	 */
	private boolean isBlockImg = false;
	/**
	 * 是否全屏显示
	 */
	private boolean fullscreen = false;
	/**
	 * 是否自动登录
	 */
	private boolean autoLogin = false;
	/**
	 * 是否保存密码
	 */
	private boolean savePassword = true;
	/**
	 * 
	 */
	private float zoomValue = 1;

	public float getZoomValue() {
		return zoomValue;
	}

	public void setZoomValue(float zoomValue) {
		this.zoomValue = zoomValue;
	}

	/**
	 * 用户皮肤
	 * 
	 * @return
	 */
	private int skin = 0;

	public int getSkin() {
		return skin;
	}

	public void setSkin(int skin) {
		this.skin = skin;
	}

	public boolean isSavePassword() {
		return savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	public boolean isAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isBlockImg() {
		return isBlockImg;
	}

	public void setBlockImg(boolean isBlockImg) {
		this.isBlockImg = isBlockImg;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	Context context = null;
	// SharedPreferences数据保存的文件名
	String SETTING_INFO = "SETTING_INFO";
	// 邮箱字段名
	String EMAIL = "EMAIL";
	// 密码字段名
	String PASSWORD = "PASSWORD";
	// UID
	public final static String UID = "UID";
	// VERIFY
	public final static String VERIFY = "VERIFY";
	// UID
	public final static String USERNAME = "USERNAME";
	// VERIFY
	public final static String USERDESC = "USERDESC";
	//
	public final static String USERHEAD = "USERHEAD";
	// cmwap代理字段名
	public final static String PROXY = "PROXY";
	// autohide代理字段名
	public final static String AUTOHIDE = "AUTOHIDE";
	// wifi代理字段名
	public final static String WIFI = "WIFI";
	// 正文是否显示图片
	public final static String BLOCKIMG = "BLOCKIMG";
	// 全屏显示
	public final static String FULLSCREEN = "FULLSCREEN";
	// 自动登录
	public final static String AUTOLOGIN = "AUTOLOGIN";
	// 保存密码
	public final static String SAVEPASSWORD = "SAVEPASSWORD";
	// 用户皮肤
	public final static String SKIN = "SKIN";
	//
	public final static String STARTURL = "STARTURL";
	//
	public final static String USECOUNT = "USECOUNT";
	//
	public final static String FOLDER = "FOLDER";
	// 文件保存天数
	public final static String SAVEDAYS = "SAVEDAYS";
	//
	private final static String ZOOM = "ZOOM";
	//
	private final static String ADITEM_TITLE = "ADITEM_TITLE";
	private final static String ADITEM_LINK = "ADITEM_LINK";
	private final static String ADITEM_PUBDATE = "ADITEM_PUBDATE";
	private final static String ADITEM_THUMBURL = "ADITEM_THUMBURL";
	private final static String ADITEM_IMAGEURL = "ADITEM_IMAGEURL";

	/**
	 * 
	 * @param context
	 */
	public UserInfo(Context context) {
		this.context = context;
		// 得到SharedPreferences实例
		SharedPreferences settings = this.context.getSharedPreferences(
				SETTING_INFO, 0);
		// 获取email字段数据
		setAutoLogin(settings.getBoolean(AUTOLOGIN, false));
		setSavePassword(settings.getBoolean(SAVEPASSWORD, true));
		setEmail(settings.getString(EMAIL, ""));
		setPassword(settings.getString(PASSWORD, ""));
		setUid(settings.getString(UID, ""));
		setVerify(settings.getString(VERIFY, ""));
		setUserName(settings.getString(USERNAME, ""));
		setUserHead(settings.getString(USERHEAD, ""));
		setUserDesc(settings.getString(USERDESC, ""));
		setProxy(settings.getBoolean(PROXY, false));
		setAutoHide(settings.getBoolean(AUTOHIDE, false));
		setWifi(settings.getBoolean(WIFI, false));
		setBlockImg(settings.getBoolean(BLOCKIMG, false));
		setStartUrl(settings.getString(STARTURL, ""));
		setFullscreen(settings.getBoolean(FULLSCREEN, false));
		setSkin(settings.getInt(SKIN, getSkin()));
		setUseCount(settings.getInt(USECOUNT, 0));
		setDefaultFolder(settings.getString(FOLDER, null));
		setSaveDays(settings.getInt(SAVEDAYS, defaultSaveDays));
		setZoomValue(settings.getFloat(ZOOM, 1));
		adItem.setTitle(settings.getString(ADITEM_TITLE, null));
		adItem.setLink(settings.getString(ADITEM_LINK, null));
		adItem.setPubDate(settings.getString(ADITEM_PUBDATE, null));
		adItem.setImageUrl(settings.getString(ADITEM_IMAGEURL, null));
		adItem.setThumbailUrl(settings.getString(ADITEM_THUMBURL, null));
	}

	/**
	 * 保存数据
	 */
	public void saveData() {
		// 得到SharedPreferences实例
		SharedPreferences settings = this.context.getSharedPreferences(
				SETTING_INFO, 0);
		// 从输入框里获取到数据保存到SharedPreferences对应的文件里
		settings.edit().putBoolean(AUTOLOGIN, isAutoLogin())
				.putBoolean(SAVEPASSWORD, isSavePassword())
				.putString(EMAIL, getEmail())
				.putString(PASSWORD, getPassword()).putString(UID, getUid())
				.putString(VERIFY, getVerify())
				.putString(FOLDER, this.getDefaultFolder())
				.putInt(USECOUNT, useCount).putString(STARTURL, getStartUrl())
				.putString(USERNAME, getUserName())
				.putString(USERDESC, getUserDesc())
				.putString(USERHEAD, getUserHead())
				.putBoolean(PROXY, isProxy())
				.putBoolean(AUTOHIDE, isAutoHide()).putBoolean(WIFI, isWifi())
				.putBoolean(BLOCKIMG, isBlockImg())
				.putBoolean(FULLSCREEN, isFullscreen()).putInt(SKIN, getSkin())
				.putInt(SAVEDAYS, this.getSaveDays())
				.putFloat(ZOOM, this.getZoomValue())
				.putString(ADITEM_TITLE, this.adItem.getTitle())
				.putString(ADITEM_LINK, this.adItem.getLink())
				.putString(ADITEM_PUBDATE, this.adItem.getPubDate())
				.putString(ADITEM_IMAGEURL, this.adItem.getImageUrl())
				.putString(ADITEM_THUMBURL, this.adItem.getThumbailUrl())
				.commit();
	}

	public void setUseCount(int count) {
		this.useCount = count;
	}

	public int userCount() {
		return useCount;
	}

	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}

	public boolean isWifi() {
		return wifi;
	}

}
