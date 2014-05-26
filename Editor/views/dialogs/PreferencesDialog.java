package views.dialogs;

import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;

import views.Editor;
import views.Workspace;
import views.dialogs.preferences.composites.PreferenceGeneralComposite;
import views.dialogs.preferences.composites.PreferenceNetworkComposite;
import views.dialogs.preferences.composites.PreferenceObstacleComposite;
import views.dialogs.preferences.composites.PreferencePathsComposite;
import controllers.managers.ApplicationSettings;

public class PreferencesDialog extends Dialog {

	protected Object result;
	protected Shell shlPreferences;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	Composite mainComposite;
	List list;
	Composite composite = null;
	
	private Editor mainWindow;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PreferencesDialog(Shell parent, int style, Editor mainWindow) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.mainWindow = mainWindow;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlPreferences.open();
		shlPreferences.layout();
		Display display = getParent().getDisplay();
		while (!shlPreferences.isDisposed()) {
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
		shlPreferences = new Shell(getParent(), getStyle());
		shlPreferences.setSize(531, 307);
		shlPreferences.setText("Preferences");
		
		Composite composite_1 = new Composite(shlPreferences, SWT.BORDER);
		composite_1.setBounds(0, 230, 524, 49);
		
		Button btnOk = new Button(composite_1, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ApplicationSettings.saveGraphicConfig();
					
					Workspace w = mainWindow.getWorkspace();
						for (Control c : w.getChildren())
							c.redraw();
				} catch (Exception e1) {
					MessageDialog.openError(getParent(), "Error", "Cannot save preference changes.\r\n" +
							"Please try again later");
				}
				shlPreferences.close();
			}
		});
		btnOk.setBounds(324, 10, 75, 25);
		formToolkit.adapt(btnOk, true, true);
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlPreferences.close();
			}
		});
		btnCancel.setBounds(418, 10, 75, 25);
		formToolkit.adapt(btnCancel, true, true);
		btnCancel.setText("Cancel");
		
		Button btnRestoreDefault = new Button(composite_1, SWT.NONE);
		btnRestoreDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ApplicationSettings.restoreDefaultGraphicSettings();
				applyListSelection();
			}
		});
		btnRestoreDefault.setBounds(10, 10, 99, 25);
		formToolkit.adapt(btnRestoreDefault, true, true);
		btnRestoreDefault.setText("Restore Default");
		
		mainComposite = new Composite(shlPreferences, SWT.BORDER);
		mainComposite.setBounds(125, 0, 399, 235);
		
		Composite composite_3 = new Composite(shlPreferences, SWT.BORDER);
		composite_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_3.setBounds(0, 0, 130, 235);
		
		list = new List(composite_3, SWT.NONE);
		list.setItems(new String[] {"General", "Network", "Paths", "Obstacles"});
		list.setBounds(10, 21, 110, 193);
		formToolkit.adapt(list, true, true);
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyListSelection();
			}
		});
		list.setSelection(0);
		applyListSelection();
	}
	
	private void applyListSelection() {
		for (Control c : mainComposite.getChildren())
			c.dispose();
		
		String selectedText = list.getSelection()[0];				
		
		switch (selectedText) {
		case "General":
			composite = new PreferenceGeneralComposite(mainComposite, SWT.NONE);
			break;
		case "Network":
			composite = new PreferenceNetworkComposite(mainComposite, SWT.NONE);					
			break;
		case "Paths":
			composite = new PreferencePathsComposite(mainComposite, SWT.NONE);
			break;
		case "Obstacles":
			composite = new PreferenceObstacleComposite(mainComposite, SWT.NONE);
			break;
		default:
			composite = null;
		}
		
		if (composite != null)
			composite.setBounds(0, 0, 388, 224);
	}
}
