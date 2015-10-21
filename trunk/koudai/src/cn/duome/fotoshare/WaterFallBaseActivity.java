package cn.duome.fotoshare;

import java.util.ArrayList;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.duome.fotoshare.FotoBaseActivity;
import cn.duome.fotoshare.ItemDetailActivity;
import com.chuangyu.music.R;
import cn.duome.fotoshare.entity.FlowTag;
import cn.duome.fotoshare.utils.FotoCommond;
import cn.duome.fotoshare.view.FlowScrollView;
import cn.duome.fotoshare.view.FlowScrollView.OnScrollListener;
import cn.duome.fotoshare.view.FlowView;

import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;

/**
 * 瀑布流展示页面基类
 * 
 * @author dky
 * 
 */
public class WaterFallBaseActivity extends FotoBaseActivity {

	RSSChannel mChannel;

	private FlowScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	public ArrayList<LinearLayout> waterfall_items;
	public int current_page = 0;// 当前页数
	public int[] column_height;// 每列的高度

	private Handler handler;
	public int item_width = 0;
	public int column_count = 0;// 显示列数
	public int column_margin = 0;// 列与列之间间隔
	public int page_count = 0;// 每次加载15张图片

	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化
		init(getText(R.string.app_name).toString(), R.layout.home,
				false);
		//
		item_width = 140;
		page_count = 30;
		Display display = this.getWindowManager().getDefaultDisplay();
		column_count = display.getWidth() / item_width;// 根据屏幕大小计算列数
		column_margin = (display.getWidth() - (column_count * item_width))
				/ (column_count + 1);
		column_height = new int[column_count];

