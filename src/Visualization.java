import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Visualization extends JPanel{

	private static final int TOWERHEIGHT = 500;
	private static final int TOWERWIDTH = 75;
	private Map<String, Color> majorColors;
	private Cohort cohort;
	
	private BufferedImage canvas;
	int height;
	int width;
	public Visualization(int width, int height) {

		JFileChooser chooser = new JFileChooser();
		File selectedFile = new File("CSCohortTest.xlsx");
		/*if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
		selectedFile = chooser.getSelectedFile();
		}*/
		cohort = new Cohort(selectedFile);
		constructColorMap();
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.height=height;
        this.width=width;
        fillCanvas(Color.WHITE);
        towerConstruction(cohort.getTotalMajorCountsInitial(), cohort.getTotalMajorCountsPerYear(), cohort.getTotalStudents().size());
    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }
    
    public void putPixel(int x, int y) {
    	canvas.setRGB(x, y, Color.BLACK.getRGB());
    }
    
    public void drawRectangle(int x0, int y0, int x, int y, Color c) {
    	for(int dx = x0; dx < x;dx++) 
    	{	
    		for(int dy = y0; dy > y; dy--)
    		{
    			canvas.setRGB(dx, dy, c.getRGB());
    		}
    	}	
    }
	
	public void putLineDDA(int x0, int y0, int x, int y, Color c) {
		canvas.setRGB(x0, y0, c.getRGB());
		canvas.setRGB(x, y, c.getRGB());
		int dx = x-x0;
		int dy = y-y0;
		int steps = 0;
		float xT = x0;
		float yT = y0;
		if(Math.abs(dx) > Math.abs(dy)) 
			steps = dx;
		else
			steps = dy;
		float xIncrement = ((float)dx)/steps;
		float yIncrement = ((float)dy)/steps;
		for(int v=0; v < steps; v++)
		{
		   xT = xT + xIncrement;
		   yT = yT + yIncrement;
		   canvas.setRGB(Math.round(xT), Math.round(yT),c.getRGB());
		}
		repaint();
	}
	
	public void constructSankeyTower(int x0, int y0,int total, Map<String, Integer> counts) {
		for(String s: counts.keySet()) {
			Color c = majorColors.get(s);
			int count = counts.get(s);
			float percent = ((float)count)/total;
			//System.out.println(count);
			int dy = Math.round(percent*TOWERHEIGHT);
			drawRectangle(x0,y0,x0+TOWERWIDTH,y0-dy,c);
			y0-=dy;
		}
	}
	
	public void towerConstruction(Map<String, Integer> initialCounts, ArrayList<Map<String, Integer>> counts, int total) {
		constructSankeyTower(50, 700, total,initialCounts);
		for(int pos = 0; pos < counts.size(); pos++) {
			constructSankeyTower(250+200*pos, 700,total, counts.get(pos));
		}
	}
	
	
	public void constructColorMap() {
		majorColors = new HashMap<String, Color>();
		for(String s: cohort.getMajors()) {
			Color c;
			if(cohort.getDepartment().hasMajor(s)) {
				majorColors.put(s, new Color(0,((int)(Math.random()*200+50)),0));
			} else if(Department.isCNS(s)) {
				majorColors.put(s, new Color(0,0,((int)(Math.random()*200+50))));
			} else if(Department.isMajor(s)) {
				majorColors.put(s, new Color(((int)(Math.random()*200+50)),0,0));
			}
		}
		majorColors.put(Department.DISMISSED, Color.BLACK);
		majorColors.put(Department.DROPOUT, Color.GRAY);
	}
	
	
	public void fillCanvas(Color c) {
		for(int x = 0; x < width;x++) 
			for(int y = 0; y < height; y++)
				canvas.setRGB(x, y, c.getRGB());
		repaint();
	}
	
	 public static Color bleach(Color color, float amount)
	  {
	    int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
	    int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
	    int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
	    return new Color(red, green, blue);
	  }
	
	public static void main(String[] args) {
		Visualization visual = new Visualization(1600, 800);
		JFrame frame = new JFrame();
		frame.add(visual);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	
}
