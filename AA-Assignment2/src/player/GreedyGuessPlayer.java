package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import ship.ShipStatus;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class GreedyGuessPlayer  implements Player
{

    int rowLimit;
    int columnLimit;
    int shipsRemaining;
	
    // Ship locations and shot history.
    private ArrayList<Guess> previousGuesses = new ArrayList<Guess>();
    private Stack<Guess> lastSuccessfullGuess = new Stack<Guess>(); // hold the coordinate's of the last guess to hit
    Boolean isHunting = true;	// in hunting mode we make guesses in a checker pattern
    private ArrayList<ShipStatus> shipStatuses = new ArrayList<ShipStatus>();
	
    Random random = new Random();
	
    @Override
    public void initialisePlayer(World world)
    {
	this.columnLimit = world.numColumn - 1;
	this.rowLimit = world.numRow - 1;

	//Initiate the new class shipStatus for each ship
	for(ShipLocation location : world.shipLocations)
	    {
		ShipStatus status = new ShipStatus();
		status.shipLocation = location;
		System.out.println("ship: "+location.ship.name()+" len: "+location.ship.len()+" widht:"+location.ship.width());
		status.health = location.ship.len() * location.ship.width();
		System.out.println("health: "+ status.health);
		shipStatuses.add(status);
	    }
    }  // end of initialisePlayer()

    // Return Answer object with .isHit set to true if the guess has hit one of our ships
    @Override
    public Answer getAnswer(Guess guess)
    {
    	Answer newAnswer = new Answer();
        newAnswer.isHit = false;
        
        for(ShipStatus status : shipStatuses)
	    {
		ArrayList<Coordinate> coordinates = status.shipLocation.coordinates;
		for(Coordinate coordinate : coordinates)
		    {
			if(coordinate.column == guess.column && coordinate.row == guess.row)
			    {	// One of our ships has been hit :'(
				status.health--;
				if(status.health <= 0 )
				    {
					newAnswer.shipSunk = status.shipLocation.ship;
					shipStatuses.remove(status);
				    }
				newAnswer.isHit = true;
				return newAnswer;
			    }
		    }
	    }
        return newAnswer;
    } // end of getAnswer()


    static int check = 0;//Maintain next position to check
    @Override
    public Guess makeGuess()
    {
    	Guess newGuess = new Guess();
        
        boolean added = false;
        if(isHunting)
	    {			// make random guess alligned on checkerboard pattern
	        while(!added)
		    {
	        	added = true;
	        	//generic greedy guess
			getRandomGuess(newGuess);
			added = isUniqueGuess(newGuess);
		    }
	    }
	else
	    {			// we hit a ship and now much make a more refined guess
	    	do
		    {
			System.out.println("check = " + check + "\nnewGuess.row = " + newGuess.row + ", newGuess.column " + newGuess.column);
			newGuess.row = lastSuccessfullGuess.peek().row;
			newGuess.column = lastSuccessfullGuess.peek().column;
			switch(check) // :)
			    {//new position (n), old position (o)
			    case 0:
				--newGuess.row;		//no
				break;
			    case 1:
				++newGuess.row;		//on
				break;
			    case 2:			//n
				++newGuess.column;     	//o
				break;
			    case 3:			//o
				--newGuess.column;	//n
				break;
			    }
			if(isUniqueGuess(newGuess) && !outOfBounds(newGuess))
			    {
				++check;
				break;
			    }
			else
			    {
				++check;
				if(check == 4)
				    {
					break;
				    }
			    }
		    }while(true);		
		if(check == 4)
		    {
			check = 0;
			lastSuccessfullGuess.pop();
			if(lastSuccessfullGuess.empty() == true)
			    isHunting = true;
		    }
		System.out.println("newGuess.row = " + newGuess.row + ", newGuess.column " + newGuess.column + "\n");
		//Scanner sc = new Scanner(System.in);
		//int i = sc.nextInt();
	    }
        
        previousGuesses.add(newGuess);
        return newGuess;
    } // end of makeGuess()

    private void getRandomGuess(Guess newGuess)
    {
	newGuess.column = random.nextInt(columnLimit - 0 + 1) + 0;
	if(newGuess.column % 2 == 0)
	    {
		do
		    {
			newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
		    }while(newGuess.row % 2 != 0);
	    }
	else
	    {
		do
		    {
			newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
		    }while(newGuess.row % 2 == 0);
	    }
    }
    
    public boolean isUniqueGuess(Guess newGuess)
    {
    	//make sure I havent done this guess already
        for(Guess guess : previousGuesses)
	    {
        	if(newGuess.row == guess.row && newGuess.column == guess.column)
		    {
        		return false;
		    }
	    }
        return true;
    }

    // return's false
    private boolean outOfBounds(final Guess newGuess)
    {
	if(newGuess.row > rowLimit || newGuess.row < 0
	   || newGuess.column > columnLimit || newGuess.column < 0)
	    {
		System.out.println("newGuess out of bounds\n\n");
		return true;
	    }
	return false;
    }


    //Update info about last guess. Was it correct (answer)?
    @Override
    public void update(Guess guess, Answer answer)
    {
    	if(answer.isHit)
	    {
		System.out.println("is a hit");
		lastSuccessfullGuess.push(guess);
		isHunting = false;
	    }
	else
	    {
		System.out.println("we missed :'(");
	    }
	previousGuesses.add(guess);
    } // end of update()


    @Override
    public boolean noRemainingShips()
    {
        // To be implemented.
    	
    	if(shipStatuses.isEmpty())
	    {
    		return true;
	    }
    	
        return false;
    } // end of noRemainingShips()

} // end of class GreedyGuessPlayer
