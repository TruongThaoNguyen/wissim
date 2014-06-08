package views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/*
 * Author: Trong Nguyen
 * 
 */

public class MainContent extends Composite {

	protected MenuManager menuManager;
	protected StatusLineManager statusLineManager;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param MenuManager
	 * @param StatusLineManager
	 */
	public MainContent(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, SWT.NONE);
		this.menuManager = menuManager;
		this.statusLineManager = statusLineManager;
	}
	
	public void UpdateMenu()
	{
		menuManager.removeAll();
		updateMenu();
		menuManager.update(true);
	}
	
	protected void updateMenu() {}
}
