package controllers.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.swtchart.Chart;

import controllers.Configure;
import controllers.chart2d.ChangeGraphInfo;
import views.Analyzer;

public abstract class Tab {
	/**
	* Common groups and composites. 
	*/
	Composite tabFolderPage;
	
	SashForm sash;
	
	Group layoutGroup, controlGroup, childGroup;
	
	/**
	* The composite that contains the example layout. 
	*/
	Composite layoutComposite;
	
	/**
	* Common controls for modifying the example layout. 
	*/
	String[] names;
	
	Control[] children;
	
	Button analyze,exportImage,changeInfoGraph;
	Chart chart;
	
	/**
	* Common values for working with TableEditors.
	*/
	Table table;
	double[] ySeries ;
	double[] xSeries;
	int index;
	
	TableItem newItem, lastSelected;
	
	/**
	* Controlling instance.
	*/
	final Analyzer instance;
	
	/**
	* Listeners.
	*/
	SelectionListener selectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			resetEditors();
		}
	};
	
	TraverseListener traverseListener = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
				resetEditors();
			}
		}
	};
	
	/**
	* Creates the Tab within a given instance of LayoutExample.
	*/
	public Tab(Analyzer instance) {
		this.instance = instance;
	}
	
	/**
	* Creates the "child" group. This is the group that allows you to add
	* children to the layout. It exists within the controlGroup.
	*/
	void createChildGroup() {
		childGroup = new Group(controlGroup, SWT.NONE);
		childGroup.setText(Analyzer.getResourceString("Results"));
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		childGroup.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 8;
		childGroup.setLayoutData(data);
		createChildWidgets();
	}
	
	/**
	* Creates the controls for modifying the "children" table, and the table
	* itself. Subclasses override this method to augment the standard table.
	*/
	void createChildWidgets() {
		/* Controls for adding and removing children */
		Button exportToExcel = new Button(childGroup, SWT.PUSH);
		exportToExcel.setText(Analyzer.getResourceString("Export Data"));
		exportToExcel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		/* Add listener to button analyze group */
		exportToExcel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (table.getItemCount() > 0)
				{
					FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(Configure.getDirectory());
					String[] filterExt = { "*.xls", "*.csv", "*.txt"};
					fd.setFilterExtensions(filterExt);
					String selected = fd.open();
					
					if(selected != null ){
						try {
							createFileFromTable(table,selected);
						} catch (Exception e1) {
							e1.printStackTrace();
						} 			
					}
				}
				else{
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("");
					dialog.setMessage("No data!");
					dialog.open();
				}
			}
		});
		
		/* Create the "children" table */
		table = new Table(childGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL	| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 8;
		gridData.heightHint = 150;
		table.setLayoutData(gridData);
		table.addTraverseListener(traverseListener);
		
		/* Add columns to the table */
		String[] columnHeaders = getLayoutDataFieldNames();
		for (int i = 0; i < columnHeaders.length; i++) 
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnHeaders[i]);
			if (i == 0)
				column.setWidth(30);
			else if (i == 1 || i==2 || i==3)
				column.setWidth(100);
			else
				column.pack();
		}	
	}
	
	public void createFileFromTable(Table table,String selected) throws IOException, RowsExceededException, WriteException{
		String[] columnHeaders = getLayoutDataFieldNames();
		TableItem[] items = table.getItems();
		
		if(selected.contains(".xls") || selected.contains(".csv")){
			WritableWorkbook workbook = Workbook.createWorkbook(new File(selected));
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);
	
			for (int j = 0; j < columnHeaders.length; j++) {
				Label label = new Label(j, 0,columnHeaders[j]);
				sheet.addCell(label);
			}
	
			int i=1;
			for (TableItem item : items) {
				for(int j = 0; j < columnHeaders.length; j++){ 
					Label label = new Label(j, i,item.getText(j));
					sheet.addCell(label);
				}
				i++;
			}
		
			workbook.write();
			workbook.close();
	}
	else{
		PrintWriter writer = new PrintWriter(selected, "UTF-8");
	for (int j = 0; j < columnHeaders.length; j++) {
	writer.print(columnHeaders[j]+ "\t"); 
	}
	writer.println();
	for (TableItem item : items) {
		for(int j = 0; j < columnHeaders.length; j++){ 
		writer.print(item.getText(j) + "\t");
		}
		writer.println();
	}
		writer.close();
	}
	
	System.out.println("File output complete");
	
	}

	/**
	* Creates the "control" group. This is the group on the right half of each
	* example tab. It contains controls for adding new children to the
	* layoutComposite, and for modifying the children's layout data.
	*/
	void createControlGroup() {
		controlGroup = new Group(sash, SWT.NONE);
		controlGroup.setText(Analyzer.getResourceString("Analyze"));
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		controlGroup.setLayout(layout);
		createControlWidgets();
	}
	
	/**
	* Creates the "control" widget children. Subclasses override this method to
	* augment the standard controls created.
	*/
	void createControlWidgets() {			
		createChildGroup();
	
		/* Position the sash */
		sash.setWeights(new int[] { 1, 1 });
	}
	
	/**
	* Creates the example layout. Subclasses override this method.
	*/
	void createLayout() {
		GridData grid = new GridData(GridData.VERTICAL_ALIGN_FILL);
		grid.horizontalSpan = 2;
		exportImage = new Button(layoutGroup, SWT.PUSH);
		exportImage.setText(Analyzer.getResourceString("Export to Image"));
		exportImage.setLayoutData(grid);
	
		/* export listener*/
		exportImage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (layoutComposite.getChildren().length > 0){
					FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath("D:\\");
					String[] filterExt = { "*.png","*.eps","*.jpeg" };
					fd.setFilterExtensions(filterExt);
					String selected = fd.open();

					if (selected != null ) {
						GC gc = new GC(layoutComposite.getChildren()[0]);
						Rectangle bounds = layoutComposite.getChildren()[0].getBounds();
						Image image = new Image(layoutComposite.getChildren()[0].getDisplay(), bounds);
						try {
							gc.copyArea(image, 0, 0);
							ImageLoader imageLoader = new ImageLoader();
							imageLoader.data = new ImageData[]{ image.getImageData() };
							imageLoader.save(selected, SWT.IMAGE_PNG);
						} finally {
							image.dispose();
							gc.dispose();
						}
					}
				}
				else{
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("");
					dialog.setMessage("No input!");
					dialog.open();
				}
			}
		}); 
	
		changeInfoGraph = new Button(layoutGroup, SWT.PUSH);
		changeInfoGraph.setText(Analyzer.getResourceString("Graph Properties"));
		changeInfoGraph.setLayoutData(grid);
	
		/* change info listener*/
		changeInfoGraph.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(layoutComposite.getChildren().length > 0){		
					Chart chart = (Chart)layoutComposite.getChildren()[0];
					ChangeGraphInfo.create(chart);
					layoutComposite.getChildren()[0] = ChangeGraphInfo.chartChanged;
					layoutComposite.redraw();
				}
				else{
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("");
					dialog.setMessage("No input!");
					dialog.open();
				}
			}
		});
	}
	
	/**
	* Creates the composite that contains the example layout.
	*/
	void createLayoutComposite() {
		layoutComposite = new Composite(layoutGroup, SWT.BORDER);
		layoutComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createLayout();
	}
	
	/**
	* Creates the layout group. This is the group on the left half of each
	* example tab.
	*/
	void createLayoutGroup() {
		layoutGroup = new Group(sash, SWT.NONE);
		layoutGroup.setText(Analyzer.getResourceString("Chart"));
		layoutGroup.setLayout(new GridLayout());
	
		Button loadFile = new Button(layoutGroup, SWT.PUSH);
		loadFile.setText(Analyzer.getResourceString("Load new neighbors and trace file"));
		loadFile.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	
		Button loadTraceFile = new Button(layoutGroup, SWT.PUSH);
		loadTraceFile.setText(Analyzer.getResourceString("Load new trace file"));
		loadTraceFile.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	
		Button loadNeighborFile = new Button(layoutGroup, SWT.PUSH);
		loadNeighborFile.setText(Analyzer.getResourceString("Load new neighbors file"));
		loadNeighborFile.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	
		/* Add listener to button load trace file */
		loadFile.addSelectionListener(new SelectionAdapter() {
			String filePathNode=""; 
			String filePathEvent="";
			public void widgetSelected(SelectionEvent e) {
				try {
					JOptionPane.showMessageDialog(null, "Let choose file! First, let choose neighbors file to get position of nodes.");
					try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
					{ 
						// do nothing
					}
					JFileChooser chooser = new JFileChooser();
					JFileChooser chooserTrace = new JFileChooser();
	
					chooser.showOpenDialog(null);
					File file = chooser.getSelectedFile();
					if (file != null) {
						filePathNode = file.getAbsolutePath();				
						JOptionPane.showMessageDialog(null, "Second, let choose trace file to get information of events.");	
						chooserTrace.showOpenDialog(null);
						file = chooserTrace.getSelectedFile();						
						if (file != null)
							filePathEvent = file.getAbsolutePath();
					}
					
					if (filePathEvent != "" && filePathNode != "") {
						Analyzer.onFileOpen(filePathNode, filePathEvent);
						SleepPeriodTab.initSleepTime();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	
		/* Add listener to button load trace file */
		loadTraceFile.addSelectionListener(new SelectionAdapter() {
			String filePathNode=""; 
			String filePathEvent="";
		
			public void widgetSelected(SelectionEvent e) {
				try {
					try { 
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
					} 
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException	| UnsupportedLookAndFeelException ex) 
					{
						// do nothing
					}
					
					JFileChooser chooserTrace = new JFileChooser();
	
					chooserTrace.showOpenDialog(null);
					File file = chooserTrace.getSelectedFile();
					if (file != null) {
						filePathEvent = file.getAbsolutePath();
						filePathNode = Analyzer.filePathNode;
					}
					
					if (filePathEvent != "" && filePathNode != "") {
						Analyzer.onFileOpen(filePathNode, filePathEvent);
						SleepPeriodTab.initSleepTime();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	
		/* Add listener to button load neighbor file */
		loadNeighborFile.addSelectionListener(new SelectionAdapter() {
			String filePathNode=""; 
			String filePathEvent="";
			
			public void widgetSelected(SelectionEvent e) {
				try {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
					} 
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException	| UnsupportedLookAndFeelException ex) 
					{ 
						// do nothing
					}
					JFileChooser chooserNeighbor = new JFileChooser();
	
					chooserNeighbor.showOpenDialog(null);
					File file = chooserNeighbor.getSelectedFile();
					if (file != null) {
						filePathNode = file.getAbsolutePath();
						filePathEvent = Analyzer.filePathEvent;
					}
					
					if (filePathEvent != "" && filePathNode != "") {
						Analyzer.onFileOpen(filePathNode, filePathEvent);
						SleepPeriodTab.initSleepTime();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		createLayoutComposite();
	}
	
	/**
	* Creates the tab folder page.
	* @param tabFolder
	* @return the new page for the tab folder
	*/
	public Composite createTabFolderPage(TabFolder tabFolder) {
		/* Create a two column page with a SashForm */
		tabFolderPage = new Composite(tabFolder, SWT.NULL);
		tabFolderPage.setLayout(new FillLayout());
		sash = new SashForm(tabFolderPage, SWT.HORIZONTAL);
	
		/* Create the "layout" and "control" columns */
		createLayoutGroup();
		createControlGroup();
	
		return tabFolderPage;
	}
	
	/**
	* Disposes the editors without placing their contents into the table.
	* Subclasses override this method.
	*/
	void disposeEditors() {
	}
	
	/**
	* Returns the layout data field names. Subclasses override this method.
	*/
	String[] getLayoutDataFieldNames() {
		return new String[] {};
	}
	
	/**
	* Gets the text for the tab folder item. Subclasses override this method.
	*/
	public String getTabText() {
		return "";
	}
	
	/**
	* Refreshes the composite and draws all controls in the layout example.
	*/
	void refreshLayoutComposite() {
		/* Remove children that are already laid out */
		children = layoutComposite.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].dispose();
		}
	}
	
	/**
	* Takes information from TableEditors and stores it. Subclasses override
	* this method.
	*/
	void resetEditors() {
		resetEditors(false);
	}
	
	void resetEditors(boolean tab) {
	}
	
	/**
	* Sets the layout data for the children of the layout. Subclasses override
	* this method.
	*/
	void setLayoutData() {
	}
	
	/**
	* Sets the state of the layout. Subclasses override this method.
	*/
	void setLayoutState() {
	}
}
