package wissim.ui;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GraphFormTestPanel {

	/**
	 * @param args
	 */
	protected static MainViewPanel panel;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
		}
		JFrame frame = new JFrame();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		panel = new MainViewPanel();
		frame.add(panel);
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

}
