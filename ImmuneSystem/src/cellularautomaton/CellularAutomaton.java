/*
A cellular automaton that models the white blood cells in the immune system
and the prevention of the spread of a infection/disease.
BY: JI LIN
*/
package cellularautomaton;

//Imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;


//Start of Cellular Automaton Class
public class CellularAutomaton extends JFrame {

    //Initializing Variables
    Random r = new Random();
    
    int numGenerations = 10000;
    int currGeneration = 1;
    
    Color whiteBloodColor = Color.WHITE;
    Color infectedColor = Color.RED;
    Color immuneColor = Color.GREEN;
    Color vulnerableColor = Color.BLACK;
    Color grid = Color.BLACK;
    
    int width = 800;
    int height = 800;
    int borderWidth = 50;

    int numCellsX = 50;
    int numCellsY = 50;
    int cellWidth = (width - (2*borderWidth))/numCellsX;

    String cells[][] = new String [numCellsY][numCellsX];
    String cellsNext[][] = new String [numCellsY][numCellsX];
    boolean cellsImmune[][] = new boolean [numCellsY][numCellsX];
    int cellsImmuneLife[][] = new int [numCellsY][numCellsX];
    int surroundingCells[] = new int [4];
    ArrayList<Integer> coordinateList = new ArrayList<>();
    
    int labelX = width / 2;
    int labelY = borderWidth;
    int totalInfected;
    String humanState;
    
    int numWhiteBloodCells = 25;
    int numInfectedCells = 1;
    int whiteBloodStrength = 5;
    int cellsImmuneLifespan = 200;
    
    //Methods
    
