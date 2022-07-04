package cn.com.hellowood.mapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.com.hellowood.mapper.mapper")
public class MapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(MapperApplication.class, args);
	}
}
