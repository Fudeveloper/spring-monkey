package xyz.tmlh.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


@ServletComponentScan
@EnableSwagger2
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }


}
