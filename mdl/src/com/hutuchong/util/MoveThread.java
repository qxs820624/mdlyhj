package com.hutuchong.util;


/**
 * 
 * @author 3gqa.com
 * 
 */
public class MoveThread extends Thread {
	private ViewHandle handle;
	private int offX = 0;
	private int offY = 0;
	private boolean isNeedAdjust = true;

	public MoveThread(ViewHandle view) {
		this.handle = view;
	}

	/**
	 * 
	 * @return
	 */
	public boolean startMove(int offx, int offy, boolean isNeedAdjust) {
		this.offX += offx;
		this.offY += offy;
		this.isNeedAdjust = isNeedAdjust;
		this.start();
		return true;
	}

	@Override
	public void run() {
		try {
			while (true) {
				moveoffset(offX, offY);
				if (offX == 0 && offY == 0) {
					if (isNeedAdjust)
						handle.adjustOffset();
					break;
				}
				Thread.sleep(50);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
	}

	/**
	 * 
	 * @param value
	 * @param isx
	 * @return
	 */
	private void moveoffset(int x, int y) {
		int tmpx = x;
		int offx = x / 3;
		if (tmpx >= -1 && tmpx <= 1) {
			offx = tmpx;
		}
		int tmpy = y;
		int offy = y / 3;
		if (tmpy >= -1 && tmpy <= 1) {
			offy = tmpy;
		}
		//
		handle.moveOffset(offx, offy);
		if (tmpx == -2)
			offx = -1;
		else if (tmpx == 2)
			offx = 1;
		tmpx -= offx;
		//
		if (tmpy == -2)
			offy = -1;
		else if (tmpy == 2)
			offy = 1;
		tmpy -= offy;
		//
		this.offX = tmpx;
		this.offY = tmpy;
	}
}
