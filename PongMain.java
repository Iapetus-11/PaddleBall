import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.AWTException;

public class PongMain{
   public static String ver = "v3.1"; /* Paddle collision changes (direction changed based on where ball hits paddle),
                                         new pause button, shrunk paddle movement code, fixed JFrame.setCursor(int) deprecation
                                         warning, started working on code for breakout stuff, did some more minor bug fixes*/
   public static JTextArea textField = new JTextArea();
   public static JFrame jframe = new JFrame("PaddleBall "+ver+" | Score: 0");
   public static char bg = ' '; //background character
   public static char c = '#'; //paddle character
   public static String chr = ""+c+c+c+c+c+c+c+c+c+c+c+c; //12 of c
   public static String ball = "O";
   public static char newLine = '\n';
   public static boolean lock = false;
   public static boolean locked = false; //for longer locks
   public static int score = 0;
   public static boolean inMenu = true;
   public static int minSpeed = 0; //min speed for ball
   public static int maxSpeed = 0; //max speed for ball
   public static int pen = 2; //ball hitting floor penalty
   //secret color mode stuff (shhhhhhhhh)
   public static boolean colourz = false; //whether or not to do colors
   public static Color red = new Color(255, 0, 0);
   public static Color orange = new Color(255, 165, 0);
   public static Color yellow = new Color(255, 255, 0);
   public static Color green = new Color(0, 255, 0);
   public static Color blue = new Color(0, 0, 255);
   public static Color indigo = new Color(120, 0, 175);
   public static Color violet = new Color(200, 0, 200);
   public static Color[] colors = new Color[7];
   public static int cAt = 0; //color At
   //other stuff
   public static int mousePosX = 0; //mouse position on x axis
   public static int mousePosXPrev = 0; //previous mouse position on x axis
   public static int paddlePos = 0; //position of paddle based on index in display string
   public static Point winMid = new Point(0, 0); //a point that should be in the middle of the window (eventually)
   public static String diff = "";
   
   public static void main(String[] args){
      //while (true) loop allows for back to menu feauture (if exit is called it will use System.exit() the only reason
      //this would run multiple times is if 'm' is pressed.)
      while (true){
         System.out.println("main");
         try{initDisp();} //init display (setup jframe and jtextfield and some other stuff)
         catch(IOException e){} 
         menu(); //do menu (difficulty options) and stuff
         Utils.sleep(500);
      }
   }
   
   public static void exit(){
      System.out.println("exit");
      lock = true;
      Utils.sleep(750);
      //make sure everything is exited (1 System.exit(0) sometimes doesn't work properly for some reason)
      System.exit(0);
      System.exit(0);
   }
   
   //get icon from jar file
   public BufferedImage getImg(String file) throws Exception{
      return ImageIO.read(this.getClass().getResource(file));
   }
   
