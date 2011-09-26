package com.ncc.edu.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * ========================================================================================
 * TicTaTacToeActivity --
 * The main Activity for the tictactoe game.
 * 	This class is responsible for all the game logic, keeping track
 * 	of the buttons, the tiles, the players, etc.
 * 	- The tic tac toe game is won by getting three of the same tiles in a row (X's or O's)
 * @author Rich Tufano
 *========================================================================================
 */
public class TicTacToeActivity extends Activity implements OnClickListener{

    /**
     * ========================================================================================
     * State -
     * 	The state enumeration keeps track of the state of the tiles.  The possible states are:
     * 	1)	EMPTY	- the tile has not been claimed by a player.
     * 	2)	X		- the tile has been claimed by player 1
     * 	3)	O		- the tile has been claimed by player 2
     * @author Rich Tufano
     * ========================================================================================
     */
    enum State {EMPTY, X, O};
    /**
     * ========================================================================================
     * Player -
     * 	The player enumeration keeps track of the players, their string names
     * 	and the assigned State.  The possible players are:
     * 	-	PLAYER1 - "Player 1", X
     * 	-	PLAYER2 - "Player 2", O
     * @author Rich Tufano
     * ========================================================================================
     */
    enum Player {
    	PLAYER1("Player 1", State.X), PLAYER2("Player 2", State.O);
    	String playerName;
    	State playerState;
    	Player(String name, State s){
    		playerName = name;
    		playerState = s;
		}
    	String getName(){
    		return playerName;
    	}
    	State getState(){
    		return playerState;
    	}
	}
    /**
     * ========================================================================================
     * TileException
     * 	- This exception is thrown if the Tile is already claimed.
     * @author Rich Tufano
     * ========================================================================================
     */
    class TileException extends Exception{
		private static final long serialVersionUID = -553368861900121392L;
		public TileException(){
    		super("This tile is already taken, choose another");
    	}
    }
    /**
     * ========================================================================================
     * GameOverException
     * 	- This exception is thrown if the game has been won.
     * @author Rich Tufano
     * ========================================================================================
     */
    class GameOverException extends Exception{
		private static final long serialVersionUID = 1L;
		public GameOverException(){
    		super("Game over.  Hit play again to start a new game.");
    	}
    }
    /**
     * ========================================================================================
     * NoMoreTurnsException
     * 	- This exception is thrown if there are no more turns available (the board is fully
     * 	claimed but there is no winner).  The game is now over.
     * @author Rich Tufano
     * ========================================================================================
     */
    class NoMoreTurnsException extends Exception{
		private static final long serialVersionUID = 1L;
		public NoMoreTurnsException(){
    		super("There are no more turns available.  Please start a new game!");
    	}
    }
    /** 
     * ========================================================================================
     *  Two dimensional array of Button references that represents the tiles.
     * 	the first dimension represents the x position of the tile/button, and the
     *  second dimension represents the y position of the tile/button.
     * ========================================================================================
     **/
    Button[][] tiles; 	
    /** Button reference to the new game button **/
    Button play;		
    /** Reference to the status text-box. **/
    TextView status;	
    /** Flag that sets the buttons unclickable after someone has won. **/
    boolean lockGame;	
    /** Two dimensional array to State references that represents the state of the tiles **/
    State[][] board = {	
    	{State.EMPTY,State.EMPTY,State.EMPTY},
    	{State.EMPTY,State.EMPTY,State.EMPTY},
    	{State.EMPTY,State.EMPTY,State.EMPTY}
	};
    /** A Player reference that points to the current player **/
    Player currentPlayer = null;
    /** Int storing the number of turns since the start of this game **/
    int turns = 0;		
    
