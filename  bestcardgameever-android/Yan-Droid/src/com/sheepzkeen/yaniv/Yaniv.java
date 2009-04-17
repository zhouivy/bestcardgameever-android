package com.sheepzkeen.yaniv;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sheepzkeen.yaniv.R.id;

public class Yaniv extends Activity {
	
	public static final int YANIV_NUM_CARDS = 5;
	//Deck
	private ImageView deckImg;
	int currentCard;
	SingleDeck deck;
	
	// Player 1
	private TextView p1Name;
	private PlayerHand p1Hand;
	private ImageView[] p1Cards;
	private ImageView p1c1Img;
	private ImageView p1c2Img;
	private ImageView p1c3Img;
	private ImageView p1c4Img;
	private ImageView p1c5Img;
	private LinearLayout p1Container;

	// Opponent 1
	private TextView o1Name;
	private ImageView o1c1Img;
	private ImageView o1c2Img;
	private ImageView o1c3Img;
	private ImageView o1c4Img;
	private ImageView o1c5Img;
	private LinearLayout o1Container;
	private ImageView[] o1Cards;
	private OpponentHand o1Hand;
	
	// Opponent 2
	private TextView o2Name;
	private ImageView o2c1Img;
	private ImageView o2c2Img;
	private ImageView o2c3Img;
	private ImageView o2c4Img;
	private ImageView o2c5Img;
	private LinearLayout o2Container;
	private ImageView[] o2Cards;
	private OpponentHand o2Hand;
		
	// Opponent 3
	private TextView o3Name;
	private ImageView o3c1Img;
	private ImageView o3c2Img;
	private ImageView o3c3Img;
	private ImageView o3c4Img;
	private ImageView o3c5Img;
	private LinearLayout o3Container;
	private ImageView[] o3Cards;
	private OpponentHand o3Hand;

	//Thrown Cards
	private ImageView c1Thrown;
	private ImageView c2Thrown;
	private ImageView c3Thrown;
	private ImageView c4Thrown;
	private ImageView c5Thrown;
	private ImageView[] thrownCardsImgs;
	private View thrownCardsContainer;
	
	
	//Misc.
	private Button dropCardsBtn;
	private boolean firstDeal;
	private ThrownCards thrownCards;
	protected MyDialog uhOhDialog1;
	private ArrayList<Hand> playersInOrder;
	private Turn<Hand> turn;
	private Button yanivBtn;
	
	//TODO: remove this
	private Dialog d;
	private PlayingCard[] tempThrownArr;
	//TODO: end remove this
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
		
		  //////////////////
		 //TESTING/////////
		//////////////////
try{
		TextView t = new TextView(this);
		t.setText("test");
		PlayerHand p1h = new PlayerHand(null,null,t);
			ArrayList<PlayingCard[]> cardsList = new ArrayList<PlayingCard[]>();
			
			PlayingCard[] pca = new PlayingCard[5];

			for(Integer i = 0; i<5; i++){
				PlayingCard c = new PlayingCard(PlayingCard.CLUBS,Character.forDigit(i, 10) );
				c.setSelected(true);
				pca[i] = c;
			}
			
			cardsList.add(pca);
			
			for (PlayingCard[] playingCards : cardsList) {
				p1h.cards = playingCards;
				
					p1h.selectCardsToDrop();
				
			}
}catch (Exception e) {
	e.printStackTrace();
}
			  //////////////////
			 //END TESTING/////
			//////////////////

		
		
		
		// Perform Yaniv Listener
		yanivBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				p1Hand.doYaniv();
				//TODO: Perform yaniv (call p1hand.doYaniv()), end game
				//Note: will only be visible when yaniv is possible
				//note 2: this ends the game, need to call score here
				
