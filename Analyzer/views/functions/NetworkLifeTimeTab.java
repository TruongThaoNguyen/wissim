package views.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;
import java.util.TreeMap;

import models.Node;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;

import views.Analyzer;
import views.Tab;
import controllers.chart2d.BarChart;
import controllers.chart2d.ChartAllNodeMultiArea;
import controllers.parser.ParserManager;

public class NetworkLifeTimeTab extends Tab implements Observer {
	
	/**
	 * list of areas.
	 * each area is a sublist of nodes in this area.
	 */
	private List<List<Node>> listNodeAreas;
	
	FillLayout fillLayout;
	Text deadPercentText,lifeTimeText,energyNodeDeadText;
	Combo filterByCombo;
	Button resetButton;
	
	ChartAllNodeMultiArea chartAllNodeLifeTime;
	ArrayList<Double> listLifeTimeOfAreas;
	final static double MAX_TIME = 100000;
	boolean checkLoadEnergy = false;

	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	public NetworkLifeTimeTab(Analyzer instance) {
		super(instance);
		listNodeAreas = new ArrayList<List<Node>>();
		listLifeTimeOfAreas = new ArrayList<Double>();
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	protected void createChildWidgets() {
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
	protected void createControlWidgets() 
	{
		/* Controls the type of Throughput */
		Label filterByLabel=new Label(controlGroup, SWT.None);
		filterByLabel.setText("Filter by");
		filterByLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		filterByCombo = new Combo(controlGroup, SWT.READ_ONLY);
		filterByCombo.setItems(new String[] {"All Node", "Choose Group"});
		filterByCombo.select(0);
		
		/* Add listener */
		filterByCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				initDataFilterByComboChange();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Label energyNodeDeadLabel=new Label(controlGroup, SWT.None);
		energyNodeDeadLabel.setText("Energy of node dead = ");
		energyNodeDeadLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		energyNodeDeadText = new Text(controlGroup, SWT.BORDER);
		energyNodeDeadText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		energyNodeDeadText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkLoadEnergy = false;
			}
		});
		
		Label deadPercentLabel=new Label(controlGroup, SWT.None);
		deadPercentLabel.setText("Deadth Percent = ");
		deadPercentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		deadPercentText = new Text(controlGroup, SWT.BORDER);
		deadPercentText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		Label percentLabel=new Label(controlGroup, SWT.None);
		percentLabel.setText("% ");
		percentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		analyze = new Button(controlGroup, SWT.PUSH);
		analyze.setText("Analyze");
		analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		/* Analyze listener	*/
		analyze.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {	
				CalculateLifeTime();								 								
			}			
		});
		 
		/* Add common controls */
		super.createControlWidgets();	 
	}
	
	private void CalculateLifeTime()
	{			
		table.removeAll();
		int No = 1;
		int percentNodeDead = Integer.parseInt(deadPercentText.getText());
		double energyNodeDead = Double.parseDouble(energyNodeDeadText.getText());
		
		// check condition
		if (percentNodeDead == 0 || percentNodeDead > 100)
		{
			MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô phần trăm một số >0 và <=100");
			dialog.open(); 
			deadPercentText.setText("");
			deadPercentText.setFocus();
			
			return;
		}
		
		SortedMap<Double, Node> deadNodes = new TreeMap<>();
		
		if (filterByCombo.getSelectionIndex() == 0)	// all nodes
		{						
			lifeTimeText.setText("Not die!");

			for (Node node : ParserManager.getParser().getNodes().values()) 
			{
				for (Double time : node.getEnergy().keySet()) 
				{					
					if (node.getEnergy().get(time) <= energyNodeDead)
					{
						deadNodes.put(time, node);				
						break;
					}
				}
			}
		}
		else	// selected nodes
		{
			if (listNodeAreas.size() == 0)
			{
				MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
				dialog.setText("Error");
				dialog.setMessage("some thing wrong!");
				dialog.open(); 
			}
			else
			{					 						 
				for (List<Node> area : listNodeAreas) 
				{
					for (Node node : area) 
					{
						for (Double time : node.getEnergy().keySet()) 
						{
							if (node.getEnergy().get(time) <= energyNodeDead)
							{
								deadNodes.put(time, node);
								break;
							}
						}
					}
				}
				Shell shell = new Shell();		
				new BarChart(shell, listLifeTimeOfAreas, "Life Time with Energy: " + 0 + ", Percent: " + percentNodeDead + "%");
			}
		}

		int count = percentNodeDead * ParserManager.getParser().getNodes().size() / 100;
		
		for (Double time : deadNodes.keySet()) {		
			Node node = deadNodes.get(time);
			
			TableItem  tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, Integer.toString(No++));
			tableItem.setText(1, Integer.toString(node.getId()));
			tableItem.setText(2, time.toString());
			
			if (No > count)
			{
				lifeTimeText.setText(time.toString());
				break;	
			}
		}
	}
	
	void initDataFilterByComboChange(){		
		if(filterByCombo.getSelectionIndex()==0)
		{
			resetButton.setVisible(false);
		}
		if(filterByCombo.getSelectionIndex()==1)
		{
			super.refreshLayoutComposite();

			ySeries = new double[ParserManager.getParser().getNodes().size()];
			xSeries = new double[ParserManager.getParser().getNodes().size()];		
			
			int j = 0;
			for (Node node : ParserManager.getParser().getNodes().values()) {
				xSeries[j]=node.getX();
				ySeries[j]=node.getY();
				j++;
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

	protected void createLayout() {
		fillLayout = new FillLayout();
		layoutComposite.setLayout(fillLayout);
		resetButton = new Button(layoutGroup, SWT.PUSH);
		resetButton.setText("Reset");
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
	protected void disposeEditors() {
		
	}

	/**
	 * Returns the layout data field names.
	 */
	protected String[] getLayoutDataFieldNames() {
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
	protected void resetEditors() {
		setLayoutState();
		refreshLayoutComposite();
		layoutComposite.layout(true);
		layoutGroup.layout(true);
	}
	protected void refreshLayoutComposite() {
					
		}
	/**
	 * Sets the state of the layout.
	 */
	protected void setLayoutState() {
		
	}
}


