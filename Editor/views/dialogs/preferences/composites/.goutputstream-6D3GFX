package views.dialogs.preferences.composites;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;

import controllers.managers.ApplicationSettings;


public class PreferenceNetworkComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferenceNetworkComposite(Composite parent, int style) {
		super(parent, style);
		
		Group grpNetwork = new Group(this, SWT.NONE);
		grpNetwork.setText("Network");
		grpNetwork.setBounds(24, 22, 337, 56);
		
		final Canvas cvsBackgroundColor = new Canvas(grpNetwork, SWT.BORDER);
		cvsBackgroundColor.setBounds(123, 16, 100, 25);
		cvsBackgroundColor.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.backgroundColor));
		cvsBackgroundColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					cvsBackgroundColor.setBackground(color);
					ApplicationSettings.backgroundColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		Label lblBackgroundColor = new Label(grpNetwork, SWT.NONE);
		lblBackgroundColor.setBounds(10, 23, 101, 15);
		lblBackgroundColor.setText("Background Color");
		
		Group grpNode = new Group(this, SWT.NONE);
		grpNode.setText("Node");
		grpNode.setBounds(24, 103, 337, 100);
		
		Label lblBoderType = new Label(grpNode, SWT.NONE);
		lblBoderType.setBounds(10, 30, 66, 15);
		lblBoderType.setText("Boder Type");
		
		Combo cbNodeBorderType = new Combo(grpNode, SWT.READ_ONLY);
		cbNodeBorderType.setItems(new String[] {"Circle", "Square"});
		cbNodeBorderType.setBounds(82, 27, 91, 23);
		cbNodeBorderType.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Combo cb = (Combo) arg0.getSource();
				
				switch (cb.getText()) {
				case "Circle":
					ApplicationSettings.nodeBorderType = ApplicationSettings.CIRCLE;
					break;
				case "Square":
					ApplicationSettings.nodeBorderType = ApplicationSettings.SQUARE;
					break;
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		switch (ApplicationSettings.nodeBorderType) {
		case ApplicationSettings.CIRCLE:
			cbNodeBorderType.select(0);
			break;
		case ApplicationSettings.SQUARE:
			cbNodeBorderType.select(1);
		}
		
		
		Label lblColor = new Label(grpNode, SWT.NONE);
		lblColor.setBounds(10, 64, 55, 15);
		lblColor.setText("Color");
		
		final Canvas cvsNodeColor = new Canvas(grpNode, SWT.BORDER);
		cvsNodeColor.setBounds(82, 60, 91, 25);
		cvsNodeColor.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.nodeColor));
		cvsNodeColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					cvsNodeColor.setBackground(color);
					ApplicationSettings.nodeColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		Label lblRadius = new Label(grpNode, SWT.NONE);
		lblRadius.setBounds(212, 64, 47, 15);
		lblRadius.setText("Radius");
		
		final Spinner spnNodeRadius = new Spinner(grpNode, SWT.BORDER | SWT.READ_ONLY);
		spnNodeRadius.setMaximum(7);
		spnNodeRadius.setMinimum(3);
		spnNodeRadius.setBounds(269, 61, 47, 22);
		spnNodeRadius.setSelection(ApplicationSettings.nodeSize / 2);
		spnNodeRadius.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationSettings.nodeSize = spnNodeRadius.getSelection() * 2;
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