   //setup window and textarea
   public static void initDisp()throws IOException{
      System.out.println("initDisp");
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
      textField.setSelectionColor(Color.BLACK);
      textField.setSelectedTextColor(Color.WHITE);
      textField.setBackground(Color.BLACK);
      textField.setForeground(Color.WHITE);
      textField.addKeyListener(new MKeyListener()); //add key listener
      textField.setEditable(false); //make so user can't edit the jtextarea
      textField.setFont(font);
      jframe.add(textField); //add textField to jframe
      Dimension minSize = new Dimension(63+465*1, 45+312*1);
      jframe.setTitle("PaddleBall "+ver+" | Score: 0");
      jframe.setSize(63+465*1, 45+312*1);
      jframe.setMinimumSize(minSize);
      jframe.setVisible(true);
      jframe.setBackground(Color.BLACK);
      jframe.setAlwaysOnTop(false);
      try{jframe.setIconImage(new PongMain().getImg("icon.png"));}
      catch(Exception e){} //call method to get & set window icon
      jframe.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      textField.addMouseMotionListener(new MouseMovement()); //mouse movement listener (moving, dragging)
      textField.addMouseListener(new MouseMovement()); //mouse listener (click, buttons)
      //catch window closing event, call JFrame.dispose() and exit()
      jframe.addWindowListener(
         new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent event){
               jframe.dispose();
               exit();
            }
         }
      );
   }
   
   public static void menu(){
      System.out.println("menu");
      inMenu = true;
      textField.setForeground(Color.WHITE);
      BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); //make blank cursor
      Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor"); //make blank cursor
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14*1);
      Font font2 = new Font(Font.MONOSPACED, Font.PLAIN, 18*1);
      textField.setFont(font2);
      String welcome = "\n    PaddleBall - an Olim One Studios game\n\n\n             Select Difficulty:\n\n        1. Can I play, Daddy?\n        2. It's paddle breaking time!";
      textField.setText(welcome); //set jtextarea to the menu string
      while(inMenu){Utils.sleep(1);}
      textField.setFont(font);
      String display = "";
      jframe.setCursor(blankCursor); //set cursor to blank cursor
      initGrid(display); //setup 'grid' for game
      lock = false;
      moveBall();
   }
   
   public static void doColourz(){
      System.out.println("doColourz");
      colors[0] = red;
      colors[1] = orange;
      colors[2] = yellow;
      colors[3] = green;
      colors[4] = blue;
      colors[5] = indigo;
      colors[6] = violet;
      textField.setForeground(colors[cAt]);
      cAt++;
      if (cAt-1==6){
         cAt = 0;
      }
   }
   
   //setup 'grid'
   public static String initGrid(String display){
      System.out.println("initGrid");
      int i = 0;
      bg = PongMain.bg;
      display = "";
      while (i!=1088-PongMain.chr.length()){
         if (i==792+64*2+1){ //place paddle
            display+=PongMain.chr;
            i++;
         }else if (i==64+31){ //place ball
            display+=ball;
            i++;
         }else{ //place background char
            display+=bg;
            i++;
         }
      }
      draw(display);
      return display;
   }
   
   //'draws' string to the jtextarea
   //splits up string, adds \n to end of each line, then sends it to jtextarea
   public static void draw(String display){
      System.out.println("draw");
      int i = 0;
      int lines = 16;
      String line[] = new String[lines];
      while (i!=lines){
         line[i] = display.substring((i*64),64+(i*64));
         i++;
      }
      display = "";
      i = 0;
      while (i!=lines){
         display += line[i]+"\n";
         i++;
      }
      PongMain.textField.setText(display);
   }
   
   /*
   //generate obstacles based on given pattern
   public static genObss(String pattern){
      //generate obstacles 
   }
   
   //generate random sized obstacle based on position p in string
   public static genObss(int p){
   
   }*/
   
   //move paddle back and forth with keyboard
   public static void moveChr(int x, int y){
      System.out.println("moveChr");
      while (lock){Utils.sleep(1);}
      lock = true;
      String display = PongMain.textField.getText();
      int pos = display.indexOf(chr);
      String disp1;
      String disp2;
      int i = 0;
      
      //right
      if (x>1){
         for (int c = 0; c <= 5; c++){
            if (pos!=-1 && display.charAt(pos+chr.length()+1+c)==bg && pos+1<963){
               disp1 = display.substring(0, pos);
               disp2 = display.substring(pos+PongMain.chr.length());
               display = disp1+PongMain.bg+PongMain.bg+PongMain.bg+disp2;
               disp1 = display.substring(0, pos+1);
               disp2 = display.substring(pos+4);
               display = disp1+chr+disp2;
               pos++;
            }
         }
      }
      
      //left
      if (x<0){
         for (int c = 0; c<=5; c++){
            if (pos!=-1 && display.charAt(pos-1)==bg && pos-1>909){   
               disp1 = display.substring(0, pos);
               disp2 = display.substring(pos+PongMain.chr.length());
               display = disp1+PongMain.bg+PongMain.bg+PongMain.bg+disp2;
               disp1 = display.substring(0, pos-1);
               disp2 = display.substring(pos+2);
               display = disp1+chr+disp2;
               pos--;
            }
         }
      }      
      paddlePos = pos;
      PongMain.textField.setText(display);
      lock = false;
   }
   
   //move paddle with mouse
   public static void moveChr2(int x){
      System.out.println("moveChr2");
      while (lock){Utils.sleep(1);}
      lock = true;
      String display = PongMain.textField.getText();
      int pos = display.indexOf(chr);
      String disp1;
      String disp2;
      
      //right
      if (x>0){
         if (pos<962 && pos!=-1 && display.charAt(pos+1)!=newLine && display.charAt(pos+1)!=ball.charAt(0)){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+PongMain.chr.length());
            display = disp1+PongMain.bg+PongMain.bg+PongMain.bg+disp2;
            disp1 = display.substring(0, pos+1);
            disp2 = display.substring(pos+4);
            display = disp1+chr+disp2;
         }
      }
      
      //left
      if (x<0){
         if (pos>910 && pos!=-1 && display.charAt(pos-1)!=newLine && display.charAt(pos-1)!=ball.charAt(0)){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+PongMain.chr.length());
            display = disp1+PongMain.bg+PongMain.bg+PongMain.bg+disp2;
            disp1 = display.substring(0, pos-1);
            disp2 = display.substring(pos+2);
            display = disp1+chr+disp2;
         }
      }
      paddlePos = pos;
      PongMain.textField.setText(display);
      lock = false;
   }
   
   //reset paddle location and ball position, can reset score
   public static void reset(int s){
      System.out.println("reset");
      lock = true;
      String display = "";
      initGrid(display);
      score = s;
      if (diff.equals("Easy")){
         textField.setForeground(Color.PINK);
      }else{
         textField.setForeground(Color.WHITE);
      }
      jframe.setTitle("PaddleBall "+ver+" | Score: "+score+" | Difficulty: "+diff);
      System.out.println("#### RESET ####");
      try{
         Robot robo = new Robot();
         winMid = textField.getLocationOnScreen();
         robo.mouseMove((int)PongMain.winMid.getX()+((63+465*1)/2), (int)PongMain.winMid.getY()+((45+312*1)/2));
      }catch(AWTException e2){e2.printStackTrace();}
      lock = false;
   }
   
   //main game loop
   //moves ball, checks for errors, etc...
   public static void moveBall(){
      System.out.println("moveBall");
      JTextArea textField = PongMain.textField;
      String display = textField.getText();
      int headingX = 0;
      int headingY = -1;
      int pos = textField.getText().indexOf(ball);
      int i = 0;
      String disp1;
      String disp2;
      Utils.sleep(1000);
      int speed = 125;
      //ball movement
      while (true){
         System.out.println("moveBall");
         if (inMenu){
            return;
         }
         //System.out.println(paddlePos);
         Utils.sleep(speed);
         while (lock){
            Utils.sleep(1);
            if (inMenu){
               return;
            }
         }
         lock = true;
         if (colourz){doColourz();}
         display = textField.getText();
         pos = display.indexOf(ball);
         if (headingY==0){headingY=-1;}
         //reset game if needed, checks for missing ball, missing paddle, checks for ball in index 0 (top left corner)
         if (pos==-1||display.indexOf(chr)==-1){
            if (!PongMain.inMenu){
               reset(score);
               Utils.sleep(1000);
               display = textField.getText();
               pos = display.indexOf(ball);
               i = 0;
               headingX = 0;
               headingY = -1;
               speed = 125;
            }
         }
         
         //right
         i = 0;
         while (headingX>0 && i<Math.abs(headingX)){
           if (display.charAt(pos+1)!=newLine){
               disp1 = display.substring(0, pos);
               disp2 = display.substring(pos+PongMain.ball.length());
               display = disp1+PongMain.bg+disp2;
               disp1 = display.substring(0, pos+1);
               disp2 = display.substring(pos+2);
               display = disp1+ball+disp2;
               pos++;
            }else{
               headingX = (int)(Math.random()*2+1)*-1;
            }
            i++;
         }
         
         //left
         i = 0;
         while (headingX<0 && i<Math.abs(headingX)){
            if (pos>0 && display.charAt(pos-1)!=newLine){
               disp1 = display.substring(0, pos);
               disp2 = display.substring(pos+PongMain.ball.length());
               display = disp1+PongMain.bg+disp2;
               disp1 = display.substring(0, pos-1);
               disp2 = display.substring(pos);
               display = disp1+ball+disp2;
               pos--;
            }else{
               headingX = (int)(Math.random()*2+1);
            }
            i++;
         }
         
         //down
         if (headingY<0){
            if (pos<960){
               if (display.charAt(pos+65)!=c){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+PongMain.ball.length());
                  display = disp1+PongMain.bg+disp2;
                  disp1 = display.substring(0, pos+65);
                  disp2 = display.substring(pos+66);
                  display = disp1+ball+disp2;
               }else if (display.charAt(pos+64+1)==c){
                  headingY = 1;
                  headingX = ((int)(Math.random()*5)-2);
                  if (headingX==0){headingX=((int)(Math.random()*5)-2);}
                  if (pos+65>paddlePos+6+1){
                     headingX = Math.abs(headingX);
                  }else if (pos+65<paddlePos+6-1){
                     headingX = Math.abs(headingX)*-1;
                  }
                  score++;
                  jframe.setTitle("PaddleBall "+ver+" | Score: "+score+" | Difficulty: "+diff);
                  speed = (int)(Math.random()*(maxSpeed-minSpeed+1)+minSpeed);
                  if (Math.abs(headingX)>1){
                     speed+=15;
                  }
               }
            }else if (pos>128){ //if ball is not at ceiling
               speed = (int)(Math.random()*(maxSpeed-minSpeed+1)+minSpeed);
               headingY = 1;
               headingX = ((int)(Math.random()*5)-2);
               if (headingX==0){headingX=((int)(Math.random()*5-2));}
               if (headingX==0){headingX=((int)(Math.random()*5-2));}
               if (Math.abs(headingX)>1){
                  speed+=15;
               }
               score-=pen;
               jframe.setTitle("PaddleBall "+ver+" | Score: "+score+" | Difficulty: "+diff);
            }
         }
         
         //up
         if (headingY>0){
            if (pos-64>0){
               if (display.charAt(pos-65)!='#'){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+PongMain.ball.length());
                  display = disp1+PongMain.bg+disp2;
                  disp1 = display.substring(0, pos-65);
                  disp2 = display.substring(pos-64);
                  display = disp1+ball+disp2;
               }
            }
         }
         
         if (pos<64){ //if pos is near top
            headingY = -1;
            if (Math.abs(headingX)>1){
               int prevHeadingX = headingX;
               do{
                  headingX = ((int)(Math.random()*5)-2);
               }while (headingX==0);
               if (prevHeadingX>0){
                  headingX = Math.abs(headingX);
               }else if (prevHeadingX<0){
                  headingX = Math.abs(headingX)*-1;
               }
               if (Math.abs(headingX)>1){
                  speed+=15;
               }
            }
         }
         
         //draw it
         PongMain.textField.setText(display);
         lock = false;
      }
   }
}

