package com.hutuchong.app_game.assist;

import android.view.animation.Interpolator;

public class ElasticInterpolator implements Interpolator {
	private float amplitude;
	private float period;

	public ElasticInterpolator(float paramFloat1, float paramFloat2) {
		this.amplitude = paramFloat1;
		this.period = paramFloat2;
	}

	private float procedure(float paramFloat1, float paramFloat2,
			float paramFloat3) {
		float f1 = 0.0F;
		if (paramFloat1 == 0.0F) {
			f1 = 0.0F;
		} else {
			// :cond_0
			if (paramFloat1 < 1.0F) {
				// :cond_1
				if (paramFloat3 == 0.0F) {
					paramFloat3 = 0.3F;
				} else {
					float f2;
					// :cond_2
					if (paramFloat2 == 0.0F) {
						// :cond_3
						paramFloat2 = 1.0F;
						f2 = paramFloat3 / 4.0F;
						double d1 = paramFloat2;
						double d2 = -10.0F * paramFloat1;
						double d3 = Math.pow(2.0D, d2);
						double d4 = d1 * d3;
						double d5 = (paramFloat1 - f2) * 6.283185307179586D;
						double d6 = paramFloat3;
						double d7 = Math.sin(d5 / d6);
						f1 = (float) (d4 * d7 + 1.0D);
					} else {
						if (paramFloat2 >= 1.0F) {
							// :cond_4
							double d8 = paramFloat3 / 6.283185307179586D;
							double d9 = Math.asin(1.0F / paramFloat2);
							f2 = (float) (d8 * d9);
							//

							double d1 = paramFloat2;
							double d2 = -10.0F * paramFloat1;
							double d3 = Math.pow(2.0D, d2);
							double d4 = d1 * d3;
							double d5 = (paramFloat1 - f2) * 6.283185307179586D;
							double d6 = paramFloat3;
							double d7 = Math.sin(d5 / d6);
							f1 = (float) (d4 * d7 + 1.0D);
							//
							//System.out.println("f1=" + f1);
						}
					}
				}
			} else {
				f1 = 1.0F;
			}
		}
		
		return f1;
	}

	public float getInterpolation(float paramFloat) {
		float f1 = this.amplitude;
		float f2 = this.period;
		return procedure(paramFloat, f1, f2);
	}
}