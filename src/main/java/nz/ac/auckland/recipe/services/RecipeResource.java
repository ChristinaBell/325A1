package nz.ac.auckland.recipe.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nz.ac.auckland.recipe.PersistenceManager;
import nz.ac.auckland.recipe.domain.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class to implement a simple REST Web service for managing recipes.
 * 
 * recipeResource implements a WEB service with the following interface: - GET
 * <base-uri>/recipes/{id} Retrieves a recipe based on their unique id. The
 * format of the returned data is XML.
 * 
 * - GET <base-uri>/recipes?start&size Retrieves a collection of recipes, where
 * the "start" query parameter identifies an index position, and "size"
 * represents the max number of successive recipes to return. The format of the
 * returned data is XML.
 * 
 * - POST <base-uri>/recipes Creates a new recipe. The HTTP post message
 * contains an XML representation of the recipe to be created.
 * 
 * - PUT <base-uri>/recipes/{id} Updates a recipe, identified by their id.The
 * HTTP PUT message contains an XML document describing the new state of the
 * recipe.
 * 
 * - DELETE <base-uri>/recipes/{id} Deletes a recipe, identified by their unique
 * id.
 * 
 * @author Christina Bell
 *
 */

@Path("/recipeBlogs")
public class RecipeResource {

	// Setup a Logger.
	private static Logger _logger = LoggerFactory
			.getLogger(RecipeResource.class);

	/*
	 * This method retrieves a recipe based on the recipe's id.
	 * 
	 * @return a StreamingOutput object that writes out up recipes in XML form.
	 */
	@GET
	@Path("/recipes/{id}")
	@Produces("application/xml")
	public nz.ac.auckland.recipe.dto.Recipe retrieveRecipe(
			@PathParam("id") Long id) {
		_logger.info("Retrieving recipe with id: " + id);
		EntityManager em = PersistenceManager.instance().createEntityManager();
		// Lookup the Recipe with the entity manager.
		final Recipe recipe = em.find(Recipe.class, id);
		em.close();
		nz.ac.auckland.recipe.dto.Recipe dtoRecipe = RecipeMapper.toDto(recipe);
		return dtoRecipe;

	}

	/**
	 * Creates a new recipe.
	 * 
	 * @param is
	 *            the dto recipe object.
	 * 
	 * @return a Response object that includes the HTTP "Location" header, whose
	 *         value is the URI of the newly created resource. The HTTP response
	 *         code is 201. The JAX-RS run-time processes the Response object
	 *         when preparing the HTTP response message.
	 */
	@POST
	@Produces("application/xml")
	public Response createRecipe(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
		// Read an XML representation of a new Recipe. Note that with JAX-RS,
		// any non-annotated parameter in a Resource method is assumed to hold
		// the HTTP request's message body.
		_logger.debug("Creating recipe with id: " + dtoRecipe.getId());
		Recipe recipe = RecipeMapper.toDomainModel(dtoRecipe);
		EntityManager em = PersistenceManager.instance().createEntityManager();
		EntityTransaction trans = em.getTransaction();
		trans.begin();
		em.persist(recipe);
		trans.commit();
		em.close();

		_logger.debug("Created recipe with id: " + recipe.getId());
		return Response.created(URI.create("/recipes/" + recipe.getId()))
				.build();
	}

	/**
	 * Attempts to update an existing recipe. If the specified recipe is found
	 * it is updated, resulting in a HTTP 204 response being returned to the
	 * consumer. In other cases, a 404 response is returned.
	 * 
	 * @param id
	 *            the unique id of the recipe to update.
	 * 
	 * @param is
	 *            the InputStream used to store an XML representation of the new
	 *            state for the recipe.
	 */
	@PUT
	@Path("/recipes/{id}")
	@Consumes("application/xml")
	public void updateRecipe(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
		EntityManager em = PersistenceManager.instance().createEntityManager(); 
		Recipe recipe = em.find(Recipe.class, dtoRecipe.getId()); 
		EntityTransaction trans = em.getTransaction();
		trans.begin();
		// Update the details of the Recipe to be updated.
		recipe.setAuthor(dtoRecipe.getAuthor());
		recipe.setContent(dtoRecipe.getContent());
		recipe.setCategory(dtoRecipe.getCategory());
		recipe.setName(dtoRecipe.getName());
		recipe.updateReview(dtoRecipe.getReviews());
		trans.commit();
		em.close();

	}

