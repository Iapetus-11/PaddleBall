import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.Math;
import java.awt.AWTException;

public class pongMain{
   public static JTextArea textField = new JTextArea();
   public static JFrame jframe = new JFrame("PaddleBall by Olim One Studios | Score: 0");
   public static char bg = ' ';
   public static char c = '#';
   public static String chr = ""+c+c+c+c+c+c+c+c+c+c+c+c;//12
   public static String ball = "O";
   public static char newLine = '\n';
   public static boolean lock = false;
   public static int score = 0;
   public static boolean inMenu = true;
   public static int minSpeed = 0;
   public static int maxSpeed = 0;
   public static int pen = 2;
   //secret color mode stuff
   public static boolean colourz = false;
   public static Color red = new Color(255, 0, 0);
   public static Color orange = new Color(255, 165, 0);
   public static Color yellow = new Color(255, 255, 0);
   public static Color green = new Color(0, 255, 0);
   public static Color blue = new Color(0, 0, 255);
   public static Color indigo = new Color(100, 0, 155);
   public static Color violet = new Color(128, 0, 128);
   public static Color[] colors = new Color[7];
   public static int cAt = 0;
   public static int mousePosX = 0;
   public static int mousePosXPrev = 0;
   public static int paddlePos = 0;
   public static Point winMid = new Point(0, 0);
   public static String diff = "";
      
   public static void main(String[] args){
      System.out.println("main");
      try{initDisp();}catch(IOException e){}
      menu();
   }
   
   public static void exit(){
      System.out.println("exit");
      lock = true;
      utils.sleep(750);
      System.exit(0);
      System.exit(0);
   }
   
   public BufferedImage getImg(String file) throws Exception{
      return ImageIO.read(this.getClass().getResource(file));
   }
   
