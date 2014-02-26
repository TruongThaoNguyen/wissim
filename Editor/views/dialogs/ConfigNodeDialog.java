package views.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import models.Project;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import controllers.managers.ApplicationSettings;
import view.Workspace;


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
	private Workspace workspace;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @param workspace 
	 */
	public ConfigNodeDialog(Shell parent, int style, int type, Workspace workspace) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.type = type;
		this.workspace = workspace;
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
		Project project = workspace.getProject();
		
		setText("Node Configuration");
		
		txtRange.setText(project.getNodeRange() + "");
		
		loadComboContent(cbTransportProtocol, project.getTransportProtocols(), new StringBuilder(project.getSelectedTransportProtocol()));
		loadComboContent(cbAppProtocol, project.getApplicationProtocols(), new StringBuilder(project.getSelectedApplicationProtocol()));
		loadComboContent(cbRoutingProtocol, project.getRoutingProtocols(), new StringBuilder(project.getSelectedRoutingProtocol()));
		loadComboContent(cbLinkLayer, project.getLinkLayers(), new StringBuilder(project.getSelectedLinkLayer()));
		loadComboContent(cbMac, project.getMacs(), new StringBuilder(project.getSelectedMac()));
		loadComboContent(cbPropagationModel, project.getPropagationModels(), new StringBuilder(project.getSelectedPropagationModel()));
		loadComboContent(cbChannel, project.getChannels(), new StringBuilder(project.getSelectedChannel()));
		loadComboContent(cbInterfaceQueue, project.getInterfaceQueues(), new StringBuilder(project.getSelectedInterfaceQueue()));
		loadComboContent(cbNetworkInterface, project.getNetworkInterfaces(), new StringBuilder(project.getSelectedNetworkInterface()));
		loadComboContent(cbAntenna, project.getAntennas(), new StringBuilder(project.getSelectedAntenna()));
		
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
	
	private void loadComboContent(Combo combo, HashMap<String, HashMap<String, String>> items, StringBuilder defaultItem) {
		combo.removeAll();
		Set<Entry<String, HashMap<String, String>>> set = items.entrySet();
		Iterator<Entry<String, HashMap<String, String>>> iterator = set.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<String, HashMap<String, String>> e = iterator.next();
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
		shlNodeConfiguration.setSize(588, 551);
		
		Composite composite = new Composite(shlNodeConfiguration, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 582, 64);
		
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
		lblDescription.setBounds(10, 35, 338, 15);
		switch (type) {
		case PROJECT_CONFIG:
			lblDescription.setText("Set up configuration for all nodes in the current network");
			break;
		case APP_CONFIG:
			lblDescription.setText("Set up configuration for all nodes in future projects");
			break;
		}
		
		Composite composite_1 = new Composite(shlNodeConfiguration, SWT.BORDER);
		composite_1.setBounds(0, 474, 582, 48);
		
		Button btnResetDefault = new Button(composite_1, SWT.NONE);
		btnResetDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadContentsForAppConfig();
			}
		});
		btnResetDefault.setBounds(270, 10, 89, 25);
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
				switch (type) {
				case APP_CONFIG:
					try {
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
						
						ApplicationSettings.saveNetworkConfig();
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					break;
				case PROJECT_CONFIG:
					try {
						Project project = workspace.getProject();
						
						project.setNodeRange(Integer.parseInt(txtRange.getText()));
						
						project.setSelectedTransportProtocol(cbTransportProtocol.getText());
						project.setSelectedApplicationProtocol(cbAppProtocol.getText());
						
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
						
						workspace.getCareTaker().save(project, "Project configuration changed");
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					
					break;
				}
				
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
		composite_2.setBounds(0, 70, 582, 398);
		
		Group grpEnergyModel = new Group(composite_2, SWT.NONE);
		grpEnergyModel.setText("Energy Model");
		grpEnergyModel.setBounds(292, 237, 280, 151);
		
		Label lblNewLabel = new Label(grpEnergyModel, SWT.NONE);
		lblNewLabel.setBounds(24, 29, 66, 15);
		lblNewLabel.setText("Idle");
		
		Label lblReceptionPower = new Label(grpEnergyModel, SWT.NONE);
		lblReceptionPower.setBounds(24, 56, 55, 15);
		lblReceptionPower.setText("Reception");
		
		Label lblTransmissionPower = new Label(grpEnergyModel, SWT.NONE);
		lblTransmissionPower.setBounds(24, 83, 86, 15);
		lblTransmissionPower.setText("Transmission");
		
		Label lblNewLabel_1 = new Label(grpEnergyModel, SWT.NONE);
		lblNewLabel_1.setBounds(24, 110, 55, 15);
		lblNewLabel_1.setText("Sleep");
		
		txtIddle = new Text(grpEnergyModel, SWT.BORDER);
		txtIddle.setBounds(130, 26, 76, 21);
		
		txtReception = new Text(grpEnergyModel, SWT.BORDER);
		txtReception.setBounds(130, 53, 76, 21);
		
		txtTransmission = new Text(grpEnergyModel, SWT.BORDER);
		txtTransmission.setBounds(130, 80, 76, 21);
		
		txtSleep = new Text(grpEnergyModel, SWT.BORDER);
		txtSleep.setBounds(130, 107, 76, 21);
		
		Group grpQueue = new Group(composite_2, SWT.NONE);
		grpQueue.setText("Queue");
		grpQueue.setBounds(292, 130, 280, 91);
		
		Label lblInterfaceQueue = new Label(grpQueue, SWT.NONE);
		lblInterfaceQueue.setBounds(24, 30, 94, 15);
		lblInterfaceQueue.setText("Interface Queue");
		
		cbInterfaceQueue = new Combo(grpQueue, SWT.NONE);
		cbInterfaceQueue.setBounds(130, 27, 125, 23);
		cbInterfaceQueue.setItems(new String[] {});
		cbInterfaceQueue.select(0);
		
		spnQueueLength = new Spinner(grpQueue, SWT.BORDER);
		spnQueueLength.setBounds(130, 56, 47, 22);
		spnQueueLength.setMinimum(30);
		spnQueueLength.setSelection(50);
		
		Label lblQueueLength = new Label(grpQueue, SWT.NONE);
		lblQueueLength.setBounds(24, 59, 94, 15);
		lblQueueLength.setText("Queue Length");
		
		Group grpPhysicLayer = new Group(composite_2, SWT.NONE);
		grpPhysicLayer.setText("Physic Layer");
		grpPhysicLayer.setBounds(10, 130, 267, 258);
		
		Label lblRoutingProtocol = new Label(grpPhysicLayer, SWT.NONE);
		lblRoutingProtocol.setBounds(10, 19, 100, 15);
		lblRoutingProtocol.setText("Routing Protocol");
		
		cbRoutingProtocol = new Combo(grpPhysicLayer, SWT.NONE);
		cbRoutingProtocol.setBounds(125, 12, 125, 23);
		cbRoutingProtocol.setItems(new String[] {});
		
		Label lblLinkLayerType = new Label(grpPhysicLayer, SWT.NONE);
		lblLinkLayerType.setBounds(10, 53, 94, 15);
		lblLinkLayerType.setText("Link Layer");
		
		cbLinkLayer = new Combo(grpPhysicLayer, SWT.NONE);
		cbLinkLayer.setBounds(125, 47, 125, 23);
		cbLinkLayer.setItems(new String[] {});
		
		Label lblMacType = new Label(grpPhysicLayer, SWT.NONE);
		lblMacType.setBounds(10, 87, 55, 15);
		lblMacType.setText("Mac");
		
		cbMac = new Combo(grpPhysicLayer, SWT.NONE);
		cbMac.setBounds(125, 82, 125, 23);
		cbMac.setItems(new String[] {});
		
		Label lblChannel = new Label(grpPhysicLayer, SWT.NONE);
		lblChannel.setBounds(10, 121, 55, 15);
		lblChannel.setText("Channel");
		
		Label lblPropagationmodel = new Label(grpPhysicLayer, SWT.NONE);
		lblPropagationmodel.setBounds(10, 155, 108, 15);
		lblPropagationmodel.setText("Propagation Model");
		
		cbChannel = new Combo(grpPhysicLayer, SWT.NONE);
		cbChannel.setBounds(125, 117, 125, 23);
		cbChannel.setItems(new String[] {});
		
		Label lblNetworkInterface = new Label(grpPhysicLayer, SWT.NONE);
		lblNetworkInterface.setBounds(10, 189, 100, 15);
		lblNetworkInterface.setText("Network Interface");
		
		cbPropagationModel = new Combo(grpPhysicLayer, SWT.NONE);
		cbPropagationModel.setBounds(125, 152, 125, 23);
		cbPropagationModel.setItems(new String[] {});
		
		Label lblAntenna = new Label(grpPhysicLayer, SWT.NONE);
		lblAntenna.setBounds(10, 223, 55, 15);
		lblAntenna.setText("Antenna");
		
		cbNetworkInterface = new Combo(grpPhysicLayer, SWT.NONE);
		cbNetworkInterface.setBounds(125, 187, 125, 23);
		cbNetworkInterface.setItems(new String[] {});
		
		cbAntenna = new Combo(grpPhysicLayer, SWT.NONE);
		cbAntenna.setBounds(125, 222, 125, 23);
		cbAntenna.setItems(new String[] {});
		
		Group grpRange = new Group(composite_2, SWT.NONE);
		grpRange.setText("Range");
		grpRange.setBounds(10, 10, 267, 114);
		
		Label lblRange = new Label(grpRange, SWT.NONE);
		lblRange.setBounds(66, 48, 48, 15);
		lblRange.setText("Range");
		
		txtRange = new Text(grpRange, SWT.BORDER);
		txtRange.setBounds(120, 45, 76, 21);
		
		Group grpProtocols = new Group(composite_2, SWT.NONE);
		grpProtocols.setText("Protocols");
		grpProtocols.setBounds(292, 10, 280, 114);
		
		Label lblTransportProtocol = new Label(grpProtocols, SWT.NONE);
		lblTransportProtocol.setBounds(10, 24, 72, 15);
		lblTransportProtocol.setText("Transport");
		
		cbTransportProtocol = new Combo(grpProtocols, SWT.NONE);
		cbTransportProtocol.setBounds(88, 21, 91, 23);
		
		Label lblApplication = new Label(grpProtocols, SWT.NONE);
		lblApplication.setBounds(10, 62, 72, 15);
		lblApplication.setText("Application");
		
		cbAppProtocol = new Combo(grpProtocols, SWT.NONE);
		cbAppProtocol.setBounds(88, 59, 91, 23);
		
		Button btnConfig = new Button(grpProtocols, SWT.NONE);
		btnConfig.setBounds(195, 21, 75, 25);
		btnConfig.setText("Config");
		
		Button button = new Button(grpProtocols, SWT.NONE);
		button.setText("Config");
		button.setBounds(195, 59, 75, 25);
		
		shlNodeConfiguration.setDefaultButton(btnApply);
	}
}
