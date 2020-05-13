package com.unitn.disi.lpsmt.racer.ui.component.dialog;

/**
 * Listener used in Dialog class fragments when one or more has been selected
 *
 * @param <T> Type of the selected Dialog data parameter
 * @author Carlo Corradini
 */
public interface OnDialogSelectionInterface<T> {
    /**
     * Called when positive button has been clicked with the selected T data parameter
     *
     * @param result The selected data parameter
     */
    void onDialogSelection(T result);
}
