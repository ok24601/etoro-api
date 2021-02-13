package ok.work.etoroapi

import com.fasterxml.classmate.TypeResolver
import ok.work.etoroapi.client.EtoroHttpClient
import ok.work.etoroapi.model.ofString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
class EtoroApiApplication


fun main(args: Array<String>) {
    runApplication<EtoroApiApplication>(*args)
}


@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Autowired
    lateinit var typeResolver: TypeResolver

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ok.work.etoroapi.controller"))
                .paths(PathSelectors.any())
                .build()
    }


    fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("Etoro API server")
                .description("Etoro webbased API server")
                .version("0.0.1")
                .build()
    }
}
