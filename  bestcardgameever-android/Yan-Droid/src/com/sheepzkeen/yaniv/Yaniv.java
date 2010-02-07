package com.sheepzkeen.yaniv;

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
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sheepzkeen.yaniv.R.id;

/**
 * The core activity of the application
 * @author Elad
 */

public class Yaniv extends Activity {
	
	public static final int YANIV_NUM_CARDS = 5;
	public static final int START_GAME = 1000;
	public static final String STATE = "state";
	public static final int RESUME_GAME = 2000;
	public static enum STATES{start,resume,end};
	public static final int defaultStartingPlayer = 0;
	public static final String GAMEDATA_PARAMNAME = "gameData";
	
	//Deck - UI elements
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

	// Opponent 1  - UI elements
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

	//Thrown Cards - UI elements
	private ImageView c1Thrown;
	private ImageView c2Thrown;
	private ImageView c3Thrown;
	private ImageView c4Thrown;
	private ImageView c5Thrown;
	private ImageView[] thrownCardsImgs;
	private View thrownCardsContainer;

	//Turn
	private ArrayList<Hand> playersInOrder;

	//Misc.
	private Button dropCardsBtn;
	private Button nextPlayerBtn;
	private boolean firstDeal;
	protected MyDialog uhOhDialog1;
	private Button yanivBtn;
	private boolean firstRun;
	private boolean isCheating;
	private String cheatString;
	private TextView headingTv;
	//TODO: remove this
	private Dialog d;
	private PlayingCard[] tempThrownArr;
	private GameData gameData;
	//TODO: end remove this
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("PUKI","onCreate");

		// Load game data that was passed by MainScreen
		if(getIntent().getExtras() != null) {
			gameData = (GameData)getIntent().getExtras().getSerializable(GAMEDATA_PARAMNAME);
		}
		else {
			Log.e("PUKI", "getIntent().getExtras() == null");
		}
		
		//	firstRun = true;
		setContentView(R.layout.main);
		
		// Load state given by MainScreen
		switch ((STATES)getIntent().getExtras().get(STATE)) {
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
		
		// Transferring game data to MainScreen 
		Bundle b = new Bundle();
		b.putInt("int", 5);
		b.putSerializable(GAMEDATA_PARAMNAME, gameData);
		Intent i = new Intent();
		i.putExtras(b);
		setResult(0, i);
		
		init(firstRun);
		
			  //////////////////
			 //TESTING/////////
			//////////////////
		
		//TestDropCardsAlgorithm tests = new TestDropCardsAlgorithm(this);
		//tests.testCardsCombination();

			  //////////////////
			 //END TESTING/////
			//////////////////
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("PUKI","onDestroy");
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("PUKI","onSaveInstanceState");
	}

