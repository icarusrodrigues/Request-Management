package request.management.project.config;

import br.com.caelum.stella.validation.CPFValidator;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CPFValidator cpfValidator() {
        return new CPFValidator();
    }
}
