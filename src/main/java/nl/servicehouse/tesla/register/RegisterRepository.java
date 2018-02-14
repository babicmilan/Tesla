package nl.servicehouse.tesla.register;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRepository extends PagingAndSortingRepository<Register, Long> {

}
