package com.orgx.service;

import com.orgx.model.Employee;
import com.orgx.model.Payment;
import com.orgx.model.Report;

import java.util.List;
import java.util.UUID;

/**
 * Service generating reporting documents for Organization X.
 */
public class ReportService {
    private final EmployeeService employeeService;
    private final PayrollService payrollService;

    public ReportService(EmployeeService employeeService, PayrollService payrollService) {
        this.employeeService = employeeService;
        this.payrollService = payrollService;
    }

    public Report generateEmployeeDirectoryReport() {
        List<Employee> employees = employeeService.getAllEmployees();
        StringBuilder sb = new StringBuilder();
        sb.append("=== EMPLOYEE DIRECTORY REPORT ===\n");
        sb.append(String.format("Total Registered Employees: %d\n\n", employees.size()));
        
        if (employees.isEmpty()) {
            sb.append("No employee records found in system.\n");
        } else {
            for (Employee emp : employees) {
                sb.append(String.format("ID: %s | Name: %s | Dept: %s | Role: %s | Base Salary: $%.2f | Status: %s\n",
                        emp.getId(), emp.getName(), emp.getDepartment(), emp.getRole(), emp.getBaseSalary(),
                        emp.isActive() ? "ACTIVE" : "INACTIVE"));
            }
        }

        return new Report("REP-" + UUID.randomUUID().toString().substring(0, 8), "Employee Directory", sb.toString());
    }

    public Report generatePayrollSummaryReport(String department) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== PAYROLL SUMMARY REPORT (%s) ===\n", department == null ? "ALL" : department.toUpperCase()));

        List<Employee> employees = (department == null || department.trim().isEmpty())
                ? employeeService.getAllEmployees()
                : employeeService.getEmployeesByDepartment(department);

        if (employees.isEmpty()) {
            sb.append("No employees found for the specified department criteria.\n");
            return new Report("REP-" + UUID.randomUUID().toString().substring(0, 8), "Payroll Summary", sb.toString());
        }

        double totalGross = 0;
        double totalTax = 0;
        double totalNet = 0;
        int processedCount = 0;

        List<Payment> history = payrollService.getPaymentHistory();
        for (Employee emp : employees) {
            for (Payment pay : history) {
                if (pay.getEmployeeId().equals(emp.getId())) {
                    totalGross += pay.getGrossPay();
                    totalTax += pay.getTaxDeduction();
                    totalNet += pay.getNetPay();
                    processedCount++;
                }
            }
        }

        sb.append(String.format("Total Employees Evaluated: %d\n", employees.size()));
        sb.append(String.format("Total Payments Issued: %d\n", processedCount));
        sb.append(String.format("Total Gross Disbursement: $%.2f\n", totalGross));
        sb.append(String.format("Total Tax Withheld: $%.2f\n", totalTax));
        sb.append(String.format("Total Net Payroll: $%.2f\n", totalNet));

        return new Report("REP-" + UUID.randomUUID().toString().substring(0, 8), "Payroll Summary", sb.toString());
    }
}
