package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;
import com.woytuloo.accountingapp.InvoiceManagement.InvoiceBlueprintAdder;
import com.woytuloo.accountingapp.component.InvoiceBlueprintPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class InvoiceBlueprintHandlerTest {

    private JPanel savedBlueprintCard;
    private JPanel displayPanel;
    private InvoiceBlueprintAdder adderMock;
    private HashMap<String, Invoice> collection;

    @BeforeEach
    void setup() {
        savedBlueprintCard = new JPanel();
        displayPanel = new JPanel();
        adderMock = mock(InvoiceBlueprintAdder.class);
        collection = new HashMap<>();
    }

    private void fireComponentShown(JPanel panel) {
        for (var listener : panel.getComponentListeners()) {
            listener.componentShown(new ComponentEvent(panel, ComponentEvent.COMPONENT_SHOWN));
        }
    }

    @Test
    void testReloadBlueprintsWithEmptyCollectionShowsNothing() {
        try (MockedConstruction<InvoiceBlueprintPanel> ignored = mockConstruction(InvoiceBlueprintPanel.class, (mock, context) -> {
            JButton removeButton = new JButton();
            when(mock.getRemoveButton()).thenReturn(removeButton);
        })) {
            InvoiceBlueprintHandler handler = new InvoiceBlueprintHandler(savedBlueprintCard, displayPanel, collection, adderMock);
            fireComponentShown(savedBlueprintCard);
            assertEquals(0, displayPanel.getComponentCount(), "No components should be shown for empty collection");
        }
    }

    @Test
    void testGetInvoiceByNameReturnsNullWhenMissing() {
        InvoiceBlueprintHandler handler = new InvoiceBlueprintHandler(savedBlueprintCard, displayPanel, collection, adderMock);
        assertNull(InvoiceBlueprintHandler.getInvoiceByName("does-not-exist"));
    }

    @Test
    void testGetInvoiceByNameReturnsCorrectInvoice() {
        Invoice testInvoice = new Invoice("TestInvoice", new File("test.pdf"));
        collection.put(testInvoice.getName(), testInvoice);

        InvoiceBlueprintHandler handler = new InvoiceBlueprintHandler(savedBlueprintCard, displayPanel, collection, adderMock);

        assertEquals(testInvoice, InvoiceBlueprintHandler.getInvoiceByName("TestInvoice"));
    }
}
