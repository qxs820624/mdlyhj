package jw.http;

import java.io.File;
import android.text.TextUtils;

public class UploadData {
	public int nType; // 0 -- Get
	// 1 -- Post application/x-www-form-urlencoded
	// 2 -- Post multipart/form-data data in data array
	// 3 -- Download file
	// 4 -- stream

	public byte[] data; // PostData
	public String strType; // Content Type
	public String strText; // File Description or if nType == 1, data
	public String strFilename; // File Name
	public String responseFileName;
	public String strBoundry;
	// public String strLocalPath; // when nType == 3 directly save file to the
	// directory
	// public InputStream is;
	// public OutputStream os;
	public byte[] headerData;
	public byte[] endData;
	public int contentLength;
	public int fileLen;
	public Object tag;
	public ProgressListener progressListener;

	/**
	 * 
	 * @return
	 */
	private static String generateBoundry() {
		java.util.Random rand = new java.util.Random();
		int i = rand.nextInt();
		return "---------------------" + Integer.toHexString(i);
	}

	/**
	 * 
	 * @param upload
	 * @return
	 */
	public void processData() {

		if (nType == 0)
			data = new byte[0];
		else if (nType == 1) {
			String str = strText;// urlEncode(upload.strText);
			strType = "application/x-www-form-urlencoded";
			data = str.getBytes();
			contentLength = data.length;
		} else if (nType == 3) {
			data = new byte[0];
		} else if (nType == 4) {
		} else if (nType == 2) {
			strBoundry = generateBoundry();

			StringBuffer sbHeader = new StringBuffer();
			//
			if (!TextUtils.isEmpty(strText)) {
				String[] formdata = strText.split("&");
				for (int i = 0; i < formdata.length; i++) {
					String[] vd = formdata[i].split("=");
					if (vd.length == 2) {
						// Add title part
						sbHeader.append("--" + strBoundry);
						sbHeader.append("\r\n");
						sbHeader
								.append("Content-Disposition: form-data; name=\""
										+ vd[0] + "\"");
						sbHeader.append("\r\n\r\n");
						sbHeader.append(vd[1]);
						sbHeader.append("\r\n");
					}
				}
				endData = ("--" + strBoundry + "--\r\n").getBytes();
			}
			// Add file data part
			if (!TextUtils.isEmpty(strFilename)) {
				sbHeader.append("--" + strBoundry);
				sbHeader.append("\r\n");

				sbHeader
						.append("Content-Disposition: form-data; name=\"file\"; filename=\"");
				sbHeader.append("aa.jpg"/*strFilename*/);
				sbHeader.append("\"\r\n");

				sbHeader.append("Content-Type: image/pjpeg");
				sbHeader.append("\r\n\r\n");
				endData = ("\r\n--" + strBoundry + "--\r\n").getBytes();
			}

			headerData = sbHeader.toString().getBytes();
//			Debug.d(sbHeader.toString());
//			Debug.d(new String(endData));
//			Debug.d("headerData.length:" + headerData.length);
//			Debug.d("endData.length:" + endData.length);
			//
			contentLength = headerData.length;
			if (!TextUtils.isEmpty(strFilename)) {
				File file = new File(strFilename);
				fileLen = (int) file.length();
				contentLength += fileLen;
				file = null;
			}
			contentLength += endData.length;
//			Debug.d("contentLength:" + contentLength);
		}
	}
}
