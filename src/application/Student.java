package application;
import java.util.ArrayList;
import java.util.Comparator;

public class Student {

	public static final String isURM = "YES";
	public static final String MALE = "MALE";
	public static final String FEMALE = "FEMALE";
	
	private String id;
	private String gender;
	private boolean URM;
	private String initialMajor;
	private ArrayList<String> majorAfterEachYear;
	
	public ArrayList<String> getMajorAfterEachYear() {
		return majorAfterEachYear;
	}

	public Student(String id, String gender, boolean uRM, String intialMajor) {
		this.id = id;
		this.gender = gender;
		URM = uRM;
		this.initialMajor = intialMajor;
		majorAfterEachYear = new ArrayList<String>();
	}
	
	public void addYear(String major) {
		majorAfterEachYear.add(major);
	}
	
	public String getYear(int year) {
		if(year==-1)
			return initialMajor;
		return majorAfterEachYear.get(year);
	}

	public String getId() {
		return id;
	}

	public String getGender() {
		return gender;
	}

	public boolean isURM() {
		return URM;
	}

	public String getIntialMajor() {
		return initialMajor;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		return this.id.equals(((Student)obj).id);
	}

	public boolean hasMajor(String major) {
		return major.equals(initialMajor) || majorAfterEachYear.contains(major);
	}

	
}

class StudentComparator implements Comparator<Student> 
{

	Department department;
	int year;
	public StudentComparator(int year, Department department) {
		this.department = department;
		this.year = year;
	}
	@Override
	public int compare(Student o1, Student o2) {
		String major1, major2;
		//if(year != 6 && o1.getYear(year).equals(o2.getYear(year))) {
		//	major1 = o1.getYear(year+1);
			//major2 = o2.getYear(year+1);
	//	} else {
			major1 = o1.getYear(year);
			major2 = o2.getYear(year);
		//}
		
		boolean major1IsDismissed = Department.isDismissed(major1);
		boolean major2IsDismissed = Department.isDismissed(major2);
		if(major1IsDismissed)
			return 1;
		if(major2IsDismissed)
			return -1;

		boolean major1IsDropOut = Department.isDropOut(major1);
		boolean major2IsDropOut = Department.isDropOut(major2);

		if(major1IsDropOut)
			return 1;
		if(major2IsDropOut)
			return -1;

		boolean major1IsCNS = Department.isCNS(major1);
		boolean major2IsCNS = Department.isCNS(major2);
		if(!major1IsCNS)
			return 1;
		if(!major2IsCNS)
			return -1;
		//System.out.println(department);
		boolean major1IsDepartment = department.hasMajor(major1);
		boolean major2IsDepartment = department.hasMajor(major2);
		if(!major1IsDepartment)
			return 1;
		if(!major2IsDepartment)
			return -1;
		return major2.compareTo(major1);
	}

	
	

}