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

import view.Workspace;

public class GHorizontalRuler extends Canvas {
	public static final int minPixel = 20;
	
	Workspace workspace;
	
	public int thickSize = 15;
	
	public GHorizontalRuler(Composite parent, int style) {
		super(parent, style);
	
		workspace = (Workspace) ((ScrolledComposite)parent).getContent();
		this.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

		this.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				WirelessNetwork network = workspace.getProject().getNetwork();		
				GNetwork gnetwork = workspace.getGraphicNetwork();
				
				// paint the border		
				arg0.gc.drawLine(0, thickSize - 1, getSize().x, thickSize - 1);
				
				// calculate unit
				double ratio = (double) gnetwork.getSize().x / network.getWidth();
				
				int unit = 0;
				while (true) {
					if (unit * ratio > minPixel)
						break;
					
					unit += 10;
				}
				
				// display ruler
				int x;
				int i = 0;
				arg0.gc.setFont(new Font(getDisplay(), "Consolas", 7, SWT.NORMAL));
				while (true) {
					x = (int)(i * unit * ratio);					
					x = x + gnetwork.getLocation().x - ((ScrolledComposite)getParent()).getOrigin().x - thickSize;
					
					if (x > getSize().x)
						break;					
								
					if (unit * i % 50 != 0) {
						arg0.gc.drawLine(x, thickSize - 4, x, thickSize - 1);
					} else {
						arg0.gc.drawLine(x, thickSize - 5, x, thickSize - 1);						
						arg0.gc.drawText(unit * i + "", x - 4, 0, true);
					}
					
					i++;
				}
				
				i = 0;
				while (true) {
					i--;
					x = (int)(i * unit * ratio);
					x = x + gnetwork.getLocation().x - ((ScrolledComposite)getParent()).getOrigin().x - thickSize;					
					if (x < 0)
						break;
					
					if (unit * i % 50 != 0) {
						arg0.gc.drawLine(x, thickSize - 4, x, thickSize - 1);
					} else {
						arg0.gc.drawLine(x, thickSize - 5, x, thickSize - 1);
						arg0.gc.drawText(unit * i + "", x - 4, 0, true);
					}
				}
			}
		});
		
		final Canvas hLine = new Canvas(getParent(), SWT.NONE);
		hLine.setBackground(new Color(getParent().getDisplay(), 150, 150, 150));
		hLine.setBounds(0, 0, 1, getParent().getClientArea().height);		
		hLine.moveAbove(null);		
		hLine.setVisible(false);
		hLine.setEnabled(false);
		
		this.addMouseTrackListener(new MouseTrackAdapter() {			
			@Override
			public void mouseExit(MouseEvent arg0) {
				hLine.setVisible(false);
			}
			
			@Override
			public void mouseEnter(MouseEvent arg0) {
				hLine.setVisible(true);
			}
		});
		
		this.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent arg0) {
				hLine.setBounds(arg0.x + getLocation().x, 0, 1, getParent().getClientArea().height);
				
				GNetwork gnetwork = workspace.getGraphicNetwork();
				int x = arg0.x + GHorizontalRuler.this.getLocation().x - gnetwork.getLocation().x;
				x = (int) (x / gnetwork.getRatio());
				GHorizontalRuler.this.setToolTipText(x + "");
			}
		});
	}
	
	public void updateBounds() {
		setBounds(thickSize, 0, getParent().getClientArea().width, thickSize);
	}

	public void adaptChanges() {
		updateBounds();
		redraw();
	}
}