   //setup window and textarea
   public static void initDisp()throws IOException{
      System.out.println("initDisp");
      //File image = new File("icon.png");
      //Image winIcon = ImageIO.read(image);
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
      Color white = new Color(230, 230, 230);
      textField.setSelectionColor(Color.BLACK);
      textField.setSelectedTextColor(Color.WHITE);
      textField.setBackground(Color.BLACK);
      textField.setForeground(Color.WHITE);
      textField.addKeyListener(new MKeyListener());
      textField.setEditable(false);
      textField.setFont(font);
      jframe.add(textField);
      Dimension minSize = new Dimension(63+465*1, 45+312*1);
      jframe.setSize(63+465*1, 45+312*1);
      jframe.setMinimumSize(minSize);
      jframe.setVisible(true);
      jframe.setBackground(Color.BLACK);
      jframe.setAlwaysOnTop(false);
      try{jframe.setIconImage(new pongMain().getImg("icon.png"));}catch(Exception e){}
      jframe.setCursor(Cursor.DEFAULT_CURSOR);
      jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      textField.addMouseMotionListener(new MouseMovement());
      textField.addMouseListener(new MouseMovement());
      jframe.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent event) {
            jframe.dispose();
            exit();
         }
      });
   }
   
   public static void menu(){
      System.out.println("menu");
      inMenu = true;
      BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
      Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14*1);
      Font font2 = new Font(Font.MONOSPACED, Font.PLAIN, 18*1);
      textField.setFont(font2);
      String welcome = "\n    PaddleBall - an Olim One Studios game\n\n\n             Select Difficulty:\n\n        1. Can I play, Daddy?\n        2. It's paddle breaking time!";
      textField.setText(welcome);
      while(inMenu){utils.sleep(1);}
      textField.setFont(font);
      String display = "";
      jframe.setCursor(blankCursor);
      initGrid(display);
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
      bg = pongMain.bg;
      display = "";
      while (i!=1088-pongMain.chr.length()){
         if (i==792+64*2+1){
            display+=pongMain.chr;
            i++;
         }else if (i==64+31){
            display+=ball;
            i++;
         }else{
            display+=bg;
            i++;
         }
      }
      draw(display);
      return display;
   }
   
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
      pongMain.textField.setText(display);
   }
   
   public static void moveChr(int x, int y){
      System.out.println("moveChr");
      while (lock){utils.sleep(1);}
      lock = true;
      String display = pongMain.textField.getText();
      int pos = display.indexOf(chr);
      String disp1;
      String disp2;
      
      //right
      if (x>1){
         if (pos<=957&&pos!=-1){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+5);
            disp2 = display.substring(pos+8);
            display = disp1+chr+disp2;
         }else if (pos==958&&pos!=-1){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+4);
            disp2 = display.substring(pos+7);
            display = disp1+chr+disp2;
         }else if (pos==959&&pos!=-1){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+3);
            disp2 = display.substring(pos+6);
            display = disp1+chr+disp2;
         }else if (pos==960&&pos!=-1){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+2);
            disp2 = display.substring(pos+5);
            display = disp1+chr+disp2;
         }else if (pos==961&&pos!=-1){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+1);
            disp2 = display.substring(pos+4);
            display = disp1+chr+disp2;
         }
      }
      
      //left
      if (x<0){
         if (pos>=915){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-5);
            disp2 = display.substring(pos-2);
            display = disp1+chr+disp2;
         }else if (pos==914){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-4);
            disp2 = display.substring(pos-1);
            display = disp1+chr+disp2;
         }else if (pos==913){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-3);
            disp2 = display.substring(pos);
            display = disp1+chr+disp2;
         }else if (pos==912){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-2);
            disp2 = display.substring(pos+1);
            display = disp1+chr+disp2;
         }else if (pos==911){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-1);
            disp2 = display.substring(pos+2);
            display = disp1+chr+disp2;
         }
      }
      paddlePos = pos;
      pongMain.textField.setText(display);
      lock = false;
   }
   
   public static void moveChr2(int x){
      System.out.println("moveChr2");
      while (lock){utils.sleep(1);}
      lock = true;
      String display = pongMain.textField.getText();
      int pos = display.indexOf(chr);
      String disp1;
      String disp2;
      
      //right
      if (x>0){
         if (pos<962&&pos!=-1&&display.charAt(pos+1)!=newLine&&display.charAt(pos+1)!=ball.charAt(0)){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos+1);
            disp2 = display.substring(pos+4);
            display = disp1+chr+disp2;
         }
      }
      
      //left
      if (x<0){
         if (pos>910&&pos!=-1&&display.charAt(pos-1)!=newLine&&display.charAt(pos-1)!=ball.charAt(0)){
            disp1 = display.substring(0, pos);
            disp2 = display.substring(pos+pongMain.chr.length());
            display = disp1+pongMain.bg+pongMain.bg+pongMain.bg+disp2;
            disp1 = display.substring(0, pos-1);
            disp2 = display.substring(pos+2);
            display = disp1+chr+disp2;
         }
      }
      paddlePos = pos;
      pongMain.textField.setText(display);
      lock = false;
   }
   
   public static void reset(int s){
      System.out.println("reset");
      lock = true;
      String display = "";
      initGrid(display);
      score = s;
      textField.setForeground(Color.WHITE);
      jframe.setTitle("PaddleBall by Olim One Studios | Score: "+score+" | Difficulty: "+diff);
      System.out.println("#### RESET ####");
      try{
         Robot robo = new Robot();
         winMid = textField.getLocationOnScreen();
         robo.mouseMove((int)pongMain.winMid.getX()+(int)((63+465*1)/2), (int)pongMain.winMid.getY()+(int)((45+312*1)/2));
      }catch(AWTException e2){e2.printStackTrace();}
      lock = false;
   }
   
   public static void moveBall(){
      System.out.println("moveBall");
      JTextArea textField = pongMain.textField;
      String display = textField.getText();
      int headingX = 0;
      int headingY = -1;
      int pos = textField.getText().indexOf(ball);
      int i = 0;
      String disp1;
      String disp2;
      utils.sleep(1000);
      int speed = 125;
      //ball movement
      while (true){
         //System.out.println(paddlePos);
         utils.sleep(speed);
         while (lock){utils.sleep(1);}
         lock = true;
         if (colourz){doColourz();}
         display = textField.getText();
         pos = display.indexOf(ball);
         if (headingY==0){headingY=-1;}
         //reset game if needed, checks for missing ball, missing paddle, checks for ball in index 0 (top left corner)
         if (pos==-1||display.indexOf(chr)==-1){
            if (!pongMain.inMenu){
               reset(score);
               utils.sleep(1000);
               display = textField.getText();
               pos = display.indexOf(ball);
               i = 0;
               headingX = 0;
               headingY = -1;
               speed = 125;
            }
         }
         
            //1st right
            if (headingX>0){
               if (display.charAt(pos+1)!=newLine){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+pongMain.ball.length());
                  display = disp1+pongMain.bg+disp2;
                  disp1 = display.substring(0, pos+1);
                  disp2 = display.substring(pos+2);
                  display = disp1+ball+disp2;
                  pos++;
               }else{
                  headingX = (int)(Math.random()*2+1)*-1;
               }
            }
            
            //2nd right
            if (headingX>0){
               if (Math.abs(headingX)>1){
                  if (display.charAt(pos+1)!=newLine){
                     disp1 = display.substring(0, pos);
                     disp2 = display.substring(pos+pongMain.ball.length());
                     display = disp1+pongMain.bg+disp2;
                     disp1 = display.substring(0, pos+1);
                     disp2 = display.substring(pos+2);
                     display = disp1+ball+disp2;
                     pos++;
                  }else{
                     headingX = (int)(Math.random()*2+1)*-1;
                  }
               }
            }
         
         //1st left
         if (headingX<0){
            if (pos>0&&display.charAt(pos-1)!=newLine){
               disp1 = display.substring(0, pos);
               disp2 = display.substring(pos+pongMain.ball.length());
               display = disp1+pongMain.bg+disp2;
               disp1 = display.substring(0, pos-1);
               disp2 = display.substring(pos);
               display = disp1+ball+disp2;
               pos--;
            }else{
               headingX = (int)(Math.random()*2+1);
            }
         }
         
         //2nd left
         if (headingX<0){
            if (Math.abs(headingX)>1){
               if (pos>0&&display.charAt(pos-1)!=newLine){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+pongMain.ball.length());
                  display = disp1+pongMain.bg+disp2;
                  disp1 = display.substring(0, pos-1);
                  disp2 = display.substring(pos);
                  display = disp1+ball+disp2;
                  pos--;
               }else{
                  headingX = (int)(Math.random()*2+1);
               }
            }
         }
         
         //down
         if (0>headingY){
            if (pos<960){
               c = pongMain.c;
               if (display.charAt(pos+65)!=c&&display.charAt(pos+64+1)!=c){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+pongMain.ball.length());
                  display = disp1+pongMain.bg+disp2;
                  disp1 = display.substring(0, pos+65);
                  disp2 = display.substring(pos+66);
                  display = disp1+ball+disp2;
               }else if (display.charAt(pos+64)==c||display.charAt(pos+64+1)==c){
                  headingY = 1;
                  headingX = ((int)(Math.random()*5)-2);
                  if (headingX==0){headingX=((int)(Math.random()*5)-2);}
                  score++;
                  jframe.setTitle("PaddleBall by Olim One Studios | Score: "+score+" | Difficulty: "+diff);
                  speed = (int)(Math.random()*(maxSpeed-minSpeed+1)+minSpeed);
                  if (Math.abs(headingX)>1){
                     speed+=15;
                  }
               }
            }else if (pos>128){
               headingY = 1;
               headingX = ((int)(Math.random()*5)-2);
               if (headingX==0){headingX=((int)(Math.random()*5-2));}
               if (headingX==0){headingX=((int)(Math.random()*5-2));}
               if (Math.abs(headingX)>1){
                  speed+=15;
               }
               score-=pen;
               jframe.setTitle("PaddleBall by Olim One Studios | Score: "+score+" | Difficulty: "+diff);
            }
         }
         
         //up
         if (0<headingY){
            if (pos-64>0){
               if (display.charAt(pos-65)!='#'){
                  disp1 = display.substring(0, pos);
                  disp2 = display.substring(pos+pongMain.ball.length());
                  display = disp1+pongMain.bg+disp2;
                  disp1 = display.substring(0, pos-65);
                  disp2 = display.substring(pos-64);
                  display = disp1+ball+disp2;
               }
            }
         }
         
         if (pos<64){
            headingY = -1;
         }
         
         //draw it
         pongMain.textField.setText(display);
         lock = false;
      }
   }
}
 
