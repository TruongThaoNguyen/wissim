package views.dialogs.preferences.composites;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Spinner;

import controllers.managers.ApplicationSettings;


public class PreferenceObstacleComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferenceObstacleComposite(Composite parent, int style) {
		super(parent, style);
		
		Group grpObstacle = new Group(this, SWT.NONE);
		grpObstacle.setText("Obstacle");
		grpObstacle.setBounds(28, 27, 321, 131);
		
		Label lblColor = new Label(grpObstacle, SWT.NONE);
		lblColor.setBounds(22, 29, 45, 15);
		lblColor.setText("Color");
		
		final Canvas canvas = new Canvas(grpObstacle, SWT.BORDER);
		canvas.setBounds(149, 24, 100, 25);
		canvas.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.obstacleBackgroundColor));
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					canvas.setBackground(color);
					ApplicationSettings.obstacleBackgroundColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		Label lblBorderThickness = new Label(grpObstacle, SWT.NONE);
		lblBorderThickness.setBounds(22, 67, 100, 15);
		lblBorderThickness.setText("Border Thickness");
		
		final Spinner spinner = new Spinner(grpObstacle, SWT.BORDER);
		spinner.setMaximum(3);
		spinner.setMinimum(1);
		spinner.setBounds(149, 63, 47, 22);
		spinner.setSelection(ApplicationSettings.obstacleBorderThickness);
		spinner.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationSettings.obstacleBorderThickness = spinner.getSelection();
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
