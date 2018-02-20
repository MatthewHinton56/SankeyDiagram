import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem ;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class VisualizationMenu extends JMenuBar {

	private class SaveMenu extends JFrame implements ActionListener{

		private JTextField field;
		private SankeyFrame sankeyFrame;

		public SaveMenu(SankeyFrame sankeyFrame) {
			this.sankeyFrame = sankeyFrame;
			field = new JTextField(20);
			JButton button = new JButton("Save");
			button.addActionListener(this);
			JPanel panel = new JPanel();
			panel.add(field);
			panel.add(button);
			panel.setSize(400, 200);
			this.add(panel);
			this.pack();
	        this.setVisible(true);
	        this.setResizable(false);
	        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String fileName = field.getText();
			sankeyFrame.saveImage(fileName);
		}

	}

	private class ColorChanger extends JFrame implements ActionListener {

		private JComboBox<String> majorList;
		private JComboBox<String> red;
		private JComboBox<String> blue;
		private JComboBox<String> green;
		private Visualization v;

		public ColorChanger(Visualization v) {
			majorList = new JComboBox<String>(v.cohort.getMajors().toArray(new String[v.cohort.getMajors().size()]));
			this.v = v;
			String[] numbers = new String[256];
			for(int i = 0;i < 256; i++) {
				numbers[i] = ""+i;
			}
			red = new JComboBox<String>(numbers);
			blue = new JComboBox<String>(numbers);
			green = new JComboBox<String>(numbers);
			JButton button = new JButton("Change");
			button.addActionListener(this);
			JPanel panel = new JPanel();
			panel.add(majorList);
			panel.add(red);
			panel.add(blue);
			panel.add(green);
			panel.add(button);
			panel.setSize(400, 200);
			this.add(panel);
			this.pack();
	        this.setVisible(true);
	        this.setResizable(false);
	        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String major = (String)majorList.getSelectedItem();
			int redVal = Integer.parseInt((String)red.getSelectedItem());
			int greenVal = Integer.parseInt((String)green.getSelectedItem());
			int blueVal = Integer.parseInt((String)blue.getSelectedItem());
			v.getMajorColors().put(major, new Color(redVal,greenVal,blueVal));
			v.redrawSankey();
		}
	}

	public VisualizationMenu(Set<String> majors, Department d, Visualization v, SankeyFrame sankeyFrame) {
		JMenu file = new JMenu("File");
		JMenuItem  load = new JMenuItem ("Load");
		JMenuItem  save = new JMenuItem ("Save");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveMenu(sankeyFrame);
				
			}
			
		});
		file.add(load);
		file.add(save);
		this.add(file);
		
		JMenu edit = new JMenu("Edit");
		JMenuItem changeColor = new JMenuItem("Change Color");
		changeColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ColorChanger(v);
			}
		
		});
		
		
		JRadioButtonMenuItem studentSelection = new JRadioButtonMenuItem("Student Selection");
		studentSelection.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				v.selectionType = "Student Selection";
				v.redrawSankey();
			}
			
		});
		JRadioButtonMenuItem majorSelection = new JRadioButtonMenuItem("Major Selection");
		majorSelection.setSelected(true);
		majorSelection.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				v.selectionType = "Major Selection";
				v.redrawSankey();
			}
			
		});
		
		ButtonGroup selectionGroup = new ButtonGroup();
		selectionGroup.add(studentSelection);
		selectionGroup.add(majorSelection);
		edit.add(changeColor);
		edit.addSeparator();
		edit.add(majorSelection);
		edit.add(studentSelection);
		edit.addSeparator();
		
		JCheckBoxMenuItem clickOn = new JCheckBoxMenuItem("Click On");
		clickOn.setSelected(true);
		clickOn.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				v.clickOn = ((JCheckBoxMenuItem)VisualizationMenu.this.getMenu(1).getItem(5)).isSelected();
				
			}
			
		});
		edit.add(clickOn);
		JCheckBoxMenuItem hoverOn = new JCheckBoxMenuItem("Hover On");
		hoverOn.setSelected(true);
		hoverOn.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				v.hoverOn = ((JCheckBoxMenuItem)VisualizationMenu.this.getMenu(1).getItem(6)).isSelected();
				
			}
			
		});
		edit.add(hoverOn);
		
		this.add(edit);
		JMenu gender = new JMenu("Gender");
		JRadioButtonMenuItem male = new JRadioButtonMenuItem("Male");
		male.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem female = new JRadioButtonMenuItem("Female");
		female.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem all = new JRadioButtonMenuItem("All");
		all.setSelected(true);
		all.addItemListener(new VisualItemListener(this,v));
		ButtonGroup genderGroup = new ButtonGroup();
		genderGroup.add(male);
		genderGroup.add(female);
		genderGroup.add(all);
		gender.add(male);
		gender.add(female);
		gender.add(all);
		this.add(gender);
		
		JMenu URMMenu = new JMenu("URM");
		JRadioButtonMenuItem URM = new JRadioButtonMenuItem("URM");
		URM.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem NONURM = new JRadioButtonMenuItem("NONURM");
		NONURM.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem allURM = new JRadioButtonMenuItem("All");
		allURM.setSelected(true);
		allURM.addItemListener(new VisualItemListener(this,v));
		ButtonGroup URMGroup = new ButtonGroup();
		URMGroup.add(URM);
		URMGroup.add(NONURM);
		URMGroup.add(allURM);
		URMMenu.add(URM);
		URMMenu.add(NONURM);
		URMMenu.add(allURM);
		this.add(URMMenu);
		JMenu department = new JMenu("Department");
		JRadioButtonMenuItem remainInDepartment = new JRadioButtonMenuItem("Remains In department");
		remainInDepartment.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem leaveDepartment = new JRadioButtonMenuItem("Leaves Department");
		leaveDepartment.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem departmentAll = new JRadioButtonMenuItem("All");
		departmentAll.setSelected(true);
		departmentAll.addItemListener(new VisualItemListener(this,v));
		ButtonGroup departmentGroup = new ButtonGroup();
		departmentGroup.add(remainInDepartment);
		departmentGroup.add(leaveDepartment);
		departmentGroup.add(departmentAll);
		department.add(remainInDepartment);
		department.add(leaveDepartment);
		department.add(departmentAll);
		department.addSeparator();
		for(String s: majors) {
			if(d.hasMajor(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				department.add(major);
			}
		}
		this.add(department);
		JMenu college = new JMenu("College");
		JRadioButtonMenuItem remainInCollege = new JRadioButtonMenuItem("Remains In college");
		remainInCollege.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem leaveCollege = new JRadioButtonMenuItem("Leaves College");
		leaveCollege.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem collegeAll = new JRadioButtonMenuItem("All");
		collegeAll.setSelected(true);
		collegeAll.addItemListener(new VisualItemListener(this,v));
		ButtonGroup collegeGroup = new ButtonGroup();
		collegeGroup.add(remainInCollege);
		collegeGroup.add(leaveCollege);
		collegeGroup.add(collegeAll);
		college.add(remainInCollege);
		college.add(leaveCollege);
		college.add(collegeAll);
		college.addSeparator();
		for(String s: majors) {
			if(!d.hasMajor(s) && Department.isCNS(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				college.add(major);
			}
		}
		this.add(college);
		JMenu otherColleges = new JMenu("Other Colleges");
		for(String s: majors) {
			if(!d.hasMajor(s) && !Department.isCNS(s) && Department.isMajor(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				otherColleges.add(major);
			}
		}
		this.add(otherColleges);
		JMenu other = new JMenu("Other");
		JRadioButtonMenuItem remainInUT = new JRadioButtonMenuItem("Remains In uT");
		remainInUT.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem leaveUT = new JRadioButtonMenuItem("Leaves UT");
		leaveUT.addItemListener(new VisualItemListener(this,v));
		JRadioButtonMenuItem uTAll = new JRadioButtonMenuItem("All");
		uTAll.setSelected(true);
		uTAll.addItemListener(new VisualItemListener(this,v));
		ButtonGroup otherGroup = new ButtonGroup();
		otherGroup.add(remainInUT);
		otherGroup.add(leaveUT);
		otherGroup.add(uTAll);
		other.add(remainInUT);
		other.add(leaveUT);
		other.add(uTAll);
		other.addSeparator();
		for(String s: majors) {
			if(!d.hasMajor(s) && !Department.isCNS(s) && !Department.isMajor(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				other.add(major);
			}
		}
		this.add(other);
		
		
	}
}
