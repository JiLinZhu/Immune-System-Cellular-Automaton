
package gameoflife;

//Imports
import java.io.*;
import java.util.Scanner;
import java.awt.*; 
import java.awt.image.BufferedImage;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

//GameofLife Class
public class GameOfLife extends JFrame {

    //Initializing Variables
    int numGenerations = 1000;
    int currGeneration = 1;
    
    Color aliveColor = Color.BLACK;
    Color deadColor = Color.WHITE;
    
    String fileName = "InitialCells2.txt";
    
    int width = 800;
    int height = 800;
    int borderWidth = 50;

    int numCellsX = 100;
    int numCellsY = 100;
    int cellWidth = (width - (2*borderWidth))/numCellsX;

    boolean alive[][] = new boolean [numCellsY][numCellsX];
    boolean aliveNext[][] = new boolean [numCellsY][numCellsX];
    
    int labelX = width / 2;
    int labelY = borderWidth;

    
    //METHODS
    
    //Creates First Generation
    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        
        //InitialCells1
        //plantFromFile( fileName );
        //plantBlock( 10, 10, 15, 15 );
        
        //InitialCells2
        //plantFromFile( fileName );
        
        //plantLWSS(20, 20, 4);
        
        //plantBlock(10,10,1,3);
        plantGliderGun(40,40);
        
    }//End of plantFirstGeneration Method

    
    //Sets All Cells To Dead
    public void makeEveryoneDead() {
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) 
                alive[i][j] = false;
        }
    }//End of makeEveryoneDead Method

    
    //Type 1: Read First Generation From File
    public void plantFromFile(String fileName) throws IOException {
        int x, y;
        FileReader f1 = new FileReader(fileName);
        Scanner s1 = new Scanner(f1);

        while ( s1.hasNext() ) {
            x = s1.nextInt() - 1;
            y = s1.nextInt() - 1;
            alive[y][x] = true;
        }
    }//End of plantFromFile Method

    
    //Type 2: Plants Block For First Generation
    public void plantBlock(int startX, int startY, int numColumns, int numRows) {
        
        int endCol = Math.min(startX + numColumns, numCellsX);
        int endRow = Math.min(startY + numRows, numCellsY);

        for (int i = startY; i < endRow; i++) {
            for (int j = startX; j < endCol; j++)
                alive[i][j] = true;
        }
    }//End of plantBlock Method

    
    //Type 3: Glider (Direction 1:NE, 2:SE, 3:SW, 4:NW) (startX and startY represent center)
    public void plantGlider(int startX, int startY, int direction) {
        alive[startY][startX] = true;
        
        switch (direction) {
            case 1: //Northeast
                alive[startY-1][startX-1] = true;
                alive[startY-1][startX] = true;
                alive[startY][startX+1] = true;
                alive[startY+1][startX-1] = true;
                break;
            case 2: //Southeast
                alive[startY-1][startX-1] = true;
                alive[startY][startX+1] = true;
                alive[startY+1][startX-1] = true;
                alive[startY+1][startX] = true;
                break;
            case 3: //Southwest
                alive[startY-1][startX+1] = true;
                alive[startY][startX-1] = true;
                alive[startY+1][startX] = true;
                alive[startY+1][startX+1] = true;
                break;
            case 4: //Northwest
                alive[startY-1][startX] = true;
                alive[startY-1][startX+1] = true;
                alive[startY][startX-1] = true;
                alive[startY+1][startX+1] = true;
                break;
        }
    }//End of plantGlider Method
    
    
    /*
    Type 4: Lightweight Spaceship (LWSS) (Directions 1:N, 2:E, 3:S, 4:W) 
    startX and startY represent top left corner
    Build is 5x5 and begins from top left corner, this might cause
    error if placed outside of range of array index.
    */
    public void plantLWSS(int startX, int startY, int direction) throws IOException{
        FileReader f2 = new FileReader("LWSS.txt");
        Scanner s2 = new Scanner(f2);
        
        for ( int l = 0; l < direction; l++)
            s2.nextLine();
        
        for ( int i = startY; i < startY+5; i++ ) {
            for ( int j = startX; j < startX+5; j++ ) {
                if ( s2.next().equals("x") ) 
                    alive[i][j] = true;
            }
        }
    }//End of plantLWSS Method

    
    //Type 5: Glider Gun(May cause error due to array range)
    public void plantGliderGun(int startX, int startY) throws IOException {
        FileReader f3 = new FileReader("GliderGun.txt");
        Scanner s3 = new Scanner(f3);
        
        s3.nextLine();
        
        for ( int i = startY; i < startY+9; i++ ) {
            for ( int j = startX; j < startX+38; j++ ) {
                if ( s3.next().equals("x") ) 
                    alive[i][j] = true;
            }
        }   
    }//End of plantGliderGun Method
    
    //Finds and Saves the True/False Values of the Next Generation
    public void computeNextGeneration() {
        int numNeighbours;
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                numNeighbours = countLivingNeighbours(i,j);
                aliveNext[i][j] = !(numNeighbours <= 1 || numNeighbours >= 4 || alive[i][j] == false && numNeighbours == 2);
            }
        }      
    }//End of computeNextGeneration Method

    
    //Updates to Next Generation
    public void plantNextGeneration() {
        for (int i = 0; i < numCellsY; i++)
            System.arraycopy(aliveNext[i], 0, alive[i], 0, numCellsX);
    }//End of plantNextGeneration Method

    
    //Counts Number of Living Cells Adjacent to each Cell
    public int countLivingNeighbours(int i, int j) {
        int living = 0;
        int rowBegin, rowEnd, colBegin, colEnd;
        
        if ( i == 0 ) {
            rowBegin = i;
            rowEnd = i + 1;            
        } else if ( i == numCellsY-1 ) {
            rowBegin = i - 1;
            rowEnd = i;                
        } else {
            rowBegin = i - 1;
            rowEnd = i + 1;                
        }

        if ( j == 0 ) {
            colBegin = j;
            colEnd = j + 1;
        } else if ( j == numCellsX-1 ) {
            colBegin = j - 1;
            colEnd = j;
        } else {
            colBegin = j - 1;     
            colEnd = j + 1;
        }
        
        if ( alive[i][j] )
            living--;    
        
        for (int y = rowBegin; y <= rowEnd; y++) {
            for (int z = colBegin; z <= colEnd; z++) {
                if ( alive[y][z] )
                    living++;
            }  
        }
        return living;
    }//End of countLivingNeighbours Method

    
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
        g.drawString("Generation: " + state, labelX-41, labelY);
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
                
                if (alive[i][j]) 
                    g.setColor(aliveColor);               
                else 
                    g.setColor(deadColor);
                
                g.fillRect(x, y, cellWidth, cellWidth);
                g.setColor(Color.black);
                g.drawRect(x, y, cellWidth, cellWidth);
                
                x += cellWidth;
            }
            y += cellWidth;
        }
        return bufferedImage;
    } //End of createImage Method


    //Sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Game of Life Simulator");
        setSize(height, width);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.black);
        setVisible(true); //calls paint() for the first time
    } //End of initializeWindow Method
    
    
    //Main Algorithm
    public static void main(String args[]) throws IOException {

        GameOfLife currGame = new GameOfLife();
        currGame.plantFirstGeneration();
        currGame.initializeWindow();
            
        for (int i = 1; i <= currGame.numGenerations; i++) {
            currGame.computeNextGeneration();
            currGame.plantNextGeneration();
            currGame.currGeneration++;
            currGame.sleep( 50 );
            currGame.repaint(); 
        }
    } //End of Main Algorithm
    
} //End of Class
