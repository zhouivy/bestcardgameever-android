package com.geekadoo.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.geekadoo.R;
import com.geekadoo.R.id;

/**
 * A dialog box to show messages.
 * @author Elad
 */
public class MyDialog extends Dialog implements OnClickListener {
	private Button okButton;
	public MyDialog(Context context) {
		super(context);
		this.setTitle(R.string.msg1Heading);        
        setContentView(R.layout.dialog1);
    	
		okButton =  (Button)findViewById(id.dialogOkButton);
		okButton.setOnClickListener(this);
        // Have the system blur any windows behind this one.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialogOkButton:
			dismiss();
			//cancel();
			break;

		default:
			break;
		}
		
	}

}
