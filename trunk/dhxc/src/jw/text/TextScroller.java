package jw.text;

import java.util.Vector;
import android.graphics.*;
//import javax.microedition.lcdui.*;
//
//import com.sonyericsson.kx.KxMIDlet;
//import com.sonyericsson.kx.MainCanvas;

public class TextScroller {
	static final boolean DEBUG = false;
	static final int SCROLLTYPE_VERTICAL = 0;
	static final int SCROLLTYPE_HORIZONTAL = 1;
	static final int ACTION_NONE = -1;

	static boolean isChinese = true;
	int scrollerType;
	public String text;
	String[] textArray;
	int posX;
	int posY;
	int wid;
	int hei;
	Paint font;
	int color;
	int lineSpace;
	int autoMoveSpeed;
	int manualMoveSpeed;

	int fontHeight;
	public int lineHeight;
	int totalLength;
	int currentAction = ACTION_NONE;
	boolean isEnd = false;
	static int FPS = 100;
	// 信息绘制的左上点坐标
	int curPosX;
	int curPosY;
	// 上下滚动时，每行文本是否剧中显示
	public boolean isHcenter;

	static int nowWidth = 0;

	public TextScroller() {

	}

	public TextScroller(int scrollerType, String text, int posX, int posY,
			int wid, int hei, Paint font, int color, int lineSpace) {
		this.scrollerType = scrollerType;
		this.text = text;
		this.posX = posX;
		this.posY = posY;
		this.wid = wid;
		this.hei = hei;
		this.font = font;
		this.color = color;
		this.lineSpace = lineSpace;
		fontHeight = font.getFontMetricsInt(font.getFontMetricsInt());
		lineHeight = fontHeight + lineSpace;
		isEnd = false;
		curPosX = posX;
		curPosY = posY;

		autoMoveSpeed = 1;
		manualMoveSpeed = 4;
		isHcenter = false;
		init();
	}

	void init() {
		nowWidth = 0;
		parseText();
		// new TextThread().start();
	}

	public void setPosition(int x, int y){
		curPosX += (x - posX);
		curPosY += (y - posY);
		posX = x;
		posY = y;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public int getHeight(){
		return totalLength;
	}
	
	public int getViewHeight(){
		return (totalLength > hei)?hei:totalLength;
	}
	
	public void refreshText(String s){
		this.text = s;
		isEnd = false;
		curPosX = posX;
		curPosY = posY;
        init();
	}
	
//	public void paint(Graphics g) {
//		if (textArray != null) {
//			int clipX = g.getClipX();
//			int clipY = g.getClipY();
//			int clipWidth = g.getClipWidth();
//			int clipHeight = g.getClipHeight();
//			g.setColor(color);
//			if (DEBUG)
//				System.out.println("posX:" + posX + ",wid:" + wid);
//			g.setClip(posX, posY, wid, hei);
//			g.setFont(font);
//			if (scrollerType == SCROLLTYPE_VERTICAL)
//				paintV(g);
//			else if (scrollerType == SCROLLTYPE_HORIZONTAL)
//				paintH(g);
//
//			g.setClip(clipX, clipY, clipWidth, clipHeight);
//			if (DEBUG)
//				g.drawString("P:" + getPercentage(), 170, posY, 0);
//		}
//		update();
//	}

	
	
//	void paintV(Graphics g) {
//		for (int i = 0; i < textArray.length; i++) {
//			if (DEBUG)
//				System.out.println("No." + i + ",curPosX:" + curPosX
//						+ ",width:" + font.stringWidth(textArray[i])
//						+ ",content:" + textArray[i]);
//			int py = curPosY + i * lineHeight;
//			if (py + lineHeight < posY)
//				continue;
//			if (py > posY + hei)
//				break;
//			if (isHcenter)
//				g.drawString(textArray[i], curPosX + wid / 2, py,
//						Graphics.HCENTER | Graphics.TOP);
//			else
//				g.drawString(textArray[i], curPosX, py, Graphics.LEFT
//						| Graphics.TOP);
//		}
//	}

//	void paintH(Graphics g) {
//		for (int i = 0; i < textArray.length; i++) {
//			g.drawString(textArray[i], curPosX, curPosY, Graphics.LEFT
//					| Graphics.TOP);
//		}
//	}

//	void update() {
//		if (scrollerType == SCROLLTYPE_VERTICAL)
//			updateV();
//		else if (scrollerType == SCROLLTYPE_HORIZONTAL)
//			updateH();
//	}

	int juli = 0;
//	void updateV() {
//		int speed = 0;
//		juli += autoMoveSpeed;
//		if(juli > 10){
//			speed = juli/10;
//			juli = 0;
//		}
//		if (currentAction == ACTION_NONE) {
//			if (curPosY + totalLength > posY + hei)
//				curPosY -= speed;
//		} else if (currentAction == Canvas.UP) {
//			if (curPosY < posY)
//				curPosY += manualMoveSpeed;
//		} else if (currentAction == Canvas.DOWN) {
//			if (curPosY + totalLength > posY + hei)
//				curPosY -= manualMoveSpeed;
//		}
//	}

	void updateH() {
		// if (currentAction == ACTION_NONE) {
		// curPosX -= autoMoveSpeed;
		// }
		if (totalLength > wid && curPosX + totalLength > posX + wid)
			curPosX -= autoMoveSpeed;
		else if (totalLength > wid && curPosX + totalLength <= posX + wid)
			curPosX = posX;
	}

	public void keyPressed(int action) {
		currentAction = action;
	}

	public void keyReleased(int action) {
		currentAction = ACTION_NONE;
	}

	public void setPercentage(int percent) {

	}

	/**
	 * 返回显示过的信息占全部的比例 0 - 100
	 * 
	 * @return
	 */
	public int getPercentage() {
		int per = 0;
		if (scrollerType == SCROLLTYPE_VERTICAL) {
			if (totalLength <= hei)
				per = 100;
			else
				per = ((posY - curPosY + hei) * 100) / totalLength;
		} else if (scrollerType == SCROLLTYPE_HORIZONTAL) {
			if (totalLength <= wid)
				per = 100;
			else
				per = ((posX - curPosX + wid) * 100) / totalLength;
		}
		if (per > 100)
			per = 100;
		return per;
	}

	void parseText() {
		if (scrollerType == SCROLLTYPE_VERTICAL) {
			textArray = changeToArrayVertical(text, font, 0, wid);
			if (textArray != null)
				totalLength = lineHeight * textArray.length;
		} else if (scrollerType == SCROLLTYPE_HORIZONTAL) {
			textArray = changeToArrayHorizontal(text, font);
			if (textArray != null)
				totalLength = (int) font.measureText(textArray[0]);
		}
	}

	/**
	 * 水平滚动时字符串的解析
	 * 
	 * @param str
	 * @return
	 */
	String[] changeToArrayHorizontal(String str, Paint font) {
		String[] s = new String[1];
		s[0] = str;
		return s;
	}

	/**
	 * 垂直滚动时字符串的解析
	 * 
	 * @param str
	 * @param font
	 * @param w
	 * @return
	 */
	static final String[] changeToArrayVertical(String str, Paint font, int w0,
			int w) {
		if (!isChinese)
			return changeToArrayEn(str, font, w0, w);
		else
			return changeToArrayCn(str, font, w0, w);
	}

	public static final String[] changeToArrayCn(String str, Paint font, int w0,
			int width) {
		// int nowWidth = 0;
		nowWidth = w0;
		Vector result = new Vector();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			int chWidth = (int) font.measureText(new char[ch], 0, 1);

			if (ch == '\n' | ch == '|')
				chWidth = 0;
			if (ch == '\n' | ch == '|') {
				result.addElement(buffer.toString());
				buffer = new StringBuffer();
				nowWidth = 0;
				continue;
			}
			nowWidth += chWidth;
			if (nowWidth >= width) {// 大于指定的宽

				if (!isSign(ch)) {
					result.addElement(buffer.toString());
					buffer = new StringBuffer();
					nowWidth = chWidth;

				}
			}

			buffer.append(ch);
		}
		if (buffer.length() > 0)
			result.addElement(buffer.toString());

		String[] s = new String[result.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = (String) result.elementAt(i);
		}
		return s;
	}

