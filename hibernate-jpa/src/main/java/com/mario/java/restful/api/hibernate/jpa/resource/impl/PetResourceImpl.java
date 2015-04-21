package com.mario.java.restful.api.hibernate.jpa.resource.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mario.java.restful.api.hibernate.jpa.domain.PetDomain;
import com.mario.java.restful.api.hibernate.jpa.domain.PetDomain_;
import com.mario.java.restful.api.hibernate.jpa.domain.UserDomain;
import com.mario.java.restful.api.hibernate.jpa.domain.validation.DomainValidator;
import com.mario.java.restful.api.hibernate.jpa.domain.validation.impl.DomainValidatorJPAImpl;
import com.mario.java.restful.api.hibernate.jpa.repository.exception.ObjectNotFoundException;
import com.mario.java.restful.api.hibernate.jpa.resource.Resource;
import com.mario.java.restful.api.hibernate.jpa.resource.annotation.PATCH;
import com.mario.java.restful.api.hibernate.jpa.resource.bean.param.impl.PetDomainBeanParamImpl;
import com.mario.java.restful.api.hibernate.jpa.resource.response.HttpStatus;
import com.mario.java.restful.api.hibernate.jpa.service.Service;
import com.mario.java.restful.api.hibernate.jpa.service.impl.qualifiers.PetService;
import com.mario.java.restful.api.hibernate.jpa.service.impl.qualifiers.UserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/pets")
@Consumes("application/json")
@Produces("application/json")
@RequestScoped
@Api(value = "/pets", description = "operation on pet", tags = "pet", protocols = "http")
public class PetResourceImpl implements Resource<PetDomain, Long, PetDomainBeanParamImpl> {

	private static final Logger LOGGER = Logger.getLogger(PetResourceImpl.class.getName());

    private Service<PetDomain, Long> petService;
    private Service<UserDomain, Long> userService;
    private DomainValidator domainValidator;

    public PetResourceImpl() {
    }

    @Inject
    public PetResourceImpl(@PetService Service<PetDomain, Long> petService, @UserService Service<UserDomain, Long> userService, DomainValidator domainValidator) {
        this.petService = petService;
        this.userService = userService;
        this.domainValidator = domainValidator;
    }

    @Override
	@GET
    @Path("{id}")
    @ApiOperation(
    		value = "finds a pet by {id}",
    		notes = "",
    		nickname = "find"
	)
    @ApiResponses(
    		value = {
					@ApiResponse(
							code = 404,
							message = "not found"
					),
					@ApiResponse(
    						code = 200,
    						message = "found",
    						response = PetDomain.class
					)
    		}
	)
    public Response find(@ApiParam(value = "the id of the pet") @PathParam("id") Long id) {
    	LOGGER.info("find(id=)".replace(":id", id.toString()));

        Response res = null;

        PetDomain pet = this.petService.find(id);

        if (pet == null) {
            res = Response.status(Status.NOT_FOUND).build();
        } else {
            res = Response.ok(pet).build();
        }

        return res;
    }

