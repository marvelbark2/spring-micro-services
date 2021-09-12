package ws.prospeak.personal.microservice.customerservice.data;

import org.springframework.data.rest.core.config.Projection;
import org.springframework.stereotype.Component;

@Component
@Projection(name = "fullCustomer", types = Customer.class)
public interface CustomerProjection extends Projection {
    Long getId();
    String getName();
    String getEmail();
}