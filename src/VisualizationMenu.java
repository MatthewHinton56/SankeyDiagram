import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem ;

public class VisualizationMenu extends JMenuBar {

	public VisualizationMenu(Set<String> majors, Department d, Visualization v) {
		JMenu file = new JMenu("File");
		JMenuItem  load = new JMenuItem ("Load");
		JMenuItem  save = new JMenuItem ("Save");
		file.add(load);
		file.add(save);
		this.add(file);
		
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
