package com.fortisbank.ui;

import com.fortisbank.business.services.RegisterService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.frames.mainFrames.LoginFrame;

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
                // Initialize the data for testing purposes
                // This is just for testing, in a real application this would be done in a setup or migration phase
                initData(storageMode);
                // LoginFrame is the entry point of the application
                new LoginFrame(storageMode).setVisible(true);
            }
        });
    }

    // method to create initial data to allow login testing
    private static void initData(StorageMode storageMode) {
        // Register Service
        var registerService = RegisterService.getInstance(storageMode);
        // Create a test manager
        try {
            registerService.registerNewBankManager("Test","Manager","manager@test.com","Password1".toCharArray(),"1111".toCharArray());
        } catch (Exception e) {
            System.err.println("Failed to create test customer: " + e.getMessage());
        }
        // Create a test customer
        try {
            registerService.registerNewCustomer("Test","User","user@test.com","1234567890","Password1".toCharArray(),"1111".toCharArray());
        } catch (Exception e) {
            System.err.println("Failed to create test customer: " + e.getMessage());
        }

    }
}

