importClass(java.awt.Color);
importClass(java.awt.BasicStroke);
importPackage(java.awt.image);
importPackage(java.awt.geom);
importPackage(java.util);
importPackage(Packages.ua.com.ifno.pogi.AnimationEngine);

var WIDTH = 200;
var HEIGTH = 100;
var RECT_WIDTH = 0;
var RECT_HEIGTH = 0;
var WSTEP = 40;
var HSTEP = 20;
var showTime = 100;
var rect = new Rectangle2D.Double(0, 0, RECT_WIDTH, RECT_HEIGTH);
var line = new Line2D.Double(0, 0, 10, 0);
var stroke = new BasicStroke(2.0);
var animation = new Animation();

for (var i = 0; i < HEIGTH/HSTEP-1; i++) {
RECT_WIDTH+=WSTEP;
RECT_HEIGTH+=HSTEP;
var bi = new BufferedImage(WIDTH, HEIGTH, BufferedImage.TYPE_INT_ARGB)
var g2d = bi.createGraphics();
g2d.setColor(Color.RED);
g2d.setStroke(stroke);
rect.setRect((WIDTH/2)-(RECT_WIDTH/2), (HEIGTH/2)-(RECT_HEIGTH/2), RECT_WIDTH, RECT_HEIGTH);
line.setLine(rect.getX(),rect.getY(),rect.getX()+10,rect.getY());
g2d.draw(line);
line.setLine(rect.getX(),rect.getY(),rect.getX(),rect.getY()+10);
g2d.draw(line);
line.setLine(rect.getMaxX(),rect.getY(),rect.getMaxX()-10,rect.getY());
g2d.draw(line);
line.setLine(rect.getMaxX(),rect.getY(),rect.getMaxX(),rect.getY()+10);
g2d.draw(line);
line.setLine(rect.getX(),rect.getMaxY(),rect.getX(),rect.getMaxY()-10);
g2d.draw(line);
line.setLine(rect.getX(),rect.getMaxY(),rect.getX()+10,rect.getMaxY());
g2d.draw(line);
line.setLine(rect.getMaxX(),rect.getMaxY(),rect.getMaxX()-10,rect.getMaxY());
g2d.draw(line);
line.setLine(rect.getMaxX(),rect.getMaxY(),rect.getMaxX(),rect.getMaxY()-10);
g2d.draw(line);
g2d.dispose();
var frame = new Frame(bi, showTime);
animation.addFrame(frame);
}

var result = animation;

