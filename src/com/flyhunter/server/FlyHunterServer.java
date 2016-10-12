package com.flyhunter.server;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

import com.flyhunter.rmiadapter.FlyHunterClientInterface;
import com.flyhunter.rmiadapter.FlyHunterServerInterface;

public class FlyHunterServer implements FlyHunterServerInterface {

	int numOfPlayers = 0;
	int score = 0;
	HashMap<Integer, Integer> gameInfo = new HashMap<Integer, Integer>();

	protected FlyHunterServer() throws RemoteException {
		super();

	}

	@Override
	public int[] updateLocation() {

		Random ran = new Random();
		int xAxis = ran.nextInt(650) + 50;
		int yAxis = ran.nextInt(450) + 50;

		int loc[] = { xAxis, yAxis };

		return loc;

	}

	public static void main(String args[]) {

		FlyHunterServer flyHuntServer;

		FlyHunterServerInterface FlyHunterIntObject;

		// FlyHunterServerInterface FlyHunterIntObject = new FlyHunterServer() ;
		System.out.println("Starting RMI binding");
		try {
			flyHuntServer = new FlyHunterServer();
			FlyHunterIntObject = (FlyHunterServerInterface) UnicastRemoteObject
					.exportObject(flyHuntServer, 0);
			Registry registry = LocateRegistry.getRegistry();

			registry.bind("FlyHunterIntObject", FlyHunterIntObject);

		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int playerID() throws RemoteException {
		int assignedID = numOfPlayers + 1;
		numOfPlayers++;

		gameInfo.put(assignedID, 0);

		return assignedID;
	}

	@Override
	public void iHuntedTheFly(int playerID) throws RemoteException {
		// unwrap the player id and invoke gameover at clients
		System.out.println(playerID + "Killed the fly");
		int currentScore = gameInfo.get(playerID);
		currentScore++;
		gameInfo.put(playerID, currentScore);

		Registry registry = LocateRegistry.getRegistry();
		try {
			for (int i = 1; i <= numOfPlayers; i++) {

				String lookUpString = "FlyHunterClientIntObject" + i;
				FlyHunterClientInterface flyHunterClientIntObj = (FlyHunterClientInterface) registry
						.lookup(lookUpString);
				flyHunterClientIntObj.someoneWonGameOver(playerID);
			}

		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public HashMap<Integer, Integer> updateGameInfo() throws RemoteException {

		return gameInfo;
	}

}
