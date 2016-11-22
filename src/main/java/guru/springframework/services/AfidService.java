package guru.springframework.services;

import java.util.Collection;
import java.util.List;

import guru.springframework.concurrent.FaceMatchResultDTO;
import guru.springframework.domain.Afid;

public interface AfidService {
    public void deleteAll(Collection<Afid> collection);
    public List<Afid> getAll();
    
    public FaceMatchResultDTO search(byte[] query);
    
    public List<byte[]> getAfidList();
}