	/**
	 * Attempts to delete an existing recipe. If the specified recipe isn't
	 * found, a 404 response is returned to the consumer. In other cases, a 204
	 * response is returned after deleting the recipe.
	 * 
	 * @param id
	 *            the unique id of the recipe to delete.
	 */
	@DELETE
	@Path("/recipes/{id}")
	public void deleteRecipe(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
		Recipe recipe = RecipeMapper.toDomainModel(dtoRecipe);
		EntityManager em = PersistenceManager.instance().createEntityManager();
		EntityTransaction trans = em.getTransaction();
		trans.begin();
		em.remove(recipe);
		trans.commit();
		em.close();

		_logger.info("Deleted recipe with ID: " + recipe.getId());
	}

	// // --------------------------------- baker http methods ------------
	// // -----------------------------------------------------------------
	//
	// /*
	// * This method retrieves a recipe based on the recipe's id.
	// *
	// * @return a StreamingOutput object that writes out up recipes in XML
	// form.
	// */
	// @GET
	// @Path("/recipes/{id}")
	// @Produces("application/xml")
	// public StreamingOutput retrieveBaker(@PathParam("id") Long id) {
	// _logger.info("Retrieving baker with id: " + id);
	// EntityManager em = PersistenceManager.instance().createEntityManager();
	//
	// // Lookup the Recipe within the in-memory data structure.
	// final Baker baker = em.find(Baker.class, id);
	// em.close();
	// if (baker == null) {
	// // Return a HTTP 404 response if the specified Recipe isn't found.
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	//
	// // Return a StreamingOuput instance that the JAX-RS implementation will
	// // use to set the body of the HTTP response message.
	// return new StreamingOutput() {
	// public void write(OutputStream outputStream) throws IOException,
	// WebApplicationException {
	// outputBaker(outputStream, baker);
	// }
	// };
	// }
	//
	//
	// /**
	// * Creates a new recipe.
	// *
	// * @param is
	// * the Inputstream that contains an XML representation of the
	// * recipe to be created.
	// *
	// * @return a Response object that includes the HTTP "Location" header,
	// whose
	// * value is the URI of the newly created resource. The HTTP response
	// * code is 201. The JAX-RS run-time processes the Response object
	// * when preparing the HTTP response message.
	// */
	// @POST
	// @Produces("application/xml")
	// public Response createBaker(nz.ac.auckland.recipe.dto.Baker dtoBaker) {
	// // Read an XML representation of a new Recipe. Note that with JAX-RS,
	// // any non-annotated parameter in a Resource method is assumed to hold
	// // the HTTP request's message body.
	// _logger.debug("Creating recipe with id: " + dtoBaker.getId());
	// Baker baker = RecipeMapper.toDomainModel(dtoBaker);
	// EntityManager em = PersistenceManager.instance().createEntityManager();
	// EntityTransaction trans = em.getTransaction();
	// trans.begin();
	// em.persist(baker);
	// trans.commit();
	// em.close();
	//
	// _logger.debug("Created recipe with id: " + baker.getId());
	// return Response.created(URI.create("/recipes/" + baker.getId()))
	// .build();
	// }
	//
	// /**
	// * Attempts to update an existing recipe. If the specified recipe is found
	// * it is updated, resulting in a HTTP 204 response being returned to the
	// * consumer. In other cases, a 404 response is returned.
	// *
	// * @param id
	// * the unique id of the recipe to update.
	// *
	// * @param is
	// * the InputStream used to store an XML representation of the new
	// * state for the recipe.
	// */
	// @PUT
	// @Path("/recipes/{id}")
	// @Consumes("application/xml")
	// public void updateRecipe(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
	// Recipe recipe = RecipeMapper.toDomainModel(dtoRecipe);
	//
	// if (recipe == null) {
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	//
	// // Update the details of the Recipe to be updated.
	// recipe.setAuthor(dtoRecipe.getAuthor());
	// recipe.setContent(dtoRecipe.getContent());
	// recipe.setCategory(dtoRecipe.getCategory());
	// recipe.setName(dtoRecipe.getName());
	// recipe.updateReview(dtoRecipe.getReviews());
	//
	// }
	//
	// /**
	// * Attempts to delete an existing recipe. If the specified recipe isn't
	// * found, a 404 response is returned to the consumer. In other cases, a
	// 204
	// * response is returned after deleting the recipe.
	// *
	// * @param id
	// * the unique id of the recipe to delete.
	// */
	// @DELETE
	// @Path("/recipes/{id}")
	// public void deleteRecipe(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
	// Recipe recipe = RecipeMapper.toDomainModel(dtoRecipe);
	//
	// if (recipe == null) {
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	//
	// // Remove the Recipe.
	// recipe = null;
	// _logger.info("Deleted recipe with ID: " + recipe.getId());
	// }
	//
	// /**
	// * Helper method to generate an XML representation of a particular recipe.
	// *
	// * @param os
	// * the OutputStream used to write out the XML.
	// *
	// * @param recipe
	// * the recipe for which to generate an XML representation.
	// *
	// * @throws IOException
	// * if an error is encountered in writing the XML to the
	// * OutputStream.
	// */
	// protected void outputRecipe(OutputStream os, Recipe recipe)
	// throws IOException {
	// DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
	//
	// PrintStream writer = new PrintStream(os);
	// writer.println("<recipe>");
	// writer.println("	<id>" + recipe.getId() + "</id>");
	// writer.println("	<author>" + recipe.getAuthor() + "</author>");
	// writer.println("   	<content>" + recipe.getContent() + "</content>");
	// writer.println("	<reviews>" + recipe.getReviews() + "</reviews>");
	// writer.println("	<category>" + recipe.getCategory() + "</category>");
	// writer.println("</recipe>");
	// }
	//
	// /**
	// * Helper method to generate an XML representation for a collection of
	// * recipes.
	// */
	// protected void outputRecipes(OutputStream os, List<Recipe> recipes)
	// throws IOException {
	// for (Recipe recipe : recipes) {
	// outputRecipe(os, recipe);
	// }
	// }
	//
	// /**
	// * Helper method to read an XML representation of a Recipe, and return a
	// * corresponding Recipe object. This method uses the org.w3c API for
	// parsing
	// * XML. The details aren't important, and shortly we'll use an automated
	// * approach rather than having to do this by hand. Currently this is a
	// * minimal Web service and so we'll parse the XML by hand.
	// *
	// * @param is
	// * the InputStream containing an XML representation of the recipe
	// * to create.
	// *
	// * @return a new Recipe object.
	// */
	// protected Recipe readRecipe(InputStream is) {
	// try {
	// DocumentBuilder builder = DocumentBuilderFactory.newInstance()
	// .newDocumentBuilder();
	// Document doc = builder.parse(is);
	// Element root = doc.getDocumentElement();
	//
	// Recipe recipe = new Recipe();
	// if (root.getAttribute("id") != null
	// && !root.getAttribute("id").trim().equals(""))
	// recipe.setId(Integer.valueOf(root.getAttribute("id")));
	// NodeList nodes = root.getChildNodes();
	// for (int i = 0; i < nodes.getLength(); i++) {
	// Element element = (Element) nodes.item(i);
	// if (element.getTagName().equals("content")) {
	// recipe.setContent(element.getTextContent());
	// }
	// }
	// return recipe;
	// } catch (Exception e) {
	// throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
	// }
	// }
	//
	//

}
