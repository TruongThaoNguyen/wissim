package views.dialogs;

import java.util.Random;

import models.networkcomponents.features.GraphicLabel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import controllers.managers.ProjectManager;

public class CreateLabelDialog extends Dialog {

	protected Object result;
	protected Shell shlAddLabel;
	private Text txtLabel;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param project 
	 */
	public CreateLabelDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		shlAddLabel.open();
		shlAddLabel.layout();
		Display display = getParent().getDisplay();
		while (!shlAddLabel.isDisposed()) {
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
		shlAddLabel = new Shell(getParent(), getStyle());
		shlAddLabel.setSize(284, 152);
		shlAddLabel.setText("Add Label");
		
		Label lblLabelName = new Label(shlAddLabel, SWT.NONE);
		lblLabelName.setBounds(25, 24, 37, 15);
		lblLabelName.setText("Label");
		
		txtLabel = new Text(shlAddLabel, SWT.BORDER);
		txtLabel.setBounds(75, 21, 96, 21);
		
		final Canvas canvas = new Canvas(shlAddLabel, SWT.BORDER);
		canvas.setBounds(75, 48, 96, 23);
		
		Random rand = new Random();
		canvas.setBackground(new Color(getParent().getDisplay(), rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));		
		
		Button btnNewButton = new Button(shlAddLabel, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RGB color = new ColorDialog(shlAddLabel).open();
				
				if (color != null) 
					canvas.setBackground(new Color(getParent().getDisplay(), color.red, color.green, color.blue));
			}
		});
		btnNewButton.setBounds(182, 46, 75, 25);
		btnNewButton.setText("Choose");
		
		Label lblColor = new Label(shlAddLabel, SWT.NONE);
		lblColor.setBounds(25, 56, 42, 15);
		lblColor.setText("Color");
		
		Button btnAdd = new Button(shlAddLabel, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txtLabel.getText() == "") {
					MessageDialog.openError(getParent(), "Label Name Missing", "Please enter a label name");
					return;
				}
				
				java.awt.Color color = new java.awt.Color(canvas.getBackground().getRed(), canvas.getBackground().getGreen(), canvas.getBackground().getBlue());
				GraphicLabel label = new GraphicLabel(txtLabel.getText(), color);
				
				if (!ProjectManager.addNewLabel(label)) {
					MessageDialog.openError(getParent(), "Error", "A label with this name is existed. Try a different name");
					return;
				}
				
				result = label;
				shlAddLabel.dispose();
			}
		});
		btnAdd.setBounds(59, 88, 75, 25);
		btnAdd.setText("Add");
		
		Button btnCancel = new Button(shlAddLabel, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlAddLabel.dispose();
			}
		});
		btnCancel.setBounds(146, 88, 75, 25);
		btnCancel.setText("Cancel");
		
		shlAddLabel.setDefaultButton(btnAdd);
	}
}
