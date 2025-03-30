package com.fortisbank.ui;

import com.fortisbank.business.services.BankManagerService;
import com.fortisbank.business.services.CustomerService;
import com.fortisbank.business.services.LoginService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.forms.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                /**
                 * The idea here is to pass the storage mode to the LoginFrame constructor
                 * and then use it to initialize the LoginService instance.
                 *
                 * after login, the storage mode will be passed to the DashboardFrame constructor
                 * and  from there the other services needed by the application will be initialized and passed to the components.
                 */

                //Define the storage mode for the application
                StorageMode storageMode = StorageMode.FILE;
                // LoginFrame is the entry point of the application
                new LoginFrame(storageMode).setVisible(true);
            }
        });
    }

    // method to create initial data to allow login testing
    private void initData(){
        var managerService = BankManagerService.getInstance(StorageMode.FILE);
        var customerService = CustomerService.getInstance(StorageMode.FILE);

        // Create a test manager
        managerService.
    }
}

