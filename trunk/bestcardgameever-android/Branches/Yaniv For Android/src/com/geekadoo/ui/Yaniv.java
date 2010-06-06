package com.geekadoo.ui;

/*
 * TODO: 
 * GameComponents need to be initialized from MainScreen activity, so it will not "die"
 * when Yaniv activity dies...
 * The separation we performed for init() is not sufficient.
 * we need to separate the graphics components from the hand object and then pass it an adapter upon 
 * initialization of graphic elements or keep the entire thing outside this activity and use it 
 */

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekadoo.R;
import com.geekadoo.R.id;
import com.geekadoo.db.YanivPersistenceAdapter;
import com.geekadoo.exceptions.InvalidDropException;
import com.geekadoo.exceptions.YanivPersistenceException;
import com.geekadoo.logic.GameData;
import com.geekadoo.logic.Hand;
import com.geekadoo.logic.PickupMethod;
import com.geekadoo.logic.PlayingCard;
import com.geekadoo.logic.Turn;

/**
 * The core activity of the application
 * 
 * @author Elad
 */

public class Yaniv extends Activity {

	private static final String YANIV_TAG = "Yaniv";

	private static final int MENU_VIEW_SCORES = 0;

	// //////////////////////
	// Deck - UI elements
	private ImageView deckImg;

	// Player 1 - UI elements
	private TextView p1Name;
	private ImageView[] p1Cards;
	private ImageView p1c1Img;
	private ImageView p1c2Img;
	private ImageView p1c3Img;
	private ImageView p1c4Img;
	private ImageView p1c5Img;
	private LinearLayout p1Container;

	// Opponent 1 - UI elements
	private TextView o1Name;
	private ImageView o1c1Img;
	private ImageView o1c2Img;
	private ImageView o1c3Img;
	private ImageView o1c4Img;
	private ImageView o1c5Img;
	private LinearLayout o1Container;
	private ImageView[] o1Cards;

	// Opponent 2 - UI elements
	private TextView o2Name;
	private ImageView o2c1Img;
	private ImageView o2c2Img;
	private ImageView o2c3Img;
	private ImageView o2c4Img;
	private ImageView o2c5Img;
	private LinearLayout o2Container;
	private ImageView[] o2Cards;

	// Opponent 3 - UI elements
	private TextView o3Name;
	private ImageView o3c1Img;
	private ImageView o3c2Img;
	private ImageView o3c3Img;
	private ImageView o3c4Img;
	private ImageView o3c5Img;
	private LinearLayout o3Container;
	private ImageView[] o3Cards;

	// Thrown Cards - UI elements
	private ImageView c1Thrown;
	private ImageView c2Thrown;
	private ImageView c3Thrown;
	private ImageView c4Thrown;
	private ImageView c5Thrown;
	private ImageView[] thrownCardsImgs;
	private View thrownCardsContainer;

	// Misc.
	private Button nextPlayerBtn;
	protected MyDialog uhOhDialog1;
	private Button yanivBtn;
	private TextView headingTv;
	// End UI Elements

	// Cheating - for debugging AI
	private boolean isCheating;
	private String cheatString;

	// TODO: remove this
	private Dialog d;
	private PlayingCard[] tempThrownArr;
	private GameData gameData;
	// TODO: end remove this
	// //////////////////////
	private boolean firstRun;

	private ScoresDialog scoresDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(YANIV_TAG, "onCreate");
		setContentView(R.layout.main);

		// Load state given by MainScreen
		switch ((GameData.STATES) getIntent().getExtras().get(GameData.STATE)) {
		case start:
			firstRun = true;
			break;
		case resume:
			firstRun = false;
			break;
		default:
			firstRun = true;
			break;
		}

		// TODO: change the loading of the GameData to be performed in
		// GameData - on first access

		gameData = (savedInstanceState == null) ? null
				: (GameData) savedInstanceState
						.getSerializable(YanivPersistenceAdapter.GAME_DATA);

