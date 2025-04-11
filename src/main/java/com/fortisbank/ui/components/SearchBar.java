package com.fortisbank.ui.components;

import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A generic SearchBar component with a text field and a search button.
 * Allows integration with custom search logic through a callback.
 */
public class SearchBar extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(SearchBar.class.getName());

    private JTextField searchField;
    private JButton searchButton;

    /**
     * Constructs a SearchBar with a callback for handling search actions.
     *
     * @param placeholderText the placeholder text for the search field
     * @param searchListener  the callback to handle search actions
     */
    public SearchBar(String placeholderText, SearchListener searchListener) {
        try {
            setLayout(new BorderLayout(5, 5));
            StyleUtils.styleFormPanel(this);

            // Create and style the search field
            searchField = new JTextField();
            StyleUtils.styleTextField(searchField);
            searchField.setToolTipText(placeholderText);

            // Create and style the search button
            searchButton = new JButton("Search");
            StyleUtils.styleButton(searchButton, true);

            // Add action listener to the search button
            ActionListener searchAction = e -> handleSearch(searchListener);
            searchButton.addActionListener(searchAction);
            searchField.addActionListener(searchAction); // Trigger search on Enter key

            // Add components to the panel
            add(searchField, BorderLayout.CENTER);
            add(searchButton, BorderLayout.EAST);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing SearchBar: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to initialize search bar: " + e.getMessage());
        }
    }

    /**
     * Handles the search action by validating input and invoking the search listener.
     *
     * @param searchListener the callback to handle search actions
     */
    private void handleSearch(SearchListener searchListener) {
        try {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                searchListener.onSearch(query);
            } else {
                StyleUtils.showStyledWarningDialog(this, "Search query cannot be empty.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during search action: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "An error occurred while performing the search: " + e.getMessage());
        }
    }

    /**
     * Clears the search field.
     */
    public void clearSearchField() {
        try {
            searchField.setText("");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error clearing search field: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to clear the search field: " + e.getMessage());
        }
    }

    /**
     * Interface for handling search actions.
     */
    public interface SearchListener {
        void onSearch(String query);
    }
}