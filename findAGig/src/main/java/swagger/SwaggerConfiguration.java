package swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
	   @Bean
	    public Docket findaGigSwagger() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.lisasmith.findAGig")) 
	                .paths(regex("/.*"))
	                .build();
	        
//	                .apiInfo(metaInfo());
//	    }
//	   private ApiInfo metaInfo() {
//			ApiInfo apiInfo = new ApiInfo(
//	                "FindAGig Service", 
//	                "API documentation for FindAGig Service entities. " + 
//	                        "Entities include: \nAddress, User, Gig, " +
//	                        "GigStatus, Instrument, and Credentials",
//	                "V1.0",
//	                "Terms of Service",
//	                "Lisa Maatta Smith", 
//	                "Apache License Version 2.0",
//	                "https://www.apache.org/licenses/LICENSE-2.0"
//	        );
//	        return apiInfo;
    }		
	
	
}

