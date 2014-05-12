package views.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import views.Workspace;

public class ExportPDFDialog extends Dialog {
	
	protected Object result;
	protected Shell shlExport;
	private Text txtDirectory;
	private Text txtFileName;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExportPDFDialog(Shell parent, int style, Workspace workspace) {
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
		shlExport.open();
		shlExport.layout();
		Display display = getParent().getDisplay();
		while (!shlExport.isDisposed()) {
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
		shlExport = new Shell(getParent(), getStyle());
		shlExport.setSize(450, 300);
		shlExport.setText("Export to PDF");
		
		Composite composite = new Composite(shlExport, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 444, 61);
		
		Label lblExport = new Label(composite, SWT.NONE);
		lblExport.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		lblExport.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExport.setBounds(10, 10, 249, 33);
		lblExport.setText("Export network to PDF");
		
		Composite composite_1 = new Composite(shlExport, SWT.BORDER);
		composite_1.setBounds(0, 57, 444, 155);
		
		Label lblDirectory = new Label(composite_1, SWT.NONE);
		lblDirectory.setBounds(56, 80, 55, 15);
		lblDirectory.setText("Directory");
		
		txtDirectory = new Text(composite_1, SWT.BORDER);
		txtDirectory.setText("D:\\");
		txtDirectory.setEditable(false);
		txtDirectory.setBounds(117, 79, 161, 21);
		
		Button btnBrowse = new Button(composite_1, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog browseDialog = new DirectoryDialog(shlExport, SWT.NONE);
				browseDialog.setText("Browse");
				browseDialog.setFilterPath("D:/");
				txtDirectory.setText(browseDialog.open());					
			}
		});
		btnBrowse.setBounds(284, 75, 75, 25);
		btnBrowse.setText("Browse");
		
		Label lblFileName = new Label(composite_1, SWT.NONE);
		lblFileName.setBounds(56, 48, 55, 15);
		lblFileName.setText("File Name");
		
		txtFileName = new Text(composite_1, SWT.BORDER);
		txtFileName.setBounds(117, 44, 105, 21);
		txtFileName.setText(workspace.getProject().getNetwork().getName());
		
		Composite composite_2 = new Composite(shlExport, SWT.NONE);
		composite_2.setBounds(0, 218, 444, 53);
		
		Button btnExport = new Button(composite_2, SWT.NONE);
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btnExport.setBounds(250, 10, 75, 25);
		btnExport.setText("Export");
		
		Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
		btnNewButton_1.setBounds(345, 10, 75, 25);
		btnNewButton_1.setText("Cancel");

	}
}
