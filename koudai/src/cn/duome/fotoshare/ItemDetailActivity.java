package cn.duome.fotoshare;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.duome.fotoshare.adapter.ItemDetailAdapter;
import cn.duome.fotoshare.adapter.ShareItemAdapter;
import cn.duome.fotoshare.utils.Constant;
import cn.duome.fotoshare.utils.FotoCommond;
import cn.duome.fotoshare.view.CircleFlowIndicator;
import cn.duome.fotoshare.view.ViewFlow;
import cn.duome.fotoshare.view.ViewFlow.ViewSwitchListener;

import com.chuangyu.music.R;
import com.hutuchong.share.ShareKaixin001;
import com.hutuchong.share.ShareRenren;
import com.hutuchong.share.ShareWeibo;
import com.hutuchong.util.BaseCommond;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;
/**
 * 图片详细信息页面
 * @author dky
 *
 */
public class ItemDetailActivity extends FotoBaseActivity {
	ArrayList<ViewFlow> viewFlows = new ArrayList<ViewFlow>();
	View vf_panel;
	RSSItem mItem;
	EditText etInput;
	String commentListUrl = "http://xlb.jianrencun.com/fotoshare/foto_comments.php";
	GridView gvShare;
	ShareItemAdapter shareAdapter;
	String mInput;
	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		Intent i = this.getIntent();
		int index = i.getIntExtra(BaseContant.EXTRA_INDEX, 0);
		mItem = FotoCommond.mChannel.getItem(index);
		// 初始化
		init(mItem.getTitle(), R.layout.itemdetail, true);
		//
		vf_panel = findViewById(R.id.vf_panel);
		//
		ViewFlow viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new ItemDetailAdapter(this, FotoCommond.mChannel,
				this.imagelistener, clickListener), index);
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
		viewFlow.setOnViewSwitchListener(this.switchListener);

		viewFlows.add(viewFlow);

		// 回复按钮
		View btnReply = findViewById(R.id.btn_reply);
		btnReply.setOnClickListener(clickListener);
		// 回复输入框
		etInput = (EditText) findViewById(R.id.et_input);
		// 初始化共享面板
		initSharePanel();
	}

	/**
	 * 点击事件侦听器
	 */
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String email = BaseCommond.userInfo.getEmail();
			if (TextUtils.isEmpty(email) && arg0.getId() != R.id.wb_panel) {
				Intent i = new Intent(mContext, UserLoginActivity.class);
				startActivityForResult(i, Constant.REQUEST_CODE_LOGIN);
				return;
			}
			switch (arg0.getId()) {
			case R.id.btn_reply:
				String input = etInput.getText().toString();
				if (TextUtils.isEmpty(input)) {
					Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String url1 = "http://xlb.jianrencun.com/fotoshare/user_comment.php";
				String data = "email=" + URLEncoder.encode(email) + "&id="
						+ mItem.getId() + "&nick="
						+ URLEncoder.encode(BaseCommond.userInfo.getUserName())
						+ "&input=" + URLEncoder.encode(input);
				//
				mInput = input;
				etInput.setText("");
				etInput.clearFocus();
				etInput.setHint("正在发送中...");
				//
				// getWindow().setSoftInputMode(
				// WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				InputMethodManager imm = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(
							etInput.getApplicationWindowToken(), 0);
				}
				//
				requestItem(url1, BaseContant.PAGE_POST, true, true, data, null);
				break;
			case R.id.love_panel:
				String url2 = "http://xlb.jianrencun.com/fotoshare/user_love.php?id="
						+ mItem.getId() + "&email=" + URLEncoder.encode(email);
				requestItem(url2, BaseContant.PAGE_VOTE, true, true, null, true);
				break;

			case R.id.fav_panel:
				String url3 = "http://xlb.jianrencun.com/fotoshare/user_fav.php?id="
						+ mItem.getId() + "&email=" + URLEncoder.encode(email);
				requestItem(url3, BaseContant.PAGE_FAV, true, true, null, true);
				break;
			case R.id.wb_panel:
				if (gvShare.getVisibility() != View.VISIBLE) {
					gvShare.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(
							ItemDetailActivity.this, R.anim.popup_alpha_scale);
					gvShare.startAnimation(anim);
				} else {
					hideSharePanel();
				}
				break;
			}
		}
	};

	/**
	 * 分享列表设置
	 */
	private void initSharePanel() {
		int[] shareIconIds = { R.drawable.icon_renren,
				R.drawable.icon_kaixin001, R.drawable.icon_tencent,
				R.drawable.icon_sina };
		int[] shareTitleIds = { R.string.share_renren,
				R.string.share_kaixin001, R.string.share_tencent,
				R.string.share_sina };
		//
		gvShare = (GridView) findViewById(R.id.gridview_share);
		shareAdapter = new ShareItemAdapter(this, shareIconIds, shareTitleIds);
		gvShare.setAdapter(shareAdapter);
		gvShare.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String title = TextUtils.isEmpty(mItem.getTitle()) ? "" : mItem
						.getTitle();
				String content = TextUtils.isEmpty(mItem.getDescription()) ? ""
						: mItem.getDescription();
				String link = mItem.getImageUrl();
				String downUrl = "";
				String picUrl = mItem.getImageUrl();
				//
				switch (arg2) {
				case 0:// 人人分享
					ShareRenren.getInstance(ItemDetailActivity.this).shareFeed(
							title, content, link, picUrl,
							getString(R.string.app_name), downUrl);
					break;
				case 1:// 开心分享
					ShareKaixin001.getInstance(ItemDetailActivity.this).share(
							title, content, link, picUrl, null, downUrl, null);
					break;
				case 2:// 腾讯微博分享
					break;
				case 3:// 新浪微博分享
					ShareWeibo.getInstance(ItemDetailActivity.this, content,
							picUrl);
					break;
				}
				hideSharePanel();
			}
		});
	}

	/**
	 * 加载服务器返回时数据处理函数
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear,
			boolean isRefresh, boolean isRemote) throws Exception {
		//
		switch (pageId) {
		case BaseContant.PAGE_IMAGE:
			ImageView iv = (ImageView) vf_panel.findViewWithTag(url);
			if (iv != null) {
				Bitmap bmp = BaseContent.getInstance(mContext).getCacheBitmap(
						url,0,0);
				if (bmp != null)
					iv.setImageBitmap(bmp);
			}
			break;
		// 回复后返回处理
		case BaseContant.PAGE_POST:
			etInput.setHint(R.string.reply_hint);
			RSSChannel channel = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel != null && !TextUtils.isEmpty(channel.getTitle())) {
				String[] tmps = channel.getTitle().split("\\|");
				if ("0".equals(tmps[0])) {
					String listurl = commentListUrl + "?id=" + mItem.getId();
					requestItem(listurl, BaseContant.PAGE_CHANNEL, true, true,
							null, true);
					etInput.setText("");
					return;
				}
				Toast.makeText(mContext, tmps[1], Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(mContext, "返回异常，回复失败", Toast.LENGTH_SHORT)
						.show();
			}
			etInput.setText(mInput);
			break;
		// 评论列表请求返回处理
		case BaseContant.PAGE_CHANNEL:
			LinearLayout rootView = (LinearLayout) vf_panel
					.findViewWithTag(url);
			if (rootView != null) {
				rootView.removeAllViews();
				//
				RSSChannel channel1 = BaseContent.getInstance(mContext)
						.getChannel(url);
				if (channel1 == null)
					return;
				if (!TextUtils.isEmpty(channel1.getComments())) {
					String[] tmps = channel1.getComments().split("\\|");
					setFavPanel(url, null, tmps[0]);
					setLovePanel(url, null, tmps[1]);
				}
				if (channel1.getItems().size() > 0) {
					// 判断是否为同一条目
					//
					LayoutInflater vi = (LayoutInflater) mContext
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					for (RSSItem item : channel1.getItems()) {
						View itemView = vi.inflate(
								R.layout.itemdetail_item_comments_item, null);
						// 昵称
						TextView tvName = (TextView) itemView
								.findViewById(R.id.item_name);
						tvName.setText(item.getTitle());
						// 评论内容
						TextView tvContent = (TextView) itemView
								.findViewById(R.id.item_content);
						tvContent.setText(item.getDescription());
						// 评论时间
						TextView tvDate = (TextView) itemView
								.findViewById(R.id.item_date);
						tvDate.setText(item.getPubDate());
						//
						rootView.addView(itemView);
					}
				}
			}
			break;
		// 收藏请求返回处理
		case BaseContant.PAGE_FAV:
			RSSChannel channel2 = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel2 != null && !TextUtils.isEmpty(channel2.getTitle())) {
				String[] tmps = channel2.getTitle().split("\\|");
				if ("0".equals(tmps[0])) {
					setFavPanel(url, tmps[1], tmps[2]);
				}
			} else {
				Toast.makeText(mContext, "返回异常，收藏失败", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		// 喜欢请求返回处理
		case BaseContant.PAGE_VOTE:
			RSSChannel channel3 = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel3 != null && !TextUtils.isEmpty(channel3.getTitle())) {
				String[] tmps = channel3.getTitle().split("\\|");
				if ("0".equals(tmps[0])) {
					setLovePanel(url, tmps[1], tmps[2]);
				}
			} else {
				Toast.makeText(mContext, "返回异常，加入喜欢失败", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear, isRefresh, isRemote);
	}

	/**
	 * 设置收藏面板信息
	 * @param url
	 * @param count
	 * @param favFlg
	 */
	private void setFavPanel(String url, String count, String favFlg) {
		int pos = url.indexOf("&");
		if (pos == -1)
			pos = url.length();
		String id = url.substring(url.indexOf("?id=") + 4, pos);
		LinearLayout llFav = (LinearLayout) vf_panel.findViewWithTag(id
				+ "_fav");
		if (!TextUtils.isEmpty(count)) {
			TextView tvFav = (TextView) llFav.findViewById(R.id.tv_fav);
			tvFav.setText(count);
		}
		//
		ImageButton ibFav = (ImageButton) llFav.findViewById(R.id.ib_fav);
		if ("0".equals(favFlg)) {
			ibFav.setBackgroundResource(R.drawable.btn_fav_un_background);
		} else {
			ibFav.setBackgroundResource(R.drawable.btn_fav_background);
		}
	}

	/**
	 * 设置喜欢面板信息
	 * @param url
	 * @param count
	 * @param lovFlg
	 */
	private void setLovePanel(String url, String count, String lovFlg) {
		int pos = url.indexOf("&");
		if (pos == -1)
			pos = url.length();
		String id = url.substring(url.indexOf("?id=") + 4, pos);
		LinearLayout lllove = (LinearLayout) vf_panel.findViewWithTag(id
				+ "_love");
		if (!TextUtils.isEmpty(count)) {
			TextView tvLove = (TextView) lllove.findViewById(R.id.tv_love);
			tvLove.setText(count);
		}
		//
		ImageButton ibLove = (ImageButton) lllove.findViewById(R.id.ib_love);
		if ("0".equals(lovFlg)) {
			ibLove.setBackgroundResource(R.drawable.btn_love_un_background);
		} else {
			ibLove.setBackgroundResource(R.drawable.btn_love_background);
		}
	}

	/**
	 * 左右滑动时切换条目侦听器
	 */
	ViewSwitchListener switchListener = new ViewSwitchListener() {
		@Override
		public void onSwitched(View view, int position) {
			// TODO Auto-generated method stub
			mItem = FotoCommond.mChannel.getItem(position);
			// view.findViewById(id)
			// 设置标题
			TextView tvTitle = (TextView) findViewById(R.id.tv_title);
			if (tvTitle != null) {
				tvTitle.setText(mItem.getTitle());
			}
		}
	};

	/**
	 * 隐藏分享面板
	 */
	private void hideSharePanel() {
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.popup_alpha_out);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				gvShare.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

		});
		gvShare.startAnimation(anim);
	}

	/**
	 * 判断是否触摸点击了分享按钮和分享面板
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		if (gvShare.getVisibility() == View.VISIBLE) {
			Rect r = new Rect();
			int x = (int) e.getRawX();
			int y = (int) e.getRawY();
			r.set(gvShare.getLeft(), gvShare.getTop(), gvShare.getRight(),
					gvShare.getBottom());
			if (!r.contains(x, y)) {
				hideSharePanel();
			}
		}
		return super.dispatchTouchEvent(e);
	}
	/**
	 * 重载本Activity销毁时调用的函数
	 */
	@Override
	public void onDestroy() {
		if (FotoCommond.mChannel != null) {
			for (RSSItem item : FotoCommond.mChannel.getItems()) {
				BaseContent.getInstance(mContext).clearCacheBitmap(
						item.getImageUrl());
			}
		}
		super.onDestroy();
	}
}