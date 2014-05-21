package views.dialogs;

import java.util.HashMap;
import java.util.Iterator;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import views.Workspace;
import controllers.managers.ApplicationSettings;


public class CreateTrafficFlowDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateTrafficFlow;
	
	private Workspace workspace;
	private Table table;
	
	private List<EventEntry> eventList = new LinkedList<EventEntry>();
	int trafficFlowCount = 0;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNetwork 
	 */
	public CreateTrafficFlowDialog(Shell parent, int style, Workspace workspace) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.workspace = workspace;
		
		// calculate traffic flow number
		for (Node n : workspace.getProject().getNetwork().getNodeList()) {
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
		shlCreateTrafficFlow.setSize(397, 382);
		shlCreateTrafficFlow.setText("Create Traffic Flow");
		
		Composite composite = new Composite(shlCreateTrafficFlow, SWT.BORDER);
		composite.setBounds(0, 303, 391, 61);
		
		Composite composite_1 = new Composite(shlCreateTrafficFlow, SWT.NONE);
		composite_1.setBounds(0, 0, 391, 297);
		
		Label lblSourceNode = new Label(composite_1, SWT.NONE);
		lblSourceNode.setBounds(31, 34, 80, 15);
		lblSourceNode.setText("Source Node");
		
		final Combo cbSourceNode = new Combo(composite_1, SWT.NONE);
		cbSourceNode.setBounds(117, 30, 47, 23);
		
		Label lblDestinationNode = new Label(composite_1, SWT.NONE);
		lblDestinationNode.setBounds(196, 34, 100, 15);
		lblDestinationNode.setText("Destination Node");
		
		final Combo cbDestNode = new Combo(composite_1, SWT.NONE);
		cbDestNode.setBounds(304, 30, 47, 23);
		
		final WirelessNetwork wirelessNetwork = workspace.getProject().getNetwork();
		
		for (Node n : wirelessNetwork.getNodeList()) {
			cbSourceNode.add(n.getId() + "");
			cbDestNode.add(n.getId() + "");
		}
		
		Group grpInternetProtocol = new Group(composite_1, SWT.NONE);
		grpInternetProtocol.setBounds(31, 59, 322, 228);
		grpInternetProtocol.setText("Transport Protocol");
		
		Label lblType = new Label(grpInternetProtocol, SWT.NONE);
		lblType.setBounds(50, 33, 108, 15);
		lblType.setText("Transport Protocol");
		
		final Combo cbTransportProtocol = new Combo(grpInternetProtocol, SWT.NONE);
		cbTransportProtocol.setBounds(176, 30, 91, 23);
		cbTransportProtocol.select(0);
		loadComboContent(cbTransportProtocol, ApplicationSettings.transportProtocols, ApplicationSettings.defaultTransportProtocol);
		
		
		final Combo cbApplication = new Combo(grpInternetProtocol, SWT.NONE);
		cbApplication.setBounds(176, 59, 91, 23);
		cbApplication.select(0);
		loadComboContent(cbApplication, ApplicationSettings.applicationProtocols, ApplicationSettings.defaultApplicationProtocol);
		
		Label lblApplication = new Label(grpInternetProtocol, SWT.NONE);
		lblApplication.setBounds(50, 62, 114, 15);
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
				
				try {
					int sid = Integer.parseInt(cbSourceNode.getText());
					int did = Integer.parseInt(cbDestNode.getText());
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
				String transName = transType + "_" + trafficFlowCount;				
				
				TransportProtocol transProtocol = s.addTransportProtocol(transType, transName);
				
				HashMap<String, String> transProtocolParams = ApplicationSettings.transportProtocols.get(transType.toString());
				Set<Entry<String, String>> set = transProtocolParams.entrySet();
				Iterator<Entry<String, String>> i = set.iterator();
				while (i.hasNext()) {
					Entry<String, String> me = i.next();
					transProtocol.addParameter(me.getKey(), me.getValue());
				}
				
				// create app protocol that use that transport protocol
				ApplicationProtocolType appType = ApplicationProtocolType.valueOf(cbApplication.getText());				
				String appName = appType + "_" + trafficFlowCount;				
				
				ApplicationProtocol appProtocol = transProtocol.addApp(appType, appName, d);
				
				HashMap<String, String> appProtocolParams = ApplicationSettings.applicationProtocols.get(appType.toString());
				
				set = appProtocolParams.entrySet();
				i = set.iterator();
				while (i.hasNext()) {
					Entry<String, String> me = i.next();
					appProtocol.addParameter(me.getKey(), me.getValue());
				}
				
				for (TableItem item : table.getItems())	appProtocol.addEvent(EventType.valueOf(item.getText(0)), Integer.parseInt(item.getText(1)));					
				
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

		shlCreateTrafficFlow.setDefaultButton(btnCreate);
	}
	
	private void updateEventTable() {
		table.removeAll();
		
		for (EventEntry ee : eventList) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { ee.type.toString(), ee.time + "" });
		}
	}
	
	private void loadComboContent(Combo combo, HashMap<String, HashMap<String, String>> items, StringBuilder defaultItem) {
		combo.removeAll();
		Set<Entry<String, HashMap<String, String>>> set = items.entrySet();
		Iterator<Entry<String, HashMap<String, String>>> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<String, HashMap<String, String>> e = iterator.next();
			combo.add(e.getKey());
			if (e.getKey().equals(defaultItem.toString()))
				combo.select(i);
			
			i++;
		}			
	}
}
