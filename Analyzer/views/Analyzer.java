package views;

import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
public static boolean checkFileFormat = false;
public Analyzer(Composite parent, MenuManager menuManager, StatusLineManager statusLineManager) {
	super(parent, menuManager, statusLineManager);
	//createContents();
	initAnalyzer();
}

@Override
protected void updateMenu() {

}
public void initAnalyzer(){
	if(mParser == null){
		  /*Gọi hàm khởi tạo các đối tượng Node và Packet*/
			    try {
			    	JFileChooser chooser = new JFileChooser();
					chooser.setMultiSelectionEnabled(true);
			
					chooser.showOpenDialog(null);
					File[] files = chooser.getSelectedFiles();
					if (files.length != 2) {
						JOptionPane.showMessageDialog(null, "Need import 2 files");		
						MainWindow.setAnalyzer();
					} else {
			
						if (files[0].getName().equals("Neighbors.txt")
								|| files[0].getName().equals("Neighbors.tr")) {
			
							onFileOpen(files[0].getAbsolutePath(),
									files[1].getAbsolutePath());
							checkFileFormat = true;
						} else if (files[1].getName().equals("Neighbors.txt")
								|| files[1].getName().equals("Neighbors.tr")) {
			
							onFileOpen(files[1].getAbsolutePath(),
									files[0].getAbsolutePath());
							checkFileFormat = true;	
						} else{
							JOptionPane.showMessageDialog(null,
									"Invalid file format");
							MainWindow.setAnalyzer();
						}
					}
			    } catch (IOException e1) {
					e1.printStackTrace();
					}
			    
	if(checkFileFormat)
		createContents();  
	}
//	else
//		createContents();
}
// read file load
public static void onFileOpen(String filePathNode, String filePathEvent) throws IOException {
	// TODO Auto-generated method stub
		String returnvalue = AbstractParser.getHeaderFileParser(filePathEvent);
		if(returnvalue.equals("Y")){
			mParser = new FullParser();
		}
		else{
			mParser = new EventParser();	
		}
		mParser.ConvertTraceFile(filePathNode, filePathEvent);
		System.out.println("Trace completed");
	}

public void createContents() {
//	setLayout(new GridLayout(1, false));
	setLayout(new FillLayout());
	tabFolder = new TabFolder(this, SWT.NULL);
	Tab[] tabs = new Tab[] { new ThroughputTab(this),new DelayTab(this),new HopCountTab(this),
	new EfficiencyTab(this),new EnergyTab(this),new NetworkLifeTimeTab(this),new SleepPeriodTab(this)};
	for (int i = 0; i < tabs.length; i++) {
		TabItem item = new TabItem(tabFolder, SWT.NULL);
		item.setText(tabs[i].getTabText());
		item.setControl(tabs[i].createTabFolderPage(tabFolder));
	}
	System.out.println("create content completed");
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
// public void setFocus() {
// tabFolder.setFocus();
// }

/**
* Disposes of all resources associated with a particular instance of the
* LayoutExample.
*/
	public void dispose() {
		tabFolder = null;
	}
}