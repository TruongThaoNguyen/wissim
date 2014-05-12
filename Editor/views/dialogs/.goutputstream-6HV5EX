package views.dialogs;

import models.networkcomponents.WirelessNode;
import models.networkcomponents.events.NodeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class CreateNodeEventDialog extends Dialog {

	protected Object result;
	protected Shell shlNewNodeEvent;
	
	private WirelessNode wirelessNode;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNode 
	 */
	public CreateNodeEventDialog(Shell parent, int style, WirelessNode wirelessNode) {
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
		shlNewNodeEvent.open();
		shlNewNodeEvent.layout();
		Display display = getParent().getDisplay();
		while (!shlNewNodeEvent.isDisposed()) {
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
		shlNewNodeEvent = new Shell(getParent(), getStyle());
		shlNewNodeEvent.setSize(329, 196);
		shlNewNodeEvent.setText("New Event for Node " + wirelessNode.getId());
		
		Label lblRaisedTime = new Label(shlNewNodeEvent, SWT.NONE);
		lblRaisedTime.setBounds(21, 47, 77, 15);
		lblRaisedTime.setText("RaisedTime");
		
		int minTime = 1;
		int type = NodeEvent.ON;
		if (wirelessNode.getEventList().size() > 0) { 
			NodeEvent lastEvent = wirelessNode.getEventList().get(wirelessNode.getEventList().size() - 1);
			minTime = lastEvent.getRaisedTime() + 1;
			
			switch (lastEvent.getType()) {
			case NodeEvent.ON:
				type = NodeEvent.OFF;
				break;
			case NodeEvent.OFF:
			default:
				type = NodeEvent.ON;
			}
		}
		
		final Scale scale = new Scale(shlNewNodeEvent, SWT.NONE);
		scale.setBounds(104, 33, 189, 42);
		scale.setMinimum(minTime);
		scale.setMaximum(wirelessNode.getNetwork().getTime());
		scale.setPageIncrement((scale.getMaximum() - scale.getMinimum()) / 10);
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				scale.setToolTipText(scale.getSelection() + "");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label lblEventType = new Label(shlNewNodeEvent, SWT.NONE);
		lblEventType.setBounds(21, 94, 77, 15);
		lblEventType.setText("Event Type");
		
		Button btnAdd = new Button(shlNewNodeEvent, SWT.NONE);
		btnAdd.setBounds(83, 132, 75, 25);
		btnAdd.setText("Add");
		
		Button btnCancel = new Button(shlNewNodeEvent, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNewNodeEvent.dispose();
			}
		});
		btnCancel.setBounds(176, 132, 75, 25);
		btnCancel.setText("Cancel");
		
		final Combo combo = new Combo(shlNewNodeEvent, SWT.READ_ONLY);
		combo.setItems(new String[] {"ON", "OFF"});
		combo.setBounds(104, 90, 91, 23);
		combo.select(type);
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int type = NodeEvent.ON;
				
				switch (combo.getItem(combo.getSelectionIndex())) {
				case "ON":
					type = NodeEvent.ON;
					break;
				case "OFF":
					type = NodeEvent.OFF;
					break;
				}
				
				new NodeEvent(type, scale.getSelection(), wirelessNode);
				shlNewNodeEvent.dispose();
			}
		});		

	}
}
