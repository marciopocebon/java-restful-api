package com.mario.java.restful.api.hibernate.jpa.domain;

import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Abstract class which represents the base domain for all domains used by the application.<br>
 * All domains must extend this class.
 *
 * @author marioluan
 *
 */
@MappedSuperclass
public abstract class BaseDomain extends DatedDomain {

	@Transient
	private List<String> propertiesToBeDisplayed;

	/**
	 * @return the propertiesToBeDisplayed
	 */
	public List<String> getPropertiesToBeDisplayed() {
		return this.propertiesToBeDisplayed;
	}

	/**
	 * @param propertiesToBeDisplayed the propertiesToBeDisplayed to set
	 */
	public void setPropertiesToBeDisplayed(List<String> propertiesToBeDisplayed) {
		this.propertiesToBeDisplayed = propertiesToBeDisplayed;
	}
}
