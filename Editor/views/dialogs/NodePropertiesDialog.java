package views.dialogs;

import models.networkcomponents.WirelessNode;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import controllers.graphicscomponents.GWirelessNode;
import view.Workspace;

public class NodePropertiesDialog extends Dialog {

	protected Object result;
	protected Shell shlNodeProperties;
	private Table table;

	private GWirelessNode gnode;
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NodePropertiesDialog(Shell parent, int style, Workspace workspace, GWirelessNode gnode) {
		super(parent, style);
		setText("SWT Dialog");

		this.gnode = gnode;
		this.workspace = workspace;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlNodeProperties.open();
		shlNodeProperties.layout();
		Display display = getParent().getDisplay();
		while (!shlNodeProperties.isDisposed()) {
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
		shlNodeProperties = new Shell(getParent(), getStyle());
		shlNodeProperties.setSize(323, 287);
		shlNodeProperties.setText("Node Properties");

		final WirelessNode node = gnode.getWirelessNode();

		table = new Table(shlNodeProperties, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 22, 297, 178);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnProperties = new TableColumn(table, SWT.NONE);
		tblclmnProperties.setWidth(142);
		tblclmnProperties.setText("Properties");

		TableColumn tblclmnValues = new TableColumn(table, SWT.NONE);
		tblclmnValues.setWidth(151);
		tblclmnValues.setText("Values");

		TableItem tblItemId = new TableItem(table, SWT.NONE);
		tblItemId.setText(new String[] { "Id", node.getId() + "" });
		tblItemId.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

		TableItem tblItemName = new TableItem(table, SWT.NONE);
		tblItemName.setText(new String[] { "Name", node.getName() });


		TableItem tblItemRange = new TableItem(table, SWT.NONE);
		tblItemRange.setText(new String[] { "Range", node.getRange() + "" });
		tblItemRange.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

		TableItem tblItemPosX = new TableItem(table, SWT.NONE);
		tblItemPosX.setText(new String[] { "X", node.getX() + "" });		

		TableItem tblItemPosY = new TableItem(table, SWT.NONE);
		tblItemPosY.setText(new String[] { "Y", node.getY() + "" });	

		Button btnOk = new Button(shlNodeProperties, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNodeProperties.dispose();
			}
		});
		btnOk.setBounds(118, 216, 75, 25);
		btnOk.setText("Ok");

		// handle table editor 
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);				
				int valueColumnIndex = 1;
				
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					final TableItem item = table.getItem(index);
					
					if (item.getText(0) == "Name" || item.getText(0) == "X" || item.getText(0) == "Y") {
						Rectangle rect = item.getBounds(valueColumnIndex);
						
						if (rect.contains(pt)) {
							final int column = valueColumnIndex;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										switch (item.getText(0)) {
										case "Name":
											node.setName(text.getText());
											item.setText(column, text.getText());
											
											workspace.getCareTaker().save(workspace.getProject(), "Edit node name");
											break;
										case "X":
											try {
												int x = Integer.parseInt(text.getText());
												node.setPosition(x, node.getY());
												item.setText(column, text.getText());
												
												workspace.getCareTaker().save(workspace.getProject(), "Edit node x-location");
											} catch (NumberFormatException exc) {
												MessageDialog.openError(getParent(), "Invalid Input", "Please enter an integer value");
											}
											break;
										case "Y":
											try {
												int y = Integer.parseInt(text.getText());
												node.setPosition(node.getX(), y);
												item.setText(column, text.getText());
												
												workspace.getCareTaker().save(workspace.getProject(), "Edit node y-location");
											} catch (NumberFormatException exc) {
												MessageDialog.openError(getParent(), "Invalid Input", "Please enter an integer value");
											}											
											break;
										default:
											item.setText(column, text.getText());											
										}

										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											switch (item.getText(0)) {
											case "Name":
												node.setName(text.getText());
												item.setText(column, text.getText());	
												
												workspace.getCareTaker().save(workspace.getProject(), "Edit node name");
												break;
											case "X":
												try {
													int x = Integer.parseInt(text.getText());
													node.setPosition(x, node.getY());
													item.setText(column, text.getText());
													
													workspace.getCareTaker().save(workspace.getProject(), "Edit node x-location");
												} catch (NumberFormatException exc) {
													MessageDialog.openError(getParent(), "Invalid Input", "Please enter an integer value");
												}
												break;
											case "Y":
												try {
													int y = Integer.parseInt(text.getText());
													node.setPosition(node.getX(), y);
													item.setText(column, text.getText());
													
													workspace.getCareTaker().save(workspace.getProject(), "Edit node y-location");
												} catch (NumberFormatException exc) {
													MessageDialog.openError(getParent(), "Invalid Input", "Please enter an integer value");
												}											
												break;
											default:
												item.setText(column, text.getText());											
											}
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, valueColumnIndex);
							text.setText(item.getText(valueColumnIndex));
							text.selectAll();
							text.setFocus();
							return;
						}
					}
					index++;
				}
			}
		});	

		shlNodeProperties.setDefaultButton(btnOk);
	}
}
