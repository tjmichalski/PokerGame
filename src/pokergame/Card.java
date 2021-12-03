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
public class Card {
    
    private final String rank;
    private final String suit;
    
    public Card(String rank, Character suit){
        this.rank = rank+ "";
        this.suit = suit + "";
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }
    
    public int getValue(){
        try{
            return Integer.parseInt(rank);
        }catch(NumberFormatException e){
            return switch (rank) {
                case "A" -> 14;
                case "K" -> 13;
                case "Q" -> 12;
                case "J" -> 11;
                case "T" -> 10;
                default -> -1;
            };
        }
    }
             
    @Override
    public String toString(){
        return this.rank + " " + this.suit;
    }
        

}
