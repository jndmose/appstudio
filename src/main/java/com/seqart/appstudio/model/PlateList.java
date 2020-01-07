package com.seqart.appstudio.model;

import com.opencsv.bean.CsvBindByName;

public class PlateList {
	@CsvBindByName
	private String PlateNo;
	@CsvBindByName
	private String clientPlateId;
	@CsvBindByName
	private String clientPlateBarcode;
	@CsvBindByName
	private String sampleSubmissionFormat;

	public String getPlateNo() {
		return PlateNo;
	}

	public void setPlateNo(String plateNo) {
		PlateNo = plateNo;
	}

	public String getClientPlateId() {
		return clientPlateId;
	}

	public void setClientPlateId(String clientPlateId) {
		this.clientPlateId = clientPlateId;
	}

	public String getClientPlateBarcode() {
		return clientPlateBarcode;
	}

	public void setClientPlateBarcode(String clientPlateBarcode) {
		this.clientPlateBarcode = clientPlateBarcode;
	}

	public String getSampleSubmissionFormat() {
		return sampleSubmissionFormat;
	}

	public void setSampleSubmissionFormat(String sampleSubmissionFormat) {
		this.sampleSubmissionFormat = sampleSubmissionFormat;
	}

	@Override
	public String toString() {
		return "PlateList [PlateNo=" + PlateNo + ", clientPlateId=" + clientPlateId + ", clientPlateBarcode="
				+ clientPlateBarcode + ", sampleSubmissionFormat=" + sampleSubmissionFormat + "]";
	}

}
