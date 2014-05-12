package views.dialogs;

import models.networkcomponents.WirelessNetwork;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import views.Workspace;

public class NetworkPropertiesDialog extends Dialog {

	protected Object result;
	protected Shell shlNetworkInformation;
	private Workspace workspace;
	private Table table;
	private Button btnOk;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NetworkPropertiesDialog(Shell parent, int style, Workspace workspace) {
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
		initialize();
		
		shlNetworkInformation.open();
		shlNetworkInformation.layout();
		Display display = getParent().getDisplay();
		while (!shlNetworkInformation.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void initialize() {	
		final WirelessNetwork network = workspace.getProject().getNetwork();
		
		TableItem tbltmName;		
		tbltmName = new TableItem(table, SWT.NONE);
		tbltmName.setText(new String[] {"Name", network.getName() });
		
		TableItem tbltmWidth;		
		tbltmWidth = new TableItem(table, SWT.NONE);
		tbltmWidth.setText(new String[] {"Width", ((WirelessNetwork) network).getWidth() + "" });
		tbltmWidth.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		
		TableItem tbltmLength;		
		tbltmLength = new TableItem(table, SWT.NONE);
		tbltmLength.setText(new String[] {"Length", ((WirelessNetwork) network).getLength() + "" });
		tbltmLength.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		
		TableItem tbltmNumOfNodes;		
		tbltmNumOfNodes = new TableItem(table, SWT.NONE);
		tbltmNumOfNodes.setText(new String[] {"Nodes Count", ((WirelessNetwork) network).getNodeList().size() + "" });
		tbltmNumOfNodes.setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		
		TableItem tbltmSimulationTime;
		tbltmSimulationTime = new TableItem(table, SWT.NONE);
		tbltmSimulationTime.setText(new String[] {"Simulation Time", ((WirelessNetwork) network).getTime() + ""});
		
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
					
					if (item.getText(0).equals("Name") || item.getText(0).equals("Simulation Time")) {
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
											network.setName(text.getText());
											item.setText(column, text.getText());	
											
											workspace.getCareTaker().save(workspace.getProject(), "Edit network name");
											break;
										case "Simulation Time":
											try {
												int t = Integer.parseInt(text.getText());
												network.setTime(t);
												item.setText(column, text.getText());
												
												workspace.getCareTaker().save(workspace.getProject(), "Edit simulation time");
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
												network.setName(text.getText());
												item.setText(column, text.getText());	
												
												workspace.getCareTaker().save(workspace.getProject(), "Edit network name");
												break;
											case "Simulation Time":
												try {
													int t = Integer.parseInt(text.getText());
													network.setTime(t);
													item.setText(column, text.getText());
													
													workspace.getCareTaker().save(workspace.getProject(), "Edit simulation time");
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
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {		
		shlNetworkInformation = new Shell(getParent(), getStyle());
		shlNetworkInformation.setSize(345, 216);
		shlNetworkInformation.setText("Network Information");
		
		table = new Table(shlNetworkInformation, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 319, 125);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnProperty = new TableColumn(table, SWT.NONE);
		tblclmnProperty.setWidth(100);
		tblclmnProperty.setText("Property");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(210);
		tblclmnValue.setText("Value");
		
		btnOk = new Button(shlNetworkInformation, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNetworkInformation.dispose();
			}
		});
		btnOk.setBounds(128, 152, 75, 25);
		btnOk.setText("Ok");

	}
}