		if (gameData == null) {
			gameData = GameData.getInstance(firstRun, getApplicationContext());
			initGraphicComponents();
		}
	}

	private void initGraphicComponents() {
		// TODO: remove this
		d = new Dialog(this);
		d.setTitle("hello");
		// TODO: end remove this

		headingTv = (TextView) findViewById(id.headingText);

		uhOhDialog1 = new MyDialog(this);
		scoresDialog = new ScoresDialog(this);

		// Player 1
		p1Name = (TextView) findViewById(id.p1Name);
		p1c1Img = (ImageView) findViewById(id.p1c1);
		p1c2Img = (ImageView) findViewById(id.p1c2);
		p1c3Img = (ImageView) findViewById(id.p1c3);
		p1c4Img = (ImageView) findViewById(id.p1c4);
		p1c5Img = (ImageView) findViewById(id.p1c5);

		p1Container = (LinearLayout) findViewById(id.bottom);

		p1Cards = new ImageView[] { p1c1Img, p1c2Img, p1c3Img, p1c4Img, p1c5Img };

		// Opponent 1
		o1Name = (TextView) findViewById(id.o1Name);
		o1c1Img = (ImageView) findViewById(id.o1c1);
		o1c2Img = (ImageView) findViewById(id.o1c2);
		o1c3Img = (ImageView) findViewById(id.o1c3);
		o1c4Img = (ImageView) findViewById(id.o1c4);
		o1c5Img = (ImageView) findViewById(id.o1c5);

		o1Container = (LinearLayout) findViewById(id.leftCol);

		o1Cards = new ImageView[] { o1c1Img, o1c2Img, o1c3Img, o1c4Img, o1c5Img };

		// Opponent 2
		o2Name = (TextView) findViewById(id.o2Name);
		o2c1Img = (ImageView) findViewById(id.o2c1);
		o2c2Img = (ImageView) findViewById(id.o2c2);
		o2c3Img = (ImageView) findViewById(id.o2c3);
		o2c4Img = (ImageView) findViewById(id.o2c4);
		o2c5Img = (ImageView) findViewById(id.o2c5);
		o2Container = (LinearLayout) findViewById(id.topRow);

		o2Cards = new ImageView[] { o2c1Img, o2c2Img, o2c3Img, o2c4Img, o2c5Img };

		// Opponent 3
		o3Name = (TextView) findViewById(id.o3Name);
		o3c1Img = (ImageView) findViewById(id.o3c1);
		o3c2Img = (ImageView) findViewById(id.o3c2);
		o3c3Img = (ImageView) findViewById(id.o3c3);
		o3c4Img = (ImageView) findViewById(id.o3c4);
		o3c5Img = (ImageView) findViewById(id.o3c5);

		o3Container = (LinearLayout) findViewById(id.rightCol);

		o3Cards = new ImageView[] { o3c1Img, o3c2Img, o3c3Img, o3c4Img, o3c5Img };

		// Thrown Cards
		c5Thrown = (ImageView) findViewById(id.card5);
		c4Thrown = (ImageView) findViewById(id.card4);
		c3Thrown = (ImageView) findViewById(id.card3);
		c2Thrown = (ImageView) findViewById(id.card2);
		c1Thrown = (ImageView) findViewById(id.card1);
		thrownCardsImgs = new ImageView[] { c1Thrown, c2Thrown, c3Thrown,
				c4Thrown, c5Thrown };
		thrownCardsContainer = findViewById(id.cardsThrown);

		// Deck
		deckImg = (ImageView) findViewById(id.deck);

		// Next Player Button
		nextPlayerBtn = (Button) findViewById(id.NextPlayer);
		// will be gone until required
		nextPlayerBtn.setVisibility(View.GONE);

		// Perform Yaniv Button
		yanivBtn = (Button) findViewById(id.PerformYaniv);
		// will be gone until required
		yanivBtn.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(YANIV_TAG, "OnPause");
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(YANIV_TAG, "OnResume");
		populateGameData();
		redrawAll();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		System.out.println("new config, orientation: " + newConfig.orientation);

		if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
			System.out.println("Keyboard open");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("Orientation Landscape");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.v(YANIV_TAG, "OnSaveInstanceState");
		saveState();
		outState.putSerializable(YanivPersistenceAdapter.GAME_DATA, gameData);

	}

	private void saveState() {
		try {
			gameData.save(getApplicationContext());
			Log.d(YANIV_TAG, "Yaniv saving state");
		} catch (YanivPersistenceException e) {
			// TODO: pop up a sorry box and report this problem... - could not
			// save
			Log.e(YANIV_TAG, "Yaniv could not save state");
		}
	}

	private void populateGameData() {
		gameData.getP1Hand().bindGraphicComponents(p1Container, p1Cards,
				p1Name.getText());
		gameData.getO1Hand().bindGraphicComponents(o1Container, o1Cards,
				o1Name.getText());
		gameData.getO2Hand().bindGraphicComponents(o2Container, o2Cards,
				o2Name.getText());
		gameData.getO3Hand().bindGraphicComponents(o3Container, o3Cards,
				o3Name.getText());
		// Cheating
		cheatString = new String();
	}

	protected void onStart() {
		super.onStart();
		Log.v(YANIV_TAG, "onStart");

		// Perform Yaniv Listener
		yanivBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				performYanivHandler();
			}

		});

		// Next player Listener
		nextPlayerBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				gameData.getTurn().next();
			}
		});

		// add a click listener to the deck
		deckImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deckClickHandler();
			}
		});

		// click listener for each card of player 1
		for (int cardIndex = 0; cardIndex < GameData.YANIV_NUM_CARDS; cardIndex++) {
			final ImageView card = p1Cards[cardIndex];
			final int finalCardIndex = cardIndex;
			card.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					p1CardsClickHandler(finalCardIndex);
				}
			});
		}

		// Thrown Cards Listener for pickup
		c1Thrown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				thrownCardsClickHandler();
			}
		});

		gameData.getTurn().addOnTurnEndedListener(
				new Turn.OnTurnStartedListener<Hand>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 5094578452334038986L;

					@Override
					public void onTurnStarted(Hand hand) {
						turnStartedHandler(hand);
					}
				});

	}

	// /////////////////////////////////////////////////////////////////////////////
	// ///////////////////Handlers//////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////
	/**
	 * what to do when one of the players cards are clicked mark as selected \
	 * unselected in gameplay and update the hand
	 * 
	 * @param cardIndex
	 *            the index of the selected card
	 */
	private void p1CardsClickHandler(final int cardIndex) {
		if (gameData.getTurn().peek().isHumanPlayer()) {
			gameData.getP1Hand().changeSelectionStateOnCard(cardIndex);
			redrawHand(gameData.getP1Hand());
		}// TODO:ELSE (show uhoh dialog)
	}

	/**
	 * I - 1 Drop cards that were previously marked RULE: first you drop, then
	 * you pickup
	 */
	private void dropCards() {
		if (gameData.getP1Hand().getCanDrop() == true) {
			try {
				tempThrownArr = gameData.getP1Hand().drop();

				// Rule: after drop you are not allowed to drop again
				gameData.getP1Hand().setCanDrop(false);
				// Rule: after drop you are allowed to pickup 
				gameData.getP1Hand().setCanPickup(true);
				// Note, we don't redraw the cards here, since we want the
				// player to see the cards he is throwing until they are down
			} catch (InvalidDropException e) {
				d.setTitle("You Can't Drop This!\nReason: " + e.getMessage());
				d.show();
			}
		} else {
			uhOhDialog1.show();
		}
	}

	/**
	 * II - 2 handler for the thrown cards click RULE: first you drop, then you
	 * pickup
	 */
	private void thrownCardsClickHandler() {
		// When the last thrown card is clicked it is picked up
		p1Pickup(PickupMethod.fromThrown);
	}

	/**
	 * II - 2 handler for the deck click RULE: first you drop, then you pickup
	 */

	private void deckClickHandler() {
		if (gameData.isFirstDeal()) {
			dealCards();
			gameData.setFirstDeal(false);
		} else {
			p1Pickup(PickupMethod.fromDeck);
		}
	}

	private void performYanivHandler() {
		gameData.getP1Hand().doYaniv();
		// TODO: Perform yaniv (call p1hand.doYaniv()), end game
		// Note: will only be visible when yaniv is possible
		// note 2: this ends the game, need to call score here

		// Show everyone's cards
		gameData.getO1Hand().setShouldCardsBeShown(true);
		gameData.getO2Hand().setShouldCardsBeShown(true);
		gameData.getO3Hand().setShouldCardsBeShown(true);
		// And count the cards for each player
		int p1Count = gameData.getP1Hand().sumCards();
		int o1Count = gameData.getO1Hand().sumCards();
		int o2Count = gameData.getO2Hand().sumCards();
		int o3Count = gameData.getO3Hand().sumCards();
		p1Name.setText(String.valueOf(p1Count));
		o1Name.setText(String.valueOf(o1Count));
		o2Name.setText(String.valueOf(o2Count));
		o3Name.setText(String.valueOf(o3Count));
		// redraw hands
		redrawHand(gameData.getP1Hand());
		redrawHand(gameData.getO1Hand());
		redrawHand(gameData.getO2Hand());
		redrawHand(gameData.getO3Hand());

		// Check if won or lost
		ArrayList<Hand> playersByPosition = gameData.getPlayersInOrder();
		Collections.sort(playersByPosition);
		if (playersByPosition.indexOf(gameData.getP1Hand()) == 0) {
			// P1 won

			d.setTitle("You Won!");
			d.show();
		} else {
			d.setTitle(playersByPosition.get(0).getPlayerName()
					+ " won! (you lost)");
			d.show();
		}
		// end game and add to scores
		// playersInOrder = winner is first and others after him clockwise
		// turn = new Turn<Hand>(playersInOrder);
	}

	private void turnStartedHandler(Hand hand) {
		if (!hand.isHumanPlayer()) {
			try {
				// First perform yaniv if strategy dictates it
				hand.doYaniv();

				switchCards(hand);
			} catch (InvalidDropException e) {
				d.setTitle(e.getMessage());
				d.show();// i know it's a bug, bug it should happen irl - no way
				// that the ai will do an invalid drop...
			}
		}
		
		// Reset buttons - so the Yaniv button will show up when its needed
		redrawButtons(hand);

		hand.getContainer().setBackgroundDrawable(null);
	}

	protected void dealCards() {

		int startOffset = 0;
		// 5 cards for each player
		for (int i = 0; i < GameData.YANIV_NUM_CARDS; i++) {

			for (Hand hand : gameData.getPlayersInOrder()) {
				hand.addCard(gameData.getDeck().popTopCard());

				// anim
				int[] handLocation = { 0, 0 };
				hand.getContainer().getLocationInWindow(handLocation);
				Log.e("COOR", "Hand: [" + handLocation[0] + ","
						+ handLocation[1] + "]");
				int[] deckLocation = { 0, 0 };
				deckImg.getLocationInWindow(deckLocation);
				Log.e("COOR", "Deck: [" + deckLocation[0] + ","
						+ deckLocation[1] + "]");
				// move from deck location to hand location
				TranslateAnimation dealCardAnimation = new TranslateAnimation(
						Animation.ABSOLUTE, deckLocation[0],
						Animation.ABSOLUTE, handLocation[0],
						Animation.ABSOLUTE, deckLocation[1],
						Animation.ABSOLUTE, handLocation[1]);
				dealCardAnimation.setDuration(100);
				dealCardAnimation.setStartOffset(100 * startOffset++);
				// end

				hand.getCardsViews()[i].startAnimation(dealCardAnimation);

				redrawHand(hand);
			}
		}

		// after dealing, put a card on the table for pick up
		gameData.getThrownCards().push(gameData.getDeck().popTopCard());
		redrawThrownCards();
	}

	private void p1Pickup(PickupMethod method) {
		// Usability fix:
		if (gameData.getP1Hand().hasSelectedCard()) {
			dropCards();
		}
		Hand currentHand = gameData.getTurn().peek();
		// First verify that it is the player's turn and that he is
		// eligible for pickup
		if (gameData.getP1Hand().canPickup()
				&& currentHand.isHumanPlayer() == true
				&& currentHand.getCanPickup() == true) {
			// fill virtual hand on first available place
			gameData.getP1Hand().pickup(method);

			// Rule: after pickup you are allowed to drop again (in the next turn)
			gameData.getP1Hand().setCanDrop(true);
			// Rule: after pickup you are not allowed to pickup again
			gameData.getP1Hand().setCanPickup(false);

			// mark the cards in the cards to drop as unselected so that if
			// somebody picks them up they will be unselected
			for (PlayingCard card : tempThrownArr) {
				if (card != null) {
					card.setSelected(false);
				}
			}
			// Update the thrown cards only after the pickup (RULE)
			gameData.getThrownCards().pushMulti(tempThrownArr);
			// redraw the thrown deck
			redrawThrownCards();
			// redraw the hand
			redrawHand(gameData.getP1Hand());
			// and advance a turn
			gameData.getTurn().next();
		} else {
			// show a dialog box saying 'cant pick up' or something
			uhOhDialog1.show();
		}
	}

	private void redrawHand(Hand hand) {
		ImageView[] cardView = hand.getCardsViews();
		View container = hand.getContainer();

		for (int i = 0; i < GameData.YANIV_NUM_CARDS; i++) {
			PlayingCard card = hand.getCardByLocation(i);
			if (card != null) {
				// Show Card
				cardView[i].setVisibility(View.VISIBLE);
				int resId;
				if (hand.shouldCardsBeShown()) {
					resId = card.getImageResourceId();
				} else {
					resId = R.drawable.back;
				}
				cardView[i].setImageResource(resId);

				// TODO: Disgusting patch, need to fix asap!!!
				if (hand == gameData.getP1Hand()) {
					// Show isSelected
					// when selected, move up 10 pixels
					boolean isSelected = hand.isCardSelected(i);
					((LinearLayout.LayoutParams) cardView[i].getLayoutParams()).bottomMargin = isSelected ? 10
							: 0;
				}
			} else {
				cardView[i].setVisibility(View.INVISIBLE);
			}
		}
		
		// Handle button display
//		redrawButtons();

		// // TODO: problematic - this only applies to p1:
		//
		// // and show the drop cards button if needed
		// // if this and no other cards are selected, don't show the button
		// if (gameData.getP1Hand().hasSelectedCard() == false) {
		// dropCardsBtn.setVisibility(View.GONE);
		// } else {
		// dropCardsBtn.setVisibility(View.VISIBLE);
		// }
		// // and the perform yaniv button...
		// if (gameData.getP1Hand().canYaniv()) {
		// yanivBtn.setVisibility(View.VISIBLE);
		// } else {
		// yanivBtn.setVisibility(View.GONE);
		// }
		//
		container.requestLayout();
	}

	private void redrawThrownCards() {
		PlayingCard[] cards = gameData.getThrownCards().peekTopFive();
		System.out.println("redrawThrownCards: " + cards);
		for (int thrownCardIndex = 0; thrownCardIndex < cards.length; thrownCardIndex++) {
			if (cards[thrownCardIndex] != null) {
				thrownCardsImgs[thrownCardIndex].setVisibility(View.VISIBLE);
				thrownCardsImgs[thrownCardIndex]
						.setImageResource(cards[thrownCardIndex]
								.getImageResourceId());
			} else {
				thrownCardsImgs[thrownCardIndex].setVisibility(View.INVISIBLE);
				thrownCardsImgs[thrownCardIndex]
						.setImageResource(R.drawable.back);

			}
		}
		thrownCardsContainer.postInvalidate();

	}

	/**
	 * Drops a card\s and picks up a card Drops a single card a set or a series
	 * to the table and then picks up either from the deck or from the table
	 * 
	 * @param hand
	 *            the hand performing the switch
	 */
	private void switchCards(Hand hand) throws InvalidDropException {
		// Drop
		tempThrownArr = hand.drop();
		// Mark the cards in the cards to drop as unselected so that if somebody
		// picks them up they will be unselected
		for (PlayingCard card : tempThrownArr) {
			if (card != null) {
				card.setSelected(false);
			}
		}

		int numCardsInDeckBeforePickup = gameData.getDeck().count();
		// Pickup
		hand.pickup(PickupMethod.decidePickup);
		int numCardsInDeckAfterPickup = gameData.getDeck().count();
		// Update the thrown cards only after the pickup (RULE)
		gameData.getThrownCards().pushMulti(tempThrownArr);

		// TODO: Drop animation here

		// And redraw the thrown deck
		redrawThrownCards();

		// and redraw it
		redrawHand(hand);

		AnimationSet growShrinkAnim = new AnimationSet(true);

		ScaleAnimation grow = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		grow.setDuration(750);
		grow.setRepeatCount(0);
		growShrinkAnim.addAnimation(grow);

		ScaleAnimation shrink = new ScaleAnimation(1.5f, 0.5f, 1.5f, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		shrink.setDuration(750);
		shrink.setRepeatCount(0);
		growShrinkAnim.addAnimation(shrink);

		for (int i = 0; i < tempThrownArr.length; i++) {
			// anim - v2
			thrownCardsImgs[i].startAnimation(growShrinkAnim);
			// 2v -mina
		}

		// TODO: end drop animation here
		headingTv
				.setText(hand.getPlayerName()
						+ " dropped "
						+ tempThrownArr.length
						+ " card"
						+ (tempThrownArr.length > 1 ? "s" : "")
						+ " and picked up from the "
						+ (numCardsInDeckBeforePickup == numCardsInDeckAfterPickup ? "thrown cards"
								: "deck"));
	}

	public void redrawAll() {
		ArrayList<Hand> handsList = gameData.getPlayersInOrder();
		for (Hand hand : handsList) {
			redrawHand(hand);
		}
		redrawThrownCards();
//		redrawButtons();
	}

	private void redrawButtons(Hand currentPlayer) {
		// Buttons should not be shown by default
		yanivBtn.setVisibility(View.GONE);
		nextPlayerBtn.setVisibility(View.GONE);

		// If this is the first deal, don't show any buttons
		if (!gameData.isFirstDeal()) {
			// If it's the human player's turn
			if (currentPlayer.isHumanPlayer()) {
				if (currentPlayer.canYaniv()) {
					// Show the Yaniv button
					yanivBtn.setVisibility(View.VISIBLE);
				}
			}
			else {
				// If it's not a human player turn, show Next Player button
				nextPlayerBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	// Adding option menu
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_VIEW_SCORES, 0, "View Scores");
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_VIEW_SCORES:
	        showScores();
	        return true;
	    }
	    return false;
	}
	
	private void showScores() {
		// TODO: get actual scores from game data
		scoresDialog.showScores(ScoresDialog.DELETE_ME);
	}
	// End of menu

	// Cheating section
	private void toggleCheat() {
		isCheating = !isCheating;
		if(isCheating){
			d.setTitle("Cheater!");
			d.show();
		}
			// Show everyone's cards
		gameData.getO1Hand().setShouldCardsBeShown(isCheating);
			redrawHand(gameData.getO1Hand());
			gameData.getO2Hand().setShouldCardsBeShown(isCheating);
			redrawHand(gameData.getO2Hand());
			gameData.getO3Hand().setShouldCardsBeShown(isCheating);
			redrawHand(gameData.getO3Hand());

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	switch (keyCode) {
	case KeyEvent.KEYCODE_E:
		cheatString = "e";
		break;
	case KeyEvent.KEYCODE_L:
		if(cheatString.equals("e")){
			cheatString = "el";
		}else{
			cheatString = "";
		}
		break;
	case KeyEvent.KEYCODE_A:
		if(cheatString.equals("el")){
			cheatString = "ela";
		}else{
			cheatString = "";
		}
		break;
	case KeyEvent.KEYCODE_D:
		if(cheatString.equals("ela")){
			toggleCheat();
		}
		cheatString = "";

		break;
	default:
		super.onKeyDown(keyCode, event);
		cheatString = "";
		break;
	}
		
		Log.d("CHEATING", "Key pressed:"+ (char)keyCode + ", cheatString is now:"+cheatString+", cheat is "+(isCheating? "en":"dis")+"abled.");
		return false;
	}
	// Cheating end
}