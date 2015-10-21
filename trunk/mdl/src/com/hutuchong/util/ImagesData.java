package com.hutuchong.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImagesData {

	public static Bitmap selection_menu_bg;
	public static Bitmap selection_menu_bg_pressed;
	public static Bitmap selection_menu_divider;
	public static Bitmap selection_menu_bg_pressed_left;
	public static Bitmap selection_menu_bg_pressed_right;

	public static void initImages(Context c) {
		try {
			InputStream is = c.getAssets().open("images.bin");
			DataInputStream dis = new DataInputStream(is);
			//
			int len = dis.readInt();
			byte[] data = new byte[len];
			dis.read(data);
			selection_menu_bg = BitmapFactory.decodeByteArray(data, 0, len);
			data = null;
			//
			len = dis.readInt();
			data = new byte[len];
			dis.read(data);
			selection_menu_bg_pressed = BitmapFactory.decodeByteArray(data, 0,
					len);
			//
			len = dis.readInt();
			data = new byte[len];
			dis.read(data);
			selection_menu_divider = BitmapFactory
					.decodeByteArray(data, 0, len);
			//
			len = dis.readInt();
			data = new byte[len];
			dis.read(data);
			selection_menu_bg_pressed_left = BitmapFactory.decodeByteArray(
					data, 0, len);
			//
			len = dis.readInt();
			data = new byte[len];
			dis.read(data);
			selection_menu_bg_pressed_right = BitmapFactory.decodeByteArray(
					data, 0, len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}