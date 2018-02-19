import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;


public class Visualization extends JPanel{

	private static final int LINEDENSITY = 5;
	private static final int TOWERBOTTOM = 700;
	private static final int STARTPOINT = 50;
	private static final int TOWERHEIGHT = 500;
	private static final int TOWERWIDTH = 75;
	private Map<String, Color> majorColors;
	public String selectionType;
	public boolean clickOn;
	public boolean hoverOn;
	
	public final Cohort cohort;
	Cohort displayCohort;
	private String[][] majorMap;
	private ArrayList<ArrayList<Student>> studentTowers;

	private BufferedImage canvas;
	int height;
	int width;
	private Student[][] studentMap;
	public Visualization(int width, int height) {
		this.addMouseMotionListener(new VisualMouseListener());
		this.addMouseListener(new VisualMouseClickListener());
		JFileChooser chooser = new JFileChooser();
		File selectedFile = null;
		selectionType = "Major Selection";
		clickOn = true;
		hoverOn = true;
		//System.out.println((new Color(192,192,192)).getRGB());
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
		selectedFile = chooser.getSelectedFile();
		}
		cohort  = new Cohort(selectedFile);
		displayCohort = cohort;
		generateStudentTowers(displayCohort.getTotalStudents());
		constructColorMap();
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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

	private void lineConstruction() {
		int total = studentTowers.get(0).size();
		float percent = ((float)TOWERHEIGHT)/total;
		int towerTop = TOWERBOTTOM - TOWERHEIGHT;
		//System.out.println(count);
		int dy = Math.round(percent);
		//System.out.println(dy);
		studentMap = new Student[canvas.getHeight()][canvas.getWidth()];
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
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
		drawKeyText(g2);
		
	}

