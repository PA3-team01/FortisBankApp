package com.fortisbank.contracts.models.reports;

import com.fortisbank.contracts.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Abstract class representing a generic report.
 */
public abstract class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the report.
     */
    private final String reportId;

    /**
     * The date when the report was generated.
     */
    private final Date generatedDate;

    /**
     * The type of the report.
     */
    private final String reportType;

    /**
     * Constructor initializing the report with a specified type.
     *
     * @param reportType the type of the report
     */
    protected Report(String reportType) {
        this.reportId = IdGenerator.generateId();
        this.generatedDate = new Date();
        this.reportType = reportType;
    }

    /**
     * Returns the unique identifier for the report.
     *
     * @return the report ID
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * Returns the date when the report was generated.
     *
     * @return the generated date
     */
    public Date getGeneratedDate() {
        return generatedDate;
    }

    /**
     * Returns the type of the report.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Abstract method to be implemented by subclasses to provide a summary of the report.
     *
     * @return a string containing the summary of the report
     */
    public abstract String getSummary();
}