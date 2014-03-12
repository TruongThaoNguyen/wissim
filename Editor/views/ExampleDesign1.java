package views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class ExampleDesign1 extends Shell {
	private Text text;
	private Text text_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ExampleDesign1 shell = new ExampleDesign1(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ExampleDesign1(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new FormLayout());
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		FormData fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(0, 64);
		fd_sashForm.left = new FormAttachment(0, 60);
		fd_sashForm.bottom = new FormAttachment(0, 104);
		fd_sashForm.right = new FormAttachment(0, 164);
		sashForm.setLayoutData(fd_sashForm);
		
		text = new Text(sashForm, SWT.BORDER);
		sashForm.setWeights(new int[] {1});
		
		SashForm sashForm_1 = new SashForm(this, SWT.VERTICAL);
		FormData fd_sashForm_1 = new FormData();
		fd_sashForm_1.top = new FormAttachment(sashForm, 23);
		fd_sashForm_1.bottom = new FormAttachment(sashForm, 63, SWT.BOTTOM);
		fd_sashForm_1.right = new FormAttachment(sashForm, 0, SWT.RIGHT);
		fd_sashForm_1.left = new FormAttachment(0, 60);
		sashForm_1.setLayoutData(fd_sashForm_1);
		
		text_1 = new Text(sashForm_1, SWT.BORDER);
		sashForm_1.setWeights(new int[] {1});
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
