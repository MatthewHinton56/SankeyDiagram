# SankeyDiagram
Developed for UT College of Natural sciences to display data in a precise manner. Used to allow for various Sankey Diagrams using the set 
of data points, by creating and displaying subsets of the dataset

## Department 
A department Excel file is also included, containing major codes for 7 different departments of UT, allowing the system to highligh majors
of a specified department. 

## Back End  
Create a sankey Diagram using student input data accessed from an excel file, each student in the excel must be formatted as: 

| ID | Gender | URM | Initial Year | Years ... up to 7 |
| --- | ------ | --- | ------------ | ----------------- | 
| 1 | MALE | YES | E28400 | E28400 |
| 2 | FEMALE | YES | E28600 | E28400 |

Using the students, a cohort is formed, allowing for majors and students counts to be stored for data visualization. Once the students are 
loaded and processed, a sankey graph is formed, using major counts to form the towers, and the lines individual students

## Functionality 
The graph provides a number of options, for enhanced visualizations.

| Function | Description |
| -------- | ----------- |
| Specific Major | By selecting a specific major, only students with that major is displayed
| Leave / Remain | only includes students who either leave or remain with the specific group, such as department |
| URM / gender | By selecting a URM or gender group, the students are restricted to those of the selected subset |
| Color chnager | Select major and input in 3 0 - 255 numbers, for Red, Blue, and Green respectively, to change the color of the major
| Hover | By hovering over a major or student, all instances of the major or student are highlighted gold
| Click | A major or student can be clicked, turning the major or student silver |
| Student / Major Mode | sets the hover and click mode to either student or major |
| Click / Hover on | Ables or disables the two modes of highlighting |
| Save | Saves an png of the Sankey graph |
| Load | No functionality yet |

