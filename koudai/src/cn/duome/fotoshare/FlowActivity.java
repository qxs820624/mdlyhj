package cn.duome.fotoshare;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import cn.duome.fotoshare.adapter.ImageAdapter;
import cn.duome.fotoshare.entity.Ablum;
import cn.duome.fotoshare.entity.Item;
import cn.duome.fotoshare.utils.Constant;
import cn.duome.fotoshare.utils.FotoCommond;
import cn.duome.fotoshare.view.GalleryFlow;
import cn.duome.fotoshare.view.ViewFlow;

import com.chuangyu.music.R;
import com.hutuchong.util.BaseContant;

/**
 * 图片流形式显示页面
 * 
 * @author dky
 * 
 */
public class FlowActivity extends FotoBaseActivity {
	ArrayList<ViewFlow> viewFlows = new ArrayList<ViewFlow>();
	int mAblumIndex;
	Ablum ablum;
	ImageAdapter imageAdapter;
	GalleryFlow cf;
	ProgressBar progressBar;

	/**
	 * Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		Intent i = this.getIntent();
		mAblumIndex = i.getIntExtra("index", 0);
		ablum = FotoCommond.albums.get(mAblumIndex);
		// 初始化
		init(ablum.getItem().getName(), R.layout.coverflow, true);
		//
		cf = (GalleryFlow) this.findViewById(R.id.coverflower);
		imageAdapter = new ImageAdapter(this, ablum);
		RequestImageTask dTask = new RequestImageTask();
		dTask.execute();
		progressBar = (ProgressBar) this.findViewById(R.id.progressImage);
		progressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 列表点击侦听器
	 */
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent i = new Intent(FlowActivity.this, UploadActivity.class);
			int index = (Integer) arg1.getTag();
			Item item = ablum.getItems().get(index);
			i.putExtra(BaseContant.EXTRA_FILENAME, item.getPath());
			i.putExtra(BaseContant.EXTRA_URL, item.getThumb());
			i.putExtra(BaseContant.EXTRA_TITLE, item.getName());
			overridePendingTransition(R.anim.gotonextin, R.anim.gotonextout);
			startActivityForResult(i, Constant.REQUEST_CODE_EDIT);
		}
	};

	// ////////////////////////////////
	// ////////////////////////////////
	// ////////////////////////////////
	class RequestImageTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPostExecute(String result) {
			cf.setAdapter(imageAdapter);// 自定义图片的填充方式
			cf.setSelection(ablum.getItems().size() / 2);
			cf.setAnimationDuration(1500);
			cf.setOnItemClickListener(listener);
			progressBar.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			imageAdapter.createReflectedImages();
			return null;
		}
	}
}