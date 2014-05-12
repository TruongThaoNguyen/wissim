package wissim.comtroller.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;

import wissim.controller.animation.EventAnimation;
import wissim.controller.drawer.WissimDrawer;
import wissim.object.WiGraph;
import wissim.ui.MediaContainer;
import wissim.ui.MediaControlBoardPlayerUI;
import wissim.ui.PlayerUI;

import TraceFileParser.wissim.AbstractParser;
import TraceFileParser.wissim.Event;
import TraceFileParser.wissim.NodeTrace;

public class WissimPlayer implements MediaContainer{

	/**
	 * @param args
	 */
	protected Graph mGraph;
	protected View view;
	protected Viewer viewer;
	protected AbstractParser mParser;
	protected String mStartTime;
	protected String mEndTime;
	protected PlayerUI mPlayerUI;
	protected MediaControlBoardPlayerUI mMediaUI;
	protected WissimDrawer mWissDrawer;
	protected EventAnimation mWissAnimator;
	protected boolean isPlaying = true;
	Thread mSimulatethread;

	public WissimPlayer(AbstractParser mParser, String mStartTime,
			String mEndTime) {
		super();
		this.mParser = mParser;
		this.mStartTime = mStartTime;
		this.mEndTime = mEndTime;
		initGraph();
		mWissDrawer = new WissimDrawer();
		mWissAnimator = new EventAnimation();
		mPlayerUI = new PlayerUI(mGraph, view);
		mPlayerUI.setMediaControler(this);
		mMediaUI = new MediaControlBoardPlayerUI();
		mMediaUI.setPreferredSize(new Dimension(mPlayerUI.getWidth(), 60));
		mMediaUI.setUIContainer(mPlayerUI);
		initGraph(mGraph);
		MousePlayerController mpc = new MousePlayerController(view, mGraph);
	}

	private void initGraph(Graph graph) {
		// TODO Auto-generated method stub
		/**
		 * Redraw all node in graph
		 */
		if (mGraph.getNodeCount() != 0) {
			mGraph.clear();

		}
		view.removeAll();
		if (mParser.getListNodes() == null || mGraph == null) {
			return;
		}

		for (int i = 0; i < mParser.getListNodes().size(); i++) {
			NodeTrace node = mParser.getListNodes().get(i);
			Node n = graph.addNode(node.id + "");
			n.setAttribute("xy", node.x, node.y);
			n.addAttribute("ui.label", node.id + "");
			n.addAttribute("layout.frozen");

		}
	}

	public void control() {
		/**
		 * 
		 */
		String cntTime = mStartTime;
		mWissAnimator.setmCurrentParser(mParser);
		final ArrayList<Event> listEventinInterval = mWissAnimator
				.getEventsinInterval(mStartTime, mEndTime);
		if (listEventinInterval.size() > 0) {
			mSimulatethread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (Event e : listEventinInterval) {
						mWissAnimator.animationEnergy("", e.getTime(), mGraph,
								true);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
			mSimulatethread.start();

		}

	}

	public void visualize() {

		JFrame playerFrame = new JFrame();
		playerFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		BoxLayout boxLayout = new BoxLayout(playerFrame.getContentPane(),
				BoxLayout.Y_AXIS);
		playerFrame.setLayout(boxLayout);
		playerFrame.setPreferredSize(new Dimension(800, 650));
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.95;
		contentPane.add(mPlayerUI, gbc);
		gbc.weighty = 0.05;
		gbc.gridy = 1;
		contentPane.add(mMediaUI, gbc);
		playerFrame.getContentPane().add(contentPane);
		playerFrame.pack();

		playerFrame.setVisible(true);
		playerFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				/*
				 * on Delete thread simulation
				 */
				if (mSimulatethread != null) {
					mSimulatethread.suspend();
					mSimulatethread = null;

				}

			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void initGraph() {
		mGraph = new MultiGraph("g");
		mGraph.addAttribute("ui.antialias");
		mGraph.addAttribute("ui.quality");
		mGraph.addAttribute("ui.stylesheet", WiGraph.getCssStyle());
		viewer = new Viewer(mGraph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.disableAutoLayout();
		view = viewer.addView("v", new J2DGraphRenderer(), false);
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		if(isPlaying){
			
		}
		else{
			pause();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		isPlaying = false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSeekBar(int value) {
		// TODO Auto-generated method stub
		
	}
}
