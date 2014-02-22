package dialogs;

import java.io.IOException;

import managers.Parser;
import model.Project;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import control.helper.Helper;
import control.helper.Validator;
import control.manager.ProjectManager;

public class GenerateNodeLocationDataDialog extends Dialog {

	protected Object result;
	protected Shell shlNodeLocationData;
	private Text txtDirectory;
	
	private Project project;
	private Text txtFileName;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public GenerateNodeLocationDataDialog(Shell parent, int style, Project project) {
		super(parent, style);
		setText("SWT Dialog");
		this.project = project;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlNodeLocationData.open();
		shlNodeLocationData.layout();
		Display display = getParent().getDisplay();
		while (!shlNodeLocationData.isDisposed()) {
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
		shlNodeLocationData = new Shell(getParent(), getStyle());
		shlNodeLocationData.setSize(450, 314);
		shlNodeLocationData.setText("Node Location Data");
		
		Composite composite = new Composite(shlNodeLocationData, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 444, 64);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setBounds(10, 10, 296, 40);
		lblNewLabel.setText("Generate Node Location Data");
		
		Composite composite_1 = new Composite(shlNodeLocationData, SWT.NONE);
		composite_1.setBounds(0, 70, 444, 163);
		
		Group grpDirectory = new Group(composite_1, SWT.NONE);
		grpDirectory.setText("Directory");
		grpDirectory.setBounds(177, 10, 257, 140);
		
		txtDirectory = new Text(grpDirectory, SWT.BORDER);
		txtDirectory.setEditable(false);
		txtDirectory.setText("D:\\");
		txtDirectory.setBounds(10, 73, 237, 21);
		
		Button btnNewButton = new Button(grpDirectory, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shlNodeLocationData);
				dialog.setText("Browse");
				
				String str = dialog.open();
				if (str != null)
					txtDirectory.setText(str);
			}
		});
		btnNewButton.setBounds(172, 100, 75, 25);
		btnNewButton.setText("Browse");
		
		Label lblFileName = new Label(grpDirectory, SWT.NONE);
		lblFileName.setBounds(10, 38, 55, 15);
		lblFileName.setText("File Name");
		
		txtFileName = new Text(grpDirectory, SWT.BORDER);
		txtFileName.setBounds(83, 35, 164, 21);
		txtFileName.setText(project.getNetwork().getName());
		
		Group grpFileType = new Group(composite_1, SWT.NONE);
		grpFileType.setText("File Type");
		grpFileType.setBounds(10, 10, 148, 140);
		
		final Button btnTextFiletxt = new Button(grpFileType, SWT.RADIO);
		btnTextFiletxt.setSelection(true);
		btnTextFiletxt.setBounds(24, 37, 90, 16);
		btnTextFiletxt.setText("text file (.txt)");
		
		final Button btnxmlFile = new Button(grpFileType, SWT.RADIO);		
		btnxmlFile.setBounds(24, 66, 103, 16);
		btnxmlFile.setText("XML file (.xml)");
		
		Composite composite_2 = new Composite(shlNodeLocationData, SWT.BORDER);
		composite_2.setBounds(0, 239, 444, 46);
		
		Button btnGenerate = new Button(composite_2, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path;
				
				if (btnTextFiletxt.getSelection() == true)
					try {
						path = Validator.getFilePath(txtDirectory.getText(), Helper.getFileNameWithExt(txtFileName.getText(), "txt"));
						ProjectManager.generateNodeLocationData(project, path, Parser.TXT);
						MessageDialog.openInformation(getParent(), "Generate Node Data", "Node location data is generated successfully");
						shlNodeLocationData.close();
					} catch (IOException e1) {
						MessageDialog.openError(shlNodeLocationData, "Error", "IO Error. Please check the directory or folder permission.");
					}
				else if (btnxmlFile.getSelection() == true)
					try {
						path = Validator.getFilePath(txtDirectory.getText(), Helper.getFileNameWithExt(txtFileName.getText(), "xml"));
						ProjectManager.generateNodeLocationData(project, path, Parser.XML);
						MessageDialog.openInformation(getParent(), "Generate Node Data", "Node location data is generated successfully");
						shlNodeLocationData.close();
					} catch (IOException e1) {
						MessageDialog.openError(shlNodeLocationData, "Error", "IO Error. Please check the directory or folder permission.");
					}
			}
		});
		btnGenerate.setBounds(278, 10, 75, 25);
		btnGenerate.setText("Generate");
		
		Button btnCancel = new Button(composite_2, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNodeLocationData.close();
			}
		});
		btnCancel.setBounds(359, 10, 75, 25);
		btnCancel.setText("Cancel");

		shlNodeLocationData.setDefaultButton(btnGenerate);
	}
}
