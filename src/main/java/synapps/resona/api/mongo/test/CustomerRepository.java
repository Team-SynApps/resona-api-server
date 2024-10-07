package synapps.resona.api.mongo.test;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    public List<Customer> findByFirstName(String firstName);
}