//key listener
class MKeyListener extends KeyAdapter{
   @Override
   public void keyPressed(KeyEvent event){
      System.out.println("keyPressed");
      
      //detect movement keys
      if (!PongMain.lock){
         if (!PongMain.inMenu){
            if (event.getKeyChar()=='d'||event.getKeyChar()=='D'){
               PongMain.moveChr(2, 0);
            }
         
            if (event.getKeyChar()=='a'||event.getKeyChar()=='A'){
               PongMain.moveChr(-2, 0);
            }
         }
         
         //difficulty option 1
         if (event.getKeyChar()=='1' && PongMain.inMenu){
            PongMain.maxSpeed = 150;
            PongMain.minSpeed = 100;
            PongMain.inMenu = false;
            PongMain.pen = 2;
            PongMain.diff = "Easy";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
            PongMain.textField.setForeground(Color.PINK);
         }
         
         //difficulty option 2
         if (event.getKeyChar()=='2' && PongMain.inMenu){
            PongMain.maxSpeed = 60;
            PongMain.minSpeed = 40;
            PongMain.inMenu = false;
            PongMain.pen = 3;
            PongMain.diff = "Medium";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
         }
         
         //super secret difficulty option 3
         if (event.getKeyChar()=='0' && PongMain.inMenu){
            PongMain.maxSpeed = 30;
            PongMain.minSpeed = 25;
            PongMain.inMenu = false;
            PongMain.pen = 5;
            PongMain.diff = "Ultra";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
         }
         
         //reset key, resets score to 0 and paddle and ball position
         if (event.getKeyChar()=='r'||event.getKeyChar()=='R'){
            if (!PongMain.inMenu){
               Utils.sleep(100);
               PongMain.reset(0);
            }
         }
         
         //secret color mode key (shhhhhh)
         if (event.getKeyChar()=='c'||event.getKeyChar()=='C'){
            if (!PongMain.diff.equals("Easy")){
               PongMain.colourz = !PongMain.colourz;
               if (PongMain.colourz==false){
                  PongMain.textField.setForeground(Color.WHITE);
               }
            }
         }
      }
      //pause key
      if (event.getKeyChar()=='p'||event.getKeyChar()=='P'){
         PongMain.lock = !PongMain.lock;
         PongMain.locked = PongMain.lock;               
         try{
            PongMain.winMid = PongMain.textField.getLocationOnScreen();
            Robot robo = new Robot();
            //middle of window
            PongMain.winMid = PongMain.textField.getLocationOnScreen();
            robo.mouseMove((int)PongMain.winMid.getX()+((63+465*1)/2), (int)PongMain.winMid.getY()+((45+312*1)/2)); //move mouse back to screen center
         }catch(AWTException e2){e2.printStackTrace();}
         if (PongMain.lock){
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff+" [PAUSED]");
         }else{
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
         }
      }
      
