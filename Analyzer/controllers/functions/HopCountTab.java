package controllers.functions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;

import models.Node;
import models.Packet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

import views.Analyzer;
import views.Tab;

import com.ibm.icu.text.DecimalFormat;

import controllers.chart2d.ChartAllNode;
import controllers.parser.ParserManger;

/**
 * Hop-count calculator tab.
 * @author nghia nguyen
 *
 */
public class HopCountTab extends Tab implements Observer {
	FillLayout fillLayout;
	Text avgText,variantText,maxText,minText;
	Combo filterByCombo,fromCombo,toCombo; 
	ArrayList<Node> listNodeAreaSource,listNodeAreaDest;
	ChartAllNode chartAllNode;
	
	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	public HopCountTab(Analyzer instance) {
		super(instance);
		listNodeAreaSource = new ArrayList<Node>();
		listNodeAreaDest = new ArrayList<Node>();
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	protected void createChildWidgets() {
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
		
		Label lblVariant = new Label(childGroup, SWT.NONE);
		lblVariant.setText("Variant");
		lblVariant.setLayoutData(gridData);
		variantText = new Text(childGroup, SWT.BORDER);
		variantText.setEditable(false);
		variantText.setLayoutData(gridData);
		
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
				setItemFromComboToCombo();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {				 
			}
		});
		
		Label fromLabel=new Label(controlGroup, SWT.None);
		fromLabel.setText("From");
		fromLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		fromCombo = new Combo(controlGroup, SWT.READ_ONLY);
		
		Label toLabel=new Label(controlGroup, SWT.None);
		toLabel.setText("To");
		toLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		toCombo = new Combo(controlGroup, SWT.READ_ONLY);
		
		analyze = new Button(controlGroup, SWT.PUSH);
		analyze.setText("Analyze");
		analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
			
