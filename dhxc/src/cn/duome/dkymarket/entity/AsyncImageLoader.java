package cn.duome.dkymarket.entity;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,final ImageView imageView,
			final ImageDownloadCallBack imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded(imageUrl,(Drawable) message.obj, imageView);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = downloadDrawable(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

//	public static Drawable loadImageFromUrl(String url) {
//		// ...
//	}

	public interface ImageDownloadCallBack{
		public void imageLoaded(String imgURL,Drawable downloadedDrawable,ImageView imageView);
	}

	// œ¬‘ÿÕº∆¨
	private Drawable downloadDrawable(String imgURL) {
		Drawable drawable = null;
		try {
			URL url = new URL(imgURL);
			drawable = Drawable.createFromStream(url.openStream(), "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
}
