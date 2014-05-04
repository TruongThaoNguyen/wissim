/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wissim.ui;

import java.awt.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiNode;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.swingViewer.LayerRenderer;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.MouseManager;

import org.graphstream.ui.geom.Point3;

import wissim.object.WiGraph;

/**
 * 
 * @author DangKhanh
 */
public class ViewMouseManager implements MouseWheelListener {

	protected Viewer viewer;
	protected View view;
	Graph graph;
	double press_x = 0;
	double press_y = 0;
	double current_center_x;
	double current_center_y;
	double curr_point_x;
	double curr_point_y;
	double curr_mouse_x;
	double curr_mouse_y;
	double drag_x = 0;
	double drag_y = 0;
	double drag_start_choose_node_area_x = 0;
	double drag_start_choose_node_area_y = 0;
	double drag_end_choose_node_area_x = 0;
	double drag_end_choose_node_area_y = 0;
	double previous_drag_x = 0;
	double previous_drag_y = 0;
	boolean isPressed = false;
	boolean isRelease = false;
	boolean isChoosingArea = false;
	boolean isShiftDown = false;
	/**
	 * Array list nodes contain node in chosen area
	 */
	ArrayList<String> Nodes = new ArrayList<>();
	ArrayList<String> NodeDetectonRightClick = new ArrayList<>();

	public ViewMouseManager(Graph graph, Viewer viewer, View view) {
		this.viewer = viewer;
		this.view = view;
		this.graph = graph;
	//	this.graph.reset();
		current_center_x = view.getCamera().getViewCenter().x;
		current_center_y = view.getCamera().getViewCenter().y;
		System.out.println("Current Center " + current_center_x + " "
				+ current_center_y);
		// view.getCamera().setViewCenter(current_center_x, current_center_y,
		// 0);

		setMouseListener();
		// this.view = this.viewer.addDefaultView(false);
	}

