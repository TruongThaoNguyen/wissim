package models;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class DialogResult {
	/**
	 * Represents a result returned by CreateProjectDialog
	 * @author Trong Nguyen
	 *
	 */
	public class CreateProjectResult {
		public String name;
		public String path;
		public int width;
		public int height;
		public int time;
	}
	
	public class EditNetworkSizeResult {
		public int width;
		public int height;
		public int time;
		public int lType;
		public int wType;
	}
	
	public class CreateSingleNodeResult {
		public int posX, posY;
	}
	
	public class CreateNodeSetResult {
		public static final int RANDOM = 0, GRID = 1;
		public static final int WHOLE_NETWORK = 0, SELECTED_AREA = 1;
		
		public int creationType;
		public int areaType;
		public int numOfNodes;
		public int x_range;
		public int y_range;
	}
	
	public class SearchNodeResult {
		public final static int ALL = 0, NODE_ID = 1, NODE_NAME = 2;
		
		public int type;
		
		public List<Object> result;
	}
	
	public class NodeLocationResult {
		public int x;
		public int y;
	}
	
	public class ImportLocationDataResult {
		public List<Point> pointList;
		
		public ImportLocationDataResult() {
			pointList = new LinkedList<Point>();
		}
	}
}