    @Override
	@GET
	@ApiOperation(
    		value = "finds all pets",
    		nickname = "findAll",
    		notes = ""
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 200,
    						message = "success",
    						response = PetDomain.class
					)
    		}
	)
    public List<PetDomain> findAll() {
    	LOGGER.info("findAll()");

        List<PetDomain> pets = this.petService.findAll();

        return pets;
    }

	@Override
	@GET
	@Path("search")
	@ApiOperation(
    		value = "search pets",
    		nickname = "search",
    		notes = "WARNING: documentation for this operation is under development."
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 200,
    						message = "success",
    						response = PetDomain.class
					)
    		}
	)
    // TODO - Swagger x RESTeasy do not work well with @BeanParam.
	public List<PetDomain> search(@BeanParam PetDomainBeanParamImpl beanParameters) {
		LOGGER.info("search(beanParameters=:beanParameters)".replace(":beanParameters", beanParameters.toString()));

		List<PetDomain> pets = this.searchHelper(beanParameters);

        return pets;
	}

    @Override
	@POST
	@ApiOperation(
    		value = "creates a pet",
    		nickname = "create"
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 201,
    						message = "created"
    				),
    				@ApiResponse(
    						code = 400,
    						message = "bad syntax"
    				),
    				@ApiResponse(
    						code = 415,
    						message = "media type not supported"
    				),
    				@ApiResponse(
    						code = 422,
    						message = "unprocessable entity"
    				),
    				@ApiResponse(
    						code = 500,
    						message = "internal server error"
    				)
    		}
	)
    public Response create(@ApiParam(value = "pet to be created", required = true) PetDomain pet) {
    	LOGGER.info("create(pet=:pet)".replace(":pet", pet.toString()));

        Response res = null;

        if (this.domainValidator.isValid(pet)) {
        	res = this.createHelper(pet);
        } else {
            res = Response.status(HttpStatus.UNPROCESSABLE_ENTITY).entity(this.domainValidator.getErrors()).build();
        }

        return res;
    }

    @Override
	@PUT
    @Path("{id}")
    @ApiOperation(
    		value = "updates a pet",
    		nickname = "update"
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 204,
    						message = "updated"
					),
					@ApiResponse(
    						code = 400,
    						message = "bad syntax"
    				),
					@ApiResponse(
    						code = 415,
    						message = "media type not supported"
    				),
					@ApiResponse(
    						code = 422,
    						message = "unprocessable entity"
					),
					@ApiResponse(
    						code = 404,
    						message = "not found"
					),
					@ApiResponse(
    						code = 500,
    						message = "internal server error"
					)
    		}
	)
    public Response update(
    		@ApiParam(value = "the id of the pet") @PathParam("id") Long id,
    		@ApiParam(value = "the updated pet", required = true) PetDomain pet) {
    	LOGGER.info("update(id=:id, pet=:pet)".replace(":id", id.toString()).replace(":pet", pet.toString()));

        Response res = null;

        if (this.domainValidator.isValid(pet)) {
            res = this.updateHelper(id, pet);
        } else {
            res = Response.status(HttpStatus.UNPROCESSABLE_ENTITY).entity(this.domainValidator.getErrors()).build();
        }

        return res;
    }

    @Override
	@PATCH
    @Path("{id}")
    @ApiOperation(
    		value="updates a pet",
    		nickname = "patch",
    		notes = "allows partial update of the entity"
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 204,
    						message = "updated"
					),
					@ApiResponse(
    						code = 422,
    						message = "unprocessable entity"
					),
					@ApiResponse(
    						code = 404,
    						message = "not found"
					),
					@ApiResponse(
    						code = 500,
    						message = "internal server error"
					)
    		}
	)
    // TODO - make this operation be indexed by swagger
    public Response patch(
    		@ApiParam(value = "the id of the pet") @PathParam("id") Long id,
    		@ApiParam(value = "the pet with updated properties", required = true) PetDomain pet) {
    	LOGGER.info("patch(id=:id, pet=:pet)".replace(":id", id.toString()).replace(":pet", pet.toString()));

    	Response res = null;

        PetDomain currentPet = this.petService.find(id);

        if (currentPet != null) {
            pet.patch(currentPet);
            res = this.update(id, pet);
        } else {
            res = Response.status(Status.NOT_FOUND).build();
        }

        return res;
    }

    @Override
	@DELETE
    @Path("{id}")
    @ApiOperation(
    		value = "deletes a pet",
    		nickname = "delete"
	)
    @ApiResponses(
    		value = {
    				@ApiResponse(
    						code = 204,
    						message = "deleted"
					),
					@ApiResponse(
    						code = 404,
    						message = "not found"
					),
					@ApiResponse(
    						code = 500,
    						message = "internal server error"
					)
    		}
	)
    public Response delete(
    		@ApiParam(value = "the id of the pet") @PathParam("id") Long id) {
    	LOGGER.info("delete(id=:id)".replace(":id", id.toString()));

    	Response res = null;

        try {
            this.petService.delete(id);
            res = Response.noContent().build();
        } catch (ObjectNotFoundException e) {
            res = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
        	e.printStackTrace();
			res = Response.serverError().build();
		}

        return res;
    }

    private Response createHelper(PetDomain pet){
    	Response res;

    	if(this.userService.find(pet.getUserId()) != null){
    		try {
				this.petService.persist(pet);
				URI uri = URI.create("/pets/" + pet.getId());
	            res = Response.created(uri).build();
			} catch (Exception e) {
				e.printStackTrace();
				res = Response.serverError().build();
			}
    	} else {
    		res = this.buildUserIdNotFoundResponse();
    	}

    	return res;
    }

    private Response updateHelper(Long id, PetDomain pet){
    	Response res;

    	if(this.userService.find(pet.getUserId()) != null){
    		try {
                this.petService.update(id, pet);
                res = Response.noContent().build();
            } catch (ObjectNotFoundException e) {
                res = Response.status(Status.NOT_FOUND).build();
            } catch (Exception e) {
            	e.printStackTrace();
				res = Response.serverError().build();
            }
    	} else {
    		res = this.buildUserIdNotFoundResponse();
    	}

    	return res;
    }

    private List<PetDomain> searchHelper(PetDomainBeanParamImpl beanParameters){
    	List<PetDomain> pets = null;

    	Map<SingularAttribute<PetDomain, ?>, Object> restrictions = this.mapDomainFilterToRestrictions(beanParameters);

    	if(restrictions != null && !restrictions.isEmpty()){
    		pets = this.petService.findAll(restrictions);

    		if(pets != null && !pets.isEmpty()){
    			this.setPetDomainPropertiesToBeDisplayed(pets, beanParameters.getPropertiesToBeDisplayed());
        	}
    	}

    	return pets;
    }

    private Response buildUserIdNotFoundResponse(){
    	// TODO
		// move error defined messages to a file
		Map<String, Object> errors = DomainValidatorJPAImpl.buildError("userId", "not found");
		return Response.status(HttpStatus.UNPROCESSABLE_ENTITY).entity(errors).build();
    }

	private Map<SingularAttribute<PetDomain, ?>, Object> mapDomainFilterToRestrictions(PetDomainBeanParamImpl petDomainFilter){
    	Map<SingularAttribute<PetDomain, ?>, Object> restrictions = new HashMap<SingularAttribute<PetDomain, ?>, Object>();

    	if(petDomainFilter.getName() != null && !petDomainFilter.equals("")){
    		restrictions.put(PetDomain_.name, petDomainFilter.getName());
    	}

    	if(petDomainFilter.getAge() > 0){
    		restrictions.put(PetDomain_.age, petDomainFilter.getAge());
    	}

    	if(petDomainFilter.getUserId() != null){
    		UserDomain userDomain = new UserDomain();
    		userDomain.setId(petDomainFilter.getUserId());

    		restrictions.put(PetDomain_.user, userDomain);
    	}

    	return restrictions;
    }

	// TODO - find a fancy class for this method
    private void setPetDomainPropertiesToBeDisplayed(List<PetDomain> pets, List<String> propertiesToBeDisplayed){
    	for(PetDomain pet : pets){
    		pet.setPropertiesToBeDisplayed(propertiesToBeDisplayed);
    	}
    }
}
