package com.maan.eway;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;

class SpringBeanCircularDependencyTest {

    private static final String BASE_PACKAGE = "com.maan.eway";

    @Test
    void shouldNotHaveSpringBeanCircularDependencies() {

        JavaClasses classes = new ClassFileImporter()
                .importPackages(BASE_PACKAGE);

        SpringBeanGraph graph = new SpringBeanGraph(classes);

        graph.build();

        // Normal circular dependencies
        List<List<String>> cycles = graph.findCycles();

        // Direct/self dependencies
        List<List<String>> selfDependencies =
                graph.findSelfDependencies();

        System.out.println();
        System.out.println("============================================================");
        System.out.println("SPRING BEAN CIRCULAR DEPENDENCY SCAN");
        System.out.println("============================================================");


        // ============================================================
        // SELF DEPENDENCIES
        // ============================================================

        if (!selfDependencies.isEmpty()) {

            System.out.println();
            System.out.println("SELF DEPENDENCIES");
            System.out.println("------------------------------------------------------------");

            int number = 1;

            for (List<String> cycle : selfDependencies) {

                System.out.println();
                System.out.println("Self Dependency #" + number);
                System.out.println("------------------------------------------------------------");

                for (int i = 0; i < cycle.size(); i++) {

                    System.out.println(cycle.get(i));

                    if (i < cycle.size() - 1) {
                        System.out.println("    ↓");
                    }
                }

                number++;
            }
        }


        // ============================================================
        // NORMAL CIRCULAR DEPENDENCIES
        // ============================================================

        if (!cycles.isEmpty()) {

            System.out.println();
            System.out.println("CIRCULAR DEPENDENCIES");
            System.out.println("------------------------------------------------------------");

            int cycleNumber = 1;

            for (List<String> cycle : cycles) {

                System.out.println();
                System.out.println("Cycle #" + cycleNumber);
                System.out.println("------------------------------------------------------------");

                for (int i = 0; i < cycle.size(); i++) {

                    String current = cycle.get(i);

                    System.out.println(current);

                    if (i < cycle.size() - 1) {
                        System.out.println("    ↓");
                    }
                }

                cycleNumber++;
            }
        }


        // ============================================================
        // SUMMARY
        // ============================================================

        System.out.println();
        System.out.println("============================================================");
        System.out.println(
                "SELF DEPENDENCIES FOUND : "
                        + selfDependencies.size()
        );
        System.out.println(
                "CIRCULAR CYCLES FOUND   : "
                        + cycles.size()
        );
        System.out.println("============================================================");
        System.out.println();


        // ============================================================
        // SUCCESS
        // ============================================================

        if (cycles.isEmpty()
                && selfDependencies.isEmpty()) {

            System.out.println("SUCCESS");
            System.out.println(
                    "No Spring bean circular dependencies detected."
            );
            System.out.println();

            return;
        }


        // ============================================================
        // FAIL TEST
        // ============================================================

        fail(
                "Spring bean circular dependencies detected. "
                        + "Self dependencies = "
                        + selfDependencies.size()
                        + ", Circular cycles = "
                        + cycles.size()
                        + ". See console output."
        );
    }


    // ============================================================
    // SPRING BEAN GRAPH
    // ============================================================

    private static class SpringBeanGraph {

        private final JavaClasses classes;


        /*
         * ============================================================
         * Bean implementation classes
         *
         * Example:
         *
         * UserDetailsService
         *       ↓
         * AuthendicationServiceImpl
         * ============================================================
         */

        private final Set<JavaClass> beanClasses =
                new LinkedHashSet<>();


        /*
         * ============================================================
         * Map interface/superclass -> implementations
         *
         * Example:
         *
         * UserDetailsService
         *      ->
         * AuthendicationServiceImpl
         * ============================================================
         */

        private final Map<JavaClass, Set<JavaClass>> implementations =
                new LinkedHashMap<>();


        /*
         * ============================================================
         * Spring @Bean return type -> configuration class
         *
         * Example:
         *
         * BCryptPasswordEncoder
         *      ->
         * WebSecurityConfig
         * ============================================================
         */

        private final Map<JavaClass, Set<JavaClass>> beanProviders =
                new LinkedHashMap<>();


