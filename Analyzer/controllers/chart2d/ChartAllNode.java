package controllers.chart2d;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.Node;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

import controllers.parser.ParserManager;


public class ChartAllNode extends Observable{
	
	double[] ySeries;
	double[] xSeries;
	
	/**
	 * Used to remember location point of mouse down 
	 */
	private static double startX, startY;
	
	private static double endX;
	private static double endY;

	private static int startXPos;
	private static int startYPos;

	private static int currentX;
	private static int currentY;

	private static boolean drag = false, dragMove = false, checkMove = false;
	public ArrayList<Node> listNodeAreaSource,listNodeAreaDest;
	public ArrayList<ArrayList<Node>> listNodeArea;
	Chart chartAllNode;
	Menu popupMenu;
	
	public ChartAllNode(List<Double> xSeries, List<Double> ySeries)
	{
		this.xSeries = ArrayUtils.toPrimitive((Double[])xSeries.toArray());
		this.ySeries = ArrayUtils.toPrimitive((Double[])ySeries.toArray());
	}
	
	public ChartAllNode(double[] xSeries,double[] ySeries){
		this.xSeries = xSeries;
		this.ySeries = ySeries;
	}

	 /**
	  * create the chart.
	  * 
	  * @param parent
	  *				The parent composite
	  * @return The created chart
	  */
	public void createChart(Composite parent) {
	 	listNodeAreaSource = new ArrayList<Node>();
	 	listNodeAreaDest = new ArrayList<Node>();
	 	listNodeArea = new ArrayList<ArrayList<Node>>();
		  // create a chart
		  chartAllNode = new Chart(parent, SWT.NONE);
		 
		  // set titles
		  chartAllNode.getTitle().setText("All nodes");
		  chartAllNode.getAxisSet().getXAxis(0).getTitle().setText("XAxis");
		  chartAllNode.getAxisSet().getYAxis(0).getTitle().setText("YAxis");
		  
		  // create line series
		  ILineSeries lineSeries = (ILineSeries) chartAllNode.getSeriesSet().createSeries(SeriesType.LINE,"Node");
		  lineSeries.setYSeries(this.ySeries);
		  lineSeries.setXSeries(this.xSeries);
		  lineSeries.getLineStyle();
		lineSeries.setLineStyle(LineStyle.NONE);
	  //	chart.getAxisSet().getYAxis(0).enableLogScale(true);
		  // adjust the axis range
		  chartAllNode.getAxisSet().adjustRange();
		 
		  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
		dialog.setText("");
		dialog.setMessage("Let choose areas");
		 dialog.open(); 
		 
		  /* Get the plot area and add the mouse listeners */
		  final Composite plotArea = chartAllNode.getPlotArea();
		  popupMenu = new Menu(plotArea);
		  
		  plotArea.addListener(SWT.MouseDown, new Listener() {

		  	@Override
				public void handleEvent(Event event) {
		  		if(event.button == 1 && !checkMove){
						 IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
						 IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);
	
						 startX = xAxis.getDataCoordinate(event.x);
						 startY = yAxis.getDataCoordinate(event.y);
	
						 startXPos = event.x;
						 startYPos = event.y;
						
						  drag = true;
						  if(listNodeAreaDest.size() > 0 && listNodeAreaSource.size() > 0){
						 	  listNodeAreaDest.clear();
								  listNodeAreaSource.clear();
						  }
		  		}
		  		if(event.button == 1 && checkMove){
		  			startXPos = event.x;
						startYPos = event.y;
		
						dragMove = true;
		  		}
		  		
				}
		  });
		 
