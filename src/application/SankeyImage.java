package application;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import javafx.embed.swing.SwingFXUtils;
import java.awt.color.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class SankeyImage extends BufferedImage {

	private int width,height;
	private static final int LINEDENSITY = 5;
	private static final int TOWERBOTTOM = 700;
	private static final int STARTPOINT = 50;
	private static final int TOWERHEIGHT = 500;
	private static final int TOWERWIDTH = 75;
	public String[][] majorMap;
	private ArrayList<ArrayList<Student>> studentTowers;
	private Cohort displayCohort;
	public Student[][] studentMap;
	private Map<String, Color> majorColors;
	
	public SankeyImage(int width, int height, int imageType, Cohort displayCohort) {
		super(width, height, imageType);
		this.displayCohort = displayCohort;
		generateStudentTowers(displayCohort.getTotalStudents());
		constructColorMap();
		
		this.height=height;
		this.width=width;
		//majorMap = new String[height][width];
		fillCanvas(Color.WHITE);
		towerConstruction(displayCohort.getTotalMajorCountsInitial(), displayCohort.getTotalMajorCountsPerYear(), displayCohort.getTotalStudents().size());
		lineConstruction();
		drawKeyShapes();
	}
	

	private void generateStudentTowers(ArrayList<Student> studentI) {
		studentTowers = new ArrayList<ArrayList<Student>>();
		for(int i = -1; i < displayCohort.getTotalMajorCountsPerYear().size();i++) {
			ArrayList<Student> students = new ArrayList<Student>();
			students.addAll(studentI);
			students.sort(new StudentComparator(i, displayCohort.getDepartment()));

			studentTowers.add(students);
		}

	}

	public void redraw(Cohort displayCohort) {
		generateStudentTowers(displayCohort.getTotalStudents());
		fillCanvas(Color.WHITE);
		towerConstruction(displayCohort.getTotalMajorCountsInitial(), displayCohort.getTotalMajorCountsPerYear(), displayCohort.getTotalStudents().size());
		lineConstruction();
		drawKeyShapes();
	}
	
	
	private void lineConstruction() {
		int total = studentTowers.get(0).size();
		float percent = ((float)TOWERHEIGHT)/total;
		int towerTop = TOWERBOTTOM - TOWERHEIGHT;
		//System.out.println(count);
		int dy = Math.round(percent);
		//System.out.println(dy);
		studentMap = new Student[this.getHeight()][this.getWidth()];
		constructStudentTowerMap(dy);
		for(int pos = 1;pos < studentTowers.size(); pos++ ) {
			int x = 59 + 200 * pos;
			int x0 = x - 134;
			for(int sPos = 0; sPos < total; sPos++) {
				Student s = studentTowers.get(pos).get(sPos);
				String major = s.getYear(pos-1);
				Color c = majorColors.get(major);
				int y = sPos*dy+(TOWERBOTTOM-TOWERHEIGHT+5);
				int y0 = studentTowers.get(pos - 1).indexOf(s)*dy+(TOWERBOTTOM-TOWERHEIGHT);
				/*if(y0 > y)
				{
					int temp = y;
					 y = y0;
					 y0 = temp;
				}*/
				for(int yShift = 0; yShift < LINEDENSITY; yShift++)
					putLineDDA(x0,y0+yShift,x,y+yShift,bleach(c,(float) .5),major,s);
			}
		}
	}




	private void constructStudentTowerMap(int dy) {
		//System.out.println("here");
		int x = 50;
		int y = 200;
		//System.out.println(dy);
		for(ArrayList<Student> students: studentTowers) {
			for(Student s: students) {
				for(int x0 = x; x0 < x+75; x0++)
					for(int y0 = y; y0 <y+dy;y0++)
						studentMap[y0][x0] = s;
			y+=dy;	
			}
			y=200;
			x+=200;
		}
		//System.out.println(Arrays.deepToString(studentMap));

	}

	public Dimension getPreferredSize() {
		return new Dimension(this.getWidth(), this.getHeight());
	}


	private void drawKeyShapes() {
		int x = 50;
		int y = 50;
		for(String major: displayCohort.getMajors()) {
			Color c = majorColors.get(major);
			//System.out.println(c);
			this.drawRectangle(x, y+20, x+20, y, c, major);
		//	for(int x0 = x; x0 < x+20; x0++)
			//	for(int y0 = y; y0 < y+20; y0++)
				//	majorMap[y0][x0] = major;
			y+=50;
			if(y==200) {
				y = 50;
				x+=100;
			}
		}
	}
	


	public void putPixel(int x, int y) {
		this.setRGB(x, y, Color.BLACK.getRGB());
	}

	public void drawRectangle(int x0, int y0, int x, int y, Color c, String s) {
		for(int dx = x0; dx < x;dx++) 
		{	
			for(int dy = y0; dy > y; dy--)
			{
				this.setRGB(dx, dy, c.getRGB());
				majorMap[dy][dx] = s;
			}
		}	
	}

	public void putLineDDA(int x0, int y0, int x, int y, Color c, String major, Student s) {
		//System.out.println(x0 + " "+y0);
		this.setRGB(x0, y0, c.getRGB());
		this.setRGB(x, y, c.getRGB());
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
				this.setRGB(Math.round(xT), Math.round(yT),c.getRGB());
			}
			if(studentMap[ Math.round(yT)][ Math.round(xT)] == null) {
				studentMap[ Math.round(yT)][ Math.round(xT)] = s; 

			}

		}
	}

	public void constructSankeyTower(int x0, int y0,int total, Map<String, Integer> counts) {
		for(String s: counts.keySet()) {
			//System.out.println(s);
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
		majorMap = new String[this.getHeight()][this.getWidth()];
		constructSankeyTower(STARTPOINT, TOWERBOTTOM, total,initialCounts);
		for(int pos = 0; pos < counts.size(); pos++) {
			
			constructSankeyTower(250+200*pos, TOWERBOTTOM,total, counts.get(pos));
		}
	}


	public void constructColorMap() {
		majorColors = new HashMap<String, Color>();
		for(String s: displayCohort.getMajors()) {
			Color c;
			if(displayCohort.getDepartment().hasMajor(s)) {
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

	public int majorFinder(int x) {
		int x0 = 50;
		for(int i = 0; i < 8; i++) {
			if(x0 < x && x0+75 > x)
				return i-1;
			x0+=200;
		}
		for(int q = 1; q < 8; q++) {
			int xRight = 59 + 200 * q;
			//System.out.println(xRight);
			int xLeft = xRight - 134;
			if(x > xLeft && x < xRight)
				return q-1;
		}
		
		
		return -1;
	}
	
	public void fillCanvas(Color c) {
		for(int x = 0; x < width;x++) 
			for(int y = 0; y < height; y++)
				this.setRGB(x, y, c.getRGB());
	}

	public static Color bleach(Color color, float amount)
	{
		int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
		int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
		int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
		return new Color(red, green, blue);
	}


	public void setRGB(int dx, int dy, int i, int j, int k) {
		this.setRGB(dx, dy, (new Color(i,j,k)).getRGB());
	}


	public void setRGB(int dx, int dy, String currentMajor) {
		this.setRGB(dx, dy, majorColors.get(currentMajor).getRGB());
		
	}
	
	public Map<String, Color> getMajorColors() {
		return majorColors;
	}

}
