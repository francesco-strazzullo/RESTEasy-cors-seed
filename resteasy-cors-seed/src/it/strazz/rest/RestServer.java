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

@Path("/")
public class RestServer {

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
		if(person != null){
			status = Response.Status.OK;
			responseBody = GSON.toJson(person);
		}else{
			status = Response.Status.NOT_FOUND;
		}
		
		return Response.status(status).entity(responseBody).build();
	}
	
	private String extractBodyFromRequest(HttpServletRequest request){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(request.getInputStream(), baos);
			return new String(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@POST
	@Path("/user/")
	@Consumes("application/json")
	public Response createPerson(@Context HttpServletRequest request){
		
		Status status;
		String responseBody = "";
		
		String requestBody = extractBodyFromRequest(request);
		
		Person p = GSON.fromJson(requestBody, Person.class);
		
		if(p.getId() != null){
			status = Status.BAD_REQUEST;
		}else{
			status = Status.OK;
			p = Person.add(p);
			responseBody = GSON.toJson(p);
		}

		return Response.status(status).entity(responseBody).build();
	}
	
	@PUT
	@Path("/user/{id}")
	@Consumes("application/json")
	public Response updatePerson(@Context HttpServletRequest request){
		
		Status status;
		String responseBody = "";
		
		String requestBody = extractBodyFromRequest(request);
		
		Person p = GSON.fromJson(requestBody, Person.class);
		
		if(p.getId() == null){
			status = Status.BAD_REQUEST;
		}else{
			if(Person.get(p.getId()) == null){
				status = Status.NOT_FOUND;
			}else{
				status = Status.OK;
				p = Person.update(p);
				responseBody = GSON.toJson(p);
			}
		}
		
		return Response.status(status).entity(responseBody).build();
	}

	@DELETE
	@Path("/user/{id}")
	public Response deletePerson(@PathParam(value = "id") Integer id){

		Status status;
		
		Person p = Person.get(id);
		
		if(p == null){
			status = Status.NOT_FOUND;
		}else{
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

		if (requestHeaders != null){
			retValue.header("Access-Control-Allow-Headers", requestHeaders);
		}
		
		if (requestMethod != null){
			retValue.header("Access-Control-Allow-Methods", requestMethod);
		}

		return retValue.build();
	}
}
