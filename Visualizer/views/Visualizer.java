package views;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jxl.read.biff.File;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import wissim.ui.MainViewPanel;

public class Visualizer extends MainContent {
	protected static MainViewPanel panel;

	public Visualizer(Composite parent, MenuManager menuManager,
			StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);
		// TODO Auto-generated constructor stub
		createContents();
	}

	@Override
	protected void updateMenu() {

	}

	public void createContents() {
		setLayout(new GridLayout(1, false));
		
		final Composite compos = new Composite(this, SWT.EMBEDDED);
		compos.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final java.awt.Frame frame = SWT_AWT.new_Frame(compos);
		
		panel = new MainViewPanel();
		frame.add(panel);
		//frame.setSize(panel.getWidth(), panel.getHeight());
		// ((JFrame) frame).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		 try {
			UIManager.setLookAndFeel(
			            UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				for (Thread t : Thread.getAllStackTraces().keySet()) 
				{  if (t.getState()==Thread.State.RUNNABLE) 
				     t.interrupt(); 
				} 
				
				frame.dispose();
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}
}
