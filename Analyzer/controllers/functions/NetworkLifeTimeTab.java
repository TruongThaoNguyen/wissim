package controllers.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.rmi.CORBA.Tie;

import models.Node;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.ibm.icu.text.DecimalFormat;

import controllers.chart2d.BarChart;
import controllers.chart2d.ChartAllNodeMultiArea;
import controllers.parser.ns2parser.FullParser;
import views.Analyzer;
import views.Tab;

public class NetworkLifeTimeTab extends Tab implements Observer {
  
  /* The example layout instance */
  FillLayout fillLayout;
  Text deadPercentText,lifeTimeText,energyNodeDeadText;
  Combo filterByCombo;
  Button resetButton;
  ArrayList<ArrayList<Node>> listNodeAreas;
  ChartAllNodeMultiArea chartAllNodeLifeTime;
  ArrayList<Double> listLifeTimeOfAreas;
  final static double MAX_TIME = 100000;
  boolean checkLoadEnergy = false;

  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  public NetworkLifeTimeTab(Analyzer instance) {
    super(instance);
    listNodeAreas = new ArrayList<ArrayList<Node>>();
    listLifeTimeOfAreas = new ArrayList<Double>();
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 1;
    
    Label lblE = new Label(childGroup, SWT.NONE);
	lblE.setText("Life time =");
	lblE.setLayoutData(gridData);
	lifeTimeText = new Text(childGroup, SWT.BORDER);
	lifeTimeText.setEditable(false);
	lifeTimeText.setSize(40, 40);
	//lifeTimeText.setLayoutData(gridData);
	Label lblTime = new Label(childGroup, SWT.NONE);
	lblTime.setText("(s)");
	lblTime.setLayoutData(gridData);
	
	
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
	  	Label energyNodeDeadLabel=new Label(controlGroup, SWT.None);
	  	energyNodeDeadLabel.setText(Analyzer.getResourceString("Energy of node dead = "));
	  	energyNodeDeadLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	  	energyNodeDeadText = new Text(controlGroup, SWT.BORDER);
	  	energyNodeDeadText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	  	energyNodeDeadText.addModifyListener(new ModifyListener() {
	  			    public void modifyText(ModifyEvent e) {
	  			        checkLoadEnergy = false;
	  			       // System.out.println("Text changed");
	  			    }
	  			});
	  	Label deadPercentLabel=new Label(controlGroup, SWT.None);
	    deadPercentLabel.setText(Analyzer.getResourceString("Deadth Percent = "));
	    deadPercentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    deadPercentText = new Text(controlGroup, SWT.BORDER);
	    deadPercentText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

	    Label percentLabel=new Label(controlGroup, SWT.None);
	    percentLabel.setText(Analyzer.getResourceString("% "));
	    percentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    	    	    	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyzer.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Analyze listener  */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	  if(Analyzer.mParser instanceof FullParser){
		    	  int percentNodeDead;
		    	  table.removeAll();
		    	  int No=1;
		    	  try{
		    		  Analyzer.mParser.setEnergyNodeDead(Double.parseDouble(energyNodeDeadText.getText()));
			    	  try{
			    		 percentNodeDead= Integer.parseInt(deadPercentText.getText());
			    		 if(percentNodeDead==0 || percentNodeDead>100){
			    			 MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
								dialog.setText("Error");
								dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô phần trăm một số >0 và <=100");
							    dialog.open(); 
							    deadPercentText.setText("");
							    deadPercentText.setFocus();
			    		 } 
			    		 else{
			    			 if(!checkLoadEnergy){
			    				 Analyzer.mParser.setNetworkLifeTime();
			    				 checkLoadEnergy = true;
			    			 }		    				 
			    			 if(filterByCombo.getSelectionIndex()==0){
				    			 Analyzer.mParser.setNumberNodeDead(Analyzer.mParser.getListNodes().size()*percentNodeDead/100);
				    			 lifeTimeText.setText(" ");
					    		 for(Integer i : Analyzer.mParser.getListNodeDead().keySet()){
						    		 TableItem tableItem= new TableItem(table, SWT.NONE);
									 tableItem.setText(0,Integer.toString(No++));
									 tableItem.setText(1,Integer.toString(i));
									 tableItem.setText(2,Double.toString(Analyzer.mParser.getListNodeDead().get(i)));
									 if(No > Analyzer.mParser.getNumberNodeDead() ){
										 lifeTimeText.setText(Double.toString(Analyzer.mParser.getListNodeDead().get(i)));
										 break;
									 }
					    		 }
					    		 if(lifeTimeText.getText().equals(" "))
					    			 lifeTimeText.setText("Not die!");
			    			 }
			    			 else{
				   	    		  if(listNodeAreas.size() == 0){
				   	    			  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
				   						dialog.setText("Error");
				   						dialog.setMessage("some thing wrong!");
				   					    dialog.open(); 
				   	    		  }
				   	    		  else{				   	    			 
				   	    			  listLifeTimeOfAreas.clear();
				   	    			  double lifeTimeOneArea=0;  
				   	    			  double timeDie[];
				   	    			  int numberNodeDeadOfArea;
				   	    			  Map<Integer,Double> listNodeDeadArea;
				   		    			  for(int i=0; i<listNodeAreas.size(); i++){
				   		    				  ArrayList<Node> listNodeOfOneArea = listNodeAreas.get(i);
				   		    				  numberNodeDeadOfArea = listNodeOfOneArea.size()*percentNodeDead/100;
				   		    				  if(numberNodeDeadOfArea == 0)
				   		    					  lifeTimeOneArea = 0;
				   		    				  else{
					   		    				  timeDie = new double[listNodeOfOneArea.size()];
					   		    				  listNodeDeadArea = new LinkedHashMap<Integer,Double>();
					   		    				  for(int j=0; j<listNodeOfOneArea.size(); j++){
					   		    					  if(Analyzer.mParser.getListNodeDead().containsKey(listNodeOfOneArea.get(j).id))
					   		    						  timeDie[j] = Analyzer.mParser.getListNodeDead().get(listNodeOfOneArea.get(j).id);
					   		    					  else {
					   		    						  timeDie[j] = MAX_TIME;
					   		    						  Analyzer.mParser.getListNodeDead().put(listNodeOfOneArea.get(j).id, timeDie[j]);
					   		    					  }
					   		    					  listNodeDeadArea.put(listNodeOfOneArea.get(j).id, timeDie[j]);  
					   		    				  }
					   		    				  Arrays.sort(timeDie);
					   		    				  lifeTimeOneArea = timeDie[numberNodeDeadOfArea-1];
					   		    				  if(lifeTimeOneArea == MAX_TIME)
					   		    					  lifeTimeOneArea = -10;
					   		    				  listNodeDeadArea = Analyzer.mParser.sortByValue(listNodeDeadArea);
					   		    			      for(Integer k : listNodeDeadArea.keySet()){
						   		    				 if(listNodeDeadArea.get(k) <= lifeTimeOneArea){
						   		    				 	 TableItem tableItem= new TableItem(table, SWT.NONE);
						   		    				 	 tableItem.setText(0,Integer.toString(No++));
														 tableItem.setText(1,Integer.toString(k));
														 tableItem.setText(2,Double.toString(listNodeDeadArea.get(k)));
														 tableItem.setText(3, Integer.toString(i+1));
														 lifeTimeText.setText(" ");														 
						   		    				 }
						   		    				 else 
						   		    					 break;
						   		    			  }
				   		    				  }
				   		    				  listLifeTimeOfAreas.add(lifeTimeOneArea);
				   		    				  new TableItem(table, SWT.NONE);
				   		    			  }
				   		    		  Shell shell = new Shell();	  
				   	    			  new BarChart(shell,listLifeTimeOfAreas,"Life Time with Energy: "+
				   		    		                                       Analyzer.mParser.getEnergyNodeDead()+", Percent: "+percentNodeDead+"%");
				   	    			  
				   	    		  }
			   	    	  	}
			    			 
			    		 }
			    	  }
			    	  catch(NumberFormatException e1){
			    		  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô phần trăm một số tự nhiên");
						    dialog.open(); 
						    deadPercentText.setText("");
						    deadPercentText.setFocus();
			    	  }
		    	  }
		    	  catch(NumberFormatException e2){
		    		  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô năng lượng một số");
					    dialog.open(); 
					    energyNodeDeadText.setText("");
					    energyNodeDeadText.setFocus();
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
  void initDataFilterByComboChange(){
	  if(filterByCombo.getSelectionIndex()==0){
		resetButton.setVisible(false);
	  }
	  if(filterByCombo.getSelectionIndex()==1){
		 super.refreshLayoutComposite();
		 //listNodeAreas.clear();
		 ySeries = new double[Analyzer.mParser.getListNodes().size()];
	     xSeries = new double[Analyzer.mParser.getListNodes().size()];    
			for(int j=0;j<Analyzer.mParser.getListNodes().size();j++) {
				Node node = Analyzer.mParser.getListNodes().get(j);
				xSeries[j]=node.x;
				ySeries[j]=node.y;
			}
		 chartAllNodeLifeTime = new ChartAllNodeMultiArea(xSeries, ySeries);
		 chartAllNodeLifeTime.addObserver(this);
		 chartAllNodeLifeTime.createChart(layoutComposite);
		 resetButton.setVisible(true);
		 chartAllNodeLifeTime.listNodeArea = this.listNodeAreas;
         chartAllNodeLifeTime.chartAllNode.getPlotArea().redraw();
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
	    	 listLifeTimeOfAreas.clear();
	    	 listNodeAreas.clear();
	         chartAllNodeLifeTime.listNodeArea.clear();
	         chartAllNodeLifeTime.chartAllNode.getPlotArea().redraw();
	      }
	    });
    resetButton.setVisible(false);
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
    return new String[] { "No", "NodeID","Dead time","Group" };
  }

  /**
   * Gets the text for the tab folder item.
   */
  public String getTabText() {
    return "Network life time";
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


