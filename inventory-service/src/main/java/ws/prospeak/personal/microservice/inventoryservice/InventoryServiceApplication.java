package ws.prospeak.personal.microservice.inventoryservice;

import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data @ToString
class Product {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Double price;
}

@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product, Long> {}

@Component
@RequiredArgsConstructor
class ProductMooc implements InitializingBean {
	private final ProductRepository repository;

	@Override
	public void afterPropertiesSet() {
		repository.save(new Product(null, "Ordi", 12.34));
		repository.save(new Product(null, "Phone", 121.34));
		repository.save(new Product(null, "Iron", 02.34));
		repository.save(new Product(null, "Micro", 32.14));
	}
}

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
