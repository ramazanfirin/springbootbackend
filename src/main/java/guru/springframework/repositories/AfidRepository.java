package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import guru.springframework.domain.Afid;

public interface AfidRepository extends CrudRepository<Afid, Integer>{

	 @Query("SELECT o.afid FROM Afid o ")	
	 public List<byte[]> getAfidList();
	}
