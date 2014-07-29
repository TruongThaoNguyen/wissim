package views.functions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import models.Node;
import models.Packet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.ISeries.SeriesType;

import controllers.chart2d.BarChart;
import controllers.chart2d.ChartAllNode;
import controllers.chart2d.ChartAllNodeMultiArea;
import controllers.chart3d.SurfaceChartEnergy;
import views.Analyzer;
import views.Tab;


public class SleepPeriodTab extends Tab implements Observer{
  
  /* The example layout instance */
  FillLayout fillLayout;
  Combo filterByCombo,equalCombo; 
  Button resetButton;
  Text avgText,maxText,minText;
  
  ArrayList<ArrayList<Node>> listNodeAreas;
  ChartAllNodeMultiArea chartAllNodeSleepTime;
  ArrayList<Double> listSleepTimeOfAreas,listAvgSleepTimeOfAreas;
  /*constant of network*/
  public final static double STOP_TIME = 500;
  public final static double TRANSITION_TIME = 0.0129;
  static double sleepTime[];
  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  public SleepPeriodTab(Analyzer instance) {
    super(instance);
    listNodeAreas = new ArrayList<ArrayList<Node>>();
    listSleepTimeOfAreas = new ArrayList<Double>(); 
    listAvgSleepTimeOfAreas = new ArrayList<Double>();
    initSleepTime();
  }
  static void initSleepTime(){
	  /* init sleeptime */
	    sleepTime = new double[Analyzer.mParser.getListNodes().size()];
		for(int i = 0; i< sleepTime.length; i++)
			sleepTime[i] = STOP_TIME;
		for(int i = 0; i < Analyzer.mParser.getListPacket().size(); i++){
			Packet packet = Analyzer.mParser.getListPacket().get(i);
			// node  packet 
			sleepTime[Integer.parseInt(packet.sourceID)] -= TRANSITION_TIME;								
			// 						 								
			for(int j = 0; j < packet.listNode.size(); j++){	
				Node node = packet.listNode.get(j);
//				if(packet.isSuccess){
					if(packet.listNode.indexOf(node) == packet.listNode.size()-1)
						sleepTime[node.id] -= TRANSITION_TIME;
					else 
						sleepTime[node.id] -= TRANSITION_TIME*2;	
//				}
//				else 
//					sleepTime[node.id] -= TRANSITION_TIME*2;
			}
		}
  }
  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();  
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    
    Label lblAverage = new Label(childGroup, SWT.NONE);
	lblAverage.setText("Average");
	lblAverage.setLayoutData(gridData);
	avgText = new Text(childGroup, SWT.BORDER);
	avgText.setEditable(false);
	avgText.setLayoutData(gridData);
	
	Label lblMax = new Label(childGroup, SWT.NONE);
	lblMax.setText("Max");
	lblMax.setLayoutData(gridData);
	maxText = new Text(childGroup, SWT.BORDER);
	maxText.setEditable(false);
	maxText.setLayoutData(gridData);
	
	Label lblMin = new Label(childGroup, SWT.NONE);
	lblMin.setText("Min");
	lblMin.setLayoutData(gridData);
	minText = new Text(childGroup, SWT.BORDER);
	minText.setEditable(false);
	minText.setLayoutData(gridData);
    }

  /**
   * Creates the control widgets.
   */
  void createControlWidgets() {
    /* Controls the type of Throughput */
	   // GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
	   
	    Label filterByLabel=new Label(controlGroup, SWT.None);
	    filterByLabel.setText(Analyzer.getResourceString("Filter by"));
	    filterByLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    filterByCombo = new Combo(controlGroup, SWT.READ_ONLY);
	    filterByCombo.setItems(new String[] {"Node ID", "Label ID"});
	    filterByCombo.select(0);
	    /* Add listener */
	    filterByCombo.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent e) {
	          //System.out.println(filterByCombo.getSelectionIndex());
	        	setItemEqualCombo();
	        }
	        public void widgetDefaultSelected(SelectionEvent e) {
	         // System.out.println("nghia");
	        }
	      });
	    
	    Label equalLabel=new Label(controlGroup, SWT.None);
	    equalLabel.setText(Analyzer.getResourceString("equals to"));
	    equalLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    equalCombo = new Combo(controlGroup, SWT.READ_ONLY);	   
	    setItemEqualCombo();
	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyzer.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	   
	    /* Add listener to add an element to the table */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
