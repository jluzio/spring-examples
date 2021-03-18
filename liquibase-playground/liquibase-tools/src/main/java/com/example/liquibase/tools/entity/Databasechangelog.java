package com.example.liquibase.tools.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
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