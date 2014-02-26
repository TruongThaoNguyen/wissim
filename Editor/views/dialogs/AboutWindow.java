package views.dialogs;

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

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("WiSSim Editor");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 105;
		container.setLayoutData(gd_container);
		
		Label lblCopyrightBy = new Label(container, SWT.NONE);
		lblCopyrightBy.setBounds(10, 34, 396, 15);
		lblCopyrightBy.setText("Copyright 2013 by TechLab, Hanoi University of Science and Technology");
		
		Label lblAuthorLeecom = new Label(container, SWT.NONE);
		lblAuthorLeecom.setBounds(10, 55, 197, 15);
		lblAuthorLeecom.setText("Author: leecom");
		
		Label lblEmailNtrhieugmailcom = new Label(container, SWT.NONE);
		lblEmailNtrhieugmailcom.setBounds(10, 76, 186, 15);
		lblEmailNtrhieugmailcom.setText("Email: ntrhieu89@gmail.com");

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
