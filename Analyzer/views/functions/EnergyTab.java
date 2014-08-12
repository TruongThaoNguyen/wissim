package views.functions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.Node;

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
import controllers.chart3d.SurfaceChartEnergy;
import controllers.parser.ParserManager;


public class EnergyTab extends Tab implements Observer{
	
	/* The example layout instance */
	FillLayout fillLayout;
	Combo filterByCombo,equalCombo; 
	Button resetButton;
	List<List<Node>> listNodeAreas;
	ChartAllNodeMultiArea chartAllNodeEnergy;
	ArrayList<Double> listEnergyOfAreas,listAvgEnergyOfAreas;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	public EnergyTab(Analyzer instance) {
		super(instance);
		listNodeAreas = new ArrayList<List<Node>>();
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
				if (!ParserManager.getParser().getNodes().get(0).getEnergy().isEmpty())
				{
					if (filterByCombo.getSelectionIndex() == 0)	// by IDs
					{
						if (equalCombo.getSelectionIndex() == -1)
						{
							MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Let choose node!");
							dialog.open();
							return;
						}
	
						table.removeAll();
						int No = 0;
						String index = equalCombo.getItem(equalCombo.getSelectionIndex()); 
					
						if (index.equals("All nodes"))
						{						
							xSeries = new double[0];
							ySeries = new double[0];
							
							FileOutputStream fos;
							try 
							{
								fos = new FileOutputStream("DataEnergy", false);
								PrintWriter pw = new PrintWriter(fos);
								
								for (Node node : ParserManager.getParser().getNodes().values()) 
								{
									TableItem tableItem = new TableItem(table, SWT.NONE);
									tableItem.setText(0, Integer.toString(No++));
									tableItem.setText(1, Integer.toString(node.getId()));
									tableItem.setText(3, node.getEnergy().lastKey().toString());
									tableItem.setText(4, node.getEnergy().get(node.getEnergy().lastKey()).toString());										
									
									/*init dataEnergy*/									 
									pw.println(node.getX() + " " + node.getY() + " " + (node.getEnergy().get(node.getEnergy().lastKey()) - node.getEnergy().get(node.getEnergy().lastKey())));																				
								}
								pw.close();
								SurfaceChartEnergy.drawChart3D();									
							}
							catch (FileNotFoundException e1) 
							{
								e1.printStackTrace();
							}						
						}
						else	//  individual node
						{
							Node node = ParserManager.getParser().getNodes().get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex())));
							
							// initialize line chart								
							xSeries = new double[node.getEnergy().size()];
							ySeries = new double[node.getEnergy().size()];
							
							for (Double time : node.getEnergy().keySet())
							{
								Double energy = node.getEnergy().get(time);
								
								xSeries[No] = time;
								ySeries[No] = energy;									
								
								TableItem tableItem = new TableItem(table, SWT.NONE);
								tableItem.setText(0, Integer.toString(++No));
								tableItem.setText(1, index);
								tableItem.setText(3, time.toString());
								tableItem.setText(4, energy.toString());
							}
						}
						resetEditors();
					}
					else						// by label
					{
						if (listNodeAreas.size() == 0)
						{
							MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Let choose areas!");
							dialog.open(); 
						}
						else
						{
							table.removeAll();
							listEnergyOfAreas.clear();
							listAvgEnergyOfAreas.clear();
							double areaEnergy;
							int No = 1;
							
							for (List<Node> listNodeOfOneArea : listNodeAreas) 
							{							
								areaEnergy = 0;
								
								for (Node node : listNodeOfOneArea) 
								{
									TableItem tableItem= new TableItem(table, SWT.NONE);
									tableItem.setText(0, Integer.toString(No++));
									tableItem.setText(1, Integer.toString(node.getId()));
									tableItem.setText(2, Integer.toString(node.getGroupID()));
									tableItem.setText(3, node.getEnergy().lastKey().toString());
									tableItem.setText(4, node.getEnergy().get(node.getEnergy().lastKey()).toString());
							
									areaEnergy += node.getEnergy().get(node.getEnergy().firstKey()) - node.getEnergy().get(node.getEnergy().lastKey()); 
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
				else
				{
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
	
	/**
	 *  Set up item for equalCombo 
	 */
	void setItemEqualCombo()
	{
		int i = 0;
		if (filterByCombo.getSelectionIndex() == 0)
		{			
			String[] itemList = new String[ParserManager.getParser().getNodes().size() + 1] ; 
			itemList[0] = "All nodes";
					
			for (Node node : ParserManager.getParser().getNodes().values()) {			
				itemList[++i]=Integer.toString(node.getId());
			}
			equalCombo.setItems(itemList);	
			resetButton.setVisible(false);
		}
		
		if (filterByCombo.getSelectionIndex() == 1)
		{
			equalCombo.setItems(new String[] {});
			super.refreshLayoutComposite();
		 
			ySeries = new double[ParserManager.getParser().getNodes().size()];
			xSeries = new double[ParserManager.getParser().getNodes().size()];		
			
			i = 0;
			for (Node node : ParserManager.getParser().getNodes().values()) {			
				xSeries[i]=node.getX();
				ySeries[i]=node.getY();
				i++;
			}
			chartAllNodeEnergy = new ChartAllNodeMultiArea(xSeries, ySeries);
			chartAllNodeEnergy.addObserver(this);
			chartAllNodeEnergy.createChart(layoutComposite);

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
