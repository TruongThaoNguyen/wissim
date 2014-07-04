package controllers.graphicscomponents;

import models.networkcomponents.WirelessNetwork;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import views.Workspace;
import views.helpers.GraphicsUtils;

public class GVerticalRuler extends Canvas {
	public static final int minPixel = 20;
	
	Workspace workspace;
	
	public int thickSize = 15;

	/**
	 * Contructor
	 * Create a Vertical ruler 
	 * @param parent : composite parent
	 * @param style
	 */
	public GVerticalRuler(Composite parent, int style) {
		super(parent, style);
		workspace = (Workspace) ((ScrolledComposite)parent).getContent();
		this.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				WirelessNetwork network = Workspace.getProject().getNetwork();		
				GNetwork gnetwork = workspace.getGraphicNetwork();
				
				// paint the border		
				arg0.gc.drawLine(thickSize - 1, 0, thickSize - 1, getSize().y);
				
				// calculate unit
				double ratio = (double) gnetwork.getSize().y / network.getLength();
				
				int unit = 0;
				while (true) {
					if (unit * ratio > minPixel)
						break;
					
					unit += 10;
				}
				
				// display ruler
				int y;
				int i = 0;
				arg0.gc.setFont(new Font(getDisplay(), "Consolas", 7, SWT.NORMAL));
				while (true) {
					y = (int)(i * unit * ratio);					
					y = y + gnetwork.getLocation().y - ((ScrolledComposite)getParent()).getOrigin().y - thickSize;
					
					if (y > getSize().y)
						break;					
								
					if (unit * i % 50 != 0) {
						arg0.gc.drawLine(thickSize - 4, y, thickSize - 1, y);
					} else {
						arg0.gc.drawLine(thickSize - 5, y, thickSize - 1, y);						
						GraphicsUtils.drawVerticalText(unit * i + "", 0, y - 4, arg0.gc, SWT.UP);
					}
					
					i++;
				}
				
				i = 0;
				while (true) {
					i--;
					y = (int)(i * unit * ratio);
					y = y + gnetwork.getLocation().y - ((ScrolledComposite)getParent()).getOrigin().y - thickSize;					
					if (y < 0)
						break;
					
					if (unit * i % 50 != 0) {
						arg0.gc.drawLine(thickSize - 4, y, thickSize - 1, y);
					} else {
						arg0.gc.drawLine(thickSize - 5, y, thickSize - 1, y);						
						GraphicsUtils.drawVerticalText(unit * i + "", 0, y - 4, arg0.gc, SWT.UP);
					}
				}
			}
		});
		
		final Canvas vline = new Canvas(getParent(), SWT.NONE);
		vline.setBackground(new Color(getParent().getDisplay(), 150, 150, 150));
		vline.setBounds(0, 0, getParent().getClientArea().width, 1);		
		vline.moveAbove(null);		
		vline.setVisible(false);
		vline.setEnabled(false);
		
		this.addMouseTrackListener(new MouseTrackAdapter() {			
			@Override
			public void mouseExit(MouseEvent arg0) {
				vline.setVisible(false);
			}
			
			@Override
			public void mouseEnter(MouseEvent arg0) {
				vline.setVisible(true);
			}
		});
		
		this.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent arg0) {
				vline.setBounds(0, arg0.y + getLocation().y, getParent().getClientArea().width, 1);
				
				GNetwork gnetwork = workspace.getGraphicNetwork();
				int y = arg0.y + GVerticalRuler.this.getLocation().y - gnetwork.getLocation().y;
				y = (int) (y / gnetwork.getRatio());
				GVerticalRuler.this.setToolTipText(y + "");				
			}
		});
	}
	
	/**
	 * Update bound for ruler
	 */
	public void updateBounds() {
		setBounds(0, thickSize, thickSize, getParent().getClientArea().height);
	}

	/**
	 * OnUpdate with Observer
	 */
	public void adaptChanges() {
		updateBounds();
		redraw();
	}
}