class MKeyListener extends KeyAdapter{
   @Override
   public void keyPressed(KeyEvent event){
      System.out.println("keyPressed");
      char ch = event.getKeyChar();
      //Print out key System.out.println(event.getKeyChar());
      
      if (event.getKeyCode() == KeyEvent.VK_HOME){
      System.out.println("Key codes: " + event.getKeyCode());
      }
      
      //detect movement keys
      if (!pongMain.inMenu){
         if (event.getKeyChar()=='d'){
            pongMain.moveChr(2, 0);
         }
      
         if (event.getKeyChar()=='a'){
            pongMain.moveChr(-2, 0);
         }
      }
      
      if (event.getKeyChar()=='1'&&pongMain.inMenu){
         pongMain.maxSpeed = 150;
         pongMain.minSpeed = 100;
         pongMain.inMenu = false;
         pongMain.pen = 2;
         pongMain.diff = "Easy";
         pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
      }
      
      if (event.getKeyChar()=='2'&&pongMain.inMenu){
         pongMain.maxSpeed = 60;
         pongMain.minSpeed = 40;
         pongMain.inMenu = false;
         pongMain.pen = 3;
         pongMain.diff = "Medium";
         pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
      }
      
      if (event.getKeyChar()=='0'&&pongMain.inMenu){
         pongMain.maxSpeed = 30;
         pongMain.minSpeed = 25;
         pongMain.inMenu = false;
         pongMain.pen = 5;
         pongMain.diff = "Ultra";
         pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
      }
      
      if (event.getKeyChar()=='r'){
         if (!pongMain.inMenu){
            utils.sleep(100);
            pongMain.reset(0);
         }
      }
      
      if (event.getKeyChar()=='z'){
         pongMain.exit();
      }
      
      if (event.getKeyChar()=='c'){
         pongMain.colourz = !pongMain.colourz;
         if (pongMain.colourz==false){
            pongMain.textField.setForeground(Color.WHITE);
         }
      }
   }
}

