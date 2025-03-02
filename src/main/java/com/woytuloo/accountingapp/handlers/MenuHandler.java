package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.menu.Menu;
import com.woytuloo.accountingapp.menu.MenuEvent;

import javax.swing.*;
import java.awt.*;

public class MenuHandler {

    private Menu menu;
    private JPanel background1;
    private CardLayout cardLayout;
    private InvoiceBlueprintDisplayHandler invoiceDisplayHandler;
    private StorageHandler storageHandler;

    public MenuHandler(Menu menu, JPanel background, CardLayout cardLayout, InvoiceBlueprintDisplayHandler invoiceDisplayHandler, StorageHandler storageHandler) {
        this.menu = menu;
        this.background1 = background;
        this.cardLayout = cardLayout;
        this.invoiceDisplayHandler = invoiceDisplayHandler;
        this.storageHandler = storageHandler;


        menu.setEvent( new MenuEvent(){
            @Override
            public void selected(int index, int subIndex){
                String indexStr = index + " " + subIndex;
                cardLayout.show(background1,  indexStr);
                setupFocus(indexStr);
                System.out.println(index + " " + subIndex);
            }
        });

    }

    public void setupFocus(String constr){
        switch (constr) {
//            case "0 0" -> reloadDashBoard();
//            case "1 1" -> invoiceDisplayHandler.reloadInvoices();
//            case "1 2" -> reloadInvoiceFileSelection();
            case "2 0" -> storageHandler.reloadArchiveMenu();
//            case "2 1" -> reloadLastInvoices();
//            case "2 2" -> reloadRememberedPhrases();
//            case "2 3" -> reloadSavedInvoiceBlueprints();
        }
    }

}
