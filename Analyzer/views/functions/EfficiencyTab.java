package views.functions;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import models.Node;
import models.Packet;

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
import org.eclipse.swt.widgets.Text;

import views.Analyzer;
import views.Tab;

import com.ibm.icu.text.DecimalFormat;

import controllers.chart2d.BarChart;
import controllers.chart2d.ChartAllNodeMultiArea;
import controllers.chart3d.SurfaceChartEfficiency;
import controllers.parser.ParserManager;

public class EfficiencyTab extends Tab implements Observer{
	
	/* The example layout instance */
	FillLayout fillLayout;
	Text eText;
	Combo criteriaCombo; 
	Combo filterByCombo;
	Button resetButton;
	ArrayList<ArrayList<Node>> listNodeAreas;
	ChartAllNodeMultiArea chartAllNodeEfficiency;
	ArrayList<Double> listEfficiencyOfAreas;
	/**
	 * Creates the Tab within a given instance of LayoutExample.
	 */
	public EfficiencyTab(Analyzer instance) {
		super(instance);
		listNodeAreas = new ArrayList<ArrayList<Node>>();
		listEfficiencyOfAreas = new ArrayList<Double>();
	}

	/**
	 * Creates the widgets in the "child" group.
	 */
	protected void createChildWidgets() {
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
	protected void createControlWidgets() {
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
		criteriaLabel.setText("Criteria");
		criteriaLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		criteriaCombo = new Combo(controlGroup, SWT.READ_ONLY);
		criteriaCombo.setItems(new String[] {"data bits transmitted/data bit delivered"});
		criteriaCombo.select(0);
		
		analyze = new Button(controlGroup, SWT.PUSH);
		analyze.setText("Analyze");
		analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		
		/* Analyze listener	*/
		analyze.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(filterByCombo.getSelectionIndex()==0){
					DecimalFormat df = new DecimalFormat("0.00");
					String str= df.format(ratioDroppedPacket());
					eText.setText(str+" %");
					SurfaceChartEfficiency.drawChart3D();
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
						ArrayList<Node> listNodeOfSourceArea = listNodeAreas.get(0);
						
						for(int i=0; i<listNodeAreas.size(); i++){
							ArrayList<Node> listNodeOfOneArea = listNodeAreas.get(i);
							numberDroppedPacket = 0;
							numberPacketOfArea = 0;
							
							for (Packet packet : ParserManager.getParser().getPackets().values()) 
							{							
								checkBelongTo = false;
								for(int k = 0;k < listNodeOfOneArea.size(); k++){
									for(int t = 0;t < listNodeOfSourceArea.size(); t++){
										Node nodeDest = listNodeOfOneArea.get(k);
										Node nodeSource = listNodeOfSourceArea.get(t);
										if((nodeDest.getId() == packet.getDestNode().getId()) && (nodeSource.getId() == packet.getSourceNode().getId())){
											checkBelongTo = true;
											numberPacketOfArea++;
											break;
										}
									}
								} 
								if(checkBelongTo	&& packet.getType().equals("cbr")){
									TableItem tableItem= new TableItem(table, SWT.NONE);
									tableItem.setText(0,Integer.toString(No++));
									tableItem.setText(1,Integer.toString(packet.getId()));
									tableItem.setText(2,packet.getSourceNode().getId()+"---"+packet.getDestNode().getId());
									tableItem.setText(3,Integer.toString(packet.getSize()));
									tableItem.setText(4,Boolean.toString(packet.isSuccess()));
										 tableItem.setText(5, Integer.toString(i+1));
										if(!packet.isSuccess())
											numberDroppedPacket++;
									}
								}
								if(numberPacketOfArea != 0){
									listEfficiencyOfAreas.add(100-((numberDroppedPacket/numberPacketOfArea)*100));
										new TableItem(table, SWT.NONE);
								}
								else{
									listEfficiencyOfAreas.add(-1.0);
								}
							}
							Shell shell = new Shell();		
							new BarChart(shell,listEfficiencyOfAreas,"Efficiency (%)");
							eText.setText("");
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
		for (Packet packet : ParserManager.getParser().getPackets().values()) 
		{
			if( packet.getType().equals("cbr")){
				TableItem tableItem= new TableItem(table, SWT.NONE);
				tableItem.setText(0, Integer.toString(No++));
				tableItem.setText(1, Integer.toString(packet.getId()));
				tableItem.setText(2, packet.getSourceNode().getId()+"---"+packet.getDestNode().getId());
				tableItem.setText(3, Integer.toString(packet.getSize()));
				tableItem.setText(4, Boolean.toString(packet.isSuccess()));
			}
			if(!packet.isSuccess() && packet.getType().equals("cbr"))
				numberDroppedPacket++;
		}
		return (numberDroppedPacket / ParserManager.getParser().getPackets().size())*100;
	}
	
	void initDataFilterByComboChange(){
		if(filterByCombo.getSelectionIndex()==0){
			resetButton.setVisible(false);
		}
		if(filterByCombo.getSelectionIndex()==1){
			super.refreshLayoutComposite();

			ySeries = new double[ParserManager.getParser().getNodes().size()];
			xSeries = new double[ParserManager.getParser().getNodes().size()];		
			for(int j=0;j<ParserManager.getParser().getNodes().size();j++) {
				Node node = ParserManager.getParser().getNodes().get(j);
				xSeries[j]=node.getX();
				ySeries[j]=node.getY();
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
	protected void createLayout() {
		fillLayout = new FillLayout();
		layoutComposite.setLayout(fillLayout);
		resetButton = new Button(layoutGroup, SWT.PUSH);
		resetButton.setText("Reset");
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
//		Button drawChart3D = new Button(layoutGroup, SWT.PUSH);
//		drawChart3D.setText(Analyze.getResourceString("Draw 3Dchart"));
//		drawChart3D.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
//		/*Add listener to button drawChart*/
//		drawChart3D.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//				SurfaceChartEfficiency.drawChart3D();
//				}
//			}); 
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
		return new String[] { "No", "Packet","Source--Dest","Size","isDropped","Group"};
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
