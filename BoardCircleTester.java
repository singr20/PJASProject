import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.awt.AWTException;
//import javax.swing.Timer;
import java.util.*;

import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import sun.audio.*;

public class BoardCircleTester extends JPanel implements Runnable, MouseListener, MouseMotionListener
{
boolean ingame = false;
private Dimension d;
int BOARD_WIDTH=1440;
int BOARD_HEIGHT=877;

int smallX = 720;
int smallY = 438;
boolean inCircle = false;
int[][] randomArray = new int[250][2];
int startLineY2 = -400;

double bigX = BOARD_WIDTH/2;
double bigY = BOARD_HEIGHT/2;
double timeCircle;
double counter = 0;
double totalTime = 0;
double totalTimePerFrame = 0;
double totalTimeStart = 0;
double totalTimePerFrameStart = 0;

double tempX1 = 0;
double tempX2 = 0;
double tempY1 = 0;
double tempY2 = 0;
double distanceX = 0;
double distanceY = 0;

double rateCircle;

private long startTime = 0;
private long stopTime = 0;
private boolean running = false;

BufferedImage img;

Toolkit toolkit;
Timer timer;

private Thread animator;
Cursor blankCursor = null;

public BoardCircleTester()
{
    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage("blank.png"), new Point(0, 0), "blankCursor"); // blank.png is any tranparent image.
    setVisible(true);
    addKeyListener(new TAdapter());
    addMouseListener(this);
    addMouseMotionListener(this);
    setFocusable(true);
    d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    setBackground(Color.black);
    /*
    try {
        img = ImageIO.read(this.getClass().getResource("dice.png"));
    } catch (IOException e) {
        System.out.println("Image could not be read");
    }
    */
    if (animator == null || !ingame) {
        animator = new Thread(this);
        animator.start();
    }  
    
    randomArray[0][0] = BOARD_WIDTH/2;
    for(int i = 1; i < 250; i++){
        arrayX(randomArray, i);
    }
    randomArray[0][1] = -400;
    for(int j = 1; j < 250; j++){
        arrayY(randomArray, j);
    }
    
    setDoubleBuffered(true);
}
    
public void arrayX(int[][] a, int b){
    int x = (int)((BOARD_WIDTH/2 - 125) + Math.random() * (250));
    a[b][0] = x;
}

public void arrayY(int[][] a, int b){
    int x = (int)((Math.random() * 50) + 75);
    a[b][1] = a[b-1][1] - x;
}

