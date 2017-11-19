package com.itesm.oauth.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author mklfarha
 * Error mapper, to display a json error to the user.
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception ex) {
    return Response.status(Response.Status.BAD_REQUEST).entity("Error interno: <a href='/login'>regresar</a>").type(MediaType.TEXT_HTML).build();
  }

}
