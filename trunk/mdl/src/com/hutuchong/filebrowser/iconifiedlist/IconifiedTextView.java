/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $
 * 
 * Copyright 2007 Steven Osborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hutuchong.filebrowser.iconifiedlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mobi.domore.mcdonalds.R;

public class IconifiedTextView extends LinearLayout {

	private TextView mText;
	private ImageView mIcon;

	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);

		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(R.layout.file_row, this, true);

		mText = (TextView) this.findViewById(R.id.file_row_text);
		mIcon = (ImageView) this.findViewById(R.id.file_row_icon);
	}

	public void setText(String words) {
		mText.setText(words);
	}

	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}