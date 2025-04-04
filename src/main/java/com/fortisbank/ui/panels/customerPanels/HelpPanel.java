package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import javax.swing.text.Style;
import java.awt.*;

public class HelpPanel extends JPanel {

    private StorageMode storageMode;

    public HelpPanel(StorageMode storageMode){
        this.storageMode = storageMode;

        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
        helpPanel.setOpaque(false);

        JLabel title = new JLabel("Fortis Bank Help and Support");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        StyleUtils.styleFormTitle(title);

        JLabel website = new JLabel("For additional help, you can visit our website: www.fortisbank.fake");
        website.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel email = new JLabel("Email us at: support@fortisbank.fake");
        email.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel phone = new JLabel("Call us at: (123) 456-7890");
        phone.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyleUtils.styleLabel(website);
        StyleUtils.styleLabel(email);
        StyleUtils.styleLabel(phone);

        helpPanel.add(title);
        helpPanel.add(Box.createVerticalStrut(15));
        helpPanel.add(website);
        helpPanel.add(email);
        helpPanel.add(phone);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(Box.createVerticalStrut(30));
        center.add(helpPanel);
        center.add(Box.createVerticalGlue());

        add(center, BorderLayout.CENTER);
    }
}
