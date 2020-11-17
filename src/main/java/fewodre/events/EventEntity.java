package fewodre.events;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	public String getId() {
		return id;
	}
}