	protected void onStart() {
		super.onStart();
		Log.d("PUKI","onStart");

		if (firstRun) {
//			init(false);
			firstRun = false;
		}

		// Perform Yaniv Listener
		yanivBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				performYanivHandler();
			}
			
		});
		
		// Drop Cards Listener
		dropCardsBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v){
				dropCardsClickHandler();
			}
		}
		);		

		// Next player Listener
		nextPlayerBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v){
				gameData.getTurn().next();
			}
		}
		);		

		// add a click listener to the deck
		deckImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deckClickHandler();
			}
		});

		// click listener for each card of player 1 
		for (int cardIndex = 0; cardIndex < Yaniv.YANIV_NUM_CARDS; cardIndex++) {
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
		

		gameData.getTurn().addOnTurnEndedListener(new Turn.OnTurnEndedListener<Hand>(){

			
			@Override
			public void onTurnEnded(Hand hand){
				turnEndedHandler(hand);
			}
		});
		//testing
//		deckClickHandler();
//		p1CardsClickHandler(0);
//		deckClickHandler();
	}


	private void initGraphicComponents() {
		//TODO: remove this
		d = new Dialog(this);
		d.setTitle("hello");
		//TODO: end remove this

		headingTv = (TextView) findViewById(id.headingText);
		
		uhOhDialog1 = new MyDialog(this);

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
		thrownCardsImgs = new ImageView[] {c1Thrown,c2Thrown,c3Thrown,c4Thrown,c5Thrown};
		thrownCardsContainer = findViewById(id.cardsThrown);

		// Deck
		deckImg = (ImageView) findViewById(id.deck);

		// Drop Cards Button
		dropCardsBtn = (Button)findViewById(id.DropCards);
		// will be gone until required
		dropCardsBtn.setVisibility(View.GONE);
		
		// Next Player Button
		nextPlayerBtn = (Button)findViewById(id.NextPlayer);
		// will be gone until required
		nextPlayerBtn.setVisibility(View.GONE);
		
		// Perform Yaniv Button
		yanivBtn = (Button)findViewById(id.PerformYaniv);
		// will be gone until required
		yanivBtn.setVisibility(View.GONE);
	}

	private void initGameComponents() {
		firstDeal = true;
		cheatString = new String();
		
		// array of order of players in the beginning of the game (p1 is first) 
		playersInOrder = new ArrayList<Hand>();
		PlayerHand p1Hand = new PlayerHand(p1Container,p1Cards, p1Name);
		playersInOrder.add(p1Hand);
		OpponentHand o1Hand = new OpponentHand(new BasicYanivStrategy(), o1Container,o1Cards, o1Name);
		playersInOrder.add(o1Hand);
		OpponentHand o2Hand = new OpponentHand(new BasicYanivStrategy(), o2Container,o2Cards,o2Name);
		playersInOrder.add(o2Hand);
		OpponentHand o3Hand = new OpponentHand(new BasicYanivStrategy(), o3Container,o3Cards,o3Name);
		playersInOrder.add(o3Hand);

		//this.gameData = GameData.getInstance();
		gameData.init(p1Hand,
				o1Hand,
				o2Hand,
				o3Hand,
				new ThrownCards(),
				new SingleDeck(),
				new Turn<Hand>(playersInOrder, defaultStartingPlayer),
				playersInOrder);
		gameData.setGameInProgress(true);
	}

	/**
	 * Initializes all the components
	 * @param isGameCreation 
	 */
	private void init(boolean isGameCreation) {
		initGraphicComponents();
		if (isGameCreation) {
			initGameComponents();
		}
	}

	protected void dealCards() {

		int startOffset = 0;
		// 5 cards for each player
		for (int i = 0; i < Yaniv.YANIV_NUM_CARDS; i++) {
			
			for (Hand hand : playersInOrder) {
				hand.addCard(gameData.getDeck().popTopCard());
				
				//anim
				int[] handLocation = {0,0};
				hand.getContainer().getLocationInWindow(handLocation);
				Log.e("COOR","Hand: ["+handLocation[0]+","+handLocation[1]+"]");
				int[] deckLocation = {0,0};
				deckImg.getLocationInWindow(deckLocation);
				Log.e("COOR","Deck: ["+deckLocation[0]+","+deckLocation[1]+"]");
				//move from deck location to hand location
				TranslateAnimation dealCardAnimation = new TranslateAnimation(Animation.ABSOLUTE,deckLocation[0],
						Animation.ABSOLUTE,handLocation[0],
						Animation.ABSOLUTE,deckLocation[1],
						Animation.ABSOLUTE,handLocation[1]);
				dealCardAnimation.setDuration(100); 
				dealCardAnimation.setStartOffset(100*startOffset++);
				//try to change the z-order
//				hand.getCardsViews()[i].bringToFront();
				//end 
				
				hand.getCardsViews()[i].startAnimation(dealCardAnimation);
				
				//mina
//				//Anim 2
//				float fromAlpha = 0;
//				float toAlpha = 1;
//				AlphaAnimation aanim = new AlphaAnimation(fromAlpha ,toAlpha);
//				//aanim.setInterpolator(new DecelerateInterpolator(1.0f));
//				int durationInMilis = 100;
//				aanim.setStartOffset(100*startOffset++);
//				aanim.setDuration(durationInMilis);
//				hand.getCardsViews()[i].startAnimation(aanim);
//				//minA 2
				
				redrawHand(hand);
			}
		}
		
		// after dealing, put a card on the table for pick up
		gameData.getThrownCards().push(gameData.getDeck().popTopCard());
		redrawThrownCards();
	}

	private void redrawHand(Hand hand) {
		ImageView[] cardView = hand.getCardsViews();
		View container = hand.getContainer();
		
		for (int i = 0; i < Yaniv.YANIV_NUM_CARDS; i++) {
				PlayingCard card = hand.getCardByLocation(i);
				if (card != null){
					//Show Card
					cardView[i].setVisibility(View.VISIBLE);
					int resId;
					if (hand.shouldCardsBeShown()) {
						resId = card.getImageResourceId();
					}else{
						resId = R.drawable.back;
					}
					cardView[i].setImageResource(resId);

					//TODO: Disgusting patch, need to fix asap!!!
					if (hand == gameData.getP1Hand()){
						//Show isSelected
						//when selected, move up 10 pixels
						boolean isSelected = hand.isCardSelected(i);
						((LinearLayout.LayoutParams) cardView[i].getLayoutParams()).bottomMargin =
							isSelected? 10 : 0 ; 
					}
				}else{
					cardView[i].setVisibility(View.INVISIBLE);
				}
		}
		
		//TODO: problematic - this only applies to p1:
		
		//and show the drop cards button if needed
		//if this and no other cards are selected, don't show the button
		if(gameData.getP1Hand().hasSelectedCard() == false){
			dropCardsBtn.setVisibility(View.GONE);
		}else{
			dropCardsBtn.setVisibility(View.VISIBLE);
		}
		//and the perform yaniv button...
		if(gameData.getP1Hand().canYaniv()){
			yanivBtn.setVisibility(View.VISIBLE);
		}else
		{
			yanivBtn.setVisibility(View.GONE);
		}
		
		container.requestLayout();
	}
	
	private void redrawThrownCards() {
		PlayingCard[] cards = gameData.getThrownCards().peekTopFive();
		System.out.println("redrawThrownCards: " +cards );
		for (int thrownCardIndex = 0; thrownCardIndex < cards.length; thrownCardIndex++) {
			if(cards[thrownCardIndex] != null){
				thrownCardsImgs[thrownCardIndex].setVisibility(View.VISIBLE);
				thrownCardsImgs[thrownCardIndex].
				setImageResource(cards[thrownCardIndex].getImageResourceId());
			}else{
				thrownCardsImgs[thrownCardIndex].setVisibility(View.INVISIBLE);
				thrownCardsImgs[thrownCardIndex].
				setImageResource(R.drawable.back);
				
			}
		}
		thrownCardsContainer.postInvalidate();

	}
	


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		System.out.println("new config, orientation: " + newConfig.orientation );
		
		if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES){
			System.out.println("Keyboard open");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			System.out.println("Orientation Landscape");
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("PUKI", "OnPause");

	}
	@Override
	protected void onResume() {
		super.onResume();
		//TODO: restore everything
		Log.d("PUKI", "OnResume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("PUKI", "OnStop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("PUKI", "OnRestart");
	}

	
///////////////////////////////////////////////////////////////////////////////
/////////////////////Handlers//////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * 	what to do when one of the players cards are clicked
	 *  mark as selected \ unselected in gameplay and update the hand
	 *
	 * @param cardIndex the index of the selected card
	 */
	private void p1CardsClickHandler(final int cardIndex) {
		gameData.getP1Hand().changeSelectionStateOnCard(cardIndex);
		

		
		redrawHand(gameData.getP1Hand());

		
	}

	/**
	 * I - 1
	 * Drop cards that were previously marked
	 * RULE: first you drop, then you pickup
	 */
	private void dropCardsClickHandler() {
		if (gameData.getP1Hand().getCanDrop() == true){
			try {
				tempThrownArr = gameData.getP1Hand().drop();
			
			// Rule: after drop you are not allowed to drop again
				gameData.getP1Hand().setCanDrop(false);
			// Rule: after drop you are allowed to pickup again 
				gameData.getP1Hand().setCanPickup(true);
			
			dropCardsBtn.setVisibility(View.GONE);
			//Note, we don't redraw the cards here, since we want the player to see the cards he is throwing until they are down
			
			} catch (InvalidDropException e) {
				d.setTitle("You Can't Drop This!\nReason: " + e.getMessage());
				d.show();
				
			}
		}else{
			uhOhDialog1.show();
		}
	}

/**
 * II - 2
 * handler for the thrown cards click
 * RULE: first you drop, then you pickup
 */
private void thrownCardsClickHandler() {
	// When the last thrown card is clicked it is picked up
	p1Pickup(PickupMethod.fromThrown);
}

/**
 * II - 2
 * handler for the deck click
 * RULE: first you drop, then you pickup
 */

private void deckClickHandler() {
	if (firstDeal) {
		dealCards();
		firstDeal = false;
	} else {
		p1Pickup(PickupMethod.fromDeck);
	}
}

private void p1Pickup(PickupMethod method){

	//usability fix:
	if(gameData.getP1Hand().hasSelectedCard()){
		dropCardsClickHandler();
	}
	Hand currentHand = gameData.getTurn().peek();
	// First verify that it is the player's turn and that he is
	// eligible for pickup
	if(gameData.getP1Hand().canPickup() && currentHand.isAwaitingInput() == true && currentHand .getCanPickup() == true){
		// fill virtual hand on first available place
		gameData.getP1Hand().pickup(method);

		// Rule: after pickup you are allowed to drop again
		gameData.getP1Hand().setCanDrop(true);
		// Rule: after pickup you are not allowed to pickup again 
		gameData.getP1Hand().setCanPickup(false);

		//mark the cards in the cards to drop as unselected so that if somebody picks them up they will be unselected
		for (PlayingCard card : tempThrownArr) {
			if (card != null){
				card.setSelected(false);
			}
		}
		// Update the thrown cards only after the pickup (RULE)
		gameData.getThrownCards().pushMulti(tempThrownArr);
		// redraw the thrown deck
		redrawThrownCards();
		// redraw the hand
		redrawHand(gameData.getP1Hand());
		//and advance a turn
		gameData.getTurn().next();
	}else{
		//show a dialog box saying 'cant pick up' or something
		uhOhDialog1.show();
	}
}
/**
 * Drops a card\s and picks up a card
 * Drops a single card a set or a series to the table and then picks up either from the deck or from the table
 * @param hand the hand performing the switch
 */
private void switchCards(Hand hand) throws InvalidDropException {
	//Drop
	tempThrownArr = hand.drop();
	//mark the cards in the cards to drop as unselected so that if somebody picks them up they will be unselected
	for (PlayingCard card : tempThrownArr) {
		if (card != null){
			card.setSelected(false);
		}
	}
	int numCardsInDeckBeforePickup = gameData.deck.count();
	//Pickup
	hand.pickup(PickupMethod.decidePickup);
	int numCardsInDeckAfterPickup = gameData.deck.count();
	// Update the thrown cards only after the pickup (RULE)
	gameData.getThrownCards().pushMulti(tempThrownArr);

	//TODO: Drop animation here
//	int startOffset = 0;
//	int thrownCardsSize  = gameData.getThrownCards().count();
//	int thrownCardsSizeUpTo5 = thrownCardsSize > 5? 5:thrownCardsSize;
	
	// And redraw the thrown deck
	redrawThrownCards();
	
	//and redraw it
	redrawHand(hand);

	AnimationSet growShrinkAnim = new AnimationSet(true);
/////////
//	float fromAlpha = 0;
//	float toAlpha = 1;
//	AlphaAnimation aanim = new AlphaAnimation(fromAlpha ,toAlpha);
//	//aanim.setInterpolator(new DecelerateInterpolator(1.0f));
//	int durationInMilis = 1500;
//	aanim.setDuration(durationInMilis);
//	growShrinkAnim.addAnimation(aanim);
////////
	
	
 	ScaleAnimation grow = new ScaleAnimation(
  		       0.5f, 1.5f, 0.5f, 1.5f, 
  		       ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
  		       ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
 	grow.setDuration(750);
 	grow.setRepeatCount(0);	       
	growShrinkAnim.addAnimation(grow);

	ScaleAnimation shrink = new ScaleAnimation(
		       1.5f, 0.5f, 1.5f, 0.5f, 
		       ScaleAnimation.RELATIVE_TO_SELF, 0.5f, 
		       ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
	shrink.setDuration(750);
	shrink.setRepeatCount(0);	       
	growShrinkAnim.addAnimation(shrink);
		

	for(int i = 0; i<tempThrownArr.length;i++){
		//anim - v2

		thrownCardsImgs[i].startAnimation(growShrinkAnim);
		
		//2v -mina

	}

	
//	//Anim 3
//	float fromAlpha = 0;
//	float toAlpha = 1;
//	AlphaAnimation aanim = new AlphaAnimation(fromAlpha ,toAlpha);
//	//aanim.setInterpolator(new DecelerateInterpolator(1.0f));
//	int durationInMilis = 1000;
//	aanim.setDuration(durationInMilis);
//	for(int i = 0; i<tempThrownArr.length;i++){
//		thrownCardsImgs[i].startAnimation(aanim);
//
//	}
//	//minA 3

	
	
//	for (int firstCardThrownIndex =  thrownCardsSizeUpTo5 - tempThrownArr.length;
//				firstCardThrownIndex < thrownCardsSize; firstCardThrownIndex++) {
	
//		//anim - v1
//		int[] handLocation = {0,0};
//		hand.getContainer().getLocationOnScreen(handLocation);
//		Log.e("DROPANIM","Hand: ["+handLocation[0]+","+handLocation[1]+"]");
//		int[] thrownCardsLocation = {0,0};
//		thrownCardsImgs[firstCardThrownIndex].getLocationOnScreen(thrownCardsLocation);
//		Log.e("DROPANIM","thrownCardsContainer: ["+thrownCardsLocation[0]+","+thrownCardsLocation[1]+"]");
//		//move from deck location to hand location
//		TranslateAnimation dealCardAnimation = new TranslateAnimation(
//				Animation.ABSOLUTE,handLocation[0],
//				Animation.ABSOLUTE,thrownCardsLocation[0],
//				Animation.ABSOLUTE,handLocation[1],
//				Animation.ABSOLUTE,thrownCardsLocation[1]
//				                				);
//		dealCardAnimation.setDuration(300); 
//		dealCardAnimation.setStartOffset(300*startOffset++);
//		thrownCardsImgs[firstCardThrownIndex++].startAnimation(dealCardAnimation);
//		
//		//1v -mina

		
//	}
	//TODO: end drop animation here
	headingTv.setText(hand.getPlayerName() + " dropped " + tempThrownArr.length +" card"+( tempThrownArr.length >1? "s":"" )+" and picked up from the "+ 
			(numCardsInDeckBeforePickup == numCardsInDeckAfterPickup? "thrown cards":"deck"));


	

}

private void performYanivHandler() {
	gameData.getP1Hand().doYaniv();
	//TODO: Perform yaniv (call p1hand.doYaniv()), end game
	//Note: will only be visible when yaniv is possible
	//note 2: this ends the game, need to call score here
	
	// Show everyone's cards
	gameData.getO1Hand().setShouldCardsBeShown(true);
	gameData.getO2Hand().setShouldCardsBeShown(true);
	gameData.getO3Hand().setShouldCardsBeShown(true);
	// And count the cards for each player
	int p1Count = gameData.getP1Hand().countCards();
	int o1Count = gameData.getO1Hand().countCards();
	int o2Count = gameData.getO2Hand().countCards();
	int o3Count = gameData.getO3Hand().countCards();
	p1Name.setText(String.valueOf(p1Count));
	o1Name.setText(String.valueOf(o1Count));
	o2Name.setText(String.valueOf(o2Count));
	o3Name.setText(String.valueOf(o3Count));
	//redraw hands
	redrawHand(gameData.getP1Hand());
	redrawHand(gameData.getO1Hand());
	redrawHand(gameData.getO2Hand());
	redrawHand(gameData.getO3Hand());
	
	//Check if won or lost
	ArrayList<Hand> playersByPosition = playersInOrder;
	Collections.sort(playersByPosition);
	if(playersByPosition.indexOf(gameData.getP1Hand()) == 0){
		//P1 won
		
		d.setTitle("You Won!");
		d.show();
	}else{
		d.setTitle(playersByPosition.get(0).getPlayerName() +" won! (you lost)");
		d.show();
	}
	//end game and add to scores
	//playersInOrder = winner is first and others after him clockwise 
	//turn = new Turn<Hand>(playersInOrder);
}


private void turnEndedHandler(Hand hand) {
	if(hand.isAwaitingInput())
	{
		//if the hand is awaiting input, there is no point in doing anything (human player)
		nextPlayerBtn.setVisibility(View.GONE);
		return;
	}else{
		nextPlayerBtn.setVisibility(View.VISIBLE);

		try {
			//First perform yaniv if strategy dictates it
			hand.doYaniv();

			switchCards(hand);
		} catch (InvalidDropException e) {
			d.setTitle(e.getMessage());
			d.show();//i know it's a bug, bug it should happen irl - no way that the ai will do an invalid drop...
		}
										
		}
			
	hand.getContainer().setBackgroundDrawable(null);
}


///**
//* Plays one round and returns the score
//* 
//* @param hands
//*            An array of hands
//* @param firstPlayer
//*            index of winner from last round
//* @return The score for this round
//*/
//public Score playRound(Hand[] hands, int firstPlayer) {
//
//	// 10. Deal cards from the deck and put a card in the thrown stack
//	dealCards();// TODO!!!
//
//	int turn = firstPlayer;
//	Hand currentHand = hands[turn];
//	boolean gameEnded = false;
//	// 20. while there are still cards in the deck
//	while (deck.getRemainingCardsNo() > 0 && !gameEnded) {
//		// 30.One player plays his hand on each iteration
//		gameEnded = currentHand.playTurn(deck);// TODO: make sure this
//		// affects the correct deck
//		// (by ref)
//
//		// 40. advance turn to next player
//		currentHand = hands[turn++ % 4];
//	}
//
//	firstDeal = true;
//	return calculateScore();
//
//}

//private Score calculateScore() {
//	// TODO this
//	return null;
//}

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


}