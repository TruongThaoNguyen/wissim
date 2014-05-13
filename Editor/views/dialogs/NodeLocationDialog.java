package views.dialogs;

import models.DialogResult;
import models.DialogResult.NodeLocationResult;
import models.networkcomponents.WirelessNode;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import controllers.managers.ProjectManager;

public class NodeLocationDialog extends Dialog {

	protected Object result;
	protected Shell shlNodeLocation;
	private Text txtX;
	private Text txtY;
	
	private int nodeID;
	
	private WirelessNode wirelessNode() {
		return (WirelessNode) ProjectManager.getProject().getNetwork().getNodeById(nodeID);
	}

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param wirelessNode() 
	 */
	public NodeLocationDialog(Shell parent, int style, WirelessNode wirelessNode) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.nodeID = wirelessNode().getId();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		initialize();
		
		shlNodeLocation.open();
		shlNodeLocation.layout();
		Display display = getParent().getDisplay();
		while (!shlNodeLocation.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	private void initialize() {
		txtX.setText(wirelessNode().getX() + "");		
		txtY.setText(wirelessNode().getY() + "");
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlNodeLocation = new Shell(getParent(), getStyle());
		shlNodeLocation.setSize(184, 170);
		shlNodeLocation.setText("Node Location");
		
		Label lblX = new Label(shlNodeLocation, SWT.NONE);
		lblX.setBounds(33, 30, 24, 15);
		lblX.setText("X");
		
		Label lblY = new Label(shlNodeLocation, SWT.NONE);
		lblY.setText("Y");
		lblY.setBounds(33, 64, 24, 15);
		
		txtX = new Text(shlNodeLocation, SWT.BORDER);
		txtX.setBounds(63, 27, 76, 21);
		
		txtY = new Text(shlNodeLocation, SWT.BORDER);
		txtY.setBounds(63, 61, 76, 21);
		
		Button btnOk = new Button(shlNodeLocation, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NodeLocationResult r = new DialogResult().new NodeLocationResult();
				
				try {
					r.x = Integer.parseInt(txtX.getText());
					r.y = Integer.parseInt(txtY.getText());					
					result = r;					
					shlNodeLocation.dispose();
				} catch (NumberFormatException exc) {
					MessageDialog.openError(shlNodeLocation, "Error", "Please enter integer values");
					initialize();
				}
			}
		});
		btnOk.setBounds(50, 106, 75, 25);
		btnOk.setText("Ok");

		shlNodeLocation.setDefaultButton(btnOk);
	}
}
