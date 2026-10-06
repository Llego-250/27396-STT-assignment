package com.orgx.model;

import java.time.LocalDate;

/**
 * Domain model representing a processed payment for an employee.
 */
public class Payment {
    private String paymentId;
    private String employeeId;
    private double grossPay;
    private double taxDeduction;
    private double bonus;
    private double netPay;
    private LocalDate paymentDate;

    public Payment(String paymentId, String employeeId, double grossPay, double taxDeduction, double bonus, double netPay, LocalDate paymentDate) {
        this.paymentId = paymentId;
        this.employeeId = employeeId;
        this.grossPay = grossPay;
        this.taxDeduction = taxDeduction;
        this.bonus = bonus;
        this.netPay = netPay;
        this.paymentDate = paymentDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public double getTaxDeduction() {
        return taxDeduction;
    }

    public double getBonus() {
        return bonus;
    }

    public double getNetPay() {
        return netPay;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", grossPay=" + grossPay +
                ", taxDeduction=" + taxDeduction +
                ", bonus=" + bonus +
                ", netPay=" + netPay +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
