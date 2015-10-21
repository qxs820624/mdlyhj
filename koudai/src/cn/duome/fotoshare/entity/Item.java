package cn.duome.fotoshare.entity;


public class Item {
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	String path;
	String thumb;

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

//	Bitmap bmpImage;
//
//	/**
//	 * 
//	 * @param filename
//	 */
//	public Bitmap getBmpImage() {
//		if (FotoCommond.bmpCache.contains(this)) {
//			if (bmpImage != null && !bmpImage.isRecycled())
//				return bmpImage;
//		}
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		// 获取这个图片的宽和高
//		options.outHeight = 240;
//		bmpImage = BitmapFactory.decodeFile(getPath(), options); // 此时返回bm为空
//		options.inJustDecodeBounds = false;
//		// 计算缩放比
//		int be = options.outHeight / 20;
//		if (be % 10 != 0)
//			be += 10;
//		be = be / 10;
//		if (be <= 0)
//			be = 1;
//		options.inSampleSize = be;
//		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
//		bmpImage = BitmapFactory.decodeFile(getPath(), options);
//		FotoCommond.bmpCache.add(0, this);
//		int i = FotoCommond.bmpCache.size() - 1;
//		for (; i >= 10; i--) {
//			Item tmp = FotoCommond.bmpCache.remove(i);
//			System.out.println("getBmpImage 3333333333");
//			if (tmp.bmpImage != null && !tmp.bmpImage.isRecycled()) {
//				tmp.bmpImage.recycle();
//				tmp.bmpImage = null;
//			}
//		}
//		return bmpImage;
//	}
}