    //Creates First Generation Randomly
    public void plantFirstGeneration() throws IOException {
        resetToNormal();
        
        for (int a = 0; a < numWhiteBloodCells; a++)
            cells[r.nextInt(numCellsY)][r.nextInt(numCellsX)] = "whiteblood";
        for (int b = 0; b < numInfectedCells; b++)
            cells[r.nextInt(numCellsY)][r.nextInt(numCellsX)] = "infected";
    }//End of plantFirstGeneration Method

    
    //Reset the Board and Fills the Array with Initial Values
    public void resetToNormal() {
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                cells[i][j] = "vulnerable";
                cellsNext[i][j] = "vulnerable";
                cellsImmune[i][j] = false;
                cellsImmuneLife[i][j] = 0;
            }
        }
    }//End of resetToNormal Method

    
    //Computes the Next Generation using the Past Generation
    public void computeNextGeneration() {
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                surroundingCells( i,j );
                
                switch (cells[i][j]) {
                    
                    case "whiteblood":
                        if ( countInfectedNeighbours( i,j ) > whiteBloodStrength ) {
                            cellsNext[i][j] = "infected";
                        } //White blood cell gets infected
                        else {
                            clearInfected( i,j );
                            cellsNext[i][j] = "vulnerable";
                            cellNextLocation( "whiteblood",i ,j );
                        } //White blood cell clears adjacent infected cells and moves
                        break;
                        
                    case "infected":
                        if ( detectWhiteBloodNeighbours( i,j ) == false ) {
                            cellsNext[i][j] = "infected";
                            cellNextLocation( "infected",i,j );
                        }
                        break;
                }
                updateImmuneLife( i,j );
            }
        }    
    }//End of computeNextGeneration Method
    
    
    //Checks the Range for Surrounding Cells
    public void surroundingCells(int i, int j) {
        if ( i == 0 ) {
            surroundingCells[0] = i;
            surroundingCells[1] = i + 1;            
        } else if ( i == numCellsY-1 ) {
            surroundingCells[0] = i - 1;
            surroundingCells[1] = i;                
        } else {
            surroundingCells[0] = i - 1;
            surroundingCells[1] = i + 1;                
        }

        if ( j == 0 ) {
            surroundingCells[2] = j;
            surroundingCells[3] = j + 1;
        } else if ( j == numCellsX-1 ) {
            surroundingCells[2] = j - 1;
            surroundingCells[3] = j;
        } else {
            surroundingCells[2] = j - 1;     
            surroundingCells[3] = j + 1;
        }    
    }//End of surroundingCells Method
    
    
    //Counts and Returns Number of Infected Cells Adjacent to White Blood Cells
    public int countInfectedNeighbours(int i, int j) {
        int infected = 0;
        for (int y = surroundingCells[0]; y <= surroundingCells[1]; y++) {
            for (int z = surroundingCells[2]; z <= surroundingCells[3]; z++) {
                if ( cells[y][z].equals("infected") )
                    infected++;
            }  
        }
        return infected;
    }//End of countLivingNeighbours Method
    
    
    //Clears Infected Cells Around a White Blood Cell
    public void clearInfected(int i, int j) {
        for (int y = surroundingCells[0]; y <= surroundingCells[1]; y++) {
            for (int z = surroundingCells[2]; z <= surroundingCells[3]; z++) {
                if ( cells[y][z].equals("infected") )
                    cellsImmune[y][z] = true;
            }  
        }   
    }//End of clearInfected Method
    
    
    //Checks if the Infected Cells has a White Blood Cell Adjacent to it
    public boolean detectWhiteBloodNeighbours(int i, int j) {      
        for (int y = surroundingCells[0]; y <= surroundingCells[1]; y++) {
            for (int z = surroundingCells[2]; z <= surroundingCells[3]; z++) {
                if ( cells[y][z].equals("whiteblood") )
                    return true;
            }  
        }
        return false;
    }//End of detectWhiteBloodNeighbours Method
    
    
    //Randomly Determines the Cell's Next Location
    public void cellNextLocation(String type, int i, int j) { 
        int randomRowIndex, randomColIndex;
        
        for (int y = surroundingCells[0]; y <= surroundingCells[1]; y++) {
            for (int z = surroundingCells[2]; z <= surroundingCells[3]; z++) {
                
                if ( ( cells[y][z].equals("vulnerable") && 
                        cellsImmune[y][z] == false || 
                        cellsImmune[y][z] == true && 
                        type.equals("whiteblood") ) && 
                        !cellsNext[y][z].equals("whiteblood") && 
                        !cells[y][z].equals("whiteblood") ) {
                    
                    coordinateList.add(y);
                    coordinateList.add(z);
                } //Adds possible locations into an ArrayList
            }  
        } 
        
        //Chooses a Random Cell within the ArrayList
        try {
            randomRowIndex = r.nextInt( coordinateList.size()/2 ) * 2;
            randomColIndex = randomRowIndex + 1;   
            cellsNext[coordinateList.get(randomRowIndex)]
                    [coordinateList.get(randomColIndex)] = type;
        } catch (Exception e) {
            cellsNext[i][j] = type;
        }
        coordinateList.clear(); //Clears list for next cell
    }//End of cellNextLocation Method
    
    
    //Updates the Lifespan of Immune Cells
    public void updateImmuneLife(int i, int j) {
        if ( cellsImmune[i][j] ) 
            cellsImmuneLife[i][j]++;
        if ( cellsImmuneLife[i][j] >= cellsImmuneLifespan ) {
            cellsImmune[i][j] = false;
            cellsImmuneLife[i][j] = 0;
        }
    }//End of updateImmuneLife Method
    
    
    //Updates to Next Generation
    public void plantNextGeneration() {
        totalInfected = 0;
        for (int i = 0; i < numCellsY; i++)
            for ( int j = 0; j < numCellsX; j++) {
                cells[i][j] = cellsNext[i][j];
                cellsNext[i][j] = "vulnerable";
                
                if ( cells[i][j].equals("infected") ) //Calculates total infected
                    totalInfected += 1;
            }
    }//End of plantNextGeneration Method
    
    
    //Pause Between Generations
    public void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    } //End of sleep Method

    
    //Displays Generation Statistics On The Top of The Screen
    void drawLabel(Graphics g, int state) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, borderWidth+10);
        g.setColor(Color.yellow);
        
        if ( totalInfected > numCellsX*numCellsY/3 ) //Determines if human is healthy or sick
            humanState = "Sick";
        else
            humanState = "Healthy";
        
        g.drawString("Generation: " + state, labelX-130, labelY);
        g.drawString(humanState, labelX+70, labelY);
    } //End of drawLabel Method
    
    
    //Draws Current Generation onto Screen
    public void paint(Graphics g) {
        Image img = createImage();
        g.drawImage(img, 0, 0, this);
    }//End of paint Method
    
    
    //Creates Image and Returns to paint Method
    private Image createImage() {
        int x, y;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        
        y = height/2 - numCellsY*cellWidth/2 + cellWidth;
        drawLabel(g, currGeneration);

        for (int i = 0; i < numCellsY; i++) {
            x = width/2 - numCellsX*cellWidth/2;
            
            for (int j = 0; j < numCellsX; j++) {
                
                switch (cells[i][j]) {
                    case "whiteblood":
                        g.setColor(whiteBloodColor);
                        break;
                    case "infected":
                        g.setColor(infectedColor);
                        break;
                    default:
                        g.setColor(vulnerableColor);
                        break;
                }
                
                g.fillRect(x, y, cellWidth, cellWidth);
                g.setColor(grid);
                g.drawRect(x, y, cellWidth, cellWidth);
                
                if ( cellsImmune[i][j] == true ) {
                    g.setColor(immuneColor);
                    g.fillOval(x, y, cellWidth/4, cellWidth/4);
                }
                x += cellWidth;
            }
            y += cellWidth;
        }
        return bufferedImage;
    } //End of createImage Method


    //Sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Immune System Simulator");
        setSize(height, width);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.black);
        setVisible(true); //Calls paint() for the first time
    } //End of initializeWindow Method

    
    //Main Algorithm
    public static void main(String[] args) throws IOException {
        CellularAutomaton currGame = new CellularAutomaton();
        currGame.plantFirstGeneration();
        currGame.initializeWindow();
        
        for (int i = 1; i <= currGame.numGenerations; i++) {
            currGame.computeNextGeneration();
            currGame.plantNextGeneration();
            currGame.currGeneration++;
            currGame.sleep( 10 );
            currGame.repaint(); 
        }
    }//End of main Method
    
}//End of CellularAutomaton Class
