import java.util.ArrayList;

public class Student {

	public static final String isURM = "YES";
	public static final String MALE = "MALE";
	public static final String FEMALE = "FEMALE";
	
	private String id;
	private String gender;
	private boolean URM;
	private String intialMajor;
	private ArrayList<String> majorAfterEachYear;
	
	public Student(String id, String gender, boolean uRM, String intialMajor) {
		this.id = id;
		this.gender = gender;
		URM = uRM;
		this.intialMajor = intialMajor;
		majorAfterEachYear = new ArrayList<String>();
	}
	
	public void addYear(String major) {
		majorAfterEachYear.add(major);
	}
	
	public String getYear(int year) {
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
		return intialMajor;
	}
	
}
