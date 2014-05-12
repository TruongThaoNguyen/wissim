package views;

import java.util.ArrayList;

import models.networkcomponents.WirelessNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import controllers.graphicscomponents.GWirelessNode;
import controllers.managers.ProjectManager;


public class SearchNodeDialog extends Dialog {

	protected Object result;
	protected Shell shlSearch;
	private Text text;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SearchNodeDialog(Shell parent, int style, Workspace workspace) {
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
		shlSearch.open();
		shlSearch.layout();
		Display display = getParent().getDisplay();
		while (!shlSearch.isDisposed()) {
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
		shlSearch = new Shell(getParent(), getStyle());
		shlSearch.setSize(230, 177);
		shlSearch.setText("Search");
		
		Label lblSearchFor = new Label(shlSearch, SWT.NONE);
		lblSearchFor.setBounds(21, 32, 62, 15);
		lblSearchFor.setText("Search For");
		
		final Combo combo = new Combo(shlSearch, SWT.READ_ONLY);
		combo.setItems(new String[] {"All", "Node Id", "Node Name"});
		combo.setBounds(94, 28, 107, 23);
		combo.select(0);
		
		Label lblKeyWord = new Label(shlSearch, SWT.NONE);
		lblKeyWord.setBounds(21, 71, 55, 15);
		lblKeyWord.setText("Key word");
		
		text = new Text(shlSearch, SWT.BORDER);
		text.setBounds(94, 68, 107, 21);
		
		Button btnSearch = new Button(shlSearch, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if (workspace == null) return;
				
				String selectedItem = combo.getItems()[combo.getSelectionIndex()];
				switch(selectedItem) {
				case "All":
					try {
						int id = Integer.parseInt(text.getText());
						WirelessNode wnode = ProjectManager.getNodeWithId(workspace.getProject(), id);
						if (wnode != null) {
							workspace.deselectGraphicObjects();
							
							GWirelessNode gnode = workspace.getGraphicNodeById(wnode.getId());
							gnode.setSelect(true);
							gnode.redraw();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				}
				result = new ArrayList<Object>();
			}
		});
		btnSearch.setBounds(36, 113, 75, 25);
		btnSearch.setText("Search");
		
		Button btnNewButton = new Button(shlSearch, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlSearch.dispose();
			}
		});
		btnNewButton.setBounds(117, 113, 75, 25);
		btnNewButton.setText("Close");

		shlSearch.setDefaultButton(btnSearch);
	}

}
