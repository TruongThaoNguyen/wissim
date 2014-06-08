package wissim.ui;

import java.awt.Point;
import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

public interface ViewContainer {
	public void onLeftClickedonNode(Node node, Point position);
	public void onRighClickedonNode(Node node, Point position);
	public void onLeftClickedonEdge(String spriteID, Point postion);
	public void onRighClickedonEdge(String spriteID, Point position);
	public void onChooseFirstArea(ArrayList<String> listNodeinfirstArea,Point position);
	public void onChooseSecondArea(ArrayList<String> listNodeinsecondArea,Point position);
	public void onSimulateNetworkbyGroupID();
	public void onSwitchDisplayMode(Point position);
}
