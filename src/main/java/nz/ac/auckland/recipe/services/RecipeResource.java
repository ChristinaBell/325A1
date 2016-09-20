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
 * Class to implement a simple REST Web service for managing parolees.
 * 
 * ParoleeResource implements a WEB service with the following interface: - GET
 * <base-uri>/parolees/{id} Retrieves a parolee based on their unique id. The
 * format of the returned data is XML.
 * 
 * - GET <base-uri>/parolees?start&size Retrieves a collection of parolees,
 * where the "start" query parameter identifies an index position, and "size"
 * represents the max number of successive parolees to return. The format of the
 * returned data is XML.
 * 
 * - POST <base-uri>/parolees Creates a new Parolee. The HTTP post message
 * contains an XML representation of the parolee to be created.
 * 
 * - PUT <base-uri>/parolees/{id} Updates a parolee, identified by their id.The
 * HTTP PUT message contains an XML document describing the new state of the
 * parolee.
 * 
 * - DELETE <base-uri>/parolees/{id} Deletes a parolee, identified by their
 * unique id.
 * 
 * @author Ian Warren
 *
 */

@Path("/recipeBlogs")
public class RecipeResource {

	// Setup a Logger.
	private static Logger _logger = LoggerFactory
			.getLogger(RecipeResource.class);

	private Map<Long, Recipe> _recipeDB = new ConcurrentHashMap<Long, Recipe>();
//	private Map<Long, Baker> _bakerDB = new ConcurrentHashMap<Long, Baker>();
//	private Map<Long, Category> _categoryDB = new ConcurrentHashMap<Long, Category>();
//	private Map<Long, Review> _reviewDB = new ConcurrentHashMap<Long, Review>();
//	private Map<Long, Wishlist> _wishlistDB = new ConcurrentHashMap<Long, Wishlist>();
//	private AtomicInteger _idCounter = new AtomicInteger();

