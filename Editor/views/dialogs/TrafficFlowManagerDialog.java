package views.dialogs;

import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.TransportProtocol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import view.Workspace;

public class TrafficFlowManagerDialog extends Dialog {

	protected Object result;
	protected Shell shlTrafficFlowGenerator;
	private Table table;
	private Label lblCount;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNetwork 
	 */
	public TrafficFlowManagerDialog(Shell parent, int style, Workspace workspace) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.workspace = workspace;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		updateContent();		
		shlTrafficFlowGenerator.open();
		shlTrafficFlowGenerator.layout();
		Display display = getParent().getDisplay();
		while (!shlTrafficFlowGenerator.isDisposed()) {
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
		shlTrafficFlowGenerator = new Shell(getParent(), getStyle());
		shlTrafficFlowGenerator.setSize(508, 387);
		shlTrafficFlowGenerator.setText("Traffic Flow");
		
		Composite composite = new Composite(shlTrafficFlowGenerator, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 501, 64);
		
		Label lblTrafficFlowGenerator = new Label(composite, SWT.NONE);
		lblTrafficFlowGenerator.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTrafficFlowGenerator.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblTrafficFlowGenerator.setBounds(10, 10, 277, 28);
		lblTrafficFlowGenerator.setText("Traffic Flow");
		
		Label lblManageTrafficFlow = new Label(composite, SWT.NONE);
		lblManageTrafficFlow.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblManageTrafficFlow.setBounds(10, 35, 197, 15);
		lblManageTrafficFlow.setText("Manage Traffic flow in the network");
		
		Composite composite_2 = new Composite(shlTrafficFlowGenerator, SWT.NONE);
		composite_2.setBounds(0, 70, 501, 289);
		
		table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(20, 26, 341, 235);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("ok");
			}
		});
		
		TableColumn tblclmnSource = new TableColumn(table, SWT.NONE);
		tblclmnSource.setWidth(92);
		tblclmnSource.setText("Source");
		
		TableColumn tblclmnDestination = new TableColumn(table, SWT.NONE);
		tblclmnDestination.setWidth(99);
		tblclmnDestination.setText("Destination");
		
		TableColumn tblclmnApplication = new TableColumn(table, SWT.NONE);
		tblclmnApplication.setWidth(145);
		tblclmnApplication.setText("Application");
		
		Button btnAdd = new Button(composite_2, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CreateTrafficFlowDialog(shlTrafficFlowGenerator, SWT.SHEET, workspace).open();
				updateContent();
			}
		});
		btnAdd.setBounds(397, 26, 75, 25);
		btnAdd.setText("Add");
		
		Button btnRemove = new Button(composite_2, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeItems();
			}
		});
		btnRemove.setBounds(397, 88, 75, 25);
		btnRemove.setText("Remove");
		
		Button btnRemoveAll = new Button(composite_2, SWT.NONE);
		btnRemoveAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeAllItems();
			}
		});
		btnRemoveAll.setBounds(397, 119, 75, 25);
		btnRemoveAll.setText("Remove All");
		
		Button btnClose = new Button(composite_2, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlTrafficFlowGenerator.close();
			}
		});
		btnClose.setBounds(397, 236, 75, 25);
		btnClose.setText("Close");

		shlTrafficFlowGenerator.setDefaultButton(btnClose);
		
		Button btnEdit = new Button(composite_2, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnEdit.setBounds(397, 57, 75, 25);
		btnEdit.setText("Edit");
		
		WirelessNetwork wirelessNetwork = workspace.getProject().getNetwork();
		
		Label lblNetworkInfo = new Label(composite_2, SWT.NONE);
		lblNetworkInfo.setBounds(20, 5, 198, 15);
		lblNetworkInfo.setText("Network: " + wirelessNetwork.getName());
		
		lblCount = new Label(composite_2, SWT.NONE);
		lblCount.setBounds(297, 5, 64, 15);
		lblCount.setText("Count: " + table.getItemCount());
	}
	
	private void updateContent() {
		int count = 0;
		table.removeAll();
		
		for (Node n : workspace.getProject().getNetwork().getNodeList()) {
			for (TransportProtocol tp : n.getTransportPrototolList()) {
				for (ApplicationProtocol ap : tp.getAppList()) {
					String type = "CBR";
					switch (ap.getType()) {
					case ApplicationProtocol.CBR:
						type = "CBR";
						break;
					case ApplicationProtocol.VBR:
						type = "VBR";
						break;
					case ApplicationProtocol.FTP:
						type = "FTP";
						break;
					case ApplicationProtocol.PARETO:
						type = "Pareto";
						break;
					case ApplicationProtocol.TELNET:
						type = "Telnet";
						break;
					}
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(new String[] {
							n.getId() + "",
							ap.getDestNode().getId() + "",
							type
					});
				}
				
				count += tp.getAppList().size();
			}
		}
		
		lblCount.setText("Count: " + count);
	}
	
	private void removeItems() {
		TableItem[] items = table.getSelection();
		
		int sid, did;
		for (TableItem item : items) {
			sid = Integer.parseInt(item.getText(0));
			did = Integer.parseInt(item.getText(1));
			
			Node n = workspace.getProject().getNetwork().getNodeById(sid);
			for (TransportProtocol tp : n.getTransportPrototolList()) {
				for (ApplicationProtocol ap : tp.getAppList()) {
					if (ap.getDestNode().getId() == did) {
						tp.removeApp(ap);
						break;
					}
				}
				
				n.getTransportPrototolList().remove(tp);
				break;
			}
		}
		
		updateContent();
	}
	
	private void removeAllItems() {
		TableItem[] items = table.getItems();
		
		int sid, did;
		for (TableItem item : items) {
			sid = Integer.parseInt(item.getText(0));
			did = Integer.parseInt(item.getText(1));
			
			Node n = workspace.getProject().getNetwork().getNodeById(sid);
			for (TransportProtocol tp : n.getTransportPrototolList()) {
				for (ApplicationProtocol ap : tp.getAppList()) {
					if (ap.getDestNode().getId() == did) {
						tp.removeApp(ap);
						break;
					}
				}
				
				n.getTransportPrototolList().remove(tp);
				break;
			}
		}
		
		updateContent();		
	}
}
