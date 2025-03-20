package com.fortisbank.data.repositories;

import com.fortisbank.models.Report;

import java.util.List;

public interface IReportRepository {
    void save(Report report);
    Report findById(String reportId);
    List<Report> findAll();
    void update(Report report);
    void delete(String reportId);
}
