package wissim.object.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FloatRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1256049241521137237L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
//		System.err.println("-----"+value.toString());
		
		value = value.toString();
		this.setHorizontalAlignment(RIGHT);
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
