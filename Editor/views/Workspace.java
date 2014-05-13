package views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.Project;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.features.Area;
import models.networkcomponents.features.Label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import controllers.graphicscomponents.GNetwork;
import controllers.graphicscomponents.GObstacle;
import controllers.graphicscomponents.GSelectableObject;
import controllers.graphicscomponents.GWirelessNode;
import controllers.managers.ProjectManager;
import controllers.managers.WorkspacePropertyManager;
import views.dialogs.ViewProjectInfoDialog;

public class Workspace extends Composite {
	public final static int OVERVIEW = 0, EXTEND = 1;
		
	private int mode;			// view mode of workspace
	private double scale;		// handle the scale of the network
	
	private int savedStateIndex; 
	
	// temporary values
	private boolean isLeftMouseDown;
	private Point mouseLeftTempPoint;
	
	List<Label> shownLabels;
	
	WorkspacePropertyManager propertyManager;

	public Workspace(Composite parent, int style) {
		super(parent, style);
		
		isLeftMouseDown = false;		
		this.scale = 1;
		this.mode = OVERVIEW;
		this.propertyManager = new WorkspacePropertyManager();
		mouseLeftTempPoint = null;		
		
		new GNetwork(this, SWT.NONE);				
		savedStateIndex = 0;
		
		shownLabels = new LinkedList<Label>();
		
		// handle the view mode for workspace
		final Composite p = parent;		
		this.addControlListener(new ControlAdapter() {			
			@Override
			public void controlResized(ControlEvent arg0) {
				if (mode == EXTEND && getSize().x <= p.getSize().x && getSize().y <= p.getSize().y)
					mode = OVERVIEW;
				if (mode == OVERVIEW && (getSize().x > p.getSize().x || getSize().y > p.getSize().y))
					mode = EXTEND;
				
				calculateLayoutForScale();			
			}
		});
		
		this.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseUp(MouseEvent arg0) {
				isLeftMouseDown = false;				
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				if (arg0.button == 1) {
					isLeftMouseDown = true;
					mouseLeftTempPoint = new Point(arg0.x, arg0.y);
				}
				
				if (arg0.button == 3)
					updateMenu();
			}
		});
		
		this.addMouseMoveListener(new MouseMoveListener() {			
			@Override
			public void mouseMove(MouseEvent arg0) {
				if (isLeftMouseDown == true && propertyManager.getMouseMode() == WorkspacePropertyManager.HAND) {
					RulerScrolledComposite sc = (RulerScrolledComposite) Workspace.this.getParent();
					sc.setOrigin(sc.getOrigin().x - (arg0.x - mouseLeftTempPoint.x) / 2, sc.getOrigin().y - (arg0.y - mouseLeftTempPoint.y) / 2);
					mouseLeftTempPoint = new Point(arg0.x, arg0.y);
					
					sc.updateRulers();
					
					
				}
			}
		});
		
		this.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent arg0) {
				Cursor cursor = null;
				
				switch (propertyManager.getMouseMode()) {
				case WorkspacePropertyManager.NODE_GEN:
					cursor = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
					break;
				case WorkspacePropertyManager.CURSOR:
				case WorkspacePropertyManager.AREA:
					cursor = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
					break;
				case WorkspacePropertyManager.HAND:
					cursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
					break;
				default:
					cursor = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
				}
				setCursor(cursor);
			}
		});
		
		// update layout for workspace
		updateLayout();
	}
	
	public static Project getProject() { return ProjectManager.getProject(); }
	
	private void updateMenu() {
		Menu menu = new Menu(this);
		setMenu(menu);
		
		if (propertyManager.getMouseMode() == WorkspacePropertyManager.HAND) {
			MenuItem mntmViewProjectInfo = new MenuItem(menu, SWT.NONE);
			mntmViewProjectInfo.setText("Exit Hand Mode");
			mntmViewProjectInfo.addSelectionListener(new SelectionAdapter() {				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					propertyManager.setMouseMode(WorkspacePropertyManager.CURSOR);
					Workspace.this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
					enableNetwork();				
				}
			});			
		}
		
		MenuItem mntmViewProjectInfo = new MenuItem(menu, SWT.NONE);
		mntmViewProjectInfo.setText("Projects Info");
		mntmViewProjectInfo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new ViewProjectInfoDialog(getShell(), SWT.SHEET, Workspace.this).open();
			}
		});
	}
	
	/**
	 * Update layout for workspace. Call after each changed step of user. Very important!!!!!!!!!
	 */
	public void updateLayout() {		
		GNetwork gnetwork = getGraphicNetwork();
		if (gnetwork == null) return;
		
		// check whether the graphic node is sill represented of a node		
		for (GWirelessNode gn : getGraphicNodes()) 
		{						
			if (gn.getWirelessNode() == null)	gn.dispose();		// dispose if isn't represented any node						
			else 								gn.updateBounds();	// update position 			
		}		
		
		
		System.out.println("{so node: }"+getProject().getNetwork().getNodeList().size());
		System.out.println("{network size:x::"+getProject().getNetwork().getWidth()+";y::"+getProject().getNetwork().getLength()+"}");
				
		boolean isExisted;
		// keep a list of nodes that did not have a graphic network (O(n^2))
		for (Node n : getProject().getNetwork().getNodeList()) 
		{
			WirelessNode wn = (WirelessNode) n;
			
			System.out.println("{ID:"+wn.getId()+";X:"+wn.getX()+";Y:"+wn.getY()+";range:"+wn.getRange() + "}");
			
			// check whether nodes in getProject() are instantiated yet
			isExisted = false;
			for (GWirelessNode gn : getGraphicNodes())
			{
				if (gn.getWirelessNode().getId() == n.getId()) 
				{
					isExisted = true;
					break;
				}
			}
			
			if (!isExisted) 
			{				
				GWirelessNode gnode = new GWirelessNode(this, SWT.NONE, (WirelessNode) n);		
				gnode.moveAbove(null);	
				System.out.println(this.getChildren().length);				
			}
		}	
		
		if (getProject().getObstacleList() != null)
		{
			for (Area area : getProject().getObstacleList()) 
			{
				// check whether area in getProject() are instantiated yet			
				isExisted = false;
				for (GObstacle garea : getGraphicObstacles()) 
				{
					if (garea.getArea().getId() == area.getId()) 
					{
						isExisted = true;
						break;
					}
				}
			
				if (!isExisted) 
				{			
					GObstacle gobstacle = new GObstacle(this, SWT.NONE, area);
					gobstacle.moveAbove(gnetwork);
				}
			}
		}
		
		for (GObstacle garea : getGraphicObstacles()) 
		{
			// check whether the graphic node is sill represented of a node
			isExisted = false;
			for (Area area : getProject().getObstacleList())
			{
				if (garea.getArea().getId() == area.getId()) 
				{
					isExisted = true;
					break;
				}
			}
			
			if (!isExisted)	garea.dispose();			
		}
	}
	
	// calculate scale layout every time when workspace is resized
	private void calculateLayoutForScale() {
		GNetwork gnetwork = getGraphicNetwork();		
		if (gnetwork == null) return;		
		gnetwork.updateBounds();		
		
		// initialize bounds for graphics components
		if (mode == OVERVIEW) {			
			// for gnetwork
			gnetwork.setInitSize(gnetwork.getSize().x, gnetwork.getSize().y);
			gnetwork.setInitLocation(gnetwork.getLocation().x, gnetwork.getLocation().y);			
		}
		
		// update rulers
		try {
			((RulerScrolledComposite)getParent()).updateRulers();
		} catch (Exception e) {}
	}
	
	/**
	 * Get graphic node with id of the node
	 * @param id
	 * @return
	 */
	public GWirelessNode getGraphicNodeById(int id) {
		for (Control c : getChildren())
			if (c instanceof GWirelessNode && ((GWirelessNode) c).getWirelessNode().getId() == id)
				return (GWirelessNode) c;
		
		return null;
	}
	
	public GWirelessNode getGraphicNodeByNode(Node node) {
		for (Control c : getChildren())
			if (c instanceof GWirelessNode && ((GWirelessNode) c).getWirelessNode().getId() == node.getId())
				return (GWirelessNode) c;
		
		return null;
	} 
	
	public List<GObstacle> getGraphicObstacles() {
		List<GObstacle> obstacleList = new LinkedList<GObstacle>();
		
		for (Control c: getChildren())
			if (c instanceof GObstacle)
				obstacleList.add((GObstacle) c);
	
		return obstacleList;
	}
	
	/**
	 * Deselect graphic nodes
	 */
	public void deselectGraphicObjects() {
		List<GSelectableObject> selectedList = getSelectedObject();
		
		for (GSelectableObject obj : selectedList)
			obj.setSelect(false);
	}
	
	public GNetwork getGraphicNetwork() {
		for (Control c : getChildren())
			if (c instanceof GNetwork)
				return (GNetwork) c;
		
		return null;
	}
		
	public List<GSelectableObject> getSelectedObject() {
		List<GSelectableObject> selectedList = new ArrayList<GSelectableObject>();
		for (Control c : getChildren())
			if (c instanceof GSelectableObject) {
				if (((GSelectableObject) c).isSelected() == true)
					selectedList.add((GSelectableObject) c);
			}
		
		return selectedList;
	}
	
	public List<GSelectableObject> getSelectableObject() {
		List<GSelectableObject> selectableList = new ArrayList<GSelectableObject>();		
		for (Control c : getChildren())
			if (c instanceof GSelectableObject)
				selectableList.add((GSelectableObject)c);
		
		return selectableList;
	}
	
	public List<GWirelessNode> getGraphicNodes() {
		List<GWirelessNode> nodeList = new LinkedList<GWirelessNode>();
		for (Control c : getChildren()) {
			if (c instanceof GWirelessNode)
				nodeList.add((GWirelessNode) c);
		}
		
		return nodeList;
	}
	
	public int getMode() { return mode; }
	
	public double getScale() { return scale; }	
	
	public void setScale(double d) { this.scale = d; }
	
	public WorkspacePropertyManager getPropertyManager() {
		return propertyManager;
	} 
	
	public void disableNetwork() {
		Control[] cs = this.getChildren();
		
		for (Control c : cs)
			c.setEnabled(false);
	}
	
	public void enableNetwork() {
		Control[] cs = this.getChildren();
		
		for (Control c : cs)
			c.setEnabled(true);
	}
	
	public void disableNodes() {
		Control[] cs = this.getChildren();
		
		for (Control c : cs) {
			if (c instanceof GWirelessNode) {
				c.setEnabled(false);
			}
		}
	}
	
	public void enableNodes() {
		Control[] cs = this.getChildren();
		
		for (Control c : cs) {
			if (c instanceof GWirelessNode) {
				c.setEnabled(true);
			}
		}
	}
	
	public void setObstaclesVisible(boolean bool) {
		if (bool)
			for (GObstacle obs : getGraphicObstacles())
				obs.setVisible(true);
		else
			for (GObstacle obs : getGraphicObstacles())
				obs.setVisible(false);
	}
	
	public void adaptChanges() {
		ScrolledComposite sc = (ScrolledComposite)getParent();
		
		if (mode == Workspace.OVERVIEW)
			setSize(sc.getClientArea().width, sc.getClientArea().height);
		if (mode == Workspace.EXTEND)
			if (getSize().x < sc.getClientArea().width || getSize().y < sc.getClientArea().height) {
				setSize(sc.getClientArea().width + sc.getVerticalBar().getSize().x, 
						sc.getClientArea().height + sc.getHorizontalBar().getSize().y);
				mode = OVERVIEW;
			}
	}
	
	public boolean isMultipleNodesSelected() {
		List<GSelectableObject> selectedList = getSelectedObject();
		if (selectedList.size() == 0) return false;
		
		for (GSelectableObject obj : selectedList)
			if (!(obj instanceof GWirelessNode))
				return false;
		
		return true;
	}	
	
	public int getSavedStateIndex() {
		return savedStateIndex;
	}
	
	public void setSavedStateIndex(int index) {
		this.savedStateIndex = index;
	}
	
	public List<Label> getShownLabels() {
		return shownLabels;
	}
}
