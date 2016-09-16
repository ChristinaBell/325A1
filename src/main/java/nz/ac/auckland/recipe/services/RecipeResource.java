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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nz.ac.auckland.recipe.domain.Review;
import nz.ac.auckland.recipe.domain.Recipe;
import nz.ac.auckland.recipe.domain.Baker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class RecipeResource {

	
	// Setup a Logger.
	private static Logger _logger = LoggerFactory
				.getLogger(RecipeResource.class);
		
	private Map<Long, Recipe> _recipeDB = new ConcurrentHashMap<Long,Recipe>();
	private AtomicInteger _idCounter = new AtomicInteger();
	
	@GET
	@Path("{id}")
	@Produces("application/xml")
	public StreamingOutput retrieveRecipe(@PathParam("id") int id) {
		_logger.info("Retrieving recipe with id: " + id);
		// Lookup the Recipe within the in-memory data structure.
		final Recipe recipe = _recipeDB.get(id);
		if (recipe == null) {
			// Return a HTTP 404 response if the specified Recipe isn't found.
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Return a StreamingOuput instance that the JAX-RS implementation will
		// use to set the body of the HTTP response message.
		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException,
					WebApplicationException {
//				outputRecipe(outputStream, recipe);
			}
		};
	}
	
	/**
	 * Attempts to retrieve a collection of recipes. 
	 * 
	 * @param start the start index within the database of recipes.
	 * 
	 * @param size the number of recipes to return.
	 * 
	 * @return a StreamingOutput object that writes out up to a maximum of size 
	 *         recipes in XML form.  
	 */
	@GET
	@Produces("application/xml")
	public StreamingOutput retrieveRecipes(@QueryParam("start") int start, @QueryParam("size") int size) {
		final List<Recipe> recipes = new ArrayList<Recipe>();

		for (int i = start; i <= size; i++) {
			Recipe recipe = _recipeDB.get(i);
			if (recipe != null) {
				recipes.add(recipe);
			}
		}

		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException,
					WebApplicationException {
				outputRecipes(outputStream, recipes);
			}
		};
	}

	/**
	 * Creates a new recipe.
	 * 
	 * @param is the Inputstream that contains an XML representation of the
	 * recipe to be created.
	 * 
	 * @return a Response object that includes the HTTP "Location" header,
	 *         whose value is the URI of the newly created resource. The HTTP 
	 *         response code is 201. The JAX-RS run-time processes the Response
	 *         object when preparing the HTTP response message.
	 */
	@POST
	@Produces("application/xml")
	public Response createRecipe(InputStream is) {
		// Read an XML representation of a new Recipe. Note that with JAX-RS, 
		// any non-annotated parameter in a Resource method is assumed to hold 
		// the HTTP request's message body.
		Recipe recipe = readRecipe(is);

		// Generate an ID for the new Recipe, and store it in memory.
		recipe.setId(_idCounter.incrementAndGet());
		_recipeDB.put(recipe.getId(), recipe);

		_logger.debug("Created recipe with id: " + recipe.getId());

		return Response.created(URI.create("/recipes/" + recipe.getId()))
				.build();
	}

	/**
	 * Attempts to update an existing recipe. If the specified recipe is
	 * found it is updated, resulting in a HTTP 204 response being returned to 
	 * the consumer. In other cases, a 404 response is returned.
	 * 
	 * @param id the unique id of the recipe to update.
	 * 
	 * @param is the InputStream used to store an XML representation of the
	 * new state for the recipe.
	 */
	@PUT
	@Path("{id}")
	@Consumes("application/xml")
	public void updateRecipe(@PathParam("id") int id, InputStream is) {
		Recipe update = readRecipe(is);
		Recipe current = _recipeDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Update the details of the Recipe to be updated.
		current.setContent(update.getContent());
		current.setCreationTimeStamp(update.getCreationTimeStamp());
	}

	/**
	 * Attempts to delete an existing recipe. If the specified recipe isn't 
	 * found, a 404 response is returned to the consumer. In other cases, a 204
	 * response is returned after deleting the recipe.
	 * 
	 * @param id the unique id of the recipe to delete.
	 */
	@DELETE
	@Path("{id}")
	public void deleteRecipe(@PathParam("id") int id) {
		Recipe current = _recipeDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Remove the Recipe.
		_recipeDB.remove(id);
		_logger.info("Deleted recipe with ID: " + id);
	}

	/**
	 * Helper method to generate an XML representation of a particular recipe.
	 * 
	 * @param os the OutputStream used to write out the XML.
	 * 
	 * @param recipe the recipe for which to generate an XML representation.
	 * 
	 * @throws IOException if an error is encountered in writing the XML to the 
	 * OutputStream.
	 */
	protected void outputRecipe(OutputStream os, Recipe recipe)
			throws IOException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

		PrintStream writer = new PrintStream(os);
		writer.println("<recipe id=\"" + recipe.getId() + "\">");
		writer.println("   <content>" + recipe.getContent()
				+ "</content>");
		writer.println("   <creation-time-stamp>" + recipe.getCreationTimeStamp()
				+ "</last-name>");
		writer.println("</recipe>");
	}

	/**
	 * Helper method to generate an XML representation for a collection of 
	 * recipes.
	 */
	protected void outputRecipes(OutputStream os, List<Recipe> recipes)
			throws IOException {
		for (Recipe recipe : recipes) {
			outputRecipe(os, recipe);
		}
	}

	/**
	 * Helper method to read an XML representation of a Recipe, and return a
	 * corresponding Recipe object. This method uses the org.w3c API for 
	 * parsing XML. The details aren't important, and shortly we'll use an
	 * automated approach rather than having to do this by hand. Currently this
	 * is a minimal Web service and so we'll parse the XML by hand.
	 * 
	 * @param is the InputStream containing an XML representation of the 
	 *        recipe to create.
	 *        
	 * @return a new Recipe object.
	 */
	protected Recipe readRecipe(InputStream is) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(is);
			Element root = doc.getDocumentElement();

			Recipe recipe = new Recipe();
			if (root.getAttribute("id") != null
					&& !root.getAttribute("id").trim().equals(""))
				recipe.setId(Integer.valueOf(root.getAttribute("id")));
			NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				if (element.getTagName().equals("content")) {
					recipe.setContent(element.getTextContent());
				} 
				else if (element.getTagName().equals("creation-time-stamp")) {
					DateTimeFormatter formatter = DateTimeFormat
							.forPattern("dd/MM/yyyy");
					DateTime creation = formatter.parseDateTime(element
							.getTextContent());
					recipe.setCreationTimeStamp(creation);
				}
			}
			return recipe;
		} catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}

}
	

