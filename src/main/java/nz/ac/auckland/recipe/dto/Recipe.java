package nz.ac.auckland.recipe.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.ac.auckland.recipe.domain.*;
import nz.ac.auckland.recipe.domain.Review;
import nz.ac.auckland.recipe.jaxb.BakerAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/* Class to represent a Recipe. 
 * 
 * An instance of this class represents a DTO Recipe. A DTO Recipe includes
 * a subset of the Recipe data - so that it is simplified, and DTO Recipe objects are 
 * exchanged between clients and the Recipe Web service.
 * 
 * A DTO Recipe is described by:
 * - name of the recipe
 * - content of the recipe
 * - the author of the recipe
 * - the category of the recipe 
 * - the most recent review of the recipe 
 * 
 * A Recipe is uniquely identified by an id value of type long.
 * 
 */
@XmlRootElement(name="recipe")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recipe {
	
	@XmlAttribute(name="id")
	private long _id;
	
	@XmlElement(name="name")
	private String _name;
	
	@XmlElement(name="content")
	private String _content;
	
	@XmlAttribute(name="author")
//	@XmlJavaTypeAdapter(value=BakerAdapter.class)
	private Baker _author; 
	
	@XmlElement(name="categoryName")
//	@XmlJavaTypeAdapter(value=CategoryAdapter.class)
	private Category _categoryName; 
	
//	@XmlJavaTypeAdapter(value=ReviewAdapter.class)
	@XmlElement(name="most-recent-review")
	private Review _mostRecentReview; 
	
	protected Recipe() {
		
	}
	
	/**
	 * Constructs a DTO Parolee instance. This method is intended to be called
	 * by Web service clients when creating new Parolees. The id field is not 
	 * required because it is generated by the Web service. Similarly, the 
	 * last-known-position field is not required. Of the constructor's 
	 * parameters, all fields must be non-null with the exception of curfew, 
	 * which is optional (not all Parolees are subject to a curfew).
     *
	 */
	public Recipe(String name,
			String content,
			nz.ac.auckland.recipe.domain.Baker author,
			Category category) throws IllegalArgumentException {
		this(0,name,content, author,category,null);
	}
	
	/**
	 * Constructs a DTO Parolee instance. This method should NOT be called by 
	 * Web Service clients. It is intended to be used by the Web Service 
	 * implementation when creating a DTO Parolee from a domain-model Parolee 
	 * object.
	 */
	public Recipe(long id,
			String name,
			String content,
			Baker baker,
			Category category,
			Review review
			) {
		
		_id = id;
		_name = name;
		_content = content;
		_author = baker;
		_categoryName = category;
		_mostRecentReview = review; 
	}
	

	public long getId() {
		return _id;
	}
	
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
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
		_categoryName = category; 
	}

	public Category getCategory(){
		return _categoryName; 
	}
	
	public void setReviews(Review review){
		_mostRecentReview = review; 
	}
	
	public Review getReviews(){
		return _mostRecentReview; 
	}

	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
				
		buffer.append("Recipe: { [");
		buffer.append(_id);
		buffer.append("], ");
		if(_name != null) {
			buffer.append(_name);
		}
		buffer.append(", ");
		if(_content != null) {
			buffer.append(_content);
		}
		buffer.append("; ");
		if(_author != null) {
			buffer.append(_author.getId());
		}
		buffer.append("; ");
		
		if(_categoryName != null) {
			buffer.append((_categoryName.getId()));
		}
		buffer.append("; ");
		if(_mostRecentReview != null) {
			buffer.append(_mostRecentReview);
		}
		buffer.append("; ");
				
		
		buffer.append(" }");
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Recipe))
            return false;
        if (obj == this)
            return true;

        Recipe rhs = (Recipe) obj;
        return new EqualsBuilder().
            append(_id, rhs._id).
            append(_name, rhs._name).
            append(_content, rhs._content).
            append(_author, rhs._author).
            append(_categoryName, rhs._categoryName).
            append(_mostRecentReview, rhs._mostRecentReview).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            append(_name).
	            append(_content).
	            append(_author).
	            append(_categoryName).
	            append(_mostRecentReview).
	            toHashCode();
	}
}


