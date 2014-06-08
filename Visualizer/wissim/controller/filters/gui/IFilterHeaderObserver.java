package wissim.controller.filters.gui;

import javax.swing.table.TableColumn;


public interface IFilterHeaderObserver {
	/**
     * <p>Informs the observer than a new filter editor is created</p>
     *
     * @param  header       the associated table filter header
     * @param  editor
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterEditorCreated(TableFilterHeader header,
                                  IFilterEditor     editor,
                                  TableColumn       tableColumn);

    /**
     * <p>Informs the observer than an existing filter editor has been excluded
     * from the filter header</p>
     *
     * @param  header       the associated table filter header
     * @param  editor
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterEditorExcluded(TableFilterHeader header,
                                   IFilterEditor     editor,
                                   TableColumn       tableColumn);

    /**
     * <p>Notification made by the {@link
     * net.coderazzi.filters.gui.IFilterEditor} when the filter's content is
     * updated</p>
     *
     * @param  header       the associated table filter header
     * @param  editor       the observable instance
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterUpdated(TableFilterHeader header,
                            IFilterEditor     editor,
                            TableColumn       tableColumn);
}
