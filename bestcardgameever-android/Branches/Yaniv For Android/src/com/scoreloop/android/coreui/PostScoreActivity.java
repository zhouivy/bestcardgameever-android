package com.scoreloop.android.coreui;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.scoreloop.client.android.core.controller.MessageController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.SocialProvider;
import com.geekadoo.R;

public class PostScoreActivity extends BaseActivity {

	private CheckBox facebookCheckbox;
	private Map<SocialProvider, CheckBox> map;
	private EditText messageEditText;
	private CheckBox myspaceCheckbox;
	private Button noButton;
	private Button postButton;
	private CheckBox twitterCheckbox;
	
	private final RequestControllerObserver messageControllerObserver = new RequestControllerObserver() {

		@Override
		public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
			showDialogSafe(DIALOG_ERROR_NETWORK);
			finish();
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
			showToast(getResources().getString(R.string.sl_post_success));
			finish();
		}
	};

	private void blockUI(final boolean flg) {
		facebookCheckbox.setEnabled(!flg);
		myspaceCheckbox.setEnabled(!flg);
		twitterCheckbox.setEnabled(!flg);
		postButton.setEnabled(!flg);
		noButton.setEnabled(!flg);
	}

	private void handleOnCheckedChanged(final boolean isChecked, final SocialProvider socialProvider) {
		if (isChecked) {
			blockUI(true);
			connectToSocialProvider(socialProvider);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_post_score);


		messageEditText = (EditText) findViewById(R.id.message_edittext);

		noButton = (Button) findViewById(R.id.cancel_button);
		noButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		postButton = (Button) findViewById(R.id.ok_button);
		postButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final MessageController messageController = new MessageController(messageControllerObserver);
				messageController.setTarget(ScoreloopManager.getScore());
				for (SocialProvider provider : map.keySet()) {
					CheckBox checkBox = map.get(provider);
					if (checkBox.isChecked()) {
						messageController.addReceiverWithUsers(provider, null);
					}
				}
				messageController.setText(messageEditText.getText().toString());
				if (messageController.isSubmitAllowed()) {
					messageController.submitMessage();
				}
			}
		});

		facebookCheckbox = (CheckBox) findViewById(R.id.facebook_checkbox);
		facebookCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				handleOnCheckedChanged(isChecked, facebookProvider);
			}
		});

		myspaceCheckbox = (CheckBox) findViewById(R.id.myspace_checkbox);
		myspaceCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				handleOnCheckedChanged(isChecked, myspaceProvider);
			}
		});

		twitterCheckbox = (CheckBox) findViewById(R.id.twitter_checkbox);
		twitterCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				handleOnCheckedChanged(isChecked, twitterProvider);
			}
		});

		map = new HashMap<SocialProvider, CheckBox>();
		map.put(facebookProvider, facebookCheckbox);
		map.put(myspaceProvider, myspaceCheckbox);
		map.put(twitterProvider, twitterCheckbox);
	}

	@Override
	void onSocialProviderConnectionStatusChange(final SocialProvider socialProvider) {
		blockUI(false);

		if (!socialProvider.isUserConnected(Session.getCurrentSession().getUser())) {
			map.get(socialProvider).setChecked(false);
		}
	}
}