	private void drawKeyShapes() {
		int x = 50;
		int y = 50;
		for(String major: displayCohort.getMajors()) {
			Color c = majorColors.get(major);
			System.out.println(c);
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
	

	private void drawKeyText(Graphics2D g2) {
		int x = 50;
		int y = 50;
		for(String major: displayCohort.getMajors()) {
			g2.setColor(Color.BLACK);
			g2.drawString(major, x+20, y+20);
			y+=50;
			if(y==200) {
				y = 50;
				x+=100;
			}
		}

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

	public void putLineDDA(int x0, int y0, int x, int y, Color c, String major, Student s) {
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
			if(studentMap[ Math.round(yT)][ Math.round(xT)] == null) {
				studentMap[ Math.round(yT)][ Math.round(xT)] = s; 

			}

		}
		repaint();
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
		majorMap = new String[canvas.getHeight()][canvas.getWidth()];
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

	private class VisualMouseListener implements MouseMotionListener {

		private String currentMajor;
		private Student currentStudent;
		//private int year;


		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if(Visualization.this.hoverOn) {
			if(selectionType.equals("Major Selection")) {
				if( x >= 0 && y >= 0 && x < majorMap[0].length && y < majorMap.length) {
					String major = majorMap[y][x];
					//System.out.println(major);
					if(currentMajor != null && !currentMajor.equals(major)) {
						for(int dx = 0; dx < majorMap[0].length;dx++)
							for(int dy = 0; dy < majorMap.length;dy++ ) {
								if(currentMajor.equals(majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy, majorColors.get(currentMajor).getRGB());
								}
								if(major != null && major.equals(majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
								}
							}
					}
					if(currentMajor == null && major != null) {
						for(int dx = 0; dx < majorMap[0].length;dx++)
							for(int dy = 0; dy < majorMap.length;dy++ ) {
								if(major.equals(majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
								}

							}
					}
					
					currentMajor = major;
				}	
				} else {
					if( x >= 0 && y >= 0 && x < studentMap[0].length && y < studentMap.length) {
						Student student = studentMap[y][x];
						//System.out.println(student);
						if(currentStudent != null && !currentStudent.equals(student)) {
							for(int dx = 0; dx < studentMap[0].length;dx++)
								for(int dy = 0; dy < studentMap.length;dy++ ) {
									if(currentStudent.equals(studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
										canvas.setRGB(dx, dy, majorColors.get(currentStudent.getYear(Visualization.this.majorFinder(dx))).getRGB());
									}
									if(student != null && student.equals(studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
										canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
									}
								}
						}
						if(currentStudent == null && student != null) {
							for(int dx = 0; dx < studentMap[0].length;dx++)
								for(int dy = 0; dy < studentMap.length;dy++ ) {
									if(student.equals(studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
										canvas.setRGB(dx, dy, (new Color(255,255,0)).getRGB());
									}

								}
						}
						currentStudent = student;
					}
					
				}
				Visualization.this.repaint();
			}
			}

}
	public void redrawSankey() {
		generateStudentTowers(displayCohort.getTotalStudents());
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fillCanvas(Color.WHITE);
		towerConstruction(displayCohort.getTotalMajorCountsInitial(), displayCohort.getTotalMajorCountsPerYear(), displayCohort.getTotalStudents().size());
		lineConstruction();
		drawKeyShapes();
		this.getMouseListeners()[0].mouseExited(new MouseEvent(this, 0,0, height, height, height, height, clickOn));
		//this.getMouseMotionListeners()[0].mouseMoved(new MouseEvent(this, 0,0, height, height, height, height, clickOn));
		this.repaint();
	}

	public Map<String, Color> getMajorColors() {
		return majorColors;
	}

	private class VisualMouseClickListener implements MouseListener {

		private String currentMajor;
		private Student currentStudent;
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if(Visualization.this.clickOn) {
			if(selectionType.equals("Major Selection")) {
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
									canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
								}
							}
					}
					if(currentMajor == null && major != null) {
						for(int dx = 0; dx < majorMap[0].length;dx++)
							for(int dy = 0; dy < majorMap.length;dy++ ) {
								if(major.equals(majorMap[dy][dx])) {
									//System.out.println(dx+" "+dy);
									canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
								}

							}
					}
					
					if(currentMajor != null && currentMajor.equals(major)) {
						for(int dx = 0; dx < majorMap[0].length;dx++)
							for(int dy = 0; dy < majorMap.length;dy++ ) {
								if(currentMajor.equals(majorMap[dy][dx])) {
									canvas.setRGB(dx, dy, majorColors.get(currentMajor).getRGB());
								}			
					}	
					currentMajor = null;
					}
					else			
					currentMajor = major;
				}	
				} else {
					if( x >= 0 && y >= 0 && x < studentMap[0].length && y < studentMap.length) {
						Student student = studentMap[y][x];

						//System.out.println(student);
						if(currentStudent != null && !currentStudent.equals(student)) {
							for(int dx = 0; dx < studentMap[0].length;dx++)
								for(int dy = 0; dy < studentMap.length;dy++ ) {
									if(currentStudent.equals(studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, majorColors.get(currentStudent.getYear(Visualization.this.majorFinder(dx))).getRGB());
									}
									if(student != null && student.equals(studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
									}
								}
						}
						if(currentStudent == null && student != null) {
							for(int dx = 0; dx < studentMap[0].length;dx++)
								for(int dy = 0; dy < studentMap.length;dy++ ) {
									if(student.equals(studentMap[dy][dx])) {
										//System.out.println(dx+" "+dy);
										canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
									}

								}
						}
						if(currentStudent != null && currentStudent.equals(student)) {
							for(int dx = 0; dx < studentMap[0].length;dx++)
								for(int dy = 0; dy < studentMap.length;dy++ ) {
									if(currentStudent.equals(studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, majorColors.get(currentStudent.getYear(Visualization.this.majorFinder(dx))).getRGB());
									}			
						}	
						currentStudent = null;
						}
						else			
						currentStudent = student;
					}
					
				}
				Visualization.this.repaint();
			}
			
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {
			if(selectionType.equals("Major Selection") && currentMajor != null) {
				for(int dx = 0; dx < majorMap[0].length;dx++)
					for(int dy = 0; dy < majorMap.length;dy++ ) {
						if(currentMajor.equals(majorMap[dy][dx])) {
							//System.out.println(dx+" "+dy);
							canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
						}

					}
			}
			if(selectionType != null && selectionType.equals("Student Selection") && currentStudent != null) {
				for(int dx = 0; dx < studentMap[0].length;dx++)
					for(int dy = 0; dy < studentMap.length;dy++ ) {
						if(currentStudent.equals(studentMap[dy][dx])) {
							//System.out.println(dx+" "+dy);
							canvas.setRGB(dx, dy, (new Color(192,192,192)).getRGB());
						}

					}
			}
			
			
		}
		
	}
}

class VisualItemListener implements ItemListener {

	private JMenuBar menuBar;
	private Visualization visual;
	private boolean URMSelection;
	private String URMSelectionS;
	public VisualItemListener(JMenuBar menu, Visualization visual) {
		this.menuBar = menu;
		this.visual = visual;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Cohort temp = new Cohort(visual.cohort.getBaseYear(),visual.cohort.getDepartmentName());
		ArrayList<Student> students = getSubset(visual.cohort);
		for(Student s: students) {
			boolean isInSubSet = true;
			for(int i = 4; i < menuBar.getMenuCount() && isInSubSet; i++) {
				JMenu menu = menuBar.getMenu(i);
				for(int q = 0; q < menu.getItemCount() && isInSubSet; q++) {
					JCheckBoxMenuItem check = (JCheckBoxMenuItem)menu.getItem(q);
					boolean hasMajor = s.hasMajor(check.getText()) || !check.isSelected();
					isInSubSet = hasMajor && isInSubSet;
				}
			}
			//System.out.println(isInSubSet);
			isInSubSet = isInSubSet && (URMSelectionS.equals("All") || s.isURM() == URMSelection);
			if(isInSubSet)
				temp.addStudent(s);
		}
		visual.displayCohort = temp;
		//System.out.println(temp);
		visual.redrawSankey();

	}

	private ArrayList<Student> getSubset(Cohort cohort) {
		String genderSelection = ""; 
		for(int i = 0; i < menuBar.getMenu(2).getItemCount(); i++) {
			if(menuBar.getMenu(2).getItem(i).isSelected()) 
				genderSelection = menuBar.getMenu(2).getItem(i).getText();
		}
		URMSelectionS = "";
		for(int i = 0; i < menuBar.getMenu(3).getItemCount(); i++) {
			if(menuBar.getMenu(3).getItem(i).isSelected()) 
				URMSelectionS = menuBar.getMenu(3).getItem(i).getText();
		}
		URMSelection = URMSelectionS.equals("URM");
		//System.out.println(URMSelectionS);
		switch(genderSelection) {
		case "Male": return cohort.getMaleStudents();
		case "Female": return cohort.getFemaleStudents();
		} 

		switch(URMSelectionS) {
		case "URM": return cohort.getURMStudents();
		case "NONURM": return cohort.getNONURMStudents();
		}

		return cohort.getTotalStudents();
	}

}
