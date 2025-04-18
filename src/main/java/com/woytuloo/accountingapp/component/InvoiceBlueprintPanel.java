package com.woytuloo.accountingapp.component;

import com.woytuloo.accountingapp.InvoiceManagement.Invoice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class InvoiceBlueprintPanel extends JButton {
    private JLabel nameLabel;
    private JButton closeButton;
    private JLabel iconLabel;
    private Invoice invoice;

    public InvoiceBlueprintPanel(Invoice invoice) {
        this.invoice = invoice;
        setLayout(null);
        setPreferredSize(new Dimension(210, 340));
        setBorder(null);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);

        ImageIcon iconInvoice = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/InvoiceIcon.png")));
        Image imgInvoice = iconInvoice.getImage().getScaledInstance(92, 92, Image.SCALE_SMOOTH);
        iconLabel = new JLabel(new ImageIcon(imgInvoice));
        iconLabel.setBounds(55, 80, 100, 100);
        add(iconLabel);

        nameLabel = new JLabel(invoice.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setFont(new Font("Montserrat", Font.PLAIN, 16));
        nameLabel.setBounds(20, 200, 170, 30);
        add(nameLabel);

        closeButton = new JButton();
        closeButton.setBounds(175, 10, 25, 25);
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setText("");

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/closeIcon.png")));
        Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        closeButton.setFocusable(false);
        closeButton.setIcon(new ImageIcon(img));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e != null) {
                    System.out.println("Invoice closed");
                }
            }
        });

        add(closeButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(15, 15, 15));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
    }

    public JButton getRemoveButton() {
        return closeButton;
    }
}