      //exit key
      if (event.getKeyChar()=='z'||event.getKeyChar()=='Z'){
         PongMain.exit();
      }
      
      //back to menu key
      if (event.getKeyChar()=='m'||event.getKeyChar()=='M'){
         if (!PongMain.inMenu){
            //stop
            while (PongMain.lock){Utils.sleep(1);}
            PongMain.lock = true;
            Utils.sleep(750);
            PongMain.jframe.dispose(); //get rid of jframe and jtextfield
            //reset vars
            PongMain.jframe = new JFrame("PaddleBall "+"v0.0"+" | Score: 0"); //redo textField
            PongMain.textField = new JTextArea(); //redo jframe
            PongMain.score = 0; //reset score
            PongMain.colourz = false; //reset color option
            PongMain.inMenu = true;
         }
      }
   }
}

//mouse listener
class MouseMovement extends MouseInputAdapter{
   public static int move = 0;
   @Override
   public void mouseMoved(MouseEvent e){
      System.out.println("mouseMoved");
      if (!PongMain.locked){
         PongMain.mousePosX = e.getX();
         if (!PongMain.inMenu){
            while(PongMain.lock){Utils.sleep(1);}
            if (PongMain.mousePosX>500||PongMain.mousePosX<11){ //check to see if mouse is close to the edge of window
               try{
                  PongMain.winMid = PongMain.textField.getLocationOnScreen();
                  Robot robo = new Robot();
                  //middle of window
                  PongMain.winMid = PongMain.textField.getLocationOnScreen();
                  robo.mouseMove((int)PongMain.winMid.getX()+((63+465*1)/2), (int)PongMain.winMid.getY()+((45+312*1)/2)); //move mouse back to screen center
               }catch(AWTException e2){e2.printStackTrace();}
            }
            if (PongMain.mousePosX>PongMain.mousePosXPrev-1){move=1;}
            else if (PongMain.mousePosX<PongMain.mousePosXPrev+1){move=-1;}
            else{move=0;}
            PongMain.moveChr2(move);
         }
         PongMain.mousePosXPrev = PongMain.mousePosX;
      }
   }
   
