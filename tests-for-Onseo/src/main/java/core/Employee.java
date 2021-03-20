package core;

import com.github.javafaker.Faker;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import static base.APIConst.DATA;

public class Employee {
    private String profileImage;
    private String name;
    private String salary;
    private String id;
    private String age;
    private boolean isEmployeesNameHasTwoA;
    private boolean isMiddleAgeEmployee;


    public Employee(){

    }

    public Employee(JSONObject obj) {
        this.profileImage = obj.optString("profile_image");
        this.name = obj.optString("employee_name");
        this.salary = obj.optString("employee_salary");
        this.id = obj.optString("id");
        this.age = obj.optString("employee_age");
        setEmployeesNameHasTwoA();
        setMiddleAgeEmployee();
    }

    public JSONObject employeeToJSON() {
        return new JSONObject()
                .put("profile_image", this.profileImage)
                .put("employee_name", this.name)
                .put("employee_salary", this.salary)
                .put("id", this.id)
                .put("employee_age", this.age);
    }

    public JSONObject create(){
        return new EmployeeApi().createEmployee(this.employeeToJSON()).getJSONObject(DATA);
    }

    public JSONObject createWithRandomField(){
        Faker faker = new Faker();
        Employee employee = this.setName(faker.name().fullName())
                .setAge(String.valueOf(faker.number().numberBetween(18, 70)))
                .setSalary(String.valueOf(faker.number().numberBetween(1000, 2000)))
                .setProfileImage(faker.gameOfThrones().character());
        setEmployeesNameHasTwoA();
        setMiddleAgeEmployee();
        return new EmployeeApi().createEmployee(employee.employeeToJSON()).getJSONObject(DATA);
    }

    public JSONObject delete(){
        return new EmployeeApi().deleteEmployeeDataByID(this.id);
    }

    public void setEmployeesNameHasTwoA(){
        String[] allCharsOfName = this.name.split("");
        this.isEmployeesNameHasTwoA = Arrays.stream(allCharsOfName).filter(e -> e.equalsIgnoreCase("a")).count() >= 2;
    }
    public void setMiddleAgeEmployee() {
        this.isMiddleAgeEmployee = Integer.parseInt(this.getAge()) > 20 && Integer.parseInt(this.getAge()) < 50;
    }

    public boolean isEmployeesNameHasTwoA() {
        return isEmployeesNameHasTwoA;
    }

    public boolean isMiddleAgeEmployee() {
        return isMiddleAgeEmployee;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Employee setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getName() {
        return name;
    }

    public Employee setName(String name) {
        this.name = name;
        return this;
    }

    public String getSalary() {
        return salary;
    }

    public Employee setSalary(String salary) {
        this.salary = salary;
        return this;
    }

    public String getId() {
        return id;
    }

    public Employee setId(String id) {
        this.id = id;
        return this;
    }

    public String getAge() {
        return age;
    }

    public Employee setAge(String age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) && Objects.equals(salary, employee.salary) && Objects.equals(age, employee.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileImage, name, salary, id, age);
    }
}