//	    	if(Analyzer.mParser instanceof FullParser){
	    	  if(filterByCombo.getSelectionIndex()==0){
		    		if(equalCombo.getSelectionIndex()==-1 ){
						MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
								dialog.setText("Error");
								dialog.setMessage("Let choose node!");
							    dialog.open(); 
					}
					else{	
						table.removeAll();
						int No=1;
						
						if(!equalCombo.getItem(equalCombo.getSelectionIndex()).equals("All nodes")){
							//NodeTrace node = TraceFile.getListNodes().get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex())));
							double sleepTimeOfNode;
							int nodeID = Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex()));
							sleepTimeOfNode = sleepTime[nodeID];
							
							TableItem tableItem= new TableItem(table, SWT.NONE);
							 tableItem.setText(0, Integer.toString(No++));
							 tableItem.setText(1, equalCombo.getItem(equalCombo.getSelectionIndex()));
							 tableItem.setText(3, Double.toString(sleepTimeOfNode));
							 
							 avgText.setText(Double.toString(sleepTimeOfNode));
							 maxText.setText(Double.toString(sleepTimeOfNode));
							 minText.setText(Double.toString(sleepTimeOfNode));
						}
						else{						
							double maxSleepTime = 0;
							double minSleepTime = STOP_TIME;
							double totalSleepTime = 0;
							
							for(int i = 0; i< sleepTime.length; i++){
								TableItem tableItem= new TableItem(table, SWT.NONE);
								 tableItem.setText(0, Integer.toString(No++));
								 tableItem.setText(1, Integer.toString(i));
								 tableItem.setText(3, Double.toString(sleepTime[i]));
								 
								 totalSleepTime += sleepTime[i];
								 
								 if(minSleepTime >= sleepTime[i])
									 minSleepTime = sleepTime[i];
								 if(maxSleepTime <= sleepTime[i])
									 maxSleepTime = sleepTime[i];
							}
							avgText.setText(Double.toString(totalSleepTime/(No-1)));
							maxText.setText(Double.toString(maxSleepTime));
							minText.setText(Double.toString(minSleepTime));							
						}
						//resetEditors();
					}
	    	  }
	    	  
	    	  else{
	    		  if(listNodeAreas.size() == 0){
	    			  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Let choose group!");
					    dialog.open(); 
	    		  }
	    		  else{
	    			  table.removeAll();
	    			  listSleepTimeOfAreas.clear();
	    			  listAvgSleepTimeOfAreas.clear();
	    			  double areaSleepTime;
	    			  int No=1;
		    			  for(int i=0; i<listNodeAreas.size(); i++){
		    				  ArrayList<Node> listNodeOfOneArea = listNodeAreas.get(i);
		    				  areaSleepTime = 0;
		    				  for(int j=0; j<listNodeOfOneArea.size(); j++){
		    					  TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,Integer.toString(listNodeOfOneArea.get(j).id));
									 tableItem.setText(2, Integer.toString(i+1));
									 tableItem.setText(3, Double.toString(sleepTime[listNodeOfOneArea.get(j).id]));
									
		    					     areaSleepTime += sleepTime[listNodeOfOneArea.get(j).id];
		    				  }
		    				  listSleepTimeOfAreas.add(areaSleepTime);
		    				  listAvgSleepTimeOfAreas.add(areaSleepTime/listNodeOfOneArea.size());
		    				  new TableItem(table, SWT.NONE);
		    			  }
		    		  Shell shell = new Shell();	  
	    			  new BarChart(shell,listSleepTimeOfAreas,"Total Sleep Time");
	    			  new BarChart(new Shell(),listAvgSleepTimeOfAreas,"Average Sleep Time");
	    			  avgText.setText("");
	    			  maxText.setText("");
	    			  minText.setText("");	
	    		  }
	    	  }
