package views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import controllers.graphicscomponents.GHorizontalRuler;
import controllers.graphicscomponents.GVerticalRuler;
import controllers.graphicscomponents.RulerController;

public class RulerScrolledComposite extends ScrolledComposite {
	GHorizontalRuler horizontalRuler;
	GVerticalRuler verticalRuler;	
	RulerController rulerController;

	public RulerScrolledComposite(Composite parent, int style) {
		super(parent, style);
		
		this.addControlListener(new ControlAdapter() {			
			@Override
			public void controlResized(ControlEvent arg0) {
				// when the scrolled composite is resized, 3 other objects need to adapt changes: workspace, and the 2 rulers
				
				// adapts changes for workspace
				Control control = getContent();				
				if (control != null)
					((Workspace) control).adaptChanges();
				
				// adapts changes for rulers
				if (horizontalRuler != null)
					horizontalRuler.adaptChanges();
				if (verticalRuler != null)
					verticalRuler.adaptChanges();			
			}
		});
		
		this.getHorizontalBar().addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (horizontalRuler != null)
					horizontalRuler.adaptChanges();
			}
		});
		
		this.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (verticalRuler != null)
					verticalRuler.adaptChanges();
			}			
		});
	}
	
	public void initializeRulers() {
		horizontalRuler = new GHorizontalRuler(this, SWT.NONE);
		verticalRuler = new GVerticalRuler(this, SWT.NONE);
		rulerController = new RulerController(this, SWT.NONE);
		
		horizontalRuler.updateBounds();
		verticalRuler.updateBounds();
		rulerController.setBounds(0, 0, 14, 14);
		
		horizontalRuler.moveAbove(null);
		verticalRuler.moveAbove(null);
		rulerController.moveAbove(null);
	}
	
	public void updateRulers() {
		horizontalRuler.redraw();
		verticalRuler.redraw();
	}
	
	public void hideRulers() {
		horizontalRuler.setVisible(false);
		verticalRuler.setVisible(false);
		rulerController.setVisible(false);
	}
	
	public void showRulers() {
		horizontalRuler.setVisible(true);
		verticalRuler.setVisible(true);
		rulerController.setVisible(true);
	}
	
	public boolean isRulersShown() {
		return (horizontalRuler.isVisible() && verticalRuler.isVisible());
	}
}
