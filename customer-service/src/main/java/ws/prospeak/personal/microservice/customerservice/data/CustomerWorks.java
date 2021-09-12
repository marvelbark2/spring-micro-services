package ws.prospeak.personal.microservice.customerservice.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor @Slf4j
public class CustomerWorks implements InitializingBean {
    private  final CustomerRepository customerRepository;
    @Override
    public void afterPropertiesSet() throws Exception {
        customerRepository.save(new Customer(null, "Youness", "youness@f.fr"));
        customerRepository.save(new Customer(null, "Loead", "loead@f.fr"));
        customerRepository.save(new Customer(null, "SAED", "saed@f.fr"));
        customerRepository.save(new Customer(null, "Coboean", "coboean@f.fr"));
        customerRepository.findAll().forEach(x -> log.info("Customer example : {}", x));
    }
}
