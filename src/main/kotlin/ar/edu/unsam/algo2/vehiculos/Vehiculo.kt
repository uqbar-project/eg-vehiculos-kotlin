package ar.edu.unsam.algo2.vehiculos

interface Vehiculo {
    fun avanzar()
    fun chocar(vehiculo: Vehiculo) {
        this.doChocar()
        vehiculo.doChocar()
    }
    fun chocado(): Boolean
    fun doChocar()
}

class Auto : Vehiculo {
    var colisiones = 0
    var kilometros = 0

    // velocidad es una referencia que todos los autos comparten
    // se puede acceder como si fuera una variable de instancia pero es global
    // a todas las instancias
    companion object {
        val VELOCIDAD_PROMEDIO = 40
    }

    override fun avanzar() {
        kilometros += VELOCIDAD_PROMEDIO
    }

    override fun chocado(): Boolean {
        return colisiones > 0
    }

    override fun doChocar() {
        colisiones++
    }
}

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