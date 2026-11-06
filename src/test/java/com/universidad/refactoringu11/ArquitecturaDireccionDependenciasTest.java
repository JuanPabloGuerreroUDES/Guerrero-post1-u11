package com.universidad.refactoringu11;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Complementa a ArquitecturaPaquetesTest: ademas de "sin ciclos", fija la DIRECCION permitida
 * (reportes -> inventario). El dominio nunca depende de la infraestructura.
 */
class ArquitecturaDireccionDependenciasTest {

    @Test
    void inventarioNoDebeDependerDeReportes() {
        JavaClasses clases = new ClassFileImporter()
                .importPackages("com.universidad.refactoringu11");

        noClasses().that().resideInAPackage("..inventario..")
                .should().dependOnClassesThat().resideInAPackage("..reportes..")
                .check(clases);
    }
}
