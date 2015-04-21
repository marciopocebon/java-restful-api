package com.mario.java.restful.api.hibernate.jpa.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mario.java.restful.api.hibernate.jpa.domain.serializer.UserDomainSerializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Domain class which represents an user on database.
 *
 * @author marioluan
 *
 */
@JsonSerialize(using=UserDomainSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "users")
@ApiModel(description = "user dto", parent = BaseDomain.class)
public class UserDomain extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = 20)
	@ApiModelProperty(required = true, value = "the name of the user")
	private String name;

	@OneToMany(mappedBy="user")
	private List<PetDomain> pets;

	/**
	 * Default constructor, creates an empty instance.
	 */
	public UserDomain() {
	}

	/**
	 * @param name the name to set
	 */
	public UserDomain(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * Fill all null/missing attributes from the current {@link UserDomain} userDomain instance with attributes from another {@link UserDomain} userDomain instance.
     *
     * @param petDomain the petDomain to take the attributes from
     */
	public void patch(UserDomain userDomain) {

		if(this.getName() == null){
			this.setName(userDomain.getName());
		}

		this.setCreatedAt(userDomain.getCreatedAt());
	}
}
