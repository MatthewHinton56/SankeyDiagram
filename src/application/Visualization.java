package application;
//import java.awt.Color;
import javafx.scene.paint.Color;
import java.awt.Dimension;
//import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
//import java.awt.event.MouseEvent;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class Visualization extends Canvas{

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
	//private String[][] canvas.majorMap;
	//private ArrayList<ArrayList<Student>> studentTowers;
	public int displayCount;
	public SankeyImage canvas;
	//int height;
	//int width;
	//private Student[][] canvas.studentMap;
	public String title = "Sankey Diagram";
	
	VisualMouseMove moveListener;
	VisualMouseClick clickListener;
	
	public Visualization(int width, int height) {
		super(width,height);
		moveListener = new VisualMouseMove();
		clickListener = new VisualMouseClick();
		this.setOnMouseClicked(clickListener);
		this.setOnMouseMoved(moveListener);
		//JFileChooser chooser = new JFileChooser();
		//File selectedFile = null;
		selectionType = "Major Selection";
		clickOn = true;
		hoverOn = true;
		//System.out.println((new Color(192,192,192)).getRGB());
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
       
        //Show save file dialog
        File file = fileChooser.showOpenDialog(MainStage.stage);
		cohort  = new Cohort(file);
		displayCohort = cohort;
		displayCount = cohort.getTotalStudents().size();
		canvas = new SankeyImage(width, height, BufferedImage.TYPE_INT_ARGB, displayCohort);
		this.paint();
		
	}

	

	public void paint() {
		GraphicsContext gc = this.getGraphicsContext2D();
		 Image image = SwingFXUtils.toFXImage(canvas, null);
		 gc.drawImage(image, 0, 0);
		drawKeyText(gc);
		gc.setFill(Color.BLACK);
		gc.fill();
	}
	

	private void drawKeyText(GraphicsContext gc) {
		int x = 50;
		int y = 50;
		gc.setFont(new Font("Arial",20));
		for(String major: displayCohort.getMajors()) {
			gc.setFill(Color.BLACK);
			gc.fillText(major, x+20, y+20);
			y+=50;
			if(y==200) {
				y = 50;
				x+=100;
			}
		}
		
		gc.fillText(displayCount+" / "+cohort.getTotalStudents().size(), 50, 20);
		int yearX = 0;
		for(int i = 0; i < 8; i ++) {
			 yearX = 50 + 200*i;
			 if(i == 0)
			 gc.fillText("Intital", yearX, 720);
			 else 
			gc.fillText("Year: "+ i, yearX, 720);	 
			 
		}
		gc.setFont(new Font("Arial", 50));
		gc.fillText(title, x+100, 190);
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
	
	private class VisualMouseMove implements EventHandler<MouseEvent> {

		private String currentMajor;
		private Student currentStudent;
		private int counter = 0;
		//private int year;
		@Override
		 public void handle(MouseEvent e) {
			int x = (int) e.getX();
			int y = (int) e.getY();
			counter++;
			if(Visualization.this.hoverOn && counter == 10) {
				counter = 0;
			if(selectionType.equals("Major Selection")) {
				if( x >= 0 && y >= 0 && x < canvas.majorMap[0].length && y < canvas.majorMap.length) {
					String major = canvas.majorMap[y][x];
					//System.out.println(major);
					if(currentMajor != null && !currentMajor.equals(major)) {
						for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
							for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
								if(currentMajor.equals(canvas.majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy, currentMajor);
								}
								if(major != null && major.equals(canvas.majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy,255,255,0);
								}
							}
					}
					if(currentMajor == null && major != null) {
						for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
							for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
								if(major.equals(canvas.majorMap[dy][dx])) {
									if(canvas.getRGB(dx, dy) != -4144960)
									canvas.setRGB(dx, dy, 255,255,0);
								}

							}
					}
					
					currentMajor = major;
				}	
				} else {
					if( x >= 0 && y >= 0 && x < canvas.studentMap[0].length && y < canvas.studentMap.length) {
						Student student = canvas.studentMap[y][x];
						//System.out.println(student);
						if(currentStudent != null && !currentStudent.equals(student)) {
							for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
								for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
									if(currentStudent.equals(canvas.studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
										canvas.setRGB(dx, dy, currentStudent.getYear(Visualization.this.majorFinder(dx)));
									}
									if(student != null && student.equals(canvas.studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
											canvas.setRGB(dx, dy, 255,255,0);
									}
								}
						}
						if(currentStudent == null && student != null) {
							for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
								for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
									if(student.equals(canvas.studentMap[dy][dx])) {
										if(canvas.getRGB(dx, dy) != -4144960)
											canvas.setRGB(dx, dy, 255,255,0);
									}

								}
						}
						currentStudent = student;
					}
					
				}
				Visualization.this.paint();
			}
			}

}
	public void redrawSankey() {
		canvas.redraw(displayCohort);
		//this.getMouseListeners()[0].mouseExited(new MouseEvent(this, 0,0, height, height, height, height, clickOn));
		//this.getMouseMotionListeners()[0].mouseMoved(new MouseEvent(this, 0,0, height, height, height, height, clickOn));
		this.clickListener.reset();
		this.paint();
	}


	private class VisualMouseClick implements EventHandler<MouseEvent> {

		private String currentMajor;
		private Student currentStudent;
		@Override
		 public void handle(MouseEvent e){
			int x = (int) e.getX();
			int y = (int) e.getY();
			if(Visualization.this.clickOn) {
			if(selectionType.equals("Major Selection")) {
				if( x >= 0 && y >= 0 && x < canvas.majorMap[0].length && y < canvas.majorMap.length) {
					String major = canvas.majorMap[y][x];
					//System.out.println(major);
					if(currentMajor != null && !currentMajor.equals(major)) {
						for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
							for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
								if(currentMajor.equals(canvas.majorMap[dy][dx])) {
									canvas.setRGB(dx, dy,currentMajor);
								}
								if(major != null && major.equals(canvas.majorMap[dy][dx])) {
									canvas.setRGB(dx, dy, 192,192,192);
								}
							}
					}
					if(currentMajor == null && major != null) {
						for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
							for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
								if(major.equals(canvas.majorMap[dy][dx])) {
									//System.out.println(dx+" "+dy);
									canvas.setRGB(dx, dy, 192,192,192);
								}

							}
					}
					
					if(currentMajor != null && currentMajor.equals(major)) {
						for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
							for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
								if(currentMajor.equals(canvas.majorMap[dy][dx])) {
									canvas.setRGB(dx, dy,currentMajor);
								}			
					}	
					currentMajor = null;
					}
					else			
					currentMajor = major;
				}	
				} else {
					if( x >= 0 && y >= 0 && x < canvas.studentMap[0].length && y < canvas.studentMap.length) {
						Student student = canvas.studentMap[y][x];

						//System.out.println(student);
						if(currentStudent != null && !currentStudent.equals(student)) {
							for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
								for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
									if(currentStudent.equals(canvas.studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, currentStudent.getYear(Visualization.this.majorFinder(dx)));
									}
									if(student != null && student.equals(canvas.studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, 192,192,192);
									}
								}
						}
						if(currentStudent == null && student != null) {
							for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
								for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
									if(student.equals(canvas.studentMap[dy][dx])) {
										//System.out.println(dx+" "+dy);
										canvas.setRGB(dx, dy, 192,192,192);
									}

								}
						}
						if(currentStudent != null && currentStudent.equals(student)) {
							for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
								for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
									if(currentStudent.equals(canvas.studentMap[dy][dx])) {
										canvas.setRGB(dx, dy, currentStudent.getYear(Visualization.this.majorFinder(dx)));
									}			
						}	
						currentStudent = null;
						}
						else			
						currentStudent = student;
					}
					
				}
				Visualization.this.paint();
			}
			
		}
		public void reset() {
			if(selectionType.equals("Major Selection") && currentMajor != null) {
				for(int dx = 0; dx < canvas.majorMap[0].length;dx++)
					for(int dy = 0; dy < canvas.majorMap.length;dy++ ) {
						if(currentMajor.equals(canvas.majorMap[dy][dx])) {
							//System.out.println(dx+" "+dy);
							canvas.setRGB(dx, dy, 192,192,192);
						}

					}
			}
			if(selectionType != null && selectionType.equals("Student Selection") && currentStudent != null) {
				for(int dx = 0; dx < canvas.studentMap[0].length;dx++)
					for(int dy = 0; dy < canvas.studentMap.length;dy++ ) {
						if(currentStudent.equals(canvas.studentMap[dy][dx])) {
							//System.out.println(dx+" "+dy);
							canvas.setRGB(dx, dy, 192,192,192);
						}

					}
			}
			
			
		}
		
	}

}

