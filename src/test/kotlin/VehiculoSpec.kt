import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class VehiculoSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("dado un auto") {
        val auto = Auto()
        it("debe comenzar sano de entrada") {
            auto.chocado() shouldBe false
        }
        it("cuando avanza, recorre kilómetros") {
            auto.avanzar()
            auto.kilometros shouldBe 40
        }
        it("avanza una segunda vez y recorre la misma cantidad de kilómetros") {
            auto.avanzar()
            auto.kilometros shouldBe 40
        }
        it("no puede chocar con un avion") {
            shouldThrow<RuntimeException> { -> auto.chocar(Avion()) }
        }
        it("si choca con otro auto quedan chocados") {
            val otroAuto : Auto = Auto()
            auto.chocar(otroAuto)
            auto.chocado() shouldBe true
            otroAuto.chocado() shouldBe true
            auto.colisiones shouldBe 1
            otroAuto.colisiones shouldBe 1
        }
    }

    describe("dado un avion") {
        val avion = Avion()
        it("debe comenzar sano de entrada") {
            avion.chocado() shouldBe false
        }
        it("cuando avanza sabe que lo hizo") {
            avion.avances shouldBe 0
            avion.avanzar()
            avion.avances shouldBe 1
        }
        it("no puede chocar con un auto") {
            shouldThrow<RuntimeException> { -> avion.chocar(Auto()) }
        }
    }

})