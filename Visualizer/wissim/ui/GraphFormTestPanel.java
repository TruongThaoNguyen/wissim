package wissim.ui;

import java.awt.Frame;

import javax.swing.JFrame;

public class GraphFormTestPanel {

	/**
	 * @param args
	 */
	protected static MainViewPanel panel;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame();
		panel = new MainViewPanel();
		frame.add(panel);
		System.out.println(panel.getWidth()+" : " +panel.getHeight());
		frame.setSize(panel.getWidth(), panel.getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);

	}

}
