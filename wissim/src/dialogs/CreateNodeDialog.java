package dialogs;

import model.DialogResult;
import model.DialogResult.CreateSingleNodeResult;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class CreateNodeDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateNode;
	private Text txtPosX;
	private Text txtPosY;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateNodeDialog(Shell parent, int style) {
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
		shlCreateNode.setSize(384, 295);
		shlCreateNode.setText("Create Node");
		
		Composite composite = new Composite(shlCreateNode, SWT.BORDER);
		composite.setBounds(0, 219, 378, 46);
		
		Button btnCreate = new Button(composite, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateSingleNodeResult r = new DialogResult().new CreateSingleNodeResult();
				
				r.posX = Integer.parseInt(txtPosX.getText());
				r.posY = Integer.parseInt(txtPosY.getText());
				
				result = r;
				
				shlCreateNode.dispose();
			}
		});
		btnCreate.setBounds(195, 10, 75, 25);
		btnCreate.setText("Create");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreateNode.dispose();
			}
		});
		btnNewButton_1.setBounds(276, 10, 75, 25);
		btnNewButton_1.setText("Cancel");
		
		Composite composite_1 = new Composite(shlCreateNode, SWT.NONE);
		composite_1.setBounds(0, 70, 378, 143);
		
		txtPosX = new Text(composite_1, SWT.BORDER);
		txtPosX.setBounds(110, 29, 76, 21);
		
		Label lblPositionY = new Label(composite_1, SWT.NONE);
		lblPositionY.setText("Position Y");
		lblPositionY.setBounds(23, 63, 55, 15);
		
		txtPosY = new Text(composite_1, SWT.BORDER);
		txtPosY.setBounds(110, 60, 76, 21);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(23, 32, 55, 15);
		lblNewLabel.setText("Position X");
		
		Composite composite_2 = new Composite(shlCreateNode, SWT.BORDER);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_2.setBounds(0, 0, 378, 64);
		
		Label lblCreateSingleNode = new Label(composite_2, SWT.NONE);
		lblCreateSingleNode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreateSingleNode.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblCreateSingleNode.setBounds(10, 10, 180, 34);
		lblCreateSingleNode.setText("Create Single Node");
		
		shlCreateNode.setDefaultButton(btnCreate);
	}
}