public void paint(Graphics g)
{
    super.paint(g);

    g.setColor(Color.lightGray);
    g.fillRect(0, 0, d.width, d.height);
    
    if(!ingame){
        g.setColor(Color.white);
        Font start = new Font("Helvetica", Font.BOLD, 50);
        FontMetrics metr = this.getFontMetrics(start);
        g.setFont(start);
        g.drawString("Press SPACEBAR to Start", d.width/2 - 300, d.height-300);    
        start = new Font("Helvetica", Font.BOLD, 35);
        metr = this.getFontMetrics(start);
        g.setFont(start);
        g.drawString("Keep Your Cursor in the Circle!", 465, d.height - 575);  
        start = new Font("Helvetica", Font.BOLD, 80);
        metr = this.getFontMetrics(start);
        g.setFont(start);
        g.drawString("Concentration Test", d.width/2 - 350, d.height/2 - 200);  
        start = new Font("Helvetica", Font.BOLD, 18);
        metr = this.getFontMetrics(start);
        g.setFont(start);
        g.drawString("v1.0.2 by raunaqsingh", 10, 800);  
        
        g.setColor(Color.red);
        g.fillOval(smallX - 5, smallY - 5, 10,10);
    }
    
    if(ingame){
        g.setColor(Color.black);
        
        if(totalTimePerFrame >= 60){
            ingame = false;
        }
        
        for(int i = 0; i < 248; i++){
            g.drawLine(randomArray[i][0],randomArray[i][1],randomArray[i+1][0],randomArray[i+1][1]);
        }
        
        g.drawLine(BOARD_WIDTH/2,BOARD_HEIGHT,BOARD_WIDTH/2,startLineY2);
        startLineY2 += 1;
        
        for(int i = 0; i < 249; i++){
            (randomArray[i][1]) += 1;
            if((randomArray[i][1]) <= BOARD_HEIGHT/2 + 1 && (randomArray[i][1]) >= BOARD_HEIGHT/2 - 1){
                tempX1 = randomArray[i][0];
                tempY1 = randomArray[i][1];
                tempX2 = randomArray[i+1][0];
                tempY2 = randomArray[i+1][1];                
                //bigX = tempX1;
                distanceX = Math.abs(tempX2 - tempX1);
                distanceY = Math.abs(tempY2 - tempY1);
            }
        }
        
        //axes
        //g.drawLine(0,BOARD_HEIGHT/2,BOARD_WIDTH,BOARD_HEIGHT/2);
        //g.drawLine(BOARD_WIDTH/2,0,BOARD_WIDTH/2,BOARD_HEIGHT);

        g.setColor(Color.blue);
        g.drawOval((int)(bigX - 25), BOARD_HEIGHT/2 - 25, 50,50);
        //g.drawLine(0,BOARD_HEIGHT/2,(int)(bigX-10),BOARD_HEIGHT/2);
        //g.drawLine((int)(bigX+10),BOARD_HEIGHT/2,BOARD_WIDTH,BOARD_HEIGHT/2);
        //g.drawLine((int)bigX,0,(int)bigX,BOARD_HEIGHT/2 - 10);
        //g.drawLine((int)bigX,BOARD_HEIGHT/2 + 10,(int)bigX,BOARD_HEIGHT);
        rateCircle = distanceX/distanceY;
        
        if(tempX2 > tempX1 && bigX < tempX2)
            bigX += rateCircle;
        if(tempX1 > tempX2 && bigX > tempX2)
            bigX -= rateCircle;
   
        double longAccuracy = ((timeCircle/totalTimePerFrame) * 100);
        String accuracy = String.format("%.2f", longAccuracy);
        String time = String.format("%.2f", totalTimePerFrame);
        String circleTime = String.format("%.2f", timeCircle);
    
        Font small = new Font("Helvetica", Font.BOLD, 15);
        FontMetrics metric = this.getFontMetrics(small);
        g.setColor(Color.black);
        g.setFont(small);
        g.drawString("Accuracy: %" + accuracy, 1275, d.height-205);    
        g.drawString("Time in Circle: " + circleTime, 1275, d.height-170);
        g.drawString("Total Time: " + time, 1275, d.height-135);
        g.drawString("In: " + inCircle, 1275, d.height-100);
    
        g.setColor(Color.white);
        
        if(totalTimePerFrameStart >= 0.75 && totalTimePerFrameStart <= 0.76 || totalTimePerFrameStart >= 1.75 && totalTimePerFrameStart <= 1.76 || totalTimePerFrameStart >= 2.75 && totalTimePerFrameStart <= 2.76){
            try{
                String sound = "/Users/singr20/PJAS Project/beep.wav";
                InputStream in = new FileInputStream(sound);
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            }
            catch(Exception e){
                
            }
        }
        
        if(totalTimePerFrameStart >= .75 && totalTimePerFrameStart <= 1.25){
            Font bigBoi = new Font("Helvetica", Font.BOLD, 100);
            FontMetrics met = this.getFontMetrics(bigBoi);
            g.setColor(Color.red);
            g.setFont(bigBoi);
            g.drawString("3", BOARD_WIDTH/2 - 25, BOARD_HEIGHT/2 + 25);
        }
        if(totalTimePerFrameStart >= 1.75 && totalTimePerFrameStart <= 2.25){
            Font bigBoi = new Font("Helvetica", Font.BOLD, 100);
            FontMetrics met = this.getFontMetrics(bigBoi);
            g.setColor(Color.red);
            g.setFont(bigBoi);
            g.drawString("2", BOARD_WIDTH/2 - 25, BOARD_HEIGHT/2 + 25);  
        }
        if(totalTimePerFrameStart >= 2.75 && totalTimePerFrameStart <= 3.25){
            Font bigBoi = new Font("Helvetica", Font.BOLD, 100);
            FontMetrics met = this.getFontMetrics(bigBoi);
            g.setColor(Color.red);
            g.setFont(bigBoi);
            g.drawString("1", BOARD_WIDTH/2 - 25, BOARD_HEIGHT/2 + 25);  
        }
        
        g.setColor(Color.red);
        g.fillOval(smallX - 5, smallY - 5, 10,10);
        
        if (smallX >= (bigX - 25) && smallX <= (bigX + 25) && smallY >= (BOARD_HEIGHT/2 - 25) && smallY <= (BOARD_HEIGHT/2 + 25)){
          if(inCircle==false){
               inCircle = true;
          }
        }
      
        if (!(smallX >= (bigX - 25) && smallX <= (bigX + 25) && smallY >= (BOARD_HEIGHT/2 - 25) && smallY <= (BOARD_HEIGHT/2 + 25))){
          if(inCircle==true){
               inCircle = false;
          }
        }
        
        countTotalTimeStart();
        if(totalTimePerFrameStart >= 3.25){
            countTotalTime();
            countTime();
        }
        
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
}

private class TAdapter extends KeyAdapter {
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
    }
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key==32){
           ingame = true;
        }
    }
}

public void mousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

}

public void mouseDragged(MouseEvent e) {
     int x = e.getX();
     int y = e.getY();
          
     //  System.out.println("Mouse is Dragged");
}    

public void countTime(){
    if(inCircle==true){
         counter++;   
    }
    
    timeCircle = counter/185;
}

public void countTotalTime(){
    totalTime++;
    totalTimePerFrame = totalTime/185;
}

public void countTotalTimeStart(){
    totalTimeStart++;
    totalTimePerFrameStart = totalTimeStart/185;
}

public void mouseMoved(MouseEvent e){
     setCursor(blankCursor);  
    
     smallX = e.getX();
     smallY = e.getY();
      
}

public void mouseReleased(MouseEvent e) {

}

public void mouseEntered(MouseEvent e) {
      
}

public void mouseExited(MouseEvent e) {

}

public void mouseClicked(MouseEvent e) {

}

public void run() {

    long beforeTime, timeDiff, sleep;

    beforeTime = System.currentTimeMillis();
    int animationDelay = 0;
    long time = 
    System.currentTimeMillis();
    while (true) {//infinite loop
        // spriteManager.update();
        repaint();
        try {
            time += animationDelay;
            Thread.sleep(Math.max(0,time - 
            System.currentTimeMillis()));
        }catch (InterruptedException e) {
            System.out.println(e);
        }//end catch
    }//end while loop

}//end of run

}//end of class