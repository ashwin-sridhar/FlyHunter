package com.flyhunter.rmiadapter;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlyHunterClientInterface extends Remote {

	void someoneWonGameOver(int playerID) throws RemoteException;

}
