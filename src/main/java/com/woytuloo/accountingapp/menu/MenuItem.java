/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.woytuloo.accountingapp.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author wojte
 */
public class MenuItem extends JButton {

        /**
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * @return the hasSubMenu
         */
        public boolean isHasSubMenu() {
            return hasSubMenu;
        }

        /**
         * @param hasSubMenu the hasSubMenu to set
         */
        public void setHasSubMenu(boolean hasSubMenu) {
            this.hasSubMenu = hasSubMenu;
        }

        /**
         * @return the subMenuIndex
         */
        public int getSubMenuIndex() {
            return subMenuIndex;
        }

        /**
         * @param subMenuIndex the subMenuIndex to set
         */
        public void setSubMenuIndex(int subMenuIndex) {
            this.subMenuIndex = subMenuIndex;
        }

        /**
         * @return the len
         */
        public int getLen() {
            return len;
        }

        /**
         * @param len the len to set
         */
        public void setLen(int len) {
            this.len = len;
        }

        private final int index;
        private boolean hasSubMenu;
        
        
        private int subMenuIndex;
        private int len;
        
        public MenuItem(String name,int index, boolean hsm){
            super(name);
            this.index = index;
            this.hasSubMenu = hsm;
            
            
            this.setForeground(new Color(230,230,230));
            this.setFont(new Font("Verdana", 1, 14));
            this.setContentAreaFilled(false);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setBorder(new EmptyBorder(12,20,12,10));
            this.setIconTextGap(10);

        
        }    
        
        public void initSubMenu(int subMenuIndex, int len){
            this.setSubMenuIndex(subMenuIndex);
            this.setLen(len);
            this.setFont(new Font("Verdana", 1, 14));
            setOpaque(true);
            this.setBorder(new EmptyBorder(9,33,9,10));
            this.setBackground(new Color(2,2,2));


        }

    @Override
    protected void paintComponent(Graphics g) {
            super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if(len !=0){
                if(subMenuIndex == len - 1){
                    g2.setColor(new Color(100,100,100));
                    g2.drawLine(18, 0, 18, getHeight()/2);
                    g2.drawLine( 18, getHeight()/2 , 26, getHeight()/2);

                }else{
                    g2.setColor(new Color(100,100,100));
                    g2.drawLine(18, 0, 18, getHeight());
                    g2.drawLine( 18, getHeight()/2 , 26, getHeight()/2);

            }        
        }
    
    }
        
        
        
    }
