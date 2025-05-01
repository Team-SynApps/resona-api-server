package synapps.resona.api.mongo.test;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

  List<Customer> findByFirstName(String firstName);
}
