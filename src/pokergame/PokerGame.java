/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergame;

/**
 *
 * @author tylar
 */
public class PokerGame {

    private MainFrame mainFrame;
    
    public static void main(String[] args) {
        PokerGame pokerGame = new PokerGame();
        pokerGame.mainFrame = new MainFrame();
    }
    
}
