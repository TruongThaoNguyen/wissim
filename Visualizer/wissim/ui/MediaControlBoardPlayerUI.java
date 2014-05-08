package wissim.ui;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MediaControlBoardPlayerUI extends JPanel {
	public JButton btnPlay;
	public JButton btnStop ;
	public JSlider mSliderEvent;
	public MediaContainer mediaContainer;
	public MediaControlBoardPlayerUI() {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		
		btnPlay = new JButton("Play");
		
		add(btnPlay);
		
		btnStop = new JButton("Stop");
		add(btnStop);
		
		mSliderEvent = new JSlider();
		mSliderEvent.setValue(0);
		add(mSliderEvent);
		setActionButton();
	}
	public void setUIContainer(MediaContainer mediaContainer){
		this.mediaContainer = mediaContainer;
	}
	public void setActionButton(){
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(mediaContainer != null)
					mediaContainer.play();
			}
		});
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(mediaContainer != null)
					mediaContainer.stop();
			}
		});
		mSliderEvent.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mSliderEvent.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent me) {
				// TODO Auto-generated method stub
				if(mediaContainer != null)
					mediaContainer.onSeekBar( ((JSlider)me.getSource()).getValue());
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