class VisualItemListener implements EventHandler<ActionEvent> {

	private VisualizationMenu menuBar;
	private Visualization visual;
	private boolean URMSelection;
	private String URMSelectionS;
	public VisualItemListener(VisualizationMenu visualizationMenu, Visualization visual) {
		this.menuBar = visualizationMenu;
		this.visual = visual;
	}

	@Override
		public void handle(ActionEvent event) {
		Cohort temp = new Cohort(visual.cohort.getBaseYear(),visual.cohort.getDepartmentName());
		ArrayList<Student> students = getSubset(visual.cohort);
		for(Student s: students) {
			
			boolean leavesDepartment = false;
			boolean leavesCollege = false;
			boolean leavesUT = false;
			int pos = -1;
			while(!leavesDepartment && pos < s.getMajorAfterEachYear().size()) {
				leavesDepartment = !visual.cohort.getDepartment().hasMajor(s.getYear(pos));
				pos++;
			}
			pos = -1;
			while(!leavesCollege  && pos < s.getMajorAfterEachYear().size()) {
				leavesCollege = !Department.isCNS(s.getYear(pos));
				pos++;
			}
			pos = -1;
			while(!leavesUT  && pos < s.getMajorAfterEachYear().size()) {
				leavesUT = !Department.isMajor(s.getYear(pos));
				pos++;
			}
			boolean isInSubSet = true;
			//System.out.println(menuBar.getMenus().get(4).getItems().get(0).getText());
			boolean hasToRemainDepartment = ((RadioMenuItem)menuBar.getMenus().get(4).getItems().get(0)).isSelected();
			boolean hasToLeaveDepartment = ((RadioMenuItem)menuBar.getMenus().get(4).getItems().get(1)).isSelected();
			boolean hasToRemainCollege = ((RadioMenuItem)menuBar.getMenus().get(5).getItems().get(0)).isSelected();
			boolean hasToLeaveCollege = ((RadioMenuItem)menuBar.getMenus().get(5).getItems().get(1)).isSelected();
			boolean hasToRemainUt;
			boolean hasToLeaveUt;
			if(menuBar.getMenus().size() > 7) {
				 hasToRemainUt = ((RadioMenuItem)menuBar.getMenus().get(7).getItems().get(0)).isSelected();
				 hasToLeaveUt = ((RadioMenuItem)menuBar.getMenus().get(7).getItems().get(1)).isSelected(); 
			} else {
				 hasToRemainUt = false;
				 hasToLeaveUt = false;
			}
			isInSubSet &= !hasToRemainDepartment || !leavesDepartment ;
			isInSubSet &= !hasToLeaveDepartment || leavesDepartment;
			isInSubSet &= !hasToRemainCollege || !leavesCollege;
			isInSubSet &= !hasToLeaveCollege || leavesCollege;
			isInSubSet &= !hasToRemainUt || !leavesUT;
			isInSubSet &= !hasToLeaveUt || leavesUT;
			for(int i = 4; i < menuBar.getMenus().size() && isInSubSet; i++) {
				Menu menu = menuBar.getMenus().get(i);
				for(int q = 0; q < menu.getItems().size() && isInSubSet; q++) {
					MenuItem item = menu.getItems().get(q);
					if(item instanceof CheckMenuItem) {
					CheckMenuItem check = (CheckMenuItem)item;
					boolean hasMajor = s.hasMajor(check.getText()) || !check.isSelected();
					isInSubSet = hasMajor && isInSubSet;
					}
				}
			}
			//System.out.println(isInSubSet);
			isInSubSet = isInSubSet && (URMSelectionS.equals("All") || s.isURM() == URMSelection);
			if(isInSubSet)
				temp.addStudent(s);
		}
		visual.displayCount = temp.getTotalStudents().size();
		visual.displayCohort = temp;
		//System.out.println(temp);
		visual.redrawSankey();

	}

	private ArrayList<Student> getSubset(Cohort cohort) {
		String genderSelection = ""; 
		for(int i = 0; i < menuBar.getMenus().get(2).getItems().size(); i++) {
			if(((RadioMenuItem)menuBar.getMenus().get(2).getItems().get(i)).isSelected()) 
				genderSelection = menuBar.getMenus().get(2).getItems().get(i).getText();
		}
		URMSelectionS = "";
		for(int i = 0; i < menuBar.getMenus().get(3).getItems().size(); i++) {
			if(((RadioMenuItem)menuBar.getMenus().get(3).getItems().get(i)).isSelected()) 
				URMSelectionS = menuBar.getMenus().get(3).getItems().get(i).getText();
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
