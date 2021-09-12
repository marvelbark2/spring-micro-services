package ws.prospeak.personal.microservice.billingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data @ToString @NoArgsConstructor @AllArgsConstructor
class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime billingDate;
    private Long customerId;
    @OneToMany
    private Collection<ProductItem> items;
}
@RepositoryRestResource interface BillRepository extends JpaRepository<Bill, Long> { }
@FeignClient(name = "CUSTOMER-SERVICE") interface CustomerService {
    @GetMapping("/customers/{id}")
    CollectionModel<Map<String, Object>> findCustomerById(@PathVariable Long id);
}
@Data
class BillWithCustomer {
    private Long id;
    private LocalDateTime billingDate;
    private Object customer;
    private Collection<ProductItem> items;

    public BillWithCustomer(final Bill bill) {
        this.id = bill.getId();
        this.billingDate = bill.getBillingDate();
        this.items = bill.getItems();
    }
}

@Mapper
interface IBillWithCustomerWithMapper {
    BillWithCustomer getBillWithCustomerById(Long id);
    Collection<BillWithCustomer> findAllBillWithCustomer();
}


@RequiredArgsConstructor
@Component
class BillWithCustomerWithMapper implements IBillWithCustomerWithMapper {
    private final BillRepository billRepository;
    private final CustomerService customerService;

    @Override
    public BillWithCustomer getBillWithCustomerById(Long id) {
        var bill = billRepository.getById(id);
        BillWithCustomer billWithCustomer = new BillWithCustomer(bill);
        Object customer = customerService.findCustomerById(id).getContent();
        billWithCustomer.setCustomer(customer);
        return billWithCustomer;
    }

    @Override
    public Collection<BillWithCustomer> findAllBillWithCustomer() {
        return billRepository.findAll().stream().map(x -> this.getBillWithCustomerById(x.getId())).collect(Collectors.toSet());
    }
}

@Entity
@Data @ToString @NoArgsConstructor @AllArgsConstructor
class ProductItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Double quantity;
    private Double totalPrice;
}
@RepositoryRestResource interface ProductItemRepository extends JpaRepository<ProductItem, Long> {}
@FeignClient(name = "INVENTORY-SERVICE")
interface ProductService {
    @GetMapping("/products/{id}")
    CollectionModel<Object> findProductById(@PathVariable Long id);
}


@RestController
@RequiredArgsConstructor
class BillingController {
    private final IBillWithCustomerWithMapper billWithCustomerWithMapper;

    @GetMapping(path = "/bills/customers")
    public Collection<BillWithCustomer> getAllBillingWithCustomer() {
        return billWithCustomerWithMapper.findAllBillWithCustomer();
    }

    @GetMapping(path = "/bills/{id}/customers")
    public BillWithCustomer getBillWithCustomer(@PathVariable(name = "id") Long id) {
        return billWithCustomerWithMapper.getBillWithCustomerById(id);
    }
}


@SpringBootApplication @EnableFeignClients
public class BillingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(final CustomerService customerService,
                            final BillRepository billRepository,
                            final ProductItemRepository pIRepository,
                            final ProductService productService) {
        return args -> {
            ProductItem item11 = pIRepository.save(new ProductItem(null, 1L, 5.0,900.0 ));
            ProductItem item12 = pIRepository.save(new ProductItem(null, 1L, 7.0,1200.0 ));
            ProductItem item13 = pIRepository.save(new ProductItem(null, 1L, 15.0,9000.0 ));

            ProductItem item21 = pIRepository.save(new ProductItem(null, 2L, 5.0,900.0 ));
            ProductItem item22 = pIRepository.save(new ProductItem(null, 2L, 7.0,1900.0 ));
            ProductItem item23 = pIRepository.save(new ProductItem(null, 2L, 12.0,2400.0 ));

            ProductItem item31 = pIRepository.save(new ProductItem(null, 2L, 5.0,900.0 ));
            ProductItem item32 = pIRepository.save(new ProductItem(null, 2L, 7.0,1900.0 ));
            ProductItem item33 = pIRepository.save(new ProductItem(null, 2L, 12.0,2400.0 ));

            ProductItem item41 = pIRepository.save(new ProductItem(null, 2L, 5.0,900.0 ));
            ProductItem item42 = pIRepository.save(new ProductItem(null, 2L, 7.0,1900.0 ));
            ProductItem item43 = pIRepository.save(new ProductItem(null, 2L, 12.0,2400.0 ));


            billRepository.save(new Bill(null, LocalDateTime.now(), 1L, Set.of(item11, item12)));
            billRepository.save(new Bill(null, LocalDateTime.now(), 2L, Set.of(item21,item23)));
            billRepository.save(new Bill(null, LocalDateTime.now(), 3L, Set.of(item13, item22)));
            billRepository.save(new Bill(null, LocalDateTime.now(), 4L, Set.of(item31, item32, item33, item41, item42, item43)));

            System.out.println("The customer 1 is : " + customerService.findCustomerById(1L).getContent());
            System.out.println("The product 1 is : " + productService.findProductById(1L));

        };
    }
}
