import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.round;

public class Example01 extends Frame {
    public static void main(String[] args) {
        new Example01();
    }

    public Example01() {
        super("Java 2D Example01");

        setSize(1600,600);

        setVisible(true);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                dispose(); System.exit(0);
            }
        });
    }

    void DDADrawLine(Graphics g, float x1, float y1, float x2, float y2)
    {
        Graphics2D graphics2D = (Graphics2D) g;
        float xdiff = x2-x1;
        float ydiff = y2-y1;
        float dx, dy; // change in x & y at each iteration.
        float slope  = 1; // slope should be a float or a double.
        int numOfSteps = 0;
        if ( y1  == y2  )
        {
            if ( x1 == x2 ) // error! not a line.
                return;
            slope = 0;
            dx = 1;
            dy = 0;
            numOfSteps = (int) (xdiff/dx);
        }
        else if (  x1 == x2 )
        {
            slope = 2; // vertical lines have no slopes...
            dx = 0;
            dy = 1;
            numOfSteps = (int) (ydiff/dy);
        }
        else
        {
            slope = ydiff/xdiff; // Sorry I have reversed the slope as usually its calculated this way.
            dx = 1;
            dy = slope*dx;
            numOfSteps = (int) ((ydiff/dy > xdiff/dx) ? ydiff/dy : xdiff/dx);
        }

        for( int i = 0; i <= numOfSteps; i++ )
        {
            graphics2D.fillRect((int) x1 + 5,(int) y1 + 5, 5, 5);
            x1 += dx;
            y1 += dy;
        }

    }

    public void DDASymmetricDrawLine(Graphics g, float x1, float y1, float x2, float y2)
    {
        double dx,dy,steps,x,y,k;
        double xc,yc;
        dx=x2-x1;
        dy=y2-y1;
        if(Math.abs(dx)>Math.abs(dy))
            steps=Math.abs(dx);
        else
            steps=Math.abs(dy);
        xc=(dx/steps);
        yc=(dy/steps);
        x=x1;
        y=y1;
        for(k=1;k<=steps;k++)
        {
            x=x+xc;
            y=y+yc;
            g.fillRect((int)x,(int)y,5,5);
        }
    }

    public void BrasenheiLine(Graphics g, float x1, float y1, float x2, float y2){
        float x,y,k;
        double dx,dy,p;
        dx=Math.abs(x2-x1);
        dy=Math.abs(y2-y1);
        x=x1;
        y=y1;
        p=2*dy-dx;
        for(k=0;k<dx;k++) {
            if (p < 0) {
                g.fillRect((int)x++,(int) y, 5, 5);
                p = p + (2 * dy);
            } else {
                g.fillRect((int)x++,(int) y++, 5, 5);
                p = p + (2 * (dy - dx));
            }
        }
    }

    void plotCircle(Graphics g, int xm, int ym, int r)
    {
        int x = -r, y = 0, err = 2-2*r; /* II. Quadrant */
        do {
            g.fillRect(xm-x, ym+y, 5, 5); /*   I. Quadrant */
            g.fillRect(xm-y, ym-x, 5, 5); /*  II. Quadrant */
            g.fillRect(xm+x, ym-y, 5, 5); /* III. Quadrant */
            g.fillRect(xm+y, ym+x, 5, 5); /*  IV. Quadrant */
            r = err;
            if (r <= y) err += ++y*2+1;           /* e_xy+e_y < 0 */
            if (r > x || err > y) err += ++x*2+1; /* e_xy+e_x > 0 or no 2nd y-step */
        } while (x < 0);
    }

    void plotEllipseRect(Graphics g, int x0, int y0, int x1, int y1)
    {
        int a = Math.abs(x1-x0), b = Math.abs(y1-y0), b1 = b&1; /* values of diameter */
        long dx = 4*(1-a)*b*b, dy = 4*(b1+1)*a*a; /* error increment */
        long err = dx+dy+b1*a*a, e2; /* error of 1.step */

        if (x0 > x1) { x0 = x1; x1 += a; } /* if called with swapped points */
        if (y0 > y1) y0 = y1; /* .. exchange them */
        y0 += (b+1)/2; y1 = y0-b1;   /* starting pixel */
        a *= 8*a; b1 = 8*b*b;

        do {
            g.fillRect(x1, y0, 5, 5); /*   I. Quadrant */
            g.fillRect(x0, y0, 5, 5); /*  II. Quadrant */
            g.fillRect(x0, y1, 5, 5); /* III. Quadrant */
            g.fillRect(x1, y1, 5, 5); /*  IV. Quadrant */
            e2 = 2*err;
            if (e2 <= dy) { y0++; y1--; err += dy += a; }  /* y step */
            if (e2 >= dx || 2*err > dy) { x0++; x1--; err += dx += b1; } /* x step */
        } while (x0 <= x1);

        while (y0-y1 < b) {  /* too early stop of flat ellipses a=1 */
            g.fillRect(x0-1, y0, 5, 5); /* -> finish tip of ellipse */
            g.fillRect(x1+1, y0++, 5, 5);
            g.fillRect(x0-1, y1, 5, 5);
            g.fillRect(x1+1, y1--, 5, 5);
        }
    }

    void plot(Graphics2D g, double x, double y, double c) {
        g.setColor(new Color(0f, 0f, 0f, (float)c));
        g.fillOval((int) x, (int) y, 5, 5);
    }

    int ipart(double x) {
        return (int) x;
    }

    double fpart(double x) {
        return x - floor(x);
    }

    double rfpart(double x) {
        return 1.0 - fpart(x);
    }

    void WULine(Graphics2D g, double x0, double y0, double x1, double y1) {

        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        if (steep)
            WULine(g, y0, x0, y1, x1);

        if (x0 > x1)
            WULine(g, x1, y1, x0, y0);

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;

        // handle first endpoint
        double xend = round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = rfpart(x0 + 0.5);
        double xpxl1 = xend; // this will be used in the main loop
        double ypxl1 = ipart(yend);

        if (steep) {
            plot(g, ypxl1, xpxl1, rfpart(yend) * xgap);
            plot(g, ypxl1 + 1, xpxl1, fpart(yend) * xgap);
        } else {
            plot(g, xpxl1, ypxl1, rfpart(yend) * xgap);
            plot(g, xpxl1, ypxl1 + 1, fpart(yend) * xgap);
        }

        // first y-intersection for the main loop
        double intery = yend + gradient;

        // handle second endpoint
        xend = round(x1);
        yend = y1 + gradient * (xend - x1);
        xgap = fpart(x1 + 0.5);
        double xpxl2 = xend; // this will be used in the main loop
        double ypxl2 = ipart(yend);

        if (steep) {
            plot(g, ypxl2, xpxl2, rfpart(yend) * xgap);
            plot(g, ypxl2 + 1, xpxl2, fpart(yend) * xgap);
        } else {
            plot(g, xpxl2, ypxl2, rfpart(yend) * xgap);
            plot(g, xpxl2, ypxl2 + 1, fpart(yend) * xgap);
        }

        // main loop
        for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(g, ipart(intery), x, rfpart(intery));
                plot(g, ipart(intery) + 1, x, fpart(intery));
            } else {
                plot(g, x, ipart(intery), rfpart(intery));
                plot(g, x, ipart(intery) + 1, fpart(intery));
            }
            intery = intery + gradient;
        }
    }

    public void paint(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        DDADrawLine(g, 100, 100, 100, 300);
        DDADrawLine(g, 100, 100, 200, 100);
        DDADrawLine(g, 200, 200, 200, 300);
        DDADrawLine(g, 200, 300, 300, 300);
        DDADrawLine(g, 300, 200, 300, 400);
        DDADrawLine(g, 200, 400, 300, 400);
        DDADrawLine(g, 200, 400, 300, 400);
        DDADrawLine(g, 350, 300, 450, 300);
        DDADrawLine(g, 375, 200, 375, 300);
        DDADrawLine(g, 425, 200, 425, 300);
        DDADrawLine(g, 375, 200, 425, 200);
        DDADrawLine(g, 350, 300, 350, 325);
        DDADrawLine(g, 450, 300, 450, 325);
        DDADrawLine(g, 500, 200, 500, 300);
        DDADrawLine(g, 500, 175, 500, 180);
        DDADrawLine(g, 550, 200, 550, 300);
        DDADrawLine(g, 550, 200, 550, 300);
        DDADrawLine(g, 550, 250, 600, 200);
        DDADrawLine(g, 550, 250, 600, 300);
        plotEllipseRect(g, 625, 205, 675, 305);
        DDADrawLine(g, 700, 175, 700, 300);
        DDADrawLine(g, 700, 175, 725, 175);
        DDADrawLine(g, 725, 175, 725, 225);
        DDADrawLine(g, 700, 225, 750, 225);
        DDADrawLine(g, 750, 225, 750, 300);
        DDADrawLine(g, 700, 300, 750, 300);
    }
}
