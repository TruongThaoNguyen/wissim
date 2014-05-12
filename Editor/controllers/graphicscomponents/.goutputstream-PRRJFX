package controllers.graphicscomponents;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import models.Project;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.features.Area;
import models.networkcomponents.features.GraphicLabel;
import models.networkcomponents.features.Label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import controllers.managers.ApplicationManager;
import controllers.managers.ApplicationSettings;
import controllers.managers.ProjectManager;
import controllers.managers.WorkspacePropertyManager;
import views.Workspace;
import views.dialogs.NodeEventsDialog;

public class GWirelessNode extends GSelectableObject {
	// wireless node
	private WirelessNode wirelessNode;

	//private int initX, initY, initWidth, initHeight;
	private GNetwork gnetwork;

	// variables for visualization purpose	
	private int size = ApplicationSettings.nodeSize;

	// menu context of this node
	Menu menu;

	public GWirelessNode(Composite parent, int style, final WirelessNode node) {
		super(parent, style);
		this.wirelessNode = node;
		this.gnetwork = ((Workspace) getParent()).getGraphicNetwork();

		updateBounds();	
		updateMenu();
		
		this.wirelessNode.addObserver(new Observer() {			
			@Override
			public void update(Observable o, Object arg) {
				if (arg.toString().equals("Position")) {
					updateBounds();
				}					
			}
		});

		addPaintListener(new PaintListener() {			
			@Override
			public void paintControl(PaintEvent arg0) {
				arg0.gc.setAntialias(SWT.ON);	

				size = ApplicationSettings.nodeSize;
//				size = 10;
				List<Label> shownLabelList = getShownLabels();					
				
				if (isSelected() == false) {
					if (shownLabelList.size() == 0)
						arg0.gc.setBackground(ApplicationSettings.colorAWTtoSWT(ApplicationSettings.nodeColor));
					else if (shownLabelList.size() == 1) {
						GraphicLabel l = (GraphicLabel) shownLabelList.get(0);
						Color color = new Color(Display.getCurrent(), l.getColor().getRed(), l.getColor().getGreen(), l.getColor().getBlue());
						arg0.gc.setBackground(color);
					}
				} else {
					arg0.gc.setBackground(new Color(Display.getCurrent(), 0, 0, 0));
				}
				
				switch (ApplicationSettings.nodeBorderType) {
				case ApplicationSettings.CIRCLE:
					arg0.gc.fillOval(0, 0, size, size);
					arg0.gc.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
					arg0.gc.drawOval(0, 0, size - 1, size - 1);
					break;
				case ApplicationSettings.SQUARE:
					arg0.gc.fillRectangle(0, 0, size, size);
					arg0.gc.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
					arg0.gc.drawRectangle(0, 0, size - 1, size - 1);
					break;
				}
				
				if (shownLabelList != null && shownLabelList.size() > 1) {
					arg0.gc.drawLine(0, size / 2, size, size / 2);
					arg0.gc.drawLine(size / 2, 0, size / 2, size);
				}
			}
		});

		addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {				
				Workspace workspace = (Workspace) getParent();
				if (workspace.getPropertyManager().getVisualizeMode() == WorkspacePropertyManager.VISUAL_ON)
					return;
				
				if (isLeftMouseDown == true)
					isMouseDrag = true;
				
				if (isLeftMouseDown == true) {					
					// calculate graphic location
					int gx = getLocation().x + getSize().x / 2 + arg0.x - mouseStartPoint.x;
					int gy = getLocation().y + getSize().y / 2 + arg0.y - mouseStartPoint.y;

					// calculate real location
					int x = (int) (((double)gx - gnetwork.getLocation().x) / gnetwork.getRatio());
					int y = (int) (((double)gy - gnetwork.getLocation().y) / gnetwork.getRatio());
					
					for (Area a : workspace.getProject().getObstacleList())
						if (a.contains(x, y))
							return;

					node.setPosition(x, y);

					String str = "Node " + wirelessNode.getId() + "\n" + "(" + wirelessNode.getX() + ", " + wirelessNode.getY() + ")";
					setToolTipText(str);
				}
			}
		});

		String str = "Node " + wirelessNode.getId() + "\n" + "(" + wirelessNode.getX() + ", " + wirelessNode.getY() + ")";
		this.setToolTipText(str);
		
		addDisposeListener(new DisposeListener() {			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				ProjectManager.deleteNode(node);
			}
		});
	}

	protected void updateMenu() {
		if (menu != null)
			menu.dispose();

		menu = new Menu(this);
		setMenu(menu);

		MenuItem mntmViewNodeProperties = new MenuItem(menu, SWT.NONE);
		mntmViewNodeProperties.setText("Properties");
		mntmViewNodeProperties.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {				
				ApplicationManager.viewNodeInfo((Workspace)getParent(), GWirelessNode.this);
			}
		});
		
		 new MenuItem(menu, SWT.SEPARATOR);

		MenuItem mntmShowNeighbors = new MenuItem(menu, SWT.NONE);
		mntmShowNeighbors.setText("Show Neighbors");
		mntmShowNeighbors.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.showNeighbors((Workspace)getParent());
			}
		});
		
		MenuItem mntmShowRange = new MenuItem(menu, SWT.NONE);
		mntmShowRange.setText("Show Range");
		mntmShowRange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.showRange((Workspace)getParent());
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmMoveTo = new MenuItem(menu, SWT.NONE);
		mntmMoveTo.setText("Move To");
		mntmMoveTo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ApplicationManager.moveNodeTo(GWirelessNode.this);
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmNodeEvents = new MenuItem(menu, SWT.NONE);
		mntmNodeEvents.setText("Events");
		mntmNodeEvents.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new NodeEventsDialog(getShell(), SWT.SHEET, wirelessNode).open();
			}
		});

		Project project = ((Workspace) getParent()).getProject();

		if (project.getLabelList() != null) {		
			MenuItem setLabelMenu = new MenuItem(menu, SWT.CASCADE, 2);
			setLabelMenu.setText("Set Label");

			Menu labelsMenu = new Menu(setLabelMenu);
			setLabelMenu.setMenu(labelsMenu);

			MenuItem removeLabelMenu = new MenuItem(menu, SWT.CASCADE, 3);
			removeLabelMenu.setText("Remove Label");

			Menu lblMenu = new Menu(removeLabelMenu);
			removeLabelMenu.setMenu(lblMenu);

			MenuItem mntmLabel;
			for (final Label l : project.getLabelList()) {
				if (!l.getNodeList().contains(wirelessNode)) {
					mntmLabel = new MenuItem(labelsMenu, SWT.NONE);
					mntmLabel.setText(l.getName());

					Image img = new Image(getParent().getDisplay(), 15, 15);
					GC gc = new GC(img);

					java.awt.Color c = ((GraphicLabel)l).getColor();
					Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
					gc.setBackground(color);
					gc.fillRectangle(img.getBounds());
					gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
					gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
					gc.dispose();			
					mntmLabel.setImage(img);

					mntmLabel.addSelectionListener(new SelectionAdapter() {				
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							l.add(wirelessNode);
						}
					});
				} else {
					mntmLabel = new MenuItem(lblMenu, SWT.NONE);
					mntmLabel.setText(l.getName());

					Image img = new Image(getParent().getDisplay(), 15, 15);
					GC gc = new GC(img);

					java.awt.Color c = ((GraphicLabel)l).getColor();
					Color color = new Color(getParent().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
					gc.setBackground(color);
					gc.fillRectangle(img.getBounds());
					gc.setBackground(new Color(getParent().getDisplay(), 31, 31, 31));
					gc.drawRectangle(0, 0, img.getBounds().width - 1, img.getBounds().height - 1);
					gc.dispose();			
					mntmLabel.setImage(img);

					mntmLabel.addSelectionListener(new SelectionAdapter() {				
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							l.remove(wirelessNode);
						}
					});					
				}
			}

			if (labelsMenu.getItemCount() == 0)
				setLabelMenu.dispose();

			if (lblMenu.getItemCount() == 0)
				removeLabelMenu.dispose();
		}
		
		MenuItem mntmDelete = new MenuItem(menu, SWT.NONE);
		mntmDelete.setText("Delete");
		mntmDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				GWirelessNode.this.dispose();
			}
		});				
	}

	@Override
	protected void updateBounds() {
		int gx = (int)(wirelessNode.getX() * gnetwork.getRatio() + gnetwork.getLocation().x - size / 2);
		int gy = (int)(wirelessNode.getY() * gnetwork.getRatio() + gnetwork.getLocation().y - size / 2);

		setBounds(gx, gy, size, size);	
	}
	
	@Override
	public Point getCenterLocation() {
		int x = getLocation().x + getSize().x / 2 - gnetwork.getLocation().x;
		int y = getLocation().y + getSize().y / 2 - gnetwork.getLocation().y;
		
		return new Point(x, y);
	}

	public WirelessNode getWirelessNode() {
		return wirelessNode;
	}
	
	public List<Label> getShownLabels() {
		Workspace w = (Workspace) getParent();
		List<Label> list = new LinkedList<Label>();
		
		for (Label l : w.getShownLabels()) {
			if (l.hasNode(wirelessNode))
				list.add(l);
		}
		
		return list;
	}
}