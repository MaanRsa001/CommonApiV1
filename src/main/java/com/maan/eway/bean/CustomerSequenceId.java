package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

@Data
public class CustomerSequenceId implements Serializable {

	private String sequenceNo;
	private String companyid;

	public CustomerSequenceId() {
	}

	public CustomerSequenceId(String sequenceNo, String companyid) {
		this.sequenceNo = sequenceNo;
		this.companyid = companyid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CustomerSequenceId))
			return false;
		CustomerSequenceId that = (CustomerSequenceId) o;
		return Objects.equals(sequenceNo, that.sequenceNo) && Objects.equals(companyid, that.companyid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sequenceNo, companyid);
	}
}