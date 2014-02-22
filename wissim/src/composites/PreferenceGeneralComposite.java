package composites;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;

public class PreferenceGeneralComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PreferenceGeneralComposite(Composite parent, int style) {
		super(parent, style);
		
		Label lblRuler = new Label(this, SWT.NONE);
		lblRuler.setBounds(25, 24, 213, 15);
		lblRuler.setText("Show ruler when creating new project");
		
		Combo combo = new Combo(this, SWT.READ_ONLY);
		combo.setItems(new String[] {"Show", "Do not show"});
		combo.setBounds(263, 20, 97, 23);
		combo.select(0);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
