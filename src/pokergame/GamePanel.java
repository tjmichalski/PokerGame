/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JTextField;

/**
 *
 * @author tylar
 */
public class GamePanel extends javax.swing.JPanel {
    
    //Used for field validation
    //List instead of arrays for simple .contains() use
    private final ArrayList<Character> suits =  new ArrayList(Arrays.asList("H", "D", "C", "S"));
    private final ArrayList<Character> ranks =  new ArrayList(Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"));
    
    //having input fields in a list makes for easy UI resetting on turns
    private final ArrayList<JTextField> inputs;
    
    //lists contain 5 cards for each players
    private ArrayList<Card> blackCards, whiteCards;
    
    //saves high card of each hand for equal endgame decisions
    private Card blackHighcard, whiteHighcard;
    
    //string variables indicating what kind of endgame each player has (straight flush, 4 of a kind,2 pairs, etc.)
    private String blackHand, whiteHand;
    
    //blackHand and whiteHand variables mirrored as integer ranks for each engame
    //necessary to have both forms (int and string) for easy comparison (int) AND easy system.outs (string)
    private int blackHandRank, whiteHandRank;
    
    
    
    public GamePanel() {
        initComponents();
        this.inputs = new ArrayList(Arrays.asList(WC1, WC2, WC3, WC4, WC5, BC1, BC2, BC3, BC4, BC5));
        //no need to edit history diaply area
        this.GameHistoryArea.setEditable(false);
    }

    //checks if all inputs are valid for game
    private boolean inputClear(){
        
        //loop through all input fields
        for(int x=0; x<inputs.size(); x++){
            
            //if input is blank or not formatted, return false
            if(inputs.get(x).getText().isBlank()){
                return false; 
            }
            else if(!correctlyFormatted(inputs.get(x))){
                return false;
            }
        }
        //return true if passed validation
        return true;
    }
    
    //checks suits and ranks arraylist for field input validations
    private boolean correctlyFormatted(JTextField textField){        
        return suits.contains(textField.getText().charAt(1) + "") || ranks.contains(textField.getText().charAt(0) + ""); 
        
    }
    
    //create player hand lists from inputs
    private void createHands(){
        whiteCards = new ArrayList();
        blackCards = new ArrayList();
        
        //loop through input fields and create a card for each input
        //first 5 inputs in list are whites, last 5 are black's
        for(int x=0; x<5; x++){
            whiteCards.add(new Card(inputs.get(x).getText().charAt(0)  + "", inputs.get(x).getText().charAt(1)));
        }
        
        for(int x=5; x< 10; x++){
            blackCards.add(new Card(inputs.get(x).getText().charAt(0)  + "", inputs.get(x).getText().charAt(1)));
        }
        
        //sorting is essential for efficiency of various searches
        Collections.sort(whiteCards, new CustomComparator());
        Collections.sort(blackCards, new CustomComparator());
    }
    
    //a bit long and tedious in favor of more in depth game history results
    private void evaluateHands(){
        //game variables is what is displayed to user upon calculation
        String game = "";
        
        //if both have only high cards, need case to find backup highcard incase of tie
        if(whiteHandRank == 0 && blackHandRank ==0){
            //if whites high card > black's
            if(whiteHighcard.getValue() > blackHighcard.getValue()){
                game += "High card " + whiteHighcard.getRank() + " beats " + blackHighcard.getRank() + ".\n";
                game += "White has Won.\n";
            }
            //if blacks high card > white's
            else if(blackHighcard.getValue() > whiteHighcard.getValue()){
                game += "High card " + blackHighcard.getRank() + " beats " + whiteHighcard.getRank() + ".\n";
                game += "Black has Won.\n";
            }
            //case for matching high cards
            else if(blackHighcard.getValue() == whiteHighcard.getValue()){
                game += "Both players have the same high card of " + whiteHighcard.getRank() + ".\n";
                
                //loop through both hands until one wins in backup high card
                boolean winner = false;
                for(int x=4; x>=0 && winner == false; x--){
                    if(whiteCards.get(x).getValue() > blackCards.get(x).getValue()){
                        game += "Backup highcard " + whiteCards.get(x).getRank() + " beats " + blackCards.get(x).getRank() + ".\n";
                        game += "White has Won.\n";
                        winner = true;
                    }
                    else if(whiteCards.get(x).getValue() < blackCards.get(x).getValue()){
                        game += "Backup highcard " + blackCards.get(x).getRank() + " beats " + whiteCards.get(x).getRank() + ".\n";
                        game += "Black has Won.\n";
                        winner = true;
                    }
                }
                //if no high card winner found - itie
                if(!winner){
                    game += "All following ranks are symmetrical, this game is a draw.\n";
                }
            }
        }
        //if both have a single pair, must have case to find  backup highcard incase of tie
        else if(whiteHandRank == 1 && blackHandRank == 1){
            game += "Both players have a " + whiteHand + ".\n";
            
            //if white has highcard of pair
            if(whiteHighcard.getValue() > blackHighcard.getValue()){
                game += "High card " + whiteHighcard.getRank() + " beats " + blackHighcard.getRank() + ".\n";
                game += "White has Won.\n";
            }
            //if black has highcard of pair
            else if(whiteHighcard.getValue() < blackHighcard.getValue()){
                game += "High card " + blackHighcard.getRank() + " beats " + whiteHighcard.getRank() + ".\n";
                game += "Black has Won.\n";
            }
            //if pairs are equal in value
            else{
                game += "Both players have a high card of " + whiteHighcard.getRank() + ".\n";
                
                //loop through both hands until one wins in backup high card
                boolean winner = false;
                for(int x=4; x>=0 && winner == false; x--){
                    if(whiteCards.get(x).getValue() > blackCards.get(x).getValue()){
                        game += "Remaining high card " + whiteCards.get(x).getRank() + " beats " +blackCards.get(x).getRank();
                        game += "White has Won.";
                        winner = true;
                    }
                    else if(whiteCards.get(x).getValue() < blackCards.get(x).getValue()){
                        game += "Remaining high card " + blackCards.get(x).getRank() + " beats " +whiteCards.get(x).getRank();
                        game += "Black has Won.";
                        winner = true;
                    }
                }
                //neither has a high card, draw
                if(!winner){
                    game += "Remaining high cards all match.\n";
                    game += "This game is a draw.\n";
                }
            }
            
            
        }
        //incase of double pairs for both hands and equal highcard, need to have case to find backup highcard(s)
        else if(whiteHandRank == 2 && blackHandRank == 2){
            game += "Both players have " + whiteHand + ".\n";
            
            //if white has highcard
            if(whiteHighcard.getValue() > blackHighcard.getValue()){
                game += "High card " + whiteHighcard.getRank() + " beats " + blackHighcard.getRank() + ".\n";
                game+= "White has Won.\n";
            }
            //if black has highcard
            else if(whiteHighcard.getValue() < blackHighcard.getValue()){
                game += "High card " + blackHighcard.getRank() + " beats " + whiteHighcard.getRank() + ".\n";
                game+= "Black has Won.\n";
            }
            //if highcards are equal
            else{
                game += "Both players also have a highcard of " + whiteHighcard + ".\n";
                
                //get the lower of the two pair's value for each player
                Card whiteBackup = getBackupPair(whiteCards, whiteHighcard.getValue());
                Card blackBackup = getBackupPair(blackCards, blackHighcard.getValue());
                
                //if white has higher secondary pair
                if(whiteBackup.getValue() > blackBackup.getValue()){
                    game += "Second pair high card " + whiteBackup.getRank() + " beats " + blackBackup.getRank() + "\n";
                    game += "White has Won.\n";
                }
                //if black has higher secondary pair
                else if(whiteBackup.getValue() < blackBackup.getValue()){
                    game += "Second pair high card " + blackBackup.getRank() + " beats " + whiteBackup.getRank() + "\n";
                    game += "Black has Won.\n";
                }
                //if backup pairs are of equal value
                else{
                    game += "Both players also have a matching second pair rank of " + whiteBackup.getRank() + ".\n";
                    
                    //card variables for computing higher remaining value
                    Card whiteRemaining = null, blackRemaining = null;
                    
                    //find that card that is not in either of the hands pairs
                    for(int x=0; x<5; x++){
                        if(whiteCards.get(x).getValue() != whiteBackup.getValue() && whiteCards.get(x).getValue() != whiteHighcard.getValue()){
                            whiteRemaining = whiteCards.get(x);
                        }
                    }
                    for(int x=0; x<5; x++){
                        if(blackCards.get(x).getValue() != blackBackup.getValue() && blackCards.get(x).getValue() != blackHighcard.getValue()){
                            blackRemaining = blackCards.get(x);
                        }
                    }
                    
                    //possibly deferencing null warnings, but such case is not possible due to field validation and evaluateHands() function order
                    
                    //if white has higher remaining card
                    if(whiteRemaining.getValue() > blackRemaining.getValue()){
                        game += "Remaining high card " + whiteRemaining + " beats " + blackRemaining + ".\n" + "White has Won.\n";
                    }
                    //if black has higher remaining card
                    else if(whiteRemaining.getValue() < blackRemaining.getValue()){
                        game += "Remaining high card " + blackRemaining + " beats " + whiteRemaining + ".\n" + "Black has Won.\n";
                    }
                    //remaining card is also equal, draw
                    else{
                        game += "Remaining high card " + blackRemaining + " also ties " + whiteRemaining + ".\n" + "This game is a draw.\n";
                    }
                } 
            }
        }
        //if both have flush, need case to evaluate high card rules in case of first highcard tie
        else if(whiteHandRank == 5 && blackHandRank == 5){
                game += "Both players have " + whiteHand + ".\n";
                
                //white has high card
                if(whiteHighcard.getValue() > blackHighcard.getValue()){
                    game += "High card " + whiteHighcard.getRank() + " beats " + blackHighcard.getRank() + ".\n";
                    game+= "White has Won.\n";
                }
                //black has high card
                else if(whiteHighcard.getValue() < blackHighcard.getValue()){
                    game += "High card " + blackHighcard.getRank() + " beats " + whiteHighcard.getRank() + ".\n";
                    game+= "Black has Won.\n";
                }
                //equal high cards 
                else{
                    //loop through both hands until one wins in backup high card
                    boolean winner = false;
                    for(int x=4; x>=0 && winner == false; x--){
                        if(whiteCards.get(x).getValue() > blackCards.get(x).getValue()){
                            game += "Backup highcard " + whiteCards.get(x).getRank() + " beats " + blackCards.get(x).getRank() + ".\n";
                            game += "White has Won.\n";
                            winner = true;
                        }
                        else if(whiteCards.get(x).getValue() < blackCards.get(x).getValue()){
                            game += "Backup highcard " + blackCards.get(x).getRank() + " beats " + whiteCards.get(x).getRank() + ".\n";
                            game += "Black has Won.\n";
                            winner = true;
                        }
                    }
                    //if no high card winner found - itie
                    if(!winner){
                        game += "All following ranks are symmetrical, this game is a draw.\n";
                    }
                }
        }
        
        //white hand beats black hand
        else if(whiteHandRank > blackHandRank){
            game += whiteHand + " beats " + blackHand + "\n" + "White has Won.\n";
        }
        //black hand beats white hand
        else if(blackHandRank > whiteHandRank){
            game += blackHand + " beats " + whiteHand + "\n" + "Black has Won.\n";
        }
        else if(whiteHighcard.getValue() == blackHighcard.getValue()){
            game += "Both players have a " + whiteHand + "\n";
            if(whiteHighcard.getValue() > blackHighcard.getValue()){
                game += "High card " + whiteHighcard.getRank() + " beats " + blackHighcard.getRank() + ".\n";
                game += "White has Won.\n";
            }
            else if(blackHighcard.getValue() > whiteHighcard.getValue()){
                game += "High card " + blackHighcard.getRank() + " beats " + whiteHighcard.getRank() + ".\n";
                game += "Black has Won.\n";
            }
            
            
            //three of a kind, full house, four of a kind all can not possibly have matching highcards, thus never reach here
            //straight and straight flush will always be = according to rules of High Card if their max values are equal, so draw
            else if(blackHighcard.getValue() == whiteHighcard.getValue()){
                game += "Both players have the same high card of " + whiteHighcard.getRank() + ".\n";
                game += "This game is a draw.\n";
            }
        }
        //set display text to results
        GameHistoryArea.setText(game);
    }
    
    //whiteHand and whiteHandRank values for white according to white's hand
    private void findWhiteHand(){
        //if white has flush
        if(flush(whiteCards, true)){
            //if white also has a straight - straight flush endgame
            if(straight(whiteCards, true)){
                whiteHandRank = 8;
                whiteHand = "Straight Flush";
            }
            //else only has a flush
            else{
                whiteHandRank = 5;
                whiteHand = "Flush";
            }
        }
        //if white has straight
        //already checked for flush, hence no need to check for straight flush again
        else if(straight(whiteCards, true)){
            whiteHandRank = 4;
            whiteHand = "Straight";
        }
        //if white has four of a kind
        else if(fourOfKind(whiteCards, true)){
            whiteHandRank = 7;
            whiteHand = "Four of a Kind";
        }
        //if white has three of a kind
        else if(threeOfKind(whiteCards, true)){
            //if has 3 of kind and backup pair, then is full house
            if(backupPair(whiteCards, whiteHighcard.getValue())){
                whiteHandRank = 6;
                whiteHand = "Full House";
            }
            //else only has 3 of kind
            else{
                whiteHandRank = 3;
                whiteHand = "Three of a Kind";
            }
        }
        //if has a pair
        else if(pair(whiteCards, true)){
            //if has 2 sets of pairs
            if(backupPair(whiteCards, whiteHighcard.getValue())){
                whiteHandRank = 2;
                whiteHand = "Two Pairs";
            }
            //else only has 1 set of pairs
            else{
                whiteHandRank = 1;
                whiteHand = "Pair";
            }
        }
        //else hand is ranked by high card only
        else{
            whiteHandRank = 0;
            whiteHighcard = whiteCards.get(4);
            whiteHand = "High Card";
        }
    }
    
    //same exact function as findWhiteHand, just with black's variables
    private void findBlackHand(){
        if(flush(blackCards, false)){
            if(straight(blackCards, false)){
                blackHandRank = 8;
                blackHand = "Straight Flush";
            }
            else{
                blackHandRank = 5;
                blackHand = "Flush";
            }
        }
        else if(straight(blackCards, false)){
            blackHandRank = 4;
            blackHand = "Straight";
        }
        else if(fourOfKind(blackCards, false)){
            blackHandRank = 7;
            blackHand = "Four of a Kind";
        }
        else if(threeOfKind(blackCards, false)){
            if(backupPair(blackCards, blackHighcard.getValue())){
                blackHandRank = 6;
                blackHand = "Full House";
            }
            else{
                blackHandRank = 3;
                blackHand = "Three of a Kind";
            }
        }
        else if(pair(blackCards, false)){
            if(backupPair(blackCards, blackHighcard.getValue())){
                blackHandRank = 2;
                blackHand = "Two Pairs";
            }
            else{
                blackHandRank = 1;
                blackHand = "Pair";
            }
        }
        else{
            blackHandRank = 0;
            blackHighcard = blackCards.get(4);
            blackHand = "High Card";
        }
    }
    
    //true if received list contains a pair
    //set high card accordingly to isWhiteboolean if true
    private boolean pair(ArrayList<Card> cards, boolean isWhite){
        //start at highest index so always finds highest value pair (in case of 2 pairs)
        for(int x=4; x>0; x--){
            if((cards.get(x).getValue() == cards.get(x-1).getValue())){
                if(isWhite){
                    whiteHighcard = cards.get(x);
                }
                else{
                    blackHighcard = cards.get(x);
                }
                
                return true;
            }
        }

        return false;
    }
    
    //return true if there exists a pair in the list that does not equal the recieved value
    //used to find if secondary pair exists in case of a higher value pair already having been found OR a triplet having already been found
    private boolean backupPair(ArrayList<Card> cards, int takenValue){
        for(int x=1; x<5; x++){
            if((cards.get(x).getValue() == cards.get(x-1).getValue()) && cards.get(x).getValue() != takenValue){
                return true;
            }
        }

        return false;
    }
    
    //same as above, simply returns one of the cards if the secondary pair exists
    private Card getBackupPair(ArrayList<Card> cards, int takenValue){
        for(int x=1; x<5; x++){
            if((cards.get(x).getValue() == cards.get(x-1).getValue()) && cards.get(x).getValue() != takenValue){
                return cards.get(x);
            }
        }

        return null;
    }
    
    //true if three of a kind exist in received list
    //sets high card accordingly to boolean received if true
    private boolean threeOfKind(ArrayList<Card> cards, boolean isWhite){
        int counter = 1;
        for(int x=1; x<5; x++){
            if(cards.get(x).getValue() == cards.get(x-1).getValue() && cards.get(x).getValue() == cards.get(2).getValue()){
                counter++;
                if(counter == 3){
                    if(isWhite){
                        whiteHighcard = cards.get(x);
                    }
                    else{
                        blackHighcard = cards.get(x);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    //true if four of a kind exist in received list
    //sets high card accordingly to boolean received if true
    private boolean fourOfKind(ArrayList<Card> cards, boolean isWhite){
        int counter = 1;
        for(int x=1; x<5; x++){
            if(cards.get(x).getValue() == cards.get(x-1).getValue() && cards.get(x).getValue() == cards.get(2).getValue()){
                counter++;
                if(counter == 4){
                    if(isWhite){
                        whiteHighcard = cards.get(x);
                    }
                    else{
                        blackHighcard = cards.get(x);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    //true if received list contains a flush
    //sets high card accordingly to boolean received if true
    private boolean flush(ArrayList<Card> cards, boolean isWhite){     
        //loop through all cards checking if any non matching suit pairs
        for(int x=1; x<5; x++){
            if(!cards.get(x).getSuit().equals(cards.get(x-1).getSuit())){
                return false;
            }
        }
        
        if(isWhite){
            whiteHighcard = cards.get(4);
        }
        else if (!isWhite){
            blackHighcard = cards.get(4);
        }
        return true;
    }
    
    //true if received list contains a straight
    //sets high card accordingly to boolean received if true
    private boolean straight(ArrayList<Card> cards, boolean isWhite){
        //loop through all cards checking if any non matching suit pairs
        for(int x=1; x<5; x++){
            if(cards.get(x).getValue()-cards.get(x-1).getValue() != 1){
                return false;
            }
        }
        
        if(isWhite){
            whiteHighcard = cards.get(4);
        }
        else{
            blackHighcard = cards.get(4);
        }
        return true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GamePanel = new javax.swing.JPanel();
        MiddlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        GamesHistory = new javax.swing.JScrollPane();
        GameHistoryArea = new javax.swing.JTextArea();
        SubmitButton = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ErrorText = new javax.swing.JLabel();
        NextGameInstructions = new javax.swing.JLabel();
        PlayerPanel = new javax.swing.JPanel();
        WhitePanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        WC1 = new javax.swing.JTextField();
        WC2 = new javax.swing.JTextField();
        WC3 = new javax.swing.JTextField();
        WC4 = new javax.swing.JTextField();
        WC5 = new javax.swing.JTextField();
        WhitePanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        BC1 = new javax.swing.JTextField();
        BC2 = new javax.swing.JTextField();
        BC3 = new javax.swing.JTextField();
        BC4 = new javax.swing.JTextField();
        BC5 = new javax.swing.JTextField();

        GamePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        MiddlePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Games History");

        GamesHistory.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        GameHistoryArea.setColumns(20);
        GameHistoryArea.setRows(5);
        GamesHistory.setViewportView(GameHistoryArea);

        SubmitButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SubmitButton.setText("Submit Hands");
        SubmitButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        SubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Instructions:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("For each player, enter 5 cards that they are 'holding'.");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Then, hit submit to compare hands to see who wins.");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Key:");

        jLabel6.setText("Hearts: H");

        jLabel7.setText("Diamonds: D");

        jLabel8.setText("Clubs: C");

        jLabel9.setText("Spades: S");

        jLabel10.setText("Ace of Hearts: AH");

        jLabel11.setText("King of Diamonds: KD");

        jLabel12.setText("Queen of Clubs: QC");

        jLabel13.setText("Jack of Spades: JS");

        jLabel14.setText("10 of Hearts: TH");

        javax.swing.GroupLayout MiddlePanelLayout = new javax.swing.GroupLayout(MiddlePanel);
        MiddlePanel.setLayout(MiddlePanelLayout);
        MiddlePanelLayout.setHorizontalGroup(
            MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MiddlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(NextGameInstructions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ErrorText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GamesHistory, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SubmitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(MiddlePanelLayout.createSequentialGroup()
                        .addGroup(MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(15, 15, 15)))
                .addGap(6, 6, 6))
        );
        MiddlePanelLayout.setVerticalGroup(
            MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MiddlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GamesHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SubmitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NextGameInstructions, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ErrorText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10))
                .addGroup(MiddlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MiddlePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9))
                    .addGroup(MiddlePanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        WhitePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setText("Card 1:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("White Hand");

        jLabel17.setText("Card 2:");

        jLabel18.setText("Card 4:");

        jLabel19.setText("Card 3:");

        jLabel20.setText("Card 5:");

        WC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WC2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WhitePanelLayout = new javax.swing.GroupLayout(WhitePanel);
        WhitePanel.setLayout(WhitePanelLayout);
        WhitePanelLayout.setHorizontalGroup(
            WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WhitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(WhitePanelLayout.createSequentialGroup()
                        .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(WhitePanelLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(WC1))
                            .addGroup(WhitePanelLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(WC5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanelLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(WC4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(WC3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanelLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(WC2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        WhitePanelLayout.setVerticalGroup(
            WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WhitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(WC1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(WC2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(WC3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(WC4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(WC5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        WhitePanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel21.setText("Card 1:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Black Hand");

        jLabel23.setText("Card 2:");

        jLabel24.setText("Card 4:");

        jLabel25.setText("Card 3:");

        jLabel26.setText("Card 5:");

        BC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BC2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WhitePanel1Layout = new javax.swing.GroupLayout(WhitePanel1);
        WhitePanel1.setLayout(WhitePanel1Layout);
        WhitePanel1Layout.setHorizontalGroup(
            WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WhitePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(WhitePanel1Layout.createSequentialGroup()
                        .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(WhitePanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BC1))
                            .addGroup(WhitePanel1Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BC5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanel1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BC4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanel1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BC3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(WhitePanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BC2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 118, Short.MAX_VALUE)))
                .addContainerGap())
        );
        WhitePanel1Layout.setVerticalGroup(
            WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WhitePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(BC1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(BC2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(BC3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(BC4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WhitePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(BC5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PlayerPanelLayout = new javax.swing.GroupLayout(PlayerPanel);
        PlayerPanel.setLayout(PlayerPanelLayout);
        PlayerPanelLayout.setHorizontalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WhitePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(WhitePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PlayerPanelLayout.setVerticalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayerPanelLayout.createSequentialGroup()
                .addComponent(WhitePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(WhitePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout GamePanelLayout = new javax.swing.GroupLayout(GamePanel);
        GamePanel.setLayout(GamePanelLayout);
        GamePanelLayout.setHorizontalGroup(
            GamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GamePanelLayout.createSequentialGroup()
                .addComponent(PlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(MiddlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        GamePanelLayout.setVerticalGroup(
            GamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(GamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(MiddlePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PlayerPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void WC2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WC2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_WC2ActionPerformed

    private void BC2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BC2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BC2ActionPerformed

    private void SubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitButtonActionPerformed
        //simple if else to allow multifunction button
        if(SubmitButton.getText().equals("Submit Hands")){
            //check if inputs are valid before doing anything
            if(inputClear()){
                //lock in all inputs
                for(int x=0; x<10; x++){
                    inputs.get(x).setEditable(false);
                }
                
                //4 functions to compute game
                createHands();
                findWhiteHand();
                findBlackHand();
                evaluateHands();
                
                //set variables for new game info text
                SubmitButton.setText("Next Game");
                NextGameInstructions.setText("Click next game to clear submissions and play again.");
            }
            //set error text if inputs invalid
            else{
                ErrorText.setText("* Please Enter Valid Entries.");
            }
        }
        //reset game panel for new game
        else{
            GameHistoryArea.setText("");
            SubmitButton.setText("Submit Hands");
            //reenable editing of fields and clear them all for new game
            for(int x=0; x<10; x++){
                inputs.get(x).setEditable(true);
                inputs.get(x).setText("");
            }
        }
    }//GEN-LAST:event_SubmitButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BC1;
    private javax.swing.JTextField BC2;
    private javax.swing.JTextField BC3;
    private javax.swing.JTextField BC4;
    private javax.swing.JTextField BC5;
    private javax.swing.JLabel ErrorText;
    private javax.swing.JTextArea GameHistoryArea;
    private javax.swing.JPanel GamePanel;
    private javax.swing.JScrollPane GamesHistory;
    private javax.swing.JPanel MiddlePanel;
    private javax.swing.JLabel NextGameInstructions;
    private javax.swing.JPanel PlayerPanel;
    private javax.swing.JToggleButton SubmitButton;
    private javax.swing.JTextField WC1;
    private javax.swing.JTextField WC2;
    private javax.swing.JTextField WC3;
    private javax.swing.JTextField WC4;
    private javax.swing.JTextField WC5;
    private javax.swing.JPanel WhitePanel;
    private javax.swing.JPanel WhitePanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
