package org.tests.model.docstore;

import java.util.List;

import javax.persistence.Entity;

import org.tests.model.basic.BasicDomain;

import io.ebean.annotation.DbJson;

@Entity
public class ReportEntity extends BasicDomain {
  private static final long serialVersionUID = 1L;

  @DbJson
  private Report report;
  
  @DbJson
  private List<Report> reports;

  public Report getReport() {
    return report;
  }

  public void setReport(Report report) {
    this.report = report;
  }
}
