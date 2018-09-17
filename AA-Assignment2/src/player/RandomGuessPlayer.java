package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
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
	
	// Ship locations and shot history.
    public ArrayList<ShipLocation> shipLocations;
    public ArrayList<Coordinate> shots;
	
	//entire world - Maybe remove this and only have ships, shots etc.
	World myWorld;
	
	
	Random random = new Random();
	
    @Override
    public void initialisePlayer(World world) {
       this.columnLimit = world.numColumn;
       this.rowLimit = world.numRow;
       
       this.shipLocations = world.shipLocations;
       this.shots = world.shots;
       
       myWorld = world;
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
       Answer newAnswer = new Answer();
       newAnswer.isHit = myWorld.updateShot(guess);
    	
       return null;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        Guess newGuess = new Guess();
        
        //generates random integer between the 2 values.
        //so between rowLimit and 0;
        newGuess.row = random.nextInt(rowLimit - 0 + 1) + 0;
        newGuess.column = random.nextInt(columnLimit - 0 + 1) + 0;
        
        return newGuess;
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    	
    	//I think this would be where we update shots and ship hits etc
    	//cause we get the enemy guess and the answer?
    	
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.

    	//so i guess this runs through ship locations and shots
    	//and counts how many ships left?
    	
        // dummy return
        return true;
    } // end of noRemainingShips()

} // end of class RandomGuessPlayer
