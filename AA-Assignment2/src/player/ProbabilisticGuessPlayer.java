package player;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import ship.ShipStatus;
import ship.Ship;
import ship.PatrolCraft;
import ship.Submarine;
import ship.Cruiser;
import ship.Frigate;
import ship.AircraftCarrier;
import player.Answer;
import world.World;
import world.World.ShipLocation;
import world.World.Coordinate;

/**
 * Probabilistic guess player (task C).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class ProbabilisticGuessPlayer  implements Player
{
    private int rowLim;		// Number of rows in board less one
    private int colLim;		// Number of columns in board less one
    private int shipsRemaining;
    private ArrayList<Guess> privGuess = new ArrayList<Guess>();
    private ArrayList<Answer> privAnswers = new ArrayList<Answer>();
    private ArrayList<ShipStatus> shipStatuses = new ArrayList<ShipStatus>(); // The state of our ships :)
    private ArrayList<Ship> ships = new ArrayList<Ship>(); // Used to hold an instance of each type of ship
    // hold's probability of ships being in it's coordinates. center of zone = location of last ship hit and not sunk
    private int[][] targetZone;
    private Guess lastShipHit;
    boolean isHunting = true;	// Are we making rand guesses?
    Random rand = new Random();
    
    @Override
    public void initialisePlayer(World world)
    {
	this.rowLim = world.numRow -1;
	this.colLim = world.numColumn -1;
	// Populate ships ArrayList with each type of ship :)
	ships.add(new PatrolCraft());
	ships.add(new Submarine());
	ships.add(new Cruiser());
	ships.add(new Frigate());
	ships.add(new AircraftCarrier());
	// Calculate size of and initialize targetZone
	{
	    int maxLen = 0, tmp;
	    for(Ship s: ships)
		{
		    tmp = s.len();
		    maxLen = tmp > maxLen ? tmp : maxLen;
		    tmp = s.width();
		    maxLen = tmp > maxLen ? tmp : maxLen;
		}
	    maxLen = (maxLen * 2) -1; // space a ship can be in
	    targetZone = new int[maxLen][maxLen];
	}
	// Populate the shipStatuses array
	for(ShipLocation l: world.shipLocations)
	    {
		ShipStatus status = new ShipStatus();
		status.shipLocation = l;
		System.out.println("ship: " + l.ship.name() + ", len: " + l.ship.len() + ", width: " + l.ship.width());
		status.health = l.ship.len() * l.ship.width();
		System.out.println("health: " + status.health);
		shipStatuses.add(status);
	    }
    }

    // Return Answer object with .isHit set to true if the guess has hit one of our ships
    @Override
    public Answer getAnswer(Guess guess)
    {
	Answer ret = new Answer();
	ret.isHit = false;
	for(ShipStatus stat: shipStatuses)
	    {
		ArrayList<Coordinate> coords = stat.shipLocation.coordinates;
		for(Coordinate c: coords)
		    {
			if(c.column == guess.column && c.row == guess.row)
			    {	// One of our ships has been hit :'(
				--stat.health;
				if(stat.health <= 0)
				    {
					ret.shipSunk = stat.shipLocation.ship;
					shipStatuses.remove(stat);
				    }
				ret.isHit = true;
				return ret;
			    }
		    }
	    }
	return ret;
    }


    /*
    ArrayList<Guess> privGuess
    ArrayList<Answer> privAnswers
    ArrayList<ShipStatus> shipStatuses			// The state of our ships :)
    ArrayList<Ship> ships = new ArrayList<Ship>(); 	// Used to hold an instance of each type of ship
    ArrayList<ArrayList<int>> targetZone		// hold's probability of ships being in it's coordinates. center of zone = location of last ship hit and not sunk
     */
    private int checkerAttempts = 0;
    private int sequence = 0;
    @Override
    public Guess makeGuess()
    {
	Guess ret = new Guess();
	if(isHunting)
	    {
		sequence = 0;
		System.out.println("in hunting mode!!!!!!!!!!!!");
		do
		    {
			System.out.println("hello, checkerAttempts = " + checkerAttempts);
			checkerAttempts++;
			if(checkerAttempts == 20)
			    {	// Just in case there is something wrong with the algorithm we don't want to get stuck in a loop
				System.out.println("in cheker attempts");
				if(isUniqueGuess(getRandGuess(ret)))
				    {
					break;
				    }
				checkerAttempts = 0;
			    }
		    }while(!isUniqueGuess(getRandCheckerGuess(ret)));
	    }
	else
	    {			// This is where all the stuff I don't feel like writing goes :)
		calcShipPosProb(); // recalculate values in targetZone
		do
		    {
			if(sequence > (targetZone.length * targetZone.length))
			    {
				System.out.println("sequence > ");
				do{}while(!isUniqueGuess(getRandGuess(ret)));
				break;
			    }
			sequence++;
			Guess tmp = new Guess();
			ret = getHighestPosition(ret);
			tmp.row = ret.row;
			tmp.column = ret.column;
			ret.row += lastShipHit.row;
			ret.column += lastShipHit.column;
			if(isUniqueGuess(ret))
			    {	// :)
				System.out.println("the guess was unique");
				break;
			    }
			targetZone[tmp.row][tmp.column] = -1;
		    }while(true);
	    }
	checkerAttempts = 0;
	return ret;
    }

    private Guess getHighestPosition(Guess g)
    {
	Guess ret = new Guess();
	int max = 0;

	System.out.println("targetZone.length = " + targetZone.length);
	for(int a = 0; a < targetZone.length; ++a)
	    {		
		for(int b = 0; b < targetZone.length; ++b)
		    {
			if(max < targetZone[a][b])
			    {
				max = targetZone[a][b];
				ret.row = a;
				ret.column = b;
			    }									
		    }
	    }

	System.out.println("max = " + max + ". ret.row = " + ret.row + ", ret.column = " + ret.column + "seq = " + sequence);
	System.out.println("");
	return ret;
    }

    private boolean notInPrevSequence(final int a, final int b, ArrayList<Guess> prev)
    {
	for(Guess p: prev)
	    {
		if((p.row == a) && p.column == b)
		    {
			System.out.println("saldkfjlkadsaflkj;l;jdsafljdsafk");
			return false;
		    }
	    }
	return true;
    }

    private void calcShipPosProb()
    {// I tried to use "Arrays.fill(targetZone, 0);" here but it resulted in an exception and I don't have time to try to figure it out :'(
	for(int a = 0; a < targetZone.length; ++a)
	    {
		for(int b = 0; b < targetZone.length; ++b)
		    {
			targetZone[a][b] = 0;
		    }
	    }
	for(Ship s: ships)
	    {
		for(int x = 0; x < targetZone.length; ++x)
		    {
			for(int y = 0; y < targetZone.length; ++y)
			    {
				if(inRange(x + s.width()) && inRange(y + s.len()))
				    {
					boolean notFiredUpon = true; // have we fired upon the positions in question?					
					for(int ax = x; ax < (x + s.width()); ++ax)
					{
					    for(int ay = y; ay < (y + s.len()); ++ay)
						{
						    //				    System.out.println("ax = " + ax + ", ay = " + ay);
						    ++targetZone[ax][ay]; // Increment position weight
						}
					}
				    }
			    }
		    }
	    }
	/*
	for(int a[]: targetZone)
	    {
		for(int b: a)
		    {
			System.out.print(b + ", ");
		    }
		System.out.println("");
		}*/
	System.out.println("hello we are in chalcShipPosProb()");
    }
    //this isn't quite right but it's alright for now :)
    /*	10, 20, 26, 28, 26, 20, 10, 
	14, 28, 36, 38, 36, 28, 14, 
	14, 28, 36, 38, 36, 28, 14, 
	14, 28, 36, 38, 36, 28, 14, 
	14, 28, 36, 38, 36, 28, 14, 
	14, 28, 36, 38, 36, 28, 14, 
	10, 20, 26, 28, 26, 20, 10,*/

    private boolean inRange(final int a)
    {
	if(a > targetZone.length || a < 0)
	    return false;
	return true;
    }

    private Guess getRandGuess(Guess g)
    {
	g.row = rand.nextInt(colLim);
	g.column = rand.nextInt(rowLim);
	return g;
    }


    private Guess getRandCheckerGuess(Guess g)
    {
        g.column = rand.nextInt(colLim - 0 + 1) + 0;
        if(g.column % 2 == 0)
            {
                do
                    {
                        g.row = rand.nextInt(rowLim - 0 + 1) + 0;
                    }while(g.row % 2 != 0);
            }
        else
            {
                do
                    {
                        g.row = rand.nextInt(rowLim - 0 + 1) + 0;
                    }while(g.row % 2 == 0);
            }
	return g;
    }

    public boolean isUniqueGuess(Guess newGuess)
    {//make sure I havent done this guess already                         
        for(Guess guess : privGuess)
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
        if(newGuess.row > rowLim || newGuess.row < 0
           || newGuess.column > colLim || newGuess.column < 0)
            {
                System.out.println("newGuess out of bounds\n\n");
                return true;
            }
        return false;
    }


    // Update info about last guess. Was it correct (answer will tell.)
    @Override
    public void update(Guess guess, Answer answer)
    {
	if(answer.isHit)
	    {
		if(isHunting)
		    lastShipHit = guess;
		isHunting = false;
		System.out.println("is a hit");
		privGuess.add(guess);
		privAnswers.add(answer);
		if(answer.toString().endsWith(" is hit and sunk."))
		    {// We have sunk one of the opponent's ships
			for(Ship s: ships)
			    {
				if(answer.toString().contains(s.name()))
				    { // Remove this ship from the ships to consider when not in hunting mode
					System.out.println(answer.toString());
					ships.remove(s);
					isHunting = true;
					break;
				    }
			    }
		    }
	    }
	else
	    {
		System.out.println("we missed :'(");
		privGuess.add(guess);
		privAnswers.add(answer);
		//do some stuff?
	    }
    }


    @Override
    public boolean noRemainingShips()
    {
	if(shipStatuses.isEmpty())
	    return true;
        return false;
    }

}
