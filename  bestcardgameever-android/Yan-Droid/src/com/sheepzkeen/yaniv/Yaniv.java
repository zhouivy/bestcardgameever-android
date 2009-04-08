package com.sheepzkeen.yaniv;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sheepzkeen.yaniv.R.id;

public class Yaniv extends Activity {
	
	//Deck
	private ImageView deckImg;
	int currentCard;
	SingleDeck deck;
	
	// Player 1
	private PlayerHand p1Hand;
	private ImageView[] p1Cards;
	private ImageView p1c1Img;
	private ImageView p1c2Img;
	private ImageView p1c3Img;
	private ImageView p1c4Img;
	private ImageView p1c5Img;
	private LinearLayout p1Container;

	// Opponent 1
	private ImageView o1c1Img;
	private ImageView o1c2Img;
	private ImageView o1c3Img;
	private ImageView o1c4Img;
	private ImageView o1c5Img;
	private LinearLayout o1Container;
	private ImageView[] o1Cards;
	private OpponentHand o1Hand;
	
	// Opponent 2
	private ImageView o2c1Img;
	private ImageView o2c2Img;
	private ImageView o2c3Img;
	private ImageView o2c4Img;
	private ImageView o2c5Img;
	private LinearLayout o2Container;
	private ImageView[] o2Cards;
	private OpponentHand o2Hand;
		
	// Opponent 3
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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
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
		for (int cardIndex = 0; cardIndex < 5; cardIndex++) {
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
	}

	/**
	 * Initializes all the components
	 */
	private void init() {
		firstDeal = true;
		uhOhDialog1 = new MyDialog(this);
		// Player 1
		p1c1Img = (ImageView) findViewById(id.p1c1);
		p1c2Img = (ImageView) findViewById(id.p1c2);
		p1c3Img = (ImageView) findViewById(id.p1c3);
		p1c4Img = (ImageView) findViewById(id.p1c4);
		p1c5Img = (ImageView) findViewById(id.p1c5);

		p1Container = (LinearLayout) findViewById(id.bottom);

		p1Cards = new ImageView[] { p1c1Img, p1c2Img, p1c3Img, p1c4Img, p1c5Img };

		p1Hand = new PlayerHand();

		// Opponent 1
		o1c1Img = (ImageView) findViewById(id.o1c1);
		o1c2Img = (ImageView) findViewById(id.o1c2);
		o1c3Img = (ImageView) findViewById(id.o1c3);
		o1c4Img = (ImageView) findViewById(id.o1c4);
		o1c5Img = (ImageView) findViewById(id.o1c5);

		o1Container = (LinearLayout) findViewById(id.leftCol);

		o1Cards = new ImageView[] { o1c1Img, o1c2Img, o1c3Img, o1c4Img,
				o1c5Img };

		o1Hand = new OpponentHand();

		// Opponent 2
		o2c1Img = (ImageView) findViewById(id.o2c1);
		o2c2Img = (ImageView) findViewById(id.o2c2);
		o2c3Img = (ImageView) findViewById(id.o2c3);
		o2c4Img = (ImageView) findViewById(id.o2c4);
		o2c5Img = (ImageView) findViewById(id.o2c5);
		o2Container = (LinearLayout) findViewById(id.topRow);

		o2Cards = new ImageView[] { o2c1Img, o2c2Img, o2c3Img, o2c4Img,
				o2c5Img };

		o2Hand = new OpponentHand();

		// Opponent 3
		o3c1Img = (ImageView) findViewById(id.o3c1);
		o3c2Img = (ImageView) findViewById(id.o3c2);
		o3c3Img = (ImageView) findViewById(id.o3c3);
		o3c4Img = (ImageView) findViewById(id.o3c4);
		o3c5Img = (ImageView) findViewById(id.o3c5);

		o3Container = (LinearLayout) findViewById(id.rightCol);

		o3Cards = new ImageView[] { o3c1Img, o3c2Img, o3c3Img, o3c4Img,
				o3c5Img };

		o3Hand = new OpponentHand();
		
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

	}

	protected void dealCards() {


		// 5 cards for each player
		for (int i = 0; i < 5; i++) {
			// player - card visible
			// Get card from deck
			PlayingCard card = deck.dealOneCard();
			// Add it to the hand
			p1Hand.addCard(card);
			// Redraw it
			redrawHand(p1Cards, p1Hand, p1Container);

			// opponents - cards not visible, just show that a card was added
			o1Hand.addCard(deck.dealOneCard());
			redrawHand(o1Cards, o1Hand, o1Container);

			o2Hand.addCard(deck.dealOneCard());
			redrawHand(o2Cards, o2Hand, o2Container);

			o3Hand.addCard(deck.dealOneCard());
			redrawHand(o3Cards, o3Hand, o3Container);

		}
	}