   public void mouseClicked(MouseEvent e){ //for menu options
      System.out.println("mouseClicked");
      if (PongMain.inMenu){
         int mPointX = e.getX();
         int mPointY = e.getY();
         //option one (easy difficulty)
         if (mPointX>=85 && mPointX<=319 && mPointY>=151 && mPointY<=174){
            PongMain.maxSpeed = 150;
            PongMain.minSpeed = 100;
            PongMain.inMenu = false;
            PongMain.pen = 2;
            PongMain.diff = "Easy";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
            System.out.println("Difficulty: "+PongMain.diff);
            PongMain.textField.setForeground(Color.PINK);
         }
         
         //option 2 (medium difficulty)
         if (mPointX>=85 && mPointX<=408 && mPointY>=179 && mPointY<=200){
            PongMain.maxSpeed = 60;
            PongMain.minSpeed = 40;
            PongMain.inMenu = false;
            PongMain.pen = 3;
            PongMain.diff = "Medium";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
            System.out.println("Difficulty: "+PongMain.diff);
         }
         
         //hidden option 3 (ultra difficulty)
         if (mPointX>=217 && mPointX<=265 && mPointY>=30 && mPointY<=47){
            PongMain.maxSpeed = 30;
            PongMain.minSpeed = 25;
            PongMain.inMenu = false;
            PongMain.pen = 5;
            PongMain.diff = "Ultra";
            PongMain.jframe.setTitle("PaddleBall "+PongMain.ver+" | Score: "+PongMain.score+" | Difficulty: "+PongMain.diff);
            System.out.println("Difficulty: "+PongMain.diff);
         }
      }
   }
}

class Utils{
   public static void sleep(int time){ //sleep n milliseconds
      try{Thread.sleep(time);}
      catch(InterruptedException ie){}
   }
}