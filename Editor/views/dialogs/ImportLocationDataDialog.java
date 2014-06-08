package views.dialogs;

import java.awt.Point;
import java.io.*;
import java.util.Scanner;

import models.DialogResult;
import models.DialogResult.ImportLocationDataResult;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ImportLocationDataDialog extends Dialog {

	protected Object result;
	protected Shell shlImportLocationData;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ImportLocationDataDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlImportLocationData.open();
		shlImportLocationData.layout();
		Display display = getParent().getDisplay();
		while (!shlImportLocationData.isDisposed()) {
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
		shlImportLocationData = new Shell(getParent(), getStyle());
		shlImportLocationData.setSize(450, 300);
		shlImportLocationData.setText("Import Location Data");
		
		Composite composite = new Composite(shlImportLocationData, SWT.BORDER);
		composite.setBounds(0, 217, 444, 54);
		
		Composite composite_1 = new Composite(shlImportLocationData, SWT.BORDER);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setBounds(0, 0, 444, 64);
		
		Label lblImportLocationData = new Label(composite_1, SWT.NONE);
		lblImportLocationData.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblImportLocationData.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblImportLocationData.setBounds(10, 10, 203, 40);
		lblImportLocationData.setText("Import Location Data");
		
		Composite composite_2 = new Composite(shlImportLocationData, SWT.BORDER);
		composite_2.setBounds(0, 55, 444, 171);
		
		Label lblInputFile = new Label(composite_2, SWT.NONE);
		lblInputFile.setBounds(45, 29, 55, 15);
		lblInputFile.setText("Input File");
		
		text = new Text(composite_2, SWT.BORDER);
		text.setEditable(false);
		text.setBounds(45, 48, 239, 21);
		
		Button btnBrowse = new Button(composite_2, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(new FileDialog(getParent(), SWT.NONE).open());				
			}
		});
		btnBrowse.setBounds(305, 46, 75, 25);
		btnBrowse.setText("Browse");
		
		Button btnOk = new Button(composite, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (text.getText() == null || text.getText().equals(""))
					return;
				
				Scanner scanner = null;
				ImportLocationDataResult r = null;
				try {
					scanner = new Scanner(new BufferedReader(new FileReader(text.getText())));
					r = new DialogResult().new ImportLocationDataResult();
					while (scanner.hasNext()) {
						scanner.nextInt();
						int x = scanner.nextInt();
						int y = scanner.nextInt();
						r.pointList.add(new Point(x, y));
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				
				result = r;				
				shlImportLocationData.close();
			}
		});
		btnOk.setBounds(259, 19, 75, 25);
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlImportLocationData.close();
			}
		});
		btnCancel.setBounds(340, 19, 75, 25);
		btnCancel.setText("Cancel");
		
		shlImportLocationData.setDefaultButton(btnOk);
	}
}