		  plotArea.addListener(SWT.MouseUp, new Listener() {
		  	@Override
				public void handleEvent(Event event) {
					if(event.button == 1 && !checkMove){
						IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
						 IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);
	
						 endX = xAxis.getDataCoordinate(event.x);
						 endY = yAxis.getDataCoordinate(event.y);
						 
						 boolean answer = MessageDialog.openQuestion(new Shell(), "Question", "Do you want to choose this area?");						 
						 if (answer)
						 {
						 	if (listNodeAreaSource.size() == 0)
						 	{						 		
						 		for (Node node : ParserManager.getParser().getNodes().values()) 
						 		{
									if (startX <= node.getX() + 2 && endX >= node.getX() - 2 && startY >= node.getY() - 2 && endY <= node.getY() + 2)
									{
										listNodeAreaSource.add(node);
									}
								}
						 	}
						 	else
						 	{
						 		if(listNodeAreaDest.size() == 0)
						 		{
						 			for (Node node : ParserManager.getParser().getNodes().values()) 
							 		{
		 								if(startX <= node.getX() + 2 && endX >= node.getX() - 2 && startY >= node.getY() - 2 && endY <= node.getY() + 2)
		 								{
		 									listNodeAreaDest.add(node);
		 								}
		 							}
						 		}
						 	}
						 	setChanged();
							  notifyObservers();
						 }
						 
							drag = false;
	
						 plotArea.redraw();
					}
					if(event.button == 1 && checkMove){
						dragMove = false;
					}
					
				}
			  
		  });
		  	plotArea.addKeyListener(new KeyListener() {
				
				@Override
				public void keyPressed(KeyEvent e) {
					if((e.keyCode == SWT.SHIFT)  ){
						checkMove = true;
						//System.out.println(checkMove);
					}				
				}
	
				@Override
				public void keyReleased(KeyEvent e) {
					if((e.keyCode == SWT.SHIFT)  ){
						checkMove = false;
						//System.out.println(checkMove);
					}		
				}		
		  	});
		  plotArea.addListener(SWT.MouseMove, new Listener() {

				@Override
				public void handleEvent(Event event) {
					 if(drag)
					 {
						  currentX = event.x;
						  currentY = event.y;

						  plotArea.redraw();
					 }
					 if(dragMove){
					 	currentX = event.x;
						  currentY = event.y;
						  if(startXPos > currentX)
						  		chartAllNode.getAxisSet().getXAxis(0).scrollUp();
						  if(startYPos < currentY)
									chartAllNode.getAxisSet().getYAxis(0).scrollUp();
						  if(startXPos < currentX)
									chartAllNode.getAxisSet().getXAxis(0).scrollDown();
						  if(startYPos > currentY)
									chartAllNode.getAxisSet().getYAxis(0).scrollDown();
						  chartAllNode.redraw();
						  plotArea.redraw();
						  dragMove = false;
					 }
				}
		  });

		  plotArea.addListener(SWT.Paint, new Listener() {		  	
				@Override
				public void handleEvent(Event event) {
					 if(drag){
						  GC gc = event.gc;

						  gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
						  gc.setAlpha(128);

						  int minX = Math.min(startXPos, currentX);
						  int minY = Math.min(startYPos, currentY);

						  int maxX = Math.max(startXPos, currentX);
						  int maxY = Math.max(startYPos, currentY);

						  int width = maxX - minX;
						  int height = maxY - minY;

						  gc.fillRectangle(minX, minY, width, height);
						 
					 }
					 if(listNodeAreaSource.size()>0){
					 	GC gc = event.gc;
					 	gc.setAlpha(128);
						  gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
						 for(int i=0;i<listNodeAreaSource.size();i++){
						 	Node node = listNodeAreaSource.get(i);
						 	IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
							  IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

							  double x = node.getX();
							  double y = node.getY();

							  ISeries[] series = chartAllNode.getSeriesSet().getSeries();

							  double closestX = 0;
							  double closestY = 0;
							  double minDist = Double.MAX_VALUE;

							  /* over all series */
							  for (ISeries serie : series) {
									double[] xS = serie.getXSeries();
									double[] yS = serie.getYSeries();

									/* check all data points */
									for (int j = 0; j < xS.length; j++) {
										 /* compute distance to mouse position */
										 double newDist = Math.sqrt(Math.pow((x - xS[j]), 2)
													+ Math.pow((y - yS[j]), 2));

										 /* if closer to mouse, remember */
										 if (newDist < minDist) {
											  minDist = newDist;
											  closestX = xS[j];
											  closestY = yS[j];
										 }
									}
							  }

							  /* remember closest data point */
							  
							  int highlightX = xAxis.getPixelCoordinate(closestX);
							  int highlightY = yAxis.getPixelCoordinate(closestY);
							  gc.fillOval(highlightX - 5, highlightY - 5, 10, 10);						  
						 }
					 }
					 if(listNodeAreaDest.size()>0){
					 	GC gc = event.gc;
						  gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
						  gc.setAlpha(128);
				  
						 for(int i=0;i<listNodeAreaDest.size();i++){
						 	Node node = listNodeAreaDest.get(i);
						 	IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
							  IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

							  double x = node.getX();
							  double y = node.getY();

							  ISeries[] series = chartAllNode.getSeriesSet().getSeries();

							  double closestX = 0;
							  double closestY = 0;
							  double minDist = Double.MAX_VALUE;

							  /* over all series */
							  for (ISeries serie : series) {
									double[] xS = serie.getXSeries();
									double[] yS = serie.getYSeries();

									/* check all data points */
									for (int j = 0; j < xS.length; j++) {
										 /* compute distance to mouse position */
										 double newDist = Math.sqrt(Math.pow((x - xS[j]), 2)
													+ Math.pow((y - yS[j]), 2));

										 /* if closer to mouse, remember */
										 if (newDist < minDist) {
											  minDist = newDist;
											  closestX = xS[j];
											  closestY = yS[j];
										 }
									}
							  }

							  /* remember closest data point */
							  int highlightX = xAxis.getPixelCoordinate(closestX);
							  int highlightY = yAxis.getPixelCoordinate(closestY);
							  gc.fillOval(highlightX - 5, highlightY - 5, 10, 10);
						 }
						 
					 }
					 
				}
		  });
		 
		  plotArea.addListener(SWT.MouseWheel, new Listener() {
				@Override
				public void handleEvent(Event event) {
					
						  if (event.count > 0) {
						  	chartAllNode.getAxisSet().zoomIn();
						  } else {
						  	chartAllNode.getAxisSet().zoomOut();
						  }
					 
					 chartAllNode.redraw();
					
				}

		  });
		  
		  plotArea.addMouseMoveListener(new MouseMoveListener() {
		  	public void mouseMove(MouseEvent e) {
					 for (ISeries series : chartAllNode.getSeriesSet().getSeries()) {
						  for (int i = 0; i < series.getYSeries().length; i++) {
								Point p = series.getPixelCoordinates(i);
								double distance = Math.sqrt(Math.pow(e.x - p.x, 2)
										  + Math.pow(e.y - p.y, 2));

								if (distance < ((ILineSeries) series).getSymbolSize()) {
									 setToolTipText(series,i,i,i);
									 createPopupMenu(i);
									 return;
								}
						  }
					 }
					 chartAllNode.getPlotArea().setToolTipText(null);
					 popupMenu.dispose();
					
				}

		  	private void setToolTipText(ISeries series, int xIndex,int yIndex,int id) {
		  		String group = "";
		  		
		  		if (listNodeAreaSource.contains(ParserManager.getParser().getNodes().get(id)))
		  		{
		  			group = "1";
		  		}
					
		  		if (listNodeAreaDest.contains(ParserManager.getParser().getNodes().get(id)))
		  		{
		  			if(group != "") group += ",2";
						else				group = "2";
		  		}
					 
		  		chartAllNode.getPlotArea().setToolTipText(
					 		"id: "	+ id + 
					 		"\nx: " + series.getXSeries()[xIndex] + 
					 		"\ny: " + series.getYSeries()[yIndex]+"\ngroup: " + group);
				}
		  	
				private void createPopupMenu(int i)
				{
					final int nodeID = i;
					popupMenu = new Menu(plotArea);
					 MenuItem addItem = new MenuItem(popupMenu, SWT.CASCADE);
					 addItem.setText("Add to groups");
					 MenuItem removeItem = new MenuItem(popupMenu, SWT.CASCADE);
					 removeItem.setText("Get out groups");
					 
					 listNodeArea.clear();
					 listNodeArea.add(listNodeAreaSource);
					 listNodeArea.add(listNodeAreaDest);
					 	
					 Menu menuAdd = new Menu(popupMenu);
					 for (int j = 0; j < listNodeArea.size(); j++) 
					 {
					 	if (!listNodeArea.get(j).contains(ParserManager.getParser().getNodes().get(nodeID)))
					 	{
								MenuItem item = new MenuItem(menuAdd, SWT.RADIO);
								item.setText("Group " + (j+1));								
								item.addSelectionListener(new SelectionAdapter() {
								  public void widgetSelected(SelectionEvent e) {
									 MenuItem item = (MenuItem) e.widget;
									 if (item.getSelection()) {
										//System.out.println(item.getText().substring(6) + " selected");
								  	  listNodeArea.get(Integer.parseInt(item.getText().substring(6))-1).add(ParserManager.getParser().getNodes().get(nodeID));
								  	  listNodeAreaSource = listNodeArea.get(0);
								  	  listNodeAreaDest = listNodeArea.get(1);
								  	  plotArea.redraw();
								  	  
								  	  setChanged();
								  	  notifyObservers();
									 } 
								  }
								});
					 	}
					 }
					 addItem.setMenu(menuAdd);
					 
					 Menu menuRemove = new Menu(popupMenu);
					 for (int j = 0; j < listNodeArea.size(); j++) {
					 	if(listNodeArea.get(j).contains(ParserManager.getParser().getNodes().get(nodeID))){
								MenuItem item = new MenuItem(menuRemove, SWT.RADIO);
								item.setText("Group " + (j+1));							
								item.addSelectionListener(new SelectionAdapter() {
								  public void widgetSelected(SelectionEvent e) {
									 MenuItem item = (MenuItem) e.widget;
									 if (item.getSelection()) {
										//System.out.println(item.getText().substring(6) + " selected");
								  	  listNodeArea.get(Integer.parseInt(item.getText().substring(6))-1).remove(ParserManager.getParser().getNodes().get(nodeID));
								  	  listNodeAreaSource = listNodeArea.get(0);
								  	  listNodeAreaDest = listNodeArea.get(1);
								  	  plotArea.redraw();
								  	  
								  	  setChanged();
		  								 notifyObservers();
									 } 
								  }
								});
					 	}
					 }
					 removeItem.setMenu(menuRemove);
							 
					 plotArea.setMenu(popupMenu);
					 /*
					 class MenuItemListener extends SelectionAdapter {
						  public void widgetSelected(SelectionEvent event) {
							if(((MenuItem) event.widget).getText().equals("Add to groups")){
						  	 //System.out.println(TraceFile.getListNodes().get(nodeID).x);
						  	
							}
							else{
						  	 
							} 
						  }
						}
					 addItem.addSelectionListener(new MenuItemListener());
					 removeItem.addSelectionListener(new MenuItemListener());
					 */
					// System.out.println(i);
					
				}
		  });
		  
		// if(listNodeArea.size() >=1 )
		 // return listNodeArea;
	 }
	 
}

