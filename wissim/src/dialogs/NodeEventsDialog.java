package dialogs;

import networkcomponents.WirelessNode;
import networkcomponents.events.NodeEvent;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;

public class NodeEventsDialog extends Dialog {

	protected Object result;
	protected Shell shlNodeEvents;
	private Table table;
	
	private WirelessNode wirelessNode;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNode 
	 */
	public NodeEventsDialog(Shell parent, int style, WirelessNode wirelessNode) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.wirelessNode = wirelessNode;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlNodeEvents.open();
		shlNodeEvents.layout();
		Display display = getParent().getDisplay();
		while (!shlNodeEvents.isDisposed()) {
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
		shlNodeEvents = new Shell(getParent(), getStyle());
		shlNodeEvents.setSize(366, 325);
		shlNodeEvents.setText("Node Events");
		
		table = new Table(shlNodeEvents, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(20, 97, 206, 168);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnEventType = new TableColumn(table, SWT.NONE);
		tblclmnEventType.setWidth(100);
		tblclmnEventType.setText("Event Type");
		
		TableColumn tblclmnRaisedTime = new TableColumn(table, SWT.NONE);
		tblclmnRaisedTime.setWidth(100);
		tblclmnRaisedTime.setText("Raised Time");
		
		// update items on table
		updateTable();
		
		Button btnAdd = new Button(shlNodeEvents, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CreateNodeEventDialog(shlNodeEvents, SWT.SHEET, wirelessNode).open();
				updateTable();
			}
		});
		btnAdd.setBounds(255, 97, 75, 25);
		btnAdd.setText("Add");
		
		Button btnRemove = new Button(shlNodeEvents, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int size = wirelessNode.getEventList().size();
				
				if (size > 0) {
					wirelessNode.getEventList().remove(size - 1);
					updateTable();
				}
			}
		});
		btnRemove.setBounds(255, 128, 75, 25);
		btnRemove.setText("Remove");
		
		Button btnRemoveAll = new Button(shlNodeEvents, SWT.NONE);
		btnRemoveAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				while (wirelessNode.getEventList().size() > 0)
					wirelessNode.getEventList().remove(0);
				
				updateTable();
			}
		});
		btnRemoveAll.setBounds(255, 164, 75, 25);
		btnRemoveAll.setText("Remove All");
		
		Button btnClose = new Button(shlNodeEvents, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNodeEvents.dispose();
			}
		});
		btnClose.setBounds(255, 240, 75, 25);
		btnClose.setText("Close");

		shlNodeEvents.setDefaultButton(btnClose);
		
		Composite composite = new Composite(shlNodeEvents, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 360, 64);
		
		Label lblNode = new Label(composite, SWT.NONE);
		lblNode.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblNode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNode.setBounds(10, 10, 134, 40);
		lblNode.setText("Node " + wirelessNode.getId());
	}
	
	private void updateTable() {
		// clear all items
		table.removeAll();
		
		// re-add items
		for (NodeEvent e : wirelessNode.getEventList()) {
			TableItem item = new TableItem(table, SWT.NONE);
			String type;
			
			switch (e.getType()) {
			case NodeEvent.ON:
				type = "ON";
				break;
			case NodeEvent.OFF:
			default:
				type = "OFF";
			}
			item.setText(new String[] { type, e.getRaisedTime() + "" });
		}		
	}
}
