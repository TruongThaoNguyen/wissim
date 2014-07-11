package wissim.controller.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Edge;
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

public class WissimPlayer implements MediaContainer {

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
	public ArrayList<Event> listEventinInterval;
	public int mCurrentEventCount = 0;
	boolean isSimulatingEnergy;
	public double mSpeed = 1.0;
	public WissimPlayer(AbstractParser mParser, String mStartTime,
			String mEndTime, boolean isSimulatingEnergy) {
		super();
		this.mParser = mParser;
		this.mStartTime = mStartTime;
		this.mEndTime = mEndTime;
		this.isSimulatingEnergy = isSimulatingEnergy;
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
		mMediaUI.mSpeedcomboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				
				onChangeSpeed(mMediaUI.mSpeedcomboBox.getSelectedIndex());
			}

			
		});
	}
	/**
	 * 
	 * @param ae
	 */
	private void onChangeSpeed(int ae) {
		// TODO Auto-generated method stub
		
		
		switch(ae){
		case 0:
			mSpeed = 1;
			break;
		case 1:
			mSpeed = 1.25;
			break;
		case 2:
			mSpeed = 1.5;
			break;
		case 3:
			mSpeed = 2.0;
			break;
		case 4:
			mSpeed = 2.5;
			break;
		case 5:
			mSpeed = 3.0;
			break;
		case 6:
			mSpeed = 3.5;
			break;
		case 7:
			mSpeed = 4.0;
			break;
		}
	}
	/**
	 * 
	 * @param graph
	 */
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
	/**
	 * 
	 */
	public void control() {
		/**
		 * 
		 */
		mWissAnimator.setmCurrentParser(mParser);
		listEventinInterval = mWissAnimator.getEventsinInterval(mStartTime,
				mEndTime);
		mMediaUI.mSliderEvent.setMaximum(listEventinInterval.size());
		if (listEventinInterval.size() > 0) {
			mSimulatethread = new Thread(new Runnable() {

				@Override
				public void run() {
					if (isSimulatingEnergy) {

						// TODO Auto-generated method stub
						synchronized (mWissAnimator) {
							String mCurrentTime = listEventinInterval.get(0).time;
							for (Event e : listEventinInterval) {
								if (isPlaying) {
									if(e.time.equals(mCurrentTime) == false){
										if(mSimulatethread.isAlive())
											try {
												mSimulatethread.sleep((long) (250.0/mSpeed));
											} catch (InterruptedException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
									}
									mWissAnimator.animationEnergy("",
											e.getTime(), mGraph, true);

									mMediaUI.mSliderEvent
											.setValue(mCurrentEventCount++);
									mMediaUI.mEventLog.setText("Event "
											+ e.getType() + " From "
											+ e.sourceId + " To " + e.destId+" Packet ID :"+e.packetId);
									mMediaUI.mTimeInfor.setText("Time:"
											+ e.getTime());

									try {
										if (mSimulatethread.isAlive()){
											System.out.println("Sleep "+(long) (250.0/mSpeed));
											mSimulatethread.sleep((long) (250.0/mSpeed));
										}
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}
							if (mCurrentEventCount == listEventinInterval
									.size())
								mCurrentEventCount = 0;
						}
					}

					else {

						// TODO Auto-generated method stub
						synchronized (mWissAnimator) {
							String mCurrentTime = listEventinInterval.get(0).time;
							for (Event e : listEventinInterval) {
								Node sendNode = mGraph.getNode(e.sourceId);
								Node desNode = mGraph.getNode(e.destId);

								if (isPlaying) {
									if(e.time.equals(mCurrentTime) == false){
										if(mSimulatethread.isAlive())
											try {
												mSimulatethread.sleep((long) (250.0/mSpeed));
											} catch (InterruptedException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
									}
									switch (e.type) {
									case "send":

										Edge edge = mGraph.addEdge(e.packetId
												+ "To" + desNode.getId(),
												sendNode, desNode, true);
										edge.addAttribute("ui.layout", "frozen");

										sendNode.setAttribute("ui.class",
												"send");
										desNode.setAttribute("ui.class",
												"receive");

										edge.setAttribute("ui.class", "light");
										break;
									case "receive":
										if (mGraph.getEdge(e.packetId + "To"
												+ desNode.getId()) != null)
											mGraph.removeEdge(e.packetId + "To"
													+ desNode.getId());
										break;
									case "forward":
										Edge fedge = mGraph.addEdge(e.packetId
												+ "To" + desNode.getId(),
												sendNode, desNode, true);
										fedge.addAttribute("ui.layout",
												"frozen");

										sendNode.setAttribute("ui.class",
												"forward");
										desNode.setAttribute("ui.class",
												"receive");
										break;
									case "Drop":
										if (mGraph.getEdge(e.packetId + "To"
												+ desNode.getId()) != null)
											mGraph.removeEdge(e.packetId + "To"
													+ desNode.getId());
										break;
									default:
										break;
									}
									mMediaUI.mEventLog.setText("Event "
											+ e.getType() + " From "
											+ e.sourceId + " To " + e.destId+" Packet ID :"+e.packetId);
									mMediaUI.mSliderEvent
											.setValue(mCurrentEventCount++);
									mMediaUI.mTimeInfor.setText("Time:"
											+ e.getTime());

									try {
										if (mSimulatethread.isAlive()) {
											if (mCurrentTime.equals(e.time)) {

											} else {
												mCurrentTime = e.time;
												mSimulatethread.sleep((long) (250.0/mSpeed));
											}
										}
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}
							if (mCurrentEventCount == listEventinInterval
									.size())
								mCurrentEventCount = 0;
						}

					}
				}

			});
			mSimulatethread.start();

		}

	}
	/**
	 * 
	 */
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
	/**
	 * 
	 */
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
		if (!isPlaying) {
			if (mSimulatethread != null)
				mSimulatethread.resume();
			isPlaying = true;
		} else {
			pause();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		isPlaying = false;
		if (mSimulatethread != null) {

			mSimulatethread.suspend();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSeekBar(final int value) {
		while (mSimulatethread.getState() == Thread.State.TIMED_WAITING) {

		}
		
		
		mCurrentEventCount = value;
		mMediaUI.mSliderEvent.setValue(value);
		// TODO Auto-generated method stub
		System.out.println("OnSeekbar " + value);
		if (mSimulatethread != null && mSimulatethread.isAlive()) {
			mSimulatethread.stop();
			mSimulatethread.interrupt();

		}
		/**
		 * on Create new thread to animation
		 */
		mSimulatethread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (isSimulatingEnergy) {
					int cntSeek = 0;
					// TODO Auto-generated method stub

					synchronized (mWissAnimator) {
						System.out.println("onDrawing after seeking");
						mMediaUI.mSliderEvent.setValue(value);
						List<Event> listEventAfterSeek = listEventinInterval
								.subList(value, listEventinInterval.size());
						String mCurrentTime = listEventAfterSeek.get(0).time;
						for (Event e : listEventAfterSeek) {
							//
							//
							// if(cntSeek++ < value)
							// continue;
							if(e.time.equals(mCurrentTime) == false){
								if(mSimulatethread.isAlive())
									try {
										mSimulatethread.sleep((long) (250.0/mSpeed));
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							}
							System.out
									.println("onDrawing after seeking current count "
											+ mCurrentEventCount);
							mCurrentEventCount++;

							mWissAnimator.animationEnergy("", e.getTime(),
									mGraph, true);
							mMediaUI.mTimeInfor.setText("Time: " + e.getTime());
							mMediaUI.mEventLog.setText("Event " + e.getType()
									+ " From:" + e.sourceId + " To:" + e.destId);

							try {
								if (mSimulatethread.isAlive())
									mSimulatethread.sleep((long) (250.0/mSpeed));
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
						if (cntSeek == listEventinInterval.size()) {
							cntSeek = 0;
							mCurrentEventCount = 0;
						}
					}
				} else {

					List<Event> listEventAfterSeek = listEventinInterval
							.subList(value, listEventinInterval.size());
					while (mGraph.getEdgeCount() > 0) {
						mGraph.removeEdge(0);
					}
					for (int i = 0; i < mGraph.getNodeCount(); i++) {
						mGraph.getNode(i).removeAttribute("ui.class");
					}
					synchronized (mWissAnimator) {
						String mCurrentTime = listEventAfterSeek.get(0).time;
						mMediaUI.mSliderEvent.setValue(value);
						for (Event e : listEventAfterSeek) {
							Node sendNode = mGraph.getNode(e.sourceId);
							Node desNode = mGraph.getNode(e.destId);
							if(e.time.equals(mCurrentTime) == false){
								if(mSimulatethread.isAlive())
									try {
										mSimulatethread.sleep((long) (250.0/mSpeed));
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							}
							if (isPlaying) {
								switch (e.type) {
								case "send":

									Edge edge = mGraph.addEdge(e.packetId
											+ "To" + desNode.getId(), sendNode,
											desNode, true);
									edge.addAttribute("ui.layout", "frozen");

									sendNode.setAttribute("ui.class", "send");
									desNode.setAttribute("ui.class", "receive");

									edge.setAttribute("ui.class", "light");
									break;
								case "receive":
									if (mGraph.getEdge(e.packetId + "To"
											+ desNode.getId()) != null) {
										mGraph.removeEdge(e.packetId + "To"
												+ desNode.getId());

									}
									break;
								case "forward":
									Edge fedge = mGraph.addEdge(e.packetId
											+ "To" + desNode.getId(), sendNode,
											desNode, true);
									fedge.addAttribute("ui.layout", "frozen");

									sendNode.setAttribute("ui.class", "forward");
									desNode.setAttribute("ui.class", "receive");
									break;
								case "Drop":
									if (mGraph.getEdge(e.packetId + "To"
											+ desNode.getId()) != null)
										mGraph.removeEdge(e.packetId + "To"
												+ desNode.getId());
									desNode.removeAttribute("ui.class");
									break;
								default:
									break;
								}
								mMediaUI.mSliderEvent
										.setValue(mCurrentEventCount++);
								mMediaUI.mTimeInfor.setText("Time:"
										+ e.getTime());
								mMediaUI.mEventLog.setText("Event "
										+ e.getType() + " From "
										+ e.sourceId + " To " + e.destId+" Packet ID :"+e.packetId);

								try {
									if (mSimulatethread.isAlive()) {
										if (mCurrentTime.equals(e.time)) {

										} else {
											mCurrentTime = e.time;
											mSimulatethread.sleep((long) (250.0/mSpeed));
										}
									}
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
						if (mCurrentEventCount == listEventinInterval.size())
							mCurrentEventCount = 0;
					}

				}
			}
		});
		mSimulatethread.start();

	}
}
