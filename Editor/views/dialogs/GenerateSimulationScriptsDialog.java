package views.dialogs;

import java.io.File;
import java.io.FileNotFoundException;

import models.Project;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;

import controllers.helpers.Helper;
import controllers.helpers.Validator;
import controllers.managers.ProjectManager;
import view.Workspace;

public class GenerateSimulationScriptsDialog extends Dialog {

	protected Object result;
	protected Shell shlGenerateSimulationScripts;
	private Text txtD;
	private Text txtName;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public GenerateSimulationScriptsDialog(Shell parent, int style, Workspace workspace) {
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
		shlGenerateSimulationScripts.open();
		shlGenerateSimulationScripts.layout();
		Display display = getParent().getDisplay();
		while (!shlGenerateSimulationScripts.isDisposed()) {
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
		shlGenerateSimulationScripts = new Shell(getParent(), getStyle());
		shlGenerateSimulationScripts.setSize(450, 384);
		shlGenerateSimulationScripts.setText("Generate Simulation Scripts");
		
		Composite composite = new Composite(shlGenerateSimulationScripts, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 444, 64);
		
		Label lblGenerateSimulationScripts = new Label(composite, SWT.NONE);
		lblGenerateSimulationScripts.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblGenerateSimulationScripts.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblGenerateSimulationScripts.setBounds(10, 10, 257, 44);
		lblGenerateSimulationScripts.setText("Generate Simulation Scripts");
		
		Composite composite_1 = new Composite(shlGenerateSimulationScripts, SWT.NONE);
		composite_1.setBounds(0, 70, 444, 230);
		
		Group grpSimulationSetup = new Group(composite_1, SWT.NONE);
		grpSimulationSetup.setText("Simulation Setup");
		grpSimulationSetup.setBounds(10, 10, 424, 141);
		
		Button btnBrowse = new Button(grpSimulationSetup, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog browseDialog = new DirectoryDialog(shlGenerateSimulationScripts, SWT.NONE);
				browseDialog.setText("Browse");
				browseDialog.setFilterPath("C:/");
				
				String dir = browseDialog.open();
				
				if (dir != null)
					txtD.setText(browseDialog.open());				
			}
		});
		btnBrowse.setBounds(327, 95, 75, 25);
		btnBrowse.setText("Browse");
		
		txtD = new Text(grpSimulationSetup, SWT.BORDER);
		txtD.setBounds(71, 97, 239, 21);
		txtD.setText("D:\\");
		txtD.setEditable(false);
		
		Label lblDirectory = new Label(grpSimulationSetup, SWT.NONE);
		lblDirectory.setBounds(10, 100, 55, 15);
		lblDirectory.setText("Directory");
		
		txtName = new Text(grpSimulationSetup, SWT.BORDER);
		txtName.setBounds(71, 70, 172, 21);
		txtName.setText(workspace.getProject().getNetwork().getName());
		
		Label lblNewLabel = new Label(grpSimulationSetup, SWT.NONE);
		lblNewLabel.setBounds(10, 73, 55, 15);
		lblNewLabel.setText("Name");
		
		final Combo combo = new Combo(grpSimulationSetup, SWT.READ_ONLY);
		combo.setBounds(71, 41, 249, 27);
		combo.setItems(new String[] {"Network Simulator 2 (ns2)", "Omnet++"});
		combo.select(0);
		
		Label lblSimulator = new Label(grpSimulationSetup, SWT.NONE);
		lblSimulator.setBounds(10, 45, 55, 15);
		lblSimulator.setText("Simulator");
		
		Group grpTraceOptions = new Group(composite_1, SWT.NONE);
		grpTraceOptions.setText("Trace Options");
		grpTraceOptions.setBounds(10, 157, 424, 63);
		
		Button btnNamTrace = new Button(grpTraceOptions, SWT.CHECK);
		btnNamTrace.setBounds(96, 26, 93, 16);
		btnNamTrace.setText("Nam Trace");
		
		Button btnWissimTrace = new Button(grpTraceOptions, SWT.CHECK);
		btnWissimTrace.setBounds(225, 26, 93, 16);
		btnWissimTrace.setText("WiSSim Trace");
		
		Composite composite_2 = new Composite(shlGenerateSimulationScripts, SWT.BORDER);
		composite_2.setBounds(0, 306, 444, 49);
		
		Button btnGenerate = new Button(composite_2, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// set up path to save file
				String path = Validator.getFilePath(txtD.getText(), Helper.getFileNameWithExt(txtName.getText(), "tcl"));
				
				File f = new File(path);

				boolean confirmed = true;
				if (f.exists() == true)
					confirmed = MessageDialog.openQuestion(shlGenerateSimulationScripts, "File Existed", "File existed in the selected directory. Do you want to override the existing file by the new one?");

				if (confirmed == true) {
					Project project = workspace.getProject();		
					int type = ProjectManager.TCL;
					switch (combo.getText()) {
					case "Network Simulator 2 (ns2)":
						type = ProjectManager.TCL;
						break;
					case "Omnet++":
						type = ProjectManager.OMNET;
						break;
					}
					
					try {
						ProjectManager.generateScript(project, path, type);
						MessageDialog.openInformation(shlGenerateSimulationScripts, "Generate Scripts", "The scripts are generated successfully");
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						MessageDialog.openError(getParent(), "Error", "Some errors happened. Cannot generate simulation scripts\r\n" +
								"Try again later");
					}
					
					shlGenerateSimulationScripts.close();
				}
			}
		});
		btnGenerate.setBounds(259, 10, 75, 25);
		btnGenerate.setText("Generate");
		
		Button btnCancel = new Button(composite_2, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlGenerateSimulationScripts.dispose();
			}
		});
		btnCancel.setBounds(348, 10, 75, 25);
		btnCancel.setText("Cancel");

	}
}
