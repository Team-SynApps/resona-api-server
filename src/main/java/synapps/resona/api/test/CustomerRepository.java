package synapps.resona.api.test;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MongoDBRepository;

@MongoDBRepository
public interface CustomerRepository extends MongoRepository<Customer, String> {

  List<Customer> findByFirstName(String firstName);
}
