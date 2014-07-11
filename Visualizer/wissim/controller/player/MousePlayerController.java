package wissim.controller.player;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.MouseManager;

public class MousePlayerController implements MouseWheelListener {
	/**
	 * Control mouse interact with player UI
	 */
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
	boolean isPressed;
	boolean isReleased;

	public MousePlayerController(View view, Graph graph) {
		super();
		this.view = view;
		this.graph = graph;
		current_center_x = view.getCamera().getViewCenter().x;
		current_center_y = view.getCamera().getViewCenter().y;
		System.out.println("Current Center " + current_center_x + " "
				+ current_center_y);

		setMouseListener();
	}

	private void setMouseListener() {
		// TODO Auto-generated method stub

		view.addMouseWheelListener(this);
		view.setMouseManager(new MouseManager() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDragged(MouseEvent me) {
				// TODO Auto-generated method stub
				drag_x = Double.parseDouble(Integer.toString(me.getX()));
				drag_y = Double.parseDouble(Integer.toString(me.getY()));

				if (isReleased == false) {

					MoveGraph(me.getPoint());
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				// TODO Auto-generated method stub
				if (!(me.getX() == press_x && me.getY() == press_y)
						&& isPressed == true) {
					MoveGraph(me.getPoint());
					isPressed = false;

				}

				isReleased = true;
			}

			private void MoveGraph(Point curr_point) {

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

			@Override
			public void mousePressed(MouseEvent me) {

				isPressed = true;

				curr_point_x = me.getPoint().getX();
				curr_point_y = me.getPoint().getY();
				press_x = Double.parseDouble(Integer.toString(me.getX()));
				press_y = Double.parseDouble(Integer.toString(me.getY()));
				previous_drag_x = press_x;
				previous_drag_y = press_y;
				isReleased = false;

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void release() {

			}

			@Override
			public void init(GraphicGraph arg0, View arg1) {

				view.addMouseListener(this);
				view.addMouseMotionListener(this);
				view.repaint();
			}
		});
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub

		int rot = e.getWheelRotation();
		Camera c = view.getCamera();
		double p = c.getViewPercent();
		if (rot > 0) {
			if (p < 6) {
				c.setViewPercent(p + 0.1);

			}
		} else {
			if (p > 0.2) {
				c.setViewPercent(p - 0.1);
			}
		}

		e.consume();

	}

}
