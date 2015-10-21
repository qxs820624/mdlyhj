package cn.duome.fotoshare;

import java.net.URLEncoder;

import org.gnu.stealthp.rsslib.RSSChannel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.duome.fotoshare.utils.Constant;

import com.chuangyu.music.R;
import com.hutuchong.share.ShareKaixin001;
import com.hutuchong.share.ShareRenren;
import com.hutuchong.share.ShareWeibo;
import com.hutuchong.util.BaseCommond;
import com.hutuchong.util.BaseContant;
import com.hutuchong.util.BaseContent;
/**
 * 上传页面
 * @author dky
 *
 */
public class UploadActivity extends FotoBaseActivity {
	String mFileName;
	String mThumbName;
	String mTitle;
	Bitmap mBitmap;
	EditText etDesc;
	/**
	 * 重载Activity第一次启动时候调用的函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		init(getText(R.string.send_title).toString(), R.layout.upload, true);
		// 获取参数
		// Intent i = getIntent();
		// String extra = "picPath";
		// picPath = i.getStringExtra(extra);
		// 获取传递参数
		Intent i = this.getIntent();
		mFileName = i.getStringExtra(BaseContant.EXTRA_FILENAME);
		if (i.hasExtra(BaseContant.EXTRA_URL))
			mThumbName = i.getStringExtra(BaseContant.EXTRA_URL);
		if (i.hasExtra(BaseContant.EXTRA_TITLE))
			mTitle = i.getStringExtra(BaseContant.EXTRA_TITLE);
		// 预览图显示
		ImageView btnThumb = (ImageView) findViewById(R.id.iv_thumb);
		
		mBitmap = BaseContent.getInstance(mContext)
		.getCacheBitmap(mFileName,btnThumb.getWidth(),btnThumb.getHeight());
		btnThumb.setImageBitmap(mBitmap);
		// 发表按钮
		Button btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(clickListener);
		// 编辑按钮
		Button btnEdit = (Button) findViewById(R.id.btn_map);
		btnEdit.setOnClickListener(clickListener);
		// 新浪按钮
		Button btnSina = (Button) findViewById(R.id.btn_sina);
		btnSina.setOnClickListener(clickListener);
		// 腾讯按钮
		Button btntx = (Button) findViewById(R.id.btn_tx);
		btntx.setOnClickListener(clickListener);
		// 人人按钮
		Button btnrr = (Button) findViewById(R.id.btn_rr);
		btnrr.setOnClickListener(clickListener);
		// 开心按钮
		Button btnkx = (Button) findViewById(R.id.btn_kx);
		btnkx.setOnClickListener(clickListener);
		// 图片描述信息
		etDesc = (EditText) findViewById(R.id.et_desc);
	}

	/**
	 * 点击侦听器
	 */
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String title = TextUtils.isEmpty(mTitle) ? "" : mTitle;
			String content = TextUtils.isEmpty(etDesc.getText().toString()) ? ""
					: etDesc.getText().toString();
			String link = mFileName;
			String downUrl = "";
			String picUrl = mFileName;
			//
			switch (arg0.getId()) {
			case R.id.btn_send:
				String email = BaseCommond.userInfo.getEmail();
				if (TextUtils.isEmpty(email)) {
					Intent i = new Intent(mContext, UserLoginActivity.class);
					startActivityForResult(i, Constant.REQUEST_CODE_LOGIN);
					return;
				}
				String url = "http://xlb.jianrencun.com/fotoshare/foto_upload.php";
				String data = "title=" + URLEncoder.encode("test") + "&email="
						+ URLEncoder.encode(email);
				requestItem(url, BaseContant.PAGE_UPLOAD_FOTO, true,true, data,
						mFileName);
				break;
			case R.id.btn_map:
				Intent i = new Intent(mContext, EditActivity.class);
				i.putExtra(BaseContant.EXTRA_FILENAME, mFileName);
				i.putExtra(BaseContant.EXTRA_URL, mThumbName);
				startActivityForResult(i, Constant.REQUEST_CODE_EDIT);
				break;
			case R.id.btn_rr:// 人人分享
				ShareRenren.getInstance(UploadActivity.this).shareFeed(title,
						content, link, picUrl, getString(R.string.app_name),
						downUrl);
				break;
			case R.id.btn_kx:// 开心分享
				ShareKaixin001.getInstance(UploadActivity.this).share(title,
						content, link, picUrl, null, downUrl, mFileName);
				break;
			case R.id.btn_tx:// 腾讯微博分享
				break;
			case R.id.btn_sina:// 新浪微博分享
				ShareWeibo.getInstance(UploadActivity.this, content, mFileName);
				break;
			}
		}
	};

	/**
	 * 网络请求返回处理
	 */
	@Override
	public void loadUpdateThread(String url, int pageId, boolean isClear,
			boolean isRefresh, boolean isRemote) throws Exception {
		//
		switch (pageId) {
		case BaseContant.PAGE_UPLOAD_FOTO:
			RSSChannel channel = BaseContent.getInstance(mContext).getChannel(
					url);
			if (channel != null && !TextUtils.isEmpty(channel.getTitle())) {
				Toast.makeText(mContext, channel.getTitle(), Toast.LENGTH_LONG)
						.show();
				finish();
			} else {
				Toast.makeText(mContext, "发布失败", Toast.LENGTH_LONG).show();
			}
			break;
		}
		super.loadUpdateThread(url, pageId, isClear, isRefresh, isRemote);
	}
	/**
	 * 拍照后返回处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == Constant.REQUEST_CODE_EDIT) {
				ImageView btnThumb = (ImageView) findViewById(R.id.iv_thumb);
				mBitmap = BaseContent.getInstance(mContext)
				.getCacheBitmap(mFileName,200,200);
				btnThumb.setImageBitmap(mBitmap);
			}
		}
	}
}