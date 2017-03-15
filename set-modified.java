/************************************************************************
 *
 * Set.java
 *
 *    Set is a registered trademark of Set Enterprises, Inc.
 *    27 February 2000
 *
 ************************************************************************/

import java.applet.*; 
import java.awt.*;    
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.util.*;
import java.lang.*;
//import java.lang.Object.*;
//import java.lang.Integer.*;
//import java.lang.Number.*;

/*
 * image name = "shading * 27 + shape * 9 + color * 3 + number + 1".gif
 * for zero indexed variables.
 */

public class Set extends Applet
{
    private static final int NUM_CARDS = 81;
    private static final int NUM_COLS = 3;
    private static final int INIT_NUM_ROWS = 4;
    private static final int NUM_ROWS = 7;
    private static final int NUM_LOCATIONS = NUM_ROWS * NUM_COLS;
    private static final int IMG_W = 123;
    private static final int IMG_H = 80;
    private static final int BORDER_SIZE = 5;

    private static int num_selected = 0;
    private static int num_todraw = 0;

    private static Image[] image = new Image[NUM_CARDS];
    private Cards c = new Cards(NUM_CARDS);
    private Location[] locs = new Location[NUM_LOCATIONS];

    private Color bgcolor = Color.white;
    private Color txtcolor = Color.black;

    //private TextArea t = new TextArea("Hello", 5, 40);
    private Location button;

    private boolean EODtag;
   
