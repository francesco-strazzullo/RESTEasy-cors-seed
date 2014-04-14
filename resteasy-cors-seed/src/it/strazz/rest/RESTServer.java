package it.strazz.rest;

import it.strazz.rest.model.Person;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

@Path("/")
public class RESTServer {

	private static Gson GSON = new GsonBuilder().create();

	@GET
	@Path("/user/")
	@Produces("application/json")
	public Response getPeople() {
		return Response.ok(GSON.toJson(Person.getAll())).build();
	}

	@GET
	@Path("/user/{id}")
	@Produces("application/json")
	public Response getPerson(@PathParam(value = "id") Integer id) {

		Status status;
		String responseBody = "";

		Person person = Person.get(id);
		if (person != null) {
			status = Response.Status.OK;
			responseBody = GSON.toJson(person);
		} else {
			status = Response.Status.NOT_FOUND;
		}

		return Response.status(status).entity(responseBody).build();
	}

	@POST
	@Path("/user/")
	@Consumes("application/json")
	public Response createPerson(@Context HttpServletRequest request) {
		return executeStore(null, request);
	}
	
	@PUT
	@Path("/user/{id}")
	@Consumes("application/json")
	public Response updatePerson(@PathParam(value = "id") Integer id,@Context HttpServletRequest request) {
		return executeStore(id, request);
	}

	protected Response executeStore(Integer id, HttpServletRequest request) {

		Status status = Status.OK;
		String responseBody = "";
		String requestBody = "";
		Person p = null;
		
		/*
		 * Carico il body in una stringa
		 */
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(request.getInputStream(), baos);
			requestBody = new String(baos.toByteArray());
		} catch (IOException e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		/*
		 * Effettuo il parse del JSON
		 */
		try {
			p = GSON.fromJson(requestBody, Person.class);
		} catch (JsonSyntaxException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

		if(id == null){
			//Create
			if(p.getId() != null){
				status = Status.BAD_REQUEST;
			}
		}else{
			//Update
			if (p.getId() == null || !p.getId().equals(id)) {
				status = Status.BAD_REQUEST;
			} else if (Person.get(p.getId()) == null) {
				status = Status.NOT_FOUND;
			}
		}
		
		if(Status.OK.equals(status)){
			p = Person.store(p);
			responseBody = GSON.toJson(p);
		}
		
		return Response.status(status).entity(responseBody).build();
	}

	@DELETE
	@Path("/user/{id}")
	public Response deletePerson(@PathParam(value = "id") Integer id) {

		Status status;

		Person p = Person.get(id);

		if (p == null) {
			status = Status.NOT_FOUND;
		} else {
			status = Status.OK;
			Person.delete(p);
		}

		return Response.status(status).build();
	}

	@OPTIONS
	@Path("/{path:.*}")
	public Response handleCORSRequest(
			@HeaderParam("Access-Control-Request-Method") final String requestMethod,
			@HeaderParam("Access-Control-Request-Headers") final String requestHeaders) {

		final ResponseBuilder retValue = Response.ok();

		retValue.header("Access-Control-Allow-Origin", "*");

		if (requestHeaders != null) {
			retValue.header("Access-Control-Allow-Headers", requestHeaders);
		}

		if (requestMethod != null) {
			retValue.header("Access-Control-Allow-Methods", requestMethod);
		}

		return retValue.build();
	}
}
