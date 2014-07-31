package views;

import java.util.MissingResourceException;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import views.functions.DelayTab;
import views.functions.EfficiencyTab;
import views.functions.EnergyTab;
import views.functions.HopCountTab;

import com.ibm.icu.text.MessageFormat;

/**
 * main Control for Analyzer.
 * @author Nghia Nguyen
 *
 */
public class Analyzer extends MainContent {
	
	/**
	 * tab folder to control all function tab.
	 */
	private TabFolder tabFolder;
	
	/**
	 * Create new Analyzer.
	 * @param parent main composite
	 * @param menuManager main menu manager
	 * @param statusLineManager main status line manager
	 */
	public Analyzer(final Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);					
		createContents();
	}
	
	@Override
	protected void updateMenu() {
	
	}
	
	/**
	 * Create function tabs.
	 */
	public void createContents() {
		setLayout(new FillLayout());
		tabFolder = new TabFolder(this, SWT.NULL);
		Tab[] tabs = new Tab[] 
		{ 
	//		new ThroughputTab(this),
			new DelayTab(this),
			new HopCountTab(this),
			new EfficiencyTab(this),
			new EnergyTab(this),
	//		new NetworkLifeTimeTab(this),
	//		new SleepPeriodTab(this)
		};
		
		for (int i = 0; i < tabs.length; i++) 
		{
			TabItem item = new TabItem(tabFolder, SWT.NULL);
			item.setText(tabs[i].getTabText());
			item.setControl(tabs[i].createTabFolderPage(tabFolder));
		}
	}	
	
	/**
	* Gets a string from the resource bundle and binds it with the given
	* arguments. If the key is not found, return the key.
	* @return string
	*/
	public static String getResourceString(String key, Object[] args) {
		try {
			return MessageFormat.format(key, args);
		}
		catch (MissingResourceException e) {
			return key;
		} 
		catch (NullPointerException e) {	
			return "!" + key + "!";
		}
	}
	
	/**
	* Disposes of all resources associated with a particular instance of the
	* LayoutExample.
	*/
	public final void dispose() {
		tabFolder = null;
	}
}