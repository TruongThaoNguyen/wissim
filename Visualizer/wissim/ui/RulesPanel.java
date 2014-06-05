package wissim.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.PatternSyntaxException;

import javax.swing.JTextField;

import wissim.controller.filters.gui.TableFilterHeader;
import wissim.object.table.NodeTableModel;

public class RulesPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8960899108744314507L;
	private JTextField nodeIdTxt;
	private JTextField xTxt;
	private JTextField yTxt;
	private JTextField packetIdTxt;
	private JTextField typeTxt;
	private JTextField sourceTxt;
	private JTextField destTxt;
	private JTextField hopTxt;
	private JTextField sentTxt;
	private JTextField receiTxt;
	private JTextField pathTxt;
	private JTextField timeTxt;
	private JTextField evtTypeTxt;
	private JTextField evtSourceTxt;
	private JTextField evtDestTxt;
	private JTextField evtPacketTxt;
	private JCheckBox nodeIDCheck;
	private TableFilterHeader nodeFilter;
	

	/**
	 * Create the panel.
	 */
	public RulesPanel(TableFilterHeader nodeHeader,TableFilterHeader packetHeader,TableFilterHeader evtHeader) {
		
		nodeFilter = nodeHeader;
		setLayout(new BorderLayout(5, 5));
		
		JPanel body = new JPanel();
		add(body, BorderLayout.CENTER);
		GridBagLayout gbl_body = new GridBagLayout();
		gbl_body.columnWeights = new double[]{1.0};
		gbl_body.rowWeights = new double[]{0.0, 0.0, 0.0};
		body.setLayout(gbl_body);
		
		JPanel nodeRules = new JPanel();
		GridBagConstraints gbc_nodeRules = new GridBagConstraints();
		gbc_nodeRules.insets = new Insets(0, 0, 5, 0);
		gbc_nodeRules.fill = GridBagConstraints.HORIZONTAL;
		gbc_nodeRules.weighty = 1.0;
		gbc_nodeRules.weightx = 1.0;
		gbc_nodeRules.anchor = GridBagConstraints.NORTHWEST;
		gbc_nodeRules.gridx = 0;
		gbc_nodeRules.gridy = 0;
		body.add(nodeRules, gbc_nodeRules);
		nodeRules.setBorder(new TitledBorder(null, "Node rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_nodeRules = new GridBagLayout();
		gbl_nodeRules.columnWidths = new int[] {20, 100, 100, 100, 100, 100};
		gbl_nodeRules.columnWeights = new double[]{1.0, 1.0};
		gbl_nodeRules.rowWeights = new double[]{0.0, 0.0, 0.0};
		nodeRules.setLayout(gbl_nodeRules);
		
		nodeIDCheck = new JCheckBox("Node id");
		GridBagConstraints gbc_nodeIDCheck = new GridBagConstraints();
		gbc_nodeIDCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_nodeIDCheck.insets = new Insets(0, 0, 5, 5);
		gbc_nodeIDCheck.weighty = 1.0;
		gbc_nodeIDCheck.weightx = 0.1;
		gbc_nodeIDCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_nodeIDCheck.gridx = 0;
		gbc_nodeIDCheck.gridy = 0;
		nodeRules.add(nodeIDCheck, gbc_nodeIDCheck);
		
		JCheckBox xCheck = new JCheckBox("X cordinate");
		GridBagConstraints gbc_xCheck = new GridBagConstraints();
		gbc_xCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_xCheck.insets = new Insets(0, 0, 5, 5);
		gbc_xCheck.weighty = 1.0;
		gbc_xCheck.weightx = 0.1;
		gbc_xCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_xCheck.gridx = 0;
		gbc_xCheck.gridy = 1;
		nodeRules.add(xCheck, gbc_xCheck);
		
		xTxt = new JTextField();
		GridBagConstraints gbc_xTxt = new GridBagConstraints();
		gbc_xTxt.anchor = GridBagConstraints.NORTHWEST;
		gbc_xTxt.weighty = 1.0;
		gbc_xTxt.weightx = 0.5;
		gbc_xTxt.insets = new Insets(0, 0, 5, 5);
		gbc_xTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_xTxt.gridx = 1;
		gbc_xTxt.gridy = 1;
		nodeRules.add(xTxt, gbc_xTxt);
		xTxt.setColumns(10);
		
		JCheckBox yCheck = new JCheckBox("Y cordinate");
		GridBagConstraints gbc_yCheck = new GridBagConstraints();
		gbc_yCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_yCheck.insets = new Insets(0, 0, 5, 5);
		gbc_yCheck.weighty = 1.0;
		gbc_yCheck.weightx = 0.1;
		gbc_yCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_yCheck.gridx = 0;
		gbc_yCheck.gridy = 2;
		nodeRules.add(yCheck, gbc_yCheck);
		
		nodeIdTxt = new JTextField();
		GridBagConstraints gbc_nodeIdTxt = new GridBagConstraints();
		gbc_nodeIdTxt.anchor = GridBagConstraints.NORTHWEST;
		gbc_nodeIdTxt.insets = new Insets(0, 0, 5, 5);
		gbc_nodeIdTxt.weighty = 1.0;
		gbc_nodeIdTxt.weightx = 0.5;
		gbc_nodeIdTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_nodeIdTxt.gridx = 1;
		gbc_nodeIdTxt.gridy = 0;
		nodeRules.add(nodeIdTxt, gbc_nodeIdTxt);
		nodeIdTxt.setColumns(10);
		
		yTxt = new JTextField();
		GridBagConstraints gbc_yTxt = new GridBagConstraints();
		gbc_yTxt.anchor = GridBagConstraints.NORTHWEST;
		gbc_yTxt.insets = new Insets(0, 0, 5, 5);
		gbc_yTxt.weighty = 1.0;
		gbc_yTxt.weightx = 0.5;
		gbc_yTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_yTxt.gridx = 1;
		gbc_yTxt.gridy = 2;
		nodeRules.add(yTxt, gbc_yTxt);
		yTxt.setColumns(10);
		
		JPanel packetRules = new JPanel();
		GridBagConstraints gbc_packetRules = new GridBagConstraints();
		gbc_packetRules.insets = new Insets(0, 0, 5, 0);
		gbc_packetRules.fill = GridBagConstraints.HORIZONTAL;
		gbc_packetRules.weighty = 1.0;
		gbc_packetRules.weightx = 1.0;
		gbc_packetRules.anchor = GridBagConstraints.NORTHWEST;
		gbc_packetRules.gridx = 0;
		gbc_packetRules.gridy = 1;
		body.add(packetRules, gbc_packetRules);
		packetRules.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Packet rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_packetRules = new GridBagLayout();
		gbl_packetRules.columnWidths = new int[] {20, 100, 100, 100, 100, 100};
		gbl_packetRules.columnWeights = new double[]{0.0, 1.0};
		gbl_packetRules.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		packetRules.setLayout(gbl_packetRules);
		
		JCheckBox packetIdCheck = new JCheckBox("Packet ID");
		GridBagConstraints gbc_packetIdCheck = new GridBagConstraints();
		gbc_packetIdCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_packetIdCheck.weighty = 1.0;
		gbc_packetIdCheck.weightx = 1.0;
		gbc_packetIdCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_packetIdCheck.insets = new Insets(0, 0, 5, 5);
		gbc_packetIdCheck.gridx = 0;
		gbc_packetIdCheck.gridy = 0;
		packetRules.add(packetIdCheck, gbc_packetIdCheck);
		
		packetIdTxt = new JTextField();
		GridBagConstraints gbc_packetIdTxt = new GridBagConstraints();
		gbc_packetIdTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_packetIdTxt.weighty = 1.0;
		gbc_packetIdTxt.weightx = 1.0;
		gbc_packetIdTxt.insets = new Insets(0, 0, 5, 5);
		gbc_packetIdTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_packetIdTxt.gridx = 1;
		gbc_packetIdTxt.gridy = 0;
		packetRules.add(packetIdTxt, gbc_packetIdTxt);
		packetIdTxt.setColumns(10);
		
		JCheckBox pathCheck = new JCheckBox("Path");
		GridBagConstraints gbc_pathCheck = new GridBagConstraints();
		gbc_pathCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_pathCheck.weightx = 1.0;
		gbc_pathCheck.weighty = 1.0;
		gbc_pathCheck.insets = new Insets(0, 0, 5, 5);
		gbc_pathCheck.gridx = 0;
		gbc_pathCheck.gridy = 7;
		packetRules.add(pathCheck, gbc_pathCheck);
		
		pathTxt = new JTextField();
		GridBagConstraints gbc_pathTxt = new GridBagConstraints();
		gbc_pathTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_pathTxt.weighty = 1.0;
		gbc_pathTxt.weightx = 1.0;
		gbc_pathTxt.insets = new Insets(0, 0, 5, 5);
		gbc_pathTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathTxt.gridx = 1;
		gbc_pathTxt.gridy = 7;
		packetRules.add(pathTxt, gbc_pathTxt);
		pathTxt.setColumns(10);
		
		typeTxt = new JTextField();
		GridBagConstraints gbc_typeTxt = new GridBagConstraints();
		gbc_typeTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_typeTxt.weighty = 1.0;
		gbc_typeTxt.weightx = 1.0;
		gbc_typeTxt.insets = new Insets(0, 0, 5, 5);
		gbc_typeTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeTxt.gridx = 1;
		gbc_typeTxt.gridy = 1;
		packetRules.add(typeTxt, gbc_typeTxt);
		typeTxt.setColumns(10);
		
		sourceTxt = new JTextField();
		GridBagConstraints gbc_sourceTxt = new GridBagConstraints();
		gbc_sourceTxt.weighty = 1.0;
		gbc_sourceTxt.weightx = 1.0;
		gbc_sourceTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_sourceTxt.insets = new Insets(0, 0, 5, 5);
		gbc_sourceTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourceTxt.gridx = 1;
		gbc_sourceTxt.gridy = 2;
		packetRules.add(sourceTxt, gbc_sourceTxt);
		sourceTxt.setColumns(10);
		
		destTxt = new JTextField();
		GridBagConstraints gbc_destTxt = new GridBagConstraints();
		gbc_destTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_destTxt.weighty = 1.0;
		gbc_destTxt.weightx = 1.0;
		gbc_destTxt.insets = new Insets(0, 0, 5, 5);
		gbc_destTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_destTxt.gridx = 1;
		gbc_destTxt.gridy = 3;
		packetRules.add(destTxt, gbc_destTxt);
		destTxt.setColumns(10);
		
		hopTxt = new JTextField();
		GridBagConstraints gbc_hopTxt = new GridBagConstraints();
		gbc_hopTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_hopTxt.weighty = 1.0;
		gbc_hopTxt.weightx = 1.0;
		gbc_hopTxt.insets = new Insets(0, 0, 5, 5);
		gbc_hopTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_hopTxt.gridx = 1;
		gbc_hopTxt.gridy = 4;
		packetRules.add(hopTxt, gbc_hopTxt);
		hopTxt.setColumns(10);
		
		sentTxt = new JTextField();
		GridBagConstraints gbc_sentTxt = new GridBagConstraints();
		gbc_sentTxt.weighty = 1.0;
		gbc_sentTxt.weightx = 1.0;
		gbc_sentTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_sentTxt.insets = new Insets(0, 0, 5, 5);
		gbc_sentTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_sentTxt.gridx = 1;
		gbc_sentTxt.gridy = 5;
		packetRules.add(sentTxt, gbc_sentTxt);
		sentTxt.setColumns(10);
		
		receiTxt = new JTextField();
		GridBagConstraints gbc_receiTxt = new GridBagConstraints();
		gbc_receiTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_receiTxt.weighty = 1.0;
		gbc_receiTxt.weightx = 1.0;
		gbc_receiTxt.insets = new Insets(0, 0, 0, 5);
		gbc_receiTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_receiTxt.gridx = 1;
		gbc_receiTxt.gridy = 6;
		packetRules.add(receiTxt, gbc_receiTxt);
		receiTxt.setColumns(10);
		
		JCheckBox receivedCheck = new JCheckBox("Received time");
		GridBagConstraints gbc_receivedCheck = new GridBagConstraints();
		gbc_receivedCheck.weighty = 1.0;
		gbc_receivedCheck.weightx = 1.0;
		gbc_receivedCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_receivedCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_receivedCheck.insets = new Insets(0, 0, 5, 5);
		gbc_receivedCheck.gridx = 0;
		gbc_receivedCheck.gridy = 6;
		packetRules.add(receivedCheck, gbc_receivedCheck);
		
		JCheckBox sendTimeCheck = new JCheckBox("Sent time");
		GridBagConstraints gbc_sendTimeCheck = new GridBagConstraints();
		gbc_sendTimeCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_sendTimeCheck.weighty = 1.0;
		gbc_sendTimeCheck.weightx = 1.0;
		gbc_sendTimeCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_sendTimeCheck.insets = new Insets(0, 0, 5, 5);
		gbc_sendTimeCheck.gridx = 0;
		gbc_sendTimeCheck.gridy = 5;
		packetRules.add(sendTimeCheck, gbc_sendTimeCheck);
		
		JCheckBox receivedTimeCheck = new JCheckBox("Received time");
		GridBagConstraints gbc_receivedTimeCheck = new GridBagConstraints();
		gbc_receivedTimeCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_receivedTimeCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_receivedTimeCheck.insets = new Insets(0, 0, 0, 5);
		gbc_receivedTimeCheck.gridx = 0;
		gbc_receivedTimeCheck.gridy = 6;
		packetRules.add(receivedTimeCheck, gbc_receivedTimeCheck);
		
		JCheckBox typeCheck = new JCheckBox("Type");
		GridBagConstraints gbc_typeCheck = new GridBagConstraints();
		gbc_typeCheck.weighty = 1.0;
		gbc_typeCheck.weightx = 1.0;
		gbc_typeCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_typeCheck.insets = new Insets(0, 0, 5, 5);
		gbc_typeCheck.gridx = 0;
		gbc_typeCheck.gridy = 1;
		packetRules.add(typeCheck, gbc_typeCheck);
		
		JCheckBox sourceCheck = new JCheckBox("Source node ID");
		GridBagConstraints gbc_sourceCheck = new GridBagConstraints();
		gbc_sourceCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourceCheck.weighty = 1.0;
		gbc_sourceCheck.weightx = 1.0;
		gbc_sourceCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_sourceCheck.insets = new Insets(0, 0, 5, 5);
		gbc_sourceCheck.gridx = 0;
		gbc_sourceCheck.gridy = 2;
		packetRules.add(sourceCheck, gbc_sourceCheck);
		
		JCheckBox destCheck = new JCheckBox("Destination node ID");
		GridBagConstraints gbc_destCheck = new GridBagConstraints();
		gbc_destCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_destCheck.weighty = 1.0;
		gbc_destCheck.weightx = 1.0;
		gbc_destCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_destCheck.insets = new Insets(0, 0, 5, 5);
		gbc_destCheck.gridx = 0;
		gbc_destCheck.gridy = 3;
		packetRules.add(destCheck, gbc_destCheck);
		
		JCheckBox hopCheck = new JCheckBox("Hop count");
		GridBagConstraints gbc_hopCheck = new GridBagConstraints();
		gbc_hopCheck.insets = new Insets(0, 0, 5, 5);
		gbc_hopCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_hopCheck.weighty = 1.0;
		gbc_hopCheck.weightx = 1.0;
		gbc_hopCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_hopCheck.gridx = 0;
		gbc_hopCheck.gridy = 4;
		packetRules.add(hopCheck, gbc_hopCheck);
		
		JPanel eventRules = new JPanel();
		GridBagConstraints gbc_eventRules = new GridBagConstraints();
		gbc_eventRules.insets = new Insets(0, 0, 5, 0);
		gbc_eventRules.fill = GridBagConstraints.HORIZONTAL;
		gbc_eventRules.weighty = 1.0;
		gbc_eventRules.weightx = 1.0;
		gbc_eventRules.anchor = GridBagConstraints.NORTHWEST;
		gbc_eventRules.gridx = 0;
		gbc_eventRules.gridy = 2;
		body.add(eventRules, gbc_eventRules);
		eventRules.setBorder(new TitledBorder(null, "Event rules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gbl_eventRules = new GridBagLayout();
		gbl_eventRules.columnWidths = new int[] {20, 100, 100, 100, 100, 100};
		gbl_eventRules.columnWeights = new double[]{0.0, 1.0};
		gbl_eventRules.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		eventRules.setLayout(gbl_eventRules);
		
		JCheckBox timeCheck = new JCheckBox("Time");
		GridBagConstraints gbc_timeCheck = new GridBagConstraints();
		gbc_timeCheck.weighty = 1.0;
		gbc_timeCheck.weightx = 1.0;
		gbc_timeCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_timeCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeCheck.insets = new Insets(0, 0, 5, 5);
		gbc_timeCheck.gridx = 0;
		gbc_timeCheck.gridy = 0;
		eventRules.add(timeCheck, gbc_timeCheck);
		
		timeTxt = new JTextField();
		GridBagConstraints gbc_timeTxt = new GridBagConstraints();
		gbc_timeTxt.weighty = 1.0;
		gbc_timeTxt.weightx = 1.0;
		gbc_timeTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_timeTxt.insets = new Insets(0, 0, 5, 5);
		gbc_timeTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeTxt.gridx = 1;
		gbc_timeTxt.gridy = 0;
		eventRules.add(timeTxt, gbc_timeTxt);
		timeTxt.setColumns(10);
		
		evtTypeTxt = new JTextField();
		GridBagConstraints gbc_evtTypeTxt = new GridBagConstraints();
		gbc_evtTypeTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_evtTypeTxt.weighty = 1.0;
		gbc_evtTypeTxt.weightx = 1.0;
		gbc_evtTypeTxt.insets = new Insets(0, 0, 5, 5);
		gbc_evtTypeTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtTypeTxt.gridx = 1;
		gbc_evtTypeTxt.gridy = 1;
		eventRules.add(evtTypeTxt, gbc_evtTypeTxt);
		evtTypeTxt.setColumns(10);
		
		evtSourceTxt = new JTextField();
		GridBagConstraints gbc_evtSourceTxt = new GridBagConstraints();
		gbc_evtSourceTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_evtSourceTxt.weighty = 1.0;
		gbc_evtSourceTxt.weightx = 1.0;
		gbc_evtSourceTxt.insets = new Insets(0, 0, 5, 5);
		gbc_evtSourceTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtSourceTxt.gridx = 1;
		gbc_evtSourceTxt.gridy = 2;
		eventRules.add(evtSourceTxt, gbc_evtSourceTxt);
		evtSourceTxt.setColumns(10);
		
		evtDestTxt = new JTextField();
		GridBagConstraints gbc_evtDestTxt = new GridBagConstraints();
		gbc_evtDestTxt.weighty = 1.0;
		gbc_evtDestTxt.weightx = 1.0;
		gbc_evtDestTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_evtDestTxt.insets = new Insets(0, 0, 5, 5);
		gbc_evtDestTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtDestTxt.gridx = 1;
		gbc_evtDestTxt.gridy = 3;
		eventRules.add(evtDestTxt, gbc_evtDestTxt);
		evtDestTxt.setColumns(10);
		
		evtPacketTxt = new JTextField();
		GridBagConstraints gbc_evtPacketTxt = new GridBagConstraints();
		gbc_evtPacketTxt.weighty = 1.0;
		gbc_evtPacketTxt.weightx = 1.0;
		gbc_evtPacketTxt.anchor = GridBagConstraints.NORTHEAST;
		gbc_evtPacketTxt.insets = new Insets(0, 0, 5, 5);
		gbc_evtPacketTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtPacketTxt.gridx = 1;
		gbc_evtPacketTxt.gridy = 4;
		eventRules.add(evtPacketTxt, gbc_evtPacketTxt);
		evtPacketTxt.setColumns(10);
		
		JCheckBox evtPacketCheck = new JCheckBox("Packet ID");
		GridBagConstraints gbc_evtPacketCheck = new GridBagConstraints();
		gbc_evtPacketCheck.weighty = 1.0;
		gbc_evtPacketCheck.weightx = 1.0;
		gbc_evtPacketCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtPacketCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_evtPacketCheck.insets = new Insets(0, 0, 5, 5);
		gbc_evtPacketCheck.gridx = 0;
		gbc_evtPacketCheck.gridy = 4;
		eventRules.add(evtPacketCheck, gbc_evtPacketCheck);
		
		JCheckBox evtTypeCheck = new JCheckBox("Type");
		GridBagConstraints gbc_evtTypeCheck = new GridBagConstraints();
		gbc_evtTypeCheck.weighty = 1.0;
		gbc_evtTypeCheck.weightx = 1.0;
		gbc_evtTypeCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_evtTypeCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtTypeCheck.insets = new Insets(0, 0, 5, 5);
		gbc_evtTypeCheck.gridx = 0;
		gbc_evtTypeCheck.gridy = 1;
		eventRules.add(evtTypeCheck, gbc_evtTypeCheck);
		
		JCheckBox evtSourceCheck = new JCheckBox("Source node ID");
		GridBagConstraints gbc_evtSourceCheck = new GridBagConstraints();
		gbc_evtSourceCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_evtSourceCheck.weighty = 1.0;
		gbc_evtSourceCheck.weightx = 1.0;
		gbc_evtSourceCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtSourceCheck.insets = new Insets(0, 0, 5, 5);
		gbc_evtSourceCheck.gridx = 0;
		gbc_evtSourceCheck.gridy = 2;
		eventRules.add(evtSourceCheck, gbc_evtSourceCheck);
		
		JCheckBox evtDestCheck = new JCheckBox("Dest node ID");
		GridBagConstraints gbc_evtDestCheck = new GridBagConstraints();
		gbc_evtDestCheck.insets = new Insets(0, 0, 5, 5);
		gbc_evtDestCheck.weighty = 1.0;
		gbc_evtDestCheck.weightx = 0.7;
		gbc_evtDestCheck.anchor = GridBagConstraints.NORTHWEST;
		gbc_evtDestCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_evtDestCheck.gridx = 0;
		gbc_evtDestCheck.gridy = 3;
		eventRules.add(evtDestCheck, gbc_evtDestCheck);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Visualyze");
		btnNewButton.addActionListener(this);
		footer.add(btnNewButton);
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		header.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Use regular expression to set rule ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		header.add(lblNewLabel);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(nodeIDCheck.isSelected()){
			System.err.println("Visualyze now");
			String regex = nodeIdTxt.getText().trim();
			try{
				RowFilter<NodeTableModel,Object> nodeIdFilter = RowFilter.regexFilter(regex, 0);
				nodeFilter.getFilterEditor(0).directFilter(nodeIdFilter);
			}catch(PatternSyntaxException ex){
				ex.printStackTrace();
			}
		}
	}

}