	public static final String[] changeToArrayEn(String str, Paint font, int w0,
			int width) {
		// int nowWidth = 0;
		nowWidth = w0;
		Vector result = new Vector();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '\n') {
				result.addElement(buffer.toString());
				buffer = new StringBuffer();
				nowWidth = 0;
				continue;
			}

			int chWidth = (int) font.measureText(new char[ch], 0, 1);
			if (ch == '\n')
				chWidth = 0;

			nowWidth += chWidth;
			if (nowWidth >= width) {// 大于指定的宽

				char pre = str.charAt(i - 1);
				if (ch == ' ') {

					result.addElement(buffer.toString());
					buffer = new StringBuffer();
					nowWidth = 0;
					continue;
				} else if (isSign(pre)/*
										 * pre == ','||pre == '.'||pre ==
										 * '!'||pre == '?'
										 */) {
					result.addElement(buffer.toString());
					buffer = new StringBuffer();
					nowWidth = chWidth;
				} else {//
					boolean backSuccesfully = false;
					for (int b = buffer.length() - 1; b >= 0; b--) {

						char chB = buffer.charAt(b);
						if (chB == ' ') {
							i -= buffer.length() - b;
							buffer.delete(b, buffer.length());
							result.addElement(buffer.toString());
							buffer = new StringBuffer();
							nowWidth = 0;
							backSuccesfully = true;
							break;
						} else if (isSign(chB)/*
												 * chB == '.'||chB == ','||chB ==
												 * '?'||chB == ';'||chB == '!'
												 */) {

							i -= buffer.length() - b;
							buffer.delete(b + 1, buffer.length());
							result.addElement(buffer.toString());
							buffer = new StringBuffer();
							nowWidth = 0;
							backSuccesfully = true;
							break;
						}
					}
					if (!backSuccesfully) {
						buffer.append('-');
						result.addElement(buffer.toString());
						buffer = new StringBuffer();
						nowWidth = chWidth;
					} else
						continue;
				}
			}
			buffer.append(ch);
		}
		if (buffer.length() > 0)
			result.addElement(buffer.toString());
		String[] s = new String[result.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = (String) result.elementAt(i);
		}
		return s;
	}

	static final char[] SIGN_ALL = { ':', ',', '.', '?', '!', ' ', ';',// English
			'\uFF0C', '\u3002', '\uFF1F', '\uFF01', '\uFF1B', '\u3001' // Chinese
	};

	static final boolean isSign(char ch) {
		// for (int i = SIGN_ALL.length - 1; i >= 0; i--) {
		// if (ch == SIGN_ALL[i]) {
		// return true;
		// }
		// }
		return false;
	}

	// class TextThread extends Thread {
	// public void run() {
	// while (true) {
	// update();
	// try {
	// Thread.sleep(TextScroller.FPS);
	// } catch (InterruptedException e) {
	// }
	// }
	// }
	//	}

}
