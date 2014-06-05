package views.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import models.Project;

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

import views.Editor;

public class ParameterConfigDialog extends Dialog {

	protected Object result;
	protected Shell shlParameterConfigure;
	private Table table;
	private String protocolName;
	private Editor editorP;

	private HashMap<String, String> map = new HashMap<String, String>();

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param string 
	 */
	public ParameterConfigDialog(Shell parent, int style, String title,HashMap<String,String>value,Editor editor) {
		super(parent, style);
		setText(title);
		setMap(value);
		setProtocolName(title);
		this.editorP = editor;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlParameterConfigure.open();
		shlParameterConfigure.layout();
		Display display = getParent().getDisplay();
		while (!shlParameterConfigure.isDisposed()) {
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
		shlParameterConfigure = new Shell(getParent(), getStyle());
		shlParameterConfigure.setSize(309, 404);
		shlParameterConfigure.setText(getText());

		table = new Table(shlParameterConfigure, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 26, 287, 282);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item
											.setText(column, text
													.getText());
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
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});		

		TableColumn tblclmnParameter = new TableColumn(table, SWT.NONE);
		tblclmnParameter.setWidth(176);
		tblclmnParameter.setText("Parameter");

		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(103);
		tblclmnValue.setText("Value");

		Set<Entry<String, String>> paramSet = map.entrySet();
		Iterator<Entry<String, String>> iterator = paramSet.iterator();
		while (iterator.hasNext()) {
			Entry<String, String> me = iterator.next();

			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { me.getKey().toString(), me.getValue().toString() });
		}

		Button btnClose = new Button(shlParameterConfigure, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlParameterConfigure.dispose();
			}
		});
		btnClose.setBounds(186, 327, 75, 25);
		btnClose.setText("Cancel");
		
		Button button = new Button(shlParameterConfigure, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LinkedHashMap<String, String> routingProtocolMaps = new LinkedHashMap<String,String>();
				int index = table.getTopIndex();
				int flag = 0;
				while (index < table.getItemCount()) {
					final TableItem item = table.getItem(index);
					if(item.getText(0).equals("")) {
						 MessageDialog.openError(getParent(), "Error", "Parameter must be not empty!");
						 flag = 1;
					} else {
						if(item.getText(1).equals("")) {
							flag = 1;
							MessageDialog.openError(getParent(), "Error", "Value must be not empty!");
						} else {
							routingProtocolMaps.put(item.getText(0), item.getText(1));
						}
					}
					
					index++;
				}
				if(flag == 0) {
					if(routingProtocolMaps != null) {						
						Project.getRoutingProtocols().put(protocolName, routingProtocolMaps);
						boolean b = MessageDialog.openConfirm(getParent(), "Confirm", "Update successfull! Do you want to quit?");
						if(b == true) {
							editorP.getActSave().setEnabled(true);
							shlParameterConfigure.dispose();
						}
					}
				}
			}
		});
		button.setText("Apply");
		button.setBounds(77, 327, 75, 25);
		

	}

	public void addItem(String param, String value) {
		map.put(param, value);
	}

	public void setMap(HashMap<String,String> input) {
		this.map = input;
	}
	
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
}
