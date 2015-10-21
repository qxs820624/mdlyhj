package com.hutuchong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import mobi.domore.mcdonalds.R;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSImage;
import org.gnu.stealthp.rsslib.RSSItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.coolworks.entity.UserInfo;
import cn.coolworks.handle.ActivityHandle;
import cn.coolworks.util.Debug;
import cn.coolworks.util.FileUtil;
import cn.coolworks.util.RequestDataEntity;
import cn.coolworks.util.StringUtil;
import cn.coolworks.util.UpdateThread;

import com.angeeks.market.ManageActivity;
import com.hutuchong.adapter.MenuAdapter;
import com.hutuchong.adapter.OptionMenuAdapter;
import com.hutuchong.app_game.CheckVerDialog;
import com.hutuchong.app_user.MessageListActivity;
import com.hutuchong.app_user.db.DBAdapter;
import com.hutuchong.data.Data;
import com.hutuchong.filebrowser.FileBrowserActivity;
import com.jw.http.ProgressListener;

/**
 * Activity
 * 
 * @author 3gqa.com
 * 
 */
public abstract class BaseActivity extends Activity implements
		SensorEventListener, ActivityHandle, ProgressListener {
	//
	public String mTitle;
	public Context mContext;

	public GestureDetector gestureScanner;
	//
	boolean isReadyExit = false;
	boolean isConfirmExit = false;
	boolean isUpExit = false;
	protected boolean needBackConfirm = false;
	protected boolean needStopService = false;
	public UpdateThread updateThread;

	//
	public RSSChannel mChannel;
	public int mCurrentChannelIndex;
	public BindService bindService;
	public BaseService service;
	public String serviceName;
	//
	public String extra_flg;
	//
	private boolean bSensor = false;
	private SensorManager sensorMgr;
	private Sensor sensor;
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;

	// 界面显示
	public ArrayList<RSSItem> showItemList = new ArrayList<RSSItem>(0);

	//

	public void init(boolean isFullScreen) {
		mContext = this;
		// android.os.Debug.startMethodTracing("ImageActivity_tracker");
		Commond.getDeviceInfo(this);
		//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//
		if (Commond.userInfo == null) {
			Commond.userInfo = new UserInfo(this);
			Commond.userInfo.setFullscreen(isFullScreen);
			Commond.userInfo.setUseCount(Commond.userInfo.userCount() + 1);

			if (TextUtils.isEmpty(Commond.userInfo.getUid())) {
				TelephonyManager tm = (TelephonyManager) this
						.getSystemService(Context.TELEPHONY_SERVICE);

				Commond.userInfo.setUid(tm.getDeviceId() + "_"
						+ Long.toString(System.currentTimeMillis()));
			}
			Commond.userInfo.saveData();
		}
		//
		showStatusBar(Commond.userInfo.isFullscreen());

		if (bSensor) {
			sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorMgr.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}

	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if (bSensor)
			this.getWindow().getDecorView().setDrawingCacheEnabled(true);
		// vTitleMenu = this.findViewById(R.id.nav_title_menu);
		// View v = this.findViewById(R.id.v_topest);
		// if (v != null) {
		// // v.setBackgroundColor(0xFF5E69FF);//4:0xFFFF55F0 2:0xFF5E69FF
		// }
	}

	public void onBinddedService() {
		// bindService.service.setProxy(Commond.userInfo.isProxy());
		service = this.bindService.service;
		service.initService(Commond.userInfo.isProxy());
	}

	/**
	 * 
	 * @param index
	 */
	public void onSelectedIndex(int index) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param defaultUrl
	 * @param defaultPageId
	 * @param titleResId
	 */
	public void initIntent(String defaultUrl, int defaultPageId, int titleResId) {
		//
		String subtitle = null;
		String url = null;
		int pid = defaultPageId;
		//
		service.categoryUrl = defaultUrl;
		Intent i = this.getIntent();
		if (i != null) {
			//
			extra_flg = i.getStringExtra(ContantValue.EXTRA_FLG);
			//
			String title = i.getStringExtra(ContantValue.EXTRA_TITLE);
			if (TextUtils.isEmpty(title)) {
				mTitle = getString(titleResId);
			} else
				mTitle = title;
			subtitle = i.getStringExtra(ContantValue.EXTRA_SUB_TITLE);
			if (!TextUtils.isEmpty(subtitle)) {
				mTitle += " >> " + subtitle;
			}
			//
			url = i.getStringExtra(ContantValue.EXTRA_URL);
			pid = i.getIntExtra(ContantValue.EXTRA_PAGEID, defaultPageId);
			//
			String categoryUrl = i
					.getStringExtra(ContantValue.EXTRA_CATEGORY_URL);
			if (!TextUtils.isEmpty(categoryUrl)) {
				this.service.categoryUrl = categoryUrl;
			}
			//
			String searchUrl = i.getStringExtra(ContantValue.EXTRA_SEARCH_URL);
			if (!TextUtils.isEmpty(categoryUrl)) {
				this.service.searchUrl = searchUrl;
			}
			//
			String favUrl = i.getStringExtra(ContantValue.EXTRA_FAV_URL);
			if (!TextUtils.isEmpty(categoryUrl)) {
				this.service.favUrl = favUrl;
			}
			//
			this.needStopService = i.getBooleanExtra(
					ContantValue.EXTRA_NEED_STOPSERVICE, false);
			this.needBackConfirm = i.getBooleanExtra(
					ContantValue.EXTRA_NEED_BACKCONFIRM, false);
			//
			String action = i.getAction();
			if ("android.intent.action.MAIN".equals(action)) {
				this.needStopService = true;
				this.needBackConfirm = true;
			}
		}
		setNavText(subtitle);
		//
		if (TextUtils.isEmpty(url))
			url = defaultUrl;
		//
		if (!TextUtils.isEmpty(url)) {
			requestItem(url, pid, true);
		}
		//
	}

	/**
	 * 
	 * @param url
	 * @param pageid
	 */
	public void setChannelList(int resId, final View selectedBgView) {
		//
		final GridView gv = (GridView) findViewById(resId);
		if (gv != null) {
			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					//
					clearListView();
					//
					if (selectedBgView != null) {
						int fromX = selectedBgView.getLeft();
						Object tag = gv.getTag();
						if (tag != null) {
							fromX = (Integer) gv.getTag();
						}
						int toX = arg1.getLeft()
								+ (arg1.getWidth() - selectedBgView.getWidth())
								/ 2;
						toX = toX < 0 ? 2 : toX;
						gv.setTag(toX);
						//
						TranslateAnimation anim = new TranslateAnimation(fromX,
								toX, 0, 0);
						anim.setDuration(500);
						anim.setInterpolator(mContext,
								android.R.anim.accelerate_interpolator);
						anim.setFillBefore(true);
						anim.setFillAfter(true);
						anim.setFillEnabled(true);

						selectedBgView.startAnimation(anim);
						// @@特别注意：如果没有下面这句，会出现selectedBgView动画效果延迟问题
						gv.invalidate();
						//
					} else {
						Commond.showOptionMenu(BaseActivity.this, false);
					}
					//
					RSSChannel chan = service.getChannel(service.categoryUrl);
					if (chan != null) {
						//
						RSSItem item = chan.getItem(arg2);
						requestItem(item.getLink(), Commond.PAGE_CHANNEL, true);
						//
						setCategoryText(item.getTitle());
					} else {
						showTipIcon(service.categoryUrl, Commond.PAGE_CATEGORY);
					}
				}
			});
		}
	}

	/**
	 * 
	 */
	public void clearListView() {

	}

	/**
	 * 
	 * @param title
	 */
	public void setCategoryText(String title) {

	}

	/**
	 * 
	 */
	public void loadNavBar() {
		View navBar = findViewById(R.id.nav_panel);
		if (navBar != null)
			navBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * @param activity
	 */
	public void loadNavMenu() {
		View menu = findViewById(R.id.nav_menu_panel);
		if (menu == null)
			return;
		OnClickListener cateListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				View vSearch = findViewById(R.id.search_panel);
				if (vSearch.getVisibility() == View.VISIBLE) {
					Commond.showSearchInput(BaseActivity.this, true);
				} else
					Commond.showOptionMenu(BaseActivity.this, false);
				//
			}
		};
		menu.setOnClickListener(cateListener);
		menu.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	public void loadNavFav() {
		View fav = findViewById(R.id.fav_title_panel);
		if (fav == null)
			return;
		fav.setVisibility(View.VISIBLE);
		fav.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 首先隐藏分类列表
				View gv = findViewById(R.id.optionmenu_popup_grid);
				if (gv.getVisibility() == View.VISIBLE) {
					Commond.showOptionMenu(BaseActivity.this, false);
				}
				View vSearch = findViewById(R.id.search_panel);
				if (vSearch.getVisibility() == View.VISIBLE) {
					Commond.showSearchInput(BaseActivity.this, false);
				}
				//
				requestItem(service.favUrl, Commond.PAGE_CHANNEL, true);
				//
				setFavText();
			}
		});
	}

	/**
	 * 
	 */
	public void switchShowPic() {

	}

	/**
	 * 
	 * @param optionGridView
	 * @param listAdapter
	 * @param optionIconIds
	 * @param optionIconTitles
	 */
	public void loadOptionMenu(final BaseAdapter listAdapter,
			final int[] optionIconIds, final int[] optionIconTitles) {
		GridView optionGridView = (GridView) findViewById(R.id.optionmenu_popup_grid);
		if (optionGridView == null)
			return;
		optionGridView.setVisibility(View.INVISIBLE);
		final OptionMenuAdapter optionsAdapter = new OptionMenuAdapter(
				mContext, R.layout.optionmenu_item, optionIconIds,
				optionIconTitles);
		optionGridView.setNumColumns(4);
		optionGridView.setAdapter(optionsAdapter);
		optionGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg1.getId()) {
				case R.drawable.menu_help:
					GotoActivity.gotoWebDialog(mContext,
							"file:///android_asset/help.html");
					break;
				case R.drawable.menu_pic:
				case R.drawable.menu_nopic:
					Commond.userInfo.setBlockImg(!Commond.userInfo.isBlockImg());
					Commond.userInfo.saveData();
					//
					if (Commond.userInfo.isBlockImg()) {
						optionIconIds[arg2] = R.drawable.menu_pic;
						optionIconTitles[arg2] = R.string.menu_pic;
					} else {
						optionIconIds[arg2] = R.drawable.menu_nopic;
						optionIconTitles[arg2] = R.string.menu_nopic;
					}
					optionsAdapter.notifyDataSetChanged();
					if (listAdapter != null)
						listAdapter.notifyDataSetChanged();
					switchShowPic();
					break;
				case R.drawable.menu_refresh:
					if (mChannel != null) {
						service.delFile(mChannel.getLink());
						requestItem(mChannel.getLink(), Commond.PAGE_CHANNEL,
								true);
					}
					break;
				case R.drawable.menu_msg:
					CommentDialog cdlg = new CommentDialog(mContext);
					cdlg.show();
					break;
				case R.drawable.menu_ver:
					CheckVerDialog dialog = new CheckVerDialog(mContext, null,
							null, null);
					dialog.show();
					break;
				case R.drawable.menu_more:
					GotoActivity.gotoMore(mContext);
					break;
				case R.drawable.menu_clear:
					service.clearSavedFile(0);
					Commond.showToast(mContext,
							getString(R.string.menu_clear_finished));
					break;
				case R.drawable.menu_appmanage:// 软件管理
					Intent i = new Intent(mContext, ManageActivity.class);
					mContext.startActivity(i);
					break;
				case R.drawable.menu_about:
					GotoActivity.gotoAbout(mContext);
					break;
				case R.drawable.menu_exit:
					finish();
					break;
				}
				Commond.showOptionMenu(BaseActivity.this, false);
			}
		});
	}

	/**
	 * 
	 * @param title
	 */
	public void setFavText() {

	}

	/**
	 * 
	 * @param activity
	 */
	public void loadNavSearch(int resId, int hintResId) {
		View search = findViewById(R.id.search_title_panel);
		if (search == null)
			return;
		TextView searchTitle = (TextView) findViewById(R.id.nav_search_text);
		searchTitle.setText(resId);
		EditText searchInput = (EditText) findViewById(R.id.et_search);
		searchInput.setHint(hintResId);
		//
		search.setVisibility(View.VISIBLE);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 首先隐藏分类列表
				View gv = findViewById(R.id.optionmenu_popup_grid);
				if (gv != null && gv.getVisibility() == View.VISIBLE) {
					Commond.showOptionMenu(BaseActivity.this, true);
				} else {
					Commond.showSearchInput(BaseActivity.this, false);
				}
			}
		});
		//
		View btnSearch = findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText et = (EditText) findViewById(R.id.et_search);
				String ct = et.getText().toString();
				if (TextUtils.isEmpty(ct)) {
					Commond.showToast(BaseActivity.this,
							getString(R.string.no_empty_tip));
				} else {
					//
					onNavSearch(ct);
					//
					Commond.showSearchInput(BaseActivity.this, false);
				}
			}
		});
	}

	/**
	 * 
	 * @param input
	 */
	public void onNavSearch(String input) {

	}

	/**
	 * 
	 */
	public void loadNavMore(int btn_ad_more) {
		View more = findViewById(btn_ad_more);
		if (more == null)
			return;
		if (!TextUtils.isEmpty(Commond.copyright)) {
			more.setVisibility(View.GONE);
			return;
		} else
			more.setVisibility(View.VISIBLE);
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GotoActivity.gotoNavMore(mContext);
			}
		});
	}

	/**
	 * 加载返回按钮
	 * 
	 * @param webview
	 * @param showBack
	 */
	public void loadBtnBack(final WebView webview, int resId, int isShow) {
		// 返回按钮
		View ib = findViewById(resId);
		if (ib == null)
			return;

		if (isShow != View.VISIBLE) {
			ib.setVisibility(isShow);
		} else {
			ib.setVisibility(View.VISIBLE);
			OnClickListener l = new OnClickListener() {
				@Override
				public void onClick(View v) {
					//
					if (!goBack(webview)) {

						if (needBackConfirm) {
							if (!isReadyExit || !isConfirmExit) {
								//
								isReadyExit = true;
								Commond.showToast(mContext,
										getString(R.string.exit_tip));
							} else {
								finish();
							}
						} else {
							finish();
						}
					}
				}
			};
			ib.setOnClickListener(l);
		}
	}

	/**
	 * 加载 站内消息按钮
	 * 
	 * @param showNextPre
	 * @param resId
	 * @param isShow
	 */
	public void loadBtnMsg(int resId, int isShow) {
		//
		View btnRight = findViewById(resId);
		if (btnRight != null) {
			if (isShow != View.VISIBLE) {
				btnRight.setVisibility(isShow);
			} else {
				btnRight.setVisibility(View.VISIBLE);
				btnRight.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(mContext,
								MessageListActivity.class);
						mContext.startActivity(i);
					}
				});
			}
		}
	}

	/**
	 * 
	 * @param activity
	 * @param webview
	 */
	public void loadPreNext(int preResId, int nextResId, int showNextPre) {
		//
		View pre = this.findViewById(preResId);
		if (pre == null)
			return;
		if (showNextPre != View.VISIBLE) {
			pre.setVisibility(showNextPre);
		} else {
			pre.setVisibility(View.VISIBLE);
			pre.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					rightOutLeftIn();
				}

			});
		}
		View next = this.findViewById(nextResId);
		if (next == null)
			return;
		if (showNextPre != View.VISIBLE) {
			next.setVisibility(showNextPre);
		} else {
			next.setVisibility(View.VISIBLE);
			next.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					rightInLeftOut();
				}

			});
		}
	}

	/**
	 * 
	 * @param activity
	 * @param webview
	 * @return
	 */
	public boolean goBack(final WebView webview) {
		//
		if (webview != null && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		View v = findViewById(R.id.optionmenu_popup_grid);
		if (v != null && v.getVisibility() == View.VISIBLE) {
			Commond.showOptionMenu(this, false);
			return true;
		}
		View vSearch = findViewById(R.id.search_panel);
		if (vSearch != null && vSearch.getVisibility() == View.VISIBLE) {
			Commond.showSearchInput(this, false);
			return true;
		}
		//
		if (Commond.hidePanel(this, R.id.popup_share_panel))
			return true;
		if (Commond.hidePanel(this, R.id.popup_comment_panel))
			return true;
		if (Commond.hidePanel(this, R.id.popup_more_panel))
			return true;
		return false;
	}

	/**
	 * 
	 */
	public void setTitleText(String title) {
		TextView tvTitle = (TextView) this.findViewById(R.id.title_text);
		if (tvTitle != null) {
			tvTitle.setText(title.toString());
		}
	}

	/**
	 * 
	 */
	public void setNavText(String subTitle) {
		TextView tvTitle = (TextView) this.findViewById(R.id.title_text);
		if (tvTitle != null) {
			StringBuffer title = new StringBuffer();
			if (!TextUtils.isEmpty(mTitle)) {
				title.append(mTitle);
			}
			if (!TextUtils.isEmpty(subTitle)) {
				if (title.length() > 0) {
					title.append(this.getString(R.string.tosub));
				}
				title.append(subTitle);
			}

			tvTitle.setText(title.toString());
		}
	}

	/**
	 * 
	 */
	@Override
	public void onStartIntent(Intent i) {
		if (mChannel == null)
			return;
		String title = mChannel.getTitle();
		if (!TextUtils.isEmpty(title))
			title = StringUtil.replaceBR(title);
		String fb = mChannel.getFeedback();
		if (!TextUtils.isEmpty(fb)) {
			fb = StringUtil.replaceBR(fb);
			// 过滤html标签
			fb = HtmlRegexpUtil.getBody(fb);
		}
		onStartIntent(i, null, title, fb, null);
	}

	/**
	 * 
	 * @param i
	 * @param fileUrl
	 * @param title
	 * @param desc
	 */
	public void onStartIntent(Intent i, String fileUrl, String title,
			String desc, String ex) {
		if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(ex)) {

			byte[] data = this.service.getCacheData(fileUrl);
			if (data != null) {
				String name = "share_temp" + ex;// 必须要后面的扩展名，否则发送短信出错
				try {
					FileOutputStream fos = this.openFileOutput(name,
							Context.MODE_WORLD_READABLE);
					fos.write(data);
					fos.flush();
					fos.close();
					fos = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				//
				i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				//
				File f = getFileStreamPath(name);
				i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
			}
			// 蓝牙可能需要
			i.putExtra("file_name", fileUrl);
		}
		if (!TextUtils.isEmpty(desc)) {
			i.putExtra(Intent.EXTRA_TEXT, desc);
			i.putExtra("sms_body", desc);
		}
		if (!TextUtils.isEmpty(title)) {
			i.putExtra(Intent.EXTRA_TITLE, title);
			i.putExtra(Intent.EXTRA_SUBJECT, title);
		}
		//
		startActivity(i);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTouchEnable() {
		return true;
	};

	/**
	 * 
	 */
	public void appFlash(boolean ishow) {
		final ViewGroup vflash = (ViewGroup) this
				.findViewById(R.id.flash_panel);
		if (vflash == null) {
			return;
		}
		if (!ishow) {
			vflash.setVisibility(View.GONE);
			View vpage = BaseActivity.this.findViewById(R.id.v_page);
			vpage.setVisibility(View.VISIBLE);
			return;
		}
		Commond.setImgeViewSrc(this, R.id.iv_flash_bg, "bg_sub.png");
		//
		Animation comeAnim = AnimationUtils.loadAnimation(this,
				R.anim.tovisible_2s);
		AnimationListener comeListener = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Animation goAnim = AnimationUtils.loadAnimation(
						BaseActivity.this, R.anim.togone_1s);
				AnimationListener goListener = new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						vflash.setVisibility(View.GONE);
						vflash.removeView(vflash.findViewById(R.id.iv_flash_bg));
						//
						Animation comeAnim = AnimationUtils.loadAnimation(
								BaseActivity.this, R.anim.tovisible_1s);
						View vpage = BaseActivity.this
								.findViewById(R.id.v_page);
						vpage.setVisibility(View.VISIBLE);
						vpage.startAnimation(comeAnim);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}
				};
				goAnim.setAnimationListener(goListener);
				vflash.startAnimation(goAnim);
				//

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		};
		comeAnim.setAnimationListener(comeListener);
		vflash.startAnimation(comeAnim);

	}

	/**
	 * 
	 */
	public void hideViewAnim(final View v) {
		if (v == null)
			return;
		Animation anim;
		if (v.getVisibility() == View.GONE) {
			anim = AnimationUtils.loadAnimation(this, R.anim.tovisible_1s);
		} else {
			anim = AnimationUtils.loadAnimation(this, R.anim.togone_1s);
		}
		showStatusBar(Commond.userInfo.isFullscreen());
		//
		AnimationListener listener = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (v.getVisibility() != View.VISIBLE) {
					v.setVisibility(View.VISIBLE);
					// 开始定时器检测显示了多长时间
				} else {
					// 结束定时器
					v.setVisibility(View.GONE);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				if (v.getVisibility() == View.GONE) {
					v.setVisibility(View.INVISIBLE);
				}
			}
		};
		anim.setAnimationListener(listener);
		v.startAnimation(anim);
	}

	// ////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	public void onShowPress() {

	}

	/**
	 * 
	 * @param channel
	 */
	public void requestImages(RSSChannel channel, boolean isImage) {
		if (channel == null)
			return;
		if (updateThread != null) {
			updateThread.stopRequest();
			updateThread = null;
		}
		int count = channel.getItems().size();
		for (int i = 0; i < count; i++) {
			RSSItem item = channel.getItem(i);
			//
			if (isImage)
				requestImage(item.getImageUrl(), Commond.PAGE_IMAGE);
			requestImage(item.getThumbailUrl(), Commond.PAGE_ICON);
		}
	}

	/**
	 * 
	 * @param index
	 */
	public void requestImage(String url, int pageid) {
		// Debug.d("requestImage url:" + url);
		if (TextUtils.isEmpty(url))
			return;
		if (!url.startsWith("http://") && !url.startsWith("file://"))
			return;
		if (updateThread == null) {
			updateThread = new UpdateThread(this, service, true);
			updateThread.startRequest();
		}
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(pageid);
		entity.getItems().put(ContantValue.EXTRA_URL, url);
		updateThread.addRequest(entity);
	}

	/**
	 * 
	 * @param url
	 * @param pageId
	 * @param isClear
	 */
	public void requestItem(String url, int pageId, boolean isClear) {
		Debug.d("requestItem url:" + url);
		if (TextUtils.isEmpty(url))
			return;
		RequestDataEntity entity = new RequestDataEntity();
		entity.setPageID(pageId);
		entity.getItems().put(ContantValue.EXTRA_URL, url);
		//
		entity.getItems().put(Commond.MESSAGE_ISCLEAR_RESULT, isClear);
		//
		hideTipIcon();
		//
		UpdateThread thread = new UpdateThread(this, service, entity);
		thread.startRequest();
	}

	/**
	 * 
	 * @param url
	 */
	private void loginRequest(String url) {
		StringBuffer sb = new StringBuffer();
		try {
			ApplicationInfo ai = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			if (ai != null && ai.metaData != null) {
				// admob
				String admob = (String) ai.metaData.get("ADMOB_PUBLISHER_ID");
				sb.append(admob);
				// wooboo
				sb.append("__");
				String wooboo = (String) ai.metaData.get("Wooboo_PID");
				sb.append(wooboo);
				// youmi
				sb.append("__");
				String umsec = (String) ai.metaData.get("UmAd_APP_SEC");
				sb.append(umsec);
				//
				sb.append("_");
				String umaid = (String) ai.metaData.get("UmAd_APP_ID");
				sb.append(umaid);
				// casee
				sb.append("__");
				String caseeid = (String) ai.metaData
						.get("com.casee.adsdk.siteId");
				sb.append(caseeid);
				// smartmad
				sb.append("__");
				String smartmad = (String) ai.metaData.get("APP_ID");
				sb.append(smartmad);
				// smartmad
				sb.append("__");
				//String DOMOB_PID = (String) ai.metaData.get("DOMOB_PID");
				//sb.append(DOMOB_PID);
				// Debug.d("sb:" + sb.toString());
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		service.requestLogin(this, url, sb.toString());
	}

	/**
	 * 
	 */
	public void onUpdateRequest(BaseService service, RequestDataEntity entity) {
		// TODO Auto-generated method stub
		int pageid = entity.getPageID();
		boolean isClear = false;
		Object obj = entity.getItems().get(Commond.MESSAGE_ISCLEAR_RESULT);
		if (obj != null) {
			isClear = ((Boolean) obj).booleanValue();
		}
		String url = (String) entity.getItems().get(ContantValue.EXTRA_URL);
		if (Commond.preUrl(this, url)) {
			return;
		}
		Debug.d("onUpdateRequest url:" + url);
		Debug.d("onUpdateRequest pageid:" + pageid);
		switch (pageid) {
		case Commond.PAGE_LOGIN:
			loginRequest(url);
			isClear = true;
			break;
		case Commond.PAGE_CATEGORY:
			isClear = false;
		case Commond.PAGE_CHANNEL:
		case Commond.PAGE_FEED:
		case Commond.PAGE_ITEM:
			// 表示网络请求开始,显示等待状态
			if (isClear)
				Commond.sendProcessingMessage(mHandler);
			url = service.requestChannel(url);
			break;
		case Commond.PAGE_IMAGE:
			// 表示网络请求开始,显示等待状态
			Commond.sendProcessingMessage(mHandler);
		case Commond.PAGE_ICON:
			service.requestFile(null, null, url);
			break;
		case Commond.PAGE_FILE:
		case Commond.PAGE_FULL_IMAGE:
			service.requestFile(null, mProgressListener, url);
			break;
		default:
			// 表示网络请求开始,显示等待状态
			if (isClear)
				Commond.sendProcessingMessage(mHandler);
			url = service.requestChannel(url);
			break;
		}
		// 发送网络请求处理完毕的消息
		Commond.sendProcessedMessage(mHandler, null, pageid, url, isClear);
	}

	/**
	 * 
	 * @param title
	 * @param url
	 */
	public void popupMenuRequest(String title, String url, WebView aWebView,
			int pageid) {
		if (TextUtils.isEmpty(url))
			return;
		if (url.equals("http://copytext:")) {
			try {
				KeyEvent shiftPressEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_SHIFT_LEFT);
				shiftPressEvent.dispatch(aWebView);
			} catch (Exception e) {
				throw new AssertionError(e);
			}
			return;
		} else if (url.equals("http://copylink:")) {
			// 获取剪贴板管理器ClipboardManager
			ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			// 复制
			cm.setText(mChannel.getDescription());
			Commond.showToast(this, title + this.getText(R.string.op_success));

			return;
		} else if (url.startsWith("http://copyall:")) {
			if (mChannel != null) {
				String desc = HtmlRegexpUtil.getBody(mChannel.getDescription());
				// 获取剪贴板管理器ClipboardManager
				ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				// 复制
				cm.setText(desc);
				Commond.showToast(this,
						title + this.getText(R.string.op_clipboard_success));
			}
			return;
		} else if (url.startsWith("http://viewresult:")) {
			if (mChannel != null) {
				Commond.showToast(this,
						StringUtil.replaceBR(mChannel.getCopyright()));
			}
			return;
		} else if (url.startsWith("http://save:")) {
			Intent i = new Intent(mContext, FileBrowserActivity.class);
			i.putExtra(ContantValue.EXTRA_TITLE,
					this.getString(R.string.select_path));
			i.putExtra(ContantValue.FILEBROWER_FOLDER,
					Commond.userInfo.getDefaultFolder());
			i.putExtra(ContantValue.EXTRA_FLG,
					ContantValue.FILEBROWER_FLG_FOLDER);
			this.startActivityForResult(i, ContantValue.REQUEST_CODE_SAVEIMAGE);
			return;
		} else if (url.startsWith("http://setbg:")) {
			if (mChannel == null)
				return;
			//
			RSSImage rssimg = mChannel.getRSSImage();
			if (rssimg == null) {
				return;
			}
			String imgurl = rssimg.getUrl();
			if (TextUtils.isEmpty(imgurl))
				return;
			try {
				//
				String filename = bindService.service.getExitFileName(imgurl);
				if (!TextUtils.isEmpty(filename)) {
					// 注意这里需要保存到一个程序不会删除的目录，否则有可能被删除掉
					String name = FileUtil.getNamePart(filename);
					FileOutputStream fos = this.openFileOutput(name,
							MODE_PRIVATE | MODE_WORLD_WRITEABLE);
					if (!FileUtil.copyFile(filename, fos)) {
						Commond.showToast(this,
								title + this.getString(R.string.op_failed));
						return;
					}
					FileInputStream is = this.openFileInput(name);
					this.setWallpaper(is);
					is.close();
					is = null;
					//
					Commond.showToast(this,
							title + this.getString(R.string.op_success));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Commond.showToast(this,
						title + this.getString(R.string.op_failed));
				e.printStackTrace();
			}
			return;
		}
		requestItem(url, pageid, true);
	}

	/**
	 * 
	 * @param channel
	 * @param cate
	 * @param resid
	 */
	public void setMenuItem(BaseService service, RSSChannel channel,
			String cate, int resid) {
		//
		if (channel != null) {
			ArrayList<String> tList = new ArrayList<String>();
			ArrayList<String> iList = new ArrayList<String>();
			ArrayList<String> lList = new ArrayList<String>();
			//
			for (RSSItem item : channel.getItems()) {
				if (!cate.equals(item.getCategory()))
					continue;
				// Debug.d("item.getTitle():" + item.getTitle());
				tList.add(item.getTitle());
				iList.add(item.getThumbailUrl());
				lList.add(item.getLink());
			}
			//
			Object[] titles = tList.toArray();
			Object[] iconUrls = iList.toArray();
			Object[] linkUrls = lList.toArray();
			//
			GridView gv = (GridView) this.findViewById(resid);
			if (gv != null) {
				ListAdapter adapter = gv.getAdapter();
				if (adapter == null) {
					adapter = new MenuAdapter(this, R.layout.menu_popup_item,
							iconUrls, linkUrls, titles, service);
				} else {
					((MenuAdapter) adapter).updateData(iconUrls, linkUrls,
							titles);
				}
				gv.setAdapter(adapter);
			}
		}
	}

	//
	public class OnScrollListenerEx implements OnScrollListener {
		int firstVisibleItem;
		int visibleItemCount;
		int totalItemCount;
		ListView listView;

		public OnScrollListenerEx(ListView listview) {
			listView = listview;
		}

		@Override
		public void onScroll(AbsListView arg0, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			this.firstVisibleItem = firstVisibleItem;
			this.visibleItemCount = visibleItemCount;
			this.totalItemCount = totalItemCount;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (mChannel == null)
				return;
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// Debug.d("totalItemCount:" + totalItemCount);
				// Debug.d("firstVisibleItem:" + firstVisibleItem);
				// Debug.d("visibleItemCount:" + visibleItemCount);
				// Debug.d("totalItemCount:" + totalItemCount);
				if (totalItemCount > 0
						&& firstVisibleItem + visibleItemCount == totalItemCount) {
					//
					String nextUrl = mChannel.getNextLink();
					// Debug.d("nextUrl:" + nextUrl);
					if (!TextUtils.isEmpty(nextUrl)) {
						showListFooterView(listView, true);
						requestItem(nextUrl, Commond.PAGE_CHANNEL, false);
					}
				}
				// 设置隐藏
				// view.setVerticalFadingEdgeEnabled(true);
			}
		}
	};

	// /////////////////////////////w/////////////////
	// //////////////////////////////////////////////
	// //////////////////////////////////////////////
	/**
	 * 
	 */
	View mFooterView;

	/**
	 * 
	 * @param listView
	 */
	public void loadListFooterView(ListView listView) {
		LayoutInflater vi = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFooterView = vi.inflate(R.layout.loading_foot, null);
		listView.addFooterView(mFooterView, null, false);
	}

	/**
	 * 
	 * @param listViewId
	 * @param footerViewLayout
	 * @param isShow
	 */
	public void showListFooterView(ListView listView, boolean isShow) {
		if (mFooterView == null || listView == null) {
			return;
		}
		if (isShow) {
			// listView.addFooterView(mFooterView, null, false);
			mFooterView.setVisibility(View.VISIBLE);
		} else {
			mFooterView.setVisibility(View.GONE);
			// listView.removeFooterView(mFooterView);
		}
	}

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	public View progressView;
	/**
	 * 
	 */
	public Handler mProgressHandler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			// mListAdapter.notifyDataSetChanged();
			Bundle b = m.getData();
			if (b == null)
				return;
			String tag = b.getString("tag");
			String text = b.getString("text");
			if (progressView != null && !TextUtils.isEmpty(text)) {
				TextView tvProgress = (TextView) progressView
						.findViewWithTag(tag);
				if (tvProgress != null)
					tvProgress.setText(text);
			}
			b = null;
			m = null;
		}
	};
	/**
	 * 
	 */
	public ProgressListener mDownloadListener = new ProgressListener() {
		@Override
		public void onProgress(Object tag, int progress) {
			if (tag != null) {
				RSSItem item = (RSSItem) tag;
				ItemData itemData = (ItemData) item.getTag();
				itemData.progress = progress + "%";
				//
				sendChangeMsg(itemData.tag, itemData.progress);
			}
		}

		@Override
		public void onError(String msg) {
			// TODO Auto-generated method stub
			Commond.showToast(BaseActivity.this, msg);

		}

		@Override
		public void onBaseUrl(String url) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 
	 * @param url
	 * @return
	 */
	public boolean showTipIcon(final String url, final int pageid) {
		final View vRefresh = findViewById(R.id.btn_refresh);
		if (vRefresh == null)
			return false;
		//
		vRefresh.setVisibility(View.VISIBLE);
		if (url == null)
			return false;
		if (TextUtils.isEmpty(extra_flg)) {
			vRefresh.setBackgroundResource(R.drawable.btn_reflesh_background);
			vRefresh.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					vRefresh.setVisibility(View.GONE);
					requestItem(url, pageid, true);
				}
			});
		} else {
			vRefresh.setBackgroundResource(R.drawable.fav_nothing);
		}
		return true;
	}

	/**
	 * 
	 */
	public void hideTipIcon() {
		final View vRefresh = findViewById(R.id.btn_refresh);
		if (vRefresh == null)
			return;
		vRefresh.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param tag
	 * @param text
	 */
	public void sendChangeMsg(String tag, String text) {
		//
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("tag", tag);
		b.putString("text", text);
		msg.setData(b);
		mProgressHandler.sendMessage(msg);
	}

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	/**
	 * 
	 */
	protected ProgressListener mProgressListener = new ProgressListener() {

		@Override
		public void onProgress(Object tag, int progress) {
			// TODO Auto-generated method stub
			// Debug.d("progress:" + progress);
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putString(Commond.MESSAGE_PROGRESS_RESULT, progress + "%");
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(String msg) {
			// TODO Auto-generated method stub
			Commond.showToast(BaseActivity.this, msg);
		}

		@Override
		public void onBaseUrl(String url) {
			// TODO Auto-generated method stub

		}
	};

	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	// /////////////////////////////////////////////////
	/**
	 * 
	 * @param m
	 */
	public boolean handleMessage(Message m) {
		return false;
	}

	//
	protected HandlerEx mHandler = new HandlerEx(this);

	/**
	 * 
	 */
	public class HandlerEx extends Handler {
		BaseActivity mActivity;

		public HandlerEx(BaseActivity baseActivity) {
			// TODO Auto-generated constructor stub
			this.mActivity = baseActivity;
		}

		@Override
		public void handleMessage(Message m) {
			if (m.getData() != null) {
				// 自处理
				if (mActivity.handleMessage(m))
					return;
				// 显示下载进度信息
				String progress = m.getData().getString(
						Commond.MESSAGE_PROGRESS_RESULT);
				if (!TextUtils.isEmpty(progress)) {
					TextView tv = (TextView) mActivity
							.findViewById(R.id.progressText);
					if (tv != null) {
						tv.setText(progress);
						tv.setVisibility(View.VISIBLE);
					}
					m = null;
					return;
				}
				// 显示提示消息
				String message = m.getData().getString(
						Commond.MESSAGE_MESSAGE_RESULT);
				if (!TextUtils.isEmpty(message)) {
					Commond.showToast(mActivity, message);
				}
				//
				int result = m.getData().getInt(Commond.MESSAGE_DATA_RESULT);
				TextView tv = (TextView) mActivity
						.findViewById(R.id.progressText);
				View pview = mActivity.findViewById(R.id.progressImage);
				if (result == Commond.MESSAGE_PROCESSINGFLAG) {
					if (pview != null) {
						Debug.d("pview VISIBLE url:111");
						pview.setVisibility(View.VISIBLE);
					}
					if (tv != null) {
						// tv.setText("0%");
						// tv.setVisibility(View.VISIBLE);
					}
				} else {
					boolean isClear = m.getData().getBoolean(
							Commond.MESSAGE_ISCLEAR_RESULT);
					String url = m.getData().getString(
							Commond.MESSAGE_URL_RESULT);
					if (pview != null && isClear) {
						Debug.d("pview gone url:" + url);
						pview.setVisibility(View.GONE);
					}
					if (tv != null) {
						tv.setVisibility(View.GONE);
					}
					//
					int pageid = m.getData().getInt(
							Commond.MESSAGE_PAGEID_RESULT);

					try {
						loadUpdateThread(url, pageid, isClear);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	};

	/**
	 * 
	 * @param result
	 */
	public void loadLogin(String url) {
		RSSChannel channel = this.service.getChannel(url);
		if (channel == null)
			return;
		//
		String desc = channel.getDescription();
		if (!TextUtils.isEmpty(desc)) {
			if (desc.startsWith("urlsxmltr:")) {
				Data.saveTabList(this, desc.substring("urlsxmltr:".length()));
			}
		}
		String title = channel.getTitle();
		if (!TextUtils.isEmpty(title)) {
			if (title.equals("exit")) {
				finish();// 退出程序
			} else if (title.startsWith("http://")) {
				Uri uri = Uri.parse(title);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			} else if (title.startsWith("web://")) {
				GotoActivity.gotoWebDialog(this,
						title.substring("web://".length()));
			} else if (title.startsWith("url://")) {
				GotoActivity.gotoWebDialog(this,
						title.substring("url://".length()));
			} else if (title.startsWith("update:")) {
				// 版本更新提示处理
//				String str = title.substring("update://".length());
//				String[] tmp = str.split("\\|");
//				if (tmp.length == 3) {
//					CheckVerDialog dialog = new CheckVerDialog(mContext,
//							tmp[0], tmp[1], tmp[2]);
//					dialog.show();
//				}
			} else if (title.startsWith("apk:")) {
				// 版本更新提示处理
				desc = desc.replace('n', '\n');
				Commond.downloadApk(this, service.rootPath, null,
						title.substring("apk:".length()));
			} else {
				String tip = title.replace('n', '\n');
				Commond.showToast(this, tip);
			}
			// 检测广告
			for (RSSItem item : channel.getItems()) {
				if ("apk_ad".equalsIgnoreCase(item.getCategory())) {
					String packageName = item.getImageUrl();
					if (!TextUtils.isEmpty(packageName)) {
						if (!Commond.apkInstalled(this, packageName, false)) {
							Commond.userInfo.adItem.setTitle(item.getTitle());
							Commond.userInfo.adItem.setPubDate(item
									.getPubDate());
							Commond.userInfo.adItem.setThumbailUrl(item
									.getThumbailUrl());
							Commond.userInfo.adItem.setImageUrl(item
									.getImageUrl());
							Commond.userInfo.adItem.setLink(item.getLink());
							Commond.userInfo.saveData();
							insertAdItem(true);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean insertAdItem(boolean isFresh) {
		return false;
	}

	/**
	 * 
	 */
	public boolean hasAdItem() {
		//
		if (!TextUtils.isEmpty(Commond.userInfo.adItem.getTitle())) {
			String packageName = Commond.userInfo.adItem.getImageUrl();
			if (!TextUtils.isEmpty(packageName)) {
				if (!Commond.apkInstalled(this, packageName, false)) {
					return true;
				} else {
					Commond.userInfo.adItem.setTitle("");
					Commond.userInfo.adItem.setImageUrl("");
					Commond.userInfo.adItem.setThumbailUrl("");
					Commond.userInfo.saveData();
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param url
	 * @param pageId
	 */
	public void loadUpdateThread(String url, int pageId, boolean isClear)
			throws Exception {
		switch (pageId) {
		case Commond.PAGE_LOGIN:
			loadLogin(url);
			break;
		case Commond.PAGE_ICON:
			GridView gv = (GridView) this.findViewById(R.id.popup_comment);
			if (gv != null) {
				ListAdapter adapter = gv.getAdapter();
				if (adapter != null)
					gv.setAdapter(adapter);
			}
			//
			GridView gvMore = (GridView) this.findViewById(R.id.popup_more);
			if (gvMore != null) {
				ListAdapter adapterMore = gvMore.getAdapter();
				if (adapterMore != null)
					gvMore.setAdapter(adapterMore);
			}
			//
			GridView gvChannel = (GridView) this
					.findViewById(R.id.optionmenu_popup_grid);
			if (gvChannel != null) {
				ListAdapter adapterChannel = gvChannel.getAdapter();
				if (adapterChannel != null)
					gvChannel.setAdapter(adapterChannel);
			}
			break;
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean closePopup() {
		View vv = this.findViewById(R.id.optionmenu_popup_grid);
		if (vv != null && vv.getVisibility() == View.VISIBLE) {
			Commond.showOptionMenu(this, false);
			return true;
		}
		View vSearch = this.findViewById(R.id.search_panel);
		if (vSearch != null && vSearch.getVisibility() == View.VISIBLE) {
			Commond.showSearchInput(this, false);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param isNext
	 * @param channel
	 */
	public void onScroller(boolean isNext) {
		RSSChannel channel = service.getChannel(service.categoryUrl);
		if (channel == null)
			return;
		if (isNext) {
			if (mCurrentChannelIndex < channel.getItems().size() - 1) {
				mCurrentChannelIndex++;
				requestItem(channel.getItem(mCurrentChannelIndex).getLink(),
						Commond.PAGE_CHANNEL, true);
				//
				Commond.setDotView(this, mCurrentChannelIndex, channel
						.getItems().size() - mCurrentChannelIndex - 1);
			}
		} else {
			if (mCurrentChannelIndex > 0) {
				mCurrentChannelIndex--;
				requestItem(channel.getItem(mCurrentChannelIndex).getLink(),
						Commond.PAGE_CHANNEL, true);
				//
				Commond.setDotView(this, mCurrentChannelIndex, channel
						.getItems().size() - mCurrentChannelIndex - 1);
			}
		}
	}

	/**
	 * 截获按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 关闭弹出的窗口
			if (closePopup())
				return true;
			if (needBackConfirm) {
				if (!isReadyExit || !isConfirmExit) {
					//
					isReadyExit = true;
					Commond.showToast(mContext, this.getText(R.string.exit_tip)
							.toString());
					return true;
				} else {
					return super.onKeyDown(keyCode, event);
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 截获按键
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (needBackConfirm) {
				isUpExit = true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 
	 */
	public boolean installApk(String filename) {
		if (TextUtils.isEmpty(filename))
			return false;
		int result = Settings.Secure.getInt(getContentResolver(),
				Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		if (result == 0) {
			// show some dialog here
			// ...
			// and may be show application settings dialog manually

			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
			startActivity(intent);
		} else {
			//
			String name = "share_temp.apk";
			try {
				FileInputStream fis = new FileInputStream(filename);
				FileOutputStream fos = openFileOutput(name,
						Context.MODE_WORLD_READABLE);
				byte[] buffer = new byte[1024];
				int len = 0;
				int count = 0;
				while ((len = fis.read(buffer)) != -1) {
					count += len; // 字节数 文件大小
					fos.write(buffer, 0, len);
				}
				buffer = null;
				fis.close();
				fis = null;
				fos.flush();
				fos.close();
				fos = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			//
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File f = getFileStreamPath(name);
			intent.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()),
					"application/vnd.android.package-archive");
			// 或者
			// intent.setDataAndType(Uri.fromFile(f),
			// "application/vnd.android.package-archive");
			startActivity(intent);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public void refleshMessageView(DBAdapter db, int resId) {
		Button btnMsg = (Button) findViewById(resId);
		if (btnMsg != null) {
			int count = db.getUnReadCount();
			if (count == 0) {
				btnMsg.setText(null);
				btnMsg.setBackgroundResource(R.drawable.btn_message_no_background);
			} else {
				btnMsg.setBackgroundResource(R.drawable.btn_message_background);
				btnMsg.setText(String.valueOf(count));
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
	}

	/**
	 * 
	 */
	public boolean dispatchAd() {
		View adview = this.findViewById(R.id.ad_panel);
		if (adview == null)
			return false;

		long downTime = android.os.SystemClock.uptimeMillis();// 120315252;
		long eventTime = android.os.SystemClock.uptimeMillis();// 120315414;
		int action = MotionEvent.ACTION_DOWN;// 1;
		// 0.0;65539;0
		float x = 127.79217f;// r.left + r.width() / 2;
		float y = 458.31918f;// r.top + r.height() / 2;
		float pressure = 0.0627451f;// 0.0627451;
		float size = 0.0f;// 0.0;
		int metaState = 0;// 0;
		float xPrecision = 0.0f;
		float yPrecision = 0.0f;
		int deviceId = 0;// 65539;
		int edgeFlags = 0;// 0;
		MotionEvent e = MotionEvent.obtain(downTime, eventTime, action, x, y,
				pressure, size, metaState, xPrecision, yPrecision, deviceId,
				edgeFlags);
		requestAd(adview, e, 0);
		e.setAction(MotionEvent.ACTION_UP);
		requestAd(adview, e, 1);
		// printEvent(e);
		return true;
	}

	/**
	 * 
	 * @param adview
	 * @param e
	 */
	private void requestAd(View adview, android.view.MotionEvent e, int c) {
		ArrayList<View> vs = adview.getTouchables();
		if (vs != null) {
			for (View v : vs) {
				// Debug.d("v.name=" + v.getClass().getName());
				// com.wooboo.adlib_android.gg gg;
				// com.wooboo.adlib_android._ aa;
				// Debug.d("dispatchTouchEvent...begin");
				if (v.dispatchTouchEvent(e)) {
					// Debug.d("dispatchTouchEvent...end");
					// adview.setVisibility(View.GONE);//这个打开将不会请求广告
					// return true;
					//
				}
			}
		}
	}

	/**
	 * 
	 */
	public void showStatusBar(boolean isFullScreen) {
		if (isFullScreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	/**
	 * 
	 */
	public void switchFullScreen() {
		Commond.userInfo.setFullscreen(!Commond.userInfo.isFullscreen());
		Commond.userInfo.saveData();
		showStatusBar(Commond.userInfo.isFullscreen());
	}

	protected void setMenuBackground() {
		getLayoutInflater().setFactory(new Factory() {
			public View onCreateView(String name, Context context,
					AttributeSet attrs) {
				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView(name, null, attrs);
						new Handler().post(new Runnable() {
							public void run() {
								view.setBackgroundResource(R.drawable.tipbar_down_bg);
							}
						});
						return view;
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
				}
				return null;
			}
		});
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.menu, menu);
	// setMenuBackground();
	// return super.onCreateOptionsMenu(menu);
	// }

	/**
	 * 
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Commond.showOptionMenu(BaseActivity.this, false);
		return true;
	}

	//
	// /**
	// *
	// */
	// public boolean onOptionsItemSelected(MenuItem item) {
	// super.onOptionsItemSelected(item);
	// if (item.getItemId() == R.id.menu_about) {
	// return false;
	// }
	// if (item.getItemId() == R.id.menu_report) {
	// return false;
	// }
	// if (item.getItemId() == R.id.menu_mianze) {
	// return false;
	// }
	// if (item.getItemId() == R.id.menu_exit) {
	// return false;
	// }
	// return true;
	// }

	/**
	 * 
	 */
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent e) {
	// View adview = this.findViewById(R.id.adview);
	// if (adview != null) {
	// Rect r = new Rect();
	// adview.getGlobalVisibleRect(r);
	// if (r.contains((int) e.getX(), (int) e.getY())) {
	// // printEvent(e);
	// // Debug.d("dispatchTouchEvent:22222");
	// requestAd(adview, e, 1);
	// return super.dispatchTouchEvent(e);
	// }
	// }
	// if (this.isHideTitleMenu && e.getAction() == MotionEvent.ACTION_DOWN) {
	// this.initTitleMenu(this.isHideTitleMenu);
	// }
	// //
	// return super.dispatchTouchEvent(e);
	// }
	/**
	 * 
	 */
	@Override
	public void onUserInteraction() {
		if (isUpExit) {
			isUpExit = false;
			return;
		}
		if (isReadyExit && isConfirmExit) {
			isReadyExit = false;
			isConfirmExit = false;
			return;
		}
		if (isReadyExit) {
			isConfirmExit = true;
		}
		super.onUserInteraction();
	}

	@Override
	public void onDestroy() {
		// 请求广告
		if (AdUtils.showAdCount > 0 && AdUtils.ishackad) {
			if (dispatchAd()) {
				AdUtils.showAdCount--;
			}
		}
		//
		if (updateThread != null)
			updateThread.stopRequest();
		//
		if (needStopService) {
			//
			Thread thread = new Thread() {
				@Override
				public void run() {
					service.clearSavedFile(Commond.userInfo.getSaveDays());
				}
			};
			thread.start();
		}
		//
		if (bindService != null && bindService.service != null) {
			bindService.unbindService(this);
			if (needStopService) {
				bindService.stopService(this);
			}
			bindService = null;
		}

		super.onDestroy();
	}

	protected void onPause() {
		if (sensorMgr != null) {
			sensorMgr.unregisterListener(this, this.sensor);
			sensorMgr = null;
		}
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	/**
	 * 
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensor = event.sensor.getType();
		if (sensor == Sensor.TYPE_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			// 每100毫秒检测一次
			if ((curTime - lastUpdate) > 3000) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
				float[] values = event.values;
				x = values[SensorManager.DATA_X];
				y = values[SensorManager.DATA_Y];
				z = values[SensorManager.DATA_Z];

				float speed = Math.abs(x + y + z - last_x - last_y - last_z)
						/ diffTime * 10000;
				// Debug.d("speed:" + speed);
				if (speed > 3 && speed < 10) {
					//
					try {
						FileOutputStream stream;
						stream = new FileOutputStream(new File("/sdcard/"
								+ System.currentTimeMillis() + ".png"));
						getWindow().getDecorView().getDrawingCache()
								.compress(CompressFormat.PNG, 100, stream);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Commond.showToast(this, "保存成功: " + speed);
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}

	public void rightInLeftOut() {
	}

	public void rightOutLeftIn() {

	}

	/**
	 * 
	 * @param resids
	 */
	public void setGestureDetector(final int[] resids) {
		//
		if (Commond.userInfo.userCount() < 1)
			Commond.showToast(this, this.getString(R.string.ondoubletap_tip));
		//
		gestureScanner = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onDown(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float arg3) {
				if (e1 == null || e2 == null)
					return false;
				// TODO Auto-generated method stub
				int SWIPE_MIN_DISTANCE = 200;
				int SWIPE_MAX_OFF_PATH = 250;
				int SWIPE_THRESHOLD_VELOCITY = 200;
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// viewFlipper.setInAnimation(slideLeftIn);
					// viewFlipper.setOutAnimation(slideLeftOut);
					// viewFlipper.showNext();
					rightInLeftOut();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// viewFlipper.setInAnimation(slideRightIn);
					// viewFlipper.setOutAnimation(slideRightOut);
					// viewFlipper.showPrevious();
					rightOutLeftIn();
					return true;
				}
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					// viewFlipper.setInAnimation(slideLeftIn);
					// viewFlipper.setOutAnimation(slideLeftOut);
					// viewFlipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					// viewFlipper.setInAnimation(slideRightIn);
					// viewFlipper.setOutAnimation(slideRightOut);
					// viewFlipper.showPrevious();
				}
				return false;
			}

			@Override
			public void onLongPress(MotionEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
					float arg2, float arg3) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onSingleTapUp(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		gestureScanner
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						//
						for (int i = 0; i < resids.length; i++) {
							View v = findViewById(resids[i]);
							hideViewAnim(v);
						}
						Commond.isHidePanel = !Commond.isHidePanel;
						//
						switchFullScreen();
						return true;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						return true;
					}
				});
	}

	@Override
	public void onProgress(Object tag, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String msg) {
		// TODO Auto-generated method stub
		Debug.d("onError:" + msg);
		//
		Message message = new Message();
		Bundle data = new Bundle();
		message.setData(data);
		data.putString(Commond.MESSAGE_MESSAGE_RESULT, msg);
		mHandler.sendMessage(message);
	}

	@Override
	public void onBaseUrl(String url) {
		// TODO Auto-generated method stub

	}
}