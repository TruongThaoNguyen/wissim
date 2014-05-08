package wissim.controller.filters.gui.editor;

interface IChoicesParser {

	/**
     * Escapes a given expression, such that, when parsed, the parser will make
     * no character/operator substitutions.
     */
    String escapeChoice(String s);
}
