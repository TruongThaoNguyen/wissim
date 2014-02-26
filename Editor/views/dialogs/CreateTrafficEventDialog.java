package views.dialogs;

import java.util.List;

import models.networkcomponents.events.AppEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class CreateTrafficEventDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateTrafficEvent;
	
	private List<EventEntry> eventList;
	private int networkTime;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param eventList 
	 */
	public CreateTrafficEventDialog(Shell parent, int style, List<EventEntry> eventList, int networkTime) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.eventList = eventList;
		this.networkTime = networkTime;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateTrafficEvent.open();
		shlCreateTrafficEvent.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateTrafficEvent.isDisposed()) {
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
		shlCreateTrafficEvent = new Shell(getParent(), getStyle());
		shlCreateTrafficEvent.setSize(322, 189);
		shlCreateTrafficEvent.setText("Create Traffic Event");
		
		Label lblType = new Label(shlCreateTrafficEvent, SWT.NONE);
		lblType.setBounds(22, 79, 48, 15);
		lblType.setText("Type");
		
		final Scale scale = new Scale(shlCreateTrafficEvent, SWT.NONE);
		scale.setBounds(103, 22, 189, 42);
		
		if (eventList.size() > 0)
			scale.setMinimum(eventList.get(eventList.size() - 1).time + 1);
		else
			scale.setMinimum(1);
		scale.setMaximum(networkTime);
		scale.setPageIncrement((scale.getMaximum() - scale.getMinimum())/ 10);
		scale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				scale.setToolTipText(scale.getSelection() + "");
			}
		});
		
		Label lblRaisedTime = new Label(shlCreateTrafficEvent, SWT.NONE);
		lblRaisedTime.setText("Raised Time");
		lblRaisedTime.setBounds(22, 38, 75, 15);
		
		final Combo combo = new Combo(shlCreateTrafficEvent, SWT.NONE);
		combo.setItems(new String[] {"START", "STOP"});
		combo.setBounds(103, 76, 91, 23);
		if (eventList.size() == 0)
			combo.select(0);
		else
			switch (eventList.get(eventList.size() - 1).type) {
			case AppEvent.START:
				combo.select(1);
				break;
			case AppEvent.STOP:
				combo.select(0);
				break;
			}
		
		Button btnCreate = new Button(shlCreateTrafficEvent, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int type = AppEvent.START;
				
				switch (combo.getText()) {
				case "START":
					type = AppEvent.START;
					break;
				case "STOP":
					type = AppEvent.STOP;
					break;
				}
				eventList.add(new EventEntry(type, scale.getSelection()));
				
				shlCreateTrafficEvent.close();
			}
		});
		btnCreate.setBounds(62, 117, 75, 25);
		btnCreate.setText("Create");
		
		Button btnCancel = new Button(shlCreateTrafficEvent, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreateTrafficEvent.dispose();
			}
		});
		btnCancel.setBounds(161, 117, 75, 25);
		btnCancel.setText("Cancel");		
		
		shlCreateTrafficEvent.setDefaultButton(btnCreate);
	}
}
