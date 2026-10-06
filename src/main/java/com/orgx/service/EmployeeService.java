package com.orgx.service;

import com.orgx.model.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing employee operations for Organization X.
 */
public class EmployeeService {
    private final Map<String, Employee> employeeMap = new HashMap<>();

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (employeeMap.containsKey(employee.getId())) {
            throw new IllegalStateException("Employee with ID " + employee.getId() + " already exists.");
        }
        employeeMap.put(employee.getId(), employee);
    }

    public Optional<Employee> getEmployee(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(employeeMap.get(id));
    }

    public boolean updateEmployee(Employee employee) {
        if (employee == null || !employeeMap.containsKey(employee.getId())) {
            return false;
        }
        employeeMap.put(employee.getId(), employee);
        return true;
    }

    public boolean deactivateEmployee(String id) {
        Optional<Employee> empOpt = getEmployee(id);
        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();
            emp.setActive(false);
            return true;
        }
        return false;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        List<Employee> result = new ArrayList<>();
        if (department == null) {
            return result;
        }
        for (Employee emp : employeeMap.values()) {
            if (department.equalsIgnoreCase(emp.getDepartment())) {
                result.add(emp);
            }
        }
        return result;
    }
}
