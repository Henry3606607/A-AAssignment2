package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
public class GreedyGuessPlayer  implements Player{

	int rowLimit;
	int columnLimit;
	int shipsRemaining;
	
	// Ship locations and shot history.
    public ArrayList<Guess> myGuessList = new ArrayList<>();
    public ArrayList<Guess> mySuccessGuessList = new ArrayList<>();
    public ArrayList<ShipStatus> shipStatusList = new ArrayList<>();
	
	Random random = new Random();
	
    @Override
    public void initialisePlayer(World world) {
       this.columnLimit = world.numColumn - 1;
       this.rowLimit = world.numRow - 1;

       //Initiate the new class shipStatus for each ship
       for(ShipLocation location : world.shipLocations){
    	   ShipStatus status = new ShipStatus();
    	   status.shipLocation = location;
    	   System.out.println("ship: "+location.ship.name()+" len: "+location.ship.len()+" widht:"+location.ship.width());
    	   status.health = location.ship.len() * location.ship.width();
    	   System.out.println("health: "+ status.health);
    	   shipStatusList.add(status);
       }
    }  // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
    	Answer newAnswer = new Answer();
        newAnswer.isHit = false;
        
        for(ShipStatus status : shipStatusList) {
    		ArrayList<Coordinate> coordinates = status.shipLocation.coordinates;
    		for(Coordinate coordinate : coordinates) {
    			if(coordinate.column == guess.column && coordinate.row == guess.row) {
    				status.health--;
    				if(status.health <= 0 ) {
    					newAnswer.shipSunk = status.shipLocation.ship;
    					shipStatusList.remove(status);
    				}
    				newAnswer.isHit = true;
    				return newAnswer;
    			}
    		}
    	}
        return newAnswer;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
    	Guess newGuess = new Guess();
        
        boolean added = false;
        if(mySuccessGuessList.isEmpty()) {
	        while(!added){
	        	added = true;
	        	//generic greedy guess
	        	newGuess.column = random.nextInt(columnLimit - 0 + 1) + 0;
	        	if(newGuess.column % 2 == 0) {
		        	do {
		        		newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
		        	}while(newGuess.row % 2 != 0);
	        	}
	        	else {
	        		do {
		        		newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
		        	}while(newGuess.row % 2 == 0);
	        	}
	        	added = isUniqueGuess(newGuess);
	        }
    	}
	    else {
	    	//get the most recent guess
	    	do {
		    	newGuess = mySuccessGuessList.get(mySuccessGuessList.size() - 1);
		    	if(mySuccessGuessList.size() == 1) {
		    		//if first hit make a random guess
			    	int direction = random.nextInt(4 - 0 + 1) + 0;
			    	switch(direction) {
			    		case 1: newGuess.row += 1;
			    		case 2: newGuess.row -= 1;
			    		case 3: newGuess.column += 1;
			    		case 4: newGuess.column -= 1;
			    	}
		    	}
		    	else {
		    		for(Guess hit : mySuccessGuessList) {
		    			
		    		}
		    	}
	    	}while(isUniqueGuess(newGuess));
	    }
        
        myGuessList.add(newGuess);
        return newGuess;
    } // end of makeGuess()
    
    public boolean isUniqueGuess(Guess newGuess) {
    	//make sure I havent done this guess already
        for(Guess guess : myGuessList) {
        	if(newGuess.row == guess.row && newGuess.column == guess.column) {
        		return false;
        	}
        }
        return true;
    }


    @Override
    public void update(Guess guess, Answer answer) {
    	//my guess, other answer
    	if(answer.isHit) {
    		System.out.println("is a hit");
    		mySuccessGuessList.add(guess);
    		if(answer.shipSunk != null) {
    			mySuccessGuessList.clear();
    		}
    	}
    	
    	
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.
    	
    	if(shipStatusList.isEmpty()) {
    		return true;
    	}
    	
        return false;
    } // end of noRemainingShips()

} // end of class GreedyGuessPlayer
