package nz.ac.auckland.recipe.domain;

import nz.ac.auckland.recipe.jaxb.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "Recipe" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The Recipe object keeps track of a recipe entry that a baker has written. Recipes have an id, a name and content as well as their author, 
 * category and a set of their reviews. 
 *
 */

//@SuppressWarnings("serial")
@Entity
//@XmlRootElement(name="recipe")
@Access(AccessType.FIELD)
public class Recipe implements Serializable {
	@Id
//	@XmlAttribute(name="id")
	private Long _id; 
	
//	@XmlAttribute(name="name")
	private String _name; 
	
//	@XmlAttribute(name="content")
	private String _content; 
	
	@ManyToOne 
//	@XmlAttribute(name="author")
//	@XmlJavaTypeAdapter(value=BakerAdapter.class)
	private Baker _author; 
	
	@ManyToOne(cascade=CascadeType.ALL)
//	@XmlAttribute(name="category")
//	@XmlJavaTypeAdapter(value=CategoryAdapter.class)
	@JoinTable(name="RECIPE_CATEGORY", joinColumns =
		@JoinColumn( name="RECIPE_ID" ), inverseJoinColumns = 
			@JoinColumn( nullable=false ))
	private Category _category; 
	
	@ElementCollection
//	@XmlJavaTypeAdapter(value=ReviewAdapter.class)
//	@XmlAttribute(name="reviews")
	private List<Review> _reviews; 

	// Does this need to be protected for JPA? 
	public Recipe(){}
	 	
	public Recipe(String recipeAsString) {
		_name = recipeAsString; 
	}

	public Recipe(Long id, String name, String content, Baker author, Category category) {
		_id = id; 
		_name = name;
		_content = content; 
		_author = author; 
		_category = category; 
		_reviews = new ArrayList<Review>(); 
		
	}

	public Long getId(){
		return _id; 
	}
	
	public void setId(Long id){
		_id = id; 
	}
	
	public void setId(int id) {
		_id = Long.valueOf(id); 		
	}
	
	public String getName(){
		return _name;
	}
	
	public void setName(String name){
		_name = name; 
	}
	
	public String getContent(){
		return _content;
	}
	
	public void setContent(String content){
		_content = content; 
	}
	
	public void setAuthor(Baker author){
		_author = author; 
	}
	
	public Baker getAuthor(){
		return _author; 
	}
	
	public void setCategory(Category category){
		_category = category; 
	}

	public Category getCategory(){
		return _category; 
	}
	
	public void setReviews(List<Review> reviews){
		_reviews = reviews; 
	}
	
	public List<Review> getReviews(){
		return _reviews; 
	}
	
	public void addReview(Review review){
		_reviews.add(review); 
		
		Collections.sort(_reviews, Collections.reverseOrder()); 
	}
	
	public Review getMostRecentReview(){
		return _reviews.get(0); 
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
				
		buffer.append("Recipe: { [");
		buffer.append(_id);
		buffer.append("], ");
		if(_name != null) {
			buffer.append(_name);
			buffer.append(", ");
		}
		
		if(_content != null) {
			buffer.append(_content);
		}
		buffer.append("; ");
		if(_author != null) {
			buffer.append(_author.getId());
			buffer.append("; ");
		}
		
		
		if(_category != null) {
			buffer.append((_category.getId()));
			buffer.append("; ");
		}
		
		if(_reviews.isEmpty()) {
			buffer.append("none");
		} else {
			for(Review review : _reviews) {
				if(review.getContent() != null) {
					buffer.append(review.getContent());
					buffer.append(", ");
				}
				buffer.append(";");
			}
			buffer.deleteCharAt(buffer.length()-1);
		}
		
		buffer.append(" }");
		
		return buffer.toString();
	}

	// This method is for adding a review to the list of reviews 
	public void updateReview(Review review) {
		if (!(_reviews.contains(review))){
			_reviews.add(review); 
		}
		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            toHashCode();
	}

	
	
	
}

