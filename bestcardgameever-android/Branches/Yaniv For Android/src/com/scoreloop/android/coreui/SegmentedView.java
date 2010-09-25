package com.scoreloop.android.coreui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

// to keep it simple this is not meant to be a generic class 
class SegmentedView extends LinearLayout {

	private int selectedSegment;

	public SegmentedView(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	private void onInnerClick(final View view) {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) == view) {
				switchToSegment(i);
				performClick();
				return;
			}
		}
	}

	private void switchToSegment(final int segment) {
		selectedSegment = segment;
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setEnabled(i != selectedSegment);
		}
	}

	@Override
	protected void onFinishInflate() {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View view) {
					onInnerClick(view);
				}

			});
		}

		if (getChildCount() != 0) {
			switchToSegment(0);
		}
	}

	int getSelectedSegment() {
		return selectedSegment;
	}
}
