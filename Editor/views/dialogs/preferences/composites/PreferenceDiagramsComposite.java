package views.dialogs.preferences.composites;

import org.eclipse.swt.widgets.Composite;

public class PreferenceDiagramsComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferenceDiagramsComposite(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
