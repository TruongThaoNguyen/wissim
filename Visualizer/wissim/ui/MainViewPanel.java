package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.EventParser;
import TraceFileParser.wissim.IFileParser;
import TraceFileParser.wissim.NodeEnergy;
import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.Packet;
import TraceFileParser.wissim.FullParser;
import wissim.comtroller.player.WissimPlayer;
import wissim.controller.animation.EventAnimation;
import wissim.controller.filters.gui.TableFilterHeader;
import wissim.object.WiGraph;
import wissim.object.wiEdge;
import wissim.object.wiNode;
import wissim.object.wiNode.State;
import wissim.ui.ViewMouseManager.AreaState;
import wissim.ui.sliderevent.RangeSlider;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JScrollBar;
import java.awt.Button;

public class MainViewPanel extends JPanel implements ViewContainer,
		ActionListener, IObservable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected View view;
	protected JPanel visualizerPanel;
	protected Graph mGraph;
	protected JPanel mediaPanel;
	protected RangeSlider mTimeslider;
	protected String logEvenSlider = "";
	protected JTextArea mTextEventSliderInfor;
	protected JTextArea mConsoleInfor;
	protected JTextArea mLoadingInfor;
	protected JPanel mSliderPanel;
	protected int mSliderValue;
	protected int mSliderUpvalue;
	protected String mTimeInterValInfor;
	protected Viewer viewer;
	protected AbstractParser mParser;
	protected EventParser mTraceFileEventParser;
	protected JPanel consolePanel;
	protected int mCurrentUpValue;
	protected EventAnimation ea;
	protected JPanel mPropertiesPanel;
	protected JPanel networkInforPanel;
	protected JPanel GraphViewPanel;
	protected JPopupMenu jPopUpMenu;
	protected Packet mCurrentFocusPacket;
	protected String mCurrentStartTime = "";
	protected String mCurrentEndTime = "";
	protected int focusCount = 0;
	protected JPopupMenu popupDisplayModeMenu;
	protected ArrayList<String> mCurrentNodeIDGroup1 = new ArrayList<String>();
	protected ArrayList<String> mCurrentNodeIDGroup2 = new ArrayList<String>();
	protected ViewMouseManager vmm;
	private Vector observerList;
	protected JDialog mLoadingDialog;
	protected JPanel mEventPanelInfor;
	protected JTextArea mFilePathInfor;
	protected JTextArea mDisplayModeInfor;
	protected JTextArea mPacketFocusInfor;
	protected String styleSheet = "node.mark {" + "       fill-color: black;"
			+ "};";

	public static enum DisplayMode {
		NORMALMODE, AREAMODE, ENERGYMODE
	}

	public static DisplayMode mCurrentDisplayMode;

	public MainViewPanel() {

		observerList = new Vector();
		mLoadingInfor = new JTextArea("Loading trace file");
		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setLayout(null);
		mCurrentDisplayMode = DisplayMode.NORMALMODE;
		networkInforPanel = new JPanel();
		networkInforPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Network", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		networkInforPanel.setBounds(10, 11, 135, 254);
		add(networkInforPanel);
		networkInforPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		mEventPanelInfor = new JPanel();
		mEventPanelInfor.setLayout(null);
		mEventPanelInfor.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Events",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		mEventPanelInfor.setBounds(10, 276, 135, 400);
		add(mEventPanelInfor);
		mEventPanelInfor.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(145, 11, 800, 700);
		add(tabbedPane);
		JPanel topologyPanel = new JPanel();
		tabbedPane.addTab("Topology", null, topologyPanel, null);

		NodePanel nodePanel = new NodePanel(this);
		tabbedPane.addTab("Nodes", null, nodePanel, null);

//		JTable nodeTables = new JTable();
//		nodeTables.setModel(new DefaultTableModel(new Object[][] {},
//				new String[] {}));
//		nodePanel.add(nodeTables);

		InitGraph();
		 PacketPanel packetPanel = new PacketPanel(this);
		 tabbedPane.addTab("Packets", null, packetPanel, null);

		EventPanel eventPanel = new EventPanel(this);
		tabbedPane.addTab("Events", null, eventPanel, null);
		
		nodePanel.setPacketPanel(packetPanel);
		nodePanel.setEvtPanel(eventPanel);
		packetPanel.setEvtPanel(eventPanel);

		visualizerPanel = new JPanel();
		visualizerPanel.setBackground(Color.WHITE);
		tabbedPane.addTab("Visualizer", null, visualizerPanel, null);
		tabbedPane.setBackgroundAt(4, Color.WHITE);
		visualizerPanel.setLayout(null);
		
//		RulesPanel rulesPanel = new RulesPanel(nodePanel.getFilterHeader(),packetPanel.getFilterHeader(),eventPanel.getFilterHeader());
//		tabbedPane.addTab("Rules", null,rulesPanel,null);

		mediaPanel = new JPanel();
		mediaPanel.setBackground(Color.WHITE);
		mediaPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		mediaPanel.setBounds(10, 0, 770, 88);
		visualizerPanel.add(mediaPanel);

		JButton btnNewButton_1 = new JButton("Open");
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.setBounds(10, 11, 70, 60);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);

				chooser.showOpenDialog(null);
				File[] files = chooser.getSelectedFiles();
				if (files.length != 2) {
					JOptionPane.showMessageDialog(null, "Need import 2 files");
				} else {

					if (files[0].getName().equals("Neighbors.txt")
							|| files[0].getName().equals("Neighbors.tr")) {

						onFileOpen(files[0].getAbsolutePath(),
								files[1].getAbsolutePath());
					} else if (files[1].getName().equals("Neighbors.txt")
							|| files[1].getName().equals("Neighbors.tr")) {

						onFileOpen(files[1].getAbsolutePath(),
								files[0].getAbsolutePath());

					} else
						JOptionPane.showMessageDialog(null,
								"Invalid file format");
				}
			}
		});
		mediaPanel.setLayout(null);
		mediaPanel.add(btnNewButton_1);
		/**
		 * Init event infor slider
		 */
		logEvenSlider = "Lower Bound Event : " + "Upper Bound Event : ";
		mSliderPanel = new JPanel();
		mSliderPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		mSliderPanel.setForeground(Color.WHITE);
		mSliderPanel.setBackground(Color.WHITE);
		mSliderPanel.setBounds(90, 11, 600, 60);
		mediaPanel.add(mSliderPanel);

		Button visualizeBtn = new Button("Visualize");

		visualizeBtn.addActionListener(new visualizeHandle());
		visualizeBtn.setBounds(700, 11, 60, 60);
		mediaPanel.add(visualizeBtn);

		consolePanel = new JPanel();
		consolePanel.setBorder(new TitledBorder(null, "Console",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		consolePanel.setBounds(10, 540, 400, 130);

		visualizerPanel.add(consolePanel);
		mConsoleInfor = new JTextArea();
		mConsoleInfor.setEditable(false);
		mConsoleInfor.setForeground(Color.WHITE);
		mConsoleInfor.setFont(new Font("Arial", Font.PLAIN, 11));
		mConsoleInfor.setPreferredSize(new Dimension(
				consolePanel.getWidth() - 10, 10000));
		mConsoleInfor.setBackground(Color.DARK_GRAY);
		/**
		 * Add scroll bar into console infor text area
		 */
		JScrollPane jscrollpane = new JScrollPane(mConsoleInfor);
		jscrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane.setPreferredSize(new Dimension(
				consolePanel.getWidth() - 5, consolePanel.getHeight() - 5));

		consolePanel.add(jscrollpane);

		mPropertiesPanel = new JPanel();
		mPropertiesPanel.setBackground(new Color(192, 192, 192));
		mPropertiesPanel.setBorder(new TitledBorder(null, "Properties",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		mPropertiesPanel.setBounds(410, 540, 400, 130);
		visualizerPanel.add(mPropertiesPanel);
		mPropertiesPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
		
		
		mPacketFocusInfor = new JTextArea("");
		mPacketFocusInfor.setWrapStyleWord(true);
		mPacketFocusInfor.setBackground(mPropertiesPanel.getBackground());
		mPacketFocusInfor.setEditable(false);
		mPropertiesPanel.add(mPacketFocusInfor);
		GraphViewPanel = new JPanel();
		GraphViewPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GraphViewPanel.setBounds(10, 102, 800, 450);
		visualizerPanel.add(GraphViewPanel);

		view.setSize(GraphViewPanel.getWidth(), GraphViewPanel.getHeight());
		GraphViewPanel.add(view, BorderLayout.CENTER);

		GraphViewPanel.setLayout(null);

		jPopUpMenu = new JPopupMenu();
		jPopUpMenu.setBounds(0, 0, 137, 28);
		GraphViewPanel.add(jPopUpMenu);

		popupDisplayModeMenu = new JPopupMenu();
		GraphViewPanel.add(popupDisplayModeMenu);
		popupDisplayModeMenu.setBounds(0, 0, 137, 28);
		JMenuItem ict1 = new JMenuItem("NORMAL MODE");

		JMenuItem ict2 = new JMenuItem("AREA MODE");

		JMenuItem ict3 = new JMenuItem("ENERGY MODE");
		ict1.addActionListener(new displayModeHandler());
		ict2.addActionListener(new displayModeHandler());
		ict3.addActionListener(new displayModeHandler());
		popupDisplayModeMenu.add(ict1);
		popupDisplayModeMenu.add(ict2);
		popupDisplayModeMenu.add(ict3);
		mTextEventSliderInfor = new JTextArea(logEvenSlider);
		mTextEventSliderInfor.setBounds(10, 430, 700, 20);
		GraphViewPanel.add(mTextEventSliderInfor);
		mTextEventSliderInfor.setFont(new Font("Monospaced", Font.PLAIN, 11));
		GraphViewPanel.setFocusable(true);
		GraphViewPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

				System.out.println("Key" + e.getKeyCode());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Key" + e.getKeyCode());
			}
		});
	}

	public void onFileOpen(String FilePathNode, String FilePathEvent) {
		
		if (mGraph.getNodeCount() > 0) {
			resetGraph(mGraph);
		}
		try {
			mCurrentDisplayMode = DisplayMode.NORMALMODE;
			mTextEventSliderInfor.setText("Lower Bound Event : "
					+ "Upper Bound Event : " + "Time Interval : [--]");
			
			if (AbstractParser.getHeaderFileParser(FilePathEvent).equals("Y")) {
				mParser = new FullParser();
				mParser.setMainPanel(this);
				mParser.ConvertTraceFile(FilePathNode, FilePathEvent);
			} else if (AbstractParser.getHeaderFileParser(FilePathEvent)
					.equals("N")) {
				mParser = new EventParser();
				mParser.setMainPanel(this);
				mParser.ConvertTraceFile(FilePathNode, FilePathEvent);
			}

			notifyObservers();

			int sizeListEvent = mParser.getListEvents().size();
			drawNodes(mParser.getListNodes(), false);
			if (mTimeslider != null) {

				mSliderPanel.remove(mTimeslider);
				mTimeslider = null;
			}
			mTimeslider = new RangeSlider(0, sizeListEvent - 1);
			mTimeslider.setValue(0);
			mTimeslider.setUpperValue(0);
			mTimeslider.setMinorTickSpacing(500);
			mTimeslider.setPaintTicks(true);
			mConsoleInfor.setPreferredSize(new Dimension(consolePanel
					.getWidth() - 10, sizeListEvent * 75));
			mConsoleInfor.setText("");
			mTimeslider
					.setPreferredSize(new Dimension(
							mSliderPanel.getWidth() - 10, mSliderPanel
									.getHeight() - 10));
			mTimeslider.setBackground(Color.WHITE);

			mSliderPanel.add(mTimeslider);
			setTimeSliderListenter();
			networkInforPanel.removeAll();
			JTextArea jt = new JTextArea("Network infor:\n"
					+ "Num.Nodes:\n" + mParser.getListNodes().size()
					+ "\nNum.Events:\n " + mParser.getListEvents().size()
					+ "\nNum.Packets:\n "
					+ mParser.getListPacket().size());
			jt.setBackground(networkInforPanel.getBackground());
			jt.setEditable(false);
			jt.setPreferredSize(new Dimension(
					networkInforPanel.getWidth() - 10, networkInforPanel
							.getHeight() - 10));
			networkInforPanel.add(jt);

			ea = new EventAnimation();
			mCurrentStartTime = mParser.getListEvents().get(0).time;
			mCurrentEndTime = mParser.getListEvents().get(0).time;
			ea.setListEvents(mParser.getListEvents());
			ea.animationEvent(mParser.getListEvents().get(0).time, mParser
					.getListEvents().get(0).time, mGraph);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid File Format");
		}
	}

	private void resetGraph(Graph mGraph2) {
		// TODO Auto-generated method stub

		while (mGraph2.getNodeCount() > 0) {
			mGraph2.removeNode(0);
		}
		while (mGraph2.getEdgeCount() > 0) {
			mGraph2.removeEdge(0);
		}
		/**
		 * Remove view and add new view
		 */
		if (view != null) {
			GraphViewPanel.remove(view);

			InitGraph();
			view.setSize(GraphViewPanel.getWidth(), GraphViewPanel.getHeight());
			GraphViewPanel.add(view, BorderLayout.CENTER);

			GraphViewPanel.setLayout(null);
			mCurrentStartTime = "";
			mCurrentEndTime = "";
			focusCount = 0;
		}

	}

	public void setTimeSliderListenter() {
		// TODO Auto-generated method stub
		mTimeslider.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// // TODO Auto-generated method stub
				//
				// ea.setListEvents(mTraceFileParser.getListEvents());
				int valuedown = ((RangeSlider) arg0.getSource()).getValue();
				int valueup = ((RangeSlider) arg0.getSource()).getUpperValue();
				mCurrentUpValue = valueup;
				logEvenSlider = "Lower Bound Event: " + valuedown
						+ " Upper Bound Event: " + valueup
						+ " Time Interval(ms) ["
						+ mParser.getListEvents().get(valuedown).time + "--"
						+ mParser.getListEvents().get(valueup).time + "]";
				mCurrentStartTime = mParser.getListEvents().get(valuedown).time;
				mCurrentEndTime = mParser.getListEvents().get(valueup).time;
				mTextEventSliderInfor.setText(logEvenSlider);
				mEventPanelInfor.removeAll();
				JTextArea jt1 = new JTextArea("Start Event:\n" + valuedown
						+ "\nEnd Event:\n" + valueup
						+ "\nTime Interval:\n "
						+ "\nFrom:\n " + mParser.getListEvents().get(valuedown).time 
						+ "\nTo: \n"+ mParser.getListEvents().get(valueup).time) ;
				jt1.setBackground(mEventPanelInfor.getBackground());
				jt1.setEditable(false);
				jt1.setPreferredSize(new Dimension(
						mEventPanelInfor.getWidth() - 5, mEventPanelInfor
								.getHeight() - 5));
				
				mEventPanelInfor.add(jt1);
				if (mCurrentDisplayMode == DisplayMode.NORMALMODE) {
					ea.animationEvent(
							mParser.getListEvents().get(valuedown).time,
							mParser.getListEvents().get(valueup).time, mGraph);
					mCurrentStartTime = mParser.getListEvents().get(valuedown).time;
					mCurrentEndTime = mParser.getListEvents().get(valueup).time;
					mConsoleInfor.setText("");
					mConsoleInfor.setLineWrap(true);
					ea.setLogInforfromEvent(mConsoleInfor);
				} else if (mCurrentDisplayMode == DisplayMode.AREAMODE) {

					mCurrentStartTime = mParser.getListEvents().get(valuedown).time;
					mCurrentEndTime = mParser.getListEvents().get(valueup).time;
					System.out
							.println("Release mouse animation event by group ID ");
					System.out.println(mCurrentStartTime + " "
							+ mCurrentEndTime + "GROUP1" + mCurrentNodeIDGroup1
							+ "GROUP2" + mCurrentNodeIDGroup2);
					ea.animationEventbyGroupID(mCurrentStartTime,
							mCurrentEndTime, mCurrentNodeIDGroup1,
							mCurrentNodeIDGroup2, mParser, mGraph);
					mConsoleInfor.setText("");
					ea.setLogInforfromEvent(mConsoleInfor);
				} else {
					mCurrentStartTime = mParser.getListEvents().get(valuedown).time;
					mCurrentEndTime = mParser.getListEvents().get(valueup).time;

					ea.animationEnergy(mCurrentStartTime, mCurrentEndTime,
							mGraph, true);
					mConsoleInfor.setText("");
					ea.setLogInforfromEvent(mConsoleInfor);
				}

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		mTimeslider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				int value = ((RangeSlider) arg0.getSource()).getValue();
				int upvalue = ((RangeSlider) arg0.getSource()).getUpperValue();
				logEvenSlider = "Lower Bound Event : " + value
						+ "Upper Bound Event: " + upvalue
						+ " Time Interval(ms) ["
						+ mParser.getListEvents().get(value).time + "-"
						+ mParser.getListEvents().get(upvalue).time + "]";
				mTextEventSliderInfor.setText(logEvenSlider);

			}
		});
	}

	public void drawNodes(ArrayList<NodeTrace> listNodes, boolean drawNeighbors) {
		Random ran = new Random();
		// TODO Auto-generated method stub
		if (mGraph.getNodeCount() != 0) {
			mGraph.clear();

		}
		view.removeAll();
		if (listNodes == null || mGraph == null) {
			return;
		}
		System.out.println("List Node parsed "+listNodes.toString());
		for (int i = 0; i < listNodes.size(); i++) {
			NodeTrace node = listNodes.get(i);
			Node n = mGraph.addNode(node.id + "");
			n.setAttribute("xy", node.x, node.y);
			n.addAttribute("ui.label", node.id + "");
			n.addAttribute("layout.frozen");

		}
		if (drawNeighbors) {
			for (int i = 0; i < listNodes.size(); i++) {
				NodeTrace node = listNodes.get(i);
				Node currentNode = mGraph.getNode(node.id + "");
				String neigbors = node.getListNeighbors();
				String[] listNeigborsNode = neigbors.split(" ");
				for (String l : listNeigborsNode) {
					Node n = mGraph.getNode(l);
					if (n != null
							&& mGraph.getEdge("NeigborEdge" + l + node.id + "") == null
							&& mGraph.getEdge("NeigborEdge" + node.id + l + "") == null) {
						mGraph.addEdge("NeigborEdge" + l + node.id + "",
								currentNode.getId(), n.getId());

					}

				}

			}
		}

	}

	void InitGraph() {
		mGraph = new MultiGraph("g");
		mGraph.addAttribute("ui.antialias");
		mGraph.addAttribute("ui.quality");
		mGraph.addAttribute("ui.stylesheet", WiGraph.getCssStyle());
		viewer = new Viewer(mGraph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.disableAutoLayout();
		view = viewer.addView("v", new J2DGraphRenderer(), false);

		setGraphMouseManager();

	}

	void setGraphMouseManager() {
		vmm = new ViewMouseManager(mGraph, viewer, view);
		vmm.setViewContainter(this);
	}

	@Override
	public void onLeftClickedonNode(Node node, Point position) {
		// TODO Auto-generated method stub
		System.out.println("leftclick node " + node.getId());
		NodeTrace nt;
		nt = new NodeTrace(Integer.parseInt(node.getId()), "");
		for (NodeTrace nodeT : mParser.getListNodes()) {
			if (nt.id == nodeT.id) {
				nt = nodeT;
				break;
			}
		}
		JOptionPane jp = new JOptionPane();
		jp.setLocation(position);

		jp.showMessageDialog(null, "Node ID: " + nt.getId() + "\n"
				+ "Neighbors: " + nt.getListNeighbors() + "\n" + "Location : ("
				+ nt.getX() + "," + nt.getY() + ")" + "\n");

	}

	@Override
	public void onRighClickedonNode(Node node, Point position) {
		// TODO Auto-generated method stub
		System.out.println("On Right Clicked Mode="
				+ vmm.getmCurrentAreastate());
		if (mCurrentDisplayMode == DisplayMode.AREAMODE) {
			if (vmm.getmCurrentAreastate() == AreaState.ONEAREACHOOSED) {
				JPopupMenu jp = new JPopupMenu();
				JMenuItem je = new JMenuItem("Add to group 1");
				jp.add(je);
				final Node tmpNode = node;
				je.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (mCurrentNodeIDGroup1.size() > 0
								&& !mCurrentNodeIDGroup1.contains(tmpNode
										.getId())) {
							mGraph.getNode(tmpNode.getId()).setAttribute(
									"ui.class", "group1");
							mCurrentNodeIDGroup1.add(tmpNode.getId());
						}

					}
				});
				jp.show(GraphViewPanel, position.x, position.y);
			} else if (vmm.getmCurrentAreastate() == AreaState.TWOAREASCHOOSED) {
				System.out.println("Two areachoosed right clicked");
				JPopupMenu jp1 = new JPopupMenu();
				JMenuItem je1 = new JMenuItem("Add to group 1");
				JMenuItem je2 = new JMenuItem("Add to group 2");
				jp1.add(je1);
				jp1.add(je2);
				final Node tmpNode = node;
				je1.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (mCurrentNodeIDGroup1.size() > 0
								&& !mCurrentNodeIDGroup1.contains(tmpNode
										.getId())) {
							mGraph.getNode(tmpNode.getId()).setAttribute(
									"ui.class", "group1");
							mCurrentNodeIDGroup1.add(tmpNode.getId());
						}

					}
				});
				je2.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {

						if (mCurrentNodeIDGroup2.size() > 0
								&& !mCurrentNodeIDGroup2.contains(tmpNode
										.getId())) {
							mGraph.getNode(tmpNode.getId()).setAttribute(
									"ui.class", "group2");
							mCurrentNodeIDGroup2.add(tmpNode.getId());
						}

					}
				});
				jp1.show(GraphViewPanel, position.x, position.y);

			}
		}
	}

	@Override
	public void onLeftClickedonEdge(String spriteID, Point position) {
		// TODO Auto-generated method stub
		String packetInfor;
		HashMap<String, ArrayList<Event>> hmse = ea.getWsd()
				.getmHashSpriteEvent();
		ArrayList<Event> evInfor = hmse.get(spriteID);
		if (evInfor != null && evInfor.size() > 0) {
			CreatePopUpMenuFromOneEdge(evInfor, position);
		}

		else {
			System.out.println("OPPP " + spriteID + " = null");
		}

	}

	@Override
	public void onRighClickedonEdge(String spriteID, Point position) {
		// TODO Auto-generated method stub

	}

	protected void CreatePopUpMenuFromOneEdge(ArrayList<Event> listEvents,
			Point position) {
		jPopUpMenu.setLabel("List Packets");
		jPopUpMenu.removeAll();
		for (Event e : listEvents) {
			JMenuItem je = new JMenuItem("Packet ID: " + e.getPacketId());
			Packet packet = mParser.getPacketFromID(e.getPacketId());
			je.setToolTipText(e.infor() + packet.Infor());
			je.addActionListener(this);
			jPopUpMenu.add(je);
		}
		if (mCurrentFocusPacket != null && focusCount > 0) {
			JMenuItem je = new JMenuItem("Clear focus path");
			je.addActionListener(this);
			jPopUpMenu.add(je);
		}
		jPopUpMenu.show(GraphViewPanel, position.x, position.y);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getActionCommand());
		if ((e.getActionCommand().substring(11).equals("Clear focus path")) == false) {
			Packet packet = mParser.getPacketFromID(e.getActionCommand()
					.substring(11));

			if (ea != null && packet != null) {
				mCurrentFocusPacket = packet;
				focusCount++;
				
				String nodesPath = "";
				//nodesPath+= packet.sourceID+"-->";
				int cntNodePerLine = 0;
				for(NodeTrace node : packet.getListNode().subList(0, packet.getListNode().size()-2)){
					nodesPath += node.getId() + "-->";
					cntNodePerLine++;
					if((cntNodePerLine % 10) == 0)
					nodesPath+= "\n";
				}
				nodesPath+= packet.getListNode().get(packet.getListNode().size()-1).getId()+".";
				if(packet.isSuccess)
					nodesPath += "(Packet sent successfully)\n";
				else
					nodesPath +="(Packet Dropped) \n";
				mPacketFocusInfor.setText("Focus on Packet :"+ packet.getId() + " Packet Type:"+packet.getType() +"\nPath: " + nodesPath);
				ea.animationPacket(packet, mGraph);
				

			}
		}
		if (e.getActionCommand().toString().equals("Clear focus path")) {
			mPacketFocusInfor.setText("");
			if (ea != null && focusCount > 1) {
				if (mCurrentDisplayMode == DisplayMode.NORMALMODE)
					ea.animationEvent(mCurrentStartTime, mCurrentEndTime,
							mGraph);
				else if (mCurrentDisplayMode == DisplayMode.AREAMODE)
					ea.animationEventbyGroupID(mCurrentStartTime,
							mCurrentEndTime, mCurrentNodeIDGroup1,
							mCurrentNodeIDGroup2, mParser, mGraph);
				focusCount = 0;
			} else if (ea != null && focusCount <= 1
					&& mCurrentFocusPacket != null) {
				ea.resetanimationPacket(mCurrentFocusPacket, mGraph);
				focusCount = 0;
			}
		}
	}

	@Override
	public void onChooseFirstArea(ArrayList<String> listNodeinfirstArea,
			Point position) {
		System.out.println("onchoosefirstarea");
		// TODO Auto-generated method stub
		if (mCurrentDisplayMode == DisplayMode.AREAMODE
				&& vmm.getmCurrentAreastate() == AreaState.NONE) {
			int reply = JOptionPane.showConfirmDialog(null,
					"Mark as group ID 1?");
			if (reply == JOptionPane.YES_OPTION) {
				System.out.println("YES!");
				if (mCurrentNodeIDGroup1.size() > 0) {
					for (String s : mCurrentNodeIDGroup1)
						mGraph.getNode(s).setAttribute("ui.class", "");
				}
				mCurrentNodeIDGroup1.clear();
				for (String s : listNodeinfirstArea) {
					mCurrentNodeIDGroup1.add(s);
					mGraph.getNode(s).setAttribute("ui.class", "group1");
				}

				System.out.println("Group 1:" + mCurrentNodeIDGroup1);
				vmm.setmCurrentAreastate(AreaState.ONEAREACHOOSED);
			} else {
				mCurrentNodeIDGroup1.clear();
				vmm.setmCurrentAreastate(AreaState.NONE);
			}
		}
	}

	@Override
	public void onChooseSecondArea(ArrayList<String> listNodeinsecondArea,
			Point position) {

		// TODO Auto-generated method stub
		if (mCurrentDisplayMode == DisplayMode.AREAMODE
				&& vmm.getmCurrentAreastate() == AreaState.ONEAREACHOOSED) {
			int reply = JOptionPane.showConfirmDialog(null,
					"Mark as group ID 2?");
			if (reply == JOptionPane.YES_OPTION) {

				if (mCurrentNodeIDGroup2.size() > 0) {
					for (String s : mCurrentNodeIDGroup2)
						mGraph.getNode(s).setAttribute("ui.class", "");
				}
				mCurrentNodeIDGroup2 = listNodeinsecondArea;
				for (String s : mCurrentNodeIDGroup2) {
					mGraph.getNode(s).setAttribute("ui.class", "group2");
				}
				vmm.setmCurrentAreastate(AreaState.TWOAREASCHOOSED);

			}
		}
	}

	@Override
	public void onSimulateNetworkbyGroupID() {
		// TODO Auto-generated method stub
		if (ea == null)
			return;

		ea.animationEventbyGroupID(mCurrentStartTime, mCurrentEndTime,
				mCurrentNodeIDGroup1, mCurrentNodeIDGroup2, mParser, mGraph);
		mConsoleInfor.setText("");
		ea.setLogInforfromEvent(mConsoleInfor);
	}

	@Override
	public void onSwitchDisplayMode(Point position) {
		// TODO Auto-generated method stub

		popupDisplayModeMenu.show(GraphViewPanel, position.x, position.y);

	}

	protected class visualizeHandle implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (mCurrentDisplayMode == DisplayMode.AREAMODE
					&& vmm.getmCurrentAreastate() == AreaState.TWOAREASCHOOSED) {
				int repsimulate = JOptionPane.showConfirmDialog(null,
						"Do you want to simulate?");
				if (repsimulate == JOptionPane.YES_OPTION) {
					if (ea != null) {

						ea.animationEventbyGroupID(mCurrentStartTime,
								mCurrentEndTime, mCurrentNodeIDGroup1,
								mCurrentNodeIDGroup2, mParser, mGraph);
						vmm.setmCurrentAreastate(AreaState.NONE);
					}
				} else {
					mCurrentNodeIDGroup1 = new ArrayList<String>();
					mCurrentNodeIDGroup2 = new ArrayList<String>();
					vmm.setmCurrentAreastate(AreaState.NONE);
				}
			} else if (mCurrentDisplayMode == DisplayMode.ENERGYMODE) {

				WissimPlayer wp = new WissimPlayer(mParser, mCurrentStartTime,
						mCurrentEndTime,true);
				wp.visualize();
				wp.control();
			}
			else if(mCurrentDisplayMode == DisplayMode.NORMALMODE){
				WissimPlayer wp = new WissimPlayer(mParser, mCurrentStartTime,
						mCurrentEndTime,false);
				wp.visualize();
				wp.control();
			}
		}

	}

	protected class displayModeHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println(e.getActionCommand());
			switch (e.getActionCommand()) {
			case "NORMAL MODE":
				
				if (ea != null) {
					ea.animationEvent(mCurrentStartTime, mCurrentEndTime,
							mGraph);
				}
				mCurrentDisplayMode = DisplayMode.NORMALMODE;
				if(mPacketFocusInfor.getText().length() > 1)
				mPacketFocusInfor.setText("");
				
				break;
			case "AREA MODE":

				if (ea != null) {
					
					
					ea.reset(mGraph);
				}
				mCurrentDisplayMode = DisplayMode.AREAMODE;
				if(mPacketFocusInfor.getText().length() > 1)
					mPacketFocusInfor.setText("");
				vmm.setmCurrentAreastate(AreaState.NONE);
				break;
			case "ENERGY MODE":

				if (mParser.getResultofheader().equals("Y")) {
					mCurrentDisplayMode = DisplayMode.ENERGYMODE;
					if(mPacketFocusInfor.getText().length() > 1)
						mPacketFocusInfor.setText("");
					if (ea != null) {
						ea.setmCurrentParser(mParser);
						ea.animationEnergy(mCurrentStartTime, mCurrentEndTime,
								mGraph, false);
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Trace file does not contain energy data!");
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void register(IObserver obs) {
		// TODO Auto-generated method stub
		observerList.addElement(obs);
	}

	@Override
	public void unRegister(IObserver obs) {
		// TODO Auto-generated method stub
		observerList.removeElement(obs);
	}

	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		for (int i = 0; i < observerList.size(); i++) {
			IObserver obs = (IObserver) observerList.elementAt(i);
			 obs.update(this);
		}
	}

	public void onNotifyLoading(boolean isLoading) {
		System.out.println("Loading");
		if(isLoading){
			
		}
		else{
			
		}
	}

	
}
