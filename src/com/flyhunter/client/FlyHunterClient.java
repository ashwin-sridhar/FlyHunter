package com.flyhunter.client;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.flyhunter.rmiadapter.FlyHunterClientInterface;
import com.flyhunter.rmiadapter.FlyHunterServerInterface;

public class FlyHunterClient implements FlyHunterClientInterface {

	private static final int NB_OF_IMAGES_PER_SECOND = 50;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private Random random = new Random();

	private double xFly = WIDTH / 2;
	private double yFly = HEIGHT / 2;

	int playerID = 0;
	boolean reinitialization = false;
	JFrame frame = new JFrame(FlyHunterClient.class.getSimpleName());

	final JLabel gameInfo = new JLabel();

	HashMap<Integer, Integer> currentGameInfo = new HashMap<Integer, Integer>();
	FlyHunterServerInterface flyHunterIntObj;

	protected void instantiateServerRemote() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			flyHunterIntObj = (FlyHunterServerInterface) registry
					.lookup("FlyHunterIntObject");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void loadGameInfo() {

		System.out
				.println("Calling instantiateServerRemote from loadGameInfo()");
		instantiateServerRemote();

		System.out.println("Loading game info..");

		try {

			currentGameInfo = flyHunterIntObj.updateGameInfo();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void initUI() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);

		loadGameInfo();

		try {
			playerID = flyHunterIntObj.playerID();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		gameInfo.setText("You are player:" + playerID);
		gameInfo.setSize(gameInfo.getPreferredSize());

		final JLabel fly = new JLabel(new ImageIcon("fliege-t20678.jpg"));
		fly.setSize(fly.getPreferredSize());

		Timer t = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int loc[] = null;
				try {
					loc = flyHunterIntObj.updateLocation();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				fly.setLocation(loc[0], loc[1]);
			}
		});

		fly.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					flyHunterIntObj.iHuntedTheFly(playerID);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		frame.setMinimumSize(new Rectangle(fly.getPreferredSize()).union(
				new Rectangle(gameInfo.getPreferredSize())).getSize());

		frame.add(gameInfo);
		frame.add(fly);

		frame.setSize(WIDTH, HEIGHT);

		frame.setVisible(true);

		setUpClientSideObject();
		t.start();
	}

	private double getNextSpeed() {
		return 2 * Math.PI * (0.5 + random.nextDouble());
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				new FlyHunterClient().initUI();

			}
		});
	}

	@Override
	public void someoneWonGameOver(int winnerID) throws RemoteException {
		// render the info screen and restart game
		System.out.println("At Client Side : Winner is " + winnerID);
		reInitUI(winnerID, playerID);

	}

	protected void reInitUI(int winnerID, int playerID) {

		System.out.println("Result received..reinitializing UI");

		final int winner = winnerID;
		final int player = playerID;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				loadGameInfo();
				JOptionPane.showMessageDialog(null, "Player:" + winner + "wins"
						+ currentGameInfo.toString(), "Game Over!",
						JOptionPane.OK_OPTION);
				JLabel resultInfo = new JLabel();

				/*
				 * gameInfo.setText("You are player:" + player +
				 * currentGameInfo.
				 * toString()+"Please close this window to continue playing");
				 * System
				 * .out.println("Player scores :"+currentGameInfo.toString() );
				 */
				// gameInfo.setSize(gameInfo.getPreferredSize());
				// frame.add(gameInfo);
				frame.validate();
				frame.repaint();

				frame.setSize(WIDTH, HEIGHT);
				// frame.setVisible(false);
				// frame.setVisible(true);

			}
		});

	}

	protected void setUpClientSideObject() {

		FlyHunterClientInterface FlyHunterIntObject;
		FlyHunterClient flyHuntClient;

		// FlyHunterServerInterface FlyHunterIntObject = new FlyHunterServer() ;
		System.out.println("Starting RMI binding at Client");
		try {
			flyHuntClient = new FlyHunterClient();
			FlyHunterIntObject = (FlyHunterClientInterface) UnicastRemoteObject
					.exportObject(flyHuntClient, 0);
			Registry registry = LocateRegistry.getRegistry();
			String clientBindingObject = "FlyHunterClientIntObject" + playerID;
			registry.bind(clientBindingObject, FlyHunterIntObject);

		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}