        /*
         * ============================================================
         * Actual dependency graph
         *
         * A -> B means:
         *
         * Spring bean A depends on Spring bean B.
         * ============================================================
         */

        private final Map<JavaClass, Set<JavaClass>> graph =
                new LinkedHashMap<>();


        SpringBeanGraph(JavaClasses classes) {
            this.classes = classes;
        }


        // ============================================================
        // BUILD GRAPH
        // ============================================================

        void build() {

            findSpringBeans();

            buildImplementations();

            findBeanProviders();

            initialiseGraph();

            findFieldDependencies();

            findConstructorDependencies();

            findBeanMethodDependencies();
        }


        // ============================================================
        // STEP 1
        // Find Spring beans
        // ============================================================

        private void findSpringBeans() {

            for (JavaClass clazz : classes) {

                if (isSpringBean(clazz)) {

                    beanClasses.add(clazz);
                }
            }
        }


        private boolean isSpringBean(JavaClass clazz) {

            return clazz.isAnnotatedWith(Service.class)
                    || clazz.isAnnotatedWith(Component.class)
                    || clazz.isAnnotatedWith(Repository.class)
                    || clazz.isAnnotatedWith(Controller.class)
                    || clazz.isAnnotatedWith(RestController.class)
                    || clazz.isAnnotatedWith(Configuration.class);
        }


        // ============================================================
        // STEP 2
        // Find interface implementations
        // ============================================================

        private void buildImplementations() {

            for (JavaClass bean : beanClasses) {


                // ----------------------------------------------------
                // Interfaces
                // ----------------------------------------------------

                for (JavaClass interfaceClass
                        : bean.getAllRawInterfaces()) {

                    implementations
                            .computeIfAbsent(
                                    interfaceClass,
                                    key -> new LinkedHashSet<>()
                            )
                            .add(bean);
                }


                // ----------------------------------------------------
                // Superclass
                // ----------------------------------------------------

                Optional<JavaClass> superClass =
                        bean.getRawSuperclass();

                if (superClass.isPresent()
                        && !superClass.get()
                                .getName()
                                .equals("java.lang.Object")) {

                    implementations
                            .computeIfAbsent(
                                    superClass.get(),
                                    key -> new LinkedHashSet<>()
                            )
                            .add(bean);
                }
            }
        }


        // ============================================================
        // STEP 3
        // Find @Bean providers
        //
        // Example:
        //
        // @Configuration
        // class WebSecurityConfig {
        //
        //     @Bean
        //     BCryptPasswordEncoder encoder() {
        //         return new BCryptPasswordEncoder();
        //     }
        //
        // }
        //
        // Result:
        //
        // BCryptPasswordEncoder
        //      ->
        // WebSecurityConfig
        // ============================================================

        private void findBeanProviders() {

            for (JavaClass clazz : beanClasses) {

                if (!clazz.isAnnotatedWith(Configuration.class)) {
                    continue;
                }


                for (JavaMethod method : clazz.getMethods()) {

                    if (!method.isAnnotatedWith(Bean.class)) {
                        continue;
                    }


                    JavaClass returnType =
                            method.getRawReturnType();

                    beanProviders
                            .computeIfAbsent(
                                    returnType,
                                    key -> new LinkedHashSet<>()
                            )
                            .add(clazz);
                }
            }
        }


        // ============================================================
        // STEP 4
        // Initialize graph
        // ============================================================

        private void initialiseGraph() {

            for (JavaClass bean : beanClasses) {

                graph.putIfAbsent(
                        bean,
                        new LinkedHashSet<>()
                );
            }
        }


        // ============================================================
        // STEP 5
        // Field injection
        //
        // @Autowired
        // private UserDetailsService userDetailsService;
        // ============================================================

        private void findFieldDependencies() {

            for (JavaClass bean : beanClasses) {

                for (JavaField field : bean.getFields()) {

                    if (!field.isAnnotatedWith(Autowired.class)) {
                        continue;
                    }


                    JavaClass dependencyType =
                            field.getRawType();


                    addDependency(
                            bean,
                            dependencyType
                    );
                }
            }
        }


        // ============================================================
        // STEP 6
        // Constructor injection
        //
        // public MyService(
        //       PaymentService paymentService
        // ) {}
        //
        // Also handles @Autowired constructors.
        // ============================================================

