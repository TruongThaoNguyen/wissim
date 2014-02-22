package algorithms;

import java.awt.Point;
import java.awt.Polygon;

public class PolygonAlgorithm extends Algorithm {

	/**
	 * Take input as a Polygon object
	 * @param input
	 */
	public PolygonAlgorithm(Object input) {
		super(input);
	}

	@Override
	public Object doAlgorithm() {
		return null;
	}
	
	/**
	 * Check whether a polygon is simple
	 * @return
	 */
	public boolean isSimple() {
		Polygon polygon = null;
		try {
			polygon = (Polygon) input;
		} catch (Exception e) {
			System.out.println("Invalid input.");
		}
		
		if (polygon == null)
			return false;
		
		for(int i=0;i<polygon.npoints;i++){
			for(int j=0;j<polygon.npoints;j++){
				if(intersect(new Point(polygon.xpoints[i],polygon.ypoints[i]), new Point(polygon.xpoints[i+1],polygon.ypoints[i+1])
				, new Point(polygon.xpoints[j],polygon.ypoints[j]), new Point(polygon.xpoints[j+1],polygon.ypoints[j+1]))){
					return false;
				}
			}
		}
		
		return true;		
	}

	/* isLeft(): tests if P2 is Left|On|Right of the line P0 to P1.
    returns: >0 for left, 0 for on, and <0 for  right of the line.*/
	private float isLeft(Point P0,Point P1,Point P2){
		return (P1.x - P0.x)*(P2.y - P0.y) - (P2.x - P0.x)*(P1.y -  P0.y);
	}
	
	// return true if intersect 
	private boolean intersect(Point x1,Point y1,Point x2,Point y2){
		if(x1.equals(x2) || x1.equals(y2)
				|| y1.equals(x2) || y1.equals(y2) )
			return false;

		float lsign,rsign;
		lsign=isLeft(x2, y2,x1);
		rsign=isLeft(x2, y2,y1);
		if(lsign*rsign > 0) return false;

		lsign=isLeft(x1, y1,x2);
		rsign=isLeft(x1, y1,y2);
		if(lsign*rsign > 0) return false;

		return true;
	}
}
