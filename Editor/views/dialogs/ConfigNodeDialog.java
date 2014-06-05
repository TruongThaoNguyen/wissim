package views.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import models.Project;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import views.Editor;
import views.Workspace;
import controllers.managers.ApplicationSettings;
import controllers.managers.ProjectManager;


public class ConfigNodeDialog extends Dialog {
	public static final int APP_CONFIG = 0, PROJECT_CONFIG = 1;
	
	protected Object result;
	protected Shell shlNodeConfiguration;
	private Text txtIddle;
	private Text txtReception;
	private Text txtTransmission;
	private Text txtSleep;
	private Text txtRange;
	
	Combo cbInterfaceQueue;
	Combo cbRoutingProtocol;
	Combo cbMac;
	Combo cbLinkLayer;
	Combo cbNetworkInterface;
	Combo cbTransportProtocol;
	Combo cbAppProtocol;
	Combo cbChannel;
	Combo cbPropagationModel;
	Combo cbAntenna;
	Spinner spnQueueLength;
	
	private int type;
	private Editor editor;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param workspace 
	 */
	public ConfigNodeDialog(Shell parent, int style, int type, Editor editor) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.type = type;
		this.editor = editor;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		switch (type) {
		case APP_CONFIG:
			loadContentsForAppConfig();
			break;
		case PROJECT_CONFIG:
			loadContentsForProjectConfig();
			break;
		}
		
