//package views;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.MessageFormat;
//import java.util.MissingResourceException;
//import java.util.ResourceBundle;
//import java.util.Vector;
//
//import javax.swing.JFileChooser;
//import javax.swing.JOptionPane;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CCombo;
//import org.eclipse.swt.custom.SashForm;
//import org.eclipse.swt.custom.StackLayout;
//import org.eclipse.swt.custom.StyledText;
//import org.eclipse.swt.custom.TableEditor;
//import org.eclipse.swt.events.MouseAdapter;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.events.ShellAdapter;
//import org.eclipse.swt.events.ShellEvent;
//import org.eclipse.swt.events.TraverseEvent;
//import org.eclipse.swt.events.TraverseListener;
//import org.eclipse.swt.graphics.Device;
//import org.eclipse.swt.graphics.DeviceData;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.FormAttachment;
//import org.eclipse.swt.layout.FormData;
//import org.eclipse.swt.layout.FormLayout;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.RowData;
//import org.eclipse.swt.layout.RowLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Canvas;
//import org.eclipse.swt.widgets.Combo;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.CoolBar;
//import org.eclipse.swt.widgets.CoolItem;
//import org.eclipse.swt.widgets.Dialog;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Group;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.List;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
//import org.eclipse.swt.widgets.ProgressBar;
//import org.eclipse.swt.widgets.Scale;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Slider;
//import org.eclipse.swt.widgets.TabFolder;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.swt.widgets.ToolBar;
//import org.eclipse.swt.widgets.ToolItem;
//import org.eclipse.swt.widgets.Tree;
//import org.eclipse.swt.widgets.TreeItem;
//
//import parser.AbstractParser;
//import parser.EventParser;
//import parser.FullParser;
//import main.*;
//public class Analyzer {
//public static AbstractParser mParser;
//  private TabFolder tabFolder;
//
//  /**
//* Creates an instance of a LayoutExample embedded inside the supplied
//* parent Composite.
//*
//* @param parent
//* the container of the example
//*/
//  public Analyzer(Composite parent) {
//    tabFolder = new TabFolder(parent, SWT.NULL);
//    Tab[] tabs = new Tab[] { new ThroughputTab(this),new DelayTab(this),new HopCountTab(this),
//     new EfficiencyTab(this),new EnergyTab(this),new NetworkLifeTimeTab(this),new SleepPeriodTab(this)};
//    for (int i = 0; i < tabs.length; i++) {
//      TabItem item = new TabItem(tabFolder, SWT.NULL);
//      item.setText(tabs[i].getTabText());
//      item.setControl(tabs[i].createTabFolderPage(tabFolder));
//    }
//  }
//
//  /**
//* Grabs input focus.
//*/
//  public void setFocus() {
//    tabFolder.setFocus();
//  }
//
//  /**
//* Disposes of all resources associated with a particular instance of the
//* LayoutExample.
//*/
//  public void dispose() {
//    tabFolder = null;
//  }
//
//  /**
//* Invokes as a standalone program.
//*/
//public static void main(String[] args) {
//    
//    boolean checkFileFormat = false;
//    /*Gọi hàm khởi tạo các đối tượng Node và Packet*/
//    while(!checkFileFormat){
//	    try {
//	    	JFileChooser chooser = new JFileChooser();
//			chooser.setMultiSelectionEnabled(true);
//	
//			chooser.showOpenDialog(null);
//			File[] files = chooser.getSelectedFiles();
//			if (files.length != 2) {
//				JOptionPane.showMessageDialog(null, "Need import 2 files");		
//			} else {
//	
//				if (files[0].getName().equals("Neighbors.txt")
//						|| files[0].getName().equals("Neighbors.tr")) {
//	
//					onFileOpen(files[0].getAbsolutePath(),
//							files[1].getAbsolutePath());
//					checkFileFormat = true;
//				} else if (files[1].getName().equals("Neighbors.txt")
//						|| files[1].getName().equals("Neighbors.tr")) {
//	
//					onFileOpen(files[1].getAbsolutePath(),
//							files[0].getAbsolutePath());
//					checkFileFormat = true;	
//				} else
//					JOptionPane.showMessageDialog(null,
//							"Invalid file format");
//			}	
//	    } catch (IOException e) {
//			e.printStackTrace();
//			}
//    }
//    if(checkFileFormat){
////    	Device.DEBUG = true;
//	    final Display display = new Display();
//    	final Shell shell = new Shell(display);
//	    shell.setLayout(new FillLayout());
////    	shell.setLayout(new StackLayout());
////    	shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    new Analyzer(shell);
//	    shell.setText(getResourceString("Analyzer 2.0"));
//	    shell.addShellListener(new ShellAdapter() {
//	      public void shellClosed(ShellEvent e) {
//	        Shell[] shells = display.getShells();
//	        for (int i = 0; i < shells.length; i++) {
//	          if (shells[i] != shell)
//	            shells[i].close();
//	        }
//	      }
//	    });
//	    shell.open();
//	    while (!shell.isDisposed()) {
//	      if (!display.readAndDispatch())
//	        display.sleep();
//	    }
//    }
//  }
//
//  public static void onFileOpen(String filePathNode, String filePathEvent) throws IOException {
//	// TODO Auto-generated method stub
//		String returnvalue = AbstractParser.getHeaderFileParser(filePathEvent);
//		if(returnvalue.equals("Y")){
//			mParser = new FullParser();
//		}
//		else{
//			mParser = new EventParser();	
//		}
//		mParser.ConvertTraceFile(filePathNode, filePathEvent);
//}
//
///**
//* Gets a string from the resource bundle. We don't want to crash because of
//* a missing String. Returns the key if not found.
//*/
//  public static String getResourceString(String key) {
//      return key;
//  }
//
//  /**
//* Gets a string from the resource bundle and binds it with the given
//* arguments. If the key is not found, return the key.
//*/
//  static String getResourceString(String key, Object[] args) {
//    try {
//      return MessageFormat.format(getResourceString(key), args);
//    } catch (MissingResourceException e) {
//      return key;
//    } catch (NullPointerException e) {
//      return "!" + key + "!";
//    }
//  }
//}