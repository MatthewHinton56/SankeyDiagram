import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Department {

	public static final String DISMISSED = "Dismissed";
	public static final String DROPOUT = "DropOut";
	
	
	private String departmentName;
	private Set<String> majors;

	public static final Map<String,Department> DEPARTMENTS = new HashMap<String,Department>();

	public Department(String departmentName) {
		this.departmentName = departmentName;
		majors = new HashSet<String>();
	}

	public void addMajor(String major) {
		majors.add(major);
	}

	public boolean hasMajor(String major) {
		return majors.contains(major);
	}

	public Iterator<String> getMajors() {
		return majors.iterator();
	}

	public static void generateDepartments() throws InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(new File("SankeyData.xlsx"));
		Sheet departments = workbook.getSheet("Departments");
		DataFormatter dataFormatter = new DataFormatter();
		for (Row row: departments) {
				int cellCount = 0;
				Department d = new Department(dataFormatter.formatCellValue(row.getCell(0)));
				for(Cell cell: row) {
					String cellValue = dataFormatter.formatCellValue(cell);
					if(cellCount != 0)
						d.addMajor(cellValue);
					cellCount++;
				}
				DEPARTMENTS.put(d.departmentName, d);
			}    



	}

	@Override
	public String toString() {
		return "Department [departmentName=" + departmentName + ", majors=" + majors + "]";
	}

	public static boolean isCNS(String major) {
		return major.substring(0,1).equals("E");
	}
	
	public static boolean isDismissed(String major) {
		return major.equals(Department.DISMISSED);
	}
	
	public static boolean isDropOut(String major) {
		return major.equals(Department.DROPOUT);
	}
	
	public static boolean isMajor(String major) {
		return !isDropOut(major) && !isDismissed(major);
	}
	
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		generateDepartments();
		System.out.println(DEPARTMENTS);
	}

}
