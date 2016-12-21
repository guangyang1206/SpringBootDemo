package com.epam.cdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class DemoApplication {

    @Bean
    CommandLineRunner runner() {
        return args -> {
            Arrays.asList("600500,000001,000033,000024".split(","))
                    .forEach(n -> stockRepository.save(new Stock(n)));
            stockRepository.findAll().forEach(System.out::println);
            stockRepository.findByCode("000001").forEach(System.out::println);
        };
    }

    @Bean
    HealthIndicator healthIndicator() {
        return () -> Health.status("I'm here!").build();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    StockRepository stockRepository;
}

@RestController
class StockRestController {
	@RequestMapping("/findAllStocks")
	Collection<Stock> stocks() {
		return stockRepository.findAll();
	}

	@Autowired
	StockRepository stockRepository;
}

@Controller
class stockMvcController {
	@RequestMapping("/stockHome")
	String stocks(Model model) {
		model.addAttribute("stocks", stockRepository.findAll());
		return "stocks"; // src/main/resources/templetes/ + $X + .html
	}

	@Autowired
	private StockRepository stockRepository;
}

@RepositoryRestResource
//@Repository
interface StockRepository extends JpaRepository<Stock, Long> {
    Collection<Stock> findByCode(@Param("code") String code);
}

@Component
class StockResourceProcessor implements ResourceProcessor<Resource<Stock>> {
	@Override
	public Resource<Stock> process(Resource<Stock> resource) {
		resource.add(new Link("http://cdn.imaibo.net/images/" + resource.getContent().getId() + ".jpg", "profile-photo"  ));
		return resource;
	}
}

@Entity
class Stock {
    @Id
    @GeneratedValue
    private Long id;
    private String code;

    public Stock() {
    }

    public Stock(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", code='" + code + '\'' +
                '}';
    }
}