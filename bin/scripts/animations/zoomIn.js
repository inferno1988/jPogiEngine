importPackage(java.awt);
importPackage(java.awt.image);
importPackage(java.awt.geom);
importPackage(java.util);
importPackage(net.ifno.com.ua.AnimationEngine);

var WIDTH = 100;
var HEIGTH = 100;
var RECT_SIZE = 0;
var STEP = 10;
var rect = new Rectangle2D.Double(0, 0, RECT_SIZE, RECT_SIZE);
var animation = new Animation();

for (var i = 0; i < WIDTH/STEP-1; i++) {
RECT_SIZE+=STEP;
var bi = new BufferedImage(WIDTH, HEIGTH, BufferedImage.TYPE_INT_ARGB)
var g2d = bi.createGraphics();
g2d.setColor(Color.RED);
rect.setRect((WIDTH/2)-(RECT_SIZE/2), (HEIGTH/2)-(RECT_SIZE/2), RECT_SIZE, RECT_SIZE);
g2d.draw(rect);
g2d.dispose();
var frame = new net.ifno.com.ua.AnimationEngine.Frame(bi, 20);
animation.addFrame(frame);
}

var result = animation;

