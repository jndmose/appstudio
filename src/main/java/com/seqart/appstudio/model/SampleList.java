package com.seqart.appstudio.model;

import com.opencsv.bean.CsvBindByName;

public class SampleList {
	@CsvBindByName
	private Integer number;
	@CsvBindByName
	private String row;
	@CsvBindByName
	private int clientSampleId;
	@CsvBindByName
	private String comments;
	@CsvBindByName
	private String organismName;
	@CsvBindByName
	private Long column;
	@CsvBindByName
	private String tissueType;

	public SampleList() {

	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public int getClientSampleId() {
		return clientSampleId;
	}

	public void setClientSampleId(int clientSampleId) {
		this.clientSampleId = clientSampleId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getOrganismName() {
		return organismName;
	}

	public void setOrganismName(String organismName) {
		this.organismName = organismName;
	}

	public Long getColumn() {
		return column;
	}

	public void setColumn(Long column) {
		this.column = column;
	}

	public String getTissueType() {
		return tissueType;
	}

	public void setTissueType(String tissueType) {
		this.tissueType = tissueType;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "SampleList [number=" + number + ", row=" + row + ", clientSampleId=" + clientSampleId + ", comments="
				+ comments + ", organismName=" + organismName + ", column=" + column + ", tissueType=" + tissueType
				+ "]";
	}

}
