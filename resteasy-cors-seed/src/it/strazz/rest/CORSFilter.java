package it.strazz.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter
{
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public void filter(ContainerRequestContext requestContext,ContainerResponseContext responseContext) throws IOException {
		if(!responseContext.getHeaders().containsKey(ACCESS_CONTROL_ALLOW_ORIGIN)){
			responseContext.getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		}
	}
}