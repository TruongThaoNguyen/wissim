package views.dialogs;

import models.DialogResult;
import models.DialogResult.EditNetworkSizeResult;
import models.Project;
import models.networkcomponents.WirelessNetwork;

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

import views.Workspace;


public class EditNetworkSizeDialog extends Dialog {
	protected Object result;
	protected Shell shlEditNetworkSize;
	private Text txtWidth;
	private Text txtHeight;
	
	private int widthType = WirelessNetwork.RIGHT, lengthType = WirelessNetwork.BOTTOM;
	private Workspace workspace;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param workspace 
	 */
	public EditNetworkSizeDialog(Shell parent, int style, Workspace workspace) {
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
		shlEditNetworkSize.open();
		shlEditNetworkSize.layout();
		Display display = getParent().getDisplay();
		while (!shlEditNetworkSize.isDisposed()) {
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
		shlEditNetworkSize = new Shell(getParent(), getStyle());
		shlEditNetworkSize.setSize(446, 187);
		shlEditNetworkSize.setText("Edit Network Size");
		
		Button btnApply = new Button(shlEditNetworkSize, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditNetworkSizeResult r = new DialogResult().new EditNetworkSizeResult();
				r.width = Integer.parseInt(txtWidth.getText());
				r.height = Integer.parseInt(txtHeight.getText());
				r.time = Integer.parseInt(text.getText());
				r.wType = widthType;
				r.lType = lengthType;
				
				result = r;
				shlEditNetworkSize.dispose();
			}
		});
		btnApply.setBounds(37, 118, 75, 25);
		btnApply.setText("Apply");
		btnApply.setFocus();
		
		Button btnCancel = new Button(shlEditNetworkSize, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlEditNetworkSize.dispose();
			}
		});
		btnCancel.setBounds(118, 118, 75, 25);
		btnCancel.setText("Cancel");
		
		Label lblWidth = new Label(shlEditNetworkSize, SWT.NONE);
		lblWidth.setBounds(37, 13, 55, 25);
		lblWidth.setText("Width");
		
		txtWidth = new Text(shlEditNetworkSize, SWT.BORDER);
		txtWidth.setText(workspace.getProject().getNetwork().getWidth() + "");
		txtWidth.setBounds(98, 10, 96, 21);
		
		Label lblHeight = new Label(shlEditNetworkSize, SWT.NONE);
		lblHeight.setText("Length");
		lblHeight.setBounds(37, 47, 55, 25);
		
		txtHeight = new Text(shlEditNetworkSize, SWT.BORDER);
		txtHeight.setText(workspace.getProject().getNetwork().getLength() + "");
		txtHeight.setBounds(98, 44, 96, 21);
		
		final Button btnCenter = new Button(shlEditNetworkSize, SWT.NONE);
		btnCenter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.CENTER, WirelessNetwork.CENTER);
			}
		});
		btnCenter.setBounds(287, 37, 75, 65);
		
		Button btnTop = new Button(shlEditNetworkSize, SWT.NONE);
		btnTop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.CENTER, WirelessNetwork.TOP);
			}
		});
		btnTop.setBounds(287, 10, 75, 25);
		btnTop.setText("Top");
		
		Button btnBottom = new Button(shlEditNetworkSize, SWT.NONE);
		btnBottom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.CENTER, WirelessNetwork.BOTTOM);
			}
		});
		btnBottom.setText("Bottom");
		btnBottom.setBounds(287, 108, 75, 25);
		
		Button btnLeft = new Button(shlEditNetworkSize, SWT.NONE);
		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.LEFT, WirelessNetwork.CENTER);
			}
		});
		btnLeft.setText("Left");
		btnLeft.setBounds(239, 37, 42, 65);
		
		Button btnRight = new Button(shlEditNetworkSize, SWT.NONE);
		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.RIGHT, WirelessNetwork.CENTER);
			}
		});
		btnRight.setText("Right");
		btnRight.setBounds(365, 37, 42, 65);
		
		Button btnTopLeft = new Button(shlEditNetworkSize, SWT.NONE);
		btnTopLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.LEFT, WirelessNetwork.TOP);
			}
		});
		btnTopLeft.setText("TL");
		btnTopLeft.setBounds(239, 10, 42, 25);
		
		Button btnTr = new Button(shlEditNetworkSize, SWT.NONE);
		btnTr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.RIGHT, WirelessNetwork.TOP);
			}
		});
		btnTr.setText("TR");
		btnTr.setBounds(365, 10, 42, 25);
		
		Button btnBr = new Button(shlEditNetworkSize, SWT.NONE);
		btnBr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.RIGHT, WirelessNetwork.BOTTOM);
			}
		});
		btnBr.setText("BR");
		btnBr.setBounds(365, 108, 42, 25);
		
		Button btnBl = new Button(shlEditNetworkSize, SWT.NONE);
		btnBl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectMethod(WirelessNetwork.LEFT, WirelessNetwork.BOTTOM);
			}
		});
		btnBl.setText("BL");
		btnBl.setBounds(239, 108, 42, 25);
		
		Label label = new Label(shlEditNetworkSize, SWT.NONE);
		label.setText("Time ");
		label.setBounds(37, 84, 55, 21);
		
		text = new Text(shlEditNetworkSize, SWT.BORDER);
		text.setBounds(98, 81, 96, 21);

	}
	
	private void selectMethod(int wType, int lType) {
		this.widthType = wType;
		this.lengthType = lType;
	}
}
