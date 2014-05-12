package views.dialogs;

import models.DialogResult;
import models.DialogResult.CreateNodeSetResult;
import models.networkcomponents.features.Area;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;

public class CreateNodeSetDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateASet;
	private Text text;
	
	private Area selectedArea;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param selectedArea 
	 */
	public CreateNodeSetDialog(Shell parent, int style, Area selectedArea) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.selectedArea = selectedArea;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateASet.open();
		shlCreateASet.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateASet.isDisposed()) {
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
		shlCreateASet = new Shell(getParent(), getStyle());
		shlCreateASet.setSize(450, 324);
		shlCreateASet.setText("Create A Set of Nodes");
		
		Composite composite = new Composite(shlCreateASet, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 444, 56);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setBounds(10, 10, 194, 33);
		lblNewLabel.setText("Create A Set of Nodes");
		
		Composite composite_1 = new Composite(shlCreateASet, SWT.BORDER);
		composite_1.setBounds(0, 236, 444, 59);
		
		Composite composite_2 = new Composite(shlCreateASet, SWT.NONE);
		composite_2.setBounds(0, 62, 444, 168);
		
		Label lblMethod = new Label(composite_2, SWT.NONE);
		lblMethod.setBounds(22, 29, 55, 15);
		lblMethod.setText("Method");
		
		final Combo combo = new Combo(composite_2, SWT.READ_ONLY);
		combo.setItems(new String[] {"Random", "Grid"});
		combo.setBounds(87, 25, 114, 23);
		combo.select(0);
		
		Group grpNodes = new Group(composite_2, SWT.NONE);
		grpNodes.setText("Nodes");
		grpNodes.setBounds(22, 69, 398, 89);
		
		Label lblNumOfNodes = new Label(grpNodes, SWT.NONE);
		lblNumOfNodes.setBounds(10, 26, 55, 15);
		lblNumOfNodes.setText("Quantity");
		
		text = new Text(grpNodes, SWT.BORDER);
		text.setText("10");
		text.setBounds(71, 23, 40, 21);
		
		Label lblArea = new Label(grpNodes, SWT.NONE);
		lblArea.setBounds(10, 54, 55, 15);
		lblArea.setText("Area");
		
		final Combo combo_2 = new Combo(grpNodes, SWT.READ_ONLY);
		combo_2.setEnabled(false);
		combo_2.setItems(new String[] {"Whole Network", "Selected Area"});
		combo_2.setBounds(71, 50, 129, 23);
		
		if (selectedArea == null)
			combo_2.select(0);
		else
			combo_2.select(1);

		Button btnOk = new Button(composite_1, SWT.NONE);
		btnOk.setBounds(260, 20, 75, 25);
		btnOk.setText("Ok");
		btnOk.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				CreateNodeSetResult r = new DialogResult().new CreateNodeSetResult();
				
				switch (combo.getItem(combo.getSelectionIndex())) {
				case "RANDOM":
					r.creationType = CreateNodeSetResult.RANDOM;
					break;
				case "GRID":
					r.creationType = CreateNodeSetResult.GRID;
					break;
				default:
					r.creationType = CreateNodeSetResult.RANDOM;
				}

				switch (combo_2.getItem(combo_2.getSelectionIndex())) {
				case "Whole Network":
					r.areaType = CreateNodeSetResult.WHOLE_NETWORK;
					break;
				case "Selected Area":
					r.areaType = CreateNodeSetResult.SELECTED_AREA;
					break;
				default:
					r.areaType = CreateNodeSetResult.WHOLE_NETWORK;
				}
				
				r.numOfNodes = Integer.parseInt(text.getText());
				
				result = r;
				shlCreateASet.dispose();
			}
		});
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.setBounds(355, 20, 75, 25);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shlCreateASet.dispose();
			}
		});
	}
}
