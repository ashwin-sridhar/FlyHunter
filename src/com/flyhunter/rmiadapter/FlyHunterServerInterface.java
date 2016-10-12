package com.flyhunter.rmiadapter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface FlyHunterServerInterface extends Remote {

	int[] updateLocation() throws RemoteException;

	HashMap<Integer, Integer> updateGameInfo() throws RemoteException;

	int playerID() throws RemoteException;

	void iHuntedTheFly(int playerID) throws RemoteException;

}
