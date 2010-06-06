package com.geekadoo.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.geekadoo.R;
import com.geekadoo.R.id;

public class ScoresDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final double cDialogHeightSizeFactor = 0.6;
	private static final double cDialogWidthSizeFactor = 0.85;
	private static final double cGridViewHeightSizeFactor = 0.83;
	private Button okButton;

	// TODO: delete this
	public static String[] DELETE_ME = { "sivan", "elad", "Iddo", "Rachel",
			"10", "20", "30", "40", "10", "20", "30", "40", "10", "20", "30",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"40", "10", "20", "30", "40", "10", "20", "30", "40", "10", "20","20",
			"30", "40", "10", "20", "30", "40", "11", "101", "181", "112" };

	public ScoresDialog(Context context) {
		super(context);

		this.setTitle(R.string.scoresDialogHeading);
		setContentView(R.layout.scores_view);

		okButton = (Button) findViewById(id.scoresDialogOkButton);
		okButton.setOnClickListener(this);
		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	public void showScores(String[] scores) {
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new TextAdapter(this.getContext(), scores));
		fixSize(gridview);
		show();
		
		Log.e("Sivan", ""+okButton.getMeasuredHeight());
	}

	/**
	 * Set the dialog size to be x% from the screen
	 * @param gridview 
	 */
	private void fixSize(GridView gridview) {
		// Get the screen dimensions
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		LinearLayout dialogMainView = (LinearLayout) findViewById(R.id.scoresDialogMainView);

		// Prepare the dimensions for the dialog box
		width *= cDialogWidthSizeFactor;
		height *= cDialogHeightSizeFactor;
		
		// Set the dialog dimensions 
		dialogMainView.getLayoutParams().height = height;
		dialogMainView.getLayoutParams().width = width;
		
		// Set the grid view to be smaller than dialog box - and leave space for the button
		gridview.getLayoutParams().height = (int)(height * cGridViewHeightSizeFactor);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scoresDialogOkButton:
			dismiss();
			// cancel();
			break;

		default:
			break;
		}
	}
}