class MouseMovement extends MouseInputAdapter{
   public static int move = 0;
   @Override
   public void mouseMoved(MouseEvent e){
      System.out.println("mouseMoved");
      pongMain.mousePosX = e.getX();
      if (!pongMain.inMenu){
         while(pongMain.lock){utils.sleep(1);}
         if (pongMain.mousePosX>500||pongMain.mousePosX<11){
            try{
               pongMain.winMid = pongMain.textField.getLocationOnScreen();
               Robot robo = new Robot();
               //middle of window
               pongMain.winMid = pongMain.textField.getLocationOnScreen();
               robo.mouseMove((int)pongMain.winMid.getX()+(int)((63+465*1)/2), (int)pongMain.winMid.getY()+(int)((45+312*1)/2));
            }catch(AWTException e2){e2.printStackTrace();}
         }
         if (pongMain.mousePosX>pongMain.mousePosXPrev-1){move=1;}else if (pongMain.mousePosX<pongMain.mousePosXPrev+1){move=-1;}else{move=0;}
         pongMain.moveChr2(move);
      }
      pongMain.mousePosXPrev = pongMain.mousePosX;
   }
   
   public void mouseClicked(MouseEvent e){
      System.out.println("mouseClicked");
      if (pongMain.inMenu){
         int mPointX = e.getX();
         int mPointY = e.getY();
         //opt 1
         if (mPointX>=85&&mPointX<=319&&mPointY>=151&&mPointY<=174){
            pongMain.maxSpeed = 150;
            pongMain.minSpeed = 100;
            pongMain.inMenu = false;
            pongMain.pen = 2;
            pongMain.diff = "Easy";
            pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
            System.out.println("Difficulty: "+pongMain.diff);
         }
         
         //opt 2
         if (mPointX>=85&&mPointX<=408&&mPointY>=179&&mPointY<=200){
            pongMain.maxSpeed = 60;
            pongMain.minSpeed = 40;
            pongMain.inMenu = false;
            pongMain.pen = 3;
            pongMain.diff = "Medium";
            pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
            System.out.println("Difficulty: "+pongMain.diff);
         }
         
         //ultra
         if (mPointX>=217&&mPointX<=265&&mPointY>=30&&mPointY<=47){
            pongMain.maxSpeed = 30;
            pongMain.minSpeed = 25;
            pongMain.inMenu = false;
            pongMain.pen = 5;
            pongMain.diff = "Ultra";
            pongMain.jframe.setTitle("PaddleBall by Olim One Studios | Score: "+pongMain.score+" | Difficulty: "+pongMain.diff);
            System.out.println("Difficulty: "+pongMain.diff);
         }
      }
   }
}

class utils{
   public static void sleep(int time){
      try{Thread.sleep(time);}catch(InterruptedException ie){}
   }
}