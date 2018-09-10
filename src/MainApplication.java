import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

   public class MainApplication extends JFrame implements MouseListener, Runnable, MouseMotionListener {
   
   private BufferStrategy strategy;
   private static final Dimension windowSize = new Dimension(800,800);	
   private static int ROWS = 40;
   private static int COLUMNS = 40;
   private boolean playing = false;
   private boolean gameState[][][] = new boolean[40][40][2];
   private int gameStateFrontBuffer = 0;

   
   public MainApplication()
   {
	    this.setTitle("Conway's game  16409296");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = screensize.width/2 - windowSize.width/2;
		int y = screensize.height/2 - windowSize.height/2;
		setBounds(x , y, windowSize.width, windowSize.height);
		setVisible(true);
		addMouseMotionListener(this);
		
		Thread t = new Thread(this);
		t.start();
		
		addMouseListener(this);
		
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		for(int i=0; i<ROWS; i++) {
			for(int j =0; j < COLUMNS; j++) {
				for(int l = 0 ; l < 2; l++) {
				  gameState[i][j][l] = false;
			}
		}
	  }	
		
    }
	
    public void run() {
    	while(true)
		{
			try 
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
			}
			
			if(playing) {
				
			playGame();
			this.repaint();
			}
		}
	}
    
    public void paint(Graphics g)
    {
          g = strategy.getDrawGraphics();
          g.fillRect(0, 0, 800, 800);
          
          g.setColor(Color.WHITE);
          for(int i = 0 ; i < ROWS; i++) {
        	       for( int k = 0; k < COLUMNS; k++) {
        	    	      if(gameState[i][k][gameStateFrontBuffer] == true)
        	    	    	        g.fillRect(i*20, k*20, 20, 20);
        	       }
          }
       
    	   
      g.setColor(Color.BLUE);
      g.fillRect(15, 40, 70, 30);
      g.fillRect(115, 40, 100, 30);
      g.fillRect(250, 40, 70, 30);
      g.fillRect(350, 40, 70, 30);
  
      
      g.setFont(new Font("Times", Font.PLAIN, 24));
      g.setColor(Color.WHITE);
      g.drawString("Start", 20, 60);
      g.drawString("Random", 120, 60);
      g.drawString("Load", 255, 60);
      g.drawString("Save", 355, 60);
     
      g.dispose();
      strategy.show();
      
    }
    
    private void playGame() {

    	int front = gameStateFrontBuffer;
    	int back = (front+1)%2;
        for (int x=0;x<40;x++) {
        	for (int y=0;y<40;y++) {

        		int liveNeighbours=0;
        		for (int x2=-1;x2<=1;x2++) {
        			for (int y2=-1;y2<=1;y2++) {
        				if (x2!=0 || y2!=0) {
        					int x3=x+x2;
        					if (x3<0)
        						x3=39;
        					else if (x3>39)
        						x3=0;
        					int y3=y+y2;
        					if (y3<0)
        						y3=39;
        					else if (y3>39)
        						y3=0;           					
        					if (gameState[x3][y3][front])
        						liveNeighbours++;
        				}
        			}
        		}

        		
        		if (gameState[x][y][front]) {
        			if (liveNeighbours<2)
        				gameState[x][y][back] = false;
        			
        			else if (liveNeighbours<4)
        				gameState[x][y][back] = true;
        			else
        				gameState[x][y][back] = false;
        		}
        		else {
        			if (liveNeighbours==3)
        				gameState[x][y][back] = true;
        			else
        				gameState[x][y][back] = false;
        		}
        	}
        }
    	
       	gameStateFrontBuffer = back;		
	}
    
	@Override
	public void mouseClicked(MouseEvent e){		
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX()/20;
	   	int y = e.getY()/20;
    	    gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
	   	this.repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {	
		int x = e.getX()/20;
	   	int y = e.getY()/20;
	   	 
	   	 if((e.getX()>= 30 && e.getX()<= 90) && (e.getY()>=40 && e.getY()<=70)){  
	   		 playing = true;                       
	   	 }
	   	 
	   	if((e.getX()>= 115 && e.getX()<= 160) && (e.getY()>=40 && e.getY()<=70)){
	   		int random = (Math.random() <= 0.5) ? 1 : 2;                                
	   		if(random == 1)
	   		{  playing = true;
	   		}                                    
	   		else if(random == 2)
	   		{  playing = false;
	   		}
	   	}
	   	if((e.getX()>= 250 && e.getX()<= 320) && (e.getY()>=40 && e.getY()<=70)){
	   		loadImage();
	   	}
	   	
	   	if((e.getX()>= 350 && e.getX()<= 420) && (e.getY()>=40 && e.getY()<=70)){
	   		saveImage();
	   	}
	   	
 
	    gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
	   	this.repaint();
	}

	private void saveImage() {
		String outputtext="";
		for (int x=0;x<ROWS;x++) {
        	for (int y=0;y<COLUMNS;y++) {
        		if (gameState[x][y][gameStateFrontBuffer] == true)
        			outputtext+="1";
        		else
        			outputtext+="0";
        	}
        }
		String filename = "C:/Users/JordanCoyne/Documents/College /NGT 2/Conways Game.txt";
		try {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(outputtext);
		writer.close();
		}
		catch (IOException e) { }

	}

	private void loadImage() {
		String textinput = null;
		String filename = "C:/Users/JordanCoyne/Documents/College /NGT 2/Conways Game.txt";
		try {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		do {
		try {
		textinput = reader.readLine();
		
		if (textinput!=null) {
	        for (int x=0;x<ROWS;x++) {
	        	for (int y=0;y<COLUMNS;y++) {
	        		gameState[x][y][gameStateFrontBuffer] = (textinput.charAt(x*40+y)=='1');
	        	}
	        }			
		}
	
		} catch (IOException e) { }
		}
		while (textinput != null);
		reader.close();
		} catch (IOException e) { }
	}

	@Override
	public void mouseReleased(MouseEvent e) {	
	}

	@Override
	public void mouseEntered(MouseEvent e) {	
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}	
	
	public static void main(String[] args) {
		MainApplication m = new MainApplication();
	}
}