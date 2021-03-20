package core;

import base.BaseApi;
import base.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static base.APIConst.DATA;
import static base.APIConst.MAIN_PATH;

public class EmployeeApi extends BaseApi {

    public JSONArray getAllEmployeesData() {
        return getData(MAIN_PATH + "employees").getJSONArray(DATA);
    }

    public JSONObject getEmployeeDataByID(String id) {
        return getData(MAIN_PATH + "employee/" + id).getJSONObject(DATA);
    }

    public JSONObject createEmployee(JSONObject employee) {
        return postData(MAIN_PATH + "create", employee);
    }

    public JSONObject updateEmployeeDataByID(JSONObject update, String id) {
        return putData(MAIN_PATH + "update/" + id, update);
    }

    public JSONObject deleteEmployeeDataByID(String id) {
        return deleteData(MAIN_PATH + "delete/" + id);
    }

    public List<Employee> getAllEmployeesList() {
        return Utils.getJSONArrayStream(getAllEmployeesData()).map(Employee::new).collect(Collectors.toList());
    }

    public Employee getEmployeeByID(String id) {
        return new Employee(getEmployeeDataByID(id));
    }
}
