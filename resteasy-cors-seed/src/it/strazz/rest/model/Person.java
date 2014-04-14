package it.strazz.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class Person implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static List<Person> people = new ArrayList<Person>();
	
	static{
		people.add(new Person(0, "Solid Snake"));
		people.add(new Person(1, "Vulcan Raven"));
		people.add(new Person(2, "Meryl Silverburgh"));
		people.add(new Person(3, "Hal Emmerich"));
		people.add(new Person(4, "Frank Jaeger"));
	}
	
	private Integer id;
	private String name;

	public Person(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public Person() {}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public static List<Person> getAll(){
		return people;
		
	}
	
	public static Person get(final Integer id){
		return (Person) CollectionUtils.find(people, new Predicate() {
			public boolean evaluate(Object p) {
				return ((Person) p).getId().equals(id);
			}
		});
	}
	
	public static Person store(Person p){
		if(p.getId() == null){
			
			Person maxIdPerson = Collections.max(people, new Comparator<Person>() {
				public int compare(Person o1, Person o2) {
					return Integer.compare(o1.getId(), o2.getId());
				}
			});
			
			p.setId(maxIdPerson.getId()+1);
			
			people.add(p);
		}else{
			people.set(p.getId(), p);
		}
		
		return p;
	}
	
	public static void delete(Person p){
		people.remove(p);
	}
}
