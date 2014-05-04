package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

import chart2D.BarChart;
import chart2D.ChartAllNodeMultiArea;
import chart3D.SurfaceChartEfficiency;
import chart3D.SurfaceChartThroughput;

import com.ibm.icu.text.DecimalFormat;

import parser.*;
import views.Analyzer;

public class EfficiencyTab extends Tab implements Observer{
  
  /* The example layout instance */
  FillLayout fillLayout;
  Text eText;
  Combo criteriaCombo; 
  Combo filterByCombo;
  Button resetButton;
  ArrayList<ArrayList<NodeTrace>> listNodeAreas;
  ChartAllNodeMultiArea chartAllNodeEfficiency;
  ArrayList<Double> listEfficiencyOfAreas;
  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  public EfficiencyTab(Analyzer analyzer) {
    super(analyzer);
    listNodeAreas = new ArrayList<ArrayList<NodeTrace>>();
    listEfficiencyOfAreas = new ArrayList<Double>();
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    
    Label lblE = new Label(childGroup, SWT.NONE);
	lblE.setText("E=");
	lblE.setLayoutData(gridData);
	eText = new Text(childGroup, SWT.BORDER);
	eText.setEditable(false);
	eText.setLayoutData(gridData);
	
	
  }

  /**
   * Creates the control widgets.
   */
  void createControlWidgets() {
    /* Controls the type of Throughput */
	   // GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
	    filterByCombo = new Combo(controlGroup, SWT.READ_ONLY);
	    filterByCombo.setItems(new String[] {"All Node", "Choose Group"});
	    filterByCombo.select(0);
	    /* Add listener */
	    filterByCombo.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent e) {
	          //System.out.println(filterByCombo.getSelectionIndex());
	        	initDataFilterByComboChange();
	        }
	        public void widgetDefaultSelected(SelectionEvent e) {
	        }
	      });
	    Label criteriaLabel=new Label(controlGroup, SWT.None);
	    criteriaLabel.setText(Analyzer.getResourceString("Criteria"));
	    criteriaLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    criteriaCombo = new Combo(controlGroup, SWT.READ_ONLY);
	    criteriaCombo.setItems(new String[] {"data bits transmitted/data bit delivered"});
	    criteriaCombo.select(0);
	    	    	    	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyzer.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Analyze listener  */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    		if(filterByCombo.getSelectionIndex()==0){
	    			DecimalFormat df = new DecimalFormat("0.00");
					String str= df.format(ratioDroppedPacket());
					eText.setText(str+" %");
	    		}
	    		if(filterByCombo.getSelectionIndex()==1){
	    			if(listNodeAreas.size() == 0){
		    			  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Let choose group!");
						    dialog.open(); 
		    		  }
	    			else{
	    				  table.removeAll();
	    				  listEfficiencyOfAreas.clear();
	    				  double numberDroppedPacket,numberPacketOfArea;
	    				  int No=1;
	    				  boolean checkBelongTo;
	    				 
	    				  for(int i=0; i<listNodeAreas.size(); i++){
		    				  ArrayList<NodeTrace> listNodeOfOneArea = listNodeAreas.get(i);
		    				  numberDroppedPacket = 0;
		    				  numberPacketOfArea = 0;
		    				  for (int j=0;j<TraceFile.getListPacket().size();j++){ 
		    					  Packet packet=TraceFile.getListPacket().get(j);
		    					  checkBelongTo = false;
		    					  for(int k = 0;k < listNodeOfOneArea.size(); k++){
		    						  NodeTrace node = listNodeOfOneArea.get(k);
		    						  if(node.id == Integer.parseInt(packet.sourceID)){
		    							  checkBelongTo = true;
		    							  numberPacketOfArea++;
		    							  break;
		    						  }
		    					  } 
		    					  if(checkBelongTo){
			    					  TableItem tableItem= new TableItem(table, SWT.NONE);
			    						 tableItem.setText(0,Integer.toString(No++));
			    						 tableItem.setText(1,packet.id);
			    						 tableItem.setText(2,packet.sourceID);
			    						 tableItem.setText(3,packet.size);
			    						 tableItem.setText(4,Boolean.toString(packet.isSuccess));
			    						 tableItem.setText(5, Integer.toString(i+1));
			    					  if(!packet.isSuccess)
			    						  numberDroppedPacket++;
		    					  }
		    				  }
		    				  if(numberPacketOfArea != 0){
		    					  listEfficiencyOfAreas.add((numberDroppedPacket/numberPacketOfArea)*100);
		    				  	  new TableItem(table, SWT.NONE);
		    				  }
		    				  else{
		    					  listEfficiencyOfAreas.add(-1.0);
		    				  }
	    				  }
	    				  Shell shell = new Shell();	  
		    			  new BarChart(shell,listEfficiencyOfAreas,"Efficiency (%)");
	    			}
	    		}
	    		
	      }
	    });    
	   
	    /* Add common controls */
	    super.createControlWidgets();
   
  }
  double ratioDroppedPacket(){
	  double numberDroppedPacket=0;
	  int No=1;
	  table.removeAll();
	  for (int i=0;i<TraceFile.getListPacket().size();i++){ 
		  Packet packet=TraceFile.getListPacket().get(i);
		  TableItem tableItem= new TableItem(table, SWT.NONE);
			 tableItem.setText(0,Integer.toString(No++));
			 tableItem.setText(1,packet.id);
			 tableItem.setText(2,packet.sourceID);
			 tableItem.setText(3,packet.size);
			 tableItem.setText(4,Boolean.toString(packet.isSuccess));
		  if(!packet.isSuccess)
			  numberDroppedPacket++;
	  }
	  return (numberDroppedPacket/TraceFile.getListPacket().size())*100;
  }
  void initDataFilterByComboChange(){
	  if(filterByCombo.getSelectionIndex()==0){
		resetButton.setVisible(false);
	  }
	  if(filterByCombo.getSelectionIndex()==1){
		 super.refreshLayoutComposite();
		 //listNodeAreas.clear();
		 ySeries = new double[TraceFile.getListNodes().size()];
	     xSeries = new double[TraceFile.getListNodes().size()];    
			for(int j=0;j<TraceFile.getListNodes().size();j++) {
				NodeTrace node = TraceFile.getListNodes().get(j);
				xSeries[j]=node.x;
				ySeries[j]=node.y;
			}
		 chartAllNodeEfficiency = new ChartAllNodeMultiArea(xSeries, ySeries);
		 chartAllNodeEfficiency.addObserver((Observer) this);
		 chartAllNodeEfficiency.createChart(layoutComposite);
		 resetButton.setVisible(true);
		 chartAllNodeEfficiency.listNodeArea = this.listNodeAreas;
         chartAllNodeEfficiency.chartAllNode.getPlotArea().redraw();
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
	    	 listEfficiencyOfAreas.clear();
	    	 listNodeAreas.clear();
	         chartAllNodeEfficiency.listNodeArea.clear();
	         chartAllNodeEfficiency.chartAllNode.getPlotArea().redraw();
	      }
	    });
    resetButton.setVisible(false);
    super.createLayout();
    Button drawChart3D = new Button(layoutGroup, SWT.PUSH);
    drawChart3D.setText(Analyzer.getResourceString("Draw 3Dchart"));
    drawChart3D.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    /*Add listener to button drawChart*/
    drawChart3D.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	SurfaceChartEfficiency.drawChart3D();
	      }
	    }); 
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
    return new String[] { "No", "Packet","SourceID","Size","isDropped","Group"};
  }

  /**
   * Gets the text for the tab folder item.
   */
  public String getTabText() {
    return "Efficiency";
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
	        
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}


