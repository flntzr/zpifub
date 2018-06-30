import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MapFrame extends JFrame {
	
	BufferedImage image;
	int[] pixel;
	int mapSize;
	JPanel panel = new JPanel() {
		@Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
	};
	
	public MapFrame(int mapsize) {
		this.mapSize = mapsize;
		image = new BufferedImage(this.mapSize*2, this.mapSize, BufferedImage.TYPE_INT_RGB);
		pixel = new int[this.mapSize*2*this.mapSize];

		image.getRGB(0, 0, image.getWidth(), image.getHeight(),pixel,0, image.getWidth());
//		for(int i = 0; i<pixel.length; i++){
//			pixel[i] = 255<<8;
//		}
		this.add(panel);
		this.setSize(new Dimension(this.mapSize*2+20,this.mapSize+20));
	}
	
	public void setPixel(int x, int y, int color){
		pixel[(y*this.mapSize*2) + x] = color;
	}
	
	public void invalidate() {
		image.setRGB(0, 0, image.getWidth(), image.getHeight(),pixel,0, image.getWidth());
		panel.repaint();
	}
	
	
	
}