		shlNodeConfiguration.open();
		shlNodeConfiguration.layout();
		Display display = getParent().getDisplay();
		while (!shlNodeConfiguration.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void loadContentsForProjectConfig() {
		Project project = Workspace.getProject();
		
		setText("Node Configuration");
		
		txtRange.setText(project.getNodeRange() + "");
		
		loadComboContent(cbTransportProtocol, Project.getTransportProtocols(), new StringBuilder(Project.getSelectedTransportProtocol()));
		loadComboContent(cbAppProtocol, Project.getApplicationProtocols(), new StringBuilder(Project.getSelectedApplicationProtocol()));
		loadComboContent(cbRoutingProtocol, Project.getRoutingProtocols(), new StringBuilder(project.getSelectedRoutingProtocol()));
		loadComboContent(cbLinkLayer, Project.getLinkLayers(), new StringBuilder(project.getSelectedLinkLayer()));
		loadComboContent(cbMac, Project.getMacs(), new StringBuilder(project.getSelectedMac()));
		loadComboContent(cbPropagationModel, Project.getPropagationModels(), new StringBuilder(project.getSelectedPropagationModel()));
		loadComboContent(cbChannel, Project.getChannels(), new StringBuilder(project.getSelectedChannel()));
		loadComboContent(cbInterfaceQueue, Project.getInterfaceQueues(), new StringBuilder(project.getSelectedInterfaceQueue()));
		loadComboContent(cbNetworkInterface, Project.getNetworkInterfaces(), new StringBuilder(project.getSelectedNetworkInterface()));
		loadComboContent(cbAntenna, Project.getAntennas(), new StringBuilder(project.getSelectedAntenna()));
		
		spnQueueLength.setSelection(project.getQueueLength());
		
		txtIddle.setText(project.getIddleEnergy() + "");
		txtReception.setText(project.getReceptionEnergy() + "");
		txtTransmission.setText(project.getTransmissionEnergy() + "");
		txtSleep.setText(project.getSleepEnergy() + "");		
	}

	private void loadContentsForAppConfig() {
		setText("Default Node Configuration");
		
		txtRange.setText(ApplicationSettings.nodeRange + "");
		
		loadComboContent(cbTransportProtocol, ApplicationSettings.transportProtocols, ApplicationSettings.defaultTransportProtocol);
		loadComboContent(cbAppProtocol, ApplicationSettings.applicationProtocols, ApplicationSettings.defaultApplicationProtocol);
		loadComboContent(cbRoutingProtocol, ApplicationSettings.routingProtocols, ApplicationSettings.defaultRoutingProtocol);
		loadComboContent(cbLinkLayer, ApplicationSettings.linkLayers, ApplicationSettings.defaultLinkLayer);
		loadComboContent(cbMac, ApplicationSettings.macs, ApplicationSettings.defaultMac);
		loadComboContent(cbPropagationModel, ApplicationSettings.propagationModels, ApplicationSettings.defaultPropagationModel);
		loadComboContent(cbChannel, ApplicationSettings.channels, ApplicationSettings.defaultChannel);
		loadComboContent(cbInterfaceQueue, ApplicationSettings.interfaceQueues, ApplicationSettings.defaultInterfaceQueue);
		loadComboContent(cbNetworkInterface, ApplicationSettings.networkInterfaces, ApplicationSettings.defaultNetworkInterface);
		loadComboContent(cbAntenna, ApplicationSettings.antennas, ApplicationSettings.defaultAntenna);
		
		spnQueueLength.setSelection(ApplicationSettings.queueLength);
		
		txtIddle.setText(ApplicationSettings.iddleEnergy + "");
		txtReception.setText(ApplicationSettings.receptionEnergy + "");
		txtTransmission.setText(ApplicationSettings.transmissionEnergy + "");
		txtSleep.setText(ApplicationSettings.sleepEnergy + "");
	}
	
	private void loadComboContent(Combo combo, HashMap<String, LinkedHashMap<String, String>> hashMap, StringBuilder defaultItem) {
		combo.removeAll();
		Set<Entry<String, LinkedHashMap<String, String>>> set = hashMap.entrySet();
		Iterator<Entry<String, LinkedHashMap<String, String>>> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<String, LinkedHashMap<String, String>> e = iterator.next();
			combo.add(e.getKey());
			if (e.getKey().equals(defaultItem.toString()))
				combo.select(i);
			
			i++;
		}			
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlNodeConfiguration = new Shell(getParent(), getStyle());
		shlNodeConfiguration.setSize(688, 560);
		
		Composite composite = new Composite(shlNodeConfiguration, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(10, 10, 668, 64);
		
		Label lblNetworkGeneralConfiguration = new Label(composite, SWT.NONE);
		lblNetworkGeneralConfiguration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNetworkGeneralConfiguration.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblNetworkGeneralConfiguration.setBounds(10, 10, 319, 28);
		switch (type) {
		case PROJECT_CONFIG:
			lblNetworkGeneralConfiguration.setText("Node Configuration");
			break;
		case APP_CONFIG:
			lblNetworkGeneralConfiguration.setText("Node Default Configuration");
			break;			
		}
		
		Label lblDescription = new Label(composite, SWT.NONE);
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDescription.setBounds(10, 40, 351, 17);
		switch (type) {
		case PROJECT_CONFIG:
			lblDescription.setText("Set up configuration for all nodes in the current network");
			break;
		case APP_CONFIG:
			lblDescription.setText("Set up configuration for all nodes in future projects");
			break;
		}
		
		Composite composite_1 = new Composite(shlNodeConfiguration, SWT.BORDER);
		composite_1.setBounds(10, 475, 668, 48);
		
		Button btnResetDefault = new Button(composite_1, SWT.NONE);
		btnResetDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadContentsForAppConfig();
			}
		});
		btnResetDefault.setBounds(256, 10, 103, 25);
		btnResetDefault.setText("Reset Default");
		btnResetDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadContentsForAppConfig();
			}
		});
		switch (type) {
		case PROJECT_CONFIG:
			btnResetDefault.setVisible(true);
			break;
		case APP_CONFIG:
			btnResetDefault.setVisible(false);
			break;
		}
		
		Button btnApply = new Button(composite_1, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean bApply = true;
				switch (type) {
				case APP_CONFIG:
					try {
						ProjectManager.getProject();
						if(Project.getRoutingProtocols().get(cbRoutingProtocol.getText()) == null) {
							MessageDialog.openError(getParent(), "Error", "Selected Routing protocol incorrect!");
							bApply = false;
						} else { 
							ApplicationSettings.nodeRange = Integer.parseInt(txtRange.getText());
							//System.out.print(ApplicationSettings.nodeRange);
							//Project project = workspace.getProject();
							
							//project.setNodeRange(Integer.parseInt(txtRange.getText()));
							
							ApplicationSettings.defaultTransportProtocol = new StringBuilder(cbTransportProtocol.getText());
							ApplicationSettings.defaultApplicationProtocol = new StringBuilder(cbAppProtocol.getText());
							
							ApplicationSettings.defaultRoutingProtocol = new StringBuilder(cbRoutingProtocol.getText());
							ApplicationSettings.defaultLinkLayer = new StringBuilder(cbLinkLayer.getText());
							ApplicationSettings.defaultMac = new StringBuilder(cbMac.getText());
							ApplicationSettings.defaultChannel = new StringBuilder(cbChannel.getText());
							ApplicationSettings.defaultPropagationModel = new StringBuilder(cbPropagationModel.getText());
							ApplicationSettings.defaultNetworkInterface = new StringBuilder(cbNetworkInterface.getText());
							ApplicationSettings.defaultAntenna = new StringBuilder(cbAntenna.getText());
							
							ApplicationSettings.defaultInterfaceQueue = new StringBuilder(cbInterfaceQueue.getText());
							ApplicationSettings.queueLength = spnQueueLength.getSelection();
							
							ApplicationSettings.iddleEnergy = Double.parseDouble(txtIddle.getText());
							ApplicationSettings.receptionEnergy = Double.parseDouble(txtReception.getText());
							ApplicationSettings.transmissionEnergy = Double.parseDouble(txtTransmission.getText());
							ApplicationSettings.sleepEnergy = Double.parseDouble(txtSleep.getText());
							ProjectManager.getProject().setSelectedRoutingProtocol(cbRoutingProtocol.getText());
							ApplicationSettings.saveNetworkConfig();
							editor.getActSave().setEnabled(true);
						}
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					break;
				case PROJECT_CONFIG:
					try {
						ProjectManager.getProject();
						if(Project.getRoutingProtocols().get(cbRoutingProtocol.getText()) == null) {
							MessageDialog.openError(getParent(), "Error", "Selected Routing protocol incorrect!");
							bApply = false;
						} else { 
							Project project = Workspace.getProject();
							
							project.setNodeRange(Integer.parseInt(txtRange.getText()));
							
							Project.setSelectedTransportProtocol(cbTransportProtocol.getText());
							Project.setSelectedApplicationProtocol(cbAppProtocol.getText());
							
							project.setSelectedRoutingProtocol(cbRoutingProtocol.getText());
							project.setSelectedLinkLayer(cbLinkLayer.getText());
							project.setSelectedMac(cbMac.getText());
							project.setSelectedChannel(cbChannel.getText());
							project.setSelectedPropagationModel(cbPropagationModel.getText());
							project.setSelectedNetworkInterface(cbNetworkInterface.getText());
							project.setSelectedAntenna(cbAntenna.getText());
							
							project.setSelectedInterfaceQueue(cbInterfaceQueue.getText());
							project.setQueueLength(spnQueueLength.getSelection());
							
							project.setIddleEnergy(Double.parseDouble(txtIddle.getText()));
							project.setReceptionEnergy(Double.parseDouble(txtReception.getText()));
							project.setReceptionEnergy(Double.parseDouble(txtTransmission.getText()));
							project.setSleepEnergy(Double.parseDouble(txtSleep.getText()));
							editor.getActSave().setEnabled(true);
						}
						} catch (Exception exc) {
						exc.printStackTrace();
					}
					
					break;
				}
				if(bApply == true)
					shlNodeConfiguration.close();
			}
		});
		btnApply.setBounds(379, 10, 75, 25);
		btnApply.setText("Apply");
		
		Button btnNewButton_2 = new Button(composite_1, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlNodeConfiguration.close();
			}
		});
		btnNewButton_2.setBounds(475, 10, 75, 25);
		btnNewButton_2.setText("Cancel");
		
		Composite composite_2 = new Composite(shlNodeConfiguration, SWT.NONE);
		composite_2.setBounds(0, 80, 691, 389);
		
		Group grpEnergyModel = new Group(composite_2, SWT.NONE);
		grpEnergyModel.setText("Energy Model");
		grpEnergyModel.setBounds(384, 238, 297, 151);
		
		Label lblNewLabel = new Label(grpEnergyModel, SWT.NONE);
		lblNewLabel.setBounds(24, 29, 66, 15);
		lblNewLabel.setText("Idle");
		
		Label lblReceptionPower = new Label(grpEnergyModel, SWT.NONE);
		lblReceptionPower.setBounds(24, 56, 86, 21);
		lblReceptionPower.setText("Reception");
		
		Label lblTransmissionPower = new Label(grpEnergyModel, SWT.NONE);
		lblTransmissionPower.setBounds(24, 83, 98, 15);
		lblTransmissionPower.setText("Transmission");
		
		Label lblNewLabel_1 = new Label(grpEnergyModel, SWT.NONE);
		lblNewLabel_1.setBounds(24, 110, 66, 21);
		lblNewLabel_1.setText("Sleep");
		
		txtIddle = new Text(grpEnergyModel, SWT.BORDER);
		txtIddle.setBounds(154, 29, 76, 21);
		
		txtReception = new Text(grpEnergyModel, SWT.BORDER);
		txtReception.setBounds(154, 56, 76, 21);
		
		txtTransmission = new Text(grpEnergyModel, SWT.BORDER);
		txtTransmission.setBounds(154, 83, 76, 21);
		
		txtSleep = new Text(grpEnergyModel, SWT.BORDER);
		txtSleep.setBounds(154, 110, 76, 21);
		
		Group grpQueue = new Group(composite_2, SWT.NONE);
		grpQueue.setText("Queue");
		grpQueue.setBounds(384, 119, 297, 102);
		
		Label lblInterfaceQueue = new Label(grpQueue, SWT.NONE);
		lblInterfaceQueue.setBounds(24, 30, 130, 22);
		lblInterfaceQueue.setText("Interface Queue");
		
		cbInterfaceQueue = new Combo(grpQueue, SWT.NONE);
		cbInterfaceQueue.setBounds(160, 30, 125, 23);
		cbInterfaceQueue.setItems(new String[] {});
		cbInterfaceQueue.select(0);
		
		spnQueueLength = new Spinner(grpQueue, SWT.BORDER);
		spnQueueLength.setBounds(160, 70, 47, 22);
		spnQueueLength.setMinimum(30);
		spnQueueLength.setSelection(50);
		
		Label lblQueueLength = new Label(grpQueue, SWT.NONE);
		lblQueueLength.setBounds(24, 73, 100, 19);
		lblQueueLength.setText("Queue Length");
		
		Group grpPhysicLayer = new Group(composite_2, SWT.NONE);
		grpPhysicLayer.setText("Physic Layer");
		grpPhysicLayer.setBounds(10, 95, 368, 293);
		
		Label lblRoutingProtocol = new Label(grpPhysicLayer, SWT.NONE);
		lblRoutingProtocol.setBounds(10, 45, 134, 22);
		lblRoutingProtocol.setText("Routing Protocol");
		
		cbRoutingProtocol = new Combo(grpPhysicLayer, SWT.NONE);
		cbRoutingProtocol.setBounds(150, 38, 125, 23);
		cbRoutingProtocol.setItems(new String[] {});
		
		Label lblLinkLayerType = new Label(grpPhysicLayer, SWT.NONE);
		lblLinkLayerType.setBounds(10, 79, 94, 22);
		lblLinkLayerType.setText("Link Layer");
		
		cbLinkLayer = new Combo(grpPhysicLayer, SWT.NONE);
		cbLinkLayer.setBounds(150, 73, 125, 23);
		cbLinkLayer.setItems(new String[] {});
		
		Label lblMacType = new Label(grpPhysicLayer, SWT.NONE);
		lblMacType.setBounds(10, 113, 134, 15);
		lblMacType.setText("Mac");
		
		cbMac = new Combo(grpPhysicLayer, SWT.NONE);
		cbMac.setBounds(150, 108, 125, 23);
		cbMac.setItems(new String[] {});
		
		Label lblChannel = new Label(grpPhysicLayer, SWT.NONE);
		lblChannel.setBounds(10, 147, 134, 28);
		lblChannel.setText("Channel");
		
		Label lblPropagationmodel = new Label(grpPhysicLayer, SWT.NONE);
		lblPropagationmodel.setBounds(10, 181, 134, 28);
		lblPropagationmodel.setText("Propagation Model");
		
		cbChannel = new Combo(grpPhysicLayer, SWT.NONE);
		cbChannel.setBounds(150, 143, 125, 23);
		cbChannel.setItems(new String[] {});
		
		Label lblNetworkInterface = new Label(grpPhysicLayer, SWT.NONE);
		lblNetworkInterface.setBounds(10, 215, 134, 28);
		lblNetworkInterface.setText("Network Interface");
		
		cbPropagationModel = new Combo(grpPhysicLayer, SWT.NONE);
		cbPropagationModel.setBounds(150, 181, 125, 23);
		cbPropagationModel.setItems(new String[] {});
		
		Label lblAntenna = new Label(grpPhysicLayer, SWT.NONE);
		lblAntenna.setBounds(10, 249, 134, 25);
		lblAntenna.setText("Antenna");
		
		cbNetworkInterface = new Combo(grpPhysicLayer, SWT.NONE);
		cbNetworkInterface.setBounds(150, 215, 125, 23);
		cbNetworkInterface.setItems(new String[] {});
		
		cbAntenna = new Combo(grpPhysicLayer, SWT.NONE);
		cbAntenna.setBounds(150, 249, 125, 23);
		cbAntenna.setItems(new String[] {});
		
		Button button_1 = new Button(grpPhysicLayer, SWT.NONE);
		button_1.setText("Config");
		button_1.setBounds(281, 38, 75, 25);
