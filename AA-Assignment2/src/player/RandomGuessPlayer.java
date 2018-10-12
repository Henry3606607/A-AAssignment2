package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ship.Ship;
import ship.ShipStatus;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class RandomGuessPlayer implements Player{
	
	/*random guess player takes random guessess on any 
	square on the map. Doesn't care if it hits or not
	is very dumb and shoots blindly.*/
	
	
	//the boundaries of the world;
	int rowLimit;
	int columnLimit;
	int shipsRemaining;
	
	// Ship locations and shot history.
    public ArrayList<Guess> myGuessList = new ArrayList<>();
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
       
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess)
    {
    	Answer newAnswer = new Answer();
        newAnswer.isHit = false;
        
        for(ShipStatus status : shipStatusList){
			for(Coordinate coordinate : status.shipLocation.coordinates){
				if(coordinate.column == guess.column && coordinate.row == guess.row) {	
					// One of our ships has been hit :'(
					status.health--;
					if(status.health <= 0 ){
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
        
        //generates random integer between the 2 values.
        //so between rowLimit and 0;
        boolean added = false;
        while(!added){
        	added = true;
	        newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
	        newGuess.column = random.nextInt(columnLimit - 0 + 1) + 0;
	        for(Guess guess : myGuessList) {
	        	if(newGuess.row == guess.row && newGuess.column == guess.column) {
	        		added = false;
	        	}
	        }
        }
        
        myGuessList.add(newGuess);
        return newGuess;
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    	
    	
    	
    } // end of update()
    
    


    @Override
    public boolean noRemainingShips() {
        // To be implemented.
    	
    	if(shipStatusList.isEmpty()) {
    		return true;
    	}
    	System.out.println("No of ships left: "+shipStatusList.size());
    	
        return false;
    } // end of noRemainingShips()

} // end of class RandomGuessPlayer