package gui;/*
 *  constructs a prototype Lanes.Lanes View
 *
 */

import Lanes.Lane;
import Lanes.LaneEvent;
import Lanes.LaneObserver;
import Simulation.Bowler;
import Simulation.CustomIterator;
import Simulation.Party;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class LaneView implements LaneObserver, ActionListener {

	private int roll;
	private boolean initDone = true;

	JFrame frame;
	Container cpanel;
	ArrayList bowlers;
	int cur;
	Iterator bowlIt;

	JPanel[][] balls;
	JLabel[][] ballLabel;
	JPanel[][] scores;
	JLabel[][] scoreLabel;
	JPanel[][] ballGrid;
	JPanel[] pins;

	JButton maintenance;
	Lane lane;

	public LaneView(Lane lane, int laneNum) {

		this.lane = lane;

		initDone = true;
		frame = new JFrame("Lanes " + laneNum + ":");
		cpanel = frame.getContentPane();
		cpanel.setLayout(new BorderLayout());

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.hide();
			}
		});

		cpanel.add(new JPanel());

	}

	public void show() {
		frame.show();
	}

	public void hide() {
		frame.hide();
	}

	private JPanel makeFrame(Party party) {

		initDone = false;
		bowlers = party.getMembers();
		int numBowlers = bowlers.size();

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));

		balls = new JPanel[numBowlers][23];
		ballLabel = new JLabel[numBowlers][23];
		scores = new JPanel[numBowlers][10];
		scoreLabel = new JLabel[numBowlers][10];
		ballGrid = new JPanel[numBowlers][10];
		pins = new JPanel[numBowlers];

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != 23; j++) {
				ballLabel[i][j] = new JLabel(" ");
				balls[i][j] = new JPanel();
				balls[i][j].setBorder(
					BorderFactory.createLineBorder(Color.BLACK));
				balls[i][j].add(ballLabel[i][j]);
			}
		}

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != 9; j++) {
				ballGrid[i][j] = new JPanel();
				ballGrid[i][j].setLayout(new GridLayout(0, 3));
				ballGrid[i][j].add(new JLabel("  "), BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j], BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j + 1], BorderLayout.EAST);
			}
			int j = 9;
			ballGrid[i][j] = new JPanel();
			ballGrid[i][j].setLayout(new GridLayout(0, 3));
			ballGrid[i][j].add(balls[i][2 * j]);
			ballGrid[i][j].add(balls[i][2 * j + 1]);
			ballGrid[i][j].add(balls[i][2 * j + 2]);
		}

		for (int i = 0; i != numBowlers; i++) {
			pins[i] = new JPanel();
			pins[i].setBorder(
				BorderFactory.createTitledBorder(
					((Bowler) bowlers.get(i)).getNick()));
			pins[i].setLayout(new GridLayout(0, 10));
			for (int k = 0; k != 10; k++) {
				scores[i][k] = new JPanel();
				scoreLabel[i][k] = new JLabel("  ", SwingConstants.CENTER);
				scores[i][k].setBorder(
					BorderFactory.createLineBorder(Color.BLACK));
				scores[i][k].setLayout(new GridLayout(0, 1));
				scores[i][k].add(ballGrid[i][k], BorderLayout.EAST);
				scores[i][k].add(scoreLabel[i][k], BorderLayout.SOUTH);
				pins[i].add(scores[i][k], BorderLayout.EAST);
			}
			panel.add(pins[i]);
		}

		initDone = true;
		return panel;
	}

	public void receiveLaneEvent(LaneEvent le) {
		if (lane.isPartyAssigned()) {
			int numBowlers = le.getParty().getMembers().size();
			while (!initDone) {
				//System.out.println("chillin' here.");
				try {
					Thread.sleep(1);
				} catch (Exception e) {
				}
			}

			if (le.getFrameNum() == 1
				&& le.getBall() == 0
				&& le.getIndex() == 0) {
				System.out.println("Making the frame.");
				cpanel.removeAll();
				cpanel.add(makeFrame(le.getParty()), "Center");

				// Button Panel
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new FlowLayout());

				Insets buttonMargin = new Insets(4, 4, 4, 4);

				maintenance = new JButton("Maintenance Call");
				JPanel maintenancePanel = new JPanel();
				maintenancePanel.setLayout(new FlowLayout());
				maintenance.addActionListener(this);
				maintenancePanel.add(maintenance);

				buttonPanel.add(maintenancePanel);

				cpanel.add(buttonPanel, "South");

				frame.pack();

			}

			int[][] lescores = le.getCumulScore();


			//new Iterator functionality
			Party currentParty = le.getParty();
			CustomIterator partyIterator = currentParty.getIterator();
			partyIterator.process(le, ballLabel, scoreLabel);
			//now the ballLabel gui should be updated
			//all code for ballLabel below is not necessary, but the scoreLabel still needs to be updated


//			for (int k = 0; k < numBowlers; k++) { //for all bowlers in the party
//				for (int i = 0; i <= le.getFrameNum() - 1; i++) { //for all frames for each bowler
//					if (lescores[k][i] != 0) { //if their score for the frame is not 0
//						scoreLabel[k][i].setText(
//						(new Integer(lescores[k][i])).toString()); //set their score to the correct number
//					}
//				}
//				for (int i = 0; i < 21; i++) { //look through bowlers' scores for each frame
//					//System.out.println(((int[]) ((HashMap) le.getScore()).get(bowlers.get(k)))[i]);
//					if (((int[]) ((HashMap) le.getScore())
//						.get(bowlers.get(k)))[i]
//						!= -1)
//						if (((int[]) ((HashMap) le.getScore())
//							.get(bowlers.get(k)))[i]
//							== 10
//							&& (i % 2 == 0 || i == 19))
//							ballLabel[k][i].setText("X");
//						else if (
//							i > 0
//								&& ((int[]) ((HashMap) le.getScore())
//									.get(bowlers.get(k)))[i]
//									+ ((int[]) ((HashMap) le.getScore())
//										.get(bowlers.get(k)))[i
//									- 1]
//									== 10
//								&& i % 2 == 1)
//							ballLabel[k][i].setText("/");
//						else if ( ((int[])((HashMap) le.getScore()).get(bowlers.get(k)))[i] == -2 ){
//							System.out.println("Set to F");
//							ballLabel[k][i].setText("F");
//						} else {
//							ballLabel[k][i].setText(
//									(new Integer(((int[]) ((HashMap) le.getScore())
//											.get(bowlers.get(k)))[i]))
//											.toString());
//						}
//				}
//			}

		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(maintenance)) {
			lane.pauseGame();
		}
	}

}
