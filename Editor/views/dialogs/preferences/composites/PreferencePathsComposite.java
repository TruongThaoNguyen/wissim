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


public class PreferencePathsComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferencePathsComposite(Composite parent, int style) {
		super(parent, style);
		
		Group grpGreedy = new Group(this, SWT.NONE);
		grpGreedy.setText("Greedy Path");
		grpGreedy.setBounds(10, 10, 368, 55);
		
		Label lblColor = new Label(grpGreedy, SWT.NONE);
		lblColor.setBounds(10, 25, 55, 15);
		lblColor.setText("Color");
		
		final Canvas greedyColorCanvas = new Canvas(grpGreedy, SWT.BORDER);
		greedyColorCanvas.setBounds(71, 20, 100, 25);
		greedyColorCanvas.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.greedyPathColor));
		greedyColorCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					greedyColorCanvas.setBackground(color);
					ApplicationSettings.greedyPathColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		final Spinner spnGreedyThickness = new Spinner(grpGreedy, SWT.BORDER | SWT.READ_ONLY);
		spnGreedyThickness.setMaximum(3);
		spnGreedyThickness.setMinimum(1);
		spnGreedyThickness.setSelection(ApplicationSettings.greedyPathThickness);
		spnGreedyThickness.setBounds(296, 21, 47, 22);
		spnGreedyThickness.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationSettings.greedyPathThickness = spnGreedyThickness.getSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label lblThickness = new Label(grpGreedy, SWT.NONE);
		lblThickness.setBounds(221, 25, 55, 15);
		lblThickness.setText("Thickness");
		
		Group grpShortest = new Group(this, SWT.NONE);
		grpShortest.setText("Shortest Path");
		grpShortest.setBounds(10, 80, 368, 55);
		
		Label label = new Label(grpShortest, SWT.NONE);
		label.setText("Color");
		label.setBounds(10, 25, 55, 15);
		
		final Canvas shortestColorCanvas = new Canvas(grpShortest, SWT.BORDER);
		shortestColorCanvas.setBounds(71, 20, 100, 25);
		shortestColorCanvas.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.shortestPathColor));
		shortestColorCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					shortestColorCanvas.setBackground(color);
					ApplicationSettings.shortestPathColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		final Spinner spnShortestThickness = new Spinner(grpShortest, SWT.BORDER | SWT.READ_ONLY);
		spnShortestThickness.setMaximum(3);
		spnShortestThickness.setMinimum(1);
		spnShortestThickness.setSelection(ApplicationSettings.shortestPathThickness);
		spnShortestThickness.setBounds(294, 21, 47, 22);
		spnShortestThickness.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				ApplicationSettings.shortestPathThickness = spnShortestThickness.getSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label label_2 = new Label(grpShortest, SWT.NONE);
		label_2.setText("Thickness");
		label_2.setBounds(221, 25, 55, 15);
		
		Group grpUserDefinedPath = new Group(this, SWT.NONE);
		grpUserDefinedPath.setText("User Defined Path");
		grpUserDefinedPath.setBounds(10, 148, 368, 55);
		
		Label label_1 = new Label(grpUserDefinedPath, SWT.NONE);
		label_1.setText("Color");
		label_1.setBounds(10, 25, 55, 15);
		
		final Canvas userDefinedCanvas = new Canvas(grpUserDefinedPath, SWT.BORDER);
		userDefinedCanvas.setBounds(72, 20, 100, 25);
		userDefinedCanvas.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.userDefinedPathColor));
		userDefinedCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RGB rgbColor = new ColorDialog(getShell()).open();
				
				if (rgbColor != null) {
					Color color = new Color(getParent().getDisplay(), rgbColor.red, rgbColor.green, rgbColor.blue);
					userDefinedCanvas.setBackground(color);
					ApplicationSettings.userDefinedPathColor = ApplicationSettings.colorSWTtoAWT(color);					
				}
			}
		});
		
		final Spinner spnUserDefinedThickness = new Spinner(grpUserDefinedPath, SWT.BORDER | SWT.READ_ONLY);
		spnUserDefinedThickness.setMaximum(3);
		spnUserDefinedThickness.setMinimum(1);
		spnUserDefinedThickness.setSelection(ApplicationSettings.userDefinedPathThickness);
		spnUserDefinedThickness.setBounds(296, 21, 47, 22);
		spnUserDefinedThickness.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				ApplicationSettings.userDefinedPathThickness = spnUserDefinedThickness.getSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label label_3 = new Label(grpUserDefinedPath, SWT.NONE);
		label_3.setText("Thickness");
		label_3.setBounds(221, 25, 55, 15);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
