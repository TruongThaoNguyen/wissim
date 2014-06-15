package views.dialogs;

import models.Project;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import views.Workspace;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ViewProjectInfoDialog extends Dialog {

	protected Object result;
	protected Shell shlProjectInformation;
	private Table table;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ViewProjectInfoDialog(Shell parent, int style, Workspace workspace) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		initialize();
		
		shlProjectInformation.open();
		shlProjectInformation.layout();
		Display display = getParent().getDisplay();
		while (!shlProjectInformation.isDisposed()) {
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
		shlProjectInformation = new Shell(getParent(), getStyle());
		shlProjectInformation.setSize(378, 153);
		shlProjectInformation.setText("Project Information");
		
		table = new Table(shlProjectInformation, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 352, 66);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnProperty = new TableColumn(table, SWT.NONE);
		tblclmnProperty.setWidth(103);
		tblclmnProperty.setText("Property");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(242);
		tblclmnValue.setText("Value");
		
		Button btnNewButton = new Button(shlProjectInformation, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlProjectInformation.dispose();
			}
		});
		btnNewButton.setBounds(143, 90, 75, 25);
		btnNewButton.setText("Ok");
	}
	
	private void initialize() {				
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "Absolute Path", Project.getPath() });
		
		TableItem item1 = new TableItem(table, SWT.NONE);
		item1.setText(new String[] { "Created Date", Project.getCreatedDate().toString() });
	}
}
