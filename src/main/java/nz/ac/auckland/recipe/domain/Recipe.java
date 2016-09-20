package nz.ac.auckland.recipe.domain;

import nz.ac.auckland.recipe.jaxb.*;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@SuppressWarnings("serial")
@Entity
@XmlRootElement(name="recipe")
@Access(AccessType.FIELD)
public class Recipe implements Serializable {
	@Id
	@XmlAttribute(name="id")
	private Long _id; 
	
	@XmlAttribute(name="name")
	private String _name; 
	
	@XmlAttribute(name="content")
	private String _content; 
	
	@ManyToOne 
	@XmlAttribute(name="author")
	@XmlJavaTypeAdapter(value=BakerAdapter.class)
	private Baker _author; 
	
	@ManyToOne
	@XmlAttribute(name="category")
	@XmlJavaTypeAdapter(value=CategoryAdapter.class)
	@JoinTable(name="RECIPE CATEGORY", joinColumns =
		@JoinColumn( name="RECIPE_ID" ), inverseJoinColumns = 
			@JoinColumn( nullable=false ))
	private Category _category; 
	
	@ElementCollection
	@XmlJavaTypeAdapter(value=ReviewAdapter.class)
	@XmlAttribute(name="reviews")
	private Set<Review> _reviews; 

	// Does this need to be protected for JPA? 
	public Recipe(){}
	 	
	public Recipe(String recipeAsString) {
		_name = recipeAsString; 
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
	
	public void setReviews(Set<Review> reviews){
		_reviews = reviews; 
	}
	
	public Set<Review> getReviews(){
		return _reviews; 
	}

	@Override
	public String toString(){
		return _id + "-" + _name + "-" + _content + "-" + _category.toString() ;
	}
	
	
}