    public void init()
    {
        num_todraw = num_selected = 0;
        this.setBackground(bgcolor);
        //image = this.getImage(this.getDocumentBase(),
        //this.getParameter("image"));
        this.addMouseListener(new Listener());
        EODtag = false;

        //t.setLocation(400,0);
   
        for (int i = 1; i <= NUM_CARDS; i++) {
            image[i-1] = this.getImage(this.getDocumentBase(),
                                       "pics/" + i + ".gif");
        }

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                locs[i*NUM_COLS + j] = new Location(j*(IMG_W + BORDER_SIZE*2),
                                                    i*(IMG_H + BORDER_SIZE*2),
                                                    IMG_W,IMG_H,
                                                    BORDER_SIZE,
                                                    0);
            }
        }

        for (int i = 0; i < INIT_NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                locs[i*NUM_COLS + j].SetImage(c.NextCard());
                //locs[i*NUM_COLS + j].SetImage(i*NUM_COLS+j);
                locs[i*NUM_COLS + j].dodraw();
            }
        }
        deal();
        button = new Location(400, 200, 30, 30, 0, 0);
    }
    public void destroy()
    {
        for (int i = 0; i < NUM_CARDS; i++) {
            image[i].flush();
        }
    }
    public void paint(Graphics g)
    {
        Message("Copyright Feb, 2000 Weicon Conan Yuan.",
                "All Rights Reserved",7);
        for (int i = 0; i < NUM_LOCATIONS; i++) {
            locs[i].draw(g,this);
            g.setColor(txtcolor);
            if (locs[i].ToDraw()) {
                g.drawString("" + i, locs[i].GetX(), locs[i].GetY() + 10);
            }
        }

        g.setColor(Color.red);
        g.drawRect(400,200,30,30);
        g.fillRect(400,200,30,30);
        Message("Cheat", 4);
    }
    public void update(Graphics g)
    {
        paint(g);
    }

    public void deal()
    {
        if (c.EOD() && !SetExists() && EODtag) {
            EODtag = false;
            Message("Shuffle", 2);
            int temp = num_todraw;
            for (int i = 0; i < temp; i++) {
                locs[i].dontdraw();
            }
            c.shuffle();
        } else if (c.EOD() && !SetExists()) {
            Message("End of Deck: No More Sets", "Click Mouse to Deal Again", 2);
            EODtag = true;
            return;
        }

        while( !c.EOD() && ((num_todraw < INIT_NUM_ROWS * NUM_COLS) ||
                            (!SetExists()) ))
        {
            int temp = num_todraw;

            locs[temp].SetImage(c.NextCard());
            locs[temp+1].SetImage(c.NextCard());
            locs[temp+2].SetImage(c.NextCard());
            locs[temp].dodraw();
            locs[temp+1].dodraw();
            locs[temp+2].dodraw();
        }

        if (c.EOD()) {
            Message("End of Deck", 2);
        }
    }

    public void ShowSet()
    {
        String s = "";
        for(int i = 0; i < num_todraw; i++) {
            for (int j = i+1; j < num_todraw; j++) {
                for (int k = j+1; k < num_todraw; k++) {
                    if (IsSet(locs[i].GetImage(),
                              locs[j].GetImage(),
                              locs[k].GetImage()))
                    {
                        s += i + " " + j + " " + k + "; ";
                        //Message(i + " " + j + " " + k, 3);
                        //return;
                    }
                }
            }
        }
        Message(s, 3);
        return;
    }
   
    public void ClearSelected()
    {
        if (num_selected == 0) return;
        for (int i = 0; i < NUM_LOCATIONS; i++) {
            locs[i].unselect();
        }
    }
   
    public void CheckSet()
    {
        String message;
        int[] s = new int[3];
        s[0] = s[1] = s[2] = 0;

        if (num_selected < 3) return;

        for (int i = 0; i < 3; i++) {
            while(!locs[s[i]].Selected() && (s[i] < num_todraw)) {
                s[i]++;
            }
            if (i < 2) s[i+1] = s[i] + 1;
        }
        /*
          while (!locs[s[0]].Selected() && (s[0] < num_todraw)) { s[0]++; }
          s[1] = s[0] + 1;
          while (!locs[s[1]].Selected() && (s[1] < num_todraw)) { s[1]++; }
          s[2] = s[1] + 1;
          while (!locs[s[2]].Selected() && (s[2] < num_todraw)) { s[2]++; }
        */
        if ((s[0] == s[1]) || (s[1] == s[2]) || (s[0] == s[2]) ||
            (s[0] >= num_todraw) || (s[1] >= num_todraw) || (s[2] >= num_todraw) )
            return;


        if (IsSet(locs[s[0]].GetImage(),
                  locs[s[1]].GetImage(),
                  locs[s[2]].GetImage()))
        {
            Message("You Found a Set!", 1);
            // redeal
            if( (num_todraw <= INIT_NUM_ROWS * NUM_COLS) &&
                !c.EOD())
            {
                // if there are the right number of cards out,
                // just deal three in their place
                for (int i = 0; i < 3; i++) {
                    locs[s[i]].SetImage(c.NextCard());
                }
            } else {
                // otherwise, don't deal any new cards, just
                // move the old cards around.
                int temp = num_todraw;
                for (int i = 0, j = temp-1; j > temp - 4; j--) {
                    if ( (j != s[0]) &&
                         (j != s[1]) &&
                         (j != s[2]) )
                        {
                            locs[s[i]].SetImage(locs[j].GetImage());
                            i++;
                        }
                    locs[j].dontdraw();
                    //locs[j].draw(getGraphics(), this);
                }
            }
        } else {
            Message("Not a Set!: "
                    + locs[s[0]].GetImage() + " "
                    + locs[s[1]].GetImage() + " "
                    + locs[s[2]].GetImage() + "Bad "
                    + message, 1);
        }
        update(this.getGraphics());
        ClearSelected();
        update(this.getGraphics());
        deal();
        update(this.getGraphics());
    }
    public static boolean IsSet(int a, int b, int c)
    {
        int mask = 1, div = 3;
        int x, y, z;
        for (int i = 0; i < 4; i++) {
            x = (a % div) / mask;
            y = (b % div) / mask;
            z = (c % div) / mask;
            if ( !(x == y && x == z) &&
                 !(x != y && x != z && y != z) )
                return false;
            mask *= 3;
            div  *= 3;
        }
        return true;
    }
    public static boolean IsSet(int a, int b, int c, String message)
    {
        int mask = 1, div = 3;
        int x, y, z;
        for (int i = 0; i < 4; i++) {
            x = (a % div) / mask;
            y = (b % div) / mask;
            z = (c % div) / mask;
            if ( !(x == y && x == z) &&
                 !(x != y && x != z && y != z) )
            {
                switch(i)
                    {
                    case 0:
                        message = "number";
                        break;
                    case 1:
                        message = "color";
                        break;
                    case 2:
                        message = "shape";
                        break;
                    case 3:
                        message = "shading";
                        break;
                    }
                return false;
            }
            mask *= 3;
            div  *= 3;
        }
        message = "Found a set";
        return true;
    }
    public boolean SetExists()
    {
        for(int i = 0; i < num_todraw; i++) {
            for (int j = i+1; j < num_todraw; j++) {
                for (int k = j+1; k < num_todraw; k++) {
                    if (IsSet(locs[i].GetImage(),
                              locs[j].GetImage(),
                              locs[k].GetImage()))
                        return true;
                }
            }
        }
        return false;
    }

    public void Message(String s, int i)
    {
        Graphics g = getGraphics();

        if (s == null) {
            g.setColor(bgcolor);
            g.drawRect(400,50 * i - 10,200,10);
            g.fillRect(400,50 * i - 10,200,10);
            return;
        }
        /*
          g.setColor(Color.blue);
          g.drawRect(400,50 * i - 10,200,10);
          g.fillRect(400,50 * i - 10,200,10);
        */
        g.setColor(txtcolor);
        g.drawString(s,400,50 * i);
    }
   
    public void Message(String s1, String s2, int i)
    {
        Graphics g = getGraphics();

        if (s1 == null || s2 == null) {
            g.setColor(bgcolor);
            g.drawRect(400,50 * i - 10,200,20);
            g.fillRect(400,50 * i - 10,200,20);
            return;
        }
        /*
          g.setColor(Color.blue);
          g.drawRect(400,50 * i - 10,200,20);
          g.fillRect(400,50 * i - 10,200,20);
        */
        g.setColor(txtcolor);
        g.drawString(s1,400,50 * i);
        g.drawString(s2,400,50 * i + 10);
    }
   
    ///////////////////////////////////////////////////////////////
    // inner classes: Listener
    ///////////////////////////////////////////////////////////////
    class Listener extends MouseAdapter
    {
        private Location lastloc;

        public void mousePressed(MouseEvent e)
        {
            Message(null,null,1);
            Message(null,null,2);
            Message(null,null,3);
            if (EODtag) {
                deal();
                update(Applet.this.getGraphics());
                return;
            }
            Location l = findloc(e);
            if (l == null) return;
            if (l == button) {
                ShowSet();
                return;
            }

            l.toggle();

            update(Applet.this.getGraphics());

            if (num_selected >= 3) {
                CheckSet();
            }
   
            lastloc = l;
        }
        //public void mouseReleased(MouseEvent e) {}

        private Location findloc(MouseEvent e)
        {
            int i, x = e.getX(), y = e.getY();
            if (button.contains(x,y)) return button;
            for (i = 0; i < NUM_LOCATIONS; i++) {
                if (locs[i].contains(x,y)) return locs[i];
            }
            return null;
        }
    }   
    ///////////////////////////////////////////////////////////////
    // inner classes: Cards
    ///////////////////////////////////////////////////////////////

    public static class Cards
    {
        private int[] deck;
        private int num_cards;
        private int current;
        private java.util.Random rand =
            new java.util.Random(System.currentTimeMillis());

        public Cards(int n)
        {
            if (n <= 0) n = 1;
            num_cards = n;
            deck = new int[n];
            for(int i = 0; i < num_cards; i++) deck[i] = i;
            shuffle();
        }
        public void shuffle()
        {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < num_cards; j++) {
                    int k = Math.abs(rand.nextInt() % num_cards);
                    /* swap */
                    int temp = deck[j];
                    deck[j]=deck[k];
                    deck[k]=temp;
                }
            }
            current = 0;
        }
        public boolean EOD()
        {
            return (current >= num_cards);
        }
        public int NextCard()
        {
            if (EOD()) shuffle();
            return deck[current++];
        }
        public static class Test
        {
            public static void main (String[] args)
            {

                if (args.length < 3) return;

                int a = java.lang.Integer.parseInt(args[0]);
                int b = java.lang.Integer.parseInt(args[1]);
                int c = java.lang.Integer.parseInt(args[2]);
                int mask = 1, div = 3;
                int x, y, z;
                //a--; b--; c--;
                for (int i = 0; i < 4; i++) {
                    x = (a % div);
                    y = (b % div);
                    z = (c % div);
                    System.out.println("mask: " + mask +
                                       " div: " + div +
                                       " x: " + x +
                                       " y: " + y +
                                       " z: " + z);
                    x /= mask;
                    y /= mask;
                    z /= mask;
                    if ( !(x == y && x == z) &&
                         !(x != y && x != z && y != z) )
                        {
                            System.out.println("mask: " + mask +
                                               " div: " + div +
                                               " x: " + x +
                                               " y: " + y +
                                               " z: " + z);
                            return;
                        }
                    mask *= 3;
                    div  *= 3;
                }
                return;

                /*
                  if (IsSet(java.lang.Integer.parseInt(args[0]),
                  java.lang.Integer.parseInt(args[1]),
                  java.lang.Integer.parseInt(args[2])))
                  {
                  System.out.println("YES");
                  } else
                  {
                  System.out.println("NO");
                  }

                  Cards c = new Cards(21);
                  for (int j = 0; j < 2; j++)
                  for (int i = 0; i < 21; i++)
                  {
                  System.out.println(i + " " + c.NextCard());
                  }
                */
            }
        }
    }
   
    ///////////////////////////////////////////////////////////////
    // inner classes: Location
    ///////////////////////////////////////////////////////////////

        public class Location
        {
            // coords of upper left hand corner of location
            private int x, y;
            // width and height of image
            private int w, h;
            // amount of border around the image
            private int border;
            // the image is the index of the image array
            private int im;

            private boolean selected;
            private boolean todraw;
            final Color unselected_color = Color.lightGray;
            final Color selected_color = Color.yellow;

            public Location(int upper_left_x, int upper_left_y,
                            int width, int height,
                            int border_size, int image_index)
            {
                selected = false;
                todraw = false;
                x = upper_left_x;
                y = upper_left_y;
                w = width;
                h = height;
                border = border_size;
                im = image_index;
            }

            public void draw(Graphics g, ImageObserver ob)
            {
                if (!todraw) {
                    g.setColor(bgcolor);
                    g.drawRect(x,y,w+2*border,h+2*border);
                    g.fillRect(x,y,w+2*border,h+2*border);
                    return;
                }
                if (selected)
                    g.setColor(selected_color);
                else
                    g.setColor(unselected_color);
   
                g.drawRect(x,y,w+2*border,h+2*border);
                g.fillRect(x,y,w+2*border,h+2*border);
                g.drawImage(image[im], x+border, y+border, ob);
   
                //g.setColor(Color.black);
                //g.drawString("" + im,x+5,y+5);
            }

            public void SetImage(int img) { im = img; }
            public int GetImage() { return im; }
            public boolean Selected() { return selected; }
            public boolean ToDraw() { return todraw; }

            public int GetX() { return x; }
            public int GetY() { return y; }

            public void select()
            {
                if (selected) return;
                selected = true;
                num_selected++;
            }
            public void unselect()
            {
                if (!selected) return;
                selected = false;
                num_selected--;
            }
            public void toggle()
            {
                if (selected) num_selected -= 2;
                selected = !selected;
                num_selected++;
            }

            public void dodraw()
            {
                if (todraw) return;
                todraw = true;
                num_todraw++;
            }
            public void dontdraw()
            {
                if(!todraw) return;
                todraw = false;
                num_todraw--;
            }
            public void toggle_draw()
            {
                if(todraw) num_todraw -= 2;
                todraw = !todraw;
                num_todraw++;
            }

            public boolean contains(int a, int b)
            {
                return (a > this.x          &&
                        a < this.x + this.w &&
                        b > this.y          &&
                        b < this.y + this.h);
            }

            public void centerText(String s1, String s2, Graphics g, Color c,
                                   int x, int y, int w, int h)
            {
                //locs[0].centerText(s1, s2, this.getGraphics(),
                //Color.white, 400,0,200,50);
                //g.setXORMode(unselected_color);
                //centerText("pic" + im, null, g, Color.black, x, y, w, h);

                Font f = g.getFont();
                FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
                int ascent = fm.getAscent();
                int height = fm.getHeight();
                int width1 = 0, width2 = 0, x0=0, x1=0, y0=0, y1=0;
                width1 = fm.stringWidth(s1);
                if(s2 != null) width2 = fm.stringWidth(s2);
                x0 = x + (w - width1)/2;
                x0 = x + (w - width2)/2;
                if (s2 == null)
                    y0 = y + (h - height)/2 + ascent;
                else {
                    y0 = y + (h - (int)(height*2.2))/2 + ascent;
                    y1 = y0 + (int)(height*1.2);
                }
                g.setColor(c);
                g.drawString(s1,x0,y0);
                if (s2 != null) g.drawString(s2,x1,y1);
            }
        }   
}