	@GET
	@Path("/recipes/{id}")
	@Produces("application/xml")
	public StreamingOutput retrieveRecipe(@PathParam("id") Long id) {
		_logger.info("Retrieving recipe with id: " + id);
		EntityManager em = PersistenceManager.instance().createEntityManager(); 
		// Lookup the Recipe within the in-memory data structure.
		final Recipe recipe = em.find(Recipe.class, id); 
		em.close(); 
		if (recipe == null) {
			// Return a HTTP 404 response if the specified Recipe isn't found.
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Return a StreamingOuput instance that the JAX-RS implementation will
		// use to set the body of the HTTP response message.
		return new StreamingOutput() {
			public void write(OutputStream outputStream) throws IOException,
					WebApplicationException {
				outputRecipe(outputStream, recipe);
			}
		};
	}

	/**
	 * Attempts to retrieve a collection of recipes.
	 * 
	 * @param author
	 *            - within the database of recipes.
	 * 
	 * @return a StreamingOutput object that writes out up recipes in XML form.
	 */
	@GET
	@Produces("application/xml")
	public StreamingOutput retrieveRecipes(@QueryParam("author") Baker author) {
		_logger.info("Retrieving recipes by author"); 
		final List<Recipe> recipes = new ArrayList<Recipe>();
		int length = _recipeDB.size(); 
				
		for (int i = length; i <= length; i++) {
			Recipe recipe = _recipeDB.get(i);
			if ((recipe != null) && recipe.getAuthor().equals(author)){
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
	 * Attempts to retrieve a collection of recipes by category.
	 * 
	 * @param category
	 *            - within the database of recipes.
	 * 
	 * @return a StreamingOutput object that writes out up recipes in XML form.
	 */
	@GET
	@Produces("application/xml")
	public StreamingOutput retrieveRecipes(@QueryParam("category") Category category) {
		_logger.info("Retrieving recipes by category"); 
		final List<Recipe> recipes = new ArrayList<Recipe>();
		int length = _recipeDB.size(); 
				
		for (int i = length; i <= length; i++) {
			Recipe recipe = _recipeDB.get(i);
			if ((recipe != null) && recipe.getCategory().equals(category)){
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
	 * @param is
	 *            the Inputstream that contains an XML representation of the
	 *            recipe to be created.
	 * 
	 * @return a Response object that includes the HTTP "Location" header, whose
	 *         value is the URI of the newly created resource. The HTTP response
	 *         code is 201. The JAX-RS run-time processes the Response object
	 *         when preparing the HTTP response message.
	 */
	@POST
	@Produces("application/xml")
	public Response createRecipe(InputStream is) {
		// Read an XML representation of a new Recipe. Note that with JAX-RS,
		// any non-annotated parameter in a Resource method is assumed to hold
		// the HTTP request's message body.
		Recipe recipe = null; 
		EntityManager em = PersistenceManager.instance().createEntityManager(); 
		EntityTransaction trans = em.getTransaction(); 
		trans.begin(); 
		
		// try to make a new instance of jaxb context 
		try{
			// 
			JAXBContext jaxbContext; 
			jaxbContext = JAXBContext.newInstance(Recipe.class);
			
			// 
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller(); 
			recipe = (Recipe) unmarsh.unmarshal(is); 
			
			em.persist(recipe);
			trans.commit(); 
		} catch (JAXBException e) {
			// log pls 
			
		}
		
		em.close(); 

		// Generate an ID for the new Recipe, and store it in memory.
//		recipe.setId(_idCounter.incrementAndGet());
//		_recipeDB.put(recipe.getId(), recipe);

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
	@Path("{id}")
	@Consumes("application/xml")
	public void updateRecipe(@PathParam("id") Long id, InputStream is) {
		Recipe update = readRecipe(is);
		Recipe current = _recipeDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Update the details of the Recipe to be updated.
		current.setContent(update.getContent());
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
	 * @param os
	 *            the OutputStream used to write out the XML.
	 * 
	 * @param recipe
	 *            the recipe for which to generate an XML representation.
	 * 
	 * @throws IOException
	 *             if an error is encountered in writing the XML to the
	 *             OutputStream.
	 */
	protected void outputRecipe(OutputStream os, Recipe recipe)
			throws IOException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

		PrintStream writer = new PrintStream(os);
		writer.println("<recipe>");
		writer.println("	<id>" + recipe.getId() + "</id>");
		writer.println("	<author>" + recipe.getAuthor() + "</author>");
		writer.println("   	<content>" + recipe.getContent() + "</content>");
		writer.println("	<reviews>" + recipe.getReviews() + "</reviews>");
		writer.println("	<category>" + recipe.getCategory() + "</category>");
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
	 * corresponding Recipe object. This method uses the org.w3c API for parsing
	 * XML. The details aren't important, and shortly we'll use an automated
	 * approach rather than having to do this by hand. Currently this is a
	 * minimal Web service and so we'll parse the XML by hand.
	 * 
	 * @param is
	 *            the InputStream containing an XML representation of the recipe
	 *            to create.
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
			}
			return recipe;
		} catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}
//
//	// -------------------------------------------------------- baker http
//	// methods --------------------------------------------------------
//
//	/*
//	 * Get baker by id
//	 */
//	@GET
//	@Path("{id}")
//	@Produces("application/xml")
//	public StreamingOutput retrieveBaker(@PathParam("id") Long id) {
//		_logger.info("Retrieving baker with id: " + id);
//		// Lookup the Recipe within the in-memory data structure.
//		final Baker baker = _bakerDB.get(id);
//		if (baker == null) {
//			// Return a HTTP 404 response if the specified Recipe isn't found.
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Return a StreamingOuput instance that the JAX-RS implementation will
//		// use to set the body of the HTTP response message.
//		return new StreamingOutput() {
//			public void write(OutputStream outputStream) throws IOException,
//					WebApplicationException {
//				outputBaker(outputStream, baker);
//			}
//		};
//	}
//
//	/**
//	 * Creates a new baker.
//	 * 
//	 * @param is
//	 *            the Inputstream that contains an XML representation of the
//	 *            recipe to be created.
//	 * 
//	 * @return a Response object that includes the HTTP "Location" header, whose
//	 *         value is the URI of the newly created resource. The HTTP response
//	 *         code is 201. The JAX-RS run-time processes the Response object
//	 *         when preparing the HTTP response message.
//	 */
//	@POST
//	@Produces("application/xml")
//	public Response createBaker(InputStream is) {
//		// Read an XML representation of a new Recipe. Note that with JAX-RS,
//		// any non-annotated parameter in a Resource method is assumed to hold
//		// the HTTP request's message body.
//		Baker baker = readBaker(is);
//
//		// Generate an ID for the new Recipe, and store it in memory.
//		baker.setId(Long.valueOf(_idCounter.incrementAndGet()));
//		_bakerDB.put(baker.getId(), baker);
//
//		_logger.debug("Created recipe with id: " + baker.getId());
//
//		return Response.created(URI.create("/baker/" + baker.getId())).build();
//	}
//
//	/**
//	 * Attempts to delete an existing recipe. If the specified recipe isn't
//	 * found, a 404 response is returned to the consumer. In other cases, a 204
//	 * response is returned after deleting the recipe.
//	 * 
//	 * @param id
//	 *            the unique id of the recipe to delete.
//	 */
//	@DELETE
//	@Path("{id}")
//	public void deleteBaker(@PathParam("id") int id) {
//		Baker current = _bakerDB.get(id);
//		if (current == null) {
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Remove the Recipe.
//		_bakerDB.remove(id);
//		_logger.info("Deleted baker with ID: " + id);
//	}
//
//	/**
//	 * Helper method to generate an XML representation of a particular recipe.
//	 * 
//	 * @param os
//	 *            the OutputStream used to write out the XML.
//	 * 
//	 * @param recipe
//	 *            the recipe for which to generate an XML representation.
//	 * 
//	 * @throws IOException
//	 *             if an error is encountered in writing the XML to the
//	 *             OutputStream.
//	 */
//	protected void outputBaker(OutputStream os, Recipe recipe)
//			throws IOException {
//		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
//
//		PrintStream writer = new PrintStream(os);
//		writer.println("<baker>");
//		writer.println("	<id>" + recipe.getId() + "</id>");
//		writer.println("	<username>" + recipe.getAuthor() + "</username>");
//		writer.println("   	<recipes>" + recipe.getContent() + "</recipes>");
//		writer.println("	<wishlists>" + recipe.getCreationTimeStamp() + "</wishlists>");
//		writer.println("</baker>");
//	}
//	
//	
//	/**
//	 * Helper method to read an XML representation of a Recipe, and return a
//	 * corresponding Recipe object. This method uses the org.w3c API for parsing
//	 * XML. The details aren't important, and shortly we'll use an automated
//	 * approach rather than having to do this by hand. Currently this is a
//	 * minimal Web service and so we'll parse the XML by hand.
//	 * 
//	 * @param is
//	 *            the InputStream containing an XML representation of the recipe
//	 *            to create.
//	 * 
//	 * @return a new Recipe object.
//	 */
//	protected Baker readBaker(InputStream is) {
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			Document doc = builder.parse(is);
//			Element root = doc.getDocumentElement();
//
//			Baker baker = new Baker();
//			if (root.getAttribute("id") != null
//					&& !root.getAttribute("id").trim().equals(""))
//				recipe.setId(Integer.valueOf(root.getAttribute("id")));
//			NodeList nodes = root.getChildNodes();
//			for (int i = 0; i < nodes.getLength(); i++) {
//				Element element = (Element) nodes.item(i);
//				if (element.getTagName().equals("username")) {
//					baker.setUsername(element.getTextContent());
//				} else if (element.getTagName().equals("last-name")) {
//					baker.setLastname(element.getTextContent());
//				} else if (element.getTagName().equals("first-name")) {
//					baker.setFirstname(element.getTextContent());
//				} else if (element.getTagName().equals("author")) {
//					baker.setLastname(element.getTextContent());
//				} else if (element.getTagName().equals("recipes")) {
//					baker.setRecipes(element.getTextContent());
//				} else if (element.getTagName().equals("wishlists")) {
//					baker.setWishlist(element.getTextContent());
//				}
//
//			}
//			return baker;
//		} catch (Exception e) {
//			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
//		}
//	}
//
//	// -------------------------------------------------------- category http
//	// methods --------------------------------------------------------
//
//	/*
//	 * Get category by id
//	 */
//	@GET
//	@Path("{id}")
//	@Produces("application/xml")
//	public StreamingOutput retrieveCategory(@PathParam("id") int id) {
//		_logger.info("Retrieving category with id: " + id);
//		// Lookup the Recipe within the in-memory data structure.
//		final Category category = _categoryDB.get(id);
//		if (category == null) {
//			// Return a HTTP 404 response if the specified Recipe isn't found.
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Return a StreamingOuput instance that the JAX-RS implementation will
//		// use to set the body of the HTTP response message.
//		return new StreamingOutput() {
//			public void write(OutputStream outputStream) throws IOException,
//					WebApplicationException {
//				outputRecipe(outputStream, category);
//			}
//		};
//	}
//
//	/**
//	 * Creates a new _categoryDB.
//	 * 
//	 * @param is
//	 *            the Inputstream that contains an XML representation of the
//	 *            recipe to be created.
//	 * 
//	 * @return a Response object that includes the HTTP "Location" header, whose
//	 *         value is the URI of the newly created resource. The HTTP response
//	 *         code is 201. The JAX-RS run-time processes the Response object
//	 *         when preparing the HTTP response message.
//	 */
//	@POST
//	@Produces("application/xml")
//	public Response createCategory(InputStream is) {
//		// Read an XML representation of a new Recipe. Note that with JAX-RS,
//		// any non-annotated parameter in a Resource method is assumed to hold
//		// the HTTP request's message body.
//		Category category = readCategory(is);
//
//		// Generate an ID for the new Recipe, and store it in memory.
//		category.setId(_idCounter.incrementAndGet());
//		_categoryDB.put(category.getId(), category);
//
//		_logger.debug("Created category with id: " + category.getId());
//
//		return Response.created(URI.create("/category/" + category.getId()))
//				.build();
//	}
//
//	/**
//	 * Attempts to delete an existing recipe. If the specified recipe isn't
//	 * found, a 404 response is returned to the consumer. In other cases, a 204
//	 * response is returned after deleting the recipe.
//	 * 
//	 * @param id
//	 *            the unique id of the recipe to delete.
//	 */
//	@DELETE
//	@Path("{id}")
//	public void deleteCategory(@PathParam("id") int id) {
//		Category current = _categoryDB.get(id);
//		if (current == null) {
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Remove the Recipe.
//		_categoryDB.remove(id);
//		_logger.info("Deleted category with ID: " + id);
//	}
//
//	/**
//	 * Helper method to read an XML representation of a Recipe, and return a
//	 * corresponding Recipe object. This method uses the org.w3c API for parsing
//	 * XML. The details aren't important, and shortly we'll use an automated
//	 * approach rather than having to do this by hand. Currently this is a
//	 * minimal Web service and so we'll parse the XML by hand.
//	 * 
//	 * @param is
//	 *            the InputStream containing an XML representation of the recipe
//	 *            to create.
//	 * 
//	 * @return a new Recipe object.
//	 */
//	protected Category readCategory(InputStream is) {
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			Document doc = builder.parse(is);
//			Element root = doc.getDocumentElement();
//
//			Category category = new Category();
//			if (root.getAttribute("id") != null
//					&& !root.getAttribute("id").trim().equals(""))
//				recipe.setId(Integer.valueOf(root.getAttribute("id")));
//			NodeList nodes = root.getChildNodes();
//			for (int i = 0; i < nodes.getLength(); i++) {
//				Element element = (Element) nodes.item(i);
//				if (element.getTagName().equals("name")) {
//					category.setName(element.getTextContent());
//				} else if (element.getTagName().equals("recipes")) {
//					category.setRecipes(element.getTextContent());
//				}
//
//			}
//			return category;
//		} catch (Exception e) {
//			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
//		}
//	}
//
//	// -------------------------------------------------------- Review http
//	// methods --------------------------------------------------------
//
//	/*
//	 * Get review by id
//	 */
//	@GET
//	@Path("{id}")
//	@Produces("application/xml")
//	public StreamingOutput retrieveReview(@PathParam("id") int id) {
//		_logger.info("Retrieving category with id: " + id);
//		// Lookup the Recipe within the in-memory data structure.
//		final Review review = _reviewDB.get(id);
//		if (review == null) {
//			// Return a HTTP 404 response if the specified Recipe isn't found.
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Return a StreamingOuput instance that the JAX-RS implementation will
//		// use to set the body of the HTTP response message.
//		return new StreamingOutput() {
//			public void write(OutputStream outputStream) throws IOException,
//					WebApplicationException {
//				outputRecipe(outputStream, review);
//			}
//		};
//	}
//
//	/**
//	 * Creates a new _categoryDB.
//	 * 
//	 * @param is
//	 *            the Inputstream that contains an XML representation of the
//	 *            recipe to be created.
//	 * 
//	 * @return a Response object that includes the HTTP "Location" header, whose
//	 *         value is the URI of the newly created resource. The HTTP response
//	 *         code is 201. The JAX-RS run-time processes the Response object
//	 *         when preparing the HTTP response message.
//	 */
//	@POST
//	@Produces("application/xml")
//	public Response createReview(InputStream is) {
//		// Read an XML representation of a new Recipe. Note that with JAX-RS,
//		// any non-annotated parameter in a Resource method is assumed to hold
//		// the HTTP request's message body.
//		Review review = readReview(is);
//
//		// Generate an ID for the new Recipe, and store it in memory.
//		review.setId(_idCounter.incrementAndGet());
//		_reviewDB.put(review.getId(), review);
//
//		_logger.debug("Created review with id: " + review.getId());
//
//		return Response.created(URI.create("/review/" + review.getId()))
//				.build();
//	}
//
//	/**
//	 * Attempts to delete an existing recipe. If the specified recipe isn't
//	 * found, a 404 response is returned to the consumer. In other cases, a 204
//	 * response is returned after deleting the recipe.
//	 * 
//	 * @param id
//	 *            the unique id of the recipe to delete.
//	 */
//	@DELETE
//	@Path("{id}")
//	public void deleteReview(@PathParam("id") int id) {
//		Review review = _reviewDB.get(id);
//		if (current == null) {
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Remove the Recipe.
//		_reviewDB.remove(id);
//		_logger.info("Deleted review with ID: " + id);
//	}
//
//	/**
//	 * Helper method to read an XML representation of a Recipe, and return a
//	 * corresponding Recipe object. This method uses the org.w3c API for parsing
//	 * XML. The details aren't important, and shortly we'll use an automated
//	 * approach rather than having to do this by hand. Currently this is a
//	 * minimal Web service and so we'll parse the XML by hand.
//	 * 
//	 * @param is
//	 *            the InputStream containing an XML representation of the recipe
//	 *            to create.
//	 * 
//	 * @return a new Recipe object.
//	 */
//	protected Review readReview(InputStream is) {
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			Document doc = builder.parse(is);
//			Element root = doc.getDocumentElement();
//
//			Review review = new Review();
//			if (root.getAttribute("id") != null
//					&& !root.getAttribute("id").trim().equals(""))
//				recipe.setId(Integer.valueOf(root.getAttribute("id")));
//			NodeList nodes = root.getChildNodes();
//			for (int i = 0; i < nodes.getLength(); i++) {
//				Element element = (Element) nodes.item(i);
//				if (element.getTagName().equals("content")) {
//					review.setContent(element.getTextContent());
//				}
//
//			}
//			return review;
//		} catch (Exception e) {
//			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
//		}
//	}
//	
//	// -------------------------------------------------------- Wishlist http
//	// methods --------------------------------------------------------
//
//	/*
//	 * Get wishlist by id
//	 */
//	@GET
//	@Path("{id}")
//	@Produces("application/xml")
//	public StreamingOutput retrieveWishlist(@PathParam("id") int id) {
//		_logger.info("Retrieving category with id: " + id);
//		// Lookup the Recipe within the in-memory data structure.
//		final Wishlist wishlist = _wishlistDB.get(id);
//		if (wishlist == null) {
//			// Return a HTTP 404 response if the specified Recipe isn't found.
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Return a StreamingOuput instance that the JAX-RS implementation will
//		// use to set the body of the HTTP response message.
//		return new StreamingOutput() {
//			public void write(OutputStream outputStream) throws IOException,
//					WebApplicationException {
//				outputRecipe(outputStream, wishlist);
//			}
//		};
//	}
//
//	/**
//	 * Creates a new _categoryDB.
//	 * 
//	 * @param is
//	 *            the Inputstream that contains an XML representation of the
//	 *            recipe to be created.
//	 * 
//	 * @return a Response object that includes the HTTP "Location" header, whose
//	 *         value is the URI of the newly created resource. The HTTP response
//	 *         code is 201. The JAX-RS run-time processes the Response object
//	 *         when preparing the HTTP response message.
//	 */
//	@POST
//	@Produces("application/xml")
//	public Response createWishlist(InputStream is) {
//		// Read an XML representation of a new Recipe. Note that with JAX-RS,
//		// any non-annotated parameter in a Resource method is assumed to hold
//		// the HTTP request's message body.
//		Wishlist wishlist = readReview(is);
//
//		// Generate an ID for the new Recipe, and store it in memory.
//		wishlist.setId(_idCounter.incrementAndGet());
//		_wishlistDB.put(wishlist.getId(), wishlist);
//
//		_logger.debug("Created wishlist with id: " + wishlist.getId());
//
//		return Response.created(URI.create("/wishlist/" + wishlist.getId()))
//				.build();
//	}
//
//	/**
//	 * Attempts to delete an existing recipe. If the specified recipe isn't
//	 * found, a 404 response is returned to the consumer. In other cases, a 204
//	 * response is returned after deleting the recipe.
//	 * 
//	 * @param id
//	 *            the unique id of the recipe to delete.
//	 */
//	@DELETE
//	@Path("{id}")
//	public void deleteWishlist(@PathParam("id") int id) {
//		Wishlist wishlist = _wishlistDB.get(id);
//		if (current == null) {
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		}
//
//		// Remove the Recipe.
//		_wishlistDB.remove(id);
//		_logger.info("Deleted review with ID: " + id);
//	}
//
//	/**
//	 * Helper method to read an XML representation of a Recipe, and return a
//	 * corresponding Recipe object. This method uses the org.w3c API for parsing
//	 * XML. The details aren't important, and shortly we'll use an automated
//	 * approach rather than having to do this by hand. Currently this is a
//	 * minimal Web service and so we'll parse the XML by hand.
//	 * 
//	 * @param is
//	 *            the InputStream containing an XML representation of the recipe
//	 *            to create.
//	 * 
//	 * @return a new Recipe object.
//	 */
//	protected Wishlist readWishlist(InputStream is) {
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			Document doc = builder.parse(is);
//			Element root = doc.getDocumentElement();
//
//			Wishlist wishlist = new Wishlist();
//			if (root.getAttribute("id") != null
//					&& !root.getAttribute("id").trim().equals(""))
//				recipe.setId(Integer.valueOf(root.getAttribute("id")));
//			NodeList nodes = root.getChildNodes();
//			for (int i = 0; i < nodes.getLength(); i++) {
//				Element element = (Element) nodes.item(i);
//				if (element.getTagName().equals("owner")) {
//					review.setOwner(element.getTextContent());
//				} else if (element.getTagName().equals("name")) {
//					review.setName(element.getTextContent());
//				} else if (element.getTagName().equals("description")) {
//					review.setDescription(element.getTextContent());
//				} else if (element.getTagName().equals("wishlist-recipes")) {
//					review.setWishlistRecipes(element.getTextContent());
//				}
//				
//			}
//			return wishlist;
//		} catch (Exception e) {
//			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
//		}
//	}

}