        private void findConstructorDependencies() {

            for (JavaClass bean : beanClasses) {

                for (JavaConstructor constructor
                        : bean.getConstructors()) {

                    List<JavaClass> parameterTypes =
                            constructor.getRawParameterTypes();


                    if (parameterTypes.isEmpty()) {
                        continue;
                    }


                    boolean explicitlyAutowired =
                            constructor.isAnnotatedWith(
                                    Autowired.class
                            );


                    /*
                     * If there is exactly one constructor,
                     * Spring can use it without @Autowired.
                     */

                    boolean singleConstructor =
                            bean.getConstructors().size() == 1;


                    if (!explicitlyAutowired
                            && !singleConstructor) {

                        continue;
                    }


                    for (JavaClass dependencyType
                            : parameterTypes) {

                        addDependency(
                                bean,
                                dependencyType
                        );
                    }
                }
            }
        }


        // ============================================================
        // STEP 7
        // @Bean method parameters
        //
        // @Bean
        // public SecurityFilterChain filterChain(
        //        AuthenticationManager manager
        // ) {}
        //
        // WebSecurityConfig -> AuthenticationManager
        // ============================================================

        private void findBeanMethodDependencies() {

            for (JavaClass configurationClass : beanClasses) {

                if (!configurationClass
                        .isAnnotatedWith(Configuration.class)) {

                    continue;
                }


                for (JavaMethod method
                        : configurationClass.getMethods()) {

                    if (!method.isAnnotatedWith(Bean.class)) {
                        continue;
                    }


                    for (JavaClass parameterType
                            : method.getRawParameterTypes()) {

                        addDependency(
                                configurationClass,
                                parameterType
                        );
                    }
                }
            }
        }


        // ============================================================
        // ADD DEPENDENCY
        // ============================================================

        private void addDependency(
                JavaClass source,
                JavaClass dependencyType) {


            /*
             * ========================================================
             * CASE 0:
             *
             * Direct self dependency
             *
             * Example:
             *
             * @Autowired
             * private EmbeddedService embeddedService;
             *
             * inside EmbeddedService itself.
             *
             * Result:
             *
             * EmbeddedService
             *       ↓
             * EmbeddedService
             * ========================================================
             */

            if (source.equals(dependencyType)) {

                graph.get(source).add(source);

                return;
            }


            /*
             * ========================================================
             * CASE 1:
             *
             * Dependency is directly a Spring bean.
             *
             * Example:
             *
             * ServiceA -> ServiceB
             * ========================================================
             */

            if (beanClasses.contains(dependencyType)) {

                graph.get(source).add(dependencyType);

                return;
            }


            /*
             * ========================================================
             * CASE 2:
             *
             * Dependency is an interface.
             *
             * Example:
             *
             * PhoenixIntegrationService
             *          ↓
             * PhoenixIntegrationServiceImpl
             *
             * Also detects:
             *
             * PhoenixIntegrationServiceImpl
             *          ↓
             * PhoenixIntegrationService
             *          ↓
             * PhoenixIntegrationServiceImpl
             *
             * Which becomes:
             *
             * PhoenixIntegrationServiceImpl
             *          ↓
             * PhoenixIntegrationServiceImpl
             * ========================================================
             */

            Set<JavaClass> implementationsForType =
                    implementations.get(dependencyType);


            if (implementationsForType != null) {

                for (JavaClass implementation
                        : implementationsForType) {

                    if (!beanClasses.contains(implementation)) {
                        continue;
                    }


                    /*
                     * The implementation is the same bean
                     * that requested the dependency.
                     */

                    if (implementation.equals(source)) {

                        graph.get(source).add(source);

                        continue;
                    }


                    graph.get(source).add(implementation);
                }
            }


            /*
             * ========================================================
             * CASE 3:
             *
             * Dependency is produced by @Bean.
             *
             * Example:
             *
             * AuthendicationServiceImpl
             *       ↓
             * BCryptPasswordEncoder
             *       ↓
             * WebSecurityConfig
             * ========================================================
             */

            Set<JavaClass> providers =
                    beanProviders.get(dependencyType);


            if (providers != null) {

                for (JavaClass provider : providers) {

                    graph.get(source).add(provider);
                }
            }
        }