    /**
     * ========================================================================================
     * onCreate() -
     * 	- Initializes the android activity by calling Activity's constructor.  
     *  This method calls buildTileBoard, sets the currentPlayer to player1
     *  and sets the status text to "Player 1's turn; turn # 0".
     *  It also sets the game not to be locked.
     * ========================================================================================
     */
    public void onCreate(Bundle savedInstanceState) {
    	//call the super's onCreate
        super.onCreate(savedInstanceState);
        //load the content view
        setContentView(R.layout.main);
        //build the tile board
        this.buildTileBoard();
        //set the initial status
        status = (TextView)this.findViewById(R.id.tStatus);
        //set the current player; player 1 starts
        currentPlayer = Player.PLAYER1;
        //set the status text
        status.setText(currentPlayer.getName()+"'s turn; turn # "+turns);
        //make sure the game isn't locked
        lockGame = false;
    }
    /**
     * ========================================================================================
     * buildTileBoard() - 
     * 	- This method binds the 2-dimensional Button array to
     * 	it's respective buttons, using the matching ID's to order
     * 	via the xy coords.  This function also binds the Buttons
     * 	to the onClickListener.  
     * ========================================================================================
     */
    private void buildTileBoard(){
    	//initialize the Button array, and set the Buttons.
    	tiles = new Button[3][3];
    	tiles[0][0] = (Button)this.findViewById(R.id.b00);
    	tiles[0][1] = (Button)this.findViewById(R.id.b01);
    	tiles[0][2] = (Button)this.findViewById(R.id.b02);
    	tiles[1][0] = (Button)this.findViewById(R.id.b10);
    	tiles[1][1] = (Button)this.findViewById(R.id.b11);
    	tiles[1][2] = (Button)this.findViewById(R.id.b12);
    	tiles[2][0] = (Button)this.findViewById(R.id.b20);
    	tiles[2][1] = (Button)this.findViewById(R.id.b21);
    	tiles[2][2] = (Button)this.findViewById(R.id.b22);
    	//Set the newGame button to the reference
    	play = (Button)this.findViewById(R.id.bAgain);
    	//Bind the onClickListerners to the tiles
    	for(int i = 0; i<3; i++)
    		for(int j = 0; j<3; j++)
    			tiles[i][j].setOnClickListener(this);
    	//Bind the onClickListener to the newGame button
    	play.setOnClickListener(this);
    }
    /**
     * ========================================================================================
     * onClick(View v) - 
     * 	- The click listener for all the buttons.  Works as a switch statement.
     * 	If a tile button is clicked, this method checks to make sure that the
     * 	tile is not claimed.  If it's empty, it calls the markTile() method,
     * 	passing the View (button), and the x and y corrds.
     * 	If the play again button is pressed, this method calls the newGame()
     * 	method.
     * 	
     * 	- This method must handle three different exceptions.
     * 		1) TileException - thrown if the tile is already claimed.
     * 		2) GameOverException - thrown up by the markTile() method. Handled here.
     * 		3) NoMoreTurnsException - thrown up by the markTile() method. Handled here.
     * ========================================================================================
     */
	public void onClick(View v) {
		int id = v.getId();		//get the id of the View that's been clicked
		try{
			/*
			 * switch on ID.  Each case is for a button.
			 * On all the tile buttons.  we check if the button is empty.
			 * if it is, we call markTile passing the view (v), along with
			 * the x,y coordinates.
			 * if the button is claimed, throw a TileException.
			 */
			switch(id){
				case R.id.b00:
					if(board[0][0] == State.EMPTY)
						markTile(v,0,0);
					else
						throw new TileException();
					break;
				case R.id.b01:
					if(board[0][1] == State.EMPTY)
						markTile(v,0,1);
					else
						throw new TileException();
					break;
				case R.id.b02:
					if(board[0][2] == State.EMPTY)
						markTile(v,0,2);
					else
						throw new TileException();
					break;
				case R.id.b10:
					if(board[1][0] == State.EMPTY)
						markTile(v,1,0);
					else
						throw new TileException();
					break;
				case R.id.b11:
					if(board[1][1] == State.EMPTY)
						markTile(v,1,1);
					else
						throw new TileException();
					break;
				case R.id.b12:
					if(board[1][2] == State.EMPTY)
						markTile(v,1,2);
					else
						throw new TileException();
					break;
				case R.id.b20:
					if(board[2][0] == State.EMPTY)
						markTile(v,2,0);
					else
						throw new TileException();
					break;
				case R.id.b21:
					if(board[2][1] == State.EMPTY)
						markTile(v,2,1);
					else
						throw new TileException();
					break;
				case R.id.b22:
					if(board[2][2] == State.EMPTY)
						markTile(v,2,2);
					else
						throw new TileException();
					break;
				case R.id.bAgain:
					//The Play Again button has been clicked.  Start a new game.
					this.newGame();
					break;
			}
		}catch(TileException e){		//TileException - the tile clicked was already taken
			status.setText(e.getMessage());
		}catch(GameOverException e){	//GameOverException - a player has won the game. No tiles can be clicked
			status.setText(e.getMessage());
		}catch(NoMoreTurnsException e){	//NoMoreTurnsExcpetion - there are no more valid turns available.  
			status.setText(e.getMessage());
		}
	}
	
