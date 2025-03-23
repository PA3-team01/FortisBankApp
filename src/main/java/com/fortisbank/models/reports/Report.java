package com.fortisbank.models.reports;

import com.fortisbank.utils.IdGenerator;

import java.io.Serializable;
import java.util.Date;

public abstract class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String reportId;
    private final Date generatedDate;
    private final String reportType;

    protected Report(String reportType) {
        this.reportId = IdGenerator.generateId();
        this.generatedDate = new Date();
        this.reportType = reportType;
    }

    public String getReportId() {
        return reportId;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public String getReportType() {
        return reportType;
    }

    // force subclasses to implement summary output
    public abstract String getSummary();
}
