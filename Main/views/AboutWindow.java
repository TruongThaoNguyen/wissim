package views;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

public class AboutWindow extends TitleAreaDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AboutWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected final Control createDialogArea(final Composite parent) {		
		setTitle("WiSSim Editor");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 105;
		container.setLayoutData(gd_container);
		
		Label lblCopyrightBy = new Label(container, SWT.NONE);
		lblCopyrightBy.setAlignment(SWT.CENTER);
		lblCopyrightBy.setBounds(10, 30, 430, 20);
		lblCopyrightBy.setText("© 2014 SEDIC Laboratory, All Rights Reserved.");
		Label lblLab = new Label(container, SWT.NONE);
		lblLab.setAlignment(SWT.CENTER);
		lblLab.setBounds(10, 60, 430, 20);
		lblLab.setText("Trong D. Nguyen");
		
		Label lblSchool = new Label(container, SWT.NONE);
		lblSchool.setAlignment(SWT.CENTER);
		lblSchool.setBounds(10, 80, 430, 20);
		lblSchool.setText("Hanoi University of Science and Technology");
		
		Label lblAddressLabel = new Label(container, SWT.NONE);
		lblAddressLabel.setAlignment(SWT.CENTER);
		lblAddressLabel.setText("http://sedic.soict.hust.edu.vn");
		lblAddressLabel.setBounds(10, 100, 430, 20);
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected final void createButtonsForButtonBar(final Composite parent) {
		parent.setTouchEnabled(true);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected final Point getInitialSize() {
		return new Point(450, 300);
	}
}
