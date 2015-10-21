package cn.duome.fotoshare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.duome.fotoshare.adapter.AlbumItemAdapter;
import cn.duome.fotoshare.entity.Ablum;
import cn.duome.fotoshare.entity.Item;
import cn.duome.fotoshare.utils.FotoCommond;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseContent;

/**
 * 本地相册集页面
 * 
 * @author dky
 * 
 */
public class FotoShareActivity extends FotoBaseActivity {
	/**
	 * 网格视图
	 */
	GridView gridView;
	/**
	 * GridView展示适配器
	 */
	AlbumItemAdapter itemAdapter;

	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化
		init(getText(R.string.local_title).toString(), R.layout.fotoshare,
				false);
	}

	/**
	 * 图片加载进度句柄
	 */
	Handler mLoadHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				break;
			case 100:
				// 图片夹列表
				gridView = (GridView) FotoShareActivity.this
						.findViewById(R.id.gridview);
				gridView.setNumColumns(FotoCommond.sW / 177);
				itemAdapter = new AlbumItemAdapter(FotoShareActivity.this,
						FotoCommond.albums);
				gridView.setAdapter(itemAdapter);
				gridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent i = new Intent(FotoShareActivity.this,
								FlowActivity.class);
						i.putExtra("index", arg2);
						overridePendingTransition(R.anim.gotonextin,
								R.anim.gotonextout);
						startActivity(i);
					}
				});
			}
		}
	};

	/**
	 * 重载Activity恢复时调用函数
	 */
	@Override
	public void onResume() {
		super.onResume();
		// 加载图片列表
		new Thread() {
			@Override
			public void run() {
				mLoadHandler.sendEmptyMessage(0);
				FotoCommond.loadAblum();
				mLoadHandler.sendEmptyMessage(100);
			}
		}.start();
	}

	/**
	 * 重载本Activity销毁时调用的函数
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (itemAdapter != null) {
			int size = FotoCommond.albums.size();
			for (int i = 0; i < size; i++) {
				Ablum ablum = FotoCommond.albums.get(i);
				for (int j = 0; j < ablum.getItems().size(); j++) {
					Item item = ablum.getItems().get(j);
					BaseContent.getInstance(mContext).clearCacheBitmap(
							item.getPath());
					BaseContent.getInstance(mContext).clearCacheBitmap(
							item.getThumb());
				}
			}
		}
	}
}