package cn.duome.fotoshare;

import org.gnu.stealthp.rsslib.RSSItem;

import android.os.Bundle;
import android.view.Display;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;
/**
 * 瀑布流展示页面-分享大厅-网络相册
 * @author dky
 *
 */
public class HomeActivity extends WaterFallBaseActivity {
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
	 * 重载Activity恢复时调用函数
	 */
	@Override
	public void onResume() {
		super.onResume();
		//
		String url = "http://xlb.jianrencun.com/fotoshare/foto_list.php";
		this.requestItem(url, BaseContant.PAGE_CHANNEL, true, true, null, true);
	}
	/**
	 * 重载本Activity销毁时调用的函数
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChannel != null) {
			for (RSSItem item : mChannel.getItems()) {
				BaseContent.getInstance(mContext).clearCacheBitmap(
						item.getThumbailUrl());
			}
		}
	}
}
