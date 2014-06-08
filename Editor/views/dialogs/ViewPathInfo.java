package views.dialogs;

import java.util.List;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

import controllers.graphicscomponents.GNetwork;
import controllers.graphicscomponents.GWirelessNode;
import controllers.graphicscomponents.GraphicPath;

public class ViewPathInfo extends Dialog {

	protected Object result;
	protected Shell shlViewPathInfo;
	private Table table;
	
	private GNetwork gnetwork;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param gNetwork 
	 */
	public ViewPathInfo(Shell parent, int style, GNetwork gNetwork) {
		super(parent, style);
		this.gnetwork = gNetwork;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		initialize();
		
		shlViewPathInfo.open();
		shlViewPathInfo.layout();
		Display display = getParent().getDisplay();
		while (!shlViewPathInfo.isDisposed()) {
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
		shlViewPathInfo = new Shell(getParent(), getStyle());
		shlViewPathInfo.setSize(589, 346);
		shlViewPathInfo.setText("View Path Info");
		
		Label lblStartNode = new Label(shlViewPathInfo, SWT.NONE);
		lblStartNode.setBounds(24, 59, 306, 15);
		lblStartNode.setText("Type = SP (Shortest Path) || GP (Greedy Path)");
		
		table = new Table(shlViewPathInfo, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(24, 80, 440, 208);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(44);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnProperty = new TableColumn(table, SWT.NONE);
		tblclmnProperty.setWidth(49);
		tblclmnProperty.setText("Start");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(42);
		tblclmnValue.setText("End");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(65);
		tblclmnNewColumn.setText("Length");
		
		TableColumn tblclmnIsSuccess = new TableColumn(table, SWT.NONE);
		tblclmnIsSuccess.setWidth(69);
		tblclmnIsSuccess.setText("Is Success");
		
		TableColumn tblclmnDetails = new TableColumn(table, SWT.NONE);
		tblclmnDetails.setWidth(163);
		tblclmnDetails.setText("Details");
		
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(new String[] {"SP", "0", "5", "6", "Yes", "0, 6, 8, 7, 3, 2, 5"});
		
		Button btnClose = new Button(shlViewPathInfo, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlViewPathInfo.dispose();
			}
		});
		btnClose.setBounds(487, 263, 75, 25);
		btnClose.setText("Close");
		
		Button btnRemove = new Button(shlViewPathInfo, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int type, startNodeId, endNodeId;
				
				for (TableItem tblItem : table.getSelection()) {
					
					switch (tblItem.getText(0)) {
					case "SP":
						type = GraphicPath.SHORTEST;
						break;
					case "GP":
						type = GraphicPath.GREEDY;
						break;
					default:
						type = GraphicPath.USER_DEFINED;
					}
					
					startNodeId = Integer.parseInt(tblItem.getText(1));
					endNodeId = Integer.parseInt(tblItem.getText(2));
					
					GraphicPath path = gnetwork.getVisualizeManager().getPath(type, startNodeId, endNodeId);
					gnetwork.getVisualizeManager().removePath(path);
					gnetwork.redraw();
					
					tblItem.dispose();
				}		
			}
		});
		btnRemove.setBounds(487, 80, 75, 25);
		btnRemove.setText("Remove");
		
		Button btnNewButton = new Button(shlViewPathInfo, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gnetwork.getVisualizeManager().removeAllPaths();
				table.removeAll();
				gnetwork.redraw();
			}
		});
		btnNewButton.setBounds(487, 112, 75, 25);
		btnNewButton.setText("Remove All");
		
		Label lblPathnormation = new Label(shlViewPathInfo, SWT.NONE);
		lblPathnormation.setAlignment(SWT.CENTER);
		lblPathnormation.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblPathnormation.setBounds(167, 10, 211, 43);
		lblPathnormation.setText("PATH INFORMATION");

		shlViewPathInfo.setDefaultButton(btnClose);
	}
	
	private void initialize() {
		List<GraphicPath> paths = gnetwork.getVisualizeManager().getPaths();
		
		table.removeAll();
		
		TableItem tableItem;
		String type, isSuccess = "", details;
		for (GraphicPath p : paths) {
			tableItem = new TableItem(table, SWT.NONE);
			
			switch (p.getType()) {
			case GraphicPath.GREEDY:
				type = "GP";
				break;
			case GraphicPath.SHORTEST:
				type = "SP";
				break;
			case GraphicPath.USER_DEFINED:
				type = "UD";
				break;
			default:
					type = "";
			}
			
			if (p.getNodeList().size() > 0) {
				if (p.getNodeList().get(p.getNodeList().size() - 1).getWirelessNode().getId() == p.getEndNode().getWirelessNode().getId()) {
					isSuccess = "Yes"; 
				} else {
					isSuccess = "No";
				}
			}
			
			details = "";
			for (GWirelessNode gnode : p.getNodeList()) {
				details += gnode.getWirelessNode().getId() + ", ";
			}
			
			if (details.length() > 2)
				details = details.substring(0, details.length() - 2);
			
			tableItem.setText(new String[] {
					type, 												// path type
					p.getStartNode().getWirelessNode().getId() + "", 	// start node id
					p.getEndNode().getWirelessNode().getId() + "",		// end node id
					p.getNodeList().size() - 1 + "",					// path length
					isSuccess,											// whether end node is destination node or not
					details});											// path details
		}
	}
}