	/**
     * ========================================================================================
	 * markTile(View v, int x, int y) - 
	 * 	- The markTile method is used to mark a tile after it's been chosen by
	 * 	a player.  The method does this by first checking if the game is 
	 * 	locked (the game may be locked due to a winning state, and therefore no
	 * 	more gameplay is allowed).  If the game is still playable, it checks who
	 * 	the current player is and does two things:
	 * 		1) sets the text of the button equal to X or O respectively.
	 * 		2) Sets the State of that specific tile to it's correct State
	 * 	The method then calls nextTurn() to start the next players turn.
	 * @param v	the Button that's been clicked (stored as it's parent class, View)
	 * @param x	the x position of the Button that's been clicked.
	 * @param y the y position of the Button that's been clicked
	 * @throws GameOverException thrown by the nextTurn() method.  Thrown up to onClick().
	 * @throws NoMoreTurnsException thrown by the nextTurn() method.  Thrown up to onClick().
     * ========================================================================================
	 */
	private void markTile(View v, int x, int y) throws GameOverException, NoMoreTurnsException{
		try{
			//check to see if the game is in a locked state
			if(lockGame)
				//if it is, throw the GameOverException()
				throw new GameOverException();
			//Check the currentPlayer
			if(currentPlayer.equals(Player.PLAYER1)){
				//set the button text to X
				Button.class.cast(v).setText(R.string.x_val);
				//set the tile state to X
				board[x][y] = State.X;
			}
			//if player2
			else{
				//set the button text to O
				Button.class.cast(v).setText(R.string.o_val);
				//set the tile state to O
				board[x][y] = State.O;
			}
			//start the next turn.
			this.nexTurn();
		}catch(ClassCastException e){  //Exception handling for casting the view to button.
			System.out.println(e.getMessage());
		}
	}
	
