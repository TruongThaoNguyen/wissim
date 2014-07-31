package views.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentSkipListMap;

import models.Node;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

import views.Analyzer;
import views.Tab;
import controllers.chart2d.BarChart;
import controllers.chart2d.ChartAllNodeMultiArea;
import controllers.parser.ParserManager;


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
	protected void createChildWidgets() {
		/* Add common controls */
		super.createChildWidgets();	
		
		}

	/**
	 * Creates the control widgets.
	 */
	protected void createControlWidgets() {
		/* Controls the type of Throughput */
		// GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
		 
		Label filterByLabel=new Label(controlGroup, SWT.None);
		filterByLabel.setText("Filter by");
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
		equalLabel.setText("equals to");
		equalLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		equalCombo = new Combo(controlGroup, SWT.READ_ONLY);		 
		
		analyze = new Button(controlGroup, SWT.PUSH);
		analyze.setText("Analyze");
		analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		/* Add listener to add an element to the table */
		analyze.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (filterByCombo.getSelectionIndex() == 0)
				{
					if(equalCombo.getSelectionIndex()==-1 )
					{
						MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Let choose node!");
						dialog.open(); 
					}	
					else
					{	
						table.removeAll();
						int No = 1;						
					
						if (!equalCombo.getItem(equalCombo.getSelectionIndex()).equals("All nodes"))
						{
							String index = equalCombo.getItem(equalCombo.getSelectionIndex());
							Node node = ParserManager.getParser().getNodes().get(Integer.parseInt(index));							
							
							for (Entry<Double, Double> entry : node.getEnergy().entrySet()) 
							{
								TableItem tableItem= new TableItem(table, SWT.NONE);
								tableItem.setText(0, Integer.toString(No++));
								tableItem.setText(1, index);
								tableItem.setText(3, entry.getKey().toString());
								tableItem.setText(4, entry.getValue().toString());
							}
													
							//init line chart
							initXYseries(node.getEnergy());
						}
						else	// all nodes
						{	
							for (Node node : ParserManager.getParser().getNodes().values()) 
							{															
								TableItem tableItem= new TableItem(table, SWT.NONE);
								tableItem.setText(0, Integer.toString(No++));
								tableItem.setText(1, Integer.toString(node.getId()));
								tableItem.setText(3, (node.getEnergy().lastKey() - node.getEnergy().firstKey()) + "");
								tableItem.setText(4, (node.getEnergy().get(node.getEnergy().firstKey()) - node.getEnergy().get(node.getEnergy().lastKey())) + "");								
								xSeries=new double[0];
								ySeries=new double[0];																											
							}								
						}
					
						resetEditors();
					}
				}				
				else
				{
					if (listNodeAreas.size() == 0)
					{
						MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Let choose areas!");
						dialog.open(); 
					}
					else	// area
					{
						table.removeAll();
						listEnergyOfAreas.clear();
						listAvgEnergyOfAreas.clear();
						double areaEnergy;
						int No = 1;
						
						for (List<Node> group : listNodeAreas) 
						{						
							areaEnergy = 0;
							
							for (Node node : group) 
							{
								Double[] ti = (Double[]) node.getEnergy().keySet().toArray();
								Double[] en = (Double[]) node.getEnergy().values().toArray();
								
								TableItem tableItem = new TableItem(table, SWT.NONE);
								tableItem.setText(0, Integer.toString(No++));
								tableItem.setText(1, Integer.toString(node.getId()));
								tableItem.setText(2, Integer.toString(node.getGroupID()));
								tableItem.setText(3, (ti[ti.length - 1] - ti[0]) + "");
								tableItem.setText(4, (en[0] - en[en.length - 1]) + "");
							}
							listEnergyOfAreas.add(areaEnergy);
							listAvgEnergyOfAreas.add(areaEnergy / group.size());
							new TableItem(table, SWT.NONE);
						}
						
						Shell shell = new Shell();		
						new BarChart(shell,listEnergyOfAreas,"Total Energy");
						new BarChart(new Shell(),listAvgEnergyOfAreas,"Average Energy");						
					}
				}			
			}
		});		
		 
		/* Add common controls */
		super.createControlWidgets();			 
	}
	
	/**
	 * Set up item for equalCombo 
	 */	
	void setItemEqualCombo()
	{
		int i;
		if (filterByCombo.getSelectionIndex() == 0)
		{
			String[] itemList = new String[ParserManager.getParser().getNodes().size()+1] ; 
			if(ParserManager.getParser().getNodes().size()>0)
			{
				itemList[0]="All nodes";
				for (i=0;i<ParserManager.getParser().getNodes().size();i++){ 
					 Node node=ParserManager.getParser().getNodes().get(i);
					 itemList[i+1]=Integer.toString(node.getId());
				}
				equalCombo.setItems(itemList);
			}
		resetButton.setVisible(false);
		}
		if(filterByCombo.getSelectionIndex()==1){
		 equalCombo.setItems(new String[] {});
		 super.refreshLayoutComposite();
		 
		 ySeries = new double[ParserManager.getParser().getNodes().size()];
			 xSeries = new double[ParserManager.getParser().getNodes().size()];		
			for(int j=0;j<ParserManager.getParser().getNodes().size();j++) {
				Node node = ParserManager.getParser().getNodes().get(j);
				xSeries[j]=node.getX();
				ySeries[j]=node.getY();
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
			
	/**
	 * Create diagram.
	 * @param energy energy map
	 */
	public void initXYseries(ConcurrentSkipListMap<Double, Double> energy) {		
		xSeries = ArrayUtils.toPrimitive((Double[]) energy.keySet().toArray());
		ySeries = ArrayUtils.toPrimitive((Double[]) energy.values().toArray());
	}
	
	/**
	 * Creates the example layout.
	 */
	protected void createLayout() {
		fillLayout = new FillLayout();
		layoutComposite.setLayout(fillLayout);
		resetButton = new Button(layoutGroup, SWT.PUSH);
		resetButton.setText("Reset");
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
	protected void disposeEditors() {
		
	}

	/**
	 * Returns the layout data field names.
	 */
	protected String[] getLayoutDataFieldNames() {
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
	protected void resetEditors() {
		setLayoutState();
		refreshLayoutComposite();
		layoutComposite.layout(true);
		layoutGroup.layout(true);
	 
	}
	protected void refreshLayoutComposite() {
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
	protected void setLayoutState() {
		
	}
}

