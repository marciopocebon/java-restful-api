package com.mario.java.restful.api.hibernate.jpa.resource;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.ObjectNotFoundException;

import com.mario.java.restful.api.hibernate.jpa.domain.UserDomain;
import com.mario.java.restful.api.hibernate.jpa.resource.exception.HibernateValidationExceptionHandler;
import com.mario.java.restful.api.hibernate.jpa.service.UserService;

@Path("/users")
@Produces("application/json")
public class UserResource {

    private UserService service = new UserService();
    private Map<String, String> errors;
    private HibernateValidationExceptionHandler validator = new HibernateValidationExceptionHandler();

    @GET
    public List<UserDomain> findAll() {
        List<UserDomain> users = this.service.findAll();

        return users;
    }
    
    @GET
    @Path("{id}")
    public Response find(@PathParam("id") Long id) {
        Response res = null;
        
    	UserDomain user = this.service.find(id);
        
        if(user == null) {
        	res = Response.status(Status.NOT_FOUND).build();
        } else {
        	res = Response.ok(user).build();
        }

        return res;
    }

    @POST
    public Response create(UserDomain user) {
        Response res = null;

        if (this.validator.isValid(user)) {
            this.service.persist(user);
            URI uri = URI.create("/users/" + user.getId());
            res = Response.created(uri).build();
        } else {
            this.errors = this.validator.getErrors();
            res = Response.status(Status.BAD_REQUEST).entity(this.errors)
                    .build();
        }

        return res;
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, UserDomain user) {
        Response res = null;

        if (user.isValid()) {
        	
        	try {
        		this.service.update(id, user);
        		res = Response.noContent().build();
        	} catch (ObjectNotFoundException e) {
				res = Response.status(Status.NOT_FOUND).build();
			}
        	
        } else {
            this.errors = user.getErrors();
            res = Response.status(Status.BAD_REQUEST).entity(this.errors)
                    .build();
        }

        return res;
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        this.service.delete(id);

        return Response.noContent().build();
    }
}
