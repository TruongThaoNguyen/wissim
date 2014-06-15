package views.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class StopRunNS2Dialog extends Dialog {

	protected Object result;
	protected Shell shlCreateNode;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public StopRunNS2Dialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateNode.open();
		shlCreateNode.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateNode.isDisposed()) {
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
		shlCreateNode = new Shell(getParent(), getStyle());
		shlCreateNode.setSize(390, 216);
		shlCreateNode.setText("NS2 running dialog");
		
		Composite composite = new Composite(shlCreateNode, SWT.BORDER);
		composite.setBounds(0, 140, 378, 50);
		
		
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreateNode.dispose();
			}
		});
		btnNewButton_1.setBounds(159, 10, 75, 25);
		btnNewButton_1.setText("Kill");
		
		Composite composite_1 = new Composite(shlCreateNode, SWT.NONE);
		composite_1.setBounds(0, 70, 378, 64);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(23, 22, 345, 25);
		lblNewLabel.setText("NS2 is running now. You can kill it at anytime!");
		
		Composite composite_2 = new Composite(shlCreateNode, SWT.BORDER);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_2.setBounds(0, 0, 378, 64);
		
		Label lblCreateSingleNode = new Label(composite_2, SWT.NONE);
		lblCreateSingleNode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateSingleNode.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblCreateSingleNode.setBounds(10, 10, 356, 34);
		lblCreateSingleNode.setText("NS2 current running...");
		
		shlCreateNode.setDefaultButton(btnNewButton_1);
	}
}
