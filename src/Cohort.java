import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Cohort {

	private final ArrayList<Student> totalStudents, maleStudents, femaleStudents, URMStudents, NONURMStudents;
	private final Department department;
	private final String departmentName;
	private final int baseYear;
	private final ArrayList<Map<String,Integer>> totalMajorCountsPerYear,MaleMajorCountsPerYear,FemaleMajorCountsPerYear,URMMajorCountsPerYear,NONURMMajorCountsPerYear;
	private final ArrayList<Integer> inDepartmentPerYear;
	//private final ArrayList<Integer> gradInDepartmentperYear;
	private int maleCount;
	private int femaleCount;
	private int URMCount;
	private int NONURMCount;
	private Map<String, Integer> totalMajorCountsInitial,MaleMajorCountsInitial, FemaleMajorCountsInitial, URMMajorCountsInitial,NONURMMajorCountsInitial;
	private Set<String> majors;
	//Add Intial major Map
	
	public Cohort(File file) {

		try {
			Department.generateDepartments();
		} catch (InvalidFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		totalMajorCountsPerYear = new ArrayList<Map<String,Integer>>();
		MaleMajorCountsPerYear = new ArrayList<Map<String,Integer>>();
		FemaleMajorCountsPerYear = new ArrayList<Map<String,Integer>>();
		URMMajorCountsPerYear = new ArrayList<Map<String,Integer>>();
		NONURMMajorCountsPerYear = new ArrayList<Map<String,Integer>>();
		inDepartmentPerYear = new ArrayList<Integer>();
		
		totalStudents = new ArrayList<Student>();
		maleStudents = new ArrayList<Student>();
		femaleStudents = new ArrayList<Student>();
		URMStudents = new ArrayList<Student>();
		NONURMStudents = new ArrayList<Student>();
		majors = new HashSet<String>();
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(file);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sheet cohort = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = cohort.rowIterator();
		Row row1 = rowIterator.next();
		rowIterator.next();
		baseYear = Integer.parseInt(dataFormatter.formatCellValue(row1.getCell(0)));
		departmentName = dataFormatter.formatCellValue(row1.getCell(1));
		department = Department.DEPARTMENTS.get(departmentName);
		generateMapsAndLists();
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			String id = dataFormatter.formatCellValue(cellIterator.next());
			String gender = dataFormatter.formatCellValue(cellIterator.next());
			if(gender.equals(Student.MALE))
				maleCount++;
			else 
				femaleCount++;
			boolean URM = (Student.isURM.equals(dataFormatter.formatCellValue(cellIterator.next()))) ? true : false;
			if(URM)
				URMCount++;
			else
				NONURMCount++;
			String initialMajor = dataFormatter.formatCellValue(cellIterator.next());
			majors.add(initialMajor);
			incrementIntitialCount(initialMajor,gender,URM);
			Student s = new Student(id,gender,URM,initialMajor);
			int year = 0;
			while(cellIterator.hasNext()) {
				String major = dataFormatter.formatCellValue(cellIterator.next());
				majors.add(major);
				s.addYear(major);
				incrementMajorCounts(year, major, gender, URM);
				if(department.hasMajor(major))
					inDepartmentPerYear.set(year, inDepartmentPerYear.get(year)+1);
				year++;
			}
			assignStudent(s);
			
		}

	}



	public Set<String> getMajors() {
		return majors;
	}



	private void incrementIntitialCount(String major, String gender, boolean URM) {
		if(totalMajorCountsInitial.containsKey(major))
		{
			totalMajorCountsInitial.put(major, totalMajorCountsInitial.get(major)+1);
		}
		else
			totalMajorCountsInitial.put(major, 1);

		if(URM) {
			if(URMMajorCountsInitial.containsKey(major))
				URMMajorCountsInitial.put(major, URMMajorCountsInitial.get(major)+1);
			else
				URMMajorCountsInitial.put(major, 1);
		} else {
			if(NONURMMajorCountsInitial.containsKey(major))
				NONURMMajorCountsInitial.put(major, NONURMMajorCountsInitial.get(major)+1);
			else
				NONURMMajorCountsInitial.put(major, 1);
		}

		if(gender.equals(Student.MALE)) {
			if(MaleMajorCountsInitial.containsKey(major))
				MaleMajorCountsInitial.put(major, MaleMajorCountsInitial.get(major)+1);
			else
				MaleMajorCountsInitial.put(major, 1);
		} else {
			if(FemaleMajorCountsInitial.containsKey(major))
				FemaleMajorCountsInitial.put(major, FemaleMajorCountsInitial.get(major)+1);
			else
				FemaleMajorCountsInitial.put(major, 1);
		}
		
	}



	private void assignStudent(Student s) {
		totalStudents.add(s);
		if(s.isURM())
			URMStudents.add(s);
		else
			NONURMStudents.add(s);
		
		if(s.getGender().equals(Student.MALE))
			maleStudents.add(s);
		else
			femaleStudents.add(s);
	}



	private void incrementMajorCounts(int year, String major, String gender, boolean URM) {
		// TODO Auto-generated method stub

		if(totalMajorCountsPerYear.get(year).containsKey(major))
		{
			totalMajorCountsPerYear.get(year).put(major, totalMajorCountsPerYear.get(year).get(major)+1);
		}
		else
			totalMajorCountsPerYear.get(year).put(major, 1);

		if(URM) {
			if(URMMajorCountsPerYear.get(year).containsKey(major))
				URMMajorCountsPerYear.get(year).put(major, URMMajorCountsPerYear.get(year).get(major)+1);
			else
				URMMajorCountsPerYear.get(year).put(major, 1);
		} else {
			if(NONURMMajorCountsPerYear.get(year).containsKey(major))
				NONURMMajorCountsPerYear.get(year).put(major, NONURMMajorCountsPerYear.get(year).get(major)+1);
			else
				NONURMMajorCountsPerYear.get(year).put(major, 1);
		}

		if(gender.equals(Student.MALE)) {
			if(MaleMajorCountsPerYear.get(year).containsKey(major))
				MaleMajorCountsPerYear.get(year).put(major, MaleMajorCountsPerYear.get(year).get(major)+1);
			else
				MaleMajorCountsPerYear.get(year).put(major, 1);
		} else {
			if(FemaleMajorCountsPerYear.get(year).containsKey(major))
				FemaleMajorCountsPerYear.get(year).put(major, FemaleMajorCountsPerYear.get(year).get(major)+1);
			else
				FemaleMajorCountsPerYear.get(year).put(major, 1);
		}

	}



	private void generateMapsAndLists() {
		for(int i = 0; i < 7; i++) {
			totalMajorCountsPerYear.add(new TreeMap<String,Integer>(new MajorComparator(department)));
			MaleMajorCountsPerYear.add(new TreeMap<String,Integer>(new MajorComparator(department)));
			FemaleMajorCountsPerYear.add(new TreeMap<String,Integer>(new MajorComparator(department)));
			URMMajorCountsPerYear.add(new TreeMap<String,Integer>(new MajorComparator(department)));
			NONURMMajorCountsPerYear.add(new TreeMap<String,Integer>(new MajorComparator(department)));
			inDepartmentPerYear.add(0);
		}	
		totalMajorCountsInitial = new TreeMap<String, Integer>(new MajorComparator(department));
		MaleMajorCountsInitial = new TreeMap<String, Integer>(new MajorComparator(department));
		FemaleMajorCountsInitial = new TreeMap<String, Integer>(new MajorComparator(department));
		URMMajorCountsInitial = new TreeMap<String, Integer>(new MajorComparator(department));
		NONURMMajorCountsInitial = new TreeMap<String, Integer>(new MajorComparator(department));
	}



	private class MajorComparator implements Comparator<String> 
	{

		Department department;
		public MajorComparator(Department department) {
			this.department = department;
		}

		@Override
		public int compare(String o1, String o2) {
			if(o1.equals(o2))
				return 0;
			boolean o1IsDismissed = Department.isDismissed(o1);
			boolean o2IsDismissed = Department.isDismissed(o2);
			if(o1IsDismissed)
				return -1;
			if(o2IsDismissed)
				return 1;

			boolean o1IsDropOut = Department.isDropOut(o1);
			boolean o2IsDropOut = Department.isDropOut(o2);

			if(o1IsDropOut)
				return -1;
			if(o2IsDropOut)
				return 1;

			boolean o1IsCNS = Department.isCNS(o1);
			boolean o2IsCNS = Department.isCNS(o2);
			if(!o1IsCNS)
				return -1;
			if(!o2IsCNS)
				return 1;
			//System.out.println(department);
			boolean o1IsDepartment = department.hasMajor(o1);
			boolean o2IsDepartment = department.hasMajor(o2);
			if(!o1IsDepartment)
				return -1;
			if(!o2IsDepartment)
				return 1;
			return o1.compareTo(o2);
		}

	}



	public ArrayList<Student> getTotalStudents() {
		return totalStudents;
	}



	public ArrayList<Student> getMaleStudents() {
		return maleStudents;
	}



	public ArrayList<Student> getFemaleStudents() {
		return femaleStudents;
	}



	public ArrayList<Student> getURMStudents() {
		return URMStudents;
	}



	public ArrayList<Student> getNONURMStudents() {
		return NONURMStudents;
	}



	public Department getDepartment() {
		return department;
	}



	public String getDepartmentName() {
		return departmentName;
	}



	public int getBaseYear() {
		return baseYear;
	}



	public ArrayList<Map<String, Integer>> getTotalMajorCountsPerYear() {
		return totalMajorCountsPerYear;
	}



	public ArrayList<Map<String, Integer>> getMaleMajorCountsPerYear() {
		return MaleMajorCountsPerYear;
	}



	public ArrayList<Map<String, Integer>> getFemaleMajorCountsPerYear() {
		return FemaleMajorCountsPerYear;
	}



	public ArrayList<Map<String, Integer>> getURMMajorCountsPerYear() {
		return URMMajorCountsPerYear;
	}



	public ArrayList<Map<String, Integer>> getNONURMMajorCountsPerYear() {
		return NONURMMajorCountsPerYear;
	}



	public ArrayList<Integer> getInDepartmentPerYear() {
		return inDepartmentPerYear;
	}



	public int getMaleCount() {
		return maleCount;
	}



	public int getFemaleCount() {
		return femaleCount;
	}



	public int getURMCount() {
		return URMCount;
	}



	public int getNONURMCount() {
		return NONURMCount;
	}


	public Map<String, Integer> getTotalMajorCountsInitial() {
		return totalMajorCountsInitial;
	}



	public Map<String, Integer> getMaleMajorCountsInitial() {
		return MaleMajorCountsInitial;
	}



	public Map<String, Integer> getFemaleMajorCountsInitial() {
		return FemaleMajorCountsInitial;
	}



	public Map<String, Integer> getURMMajorCountsInitial() {
		return URMMajorCountsInitial;
	}



	public Map<String, Integer> getNONURMMajorCountsInitial() {
		return NONURMMajorCountsInitial;
	}

	@Override
	public String toString() {
		return "Cohort [totalStudents=" + totalStudents + ", maleStudents=" + maleStudents + ", femaleStudents="
				+ femaleStudents + ", URMStudents=" + URMStudents + ", NONURMStudents=" + NONURMStudents
				+ ", department=" + department + ", departmentName=" + departmentName + ", baseYear=" + baseYear
				+ ", totalMajorCountsPerYear=" + totalMajorCountsPerYear + ", MaleMajorCountsPerYear="
				+ MaleMajorCountsPerYear + ", FemaleMajorCountsPerYear=" + FemaleMajorCountsPerYear
				+ ", URMMajorCountsPerYear=" + URMMajorCountsPerYear + ", NONURMMajorCountsPerYear="
				+ NONURMMajorCountsPerYear + ", inDepartmentPerYear=" + inDepartmentPerYear + ", maleCount=" + maleCount
				+ ", femaleCount=" + femaleCount + ", URMCount=" + URMCount + ", NONURMCount=" + NONURMCount
				+ ", totalMajorCountsInitial=" + totalMajorCountsInitial + ", MaleMajorCountsInitial="
				+ MaleMajorCountsInitial + ", FemaleMajorCountsInitial=" + FemaleMajorCountsInitial
				+ ", URMMajorCountsInitial=" + URMMajorCountsInitial + ", NONURMMajorCountsInitial="
				+ NONURMMajorCountsInitial + "]";
	}

	public static void main(String[] args) throws InvalidFormatException, IOException {
		Cohort cohort = new Cohort(new File("CSCohortTest.xlsx"));
		System.out.println(cohort);
		//System.out.println(cohort.getTotalMajorCountsPerYear());
	}
	
}