package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ship.Ship;
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
	//the boundaries of the world;
	int rowLimit;
	int columnLimit;
	int shipsRemaining;
	
	// Ship locations and shot history.
    public ArrayList<Coordinate> shots;
    public ArrayList<Guess> myGuessList = new ArrayList<>();
    public ArrayList<ShipStatus> shipStatusList = new ArrayList<>();
	
	//entire world - Maybe remove this and only have ships, shots etc.
	World myWorld;
	
	
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
       
       this.shots = world.shots;
       
       myWorld = world;
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
       Answer newAnswer = new Answer();
       newAnswer.isHit = false;
       
       for(ShipStatus status : shipStatusList) {
    	   ArrayList<Coordinate> coordinates = status.shipLocation.coordinates;
	   		for(Coordinate coordinate : coordinates) {
	   			if(coordinate.column == guess.column && coordinate.row == guess.row) {
	   				newAnswer.isHit = true;
	   				if(status.health == 1) {
	   					newAnswer.shipSunk = status.shipLocation.ship;
	   				}
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
    	
    	//I think this would be where we update shots and ship hits etc
    	//cause we get the enemy guess and the answer?
    	for(ShipStatus status : shipStatusList) {
    		ArrayList<Coordinate> coordinates = status.shipLocation.coordinates;
    		for(Coordinate coordinate : coordinates) {
    			if(coordinate.column == guess.column && coordinate.row == guess.row) {
    				status.health--;
    				System.out.println("health reduced to: " + status.health);
    				if(status.health <= 0 ) {
    					shipStatusList.remove(status);
    					System.out.println("Ship destroyed: " + status.shipLocation.ship.name());
    				}
    				return;
    			}
    		}
    	}
    	
    } // end of update()
    
    private class ShipStatus {
    	ShipLocation shipLocation;
    	int health; 
    	boolean destroyed = false;
    }


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
