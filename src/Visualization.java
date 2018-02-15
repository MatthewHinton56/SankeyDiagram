import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
	private String[][] majorMap;
	private ArrayList<ArrayList<Student>> studentTowers;
	
	private BufferedImage canvas;
	int height;
	int width;
	public Visualization(int width, int height) {
		this.addMouseMotionListener(new VisualMouseListener());
		JFileChooser chooser = new JFileChooser();
		File selectedFile = new File("CSCohortTest.xlsx");
		/*if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
		selectedFile = chooser.getSelectedFile();
		}*/
		cohort = new Cohort(selectedFile);
		generateStudentTowers(cohort.getTotalStudents());
		constructColorMap();
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.height=height;
        this.width=width;
        //majorMap = new String[height][width];
        fillCanvas(Color.WHITE);
        towerConstruction(cohort.getTotalMajorCountsInitial(), cohort.getTotalMajorCountsPerYear(), cohort.getTotalStudents().size());
        lineConstruction();
    }

    private void generateStudentTowers(ArrayList<Student> studentI) {
    	studentTowers = new ArrayList<ArrayList<Student>>();
		for(int i = -1; i < cohort.getTotalMajorCountsPerYear().size();i++) {
			ArrayList<Student> students = new ArrayList<Student>();
			students.addAll(studentI);
			students.sort(new StudentComparator(i, cohort.getDepartment()));
			
			studentTowers.add(students);
		}
		
	}
    
    private void lineConstruction() {
    	int total = studentTowers.get(0).size();
    	float percent = ((float)TOWERHEIGHT)/total;
    	int towerTop = 200;
		//System.out.println(count);
		int dy = Math.round(percent);
		//System.out.println(dy);
		for(int pos = 1;pos < studentTowers.size(); pos++ ) {
			int x = 59 + 200 * pos;
			int x0 = x - 134;
			for(int sPos = 0; sPos < total; sPos++) {
				if(pos == 4)
					System.out.println(sPos);
				Student s = studentTowers.get(pos).get(sPos);
				String major = s.getYear(pos-1);
				Color c = majorColors.get(major);
				int y = sPos*dy+205;
				int y0 = studentTowers.get(pos - 1).indexOf(s)*dy+200;
				/*if(y0 > y)
				{
					int temp = y;
					 y = y0;
					 y0 = temp;
				}*/
				if(pos == 4)
					System.out.println(y0 + " "+ y);
				for(int yShift = 0; yShift < 5; yShift++)
					putLineDDA(x0,y0+yShift,x,y+yShift,bleach(c,(float) .5),major);
			}
		}
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
    
    public void drawRectangle(int x0, int y0, int x, int y, Color c, String s) {
    	for(int dx = x0; dx < x;dx++) 
    	{	
    		for(int dy = y0; dy > y; dy--)
    		{
    			canvas.setRGB(dx, dy, c.getRGB());
    			majorMap[dy][dx] = s;
    		}
    	}	
    }
	
	public void putLineDDA(int x0, int y0, int x, int y, Color c, String major) {
		//System.out.println(x0 + " "+y0);
		canvas.setRGB(x0, y0, c.getRGB());
		canvas.setRGB(x, y, c.getRGB());
		int dx = x-x0;
		int dy = y-y0;
		int steps = 0;
		float xT = x0;
		float yT = y0;
		if(Math.abs(dx) > Math.abs(dy)) 
			steps = Math.abs(dx);
		else
			steps = Math.abs(dy);
		float xIncrement = ((float)dx)/steps;
		float yIncrement = ((float)dy)/steps;
		for(int v=0; v < steps; v++)
		{
		   xT = xT + xIncrement;
		   yT = yT + yIncrement;
		   if(majorMap[ Math.round(yT)][ Math.round(xT)] == null) {
			   majorMap[ Math.round(yT)][ Math.round(xT)] = major; 
			   canvas.setRGB(Math.round(xT), Math.round(yT),c.getRGB());
		   }
			   
		}
		repaint();
	}
	
	public void constructSankeyTower(int x0, int y0,int total, Map<String, Integer> counts) {
		for(String s: counts.keySet()) {
			System.out.println(s);
			Color c = majorColors.get(s);
			int count = counts.get(s);
			float percent = ((float)count)/total;
			//System.out.println(count);
			int dy = Math.round(percent*TOWERHEIGHT);
			drawRectangle(x0,y0,x0+TOWERWIDTH,y0-dy,c, s);
			y0-=dy;
		}
	}
	
	public void towerConstruction(Map<String, Integer> initialCounts, ArrayList<Map<String, Integer>> counts, int total) {
		majorMap = new String[canvas.getHeight()][canvas.getWidth()];
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
	
	private class VisualMouseListener implements MouseMotionListener {

		private String currentMajor;
		//private int year;
		
		
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			//System.out.println(x+" "+y);
			if( x >= 0 && y >= 0 && x < majorMap[0].length && y < majorMap.length) {
			String major = majorMap[y][x];
			//System.out.println(major);
			if(currentMajor != null && !currentMajor.equals(major)) {
				for(int dx = 0; dx < majorMap[0].length;dx++)
					for(int dy = 0; dy < majorMap.length;dy++ ) {
						if(currentMajor.equals(majorMap[dy][dx])) {
							canvas.setRGB(dx, dy, majorColors.get(currentMajor).getRGB());
						}
						if(major != null && major.equals(majorMap[dy][dx])) {
							canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
						}
					}
			}
			if(currentMajor == null && major != null) {
				for(int dx = 0; dx < majorMap[0].length;dx++)
					for(int dy = 0; dy < majorMap.length;dy++ ) {
						if(major.equals(majorMap[dy][dx])) {
							//System.out.println(dx+" "+dy);
							canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
						}
						
					}
			}
			currentMajor = major;
			Visualization.this.repaint();
			}
 		}
		
	}
	
}
