import javax.swing.JFrame;

public class SankeyFrame extends JFrame{

	public SankeyFrame() {
		Visualization visual = new Visualization(1600, 800);
		JFrame frame = new JFrame();
		frame.add(visual);
		frame.setJMenuBar(new VisualizationMenu(visual.cohort.getMajors(),visual.cohort.getDepartment(),visual));
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	}
	
	public static void main(String[] args) {
		new SankeyFrame();
	}
}
