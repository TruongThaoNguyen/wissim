package views.functiontab;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;

import models.NodeTrace;
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

import com.ibm.icu.text.DecimalFormat;

import controllers.chart2d.ChartAllNode;


public class DelayTab extends Tab implements Observer{
	
	/**
	 * The example layout instance 
	 */
	FillLayout fillLayout;
	Text avgText,variantText,maxText,minText;
	Combo filterByCombo,fromCombo,toCombo; 
	ArrayList<NodeTrace> listNodeAreaSource,listNodeAreaDest;
	ChartAllNode chartAllNode;
	
	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	public DelayTab(Analyzer instance) {
		super(instance);
		listNodeAreaSource = new ArrayList<NodeTrace>();
		listNodeAreaDest = new ArrayList<NodeTrace>();
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
	void createControlWidgets() {
		/* Controls the type of Throughput */
		Label filterByLabel=new Label(controlGroup, SWT.None);
		filterByLabel.setText(Analyzer.getResourceString("Filter by"));
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
				// do noting
			}
		});
		
		Label fromLabel=new Label(controlGroup, SWT.None);
		fromLabel.setText(Analyzer.getResourceString("From"));
		fromLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		fromCombo = new Combo(controlGroup, SWT.READ_ONLY);
		
		Label toLabel=new Label(controlGroup, SWT.None);
		toLabel.setText(Analyzer.getResourceString("To"));
		toLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		toCombo = new Combo(controlGroup, SWT.READ_ONLY);
		setItemFromComboToCombo();
		
		analyze = new Button(controlGroup, SWT.PUSH);
		analyze.setText(Analyzer.getResourceString("Analyze"));
		analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		Button analyzeGroup = new Button(controlGroup, SWT.PUSH);
		analyzeGroup.setText(Analyzer.getResourceString("Analyze Group"));
		analyzeGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		/* Add listener to button analyze group */
		analyzeGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(listNodeAreaDest.size()>0 && listNodeAreaSource.size()>0){
					setUpInfoGroupDelay();
				}
			}
		});
		
		/* Add listener to add an element to the table */
		analyze.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(fromCombo.getSelectionIndex()==-1 || toCombo.getSelectionIndex()==-1){
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("Let choose source node and destination node!");
					dialog.open(); 
				}
				else{	
					table.removeAll();
					int No=1;
					double maxDelay=0;
					double minDelay=1000000000;
					double totalDelay=0;
					LinkedHashMap<Packet,Double> listDelayPacket = new LinkedHashMap<Packet,Double>();
					ArrayList<Packet> listPacket = new ArrayList<Packet>();
					for (int i=0;i<Analyzer.mParser.getListPacket().size();i++){ 
						Packet packet=Analyzer.mParser.getListPacket().get(i);
					 
						if (!fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") && !toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes")) 
						{
							if(fromCombo.getItem(fromCombo.getSelectionIndex()).equals((packet.sourceID)) && toCombo.getItem(toCombo.getSelectionIndex()).equals((packet.destID)) && packet.isSuccess )
							{
								listPacket.add(packet);
							}
						}
					 
						if(!fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") && toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if(fromCombo.getItem(fromCombo.getSelectionIndex()).equals((packet.sourceID)) && packet.isSuccess )
							{
								listPacket.add(packet);
							}
						}
						if(fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") && !toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if(toCombo.getItem(toCombo.getSelectionIndex()).equals((packet.destID)) && packet.isSuccess )
							{
								listPacket.add(packet);
							}
						}
						if(fromCombo.getItem(fromCombo.getSelectionIndex()).equals("All nodes") && toCombo.getItem(toCombo.getSelectionIndex()).equals("All nodes"))
						{
							if(packet.isSuccess )
							{
								listPacket.add(packet);
							}
						}				 
					}
				
					for(int i =0 ;i < listPacket.size(); i++){
						Packet packet = listPacket.get(i);
						TableItem tableItem= new TableItem(table, SWT.NONE);
						tableItem.setText(0,Integer.toString(No++));
						tableItem.setText(1,packet.id);
						tableItem.setText(2,new DecimalFormat("0.00000000").format(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)));
						tableItem.setText(3,packet.sourceID +"---"+packet.destID);
						totalDelay+=(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
						listDelayPacket.put(packet,(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)));
					
						if (maxDelay < (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)))
							maxDelay = (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
						if (minDelay > (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)))
							minDelay = (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
					}
				
					if (No == 1)
					{
						MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("No packet from node " + fromCombo.getItem(fromCombo.getSelectionIndex()) + " to node "+toCombo.getItem(toCombo.getSelectionIndex())+"!");
						dialog.open(); 
					
						avgText.setText("0");
						variantText.setText("0");
						maxText.setText("0");
						minText.setText("0");
						xSeries=new double[0];
						ySeries=new double[0];
					}
					else
					{
						DecimalFormat df = new DecimalFormat("0.00000000");
						String str= df.format(totalDelay/(No-1));

						//set mean
						avgText.setText(str);

						//set text variant
						variantText.setText(df.format(variancesDelay(listDelayPacket,totalDelay))); 
						maxText.setText(df.format(maxDelay));
						minText.setText(df.format(minDelay));

						//init line chart
						initXYseries(listDelayPacket);						
					}
					if(filterByCombo.getSelectionIndex()==0)
						resetEditors();
				}
			}
		});		
		 
		/* Add common controls */
		super.createControlWidgets(); 
	}
	
	/**
	 * Set up item for fromCombo and toCombo 
	 */
	void setItemFromComboToCombo(){
		if (filterByCombo.getSelectionIndex()==0){
			String[] itemList=new String[Analyzer.mParser.getListNodes().size()+1] ; 
			if(Analyzer.mParser.getListNodes().size()>0)
			{
				itemList[0]="All nodes";
				for (int i=0;i<Analyzer.mParser.getListNodes().size();i++) { 
					 NodeTrace node=Analyzer.mParser.getListNodes().get(i);
					 itemList[i+1]=Integer.toString(node.id);
				}
				fromCombo.setItems(itemList);
				toCombo.setItems(itemList);
			}
		}
		if (filterByCombo.getSelectionIndex()==1){
			super.refreshLayoutComposite();
			fromCombo.setItems(new String[] {});
			toCombo.setItems(new String[] {});
			 
			ySeries = new double[Analyzer.mParser.getListNodes().size()];
			xSeries = new double[Analyzer.mParser.getListNodes().size()];		
			for(int i=0;i<Analyzer.mParser.getListNodes().size();i++) {
				NodeTrace node = Analyzer.mParser.getListNodes().get(i);
				xSeries[i]=node.x;
				ySeries[i]=node.y;
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
			setUpInfoGroupDelay();
		}	
	}
	
	public void setUpInfoGroupDelay(){
		String[] itemListSource=new String[this.listNodeAreaSource.size()] ; 
		String[] itemListDest=new String[this.listNodeAreaDest.size()] ;	
			
		for (int i=0;i<this.listNodeAreaSource.size();i++){ 
			NodeTrace node=this.listNodeAreaSource.get(i);
			itemListSource[i]=Integer.toString(node.id);
		}
		
		fromCombo.setItems(itemListSource);
		
		for (int i=0;i<this.listNodeAreaDest.size();i++){ 
			 NodeTrace node=this.listNodeAreaDest.get(i);
			 itemListDest[i]=Integer.toString(node.id);
		}
		
		toCombo.setItems(itemListDest);
			
		table.removeAll();
		int No=1;
		double maxDelay=0;
		double minDelay=1000000000;
		double totalDelay=0;
		LinkedHashMap<Packet,Double> listDelayPacket = new LinkedHashMap<Packet,Double>();
		
		for (int i=0;i<Analyzer.mParser.getListPacket().size();i++){ 
			Packet packet=Analyzer.mParser.getListPacket().get(i);
			for(int j=0;j<this.listNodeAreaSource.size();j++)
			{
				for(int k=0;k<this.listNodeAreaDest.size();k++)
				{
					if(this.listNodeAreaSource.get(j).id == Integer.parseInt(packet.sourceID) && this.listNodeAreaDest.get(k).id==Integer.parseInt(packet.destID) && packet.isSuccess )
					{
						TableItem tableItem= new TableItem(table, SWT.NONE);
						tableItem.setText(0,Integer.toString(No++));
						tableItem.setText(1,packet.id);
						tableItem.setText(2,new DecimalFormat("0.00000000").format(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)));
						tableItem.setText(3,packet.sourceID+"--"+packet.destID);
							 
						totalDelay += (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
						listDelayPacket.put(packet,(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)));
							
						if (maxDelay < (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)))
							maxDelay = (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
						if(minDelay > (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime)))
							minDelay = (Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
					}
				}  
			}
			if (No==1)
			{
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
				DecimalFormat df = new DecimalFormat("0.00000000");
				String str= df.format(totalDelay/(No-1));

				//set mean
				avgText.setText(str);

				//set text variant
				variantText.setText(df.format(variancesDelay(listDelayPacket,totalDelay))); 
				maxText.setText(df.format(maxDelay));
				minText.setText(df.format(minDelay));
			}
		} 		
	}	
	
	public double variancesDelay(LinkedHashMap<Packet,Double> listDelayPacket,Double totalDelay){
		double variances=0; // 
		double expectedValue1=0; //
		double expectedValue2=0; // 
		for (Packet i : listDelayPacket.keySet()) {
			expectedValue1 += listDelayPacket.get(i)*listDelayPacket.get(i) * ((Double.parseDouble(i.endTime)-Double.parseDouble(i.startTime))/totalDelay);
			expectedValue2 += listDelayPacket.get(i)*((Double.parseDouble(i.endTime)-Double.parseDouble(i.startTime))/totalDelay);
		}
		variances=expectedValue1-expectedValue2*expectedValue2;
		return variances;
	}
	
	public void initXYseries(LinkedHashMap<Packet,Double> listDelayPacket){
		int j=0;
		xSeries=new double[listDelayPacket.size()];
		ySeries=new double[listDelayPacket.size()];
		if(listDelayPacket.size()!=0){
			for (Packet i : listDelayPacket.keySet()) {
				ySeries[j]=listDelayPacket.get(i);
				xSeries[j]=Double.parseDouble(i.startTime);
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
		return new String[] { "No", "Packet","Time","Source-Dest" };
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	public String getTabText() {
		return "Delay";
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
		chart.getTitle().setText("Delay");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Time(s)");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Delay(s)");
		
		// create line series
		ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, "line series");
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
						double distance = Math.sqrt(Math.pow(e.x - p.x, 2) + Math.pow(e.y - p.y, 2));

						if (distance < ((ILineSeries) series).getSymbolSize()) {
							setToolTipText(series,i,i,i);													
							return;
						}
					}
				}
				chart.getPlotArea().setToolTipText(null);							
			}

			private void setToolTipText(ISeries series, int xIndex,int yIndex,int id) {
				chart.getPlotArea().setToolTipText("No: " + ++id + "\nTime start send: " + series.getXSeries()[xIndex] + "\nDelay: " + series.getYSeries()[yIndex]);				 
			}
		});
	}
	
	/**
	 * Sets the state of the layout.
	 */
	void setLayoutState() {
		
	}
}

