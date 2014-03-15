package views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Composite;

import views.MainContent;

public class Analyzer extends MainContent {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Analyzer(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);
		
	}

	@Override
	protected void updateMenu() {
				
	}

}
