package wissim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import TraceFileParser.wissim.NodeTrace;
import TraceFileParser.wissim.TraceFile;
import wissim.controller.animation.EventAnimation;
import wissim.object.WiGraph;
import wissim.object.wiEdge;
import wissim.object.wiNode;
import wissim.object.wiNode.State;

public class MainViewPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected View view;
	protected Graph mGraph;
	protected JPanel mediaPanel;
	protected JSlider mTimeslider;
	protected Viewer viewer;
	protected TraceFile mTraceFileParser;

	public MainViewPanel() {

		setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "NetWork",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 135, 254);
		add(panel);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Events",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		panel_1.setBounds(10, 276, 135, 454);
		add(panel_1);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(145, 11, 1145, 719);
		add(tabbedPane);

		JPanel topologyPanel = new JPanel();
		tabbedPane.addTab("Topology", null, topologyPanel, null);

		JPanel nodePanel = new JPanel();
		tabbedPane.addTab("Nodes", null, nodePanel, null);

		JTable nodeTables = new JTable();
		nodeTables.setModel(new DefaultTableModel(new Object[][] {},
				new String[] {}));
		nodePanel.add(nodeTables);

		InitGraph();
		JPanel packetPanel = new JPanel();
		tabbedPane.addTab("Packets", null, packetPanel, null);

		JPanel eventPanel = new JPanel();
		tabbedPane.addTab("Events", null, eventPanel, null);

		JPanel visualizerPanel = new JPanel();
		tabbedPane.addTab("Visualizer", null, visualizerPanel, null);
		tabbedPane.setBackgroundAt(4, Color.WHITE);
		visualizerPanel.setLayout(null);

		mediaPanel = new JPanel();
		mediaPanel.setBorder(new LineBorder(Color.BLACK));
		mediaPanel.setBounds(10, 0, 1120, 70);
		visualizerPanel.add(mediaPanel);
		mediaPanel.setLayout(null);

		JButton btnNewButton_1 = new JButton("Play");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					onFileOpen("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(10, 11, 59, 48);
		mediaPanel.add(btnNewButton_1);

		JPanel consolePanel = new JPanel();
		consolePanel.setBorder(new TitledBorder(null, "Console",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		consolePanel.setBounds(10, 513, 427, 167);
		visualizerPanel.add(consolePanel);

		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setBorder(new TitledBorder(null, "Properties",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		propertiesPanel.setBounds(447, 513, 683, 167);
		visualizerPanel.add(propertiesPanel);

		JPanel GraphViewPanel = new JPanel();
		GraphViewPanel.setBounds(10, 79, 1120, 423);
		visualizerPanel.add(GraphViewPanel);

		view.setSize(GraphViewPanel.getWidth(), GraphViewPanel.getHeight());
		GraphViewPanel.add(view, BorderLayout.CENTER);

		GraphViewPanel.setLayout(null);

	}

	public void onFileOpen(String mFilePath) throws IOException {
		mTraceFileParser = new TraceFile();

		mTraceFileParser.ConvertTraceFile("Neighbors.txt",
				"Trace_Energy.tr");
		drawNodes(mTraceFileParser.getListNodes(), true);
		int sizeListEvent = mTraceFileParser.getListEvents().size();
		mTimeslider = new JSlider();
		mTimeslider.setBounds(80, 11, 700, 50);
		mTimeslider.setValue(0);

		mediaPanel.add(mTimeslider);
		mTimeslider.setMaximum(sizeListEvent);
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("Start"));
		labelTable.put(new Integer(sizeListEvent / 2), new JLabel("Mid"));
		labelTable.put(new Integer(sizeListEvent), new JLabel("End"));
		mTimeslider.setLabelTable(labelTable);

		mTimeslider.setPaintLabels(true);
		setTimeSliderListenter();
	}

	private void setTimeSliderListenter() {
		// TODO Auto-generated method stub
		mTimeslider.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				EventAnimation ea = new EventAnimation();
				ea.setListEvents(mTraceFileParser.getListEvents());
				int value = ((JSlider) arg0.getSource()).getValue();
				ea.animationEvent(
						mTraceFileParser.getListEvents().get(value).time,
						"495", mGraph);
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

		for (int i = 0; i < listNodes.size(); i++) {
			NodeTrace node = listNodes.get(i);
			Node n = mGraph.addNode(node.id + "");
			n.setAttribute("xy", node.x, node.y);
			n.addAttribute("ui.label", node.id + "");
			n.addAttribute("layout.frozen");

		}

		/**
		 * For test add edge
		 */
		if (drawNeighbors) {
			for (int i = 0; i < listNodes.size(); i++) {
				NodeTrace node = listNodes.get(i);
				Node currentNode = mGraph.getNode(node.id + "");
				String neigbors = node.getListNeighbors();
				String[] listNeigborsNode = neigbors.split(" ");
				for (String l : listNeigborsNode) {
					Node n = mGraph.getNode(l);
					if (n != null
							&& mGraph.getEdge("Edge" + l + node.id + "") == null
							&& mGraph.getEdge("Edge" + node.id + l + "") == null) {
						mGraph.addEdge("Edge" + l + node.id + "",
								currentNode.getId(), n.getId());
					//	System.out.println("add Edge " + l + "-" + node.id);
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
		ViewMouseManager vmm = new ViewMouseManager(mGraph, viewer, view);

	}
}
