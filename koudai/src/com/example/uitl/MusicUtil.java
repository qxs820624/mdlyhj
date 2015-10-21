package com.example.uitl;

public class MusicUtil {
	public static String formatTime(long msTime) {
		long hours = (msTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (msTime % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (msTime % (1000 * 60)) / 1000;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
