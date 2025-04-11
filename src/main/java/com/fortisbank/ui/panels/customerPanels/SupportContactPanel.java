package com.fortisbank.ui.panels.customerPanels;

    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.ui.forms.SendMessageForm;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The SupportContactPanel class represents the support contact panel of the Fortis Bank application.
     * It extends JPanel and provides a user interface to display contact information and a form to send messages to support.
     */
    public class SupportContactPanel extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(SupportContactPanel.class.getName());
        private StorageMode storageMode;

        /**
         * Constructs a SupportContactPanel with the specified storage mode.
         *
         * @param storageMode the storage mode to use for services
         */
        public SupportContactPanel(StorageMode storageMode) {
            this.storageMode = storageMode;

            try {
                setLayout(new BorderLayout());
                StyleUtils.styleFormPanel(this);

                // === Contact Info Section ===
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);

                JLabel title = new JLabel("Contact Fortis Bank Support");
                StyleUtils.styleFormTitle(title);

                JLabel address = new JLabel("📍 123 Finance Ave, Suite 456, Montreal, QC H3Z 2Y7");
                JLabel phone = new JLabel("📞 +1 (800) 123-4567");
                JLabel email = new JLabel("✉️ support@fortisbank.fake");
                JLabel hours = new JLabel("🕘 Monday - Friday: 9:00 AM to 6:00 PM");

                StyleUtils.styleLabel(address);
                StyleUtils.styleLabel(phone);
                StyleUtils.styleLabel(email);
                StyleUtils.styleLabel(hours);

                infoPanel.add(title);
                infoPanel.add(Box.createVerticalStrut(10));
                infoPanel.add(address);
                infoPanel.add(phone);
                infoPanel.add(email);
                infoPanel.add(hours);

                // === Message Form ===
                JPanel messageFormWrapper = new JPanel();
                messageFormWrapper.setLayout(new BorderLayout());
                messageFormWrapper.setOpaque(false);
                messageFormWrapper.add(Box.createVerticalStrut(20), BorderLayout.NORTH);

                try {
                    messageFormWrapper.add(new SendMessageForm(storageMode), BorderLayout.CENTER); // Use the existing SendMessageForm class here
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error initializing SendMessageForm: {0}", ex.getMessage());
                    StyleUtils.showStyledErrorDialog(this, "Failed to load the message form: " + ex.getMessage());
                }

                // === Combine Layout ===
                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
                centerPanel.setOpaque(false);
                centerPanel.add(infoPanel);
                centerPanel.add(Box.createVerticalStrut(20));
                centerPanel.add(messageFormWrapper);

                add(centerPanel, BorderLayout.CENTER);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing SupportContactPanel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the support contact panel: " + e.getMessage());
            }
        }
    }