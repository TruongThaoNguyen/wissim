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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class MediaControlBoardPlayerUI extends JPanel {
	public JButton btnPlay;
	public JButton btnStop ;
	public JSlider mSliderEvent;
	public MediaContainer mediaContainer;
	public JTextField mTimeInfor;
	public JTextField mEventLog ;
	public JComboBox mSpeedcomboBox;

	public MediaControlBoardPlayerUI() {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		
		btnPlay = new JButton("Play");
		
		add(btnPlay);
		
		btnStop = new JButton("Stop");
		add(btnStop);
		
		mSliderEvent = new JSlider();
		mSliderEvent.setMaximum(1000);
		mSliderEvent.setValue(0);
		add(mSliderEvent);
		
		mTimeInfor = new JTextField();
		mTimeInfor.setEditable(false);
		mTimeInfor.setBackground(SystemColor.control);
		add(mTimeInfor);
		mTimeInfor.setColumns(12);
		mEventLog = new JTextField();
		
		mEventLog.setEditable(false);
		mEventLog.setBackground(SystemColor.control);
		add(mEventLog);
		mEventLog.setColumns(40);
		
		mSpeedcomboBox = new JComboBox();
		mSpeedcomboBox.setModel(new DefaultComboBoxModel(new String[] {"1.0x", "1.25x", "1.5x", "2.0x", "2.5x", "3.0x", "3.5x", "4.0x"}));
		add(mSpeedcomboBox);
		setActionButton();
	}
	public void setUIContainer(MediaContainer mediaContainer){
		this.mediaContainer = mediaContainer;
	}
	
	public JTextField getEventLog() {
		return mEventLog;
	}
	public void setActionButton(){
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(mediaContainer != null)
					mediaContainer.play();
				if(btnPlay.getText().equals("Play"))
					btnPlay.setText("Pause");
				else if(btnPlay.getText().equals("Pause"))
					btnPlay.setText("Play");
				
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
