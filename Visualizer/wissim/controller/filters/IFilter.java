package wissim.controller.filters;

import javax.swing.RowFilter;

/**
 * Interface cua cac doi tuong filter co the update tu dong
 * Moi thay doi tren filter deu duoc thong bao den observer lien quan
 */
public interface IFilter {
	/*tuong duong RowFilter include*/
	boolean include(RowFilter.Entry rowEntry);
	/* true neu filter duoc enable*/
	boolean isEnabled();
	/*Enable/Disable filter*/
	void setEnabled(boolean enable);
	/*Cai dat mot observer de nhan thong bao khi filter thay doi*/
	void addFilterObserver(IFilterObserver observer);
	/*Huy mot observer */
	void removeFilterObserver(IFilterObserver observer);
}
