package cn.coolworks.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PalettedImage {
	public static Bitmap getPalettedImage(byte[] data, int[] originalColors,
			int[] palettedColors) {
		byte[] tempData = new byte[data.length];
		System.arraycopy(data, 0, tempData, 0, data.length);
		Bitmap img = null;
		int[] parameter = { 0, 0, 0 };
		analyze(tempData, parameter);
		for (int i = 0; i < originalColors.length; i++) {
			replaceColor(tempData, parameter, originalColors[i],
					palettedColors[i]);
		}
		fillData(tempData, parameter);
		try {
			img = BitmapFactory.decodeByteArray(tempData, 0, data.length);
		} catch (Exception e) {
			System.out.println("getPalettedImage && " + e.toString());
		}
		return img;
	}

	private static void analyze(byte[] data, int[] para) {
		int offset = 8;
		int chunkLen = 0;
		while (data[offset + 4] != 0x50 || data[offset + 5] != 0x4c
				|| data[offset + 6] != 0x54 || data[offset + 7] != 0x45) {
			chunkLen = readInt(data, offset);
			offset += (4 + 4 + chunkLen + 4);
		}
		chunkLen = readInt(data, offset);
		para[2] = chunkLen / 3;
		para[0] = offset + 8;
		para[1] = offset + 8 + chunkLen;
	}

	private static int readInt(byte[] data, int offset) {
		return ((data[offset] & 0xFF) << 24)
				| ((data[offset + 1] & 0xFF) << 16)
				| ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
	}

	private static void replaceColor(byte[] data, int[] para, int oldColor,
			int newColor) {
		byte rr = (byte) ((oldColor >> 16) & 0xff);
		byte gg = (byte) ((oldColor >> 8) & 0xff);
		byte bb = (byte) (oldColor & 0xff);
		for (int i = 0, offset = para[0], temp = 0; i < para[2]; i++, offset += 3) {
			if (rr == data[offset] && gg == data[offset + 1]
					&& bb == data[offset + 2]) {
				data[offset] = (byte) ((newColor >> 16) & 0xff);
				data[offset + 1] = (byte) ((newColor >> 8) & 0xff);
				data[offset + 2] = (byte) (newColor & 0xff);
				break;
			}
		}
	}

	private static void fillData(byte[] data, int[] para) {
		int checksum = update_crc(data, para[0] - 4, para[2] * 3 + 4);
		data[para[1]] = (byte) ((checksum >> 24) & 0xff);
		data[para[1] + 1] = (byte) ((checksum >> 16) & 0xff);
		data[para[1] + 2] = (byte) ((checksum >> 8) & 0xff);
		data[para[1] + 3] = (byte) ((checksum) & 0xff);
	}

	private static int update_crc(byte[] buf, int off, int len) {
		int c = 0xffffffff;
		int n, k;
		int xx;
		int[] crc_table = new int[256];
		for (n = 0; n < 256; n++) {
			xx = n;
			for (k = 0; k < 8; k++) {
				if ((xx & 1) == 1) {
					xx = 0xedb88320 ^ (xx >>> 1);
				} else {
					xx = xx >>> 1;
				}
			}
			crc_table[n] = xx;
		}
		for (n = off; n < len + off; n++) {
			c = crc_table[(c ^ buf[n]) & 0xff] ^ (c >>> 8);
		}
		return (c ^ 0xffffffff);
	}
}