package views.dialogs;

import models.DialogResult;
import models.DialogResult.CreateNodeSetResult;
import models.networkcomponents.features.Area;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import controllers.managers.ProjectManager;

public class CreateNodeSetDialog extends Dialog {

	protected Object result;
	protected Shell shlCreateASet;
	private Text text;
	
	private Area selectedArea;
	private Text text_1;
	private Text text_2;

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
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private void createContents() {
		shlCreateASet = new Shell(getParent(), getStyle());
		shlCreateASet.setSize(519, 415);
		shlCreateASet.setText("Create A Set of Nodes");
		
		Composite composite = new Composite(shlCreateASet, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 507, 56);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setBounds(10, 10, 194, 33);
		lblNewLabel.setText("Create A Set of Nodes");
		
		Composite composite_1 = new Composite(shlCreateASet, SWT.BORDER);
		composite_1.setBounds(10, 294, 497, 59);
		
		Composite composite_2 = new Composite(shlCreateASet, SWT.NONE);
		composite_2.setBounds(10, 62, 497, 168);
		
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
		lblNumOfNodes.setBounds(10, 26, 67, 21);
		lblNumOfNodes.setText("Quantity");
		
		text = new Text(grpNodes, SWT.BORDER);
		text.setText("10");
		text.setBounds(83, 26, 40, 21);
		
		Label label = new Label(grpNodes, SWT.NONE);
		label.setText("X-Range");
		label.setBounds(251, 26, 55, 21);
		
		Label label_1 = new Label(grpNodes, SWT.NONE);
		label_1.setText("Y-Range");
		label_1.setBounds(251, 54, 55, 23);
		
		text_1 = new Text(grpNodes, SWT.BORDER);
		text_1.setText("10");
		text_1.setBounds(312, 26, 40, 21);
		text_1.setEnabled(false);
		
		text_2 = new Text(grpNodes, SWT.BORDER);
		text_2.setText("10");
		text_2.setBounds(312, 56, 40, 21);
		text_2.setEnabled(false);
		
		combo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(combo.getSelectionIndex() == 1) {
					text_1.setEnabled(true);
					text_2.setEnabled(true);
					text.setEnabled(false);
				}
				else {
					text_1.setEnabled(false);
					text_2.setEnabled(false);
					text.setEnabled(true);
				}

			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				if(combo.getSelectionIndex() == 1) {
					text_1.setEnabled(true);
					text_2.setEnabled(true);
					text.setEnabled(false);
				}
				else {
					text_1.setEnabled(false);
					text_2.setEnabled(false);
					text.setEnabled(true);
				}
			}
		});
		
		Label lblArea = new Label(grpNodes, SWT.NONE);
		lblArea.setBounds(10, 54, 55, 15);
		lblArea.setText("Area");
		
		final Combo combo_2 = new Combo(grpNodes, SWT.READ_ONLY);
		combo_2.setEnabled(false);
		combo_2.setItems(new String[] {"Whole Network", "Selected Area"});
		combo_2.setBounds(83, 50, 129, 23);
		
		
		
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
				boolean b= true;
				CreateNodeSetResult r = new DialogResult().new CreateNodeSetResult();
				switch (combo.getItem(combo.getSelectionIndex())) {
				case "Random":
					r.creationType = CreateNodeSetResult.RANDOM;
					if(text.getText().equals("") || isInteger(text.getText()) == false) {
						MessageDialog.openError(getParent(), "Error", "Number of Node must be integer!");
						b = false;
					}
					if(b == true) {
						r.numOfNodes = Integer.parseInt(text.getText());
					}
					break;
				case "Grid":
					r.creationType = CreateNodeSetResult.GRID;
					if(text_1.getText().equals("") || isInteger(text_1.getText()) == false) {
						MessageDialog.openError(getParent(), "Error", "X range must be integer!");
						b = false;
					}
					if(text_2.getText().equals("") || isInteger(text_2.getText()) == false) {
						MessageDialog.openError(getParent(), "Error", "Y range must be integer!");
						b = false;
					}
					if(b == true) {
						if(Integer.parseInt(text_1.getText()) < 0 || Integer.parseInt(text_1.getText()) > ProjectManager.getProject().getNetwork().getWidth()) {
							MessageDialog.openError(getParent(), "Error", "X range out of network!");
							b = false;
						}
						if(Integer.parseInt(text_2.getText()) < 0 || Integer.parseInt(text_2.getText()) > ProjectManager.getProject().getNetwork().getLength()) {
							MessageDialog.openError(getParent(), "Error", "Y range out of network!");
							b = false;
						}
						if(b == true) {
							r.x_range = Integer.parseInt(text_1.getText());
							r.y_range = Integer.parseInt(text_2.getText());
						}
					}
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
				if(b == true) {
					result = r;
					shlCreateASet.dispose();
				}
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