	/**
     * ========================================================================================
	 * nextTurn() - 
	 * 	- The nextTurn method starts the next turn after a player selects
	 * 	an empty tile.  First it increments the turns.  Next, it callls
	 * 	the hasWon(Player p) method to see if the game is over.  If it is, it sets
	 * 	the status to signify that.	If a player hasn't won, it checks if the board 
	 * 	is full.  If it is, the method throws a NoMoreTurnsException.
	 * 	If a player hasn't won, and the board is not full, the method sets the next
	 * 	player to be the current player, and sets the status accordingly.
	 * @throws NoMoreTurnsException
     * ========================================================================================
	 */
	private void nexTurn() throws NoMoreTurnsException{
		//increment turn
		turns++;
		//check to see if the currentPlayer's won
		if(this.hasWon(currentPlayer)){
			//if the game is over, set the status text, and lock the game.
			status.setText(currentPlayer.getName()+" has won!");
			lockGame = true;
		}
		//check to see if the board is full.
		else if(this.boardFull())
			//if it is, throw up a NoMoreTurnsException
			throw new NoMoreTurnsException();
		//If the board is not full and the game hasn't been won, set the status text to the next turn's status
		else
			status.setText(currentPlayer.getName()+"'s turn; turn # "+turns);
		//Change currentPlayer to the next player.
		if(currentPlayer.equals(Player.PLAYER1))
			currentPlayer = Player.PLAYER2;
		else
			currentPlayer = Player.PLAYER1;
	}
	/**
     * ========================================================================================
	 * hasWon()
	 * 	- This method returns true if the current player has won. To check for a win case, the
	 * 	method must check 4 different possibilities.
	 * 		1) Columns 	- Check each column with a simple loop.
	 * 		2) Rows 	- Check each row with a simple loop.
	 * 		3/4) Diagnols - Check both of the Diagnol possibilities.
	 * @return true if the player has won.
     * ========================================================================================
	 */
	private boolean hasWon(Player p){
		//store the playerState (X or O)
		State toCheck = p.getState();
		//Check the columns by incrementing through each of the three columns, 
		//and seeing if each tile is equal to the player's State
		for(int col = 0; col<3; col++){
			if(board[col][0].equals(toCheck) && board[col][1].equals(toCheck) && board[col][2].equals(toCheck))
				return true;	//All three tiles are the same state as the playerState, game has been won
		}
		//Check the rows by incrementing through each of the three rows,
		//and see if each tile is equal to the player's state.
		for(int row = 0; row<3; row++){
			if(board[0][row].equals(toCheck) && board[1][row].equals(toCheck) && board[2][row].equals(toCheck))
				return true;	//All three tiles in the row are the same state as the playerState, the game has been won.
		}
		//Check the diagnols by checking positions (0,0), (1,1), (2,2,)
		if(board[0][0].equals(toCheck) && board[1][1].equals(toCheck) && board[2][2].equals(toCheck))
			return true;	//All three tiles have the same state as the player, game has been won.
		//check the other diagnol by checking positions (0,2), (1,1), (2,0)
		if(board[0][2].equals(toCheck) && board[1][1].equals(toCheck) && board[2][0].equals(toCheck))
			return true;	//All three tiles have the same state as the player, game has been won.
		//if none of the win statements have been triggered, the method will return false: the game has not been won yet
		return false;
	}
	/**
     * ========================================================================================
	 * boardFull() - 
	 * 	- checks to see if the board is full.  If it is, return true; if not, return false.
	 * @return true if the board is full
     * ========================================================================================
	 */
	private boolean boardFull(){
		//Create a boolean, assume the board is full.
		boolean full = true;
		//If any of the tiles are empty, the baord is not full.  Therefor, if any of the tiles are empty, full=false.
		for(int i = 0; i<3; i++)
			for(int j = 0; j<3; j++)
				if(board[i][j].equals(State.EMPTY))
					full = false;
		//return the value of full.
		return full;
	}
	/**
     * ========================================================================================
	 * clearTiles() - 
	 * 	- clears all the tiles on the board.  Sets all the button text to "", 
	 * 	and sets their state to EMPTY.
     * ========================================================================================
	 */
	private void clearTiles(){
		//clear all the tiles by looping through them and setting each Button's text to "" and each tile's state to EMPTY
		for(int i = 0; i<3; i++)
			for(int j = 0; j<3; j++){
				tiles[i][j].setText("");
				board[i][j] = State.EMPTY;
			}
	}
	/**
     * ========================================================================================
	 * newGame() - 
	 * 	- starts a new game.  Resets the currentPlayer to player1, 
	 * 	sets the turns to 0, and calls the clearTiles() method.
     * ========================================================================================
	 */
	private void newGame(){
		currentPlayer = Player.PLAYER1;	//set the current player back to player1
		turns = 0;						//reset the number of turns
		lockGame = false;				//unlock the game (it will most likely be locked)
		this.clearTiles();				//clear all the tiles
	}
}