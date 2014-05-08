package views;

import java.io.IOException;
import java.util.MissingResourceException;

import main.DelayTab;
import main.EfficiencyTab;
import main.EnergyTab;
import main.HopCountTab;
import main.NetworkLifeTimeTab;
import main.SleepPeriodTab;
import main.Tab;
import main.ThroughputTab;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import parser.AbstractParser;
import parser.EventParser;
import parser.FullParser;

import com.ibm.icu.text.MessageFormat;

import views.MainContent;

public class Analyzer extends MainContent {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public static AbstractParser mParser;
	public TabFolder tabFolder;
	public Analyzer(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);
		/*Gọi hàm khởi tạo các đối tượng Node và Packet*/
	    try {
	    	
			String returnvalue = AbstractParser.getHeaderFileParser("Trace.tr");
			if(returnvalue.equals("Y")){
				mParser = new FullParser();
			}
			else{
				mParser = new EventParser();
				
			}
			mParser.ConvertTraceFile("Neighbors.txt", "Trace.tr");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		createContents();
		
	}

	@Override
	protected void updateMenu() {
				
	}
	
	public void createContents() {
		setLayout(new GridLayout(1, false));
		tabFolder = new TabFolder(this, SWT.NULL);
	    Tab[] tabs = new Tab[] { new ThroughputTab(this),new DelayTab(this),new HopCountTab(this),
	    						new EfficiencyTab(this),new EnergyTab(this),new NetworkLifeTimeTab(this),new SleepPeriodTab(this)};
	    for (int i = 0; i < tabs.length; i++) {
	      TabItem item = new TabItem(tabFolder, SWT.NULL);
	      item.setText(tabs[i].getTabText());
	      item.setControl(tabs[i].createTabFolderPage(tabFolder));
	    }
	}
	
	/**
	   * Gets a string from the resource bundle. We don't want to crash because of
	   * a missing String. Returns the key if not found.
	   */
	  public static String getResourceString(String key) {
	      return key;
	  }

	  /**
	   * Gets a string from the resource bundle and binds it with the given
	   * arguments. If the key is not found, return the key.
	   */
	  public static String getResourceString(String key, Object[] args) {
	    try {
	      return MessageFormat.format(getResourceString(key), args);
	    } catch (MissingResourceException e) {
	      return key;
	    } catch (NullPointerException e) {
	      return "!" + key + "!";
	    }
	  }

	  /**
	   * Grabs input focus.
	   */
//	  public void setFocus() {
//	    tabFolder.setFocus();
//	  }

	  /**
	   * Disposes of all resources associated with a particular instance of the
	   * LayoutExample.
	   */
	  public void dispose() {
	    tabFolder = null;
	  }
}