//		button_1.setEnabled(false);
		button_1.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				String currentSelectedRouting = cbRoutingProtocol.getText();
				System.out.println(currentSelectedRouting);
				ProjectManager.getProject();
				HashMap<String, LinkedHashMap<String, String>> input = Project.getRoutingProtocols();
				if(input.get(currentSelectedRouting) != null) {
					HashMap<String,String> value = input.get(currentSelectedRouting);
					new ParameterConfigDialog(shlNodeConfiguration,SWT.SHEET, currentSelectedRouting,value,editor).open();
				} else {
					MessageDialog.openError(getParent(), "Error", "Selected Routing protocol incorrect!");
				}
			}
		});
		
		Button button_2 = new Button(grpPhysicLayer, SWT.NONE);
		button_2.setText("Config");
		button_2.setBounds(281, 73, 75, 25);
		button_2.setEnabled(false);
		
		Button button_3 = new Button(grpPhysicLayer, SWT.NONE);
		button_3.setText("Config");
		button_3.setBounds(281, 108, 75, 25);
		button_3.setEnabled(false);
		
		Button button_4 = new Button(grpPhysicLayer, SWT.NONE);
		button_4.setText("Config");
		button_4.setBounds(281, 143, 75, 25);
		button_4.setEnabled(false);
		
		Button button_5 = new Button(grpPhysicLayer, SWT.NONE);
		button_5.setText("Config");
		button_5.setBounds(281, 181, 75, 25);
		button_5.setEnabled(false);
		
		Button button_6 = new Button(grpPhysicLayer, SWT.NONE);
		button_6.setText("Config");
		button_6.setBounds(281, 215, 75, 25);
		button_6.setEnabled(false);
		
		Button button_7 = new Button(grpPhysicLayer, SWT.NONE);
		button_7.setText("Config");
		button_7.setBounds(281, 249, 75, 25);
		button_7.setEnabled(false);
		
		Group grpRange = new Group(composite_2, SWT.NONE);
		grpRange.setText("Range");
		grpRange.setBounds(10, 10, 368, 61);
		
		Label lblRange = new Label(grpRange, SWT.NONE);
		lblRange.setBounds(66, 26, 48, 21);
		lblRange.setText("Range");
		
		txtRange = new Text(grpRange, SWT.BORDER);
		txtRange.setBounds(120, 26, 76, 21);
		
		Group grpProtocols = new Group(composite_2, SWT.NONE);
		grpProtocols.setText("Protocols");
		grpProtocols.setBounds(386, 10, 295, 103);
		
		Label lblTransportProtocol = new Label(grpProtocols, SWT.NONE);
		lblTransportProtocol.setBounds(10, 24, 88, 29);
		lblTransportProtocol.setText("Transport");
		
		cbTransportProtocol = new Combo(grpProtocols, SWT.NONE);
		cbTransportProtocol.setBounds(104, 24, 91, 23);
		
		Label lblApplication = new Label(grpProtocols, SWT.NONE);
		lblApplication.setBounds(10, 62, 88, 25);
		lblApplication.setText("Application");
		
		cbAppProtocol = new Combo(grpProtocols, SWT.NONE);
		cbAppProtocol.setBounds(104, 62, 91, 23);
		
		Button btnConfig = new Button(grpProtocols, SWT.NONE);
		btnConfig.setBounds(210, 24, 75, 25);
		btnConfig.setText("Config");
		
		Button button = new Button(grpProtocols, SWT.NONE);
		button.setText("Config");
		button.setBounds(210, 62, 75, 25);
		
		shlNodeConfiguration.setDefaultButton(btnApply);
	}
}