        // ============================================================
        // FIND SELF DEPENDENCIES
        // ============================================================

        List<List<String>> findSelfDependencies() {

            List<List<String>> selfDependencies =
                    new ArrayList<>();


            for (JavaClass bean : graph.keySet()) {

                Set<JavaClass> dependencies =
                        graph.getOrDefault(
                                bean,
                                Collections.emptySet()
                        );


                if (dependencies.contains(bean)) {

                    List<String> cycle =
                            new ArrayList<>();

                    cycle.add(
                            bean.getSimpleName()
                    );

                    cycle.add(
                            bean.getSimpleName()
                    );


                    selfDependencies.add(cycle);
                }
            }


            return selfDependencies;
        }


        // ============================================================
        // FIND NORMAL CYCLES
        // ============================================================

        List<List<String>> findCycles() {

            List<List<String>> cycles =
                    new ArrayList<>();


            Set<String> uniqueCycles =
                    new HashSet<>();


            for (JavaClass start : graph.keySet()) {

                List<JavaClass> path =
                        new ArrayList<>();


                Set<JavaClass> visited =
                        new LinkedHashSet<>();


                findCyclesFrom(
                        start,
                        start,
                        path,
                        visited,
                        cycles,
                        uniqueCycles
                );
            }


            return cycles;
        }


        // ============================================================
        // DFS CYCLE DETECTION
        // ============================================================

        private void findCyclesFrom(
                JavaClass start,
                JavaClass current,
                List<JavaClass> path,
                Set<JavaClass> visited,
                List<List<String>> cycles,
                Set<String> uniqueCycles) {


            path.add(current);

            visited.add(current);


            Set<JavaClass> dependencies =
                    graph.getOrDefault(
                            current,
                            Collections.emptySet()
                    );


            for (JavaClass dependency : dependencies) {


                /*
                 * ====================================================
                 * We returned to the starting bean.
                 * ====================================================
                 */

                if (dependency.equals(start)) {


                    /*
                     * Direct self dependency:
                     *
                     * A -> A
                     *
                     * This is handled separately by
                     * findSelfDependencies().
                     */

                    if (path.size() == 1) {
                        continue;
                    }


                    List<String> cycle =
                            new ArrayList<>();


                    for (JavaClass clazz : path) {

                        cycle.add(
                                clazz.getSimpleName()
                        );
                    }


                    cycle.add(
                            start.getSimpleName()
                    );


                    String key =
                            normalizeCycle(cycle);


                    if (uniqueCycles.add(key)) {

                        cycles.add(cycle);
                    }


                    continue;
                }


                /*
                 * ====================================================
                 * Continue only if this node hasn't already
                 * appeared in the current path.
                 * ====================================================
                 */

                if (!visited.contains(dependency)) {

                    findCyclesFrom(
                            start,
                            dependency,
                            path,
                            new LinkedHashSet<>(visited),
                            cycles,
                            uniqueCycles
                    );
                }
            }


            path.remove(
                    path.size() - 1
            );
        }


        // ============================================================
        // NORMALIZE CYCLE
        // ============================================================

        private String normalizeCycle(
                List<String> cycle) {


            if (cycle.size() <= 1) {

                return String.join(
                        "->",
                        cycle
                );
            }


            /*
             * Remove final repeated starting element.
             */

            List<String> nodes =
                    new ArrayList<>(
                            cycle.subList(
                                    0,
                                    cycle.size() - 1
                            )
                    );


            /*
             * Rotate cycle so alphabetically smallest
             * class becomes first.
             */

            int smallestIndex = 0;


            for (int i = 1;
                    i < nodes.size();
                    i++) {

                if (nodes.get(i)
                        .compareTo(
                                nodes.get(
                                        smallestIndex
                                )
                        ) < 0) {

                    smallestIndex = i;
                }
            }


            List<String> normalized =
                    new ArrayList<>();


            for (int i = 0;
                    i < nodes.size();
                    i++) {

                normalized.add(
                        nodes.get(
                                (smallestIndex + i)
                                        % nodes.size()
                        )
                );
            }


            normalized.add(
                    normalized.get(0)
            );


            return String.join(
                    "->",
                    normalized
            );
        }
    }
}