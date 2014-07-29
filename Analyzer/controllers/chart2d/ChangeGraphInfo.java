package controllers.chart2d;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.swtchart.Chart;


public class ChangeGraphInfo {

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
    public static Chart chartChanged;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void create(Chart chart) {
		try {
			ChangeGraphInfo window = new ChangeGraphInfo();
			window.open(chart);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(Chart chart) {
		Display display = Display.getDefault();
		createContents(chart);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Chart chart) {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Change Graph Infomations");
		chartChanged = chart;
		
		Label lblTittle = new Label(shell, SWT.NONE);
		lblTittle.setBounds(99, 73, 32, 15);
		lblTittle.setText("Tittle");
		
		Label lblXaxis = new Label(shell, SWT.NONE);
		lblXaxis.setBounds(99, 113, 32, 15);
		lblXaxis.setText("XAxis");
		
		Label lblYaxis = new Label(shell, SWT.NONE);
		lblYaxis.setBounds(99, 155, 32, 15);
		lblYaxis.setText("YAxis");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(162, 70, 154, 21);
		text.setText(chart.getTitle().getText());
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(162, 110, 154, 21);
		text_1.setText(chart.getAxisSet().getXAxis(0).getTitle().getText());
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(162, 149, 154, 21);
		text_2.setText(chart.getAxisSet().getYAxis(0).getTitle().getText());
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				chartChanged.getTitle().setText(text.getText());			
				chartChanged.getAxisSet().getXAxis(0).getTitle().setText(text_1.getText());
				chartChanged.getAxisSet().getYAxis(0).getTitle().setText(text_2.getText());
				chartChanged.redraw();
				shell.dispose();
			}
		});
		btnSave.setBounds(143, 189, 75, 25);
		btnSave.setText("Save");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				shell.dispose();
			}
		});
		btnCancel.setBounds(263, 189, 75, 25);
		btnCancel.setText("Cancel");

	}
}
