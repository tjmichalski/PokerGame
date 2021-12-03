/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergame;

import javax.swing.JFrame;

/**
 *
 * @author tylar
 */
public class MainFrame extends JFrame{
    
    private GamePanel gamePanel;
    
    public MainFrame(){
        this.setTitle("Chess");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        gamePanel = new GamePanel();
        this.add(gamePanel); 
        
        this.setLocation(200, 50);
        this.pack();
        this.setVisible(true);
    }
    
}
