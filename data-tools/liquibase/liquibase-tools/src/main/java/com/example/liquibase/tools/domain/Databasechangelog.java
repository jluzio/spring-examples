package com.example.liquibase.tools.domain;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the DATABASECHANGELOG database table.
 * 
 */
@Entity
@IdClass(DatabasechangelogId.class)
@Table(name="DATABASECHANGELOG")
@NamedQuery(name="Databasechangelog.findAll", query="SELECT d FROM Databasechangelog d")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Databasechangelog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="AUTHOR")
	private String author;

	@Column(name="COMMENTS")
	private String comments;

	@Column(name="CONTEXTS")
	private String contexts;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATEEXECUTED")
	private Date dateexecuted;

	@Column(name="DEPLOYMENT_ID")
	private String deploymentId;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="EXECTYPE")
	private String exectype;

	@Id
	@Column(name="FILENAME")
	private String filename;

	@Id
	@Column(name="ID")
	private String id;

	@Column(name="LABELS")
	private String labels;

	@Column(name="LIQUIBASE")
	private String liquibase;

	@Column(name="MD5SUM")
	private String md5sum;

	@Column(name="ORDEREXECUTED")
	private int orderexecuted;

	@Column(name="TAG")
	private String tag;

}