package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {
	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean // 지연로딩을 무시할 수 있도록 설정하는 모듈
	Hibernate5JakartaModule hibernate5JakartaModule(){
		return new Hibernate5JakartaModule();
	}

	@Bean
	Hibernate5JakartaModule hibernate5JakartaModule2(){
		Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
		//hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING,true);
		return hibernate5JakartaModule;
	}
}
