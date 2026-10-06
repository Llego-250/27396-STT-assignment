package com.orgx.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a generated system report.
 */
public class Report {
    private String reportId;
    private String title;
    private String content;
    private LocalDateTime generatedAt;

    public Report(String reportId, String title, String content) {
        this.reportId = reportId;
        this.title = title;
        this.content = content;
        this.generatedAt = LocalDateTime.now();
    }

    public String getReportId() {
        return reportId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", title='" + title + '\'' +
                ", generatedAt=" + generatedAt +
                "\n" + content +
                '}';
    }
}
