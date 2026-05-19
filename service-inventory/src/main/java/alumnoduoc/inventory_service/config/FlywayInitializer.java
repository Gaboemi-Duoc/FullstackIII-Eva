package alumnoduoc.inventory_service.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;

@Configuration
public class FlywayInitializer {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void migrateDatabase() {
        System.out.println("=== Iniciando Flyway manualmente ===");
        
        String url = environment.getProperty("spring.datasource.url");
        String user = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        
        System.out.println("URL: " + url);
        System.out.println("User: " + user);
        
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(false)
            .table("flyway_schema_history")
            .load();
        
        // Verificar estado actual
        var info = flyway.info();
        System.out.println("Current version: " + info.current());
        System.out.println("Pending migrations: " + info.pending().length);
        
        // Ejecutar migraciones
        var result = flyway.migrate();
        System.out.println("Migraciones aplicadas: " + result.migrationsExecuted);
        System.out.println("=== Flyway completado ===");
    }
}