		InitLayout();
	}

	/**
	 * 重置处理
	 */
	public void reset() {
		current_page = 0;
		for (int j = 0; j < waterfall_items.size(); j++) {
			waterfall_items.get(j).removeAllViews();
		}
		for (int i = 0; i < column_height.length; i++)
			column_height[i] = 0;
		if (mChannel != null) {
			mChannel.getItems().clear();
		}
	}

	/**
	 * 点击侦听器
	 */
	OnClickListener itemClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			int index = (Integer) arg0.getTag();// ((FlowView)
												// arg0).getFlowTag().getFlowIndex();
			if (mChannel == null || mChannel.getItems().size() <= index)
				return;
			FotoCommond.mChannel = mChannel;
			Intent i = new Intent(mContext, ItemDetailActivity.class);
			i.putExtra(BaseContant.EXTRA_INDEX, index);
			mContext.startActivity(i);
		}
	};

	/**
	 * 初始化数据展示
	 */
	public void InitLayout() {
		waterfall_scroll = (FlowScrollView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onTop() {
				// 滚动到最顶端
				// Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
			}

			@Override
			public void onBottom() {
				// 滚动到最低端
				if (mChannel != null
						&& !TextUtils.isEmpty(mChannel.getNextLink())) {
					++current_page;
					requestItem(mChannel.getNextLink(),
							BaseContant.PAGE_CHANNEL, false, false, null, true);
				}
			}

			@Override
			public void onAutoScroll() {
				// 暂时解决,需重写
				// 自动滚动
				Rect bounds = new Rect();
				Rect scrollBounds = new Rect(waterfall_scroll.getScrollX(),
						waterfall_scroll.getScrollY(), waterfall_scroll
								.getScrollX() + waterfall_scroll.getWidth(),
						waterfall_scroll.getScrollY()
								+ waterfall_scroll.getHeight());

				for (LinearLayout ll : waterfall_items) {
					for (int i = 0; i < ll.getChildCount(); i++) {
						View view = ll.getChildAt(i);
						if (view != null && view instanceof FlowView) {
							FlowView v = (FlowView) view;
							v.getHitRect(bounds);
							if (Rect.intersects(scrollBounds, bounds)) {
								if (v.bitmap == null) {
									v.Reload();
								}
							} else {
								v.recycle();
							}
						}
					}
				}
			}
		});
		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		handler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {

				super.dispatchMessage(msg);
			}

			@Override
			public void handleMessage(Message msg) {
				// super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					break;
				}
			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};
		waterfall_items = new ArrayList<LinearLayout>();
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);

			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemParam.leftMargin = column_margin;
			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}
	}

	/**
	 * 添加单个条目到显示容器
	 * 
	 * @param pageindex
	 * @param pagecount
	 */
	public void AddItemToContainer(int pageindex, int pagecount) {
		int currentIndex = pageindex * pagecount;
		int imagecount = mChannel.getItems().size();
		for (int i = currentIndex; i < pagecount * (pageindex + 1)
				&& i < imagecount; i++) {
			// loaded_count++;
			// j = j >= column_count ? j = 0 : j;
			RSSItem item = mChannel.getItem(i);
			int colIndex = GetMinIndex(column_height);
			String wh[] = item.getAboutAttribute().split("_");
			int h = 0;
			int w = 0;
			if (wh.length == 2) {
				w = Integer.parseInt(wh[0]);
				h = Integer.parseInt(wh[1]);
				h = (140 / w) * h;
			}
			column_height[colIndex] = column_height[colIndex] + h;

			AddImage(item.getThumbailUrl(), colIndex,
					(int) Math.ceil(imagecount / (double) column_count),
					(int) item.getId(), h, i);
		}
	}

	/**
	 * 添加图片控件到展示条目中
	 * 
	 * @param filename
	 * @param columnIndex
	 * @param rowIndex
	 * @param id
	 */
	private void AddImage(String filename, int columnIndex, int rowIndex,
			int id, int h, int index) {

		// FlowView item = (FlowView) LayoutInflater.from(this).inflate(
		// R.layout.app_mall_waterfallitem, null);
		LinearLayout waterfallitem = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.waterfallitem, null);
		waterfallitem.setTag(index);
		waterfallitem.setClickable(true);
		waterfallitem.setOnClickListener(itemClickListener);
		//
		FlowView item = (FlowView) waterfallitem
				.findViewById(R.id.waterfall_image);
		item.setColumnIndex(columnIndex);
		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setViewHandler(this.handler);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowIndex(index);
		// param.setAssetManager(asset_manager);
		param.setContext(this);
		param.setFileName(filename);
		param.setItemWidth(item_width);
		param.setItemHeight(h);
		//
		item.setFlowTag(param);
		item.LoadImage();
		item.setTag(filename);
		item.setClickable(false);
		// item.setOnClickListener(itemClickListener);
		LayoutParams ll = (LinearLayout.LayoutParams) item.getLayoutParams();
		ll.height = h;
		item.layout(item.getLeft(), item.getTop(), item.getRight(),
				item.getBottom());
		waterfall_items.get(columnIndex).addView(waterfallitem);
		//
		RSSItem rssItem = mChannel.getItem(index);
		String[] counts = rssItem.getComments().split("\\|");
		TextView tvLove = (TextView) waterfallitem.findViewById(R.id.tv_love);
		tvLove.setText(counts[0]);
		TextView tvFav = (TextView) waterfallitem.findViewById(R.id.tv_fav);
		tvFav.setText(counts[1]);
		TextView tvComm = (TextView) waterfallitem.findViewById(R.id.tv_comm);
		tvComm.setText(counts[2]);

		//
		LayoutParams lpWaterfallitem = (LinearLayout.LayoutParams) waterfallitem
				.getLayoutParams();
		lpWaterfallitem.topMargin = 12;
		waterfallitem.layout(item.getLeft(), item.getTop(), item.getRight(),
				item.getBottom());
	}

	/**
	 * 得到最小索引
	 * 
	 * @param array
	 * @return
	 */
	private int GetMinIndex(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {

			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
	}

	/**
	 * 处理服务器返回数据
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear,
			boolean isRefresh, boolean isRemote) throws Exception {
		//
		switch (pageId) {
		case BaseContant.PAGE_CHANNEL:
			RSSChannel channel = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel != null && channel.getItems().size() > 0) {
				if (mChannel == null || mChannel.getItems().size() == 0) {
					mChannel = channel;
				} else {
					mChannel.setNextLink(channel.getNextLink());
					for (RSSItem item : channel.getItems())
						mChannel.addItem(item);
				}
				AddItemToContainer(current_page, page_count);
				this.requestImages(mChannel, false, BaseContant.PAGE_ICON);
			}
			break;
		case BaseContant.PAGE_ICON:
			for (LinearLayout ll : waterfall_items) {
				FlowView fv = (FlowView) ll.findViewWithTag(url);
				if (fv != null) {
					fv.LoadImage();
				}
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear, isRefresh, isRemote);
	}
}
