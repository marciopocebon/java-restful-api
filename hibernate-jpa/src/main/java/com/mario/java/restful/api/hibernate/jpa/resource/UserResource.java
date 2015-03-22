package com.mario.java.restful.api.hibernate.jpa.resource;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
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

import com.mario.java.restful.api.hibernate.jpa.domain.UserDomain;
import com.mario.java.restful.api.hibernate.jpa.repository.exception.ObjectNofFoundException;
import com.mario.java.restful.api.hibernate.jpa.resource.response.HttpStatus;
import com.mario.java.restful.api.hibernate.jpa.service.UserService;
import com.mario.java.restful.api.hibernate.jpa.service.impl.UserServiceImpl;

@Path("/users")
@Consumes("application/json")
@Produces("application/json")
@RequestScoped
public class UserResource {

	private UserService userService;

    public UserResource(){
    	this(new UserServiceImpl());
    }

    public UserResource(UserService service) {
        this.userService = service;
    }

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") Long id) {
        Response res = null;

        UserDomain user = this.userService.find(id);

        if (user == null) {
            res = Response.status(Status.NOT_FOUND).build();
        } else {
            res = Response.ok(user).build();
        }

        return res;
    }

    @GET
    public List<UserDomain> findAll() {
        List<UserDomain> users = this.userService.findAll();

        return users;
    }

    @POST
    public Response create(UserDomain user) {
        Response res = null;

        if (user.isValid()) {
            this.createHelper(user);
        } else {
            res = Response.status(HttpStatus.UNPROCESSABLE_ENTITY).entity(user.getErrors()).build();
        }

        return res;
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, UserDomain user) {
        Response res = null;

        if (user.isValid()) {
        	res = this.updateHelper(id, user);
        } else {
            res = Response.status(HttpStatus.UNPROCESSABLE_ENTITY).entity(user.getErrors()).build();
        }

        return res;
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Response res = null;

        try {
            this.userService.delete(id);
            res = Response.noContent().build();
        } catch (ObjectNofFoundException e) {
            res = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
        	e.printStackTrace();
        	res = Response.serverError().build();
        }

        return res;
    }

    private Response updateHelper(Long id, UserDomain user){
    	Response res;

    	try {
            this.userService.update(id, user);
            res = Response.noContent().build();
        } catch (ObjectNofFoundException e) {
            res = Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			e.printStackTrace();
			res = Response.serverError().build();
		}

    	return res;
    }

    private Response createHelper(UserDomain userDomain){
    	Response res;

    	try {
			this.userService.persist(userDomain);
			URI uri = URI.create("/users/" + userDomain.getId());
            res = Response.created(uri).build();
		} catch (Exception e) {
			e.printStackTrace();
			res = Response.serverError().build();
		}

    	return res;
    }
}
