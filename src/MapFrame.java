import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MapFrame extends JFrame {
	
	BufferedImage image;
	int[] pixel;
	JPanel panel = new JPanel() {
		@Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
	};
	
	public MapFrame() {
		image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		pixel = new int[256*256];

		image.getRGB(0, 0, image.getWidth(), image.getHeight(),pixel,0, image.getWidth());
		for(int i = 0; i<pixel.length; i++){
			pixel[i] = 255<<8;
		}
		image.setRGB(0, 0, image.getWidth(), image.getHeight(),pixel,0, image.getWidth());
		this.add(panel);
		this.setSize(new Dimension(256,256));
	}
	
	public void setPixel(int x, int y, int color){
		pixel[y*256 + x] = color;
	}
	
	public void invalidate() {
		image.setRGB(0, 0, image.getWidth(), image.getHeight(),pixel,0, image.getWidth());
		panel.repaint();
	}
	
	
	
}
