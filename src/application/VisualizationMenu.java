package application;
import java.awt.Color;
//import java.awt.TextField;
//import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class VisualizationMenu extends MenuBar {



	private class ColorChanger extends Stage implements EventHandler<ActionEvent> {

		private ComboBox<String> majorList;
		private ComboBox<String> red;
		private ComboBox<String> blue;
		private ComboBox<String> green;
		private Visualization v;

		public ColorChanger(Visualization v) {
			majorList = new ComboBox<String>(FXCollections.observableList(new ArrayList<String>(v.cohort.getMajors())));
			majorList.setPromptText("Majors");
			this.v = v;
			String[] numbers = new String[256];
			for(int i = 0;i < 256; i++) {
				numbers[i] = ""+i;
			}
			red = new ComboBox<String>(FXCollections.observableArrayList(numbers));
			red.setPromptText("Red");
			blue = new ComboBox<String>(FXCollections.observableArrayList(numbers));
			blue.setPromptText("Blue");
			green = new ComboBox<String>(FXCollections.observableArrayList(numbers));
			green.setPromptText("Green");
			Button button = new Button("Change");
			button.setOnAction(this);
			//Panel panel = new JPanel();
			HBox box = new HBox();
			box.getChildren().addAll(majorList,red,blue,green,button);
			this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(MainStage.stage);
            Scene thisScene = new Scene(box, 600, 50);
            this.setScene(thisScene);
            this.show();
		}

		@Override
		public void handle(ActionEvent e) {
			String major =majorList.getValue();
			
			int redVal = Integer.parseInt(red.getValue());
			int greenVal = Integer.parseInt(green.getValue());
			int blueVal = Integer.parseInt(blue.getValue());
			v.canvas.getMajorColors().put(major, new Color(redVal,greenVal,blueVal));
			v.redrawSankey();
		}
	}

	public VisualizationMenu(Set<String> majors, Department d, Visualization v, Stage sankeyFrame) {
		Menu file = new Menu("File");
		MenuItem  load = new MenuItem ("Load");
		MenuItem  save = new MenuItem ("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();
	             
	            //Set extension filter
	            FileChooser.ExtensionFilter extFilter = 
	                    new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
	            fileChooser.getExtensionFilters().add(extFilter);
	           
	            //Show save file dialog
	            File file = fileChooser.showSaveDialog(sankeyFrame);
	            if(file != null){
	                try {
	                    WritableImage writableImage = new WritableImage(1600, 800);
	                    v.snapshot(null, writableImage);
	                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
	                    ImageIO.write(renderedImage, "png", file);
	                } catch (IOException ex) {
	                }
	            }
				
			}
			
		});
		file.getItems().addAll(load,save);
		this.getMenus().add(file);
		
		Menu edit = new Menu("Edit");
		MenuItem changeColor = new MenuItem("Change Color");
		changeColor.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				new ColorChanger(v);
			}
		
		});
		
		
		RadioMenuItem studentSelection = new RadioMenuItem("Student Selection");
		studentSelection.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				v.selectionType = "Student Selection";
				v.redrawSankey();
			}
			
		});
		RadioMenuItem majorSelection = new RadioMenuItem("Major Selection");
		majorSelection.setSelected(true);
		majorSelection.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				v.selectionType = "Major Selection";
				v.redrawSankey();
			}
			
		});
		
		ToggleGroup selectionGroup = new ToggleGroup();
		studentSelection.setToggleGroup(selectionGroup);
		majorSelection.setToggleGroup(selectionGroup);
		edit.getItems().addAll(changeColor, new SeparatorMenuItem(), majorSelection, studentSelection,new SeparatorMenuItem());

		CheckMenuItem clickOn = new CheckMenuItem("Click On");
		clickOn.setSelected(true);
		clickOn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				v.clickOn = ((CheckMenuItem)VisualizationMenu.this.getMenus().get(1).getItems().get(5)).isSelected();
				
			}
			
		});
		edit.getItems().add(clickOn);
		CheckMenuItem hoverOn = new CheckMenuItem("Hover On");
		hoverOn.setSelected(true);
		hoverOn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent e) {
				v.hoverOn = ((CheckMenuItem)VisualizationMenu.this.getMenus().get(1).getItems().get(6)).isSelected();
				
			}
			
		});
		edit.getItems().add(hoverOn);
		
		this.getMenus().add(edit);
		Menu gender = new Menu("Gender");
		RadioMenuItem male = new RadioMenuItem("Male");
		male.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem female = new RadioMenuItem("Female");
		female.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem all = new RadioMenuItem("All");
		all.setSelected(true);
		all.setOnAction(new VisualItemListener(this,v));
		ToggleGroup genderGroup = new ToggleGroup();
		male.setToggleGroup(genderGroup);
		female.setToggleGroup(genderGroup);
		all.setToggleGroup(genderGroup);
		gender.getItems().addAll(male, female, all);
		this.getMenus().add(gender);
		
		Menu URMMenu = new Menu("URM");
		RadioMenuItem URM = new RadioMenuItem("URM");
		URM.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem NONURM = new RadioMenuItem("NONURM");
		NONURM.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem allURM = new RadioMenuItem("All");
		allURM.setSelected(true);
		allURM.setOnAction(new VisualItemListener(this,v));
		ToggleGroup URMGroup = new ToggleGroup();
		URM.setToggleGroup(URMGroup);
		NONURM.setToggleGroup(URMGroup);
		allURM.setToggleGroup(URMGroup);
		URMMenu.getItems().addAll(URM, NONURM, allURM);
		this.getMenus().add(URMMenu);
		
		Menu department = new Menu("Department");
		RadioMenuItem remainInDepartment = new RadioMenuItem("Remains In department");
		remainInDepartment.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem leaveDepartment = new RadioMenuItem("Leaves Department");
		leaveDepartment.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem departmentAll = new RadioMenuItem("All");
		departmentAll.setSelected(true);
		departmentAll.setOnAction(new VisualItemListener(this,v));
		ToggleGroup departmentGroup = new ToggleGroup();
		remainInDepartment.setToggleGroup(departmentGroup);
		leaveDepartment.setToggleGroup(departmentGroup);
		departmentAll.setToggleGroup(departmentGroup);
		department.getItems().addAll(remainInDepartment, leaveDepartment, departmentAll, new SeparatorMenuItem());
		
		for(String s: majors) {
			if(d.hasMajor(s)) {
				CheckMenuItem  major = new CheckMenuItem (s);
				major.setOnAction(new VisualItemListener(this,v));
				department.getItems().add(major);
			}
		}
		this.getMenus().add(department);
		
		Menu college = new Menu("College");
		RadioMenuItem remainInCollege = new RadioMenuItem("Remains In college");
		remainInCollege.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem leaveCollege = new RadioMenuItem("Leaves College");
		leaveCollege.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem collegeAll = new RadioMenuItem("All");
		collegeAll.setSelected(true);
		collegeAll.setOnAction(new VisualItemListener(this,v));
		ToggleGroup collegeGroup = new ToggleGroup();
		remainInCollege.setToggleGroup(collegeGroup);
		leaveCollege.setToggleGroup(collegeGroup);
		collegeAll.setToggleGroup(collegeGroup);
		college.getItems().addAll(remainInCollege, leaveCollege, collegeAll, new SeparatorMenuItem());
		
		for(String s: majors) {
			if(!d.hasMajor(s) && Department.isCNS(s)) {
				CheckMenuItem  major = new CheckMenuItem (s);
				major.setOnAction(new VisualItemListener(this,v));
				college.getItems().add(major);
			}
		}
		this.getMenus().add(college);
		
		Menu otherColleges = new Menu("Other Colleges");
		for(String s: majors) {
			if(!d.hasMajor(s) && !Department.isCNS(s) && Department.isMajor(s)) {
				CheckMenuItem  major = new CheckMenuItem (s);
				major.setOnAction(new VisualItemListener(this,v));
				otherColleges.getItems().add(major);
			}
		}
		this.getMenus().add(otherColleges);
		Menu other = new Menu("Other");
		RadioMenuItem remainInUT = new RadioMenuItem("Remains In uT");
		remainInUT.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem leaveUT = new RadioMenuItem("Leaves UT");
		leaveUT.setOnAction(new VisualItemListener(this,v));
		RadioMenuItem uTAll = new RadioMenuItem("All");
		uTAll.setSelected(true);
		uTAll.setOnAction(new VisualItemListener(this,v));
		ToggleGroup otherGroup = new ToggleGroup();
		remainInUT.setToggleGroup(otherGroup);
		leaveUT.setToggleGroup(otherGroup);
		uTAll.setToggleGroup(otherGroup);
		other.getItems().addAll(remainInUT, leaveUT, uTAll, new SeparatorMenuItem());
		for(String s: majors) {
			if(!d.hasMajor(s) && !Department.isCNS(s) && !Department.isMajor(s)) {
				CheckMenuItem  major = new CheckMenuItem (s);
				major.setOnAction(new VisualItemListener(this,v));
				other.getItems().add(major);
			}
		}
		this.getMenus().add(other);
		
		
	}
}
