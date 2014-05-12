package views;

import models.DialogResult;
import models.DialogResult.CreateProjectResult;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;

import controllers.helpers.Helper;
import controllers.helpers.Validator;
import controllers.managers.ApplicationSettings;

public class CreateProjectDialog extends Dialog {
	protected Object result;
	protected Shell shlCreateNewProject;
	private Text txtWidth;
	private Text txtLength;
	private Text txtTime;
	private Text txtName;
	private Text txtDirectory;
	
	private String defaultName;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateProjectDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public CreateProjectDialog(Shell parent, int style, String defaultName) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.defaultName = defaultName;  
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateNewProject.open();
		shlCreateNewProject.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateNewProject.isDisposed()) {
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
		shlCreateNewProject = new Shell(getParent(), SWT.SHEET);
		shlCreateNewProject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlCreateNewProject.setSize(450, 349);
		shlCreateNewProject.setText("Create New Project");
		
		Label lblNewProject = new Label(shlCreateNewProject, SWT.NONE);
		lblNewProject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewProject.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblNewProject.setBounds(10, 10, 163, 35);
		lblNewProject.setText("New Project");
		
		Composite composite = new Composite(shlCreateNewProject, SWT.BORDER);
		composite.setBounds(0, 59, 446, 261);
		
		txtDirectory = new Text(composite, SWT.BORDER);
		txtDirectory.setText(Validator.getHomePath());
		txtDirectory.setEnabled(false);
		txtDirectory.setBounds(93, 60, 224, 21);
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setBounds(31, 36, 55, 15);
		label_4.setText("Name");
		
		txtName = new Text(composite, SWT.BORDER);
		txtName.setText(defaultName);
		txtName.setBounds(93, 33, 224, 21);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setBounds(31, 63, 55, 15);
		label_3.setText("Directory");
		
		Group group = new Group(composite, SWT.NONE);
		group.setBounds(23, 94, 392, 106);
		group.setText("Network Setup");
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Width");
		label.setBounds(38, 33, 55, 15);
		
		txtWidth = new Text(group, SWT.BORDER);
		txtWidth.setText(ApplicationSettings.networkSize.x + "");
		txtWidth.setBounds(99, 30, 76, 21);
		
		Label lblLength = new Label(group, SWT.NONE);
		lblLength.setText("Length");
		lblLength.setBounds(38, 60, 55, 15);
		
		txtLength = new Text(group, SWT.BORDER);
		txtLength.setText(ApplicationSettings.networkSize.y + "");
		txtLength.setBounds(99, 57, 76, 21);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("Time");
		label_2.setBounds(261, 60, 36, 15);
		
		txtTime = new Text(group, SWT.BORDER);
		txtTime.setText("200");
		txtTime.setBounds(303, 57, 42, 21);
		
		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog browseDialog = new DirectoryDialog(shlCreateNewProject, SWT.NONE);
				browseDialog.setText("Browse");
				browseDialog.setFilterPath("C:/");
				if(browseDialog.open() != null)
					txtDirectory.setText(browseDialog.open());
			}
		});
		btnBrowse.setBounds(340, 59, 75, 25);
		btnBrowse.setText("Browse");
		
		Button btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(242, 213, 75, 25);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Helper.isValidName(txtName.getText())) {
					MessageDialog.openError(getParent(), "File Name invalid", "File Name invalid");
					return;
				}
				
				// get the result
				CreateProjectResult r = new DialogResult().new CreateProjectResult();
				
				r.name	 = txtName.getText();
				r.path	 = txtDirectory.getText();
				r.width  = Integer.parseInt(txtWidth.getText());
				r.height = Integer.parseInt(txtLength.getText());
				r.time   = Integer.parseInt(txtTime.getText());
				
				result = r;				
				shlCreateNewProject.dispose();
			}
		});
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlCreateNewProject.dispose();
			}
		});
		btnCancel.setBounds(340, 213, 75, 25);
		btnCancel.setText("Cancel");
		
		shlCreateNewProject.setDefaultButton(btnOk);
	}
}