	public void setMouseListener() {
		view.addMouseWheelListener(this);

		view.setMouseManager(new MouseManager() {
			@Override
			public void init(GraphicGraph gg, View view) {
				view.addMouseListener(this);
				view.addMouseMotionListener(this);
				view.repaint();
			}

			@Override
			public void release() {
				// throw new
				// UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && isChoosingArea) {
					if (!inArea(me)) {
						System.out.println("Right click out of are");
						Point p = me.getPoint();

						GraphicElement e = view.findNodeOrSpriteAt(p.getX(),
								p.getY());

						if (e != null) {
						} else {
						}
					}
				} else if (SwingUtilities.isRightMouseButton(me)
						&& !isChoosingArea) {
					System.out.println("Not choosing area");
					Point p = me.getPoint();

					GraphicElement e = view.findNodeOrSpriteAt(p.getX(),
							p.getY());

					System.out.println("Toa do: " + p.getX() + "  " + p.getY());
					System.out.println("NULL?=" + (e == null));
					if (e != null) {
					} else {
					}
				} else if (SwingUtilities.isLeftMouseButton(me)
						&& !isChoosingArea) {
					Point p = me.getPoint();
					GraphicElement e = view.findNodeOrSpriteAt(p.getX(),
							p.getY());
					if (e != null) {
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent me) {
				// throw new
				// UnsupportedOperationException("Not supported yet.");

				// System.out.println("mousePressed");
				isPressed = true;
				if (isChoosingArea == true
						&& SwingUtilities.isLeftMouseButton(me)) {
					isChoosingArea = false;
					// System.out.println("inArea? " + inArea(me));
					view.beginSelectionAt(me.getX(), me.getY());
					view.endSelectionAt(me.getX(), me.getY());
				}
				if (SwingUtilities.isLeftMouseButton(me) && !me.isShiftDown()) {
					curr_point_x = me.getPoint().getX();
					curr_point_y = me.getPoint().getY();
					press_x = Double.parseDouble(Integer.toString(me.getX()));
					press_y = Double.parseDouble(Integer.toString(me.getY()));
					previous_drag_x = press_x;
					previous_drag_y = press_y;
					// System.out.println("onPressed" + press_x + " " +
					// press_y);
					isRelease = false;

					view.endSelectionAt(me.getX(), me.getY());

				} else if (SwingUtilities.isLeftMouseButton(me)
						&& me.isShiftDown()) {
					drag_start_choose_node_area_x = me.getX();
					drag_start_choose_node_area_y = me.getY();
					isChoosingArea = true;
					view.beginSelectionAt(me.getX(), me.getY());

					System.out.println("onPress with shift key");
					Nodes.clear();
				} else if (SwingUtilities.isRightMouseButton(me) && inArea(me)
						&& isChoosingArea == true) {

					Point mpoint = new Point(me.getX(), me.getY());

					// onShowMenu(mpoint, true);
					// System.out.println("onRightClick,Node=" +
					// Nodes.toString());
					
				} else if (SwingUtilities.isRightMouseButton(me) && !inArea(me)
						&& isChoosingArea == true) {
					Point mpoint = new Point(me.getX(), me.getY());
					// onShowMenu(mpoint, false);
					view.beginSelectionAt(me.getX(), me.getY());
					view.endSelectionAt(me.getX(), me.getY());

				}

			}

			@Override
			public void mouseReleased(MouseEvent me) {
				// throw new
				// UnsupportedOperationException("Not supported yet.");
				// System.out.println("onRelease");

				if (SwingUtilities.isLeftMouseButton(me) && !me.isShiftDown()
						&& isChoosingArea == false) {

					if (!(me.getX() == press_x && me.getY() == press_y)
							&& isPressed == true) {
						MoveGraph(me.getPoint());
						isPressed = false;

					}

					isRelease = true;

				} else if (SwingUtilities.isLeftMouseButton(me)
						&& me.isShiftDown()) {
					drag_end_choose_node_area_x = me.getX();
					drag_end_choose_node_area_y = me.getY();
					isChoosingArea = true;

					// System.out.println("onRelease with shift key");
					curr_mouse_x = me.getX();
					curr_mouse_y = me.getY();

					findNodeDragging(drag_start_choose_node_area_x,
							drag_start_choose_node_area_y,
							drag_end_choose_node_area_x,
							drag_end_choose_node_area_y);
					// System.out.println("Data in nodes " + Nodes.toString());

				}

			}

			@Override
			public void mouseEntered(MouseEvent me) {
				// throw new
				// UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void mouseExited(MouseEvent me) {
				// throw new
				// UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void mouseDragged(MouseEvent me) {
				// throw new
				// UnsupportedOperationException("Not supported yet.");

				if (SwingUtilities.isLeftMouseButton(me) && !me.isShiftDown()
						&& isChoosingArea == false) {
					drag_x = Double.parseDouble(Integer.toString(me.getX()));
					drag_y = Double.parseDouble(Integer.toString(me.getY()));

					if (isRelease == false) {

						MoveGraph(me.getPoint());
					}
				} else if (SwingUtilities.isLeftMouseButton(me)
						&& me.isShiftDown()) {
					view.selectionGrowsAt(me.getX(), me.getY());
				}

			}

			@Override
			public void mouseMoved(MouseEvent me) {
			}

			private void MoveGraph(Point curr_point) {
				// System.out.println("onMove");
				Point3 currP = view.getCamera().transformPxToGu(curr_point.x,
						curr_point.y);
				Point3 lastP = view.getCamera().transformPxToGu(curr_point_x,
						curr_point_y);

				current_center_x = view.getCamera().getViewCenter().x;
				current_center_y = view.getCamera().getViewCenter().y;
				double off_x = lastP.x - currP.x;
				double off_y = lastP.y - currP.y;
				view.getCamera().setViewCenter(current_center_x + off_x,
						current_center_y + off_y, 0);

				current_center_x = view.getCamera().getViewCenter().x;
				current_center_y = view.getCamera().getViewCenter().y;
				curr_point_x = curr_point.getX();
				curr_point_y = curr_point.getY();
			}

			private void findNodeDragging(double start_x, double start_y,
					double end_x, double end_y) {

				ArrayList<GraphicElement> listNode = new ArrayList<>();
				listNode = view.allNodesOrSpritesIn(start_x < end_x ? start_x
						: end_x, start_y < end_y ? start_y : end_y,
						start_x > end_x ? start_x : end_x,
						start_y > end_y ? start_y : end_y);

				for (GraphicElement el : listNode) {
					if (graph.getNode(el.getId()) != null) {
						Nodes.add(el.getId());
					}
				}

			}
		});
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rot = e.getWheelRotation();
		Camera c = view.getCamera();
		double p = c.getViewPercent();
		if (rot > 0) {
			if (p < 2.5) {
				c.setViewPercent(p + 0.1);

			}
		} else {
			if (p > 0.2) {
				c.setViewPercent(p - 0.1);
			}
		}

		e.consume();
	}

	protected boolean inArea(MouseEvent me) {

		if ((int) Math.abs(drag_start_choose_node_area_x - me.getX())
				+ (int) Math.abs(drag_end_choose_node_area_x - me.getX()) == (int) Math
				.abs(drag_start_choose_node_area_x
						- drag_end_choose_node_area_x)
				&& (int) Math.abs(drag_start_choose_node_area_y - me.getY())
						+ (int) Math.abs(drag_end_choose_node_area_y
								- me.getY()) == (int) Math
						.abs(drag_start_choose_node_area_y
								- drag_end_choose_node_area_y)) {
			return true;
		} else {
			return false;
		}
	}

}