		/* Add listener to add an element to the table */
		analyze.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				if (fromCombo.getSelectionIndex() == -1 || toCombo.getSelectionIndex() == -1)
				{
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("Let choose source node and destination node!");
					dialog.open(); 
				}
				else
				{	
					table.removeAll();
					int No = 1;
					int maxHopCount = 0;
					int minHopCount = Integer.MAX_VALUE;
					double totalHopCount = 0;
					double totalTime=0;
					
					LinkedHashMap<Packet,Integer> listHopCountPacket = new LinkedHashMap<Packet,Integer>();
					ArrayList<Packet> listPacket = new ArrayList<Packet>();
				
					for (Packet packet : listPacket) 
					{
						if (!fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") && !toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if (fromCombo.getItem(fromCombo.getSelectionIndex()).equals((packet.getSourceNode().getId())) 
								&& toCombo.getItem(toCombo.getSelectionIndex()).equals((packet.getDestNode().getId())) && packet.isSuccess())
							{
								listPacket.add(packet);
							}
						}
						if (!fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") 
							&& toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if (fromCombo.getItem(fromCombo.getSelectionIndex()).equals(packet.getSourceNode().getId()) 
							&& packet.isSuccess())
							{
								listPacket.add(packet);
							}
						}
						
						if (fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") 
							&& !toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if (toCombo.getItem(toCombo.getSelectionIndex()).equals((packet.getDestNode().getId())) && packet.isSuccess() )
							{
								listPacket.add(packet);
							}
						}
						if (fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") 
							 && toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if (packet.isSuccess())
							{
							 listPacket.add(packet);
							}
						}	
					}
					for(int i =0 ;i < listPacket.size(); i++)
					{
						Packet packet = listPacket.get(i);
						TableItem tableItem= new TableItem(table, SWT.NONE);
						tableItem.setText(0,Integer.toString(No++));
						tableItem.setText(1,packet.getId() + "");
						tableItem.setText(2,Integer.toString(packet.getListNodes().size()-1));
						tableItem.setText(3,packet.getSourceNode().getId() +"---"+packet.getDestNode().getId());
					 totalHopCount+=packet.getListNodes().size()-1;
					 totalTime+=(Double.parseDouble(packet.getEndTime())-Double.parseDouble(packet.getStartTime()));
					 listHopCountPacket.put(packet,packet.getListNodes().size()-1);
					
					 if(maxHopCount < packet.getListNodes().size()-1)
						 maxHopCount = packet.getListNodes().size()-1;
					 if(minHopCount > packet.getListNodes().size()-1)
						 minHopCount = packet.getListNodes().size()-1;
				}
				if(No==1){
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("No packet from node "+fromCombo.getItem(fromCombo.getSelectionIndex())+
							" to node "+toCombo.getItem(toCombo.getSelectionIndex())+"!");
						dialog.open(); 
						avgText.setText("0");
					variantText.setText("0");
					maxText.setText("0");
					minText.setText("0");
					xSeries=new double[0];
					ySeries=new double[0];
				}
				else{
					DecimalFormat df = new DecimalFormat("0.00");
					//System.out.println(No-1);
					String str= df.format(totalHopCount/(No-1));
					//set mean
					avgText.setText(str);
					//set text variant
					variantText.setText(df.format(variancesHopCount(listHopCountPacket,totalTime))); 
					maxText.setText(Integer.toString(maxHopCount));
					minText.setText(Integer.toString(minHopCount));
					//init line chart
						initXYseries(listHopCountPacket);
						
					}
					if(filterByCombo.getSelectionIndex()==0)
						resetEditors();
				}
					
				}
			});		
		 
		Button analyzeGroup = new Button(controlGroup, SWT.PUSH);
		analyzeGroup.setText("Analyze Group");
		analyzeGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		/* Add listener to button analyze group */
		analyzeGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(listNodeAreaDest.size()>0 && listNodeAreaSource.size()>0){
						setUpInfoGroupHopCount();
					}
			}
		});
		
		/* Add common controls */
		super.createControlWidgets();
	}
	
	/* Set up item for fromCombo and toCombo */
	void setItemFromComboToCombo() {
		if (filterByCombo.getSelectionIndex() == 0)
		{
			String[] itemList = new String[ParserManger.getParser().getNodes().size() + 1] ; 
			if (ParserManger.getParser().getNodes().size() > 0)
			{				
				fromCombo.add("All nodes");
				toCombo.add("All nodes");
				
				for (Node node : ParserManger.getParser().getNodes().values()) 
				{
					fromCombo.add(node.getId() + "");
					toCombo.add(node.getId() + "");
				}
			}
		}
		
		if (filterByCombo.getSelectionIndex() == 1)
		{
			super.refreshLayoutComposite();
			fromCombo.setItems(new String[] {});
			toCombo.setItems(new String[] {});
			 
			ySeries = new double[ParserManger.getParser().getNodes().size()];
			xSeries = new double[ParserManger.getParser().getNodes().size()];		
			for (int i = 0; i < ParserManger.getParser().getNodes().size(); i++)
			{
				Node node = ParserManger.getParser().getNodes().get(i);
				xSeries[i]=node.getX();
				ySeries[i]=node.getY();
			}
			chartAllNode = new ChartAllNode(xSeries, ySeries);
			chartAllNode.addObserver(this);
			chartAllNode.createChart(layoutComposite);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof ChartAllNode ) {
					this.listNodeAreaSource=((ChartAllNode) arg0).listNodeAreaSource; 
				this.listNodeAreaDest=((ChartAllNode) arg0).listNodeAreaDest; 
		}
		if(this.listNodeAreaDest.size()>0 && this.listNodeAreaSource.size()>0){
			setUpInfoGroupHopCount();
		}	
	}
	
	public void setUpInfoGroupHopCount(){
		String[] itemListSource=new String[this.listNodeAreaSource.size()] ; 
		String[] itemListDest=new String[this.listNodeAreaDest.size()] ;	
			for (int i=0;i<this.listNodeAreaSource.size();i++){ 
				 Node node=this.listNodeAreaSource.get(i);
				 itemListSource[i]=Integer.toString(node.getId());
			}
			fromCombo.setItems(itemListSource);
			for (int i=0;i<this.listNodeAreaDest.size();i++){ 
				 Node node=this.listNodeAreaDest.get(i);
				 itemListDest[i]=Integer.toString(node.getId());
			}
			toCombo.setItems(itemListDest);
		
		table.removeAll();
			int No=1;
			int maxHopCount=0;
			int minHopCount=1000000000;
			double totalHopCount=0;
			double totalTime=0;
			LinkedHashMap<Packet,Integer> listHopCountPacket = new LinkedHashMap<Packet,Integer>();
			for (int i=0;i<ParserManger.getParser().getPackets().size();i++){ 
				 Packet packet=ParserManger.getParser().getPackets().get(i);
				 for(int j=0;j<this.listNodeAreaSource.size();j++)
					 	for(int k=0;k<this.listNodeAreaDest.size();k++){
					 		 if(this.listNodeAreaSource.get(j).getId() == packet.getSourceNode().getId() 
								 && this.listNodeAreaDest.get(k).getId()== packet.getDestNode().getId() && packet.isSuccess() ) {
							 TableItem tableItem= new TableItem(table, SWT.NONE);
							 tableItem.setText(0,Integer.toString(No++));
							 tableItem.setText(1,packet.getId() + "");
							 tableItem.setText(2,Integer.toString(packet.getListNodes().size()-1));
							 tableItem.setText(3,packet.getSourceNode().getId()+"--"+packet.getDestNode().getId());
							 
							 totalHopCount+=packet.getListNodes().size()-1;
							 totalTime+=(Double.parseDouble(packet.getEndTime())-Double.parseDouble(packet.getStartTime()));
							 listHopCountPacket.put(packet,packet.getListNodes().size()-1);
							
							 if(maxHopCount < packet.getListNodes().size()-1)
								 maxHopCount = packet.getListNodes().size()-1;
							 if(minHopCount > packet.getListNodes().size()-1)
								 minHopCount = packet.getListNodes().size()-1;
					 		 }
					 	}
				 
				 
			}
			if(No==1){
				MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
				dialog.setText("Error");
				dialog.setMessage("No packet from group1 -> group2");
					dialog.open(); 
					avgText.setText("0");
				variantText.setText("0");
				maxText.setText("0");
				minText.setText("0");
			}
			else{
				DecimalFormat df = new DecimalFormat("0.00");
				//System.out.println(No-1);
				String str= df.format(totalHopCount/(No-1));
				//set mean
				avgText.setText(str);
				//set text variant
				variantText.setText(df.format(variancesHopCount(listHopCountPacket,totalTime))); 
				maxText.setText(Integer.toString(maxHopCount));
				minText.setText(Integer.toString(minHopCount));
				
			}
		//	if(filterByCombo.getSelectionIndex()==0)
		//		resetEditors();
	}

		
	public double variancesHopCount(LinkedHashMap<Packet,Integer> listHopCountPacket,Double totalTime){
		double variances=0; //	E(X*X)-E(X)*E(X)
		double expectedValue1=0; // E(X*X)=x*x*p+....
		double expectedValue2=0; // E(X)=x*p+....
		for (Packet i : listHopCountPacket.keySet()) {
						//	System.out.println( i.id +" : " + listThroughputPacket.get(i));
			expectedValue1 += listHopCountPacket.get(i)*listHopCountPacket.get(i)*
					((Double.parseDouble(i.getEndTime())-Double.parseDouble(i.getStartTime()))/totalTime);
			expectedValue2 += listHopCountPacket.get(i)*((Double.parseDouble(i.getEndTime())-Double.parseDouble(i.getStartTime()))/totalTime);
					}
		variances=expectedValue1-expectedValue2*expectedValue2;
			return variances;
	}
	
	public void initXYseries(LinkedHashMap<Packet,Integer> listHopCountPacket){
		int j=0;
		xSeries=new double[listHopCountPacket.size()];
		ySeries=new double[listHopCountPacket.size()];
		if(listHopCountPacket.size()!=0){
			for (Packet i : listHopCountPacket.keySet()) {
				ySeries[j]=listHopCountPacket.get(i);
				xSeries[j]=Double.parseDouble(i.getStartTime());
				j++;
			}
		}
	}
	
	/**
	 * Creates the example layout.
	 */
	protected void createLayout() {
		fillLayout = new FillLayout();
		layoutComposite.setLayout(fillLayout);
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
		return new String[] { "No", "Packet","Hop count","Source-Dest"};
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	public	String getTabText() {
		return "Hop count";
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
				chart.getTitle().setText("Hop count");
				chart.getAxisSet().getXAxis(0).getTitle().setText("Time(s)");
				chart.getAxisSet().getYAxis(0).getTitle().setText("Hop count");
				// create line series
				ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, "line");
				lineSeries.setYSeries(ySeries);
				lineSeries.setXSeries(xSeries);
				lineSeries.setLineStyle(LineStyle.DOT);
				// adjust the axis range
				chart.getAxisSet().adjustRange();
				
				final Composite plotArea = chart.getPlotArea();
				plotArea.addMouseMoveListener(new MouseMoveListener() {
						public void mouseMove(MouseEvent e) {
								for (ISeries series : chart.getSeriesSet().getSeries()) {
										for (int i = 0; i < series.getYSeries().length; i++) {
												Point p = series.getPixelCoordinates(i);
												double distance = Math.sqrt(Math.pow(e.x - p.x, 2)
																+ Math.pow(e.y - p.y, 2));

												if (distance < ((ILineSeries) series).getSymbolSize()) {
														setToolTipText(series,i,i,i);													
														return;
												}
										}
								}
								chart.getPlotArea().setToolTipText(null);							
						}

						private void setToolTipText(ISeries series, int xIndex,int yIndex,int id) {
								chart.getPlotArea().setToolTipText(
										"No: " + ++id + "\nTime start send: " + series.getXSeries()[xIndex] + "\nHop count: "
																+ series.getYSeries()[yIndex]);
				 
						}
				});
		}
	/**
	 * Sets the state of the layout.
	 */
	protected void setLayoutState() {
		
	}
}

