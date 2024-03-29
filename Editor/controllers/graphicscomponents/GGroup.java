package controllers.graphicscomponents;

import models.Project;
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
import views.Workspace;

/**
 * Represents an obstacle in the network
 * @author leecom
 *
 */
public class GGroup extends GSelectableObject {
	/**
	 * the obstacle area.
	 */
	private Area area;
	
	int[] pointArray;
	
	private GNetwork network;
	
	/**
	 * Constructor 
	 * Create a area as a obstacle
	 * @param parent : Composite parent
	 * @param style : style to attack parent composite
	 * @param area : area to define obstacle
	 */
	public GGroup(Composite parent, int style, Area area) {
		super(parent, style);
		
		this.area = area;	
		this.network = ((Workspace) getParent()).getGraphicNetwork();
		
		updateBounds();		
		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				if (isSelected())
					setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
				else
					setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
					//setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.obstacleBackgroundColor));
				
				arg0.gc.setAntialias(SWT.ON);
				
				arg0.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				arg0.gc.setLineWidth(ApplicationSettings.obstacleBorderThickness);
				arg0.gc.drawPolygon(pointArray);
			}
		});
		
		createMenu();
		
		this.setToolTipText("Group " + area.getId());
	}
	
	private void createMenu()
	{
		Menu menu = new Menu(this);
		setMenu(menu);
		
		MenuItem mntmRemoveObstacle = new MenuItem(menu, SWT.NONE);
		mntmRemoveObstacle.setText("Remove");
		mntmRemoveObstacle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				Project.getGroupsList().remove(getArea());				
				GGroup.this.dispose();
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
		
		int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE, maxx = 0, maxy = 0;
		for (int i = 0; i < area.npoints; i++) 
		{
			Point p = getGraphicLocation(area.xpoints[i], area.ypoints[i], ratio);
			
			minx = minx > p.x ? p.x : minx;
			miny = miny > p.y ? p.y : miny;
			maxx = maxx > p.x ? maxx : p.x;
			maxy = maxy > p.y ? maxy : p.y;
		}
		
		pointArray = new int[area.npoints + area.npoints];
		for (int i = 0; i < area.npoints; i++) {
			Point p = getGraphicLocation(area.xpoints[i], area.ypoints[i], ratio);
			
			pointArray[2 * i] 	  = p.x - minx;
			pointArray[2 * i + 1] = p.y - miny;
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
	/**
	 * 
	 * @param x
	 * @param y
	 * @param ratio
	 * @return
	 */
	private Point getGraphicLocation(int x, int y, double ratio) {
		return new Point((int) (x * ratio), (int) (y * ratio));
	}
	
	/**
	 * 
	 * @return area referent to obstacle
	 */
	public Area getArea() {
		return area;
	}
}
