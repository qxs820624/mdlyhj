package com.hutuchong.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgressInfoParcelable implements Parcelable {

	private ProgressInfo info;

	public ProgressInfoParcelable(Parcel source) {

		info = (ProgressInfo) source.readValue(ProgressInfo.class
				.getClassLoader());

	}

	public ProgressInfoParcelable(ProgressInfo info) {

		this.info = info;
	}

	public int describeContents() {

		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {

		dest.writeValue(info);
	}

	public static final Parcelable.Creator<ProgressInfoParcelable> CREATOR = new Parcelable.Creator<ProgressInfoParcelable>() {

		public ProgressInfoParcelable createFromParcel(Parcel source) {

			return new ProgressInfoParcelable(source);
		}

		public ProgressInfoParcelable[] newArray(int size) {

			// return new AppParcelable[size];
			throw new UnsupportedOperationException();
		}

	};

	public ProgressInfo getInfo() {

		return info;
	}
}
