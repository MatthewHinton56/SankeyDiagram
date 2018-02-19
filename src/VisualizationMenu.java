import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem ;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class VisualizationMenu extends JMenuBar {

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

	public VisualizationMenu(Set<String> majors, Department d, Visualization v) {
		JMenu file = new JMenu("File");
		JMenuItem  load = new JMenuItem ("Load");
		JMenuItem  save = new JMenuItem ("Save");
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
		edit.add(changeColor);
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
		for(String s: majors) {
			if(d.hasMajor(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				department.add(major);
			}
		}
		this.add(department);
		JMenu college = new JMenu("College");
		for(String s: majors) {
			if(!d.hasMajor(s) && Department.isCNS(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				college.add(major);
			}
		}
		this.add(college);
		JMenu otherColleges = new JMenu("Other Collegs");
		for(String s: majors) {
			if(!d.hasMajor(s) && !Department.isCNS(s)) {
				JCheckBoxMenuItem  major = new JCheckBoxMenuItem (s);
				major.addItemListener(new VisualItemListener(this,v));
				otherColleges.add(major);
			}
		}
		this.add(otherColleges);
	}
}
