package views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * MainContent is abstract class for Editor, Visualizer, Analyzer.
 * @author Trongnguyen
 */
public abstract class MainContent extends Composite {
	
	/**
	 * manager the main menu.
	 */
	protected MenuManager menuManager;
	
	/**
	 * manager the main status line.
	 */
	protected StatusLineManager statusLineManager;
	
	/**
	 * Create the composite.
	 * @param parent parent composite
	 * @param menuManager main Menu manager
	 * @param statusLineManager main status line manager
	 */
	public MainContent(final Composite parent, final MenuManager menuManager, final StatusLineManager statusLineManager) {
		super(parent, SWT.NONE);
		this.menuManager = menuManager;
		this.statusLineManager = statusLineManager;
	}
	
	/**
	 * Reset all menu items in main menu.
	 */
	final public void UpdateMenu()	{
		menuManager.removeAll();
		updateMenu();
		menuManager.update(true);
	}
	
	/**
	 * abstract method updateMenu.
	 * implement real action to update main menu's items.
	 */
	abstract void updateMenu();
}
