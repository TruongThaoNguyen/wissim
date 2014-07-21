package views;

import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import views.functiontab.DelayTab;
import views.functiontab.EfficiencyTab;
import views.functiontab.EnergyTab;
import views.functiontab.HopCountTab;
import views.functiontab.NetworkLifeTimeTab;
import views.functiontab.SleepPeriodTab;
import views.functiontab.Tab;
import views.functiontab.ThroughputTab;

import com.ibm.icu.text.MessageFormat;

import controllers.parser.AbstractParser;
import controllers.parser.EventParser;
import controllers.parser.FullParser;

/**
 * main Control for Analyzer.
 * @author Nghia Nguyen
 *
 */
public class Analyzer extends MainContent {

	public static AbstractParser mParser;
	public TabFolder tabFolder;
	public static boolean checkFileFormat = false;
	public static Composite composite;
	public static String filePathNode=""; 
	public static String filePathEvent="";
	
	/**
	 * Create new Analyzer.
	 * @param parent main composite
	 * @param menuManager main menu manager
	 * @param statusLineManager main status line manager
	 */
	public Analyzer(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
		super(parent, menuManager, statusLineManager);	
		composite = parent;	
		composite.getShell().setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_ARROW));
		initAnalyzer();
	}
	
	@Override
	protected void updateMenu() {
	
	}
	
	/**
	 * Open trace files then creates function tabs.
	 */
	public void initAnalyzer(){
		if(mParser == null)
		{
		    try 
		    {
		    	JOptionPane.showMessageDialog(null, "Let choose file! First, let choose neighbors file to get position of nodes.");
		    	
		    	try 
		    	{ 
		    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		    	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException	| UnsupportedLookAndFeelException ex)
		    	{ 
		    		// DO NOTHING
		    	}
		    	
		    	JFileChooser chooser = new JFileChooser();
		    	JFileChooser chooserTrace = new JFileChooser();
		
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				if (file != null)
				{
					filePathNode = file.getAbsolutePath();				
					
					JOptionPane.showMessageDialog(null, "Second, let choose trace file to get information of events.");	
					
					chooserTrace.showOpenDialog(null);
				    file = chooserTrace.getSelectedFile();
				    if (file != null) 
				    {
						filePathEvent = file.getAbsolutePath();
					}
				}
				
			    if (filePathEvent != "" && filePathNode != "")
			    {
				    onFileOpen(filePathNode, filePathEvent);
				    checkFileFormat = true;
			    }
		    }
		    catch (IOException e1) 
		    {
				e1.printStackTrace();
			}
		    
		    if (checkFileFormat)
		    {
		    	createContents();
		    }
		}
	}
	
	/**
	 * read file load.
	 * @param filePathNode
	 * @param filePathEvent
	 * @throws IOException
	 */
	public static void onFileOpen(String filePathNode, String filePathEvent) throws IOException 
	{
		String returnvalue = AbstractParser.getHeaderFileParser(filePathEvent);
		if (returnvalue.equals("Y")) {
			mParser = new FullParser();
		} else {
			mParser = new EventParser();	
		}
		
		composite.getShell().setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_WAIT));		
		mParser.ConvertTraceFile(filePathNode, filePathEvent);
		System.out.println("Trace completed");		
		composite.getShell().setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_ARROW));
		
		MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_WORKING | SWT.OK);		
		dialog.setText("Ok");
		dialog.setMessage("Trace completed!");
		dialog.open();
	}
	
	/**
	 * Create function tabs.
	 */
	public void createContents() {
		setLayout(new FillLayout());
		tabFolder = new TabFolder(this, SWT.NULL);
		Tab[] tabs = new Tab[] 
		{ 
			new ThroughputTab(this),
			new DelayTab(this),
			new HopCountTab(this),
			new EfficiencyTab(this),
			new EnergyTab(this),
			new NetworkLifeTimeTab(this),
			new SleepPeriodTab(this)
		};
		
		for (int i = 0; i < tabs.length; i++) 
		{
			TabItem item = new TabItem(tabFolder, SWT.NULL);
			item.setText(tabs[i].getTabText());
			item.setControl(tabs[i].createTabFolderPage(tabFolder));
		}
	}
	
	/**
	* Gets a string from the resource bundle. We don't want to crash because of
	* a missing String. Returns the key if not found.
	*/
	@Deprecated
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
	public void dispose() {
			tabFolder = null;
	}
}