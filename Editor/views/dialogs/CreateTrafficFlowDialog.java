package views.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.events.Event.EventType;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.ApplicationProtocol.ApplicationProtocolType;
import models.networkcomponents.protocols.TransportProtocol;
import models.networkcomponents.protocols.TransportProtocol.TransportProtocolType;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;






import views.Workspace;
import controllers.managers.ApplicationSettings;


public class CreateTrafficFlowDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateTrafficFlow;
	
	private Table table;
	private Table cbDestNode;
	private Table cbSourceNode;
	
	private List<EventEntry> eventList = new LinkedList<EventEntry>();
	int trafficFlowCount = 0;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNetwork 
	 */
	public CreateTrafficFlowDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");		
		
		// calculate traffic flow number
		for (Node n : Workspace.getProject().getNetwork().getNodeList()) {
			for (TransportProtocol tp : n.getTransportPrototolList()) {
				trafficFlowCount += tp.getAppList().size();
			}
		}
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateTrafficFlow.open();
		shlCreateTrafficFlow.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateTrafficFlow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlCreateTrafficFlow = new Shell(getParent(), getStyle());
		shlCreateTrafficFlow.setSize(495, 387);
		shlCreateTrafficFlow.setText("Create Traffic Flow");
		
		Composite composite = new Composite(shlCreateTrafficFlow, SWT.BORDER);
		composite.setBounds(0, 303, 489, 61);
		
		final Composite composite_1 = new Composite(shlCreateTrafficFlow, SWT.NONE);
		composite_1.setBounds(0, 0, 489, 297);
		
		Label lblSourceNode = new Label(composite_1, SWT.NONE);
		lblSourceNode.setBounds(54, 34, 108, 15);
		lblSourceNode.setText("<-- Source Node");
		
		Label lblDestinationNode = new Label(composite_1, SWT.NONE);
		lblDestinationNode.setBounds(296, 34, 137, 15);
		lblDestinationNode.setText("Destination Node -->");
		
		final WirelessNetwork wirelessNetwork = Workspace.getProject().getNetwork();
		
		Group grpInternetProtocol = new Group(composite_1, SWT.NONE);
		grpInternetProtocol.setBounds(72, 57, 339, 228);
		grpInternetProtocol.setText("Transport Protocol");
		
		Label lblType = new Label(grpInternetProtocol, SWT.NONE);
		lblType.setBounds(50, 33, 130, 26);
		lblType.setText("Transport Protocol");
		
		final Combo cbTransportProtocol = new Combo(grpInternetProtocol, SWT.NONE);
		cbTransportProtocol.setBounds(186, 30, 98, 29);
		cbTransportProtocol.select(0);
		loadComboContent(cbTransportProtocol, ApplicationSettings.transportProtocols, ApplicationSettings.defaultTransportProtocol);
		
		
		final Combo cbApplication = new Combo(grpInternetProtocol, SWT.NONE);
		cbApplication.setBounds(186, 59, 98, 29);
		cbApplication.select(0);
		loadComboContent(cbApplication, ApplicationSettings.applicationProtocols, ApplicationSettings.defaultApplicationProtocol);
		
		Label lblApplication = new Label(grpInternetProtocol, SWT.NONE);
		lblApplication.setBounds(40, 62, 140, 26);
		lblApplication.setText("Application Protocol");
		
		Label lblEvents = new Label(grpInternetProtocol, SWT.NONE);
		lblEvents.setBounds(10, 88, 55, 15);
		lblEvents.setText("Events");
		
		table = new Table(grpInternetProtocol, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 109, 203, 109);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(95);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnRaisedTime = new TableColumn(table, SWT.NONE);
		tblclmnRaisedTime.setWidth(100);
		tblclmnRaisedTime.setText("Raised Time");
		
		Button btnAdd = new Button(grpInternetProtocol, SWT.NONE);
		btnAdd.setBounds(230, 108, 75, 25);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CreateTrafficEventDialog(shlCreateTrafficFlow, SWT.SHEET, eventList, wirelessNetwork.getTime()).open();
				updateEventTable();
			}
		});
		btnAdd.setText("Add");
		
		Button btnRemoveAll = new Button(grpInternetProtocol, SWT.NONE);
		btnRemoveAll.setBounds(230, 170, 75, 25);
		btnRemoveAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table.getItemCount() == 0) {
					MessageDialog.openWarning(getParent(), "Warning", "No data to remove");
					return;
				}
				while (eventList.size() > 0)
					eventList.remove(0);
				
				updateEventTable();
			}
		});
		btnRemoveAll.setText("Remove All");
		
		Button btnRemove = new Button(grpInternetProtocol, SWT.NONE);
		btnRemove.setBounds(230, 139, 75, 25);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table.getSelectionCount() == 0) {
					MessageDialog.openWarning(getParent(), "Warning", "No item is selected!");
				}
				if (eventList.size() > 0) {
					eventList.remove(eventList.size() - 1);
					updateEventTable();
				}
			}
		});
		btnRemove.setText("Remove");
		
		Button btnCreate = new Button(composite, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Node s = null, d = null;
				if(cbSourceNode.getSelectionCount() == 0){
					MessageDialog.openWarning(getParent(), "Warning", "No source node is selected!");
					return;
				}
				if(cbDestNode.getSelectionCount() == 0){
					MessageDialog.openWarning(getParent(), "Warning", "No destination node is selected!");
					return;
				}
				for(TableItem tiSource : cbSourceNode.getSelection()) {
					for(TableItem tiDest : cbDestNode.getSelection()) {
						if(!tiSource.getText().equals(tiDest.getText())) {
						try {
							int sid = Integer.parseInt(tiSource.getText());
							int did = Integer.parseInt(tiDest.getText());
							s = wirelessNetwork.getNodeById(sid);
							d = wirelessNetwork.getNodeById(did);
						} catch (NumberFormatException exc) {
							MessageDialog.openError(shlCreateTrafficFlow, "Error", "Node id is not invalid");
							return;
						}				
						
						if (s == null || d == null) {
							MessageDialog.openError(shlCreateTrafficFlow, "Error", "Cannot find nodes with specified ids");
							return;
						}
						
						// create transport protocol for source node
						TransportProtocolType transType = TransportProtocolType.valueOf(cbTransportProtocol.getText());					
						TransportProtocol transProtocol = s.addTransportProtocol(transType, trafficFlowCount + "");
						
						HashMap<String, String> transProtocolParams = ApplicationSettings.transportProtocols.get(transType.toString());
						Set<Entry<String, String>> set = transProtocolParams.entrySet();
						Iterator<Entry<String, String>> i = set.iterator();
						while (i.hasNext()) {
							Entry<String, String> me = i.next();
							transProtocol.addParameter(me.getKey(), me.getValue());
						}
						
						// create app protocol that use that transport protocol
						ApplicationProtocolType appType = ApplicationProtocolType.valueOf(cbApplication.getText());												
						ApplicationProtocol appProtocol = transProtocol.addApp(appType, trafficFlowCount + "", d);
						
						HashMap<String, String> appProtocolParams = ApplicationSettings.applicationProtocols.get(appType.toString());
						
						appProtocol.setParameters(appProtocolParams);							
						
						for (TableItem item : table.getItems())	appProtocol.addEvent(EventType.valueOf(item.getText(0)), Integer.parseInt(item.getText(1)));					
						}
					}
				}
				shlCreateTrafficFlow.close();
			}
		});
		btnCreate.setBounds(202, 10, 75, 25);
		btnCreate.setText("Create");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreateTrafficFlow.dispose();
			}
		});
		btnCancel.setBounds(283, 10, 75, 25);
		btnCancel.setText("Cancel");

		final Button btnNewButton = new Button(composite_1, SWT.FLAT|SWT.ARROW|SWT.DOWN);
		btnNewButton.setBounds(10, 30, 38, 29);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
	          @Override
	          public void widgetSelected(SelectionEvent e) {
	              super.widgetSelected(e);
	              if(cbSourceNode != null) {
	            	  if(cbSourceNode.getItemCount() != 0) {
	            		  cbSourceNode = null;
	            		  return;
	            	  }
	              }
	              cbSourceNode = new Table(composite_1, SWT.MULTI);
	              cbSourceNode.setSize(35,220);
	              cbSourceNode.addListener(SWT.MeasureItem, new Listener() {

	                  @Override
	                  public void handleEvent(Event event) {
	                      event.height = 20; //TODO: determine later
	                  }
	              });

	              cbSourceNode.addListener(SWT.PaintItem, new Listener() {

	                  @Override
	                  public void handleEvent(Event event) {
	                      Rectangle bounds = event.getBounds();
	                      event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLUE));
	                      event.gc.drawLine(bounds.x, bounds.y+bounds.height-1, bounds.x+bounds.width, bounds.y+bounds.height-1);
	                  }
	              });
	              for (Node n : wirelessNetwork.getNodeList()) {
	            	  TableItem tableItem = new TableItem(cbSourceNode, SWT.NONE);
	            	  tableItem.setText(n.getId()+"");
	      			}	

	              cbSourceNode.setLocation(12,65);

	          }
	      });
		
		final Button btnNewButton_1 = new Button(composite_1, SWT.FLAT|SWT.ARROW|SWT.DOWN);
		btnNewButton_1.setBounds(439, 34, 38, 29);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
	          @Override
	          public void widgetSelected(SelectionEvent e) {
	              super.widgetSelected(e);
	              if(cbDestNode != null) {
	            	  if(cbDestNode.getItemCount() != 0) {
	            		  cbDestNode = null;
	            		  return;
	            	  }
	              }
	              cbDestNode = new Table(composite_1, SWT.BORDER|SWT.MULTI);
	              cbDestNode.setSize(35, 220);
	              cbDestNode.addListener(SWT.MeasureItem, new Listener() {

	                  @Override
	                  public void handleEvent(Event event) {
	                      event.height = 20; //TODO: determine later
	                  }
	              });

	              cbDestNode.addListener(SWT.PaintItem, new Listener() {

	                  @Override
	                  public void handleEvent(Event event) {
	                      Rectangle bounds = event.getBounds();
	                      event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_BLUE));
	                      event.gc.drawLine(bounds.x, bounds.y+bounds.height-1, bounds.x+bounds.width, bounds.y+bounds.height-1);
	                  }
	              });
	              for (Node n : wirelessNetwork.getNodeList()) {
	            	  TableItem tableItem = new TableItem(cbDestNode, SWT.NONE);
	            	  tableItem.setText(n.getId()+"");
	      			}
	              
	              cbDestNode.setLocation(443,65);

	          }
	      });

		shlCreateTrafficFlow.setDefaultButton(btnCreate);
	}
	
	private void updateEventTable() {
		table.removeAll();
		
		for (EventEntry ee : eventList) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { ee.type.toString(), ee.time + "" });
		}
	}
	
	private void loadComboContent(Combo combo, HashMap<String, LinkedHashMap<String, String>> transportProtocols, StringBuilder defaultItem) {
		combo.removeAll();
		Set<Entry<String, LinkedHashMap<String, String>>> set = transportProtocols.entrySet();
		Iterator<Entry<String, LinkedHashMap<String, String>>> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<String, LinkedHashMap<String, String>> e = iterator.next();
			combo.add(e.getKey());
			if (e.getKey().equals(defaultItem.toString()))
				combo.select(i);
			
			i++;
		}			
	}
}
