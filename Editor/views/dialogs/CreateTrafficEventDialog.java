package views.dialogs;

import java.util.List;

import models.networkcomponents.events.Event.EventType;

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
		shlCreateTrafficEvent.setSize(384, 181);
		shlCreateTrafficEvent.setText("Create Traffic Event");
		
		Label lblType = new Label(shlCreateTrafficEvent, SWT.NONE);
		lblType.setBounds(22, 79, 48, 26);
		lblType.setText("Type");
		final Label lblNewLabel = new Label(shlCreateTrafficEvent, SWT.NONE);
		lblNewLabel.setBounds(316, 33, 48, 17);
		lblNewLabel.setText("1");
		
		final Scale scale = new Scale(shlCreateTrafficEvent, SWT.NONE);
		scale.setBounds(115, 22, 195, 42);
		
		if (eventList.size() > 0)
			scale.setMinimum(eventList.get(eventList.size() - 1).time + 1);
		else
			scale.setMinimum(1);
		scale.setMaximum(networkTime);
		scale.setPageIncrement((scale.getMaximum() - scale.getMinimum())/ 10);
		scale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int valueRaised = scale.getSelection();
				scale.setToolTipText(scale.getSelection() + "");
				lblNewLabel.setText(Integer.toString(valueRaised));
			}
		});
		
		Label lblRaisedTime = new Label(shlCreateTrafficEvent, SWT.NONE);
		lblRaisedTime.setText("Raised Time");
		lblRaisedTime.setBounds(22, 38, 87, 25);
		
		final Combo combo = new Combo(shlCreateTrafficEvent, SWT.NONE);
		combo.setItems(new String[] {"START", "STOP"});
		combo.setBounds(103, 76, 91, 23);
		if (eventList.size() == 0)	combo.select(0);			
		else						combo.select(combo.indexOf(eventList.get(eventList.size() - 1).toString()));			
		
		Button btnCreate = new Button(shlCreateTrafficEvent, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {			
				if (!combo.getText().isEmpty()) {
					eventList.add(new EventEntry(EventType.valueOf(combo.getText()), scale.getSelection()));				
					shlCreateTrafficEvent.close();	
				}
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
