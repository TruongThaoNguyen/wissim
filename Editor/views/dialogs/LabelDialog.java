package views.dialogs;

import models.networkcomponents.Node;
import models.networkcomponents.features.GraphicLabel;
import models.networkcomponents.features.Label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import views.Workspace;
import controllers.managers.ProjectManager;

public class LabelDialog extends Dialog {

	protected Object result;
	protected Shell shlLabels;
	private Table table;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LabelDialog(Shell parent, int style, Workspace workspace) {
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
		updateTable();
		
		shlLabels.open();
		shlLabels.layout();
		Display display = getParent().getDisplay();
		while (!shlLabels.isDisposed()) {
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
		shlLabels = new Shell(getParent(), getStyle());
		shlLabels.setSize(477, 351);
		shlLabels.setText("Labels");
		
		table = new Table(shlLabels, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(29, 31, 324, 261);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnColor = new TableColumn(table, SWT.NONE);
		tblclmnColor.setWidth(50);
		tblclmnColor.setText("Color");
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnNode = new TableColumn(table, SWT.NONE);
		tblclmnNode.setWidth(168);
		tblclmnNode.setText("Nodes");
		
		Button btnAddNew = new Button(shlLabels, SWT.NONE);
		btnAddNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object result = new CreateLabelDialog(getParent(), SWT.SHEET, workspace.getProject()).open();
				
				if (result != null) {
					updateTable();
					workspace.getCareTaker().save(workspace.getProject(), "Create new label");
				}
			}
		});
		btnAddNew.setBounds(376, 31, 75, 25);
		btnAddNew.setText("Add New");
		
		Button btnRemove = new Button(shlLabels, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {							
				TableItem[] items = table.getSelection();
				int[] indices = new int[table.getSelection().length];
				int index = 0;
				
				for (int i = 0; i < items.length; i++) {
					boolean isRemove = ProjectManager.removeLabel(items[i].getText(1));
					
					if (isRemove == true) {
						for (int j = 0; j < table.getItemCount(); j++) {
							if (items[i].getText(1).equals(table.getItem(j).getText(1))) {
								indices[index] = j;
								index++;
								break;
							}
						}
						
						workspace.getCareTaker().save(workspace.getProject(), "Remove label(s)");
					}
				}
				
				table.remove(indices);				
			}
		});
		btnRemove.setBounds(376, 62, 75, 25);
		btnRemove.setText("Remove");
		
		Button btnClose = new Button(shlLabels, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlLabels.dispose();
			}
		});
		btnClose.setBounds(376, 267, 75, 25);
		btnClose.setText("Close");

		shlLabels.setDefaultButton(btnClose);
	}
	
	private void updateTable() {
		table.removeAll();
		for (Label l : workspace.getProject().getLabelList()) {						
			TableItem item = new TableItem(table, SWT.NONE);
			
			String str = "";
			for (Node n : l.getNodeList())
				str += n.getId() + ", ";
			
			if (str.length() > 0)
				str = str.substring(0, str.length() - 2);
		
			Image img = new Image(getParent().getDisplay(), 40, 15);
			GC gc = new GC(img);
			
			java.awt.Color c = ((GraphicLabel)l).getColor();
			Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
			gc.setBackground(color);
			gc.fillRectangle(img.getBounds());
			gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
			gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
			gc.dispose();
			
			item.setText(new String[] {"", l.getName(), str});
			item.setImage(0, img);					
		}		
	}
}
