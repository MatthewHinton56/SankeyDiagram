import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;



public class SankeyFrame extends JFrame{

	private Visualization visual;

	public SankeyFrame() {
		visual = new Visualization(1600, 800);
		JFrame frame = new JFrame("Sankey Visualization");
		frame.add(visual);
		frame.setJMenuBar(new VisualizationMenu(visual.cohort.getMajors(),visual.cohort.getDepartment(),visual,this));
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	}
	
	public static void main(String[] args) {
		new SankeyFrame();
	}
	
	public void saveImage(String fileName) {
		Container c = visual;
		BufferedImage im = new BufferedImage(1600, 800, BufferedImage.TYPE_INT_ARGB);
		c.paint(im.getGraphics());
		try {
			ImageIO.write(im, "PNG", new File(fileName+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
