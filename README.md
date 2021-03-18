# Ejercicio de Diseño - Vehículos

[![Build Status](https://travis-ci.com/uqbar-project/eg-vehiculos-kotlin.svg?branch=master)](https://travis-ci.com/uqbar-project/eg-vehiculos-kotlin) [![coverage](https://codecov.io/gh/uqbar-project/eg-vehiculos-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/uqbar-project/eg-vehiculos-kotlin/branch/master/graph/badge.svg) [![BCH compliance](https://bettercodehub.com/edge/badge/uqbar-project/eg-vehiculos-kotlin?branch=master)](https://bettercodehub.com/)

![image](./images/vehicles.png)

## Entorno

- IntelliJ con el [plugin de Kotest](https://plugins.jetbrains.com/plugin/14080-kotest)
- JDK 14

## Dominio

Dados un avión y un automóvil, implementar el siguiente comportamiento:

* avanzar: el avión registra las veces que avanzó, el auto avanza 40 kilómetros cada avance
* chocar: los autos registran las colisiones que tienen, los aviones no pueden chocar nunca

## Objetivo

Es la primera prueba de concepto para conocer la tecnología.

## Conceptos a ver

### Definición de una interfaz en Kotlin

Una [interfaz](https://kotlinlang.org/docs/reference/interfaces.html) es un elemento que permite especificar un contrato, que consiste en un conjunto de
mensajes:

```kt
interface Vehiculo {
    fun avanzar()
    fun chocar(vehiculo: Vehiculo) {
        this.doChocar()
        vehiculo.doChocar()
    }
    fun chocado(): Boolean
    fun doChocar()
}
```

Como vemos la mayoría de los mensajes **no tienen implementación**, solo proveen una **interfaz** donde definen

- el nombre de un método (que se denota con el prefijo `fun`)
- la cantidad de parámetros con sus tipos de dato
- y el tipo de retorno del método: si no se especifica es _Unit_ (tiene efecto colateral, no devuelve nada) y si no tendrá un tipo

Las clases que se suscriban a dicha interfaz están obligadas por el compilador a definir implementaciones (métodos) para
cada uno de los mensajes que no esté definido, o bien podrán redefinir su comportamiento.

```kt
class Avion : Vehiculo {
    var avances = 0

    override fun avanzar() {
        avances++
    }

    override fun chocado(): Boolean {
        return false
    }

    override fun doChocar() {
        throw RuntimeException("Imposible chocar con un avion")
    }
}
```

Aquí vemos que el avión implementa Vehiculo (los dos puntos -`:`- marcan esa relación porque Vehiculo es una interfaz),
tiene una variable numérica que queremos reasignar, por eso será `var` y no `val` y define la implementación para

- chocado(): que devuelve siempre false
- y doChocar(): que tira un error ya que el negocio dice que es imposible que un avión choque

Kotlin utiliza la palabra reservada `fun` para definir tanto métodos como funciones, y `override` hace explícito que
estamos implementando una definición existente (ya sea porque tenemos una superclase o una interfaz).

### Companion object

En la definición de un vehículo, necesitamos generar una constante 40 para determinar la cantidad de kilómetros que
recorre un auto cada vez que avanza. Podríamos haberla escrito en la interfaz, pero como no el avión no tiene nada
que ver con esta definición, vamos a crear la constante en la clase Auto, **mediante un objeto global únicamente para
las instancias de la clase Auto, el [companion object](https://kotlinlang.org/docs/reference/object-declarations.html)**

```kt
class Auto : Vehiculo {
    var colisiones = 0
    var kilometros = 0

    // velocidad es una referencia que todos los autos comparten
    // se puede acceder como si fuera una variable de instancia pero es global
    // a todas las instancias
    companion object {
        val VELOCIDAD_PROMEDIO = 40
    }
```

De esa manera, cuando un auto avance, podemos usar la variable velocidad como si fuera una variable de instancia, solo
que todos los autos van a compartir su valor:

```kt
    override fun avanzar() {
        kilometros += VELOCIDAD_PROMEDIO
    }
```

Otra variante podría haber sido definir una constante no asociada al vehículo, pero justamente es una información propia del auto y suena apropiado dejarlo allí.


### Configuración del proyecto en gradle

El ejemplo está utilizando gradle como manejador de dependencias, en lugar de confiar en los archivos propios de
IntelliJ. Para poder activar las herramientas de testeo unitario, debemos tener esta definición del archivo
`build.gradle.kts` del proyecto:

```gradle
dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "14"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```


### Testeo unitario con kotest

Estaremos utilizando [kotest](https://kotest.io/) que tiene [muchos estilos con el que podés definir los tests](https://kotest.io/docs/framework/testing-styles.html),
en particular a nosotros nos gustó **DescribeSpec** que tiene una sintaxis bastante similar a los describes que viste en Algoritmos 1.

```kt
class VehiculoSpec : DescribeSpec({
   // <-- definimos una clase `VehiculoSpec` que hereda de la clase `DescribeSpec` 
```

Para testear una excepción, utilizamos el matcher shouldThrow que parametriza el tipo de la excepción esperada
y un bloque de código contra el que se ejecutará.

```kt
    isolationMode = IsolationMode.InstancePerTest

    describe("dado un auto") {  // <-- agrupa tests
        // arrange
        //    la referencia Auto se inicializará con cada test
        //    por el isolation mode definido arriba
        val auto = Auto() 
                                 
        it("debe comenzar sano de entrada") {
            // assert
            auto.chocado() shouldBe false
        }
        it("cuando avanza, recorre kilómetros") {
            // act 
            auto.avanzar()
            // assert
            auto.kilometros shouldBe 40
        }
        it("no puede chocar con un avion") {
            // assert que espera una excepción
            shouldThrow<RuntimeException> { -> auto.chocar(Avion()) }
        }
```

### Cobertura de tests

Podés entrar a [esta URL](https://codecov.io/gh/uqbar-project/eg-vehiculos-kotlin/branch/master) donde vas a ver online
el grado de cobertura de este último commit.
