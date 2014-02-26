package controllers.graphicscomponents;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import views.wissims.RulerScrolledComposite;

public class RulerController extends Canvas {

	public RulerController(Composite parent, int style) {
		super(parent, style);
		
		this.setBackground(new Color(getShell().getDisplay(), 255, 255, 255));
		
		Menu menu = new Menu(this);
		setMenu(menu);
		
		MenuItem mntmHideRuler = new MenuItem(menu, SWT.NONE);
		mntmHideRuler.setText("Hide Rulers");
		mntmHideRuler.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				RulerScrolledComposite sc = (RulerScrolledComposite) getParent();				
				sc.hideRulers();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
