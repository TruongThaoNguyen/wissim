package controllers.graphicscomponents;

import models.networkcomponents.features.Area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionEvent;

import controllers.managers.ApplicationSettings;
import view.Workspace;

/**
 * Represents an obstacle in the network
 * @author leecom
 *
 */
public class GObstacle extends GSelectableObject {
	// the obstacle area
	private Area area;
	
	int[] pointArray;
	
	private GNetwork network;

	public GObstacle(Composite parent, int style, Area area) {
		super(parent, style);
		
		this.area = area;	
		this.network = ((Workspace) getParent()).getGraphicNetwork();
		
		updateBounds();		
		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				if (isSelected())
					setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				else
					setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.obstacleBackgroundColor));
				
				arg0.gc.setAntialias(SWT.ON);
				
				arg0.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				arg0.gc.setLineWidth(ApplicationSettings.obstacleBorderThickness);
				arg0.gc.drawPolygon(pointArray);
			}
		});
		
		Menu menu = new Menu(this);
		setMenu(menu);
		
		MenuItem mntmRemoveObstacle = new MenuItem(menu, SWT.NONE);
		mntmRemoveObstacle.setText("Remove");
		mntmRemoveObstacle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Workspace w = (Workspace) getParent();
				w.getProject().getObstacleList().remove(getArea());
				w.getCareTaker().save(w.getProject(), "Remove obstacle");
				GObstacle.this.dispose();
			}
		});
	}

	@Override
	public Point getCenterLocation() {
		return null;
	}

	@Override
	protected void updateBounds() {
		double ratio = network.getRatio();
		Region region = new Region();
		
		int minx = 9999, miny = 9999, maxx = 0, maxy = 0;
		for (int i = 0; i < area.npoints; i++) {
			Point p = getGraphicLocation(area.xpoints[i], area.ypoints[i], ratio);
			
			minx = minx > p.x ? p.x : minx;
			miny = miny > p.y ? p.y : miny;
			maxx = maxx > p.x ? maxx : p.x;
			maxy = maxy > p.y ? maxy : p.y;
		}
		
		pointArray = new int[area.npoints + area.npoints];
		for (int i = 0; i < area.npoints; i++) {
			Point p = getGraphicLocation(area.xpoints[i], area.ypoints[i], ratio);
			
			pointArray[2*i] = p.x - minx;
			pointArray[2*i+1] = p.y - miny;
		}
		region.add(pointArray);
		
		Workspace workspace = (Workspace) getParent();
		this.setBounds(
				minx + workspace.getGraphicNetwork().getLocation().x, 
				miny + workspace.getGraphicNetwork().getLocation().y, 
				maxx - minx, 
				maxy - miny);
		
		this.setRegion(region);
	}
	
	private Point getGraphicLocation(int x, int y, double ratio) {
		return new Point((int) (x * ratio), (int) (y * ratio));
	}
	
	public Area getArea() {
		return area;
	}
}