				// Show everyone's cards
				o1Hand.setShouldCardsBeShown(true);
				o2Hand.setShouldCardsBeShown(true);
				o3Hand.setShouldCardsBeShown(true);
				// And count the cards for each player
				int p1Count = p1Hand.countCards();
				int o1Count = o1Hand.countCards();
				int o2Count = o2Hand.countCards();
				int o3Count = o3Hand.countCards();
				p1Name.setText(String.valueOf(p1Count));
				o1Name.setText(String.valueOf(o1Count));
				o2Name.setText(String.valueOf(o2Count));
				o3Name.setText(String.valueOf(o3Count));
				//redraw hands
				redrawHand(p1Hand);
				redrawHand(o1Hand);
				redrawHand(o2Hand);
				redrawHand(o3Hand);
				
				//Check if won or lost
				ArrayList<Hand> playersByPosition = playersInOrder;
				Collections.sort(playersByPosition);
				if(playersByPosition.indexOf(p1Hand) == 0){
					//P1 won
					
					d.setTitle("You Won!");
					d.show();
				}else{
					d.setTitle(playersByPosition.get(0).getPlayerName() +" won! (you lost)");
					d.show();
				}
			}
			
		});
		
		// Drop Cards Listener
		dropCardsBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v){
				dropCardsClickHandler();
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
		
		turn.setOnTurnEndedListener(new Turn.OnTurnEndedListener<Hand>(){
			
			@Override
			public void onTurnEnded(Hand hand){
				if(hand.isAwaitingInput())
				{
					//if the hand is awaiting input, there is no point in doing anything
					return;
				}else{
					//this hand has to go through the motions
					//TODO: remove this (just a simulation of going through the players)
					d.setCancelable(true);
					d.setTitle("This is Player " + hand.getPlayerName());
					d.show();
					d.setOnCancelListener(new OnCancelListener(){

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							turn.next();							
						}
						
					});
					
					//TODO: end remove this
					

				}
			}
		});
	}

	/**
	 * Initializes all the components
	 */
	private void init() {
		firstDeal = true;

		//TODO: remove this
		d = new Dialog(this);
		d.setTitle("hello");
		//TODO: end remove this

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

		p1Hand = new PlayerHand(p1Container,p1Cards, p1Name);

		// Opponent 1
		o1Name = (TextView) findViewById(id.o1Name);
		o1c1Img = (ImageView) findViewById(id.o1c1);
		o1c2Img = (ImageView) findViewById(id.o1c2);
		o1c3Img = (ImageView) findViewById(id.o1c3);
		o1c4Img = (ImageView) findViewById(id.o1c4);
		o1c5Img = (ImageView) findViewById(id.o1c5);

		o1Container = (LinearLayout) findViewById(id.leftCol);

		o1Cards = new ImageView[] { o1c1Img, o1c2Img, o1c3Img, o1c4Img,
				o1c5Img };

		o1Hand = new OpponentHand(o1Container,o1Cards, o1Name);

		// Opponent 2
		o2Name = (TextView) findViewById(id.o2Name);
		o2c1Img = (ImageView) findViewById(id.o2c1);
		o2c2Img = (ImageView) findViewById(id.o2c2);
		o2c3Img = (ImageView) findViewById(id.o2c3);
		o2c4Img = (ImageView) findViewById(id.o2c4);
		o2c5Img = (ImageView) findViewById(id.o2c5);
		o2Container = (LinearLayout) findViewById(id.topRow);

		o2Cards = new ImageView[] { o2c1Img, o2c2Img, o2c3Img, o2c4Img,
				o2c5Img };

		o2Hand = new OpponentHand(o2Container,o2Cards,o2Name);

		// Opponent 3
		o3Name = (TextView) findViewById(id.o3Name);
		o3c1Img = (ImageView) findViewById(id.o3c1);
		o3c2Img = (ImageView) findViewById(id.o3c2);
		o3c3Img = (ImageView) findViewById(id.o3c3);
		o3c4Img = (ImageView) findViewById(id.o3c4);
		o3c5Img = (ImageView) findViewById(id.o3c5);

		o3Container = (LinearLayout) findViewById(id.rightCol);

		o3Cards = new ImageView[] { o3c1Img, o3c2Img, o3c3Img, o3c4Img,
				o3c5Img };

		o3Hand = new OpponentHand(o3Container,o3Cards,o3Name);
		
		// Thrown Cards 
		thrownCards = new ThrownCards();
		c5Thrown = (ImageView) findViewById(id.card5);
		c4Thrown = (ImageView) findViewById(id.card4);
		c3Thrown = (ImageView) findViewById(id.card3);
		c2Thrown = (ImageView) findViewById(id.card2);
		c1Thrown = (ImageView) findViewById(id.card1);
		thrownCardsImgs = new ImageView[] {c1Thrown,c2Thrown,c3Thrown,c4Thrown,c5Thrown};
		thrownCardsContainer = findViewById(id.cardsThrown);

		// Deck
		deckImg = (ImageView) findViewById(id.deck);
		deck = new SingleDeck();

		// Drop Cards Button
		dropCardsBtn = (Button)findViewById(id.DropCards);
		// will be gone until required
		dropCardsBtn.setVisibility(View.GONE);
		
		// Perform Yaniv Button
		yanivBtn = (Button)findViewById(id.PerformYaniv);
		// will be gone until required
		yanivBtn.setVisibility(View.GONE);
		
		// array of starting order of players 
		playersInOrder = new ArrayList<Hand>();
		playersInOrder.add(p1Hand);
		playersInOrder.add(o1Hand);
		playersInOrder.add(o2Hand);
		playersInOrder.add(o3Hand);
		
		// Turn
		turn = new Turn<Hand>(playersInOrder);
	}

	protected void dealCards() {


		// 5 cards for each player
		for (int i = 0; i < Yaniv.YANIV_NUM_CARDS; i++) {
			
			for (Hand hand : playersInOrder) {
				hand.pickup(deck.dealOneCard());
				redrawHand(hand);
			}
		}
		// after dealing, put a card on the table for pick up
		thrownCards.push(deck.dealOneCard());
		redrawThrownCards();
	}

	private void redrawHand(Hand hand) {
		ImageView[] cardView = hand.getCardsViews();
		View container = hand.getContainer();
		
		for (int i = 0; i < Yaniv.YANIV_NUM_CARDS; i++) {
			if (hand.shouldCardsBeShown()) {
				PlayingCard card = hand.getCardByLocation(i);
				if (card != null){
					//Show Card
					cardView[i].setVisibility(View.VISIBLE);
					int resId = card.getImageResourceId();
					cardView[i].setImageResource(resId);
					//TODO: Disgusting patch, need to fix asap!!!
					if (hand == p1Hand){
						//Show isSelected
						//when selected, move up 10 pixels
						boolean isSelected = hand.isCardSelected(i);
						android.view.ViewGroup.LayoutParams currentParams = cardView[i].getLayoutParams();
						cardView[i].setLayoutParams(
								new AbsoluteLayout.LayoutParams
								(currentParams.width,currentParams.height,
										((AbsoluteLayout.LayoutParams)currentParams).x,
										 isSelected ? 0 : 10));
					}
				}else{
					cardView[i].setVisibility(View.INVISIBLE);
				}
			} else {
				if (hand.getCardByLocation(i) != null) {
					cardView[i].setImageResource(R.drawable.back);
				}
			}
		}
		
		//TODO: problematic - this only applies to p1:
		//and show the drop cards button if needed
		//if this and no other cards are selected, don't show the button
		if(p1Hand.isAnyCardSelected() == false){
			dropCardsBtn.setVisibility(View.GONE);
		}else{
			dropCardsBtn.setVisibility(View.VISIBLE);
		}
		//and the perform yaniv button...
		if(p1Hand.canYaniv()){
			yanivBtn.setVisibility(View.VISIBLE);
		}else
		{
			yanivBtn.setVisibility(View.GONE);
		}
		
	
		container.postInvalidate();

	}
	
	private void redrawThrownCards() {
		PlayingCard[] cards = thrownCards.peekTopFive();
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
	
//	/**
//	 * Plays one round and returns the score
//	 * 
//	 * @param hands
//	 *            An array of hands
//	 * @param firstPlayer
//	 *            index of winner from last round
//	 * @return The score for this round
//	 */
//	public Score playRound(Hand[] hands, int firstPlayer) {
//
//		// 10. Deal cards from the deck and put a card in the thrown stack
//		dealCards();// TODO!!!
//
//		int turn = firstPlayer;
//		Hand currentHand = hands[turn];
//		boolean gameEnded = false;
//		// 20. while there are still cards in the deck
//		while (deck.getRemainingCardsNo() > 0 && !gameEnded) {
//			// 30.One player plays his hand on each iteration
//			gameEnded = currentHand.playTurn(deck);// TODO: make sure this
//			// affects the correct deck
//			// (by ref)
//
//			// 40. advance turn to next player
//			currentHand = hands[turn++ % 4];
//		}
//
//		firstDeal = true;
//		return calculateScore();
//
//	}

//	private Score calculateScore() {
//		// TODO this
//		return null;
//	}

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

	/**
	 * 	what to do when one of the players cards are clicked
	 *  mark as selected \ unselected in gameplay and update the hand
	 *
	 * @param cardIndex the index of the selected card
	 */
	private void p1CardsClickHandler(final int cardIndex) {
		p1Hand.changeSelectionStateOnCard(cardIndex);
		redrawHand(p1Hand);
	}

	/**
	 * I - 1
	 * Drop cards that were previously marked
	 * RULE: first you drop, then you pickup
	 */
	private void dropCardsClickHandler() {
		if (p1Hand.getCanDrop() == true){
			try {
				tempThrownArr = p1Hand.drop();
			
			// Rule: after drop you are not allowed to drop again
			p1Hand.setCanDrop(false);
			// Rule: after drop you are allowed to pickup again 
			p1Hand.setCanPickup(true);
			
			dropCardsBtn.setVisibility(View.GONE);
			//Note, we don't redraw the cards here, since we want the player to see the cards he is throwing until they are down
			
			} catch (InvalidYanivException e) {
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
	// First verify that it is the player's turn and that he is
	// eligible for pickup
	if (p1Hand.canPickup() && turn.peek().isAwaitingInput() == true && turn.peek().getCanPickup() == true ) {
		// Get one card from the thrown deck
		PlayingCard tempCard = thrownCards.popLastCardThrown();
		// fill virtual hand on first available place
		p1Hand.pickup(tempCard);
		// Rule: after pickup you are allowed to drop again 
		p1Hand.setCanDrop(true);
		// Rule: after pickup you are not allowed to pickup again 
		p1Hand.setCanPickup(false);
		
		//mark the cards in the cards to drop as unselected so that if somebody picks them up they will be unselected
		for (PlayingCard card : tempThrownArr) {
			if (card != null){
				card.setSelected(false);
			}
		}
		//Update the thrown cards only after the pickup (RULE)
		thrownCards.pushMulti(tempThrownArr);

		// redraw it
		redrawHand(p1Hand);
		// and the thrown card deck
		redrawThrownCards();
		turn.next();
	} else {
		//show a dialog box saying 'cant pick up' or something
		uhOhDialog1.show();
	}
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
		if(p1Hand.canPickup() && turn.peek().isAwaitingInput() == true && turn.peek().getCanPickup() == true){
			// Get one card from the deck
			PlayingCard currentCard = deck.dealOneCard();
			// fill virtual hand on first available place
			p1Hand.pickup(currentCard);
			// Rule: after pickup you are allowed to drop again
			p1Hand.setCanDrop(true);
			// Rule: after pickup you are not allowed to pickup again 
			p1Hand.setCanPickup(false);

			//mark the cards in the cards to drop as unselected so that if somebody picks them up they will be unselected
			for (PlayingCard card : tempThrownArr) {
				if (card != null){
					card.setSelected(false);
				}
			}
			// Update the thrown cards only after the pickup (RULE)
			thrownCards.pushMulti(tempThrownArr);
			// And redraw the thrown deck
			redrawThrownCards();
			
			//and redraw it
			redrawHand(p1Hand);
			turn.next();
		}else{
			//show a dialog box saying 'cant pick up' or something
			uhOhDialog1.show();
		}
		
	}
}
}