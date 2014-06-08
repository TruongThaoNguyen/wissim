package wissim.controller.filters.gui;

public enum AutoChoices {
	/** No auto choices, any choices must be explicitly inserted. */
    DISABLED,

    /** Enumerations and booleans automatically handled. */
    ENUMS,

    /**
     * Choices extracted from the model, it is guaranteed that the choices
     * include all the model's values, and only those.
     */
    ENABLED

}
