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


public class EnergyTab extends Tab implements Observer{
  
  /* The example layout instance */
  FillLayout fillLayout;
  Combo filterByCombo,equalCombo; 
  Button resetButton;
  ArrayList<ArrayList<Node>> listNodeAreas;
  ChartAllNodeMultiArea chartAllNodeEnergy;
  ArrayList<Double> listEnergyOfAreas,listAvgEnergyOfAreas;
  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  public EnergyTab(Analyzer instance) {
    super(instance);
    listNodeAreas = new ArrayList<ArrayList<Node>>();
    listEnergyOfAreas = new ArrayList<Double>(); 
    listAvgEnergyOfAreas = new ArrayList<Double>(); 
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();  
    
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
	    	if(Analyzer.mParser instanceof FullParser){
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
						//System.out.println(equalCombo.getItem(equalCombo.getSelectionIndex()));
						ArrayList<NodeEnergy> listNodeEnergy= new ArrayList<NodeEnergy>();
						
						if(!equalCombo.getItem(equalCombo.getSelectionIndex()).equals("All nodes")){
							/*
							  try {
							 
								listNodeEnergy=TraceFile.getNodeEnergy(TraceFile.getListNodes().get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex()))));
							} catch (NumberFormatException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
								for(int i=0;i<listNodeEnergy.size();i++){ 
									NodeEnergy node=listNodeEnergy.get(i);
									 TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,equalCombo.getItem(equalCombo.getSelectionIndex()));
									 tableItem.setText(3,node.getTime());
									 tableItem.setText(4,node.getEnergy());
								}
							*/	 
							
								listNodeEnergy =Analyzer.mParser.getListEnergy().get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex())));
								for(int i=0;i<listNodeEnergy.size();i++){ 
									NodeEnergy node=listNodeEnergy.get(i);
									 TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,equalCombo.getItem(equalCombo.getSelectionIndex()));
									 tableItem.setText(3,node.getTime());
									 tableItem.setText(4,node.getEnergy());
								}
								
							
							//init line chart
							initXYseries(listNodeEnergy);
						}
						else{
							FileOutputStream fos;
							try {
								fos = new FileOutputStream("DataEnergy",false);
								PrintWriter pw= new PrintWriter(fos);
								for(int i=0;i<Analyzer.mParser.getListEnergy().size();i++){ 
									listNodeEnergy =Analyzer.mParser.getListEnergy().get(i);
									 TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,Integer.toString(i));
									 tableItem.setText(3,Double.toString(Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getTime())-Double.parseDouble(listNodeEnergy.get(0).getTime())));
									 tableItem.setText(4,Double.toString(Double.parseDouble(listNodeEnergy.get(0).getEnergy())-Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy())));
									 xSeries=new double[0];
									 ySeries=new double[0];
									 
									 /*init dataEnergy*/
									 
							      pw.println(Analyzer.mParser.getListNodes().get(i).x+" "+Analyzer.mParser.getListNodes().get(i).y+" "+Double.toString(Double.parseDouble(listNodeEnergy.get(0).getEnergy())-Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy())));	        						    							
								}
							    pw.close();
							    SurfaceChartEnergy.drawChart3D();
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
														
						}
						resetEditors();
					}
	    	  }
	    	  
	    	  else{
	    		  if(listNodeAreas.size() == 0){
	    			  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Let choose areas!");
					    dialog.open(); 
	    		  }
	    		  else{
	    			  table.removeAll();
	    			  listEnergyOfAreas.clear();
	    			  listAvgEnergyOfAreas.clear();
	    			  double areaEnergy;
	    			  int No=1;
	    			  ArrayList<NodeEnergy> listNodeEnergy= new ArrayList<NodeEnergy>();
		    			  for(int i=0; i<listNodeAreas.size(); i++){
		    				  ArrayList<Node> listNodeOfOneArea = listNodeAreas.get(i);
		    				  areaEnergy = 0;
		    				  for(int j=0; j<listNodeOfOneArea.size(); j++){
		    					  listNodeEnergy = Analyzer.mParser.getListEnergy().get(listNodeOfOneArea.get(j).id);
		    					  TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,Integer.toString(listNodeOfOneArea.get(j).id));
									 tableItem.setText(2, Integer.toString(i+1));
									 tableItem.setText(3,Double.toString(Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getTime())
										                	 -Double.parseDouble(listNodeEnergy.get(0).getTime())));
									 tableItem.setText(4,Double.toString(Double.parseDouble(listNodeEnergy.get(0).getEnergy())
											                -Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy())));
		    					     areaEnergy += Double.parseDouble(listNodeEnergy.get(0).getEnergy())
		    							       -Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy());
		    				  }
		    				  listEnergyOfAreas.add(areaEnergy);
		    				  listAvgEnergyOfAreas.add(areaEnergy/listNodeOfOneArea.size());
		    				  new TableItem(table, SWT.NONE);
		    			  }
		    		  Shell shell = new Shell();	  
	    			  new BarChart(shell,listEnergyOfAreas,"Total Energy");
	    			  new BarChart(new Shell(),listAvgEnergyOfAreas,"Average Energy");
	    			  
	    		  }
	    	  }
	    	}
	    	else{
	    		MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
				dialog.setText("Error");
				dialog.setMessage("Trace file don't have energy information!");
			    dialog.open();
	    	}
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
		 chartAllNodeEnergy = new ChartAllNodeMultiArea(xSeries, ySeries);
		 chartAllNodeEnergy.addObserver(this);
		 chartAllNodeEnergy.createChart(layoutComposite);
		 //chartAllNodeEnergy.setExpandHorizontal(true);
		 resetButton.setVisible(true);
		 chartAllNodeEnergy.listNodeArea = this.listNodeAreas;
		 chartAllNodeEnergy.chartAllNode.getPlotArea().redraw();
	  }
  }
  @Override
  public void update(Observable arg0, Object arg1) {
  	if (arg0 instanceof ChartAllNodeMultiArea ) {
          this.listNodeAreas=((ChartAllNodeMultiArea) arg0).listNodeArea; 
  	}
  }
		
	
	public void initXYseries(ArrayList<NodeEnergy> listNodeEnergy){
		int j=0;
		xSeries=new double[listNodeEnergy.size()];
		ySeries=new double[listNodeEnergy.size()];
		if(listNodeEnergy.size()!=0){
			for (int i=0;i<listNodeEnergy.size();i++) {
				NodeEnergy node=listNodeEnergy.get(i);
				ySeries[j]=Double.parseDouble(node.getEnergy());
				xSeries[j]=Double.parseDouble(node.getTime());
				j++;
			}
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
	    	 listEnergyOfAreas.clear();
	    	 listNodeAreas.clear();
	         chartAllNodeEnergy.listNodeArea.clear();
	         chartAllNodeEnergy.chartAllNode.getPlotArea().redraw();
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
    return new String[] { "No", "NodeId","Group","Time","Energy" };
  }

  /**
   * Gets the text for the tab folder item.
   */
  public String getTabText() {
    return "Energy";
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
	    chart = new Chart(layoutComposite, SWT.NONE);
        chart.getTitle().setText("Energy");
        chart.getAxisSet().getXAxis(0).getTitle().setText("Time(s)");
        chart.getAxisSet().getYAxis(0).getTitle().setText("Remain Energy");
        // create line series
        ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, "line series");
        lineSeries.setYSeries(ySeries);
        lineSeries.setXSeries(xSeries);
        lineSeries.enableStep(true);
        lineSeries.setSymbolSize(3);
        lineSeries.setLineStyle(LineStyle.DOT);
        // adjust the axis range
        chart.getAxisSet().adjustRange();
        
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}

