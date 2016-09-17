package nz.ac.auckland.recipe.domain;

import org.joda.time.DateTime;

import java.util.Set;

import javax.persistence.*;


@Entity
//@Access(AccessType.FIELD)
public class Recipe {
	@Id
	private Long _id; 
	private String _content; 
	private DateTime _creationTimeStamp; 
	
	@ManyToOne 
	private Baker _author; 
	
	@ManyToOne
	@JoinTable(name="RECIPE CATEGORY", joinColumns =
		@JoinColumn( name="RECIPE ID" ), inverseJoinColumns = 
			@JoinColumn( nullable=false ))
	private Category _category; 
	
	@ElementCollection
	private Set<Review> _reviews; 

	public Recipe(){}
	 	
	public Long getId(){
		return _id; 
	}
	
	public void setId(Long id){
		_id = id; 
	}
	
	public void setId(int id) {
		_id = Long.valueOf(id); 		
	}
	
	public String getContent(){
		return _content;
	}
	
	public void setContent(String content){
		_content = content; 
	}
	
	public DateTime getCreationTimeStamp(){
		return _creationTimeStamp; 
	}
	
	public void setCreationTimeStamp(DateTime timeStamp){
		_creationTimeStamp = timeStamp; 
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

	
	
}