//	      }
	    	}
	    });    
	   
	    /* Add common controls */
	    super.createControlWidgets();

   
  }
  /* Set up item for equalCombo */
  void setItemEqualCombo(){
	  int i;
	  if(filterByCombo.getSelectionIndex()==0){
		  String[] itemList=new String[Analyzer.mParser.getListNodes().size()+1] ; 
			if(Analyzer.mParser.getListNodes().size()>0)
			{
				itemList[0]="All nodes";
				for (i=0;i<Analyzer.mParser.getListNodes().size();i++){ 
					 Node node=Analyzer.mParser.getListNodes().get(i);
					 itemList[i+1]=Integer.toString(node.id);
				}
				equalCombo.setItems(itemList);
			}
		resetButton.setVisible(false);
	  }
	  if(filterByCombo.getSelectionIndex()==1){
		 equalCombo.setItems(new String[] {});
		 super.refreshLayoutComposite();
		 
		 ySeries = new double[Analyzer.mParser.getListNodes().size()];
	     xSeries = new double[Analyzer.mParser.getListNodes().size()];    
			for(int j=0;j<Analyzer.mParser.getListNodes().size();j++) {
				Node node = Analyzer.mParser.getListNodes().get(j);
				xSeries[j]=node.x;
				ySeries[j]=node.y;
			}
		 chartAllNodeSleepTime = new ChartAllNodeMultiArea(xSeries, ySeries);
		 chartAllNodeSleepTime.addObserver(this);
		 chartAllNodeSleepTime.createChart(layoutComposite);
		 //chartAllNodeEnergy.setExpandHorizontal(true);
		 resetButton.setVisible(true);
		 chartAllNodeSleepTime.listNodeArea = this.listNodeAreas;
		 chartAllNodeSleepTime.chartAllNode.getPlotArea().redraw();
	  }
  }
  @Override
  public void update(Observable arg0, Object arg1) {
  	if (arg0 instanceof ChartAllNodeMultiArea ) {
          this.listNodeAreas=((ChartAllNodeMultiArea) arg0).listNodeArea; 
  	}
  }
		
	
  /**
   * Creates the example layout.
   */
  void createLayout() {
    fillLayout = new FillLayout();
    layoutComposite.setLayout(fillLayout);
    resetButton = new Button(layoutGroup, SWT.PUSH);
    resetButton.setText(Analyzer.getResourceString("Reset"));
    resetButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    /*Add listener to button drawChart*/
    resetButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	 listSleepTimeOfAreas.clear();
	    	 listNodeAreas.clear();
	         chartAllNodeSleepTime.listNodeArea.clear();
	         chartAllNodeSleepTime.chartAllNode.getPlotArea().redraw();
	      }
	    });
    resetButton.setVisible(false);
    /*Add Layout common*/
    super.createLayout();
  }

  /**
   * Disposes the editors without placing their contents into the table.
   */
  void disposeEditors() {
    
  }

  /**
   * Returns the layout data field names.
   */
  String[] getLayoutDataFieldNames() {
    return new String[] { "No", "NodeId","Group","Sleep Time" };
  }

  /**
   * Gets the text for the tab folder item.
   */
 public String getTabText() {
    return "Sleep Period";
  }

  /**
   * Takes information from TableEditors and stores it.
   */
  void resetEditors() {
    setLayoutState();
    refreshLayoutComposite();
    layoutComposite.layout(true);
    layoutGroup.layout(true);
  }
  void refreshLayoutComposite() {
	    super.refreshLayoutComposite();
	    
        
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}

