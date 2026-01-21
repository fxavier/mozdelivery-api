package com.xavier.mozdeliveryapi;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Test to verify Spring Modulith module structure and generate documentation.
 */
class ModulithTest {
    
    ApplicationModules modules = ApplicationModules.of(MozdeliveryApiApplication.class);
    
    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
    
    @Test
    void createModuleDocumentation() {
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml();
    }
    
    @Test
    void printModules() {
        modules.forEach(System.out::println);
    }
}