package views;

import javax.swing.JFrame;

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
		java.awt.Frame frame = SWT_AWT.new_Frame(compos);
		panel = new MainViewPanel();
		frame.add(panel);
		frame.setSize(panel.getWidth(), panel.getHeight());
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);

	}
}
