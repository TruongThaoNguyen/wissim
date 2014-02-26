package views.dialogs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import controllers.helpers.Helper;
import controllers.helpers.Validator;
import controllers.managers.ApplicationManager;
import controllers.managers.ProjectManager;
import view.Workspace;

public class ExportImageDialog extends Dialog {

	protected Object result;
	protected Shell shlExportImage;
	private Text txtFileName;
	private Text txtDirectory;
	
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param w 
	 */
	public ExportImageDialog(Shell parent, int style, Workspace w) {
		super(parent, style);
		setText("SWT Dialog");
		
		workspace = w;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlExportImage.open();
		shlExportImage.layout();
		Display display = getParent().getDisplay();
		while (!shlExportImage.isDisposed()) {
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
		shlExportImage = new Shell(getParent(), getStyle());
		shlExportImage.setSize(531, 300);
		shlExportImage.setText("Export Image");
		
		Composite composite = new Composite(shlExportImage, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 525, 64);
		
		Label lblExportImage = new Label(composite, SWT.NONE);
		lblExportImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblExportImage.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblExportImage.setBounds(10, 10, 188, 44);
		lblExportImage.setText("Export Image");
		
		Composite composite_1 = new Composite(shlExportImage, SWT.BORDER);
		composite_1.setBounds(0, 225, 525, 46);
		
		Group grpOptions = new Group(shlExportImage, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setBounds(10, 79, 154, 70);
		
		Button btnNetworkOnly = new Button(grpOptions, SWT.RADIO);
		btnNetworkOnly.setSelection(true);
		btnNetworkOnly.setBounds(10, 22, 90, 16);
		btnNetworkOnly.setText("Network only");
		
		Button btnNetworkWithFeatures = new Button(grpOptions, SWT.RADIO);
		btnNetworkWithFeatures.setBounds(10, 44, 136, 16);
		btnNetworkWithFeatures.setText("Network with features");
		
		Group grpSaveTo = new Group(shlExportImage, SWT.NONE);
		grpSaveTo.setText("Save to");
		grpSaveTo.setBounds(180, 79, 335, 130);
		
		Label lblFileName = new Label(grpSaveTo, SWT.NONE);
		lblFileName.setBounds(10, 25, 58, 15);
		lblFileName.setText("File Name");
		
		txtFileName = new Text(grpSaveTo, SWT.BORDER);
		txtFileName.setBounds(71, 22, 116, 21);
		txtFileName.setText(workspace.getProject().getNetwork().getName());
		
		Label lblDirectory = new Label(grpSaveTo, SWT.NONE);
		lblDirectory.setBounds(10, 51, 55, 15);
		lblDirectory.setText("Directory");
		
		txtDirectory = new Text(grpSaveTo, SWT.BORDER | SWT.READ_ONLY);
		txtDirectory.setText("D:\\");
		txtDirectory.setBounds(71, 48, 173, 21);
		
		Button btnBrowse = new Button(grpSaveTo, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog browseDialog = new DirectoryDialog(shlExportImage, SWT.NONE);
				browseDialog.setText("Browse");
				browseDialog.setFilterPath("D:/");
				txtDirectory.setText(browseDialog.open());				
			}
		});
		btnBrowse.setBounds(250, 46, 75, 25);
		btnBrowse.setText("Browse");
		
		final Combo cbSize = new Combo(grpSaveTo, SWT.READ_ONLY);
		cbSize.setBounds(71, 73, 105, 23);
		cbSize.setItems(new String[] {"Small", "Medium", "Large"});
		cbSize.select(1);
		
		Label lblNewLabel = new Label(grpSaveTo, SWT.NONE);
		lblNewLabel.setBounds(10, 77, 39, 15);
		lblNewLabel.setText("Size");
		
		Button btnExport = new Button(composite_1, SWT.NONE);
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int size;
				
				switch (cbSize.getText()) {
				case "SMALL":
					size = ProjectManager.IMAGE_SMALL_SIZE;
					break;
				case "LARGE":
					size = ProjectManager.IMAGE_LARGE_SIZE;
					break;
				case "MEDIUM":					
				default:
					size = ProjectManager.IMAGE_MEDIUM_SIZE;
				}
				
				BufferedImage img = ApplicationManager.exportToImage(workspace, size);
				
				File file = new File(Validator.getFilePath(txtDirectory.getText(), Helper.getFileNameWithExt(txtFileName.getText(), "png")));
				try {
					ImageIO.write(img, "png", file);
					MessageDialog.openInformation(shlExportImage, "Image Saved", "Image is generated successfully.");
				} catch (IOException exc) {
					MessageDialog.openError(shlExportImage, "Error Writing to Image", "Some errors happened. Cannot save to image");
				} finally {
					shlExportImage.close();
				}
			}
		});
		btnExport.setBounds(355, 10, 75, 25);
		btnExport.setText("Export");
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlExportImage.close();
			}
		});
		btnCancel.setBounds(436, 10, 75, 25);
		btnCancel.setText("Cancel");

		shlExportImage.setDefaultButton(btnExport);
	}
}
