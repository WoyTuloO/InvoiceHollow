/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author wojte
 */
public class Menu extends JComponent {
    private int autoindex;
    private MenuItem selectedMenuItem;
    private boolean subMenuVisible;
    private MenuEvent event;
    private MigLayout layout;

    private String[][] menuItems = new String [][]{
            {"Panel główny"},
            {"Faktury","Nowa Faktura", "Nowy szablon"},
            {"Archiwum"}
        };
    
    public Menu(){
        subMenuVisible =false;
        init();
    }
    
    private void init(){
        layout = new MigLayout("wrap 1, fillx, gapy 0, inset 2", "fill");
        setLayout(layout);
        setOpaque(true);
        for(int i= 0; i < menuItems.length; i++){
            addMenu(menuItems[i][0],i);
        }   
        
    }
    
    
    private void addMenu(String name, int index){        
        MenuItem item = new MenuItem(name, index, menuItems[index].length > 1);

        item.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (menuItems[index].length > 1) {
                        handleMultiItemMenu(item, index);
                    } else {
                        handleSingleItemMenu(item, index);
                    }
                }
                
                
        });
        
        add(item);
        revalidate();

    }
    
    
    private void handleMultiItemMenu(MenuItem item, int index) {
        if (!item.isSelected()) {
            unclickButtons();
            item.setSelected(true);

            for(Component c : getComponents())
                if(c instanceof JPanel && c.getName() != null && c.getName().equals(index + "")){
                    System.out.println(c.getName());
                    return;
                }
            addSubMenu(item, index, menuItems[index].length, getComponentZOrder(item));
        } else {
            item.setSelected(false);
            hideSubMenu(item, index);
        }
    }

    private void handleSingleItemMenu(MenuItem item, int index) {
        if (!item.isSelected()) {
            unclickButtons();
            item.setSelected(true);
        }

        if (getEvent() != null) {
            getEvent().selected(index, 0);
        }
    }

    
    
    private void unclickButtons(){
        for(Component c : getComponents()){
            if(c instanceof MenuItem){
                MenuItem item = (MenuItem)c;
                if(item.getIndex() == 1 && subMenuVisible) 
                    continue;
                item.setSelected(false);
            }else if(c instanceof JPanel){
                for(Component c2: ((JPanel) c).getComponents()){
                    if(c2 instanceof MenuItem){
                        ((MenuItem) c2).setSelected(false);
                    }
                }
            }
        }
    }
    
    

    private void addSubMenu(MenuItem item, int index, int len, int zOrder){
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, gapy 0, inset 0", "fill"));
        p.setOpaque(false);
        p.setName(index + "");
        
        for(int i = 1; i< len; i++){
            MenuItem sub = new MenuItem(menuItems[index][i],i,false);
            sub.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    if (!sub.isSelected()) {
                            unclickButtons();
                            sub.setSelected(true);
                        }

                    if(getEvent()!=null){
                        getEvent().selected(index, sub.getIndex());
                    }
                }
            });
            sub.initSubMenu(i, len);
            p.add(sub);
        }
        add(p, zOrder+1);
        revalidate();
        subMenuVisible = true;
    }

    private void hideSubMenu(MenuItem item, int index){
        for(Component c : getComponents()){
            if(c instanceof JPanel && c.getName() != null && c.getName().equals(index + "")){
                remove(c);
                break;          
            }
        }
        revalidate();
        subMenuVisible = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(60,70,150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
    }

    /**
     * @return the event
     */
    public MenuEvent getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(MenuEvent event) {
        this.event = event;
    }
    
    
    
    
    
    
}
