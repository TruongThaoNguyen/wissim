package view;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;

public class MainWindow extends ApplicationWindow {
	private Button btnEditor;
	private Button btnAnalyzer;
	private Editor editor;
	private Analyzer analyzer;
	private Composite mainComposite;
	private MenuManager menuManager;
	private MenuManager menuManager_File;
	private Action actNew;
	private Action actOpen;
	private Action actSave;
	private Action actSaveAs;
	private Action actClose;
	private StatusLineManager statusLineManager;

	/**
	 * Create the application window.
	 */
	public MainWindow() {
		super(null);
		createActions();
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Composite featureComposite = new Composite(container, SWT.NONE);
		featureComposite.setLayout(new GridLayout(1, false));
		featureComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
				
		mainComposite = new Composite(container, SWT.NONE);
		mainComposite.setLayout(new StackLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		editor   = new Editor(mainComposite, menuManager, statusLineManager);		
		analyzer = new Analyzer(mainComposite, menuManager, statusLineManager);
		
		btnEditor = new Button(featureComposite, SWT.PUSH);
		btnEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnEditor.setText("Editor");
		btnEditor.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event e) 
			{
				((StackLayout)mainComposite.getLayout()).topControl = editor;
				mainComposite.layout();						
				editor.UpdateMenu();					
			}
		});
		
		btnAnalyzer = new Button(featureComposite, SWT.PUSH);
		btnAnalyzer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAnalyzer.setText("Analyzer");
		btnAnalyzer.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event e) 
			{
				((StackLayout)mainComposite.getLayout()).topControl = analyzer;
				mainComposite.layout();
				analyzer.UpdateMenu();
			}
		});
	
		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			actNew = new Action("New ...") {
			};
		}
		{
			actOpen = new Action("Open ...") {
			};
		}
		{
			actSave = new Action("Save") {
			};
		}
		{
			actSaveAs = new Action("Save as ...") {
			};
		}
		{
			actClose = new Action("Close") {
			};
		}
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		menuManager = new MenuManager("menu");		
		return menuManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			MainWindow window = new MainWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Wissim");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
