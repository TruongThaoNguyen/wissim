package wissim.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.View;

import wissim.controller.player.WissimPlayer;

import java.awt.FlowLayout;

public class PlayerUIController extends JPanel implements MediaContainer{
	JPanel viewPanel;
	protected View mView;
	protected Graph mGraph;
	protected MediaContainer mPlayer;
	public PlayerUIController(Graph graph,View view) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBackground(Color.WHITE);
		setLayout(null);
		
		viewPanel = new JPanel();
		viewPanel.setBounds(this.getX(), this.getY(), 800, 650);
		viewPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		viewPanel.setBackground(SystemColor.control);
		
		add(viewPanel);
		mView = view;
		mView.setSize(new Dimension(viewPanel.getWidth(), viewPanel.getHeight()));
		viewPanel.add(mView,BorderLayout.CENTER);
		viewPanel.setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
		viewPanel.setLayout(null);
		
		
		
	}

	/**
	 * 
	 */
	public void setMediaControler(MediaContainer mPlayer){
		this.mPlayer = mPlayer;
	}
	private static final long serialVersionUID = 1L;
	public JPanel getViewPanel() {
		return viewPanel;
	}

	public void setViewPanel(JPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		System.out.println("onPlay");
		mPlayer.play();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		System.out.println("onPause");
		mPlayer.pause();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		System.out.println("onStop");
		mPlayer.stop();
	}

	@Override
	public void onSeekBar(int value) {
		// TODO Auto-generated method stub
		System.out.println("onSeekBar");
		mPlayer.onSeekBar(value);
	}
	
}
