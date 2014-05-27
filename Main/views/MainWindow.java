package views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StackLayout;

import views.Analyzer;
import views.Editor;

/**
 * Author: Trong Nguyen
 * 
 */

public class MainWindow extends ApplicationWindow {
	/**
	 * button to switch to Editor.
	 */
//	private Button btnEditor;
//	
//	/**
//	 * 
//	 */
	private Editor editor;
	private static Analyzer analyzer;
	private Visualizer visualizer;
	private MenuManager menuManager;
	private StatusLineManager statusLineManager;

	/**
	 * Create the application window.
	 */
	public MainWindow(Shell shell) {
		super(shell);
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
				
		final Composite mainComposite = new Composite(container, SWT.NONE);
		mainComposite.setLayout(new StackLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		
		editor   = new Editor(mainComposite, menuManager, statusLineManager);		
//		analyzer = new Analyzer(mainComposite, menuManager, statusLineManager);
//		visualizer = new Visualizer(mainComposite, menuManager, statusLineManager);
		
		Button btnEditor = new Button(featureComposite, SWT.PUSH);
		btnEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnEditor.setText("Editor");
		btnEditor.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event e) 
			{
				((StackLayout)mainComposite.getLayout()).topControl = editor;
//				mainComposite.setLayout(new FillLayout());
				mainComposite.layout();						
				editor.UpdateMenu();					
			}
		});
		
		Button btnAnalyzer = new Button(featureComposite, SWT.PUSH);
		btnAnalyzer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAnalyzer.setText("Analyzer");
		btnAnalyzer.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event e) 
			{
				
				if(analyzer == null || !Analyzer.checkFileFormat){
					analyzer = new Analyzer(mainComposite, menuManager, statusLineManager);	
					System.out.println("Init completed");
				}
				((StackLayout)mainComposite.getLayout()).topControl = analyzer;
				mainComposite.layout();
				analyzer.UpdateMenu();
			}
		});
		
		Button btnVisualizer = new Button(featureComposite, SWT.PUSH);
		btnVisualizer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnVisualizer.setText("Visualizer");
		btnVisualizer.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event e) 
			{
				((StackLayout)mainComposite.getLayout()).topControl = visualizer;
				mainComposite.layout();
				visualizer.UpdateMenu();
			}
		});
	
		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
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
			Display display = new Display();
			Shell shell = new Shell(display);
			MainWindow window = new MainWindow(shell);
			
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
		return new Point(720, 490);
	}
	
	public Editor getEditor() {
		return editor;
	}
	
	public static Analyzer getAnalyzer() {
		return analyzer;
	}
	public static void setAnalyzer() {
//		reset(analyzer);
	}
}
