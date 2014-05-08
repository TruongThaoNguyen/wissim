package wissim.controller.filters;
/**
 * Observer nhan cac thong bao khi mot doi tuong IFilter lien quan update
 * 
 * @author Tien
 * 
 */
public interface IFilterObserver {
	/*Thong bao duoc tao ra boi Observer khi doi tuong IFiler lien quan toi no update*/
	void filterUpdated(IFilter obs);
}