	private void redrawHand(ImageView[] cardView, Hand hand,
			LinearLayout container) {

		for (int i = 0; i < 5; i++) {
			if (hand.shouldCardsBeVisible()) {
				PlayingCard card = hand.getCardByLocation(i);
				if (card != null){
					//Show Card
					cardView[i].setVisibility(View.VISIBLE);
					int resId = card.getImageResourceId();
					cardView[i].setImageResource(resId);
					//Show isSelected
					//when selected, move up 10 pixels
					boolean isSelected = hand.isCardSelected(i);
					android.view.ViewGroup.LayoutParams currentParams = cardView[i].getLayoutParams();
					cardView[i].setLayoutParams(
							new AbsoluteLayout.LayoutParams
							(currentParams.width,currentParams.height,
									((AbsoluteLayout.LayoutParams)currentParams).x,
									 isSelected ? 0 : 10));

					
					
				}else{
					cardView[i].setVisibility(View.INVISIBLE);
				}
			} else {
				if (hand.getCardByLocation(i) != null) {
					cardView[i].setImageResource(R.drawable.back);
				}
			}
		}
		
		//and show the drop cards button if needed
		//if this and no other cards are selected, don't show the button
		if(hand.isAnyCardSelected() == false){
			dropCardsBtn.setVisibility(View.GONE);
		}else{
			dropCardsBtn.setVisibility(View.VISIBLE);
		}
		//TODO: problematic - this only applies to p1
		// although i did add the method to hand, this will only affect the button for p1
		// consider doing this only for p1Hand and not for hand
	
		container.postInvalidate();

	}
	
	private void redrawThrownCards() {
		PlayingCard[] cards = thrownCards.peekTopFive();
		System.out.println("redrawThrownCards: " +cards );
		for (int thrownCardIndex = 0; thrownCardIndex < cards.length; thrownCardIndex++) {
			if(cards[thrownCardIndex] != null){
				thrownCardsImgs[thrownCardIndex].
				setImageResource(cards[thrownCardIndex].getImageResourceId());
			}else{
				thrownCardsImgs[thrownCardIndex].
				setImageResource(R.drawable.back);
				
			}
		}
		thrownCardsContainer.postInvalidate();

	}
	
	/**
	 * Plays one round and returns the score
	 * 
	 * @param hands
	 *            An array of hands
	 * @param firstPlayer
	 *            index of winner from last round
	 * @return The score for this round
	 */
	public Score playRound(Hand[] hands, int firstPlayer) {

		// 10. Deal cards from the deck and put a card in the thrown stack
		dealCards();// TODO!!!

		int turn = firstPlayer;
		Hand currentHand = hands[turn];
		boolean gameEnded = false;
		// 20. while there are still cards in the deck
		while (deck.getRemainingCardsNo() > 0 && !gameEnded) {
			// 30.One player plays his hand on each iteration
			gameEnded = currentHand.playTurn(deck);// TODO: make sure this
			// affects the correct deck
			// (by ref)

			// 40. advance turn to next player
			currentHand = hands[turn++ % 4];
		}

		firstDeal = true;
		return calculateScore();

	}

	private Score calculateScore() {
		// TODO this
		return null;
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

	private void dropCardsClickHandler() {
		PlayingCard[] tempArr = p1Hand.drop();
		thrownCards.pushMulti(tempArr);
		dropCardsBtn.setVisibility(View.GONE);
		
		redrawThrownCards();
		redrawHand(p1Cards, p1Hand, p1Container);
	}



private void deckClickHandler() {
	if (firstDeal) {

		dealCards();
		firstDeal = false;
	} else {
		if(p1Hand.canPickup()){
			PlayingCard currentCard = deck.dealOneCard();
			// fill virtual hand on first available place
			/*int insLoc = */p1Hand.addCard(currentCard);
			//System.out.println("inserted card " +currentCard + " to location "+ insLoc);
			redrawHand(p1Cards, p1Hand, p1Container);
		}else{
			//show a dialog box saying 'cant pick up' or something
			uhOhDialog1.show();
			

		}
		
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
	redrawHand(p1Cards, p1Hand, p1Container);
}

private void thrownCardsClickHandler() {
	// When the last thrown card is clicked it is picked up
	// First verify that it is the player's turn and that he is
	// eligible for pickup
	if (p1Hand.canPickup()) {
		PlayingCard tempCard = thrownCards.getLastCardThrown();
		p1Hand.addCard(tempCard);
		redrawHand(p1Cards, p1Hand, p1Container);
		redrawThrownCards();
	} else {
		
		// show a dialog box saying wait ur turn or something
		uhOhDialog1.show();
	}
}
}