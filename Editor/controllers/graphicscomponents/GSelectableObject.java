package controllers.graphicscomponents;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import controllers.managers.WorkspacePropertyManager;
import views.Workspace;

/**
 * Express a graphic object that can be selected 
 * @author leecom
 *
 */
public abstract class GSelectableObject extends Canvas {
	protected boolean isSelected = false;
	
	protected boolean isLeftMouseDown = false;
	protected boolean isMouseDrag = false;
	protected Point mouseStartPoint;		
	
	public GSelectableObject(Composite parent, int style) {
		super(parent, style);
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				Workspace workspace = (Workspace) getParent();
				WorkspacePropertyManager workspacePropertyManager = workspace.getPropertyManager();
				
				switch (workspacePropertyManager.getMouseMode()) {
				case WorkspacePropertyManager.CURSOR:
					if (!workspace.getPropertyManager().isControlKeyPressed())
						workspace.deselectGraphicObjects();

					if (workspace.getSelectedObject().contains(GSelectableObject.this))
						setSelect(false);
					else
						setSelect(true);

					redraw();
					break;
				case WorkspacePropertyManager.NODE_GEN:
					break;
				}				
				
				if (isMouseDrag == true) {
					//((Workspace) getParent()).getCareTaker().save(((Workspace) getParent()).getProject(), "Move object");
					workspace.getCareTaker().save(workspace.getProject(), "Move object");
					workspace.updateLayout();
					workspace.deselectGraphicObjects();
					isMouseDrag = false;
					
					//workspace.getGraphicNodeByNode(node).setSelect(true);
				}

				isLeftMouseDown = false;				
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				// capture left mouse
				if (arg0.button == 1) {
					isLeftMouseDown = true;
					mouseStartPoint = new Point(arg0.x, arg0.y);
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelect(boolean select) {
		this.isSelected = select;
		redraw();
	}
	
	public abstract Point getCenterLocation();
	
	protected abstract void updateBounds();
}
