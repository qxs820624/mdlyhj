package com.hutuchong.filebrowser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.hutuchong.filebrowser.iconifiedlist.IconifiedText;
import com.hutuchong.filebrowser.iconifiedlist.IconifiedTextListAdapter;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;
import mobi.domore.mcdonalds.R;

public class FileBrowserActivity extends ListActivity {
	public static final String FILEBROWER_FLG = "flg";
	public static final String FILEBROWER_FOLDER = "folder";
	public static final int FILEBROWER_FLG_FILE = 0;
	public static final int FILEBROWER_FLG_FOLDER = 1;
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_FILENAME = "filename";

	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	protected static final int SUB_ACTIVITY_REQUEST_CODE = 1337;

	private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
	private List<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();
	private File currentDirectory = null;
	private int flg = FILEBROWER_FLG_FILE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Commond.userInfo.isFullscreen()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		this.setContentView(R.layout.filebrowser);

		//
		Intent i = this.getIntent();
		String defaultDirectory = null;
		if (i != null) {
			flg = i.getIntExtra(FILEBROWER_FLG, FILEBROWER_FLG_FILE);
			defaultDirectory = i.getStringExtra(FILEBROWER_FOLDER);
		}
		System.out.println("defaultDirectory:" + defaultDirectory);
		if (TextUtils.isEmpty(defaultDirectory))
			defaultDirectory = "/sdcard/";

		if (!defaultDirectory.endsWith("/")) {
			int len = defaultDirectory.lastIndexOf('/');
			defaultDirectory = defaultDirectory.substring(0, len + 1);
		}
		currentDirectory = new File(defaultDirectory);
		// 返回
		View btnBack = this.findViewById(R.id.btn_left);
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		// 向上
		View btnUp = this.findViewById(R.id.btn_right);
		btnUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				upOneLevel();
			}
		});
		// 确定
		View btnOk = this.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				// FileBrowserActivity.this.openFile(aDirectory);
				Intent i = getIntent();
				i.putExtra(ContantValue.EXTRA_FILENAME,
						currentDirectory.getAbsolutePath());
				setResult(RESULT_OK, i);
				finish();
			}
		});
		// 取消
		View btnCancel = this.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED, getIntent());
				finish();
			}
		});
		//
		browseTo(currentDirectory);
		this.setSelection(0);
	}

	/**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 */
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(final File aDirectory) {
		// On relative we display the full path in the title.
		if (this.displayMode == DISPLAYMODE.RELATIVE) {
			StringBuffer title = new StringBuffer();
			title = title.append(aDirectory.getAbsolutePath());
			this.setTitle(title.toString());
			TextView tvTitle = (TextView) this.findViewById(R.id.title_text);
			tvTitle.setText(title.toString());
			title = null;
		}
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			Intent i = getIntent();
			i.putExtra(EXTRA_FILENAME, aDirectory.getAbsolutePath());
			setResult(RESULT_OK, i);
			finish();
		}
	}

	private void openFile(File aFile) {
		try {
			Uri uri = Uri.parse("file://" + aFile.getAbsolutePath());
			Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(myIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		// Add the "." == "current directory"
		// this.directoryEntries.add(new IconifiedText(
		// getString(R.string.current_dir), getResources().getDrawable(
		// R.drawable.folder)));
		// // and the ".." == 'Up one level'
		// if (this.currentDirectory.getParent() != null)
		// this.directoryEntries.add(new IconifiedText(
		// getString(R.string.up_one_level), getResources()
		// .getDrawable(R.drawable.uponelevel)));

		setFileOrDirList(files, true);
		setFileOrDirList(files, false);

		// Collections.sort(this.directoryEntries);

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(this.directoryEntries);
		this.setListAdapter(itla);
	}

	private void setFileOrDirList(File[] files, boolean isDir) {
		Drawable currentIcon = null;
		if (files == null)
			return;
		for (File currentFile : files) {
			if (isDir) {
				if (currentFile.isDirectory()) {
					currentIcon = getResources().getDrawable(
							R.drawable.fb_folder);
				} else
					continue;
			} else {
				if (currentFile.isDirectory()) {
					continue;
				} else {
					if (flg == FILEBROWER_FLG_FOLDER)
						continue;

					String fileName = currentFile.getName();
					/*
					 * Determine the Icon to be used, depending on the
					 * FileEndings defined in: res/values/fileendings.xml.
					 */
					if (checkEndsWithInStringArray(fileName, getResources()
							.getStringArray(R.array.fileEndingImage))) {
						currentIcon = getResources().getDrawable(
								R.drawable.fb_image);
					} else if (checkEndsWithInStringArray(
							fileName,
							getResources().getStringArray(
									R.array.fileEndingWebText))) {
						currentIcon = getResources().getDrawable(
								R.drawable.fb_webtext);
					} else if (checkEndsWithInStringArray(
							fileName,
							getResources().getStringArray(
									R.array.fileEndingPackage))) {
						currentIcon = getResources().getDrawable(
								R.drawable.fb_packed);
					} else if (checkEndsWithInStringArray(
							fileName,
							getResources().getStringArray(
									R.array.fileEndingAudio))) {
						currentIcon = getResources().getDrawable(
								R.drawable.fb_audio);
					} else {
						currentIcon = getResources().getDrawable(
								R.drawable.fb_text);
					}
				}
			}
			String size = formatSize(currentFile.length());
			Date date = new Date(currentFile.lastModified());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String modifyDate = df.format(date);
			switch (this.displayMode) {
			case ABSOLUTE:
				this.directoryEntries.add(new IconifiedText(currentFile
						.getPath(), currentIcon, size, modifyDate));
				break;
			case RELATIVE:
				int currentPathStringLenght = this.currentDirectory
						.getAbsolutePath().length();
				this.directoryEntries.add(new IconifiedText(currentFile
						.getAbsolutePath().substring(currentPathStringLenght),
						currentIcon, size, modifyDate));
				break;
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectionRowID = position;
		String selectedFileString = this.directoryEntries.get(selectionRowID)
				.getText();
		if (selectedFileString.equals(getString(R.string.current_dir))) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals(getString(R.string.up_one_level))) {
			this.upOneLevel();
		} else {
			String path = "/";
			switch (this.displayMode) {
			case RELATIVE:
				path = this.currentDirectory.getAbsolutePath()
						+ this.directoryEntries.get(selectionRowID).getText();
				break;
			case ABSOLUTE:
				path = this.directoryEntries.get(selectionRowID).getText();
				break;
			}
			File clickedFile = new File(path);
			if (clickedFile != null) {
				if (clickedFile.isFile()) {
				} else {
					this.browseTo(clickedFile);
				}
			}
		}
	}

	/**
	 * Checks whether checkItsEnd ends with one of the Strings from fileEndings
	 */
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(RESULT_CANCELED, getIntent());
			finish();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public String formatSize(long size) {
		String suffix = null;
		long mod = 0;
		if (size >= 1000) {
			suffix = "K";
			size /= 1000;
			mod = size % 1000;
			if (size >= 1000) {
				suffix = "M";
				size /= 1000;
				mod = size % 1000;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}
		resultBuffer.append(".");
		resultBuffer.append(mod);

		if (suffix != null)
			resultBuffer.append(suffix);

		return resultBuffer.toString();
	}
}