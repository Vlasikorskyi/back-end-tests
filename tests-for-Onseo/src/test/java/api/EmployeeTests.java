package api;

import core.Employee;
import core.EmployeeApi;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static base.Utils.getCurrentDate;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmployeeTests {

    private final EmployeeApi employeeApi = new EmployeeApi();

    @Test
    public void findAllEmployeeWithTwoAInName() {
        employeeApi.getAllEmployeesList()
                .stream()
                .filter(Employee::isEmployeesNameHasTwoA)
                .forEach(System.out::println);
    }

    @Test
    public void recreateEmployeeWithLongestName() {
        String longestName = employeeApi.getAllEmployeesList()
                .stream()
                .max(Comparator.comparingInt(e -> e.getName().length()))
                .get()
                .getName();
        Employee newEmployee = new Employee().setName(longestName + "_" + getCurrentDate("dd-MM-yyy"))
                .setSalary("584111")
                .setAge("111");
        Employee createdEmployee = new Employee(newEmployee.create());
        assertThat("Create employee feature has incorrect result", createdEmployee, equalTo(newEmployee));
    }

    @Test
    public void deleteMiddleAgeEmployee() {
        List<Employee> employeeListBeforeDelete = employeeApi.getAllEmployeesList();
        List<Employee> employeeListToDelete = employeeListBeforeDelete
                .stream()
                .filter(Employee::isMiddleAgeEmployee)
                .collect(Collectors.toList());

        employeeListBeforeDelete.removeAll(employeeListToDelete);

        employeeListToDelete.forEach(Employee::delete);
        assertThat("Delete employee feature has incorrect result", employeeApi.getAllEmployeesList(), equalTo(employeeListBeforeDelete));
    }

    @Test
    public void createNewEmployeeWithRandomAttributes() {
        Employee newEmployee = new Employee();
        JSONObject object = newEmployee.createWithRandomField();
        Employee createdEmployee = new Employee(object);
        assertThat("Create employee feature has incorrect result", createdEmployee, equalTo(newEmployee));
    }
}
