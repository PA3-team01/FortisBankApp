package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.forms.SendMessageForm;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class SupportContactPanel extends JPanel {

    private StorageMode storageMode;

    public SupportContactPanel(StorageMode storageMode) {
        this.storageMode = storageMode;
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
        messageFormWrapper.add(new SendMessageForm(storageMode), BorderLayout.CENTER);  // Use the existing SendMessageForm class here

        // === Combine Layout ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(messageFormWrapper);

        add(centerPanel, BorderLayout.CENTER);
    }
}
